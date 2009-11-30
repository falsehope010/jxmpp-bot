package xmpp.listeners;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import xmpp.core.Connection;
import xmpp.core.IConnection;
import xmpp.core.IRoom;
import xmpp.messaging.PrivateChatMessage;
import xmpp.messaging.PrivateMessage;
import xmpp.messaging.domain.ParticipantInfo;
import xmpp.processing.IProcessor;

/**
 * Converts <code>SMACK</code> {@link Message} packets into
 * {@link PrivateMessage} instances. Used by {@link Connection} in order to
 * listen for incoming text message packets. This listener listens for both
 * private messages and any private group chat messages as well
 * <p>
 * After <code>SMACK</code> packet has been converted into
 * {@link PrivateMessage} it is sent to {@link IProcessor} for further
 * processing.
 * 
 * @author tillias
 * @see Connection
 * @see PrivateMessage
 * @see PrivateChatMessage
 * 
 */
public class PrivateMessageListener implements PacketListener {

    /**
     * Creates new instance of listener using given {@link IProcessor}
     * 
     * @param parent
     *            Parent connection for this listener
     * @param messageProcessor
     *            {@link IProcessor} implementation which will be used to
     *            process packets received by listener
     * @throws NullPointerException
     *             Thrown if any argument passed to constructor is null
     */
    public PrivateMessageListener(IConnection parent,
	    IProcessor messageProcessor) throws NullPointerException {
	if (messageProcessor == null)
	    throw new NullPointerException(
		    "Message processor passed to listener can't be null");
	if (parent == null)
	    throw new NullPointerException("Parent connection can't be null");

	this.messageProcessor = messageProcessor;
	this.parent = parent;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation checks whether packet is {@link Message} and it's
     * type is <code>chat</code>. E.g. packet is normal private chat message
     * between two xmpp participants. Then it converts packet to
     * {@link xmpp.messaging.base.Message} instance and sends to underlying
     * {@link IProcessor} for futher processing
     */
    @Override
    public void processPacket(Packet packet) {
	if (packet != null && packet instanceof Message) {

	    try {
		Message message = (Message) packet;

		if (message.getType() == Message.Type.chat) {

		    IRoom chatRoom = getRoom(message);

		    if (chatRoom != null)
			processPrivateChatMessage(message, chatRoom);
		    else
			processPrivateMessage(message);
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
    }

    /**
     * Checks whether message was sent from group chat in private mode. To do so
     * attempts to get {@link IRoom} from parent {@link IConnection} using
     * room's name
     * 
     * @param message
     *            Message to be checked
     * @return Valid {@link IRoom} if message was sent from this room in private
     *         mode, null pointer otherwise
     */
    private IRoom getRoom(Message message) {
	String senderAdress = extractSenderWithoutResource(message.getFrom());

	IRoom chatRoom = parent.getRoom(senderAdress);
	return chatRoom;
    }

    private void processPrivateChatMessage(Message message, IRoom chatRoom) {
	try {
	    String senderAdress = message.getFrom();
	    String jabberID = chatRoom.getJID(message.getFrom());

	    if (jabberID != null) {
		ParticipantInfo sender = new ParticipantInfo(jabberID,
			senderAdress);

		String recipientAddress = message.getTo();
		String recipientJID = extractSenderWithoutResource(recipientAddress);

		if (recipientAddress != null && recipientJID != null) {
		    ParticipantInfo recipient = new ParticipantInfo(
			    recipientJID, recipientAddress);
		    PrivateChatMessage privateChatMessage = new PrivateChatMessage(
			    sender, recipient, message.getBody(), chatRoom
				    .getName());
		    messageProcessor.processMessage(privateChatMessage);
		}

	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    /**
     * Processes private message (e.g. direct xmpp message from one person to
     * another without the use of multi user chat)
     * 
     * @param message
     *            Message to be processed
     * @throws NullPointerException
     *             Thrown if sender or recipient of message can't be created
     */
    private void processPrivateMessage(Message message)
	    throws NullPointerException {
	try {
	    ParticipantInfo sender = createParticipantInfo(message.getFrom());
	    ParticipantInfo recipient = createParticipantInfo(message.getTo());

	    PrivateMessage privateMessage = new PrivateMessage(sender,
		    recipient, message.getBody());
	    messageProcessor.processMessage(privateMessage);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    /**
     * Creates {@link ParticipantInfo} instance using given fully qualified
     * participant address (in form nick@server.domain/resource)
     * 
     * @param senderAdress
     *            fully qualified participant address
     * @return {@link ParticipantInfo} if succeeded, null pointer otherwise
     * @throws NullPointerException
     *             Thrown if senderAdress is null or invalid (e.g. there is no
     *             way to create {@link ParticipantInfo})
     */
    private ParticipantInfo createParticipantInfo(String senderAdress)
	    throws NullPointerException {
	ParticipantInfo result = null;

	if (senderAdress != null) {
	    Matcher m = pattern.matcher(senderAdress);
	    if (m.matches()) {
		String jabberID = m.group(1);
		if (jabberID != null)
		    result = new ParticipantInfo(jabberID, senderAdress);
	    }
	}

	return result;
    }

    private String extractSenderWithoutResource(String sender) {
	String result = null;
	if (sender != null) {
	    Matcher m = pattern.matcher(sender);
	    if (m.matches()) {
		result = m.group(1);
	    }
	}
	return result;
    }

    IConnection parent;
    IProcessor messageProcessor;

    final Pattern pattern = Pattern.compile("(.*)/(.*)");
}
