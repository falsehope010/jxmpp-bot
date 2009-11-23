package xmpp.listeners;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.muc.MultiUserChat;

import xmpp.messaging.PublicChatMessage;
import xmpp.messaging.domain.ParticipantInfo;
import xmpp.processing.IProcessor;
import xmpp.utils.presence.PresenceCache;
import xmpp.utils.presence.PresenceProcessor;

public class ChatMessageListener implements PacketListener {

    /**
     * Creates new chat message listener using given message processor
     * 
     * @param chat
     *            Chat to which this listener belongs
     * @param messageProcessor
     *            {@link IProcessor} implementation which will be used to
     *            process packets received by listener
     * @throws NullPointerException
     *             Thrown if {@link IProcessor} any argument passed to
     *             constructor is null
     */
    public ChatMessageListener(MultiUserChat chat, IProcessor messageProcessor)
	    throws NullPointerException {
	if (chat == null)
	    throw new NullPointerException("Chat room can't be null");
	if (messageProcessor == null)
	    throw new NullPointerException("Message processor can't be null");

	this.chat = chat;
	this.messageProcessor = messageProcessor;
	this.presenceCache = new PresenceCache();

	presenceProcessor = new PresenceProcessor();
    }

    @Override
    public void processPacket(Packet packet) {

	if (packet != null) {
	    if (packet instanceof Message) {
		Message msg = (Message) packet;

		if (msg.getType() == Message.Type.groupchat) {
		    ParticipantInfo sender = createParticipantInfo(msg
			    .getFrom());
		    PublicChatMessage chatMessage = new PublicChatMessage(
			    sender, msg.getBody(), chat.getRoom());
		    messageProcessor.processMessage(chatMessage);
		}
	    }
	}
    }

    private ParticipantInfo createParticipantInfo(String occupantName) {
	ParticipantInfo info = null;

	try {
	    if (occupantName != null) {
		String jabberID = presenceCache.get(occupantName);

		if (jabberID == null) {
		    jabberID = presenceProcessor.requestJabberID(chat,
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

    PresenceProcessor presenceProcessor;
    PresenceCache presenceCache;
    IProcessor messageProcessor;
    MultiUserChat chat;
}
