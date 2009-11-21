package xmpp.listeners;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import xmpp.core.Connection;
import xmpp.messaging.ParticipantInfo;
import xmpp.messaging.PrivateMessage;
import xmpp.processing.IProcessor;

/**
 * Converts <code>SMACK</code> {@link Message} packets into
 * {@link PrivateMessage} instances. Used by {@link Connection} in order to
 * listen for incoming text message packets
 * <p>
 * After <code>SMACK</code> packet has been converted into
 * {@link PrivateMessage} it is sent to {@link IProcessor} for further
 * processing.
 * 
 * @author tillias
 * @see Connection
 * 
 */
public class PrivateMessageListener implements PacketListener {

    /**
     * Creates new instance of listener using given {@link IProcessor}
     * 
     * @param messageProcessor
     *            {@link IProcessor} implementation which will be used to
     *            process packets received by listener
     * @throws NullPointerException
     *             Thrown if argument passed to constructor is null
     */
    public PrivateMessageListener(IProcessor messageProcessor)
	    throws NullPointerException {
	if (messageProcessor == null)
	    throw new NullPointerException(
		    "Message processor passed to listener can't be null");

	this.messageProcessor = messageProcessor;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation checks whether packet is {@link Message} and it's
     * type is <code>chat</code>. E.g. packet is normal private chat message
     * between two xmpp participants. Then it converts packet to
     * {@link xmpp.messaging.Message} instance and sends to underlying
     * {@link IProcessor} for futher processing
     */
    @Override
    public void processPacket(Packet packet) {
	if (packet != null && packet instanceof Message) {

	    try {
		Message message = (Message) packet;

		if (message.getType() == Message.Type.chat) {

		    ParticipantInfo sender = createParticipantInfo(message
			    .getFrom());
		    ParticipantInfo recipient = createParticipantInfo(message
			    .getTo());
		    PrivateMessage privateMessage = new PrivateMessage(sender,
			    recipient, message.getBody());
		    messageProcessor.processMessage(privateMessage);
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
    }

    /**
     * Creates {@link ParticipantInfo} instance using given fully qualified
     * participant address (in form nick@server.domain/resource)
     * 
     * @param senderAdress
     *            fully qualified participant address
     * @return {@link ParticipantInfo} if succeeded, null pointer otherwise
     */
    private ParticipantInfo createParticipantInfo(String senderAdress) {
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

    IProcessor messageProcessor;

    final Pattern pattern = Pattern.compile("(.*)/(.*)");
}
