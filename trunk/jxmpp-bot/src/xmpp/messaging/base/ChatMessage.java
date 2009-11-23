package xmpp.messaging.base;

import xmpp.messaging.domain.ParticipantInfo;

/**
 * Abstract base class for multichat messaging. Subclasses must provide
 * <b>defensive copying</b> for all getters for their internal data fields
 * 
 * @author tillias
 * 
 */
public abstract class ChatMessage extends Message {

    /**
     * Sole constructor for invocation by subclasses. No verification of
     * arguments passed to constructor is done.
     * 
     * @param sender
     *            Sender of message
     * @param recipient
     *            Recipient of message
     * @param text
     *            Text block of this message
     * @param roomName
     *            Room name with which this message is associated
     */
    public ChatMessage(ParticipantInfo sender, ParticipantInfo recipient,
	    String text, String roomName) {
	super(sender, recipient, text);

	this.roomName = roomName;
    }

    /**
     * Gets room name with which this message is associated
     * 
     * @return Room name
     */
    public String getRoomName() {
	return roomName;
    }

    String roomName;
}
