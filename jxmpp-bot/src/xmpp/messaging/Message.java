package xmpp.messaging;

import java.util.Date;

/**
 * Base class for all XMPP messages. Subclasses must be <b>immutable</b>.
 * 
 * @author tillias
 * 
 */
public abstract class Message {

    /**
     * Sole constructor for invocation by subclasses. Sets message date to
     * current one
     * 
     * @param sender
     *            Message sender
     * @param recipient
     *            Message recipient
     * @param text
     *            Message text
     */
    public Message(ParticipantInfo sender, ParticipantInfo recipient,
	    String text) {
	if (sender == null || recipient == null)
	    throw new NullPointerException("Sender or recipient can't be null");

	this.sender = sender;
	this.recipient = recipient;
	this.text = text;
	this.timestamp = new Date();
    }

    /**
     * Gets sender of this message
     * 
     * @return Sender of this message
     * @see ParticipantInfo
     */
    public ParticipantInfo getSender() {
	return new ParticipantInfo(sender);
    }

    /**
     * Gets recipient of this message
     * 
     * @return Recipient of this message
     * @see ParticipantInfo
     */
    public ParticipantInfo getRecipient() {
	return new ParticipantInfo(recipient);
    }

    /**
     * Gets timestamp of this message (e.g. timestamp when this message was
     * created)
     * 
     * @return Timestamp of this message
     */
    public Date getTimestamp() {
	return new Date(timestamp.getTime());
    }

    /**
     * Gets text block of this message. Text block can be null
     * 
     * @return Text block of this message
     */
    public String getText() {
	return text;
    }

    ParticipantInfo sender;
    ParticipantInfo recipient;
    Date timestamp;
    String text;
}
