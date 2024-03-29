package xmpp.listeners;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.muc.MultiUserChat;

import xmpp.messaging.PublicChatMessage;
import xmpp.messaging.domain.ParticipantInfo;
import xmpp.processing.IProcessor;
import xmpp.utils.presence.PresenceCache;

/**
 * Listens for multi-user chat public text packets. If received those packets
 * wraps them into {@link PublicChatMessage} instances and redirects to
 * underlying {@link IProcessor} for further processing
 * 
 * @author tillias
 * 
 */
public class GroupChatMessageListener extends AbstractChatListener {

    /**
     * Creates new chat message listener using given message processor
     * 
     * @param chat
     *            Chat which this listener listens
     * @param messageProcessor
     *            {@link IProcessor} implementation which will be used to
     *            process packets received by listener
     * @throws NullPointerException
     *             Thrown if {@link IProcessor} any argument passed to
     *             constructor is null
     */
    public GroupChatMessageListener(PresenceCache cache, MultiUserChat chat,
	    IProcessor messageProcessor) throws NullPointerException {

	super(cache, chat);

	if (messageProcessor == null)
	    throw new NullPointerException("Message processor can't be null");

	this.messageProcessor = messageProcessor;
    }

    @Override
    public void processPacket(Packet packet) {
	try {
	    if (packet != null) {
		if (packet instanceof Message) {
		    Message msg = (Message) packet;

		    if (msg.getType() == Message.Type.groupchat) {
			ParticipantInfo sender = createParticipantInfo(msg
				.getFrom());
			if (sender != null) {
			    PublicChatMessage chatMessage = new PublicChatMessage(
				    sender, msg.getBody(), getChat().getRoom());
			    messageProcessor.processMessage(chatMessage);
			}
		    }
		}

	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private ParticipantInfo createParticipantInfo(String occupantName) {
	ParticipantInfo info = null;

	try {
	    if (occupantName != null) {
		String jabberID = getCache().get(occupantName);

		if (jabberID == null) {
		    jabberID = presenceProcessor.requestJabberID(getChat(),
			    occupantName);
		}

		/*
		 * jabberID here can be null because network errors so
		 * additional validation needed
		 */
		if (jabberID != null) {
		    info = new ParticipantInfo(jabberID, occupantName);
		}

	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}

	return info;
    }

    IProcessor messageProcessor;
}
