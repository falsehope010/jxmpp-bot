package xmpp.messaging.base;

import java.util.Date;

import xmpp.messaging.domain.ParticipantInfo;

/**
 * Abstract base class for all XMPP messages. Subclasses must provide
 * <b>defensive copying</b> for all getters for their internal data fields
 * 
 * 
 * @author tillias
 * 
 */
public abstract class Message {

    /**
     * Sole constructor for invocation by subclasses. Sets message date to
     * current one. No verification of arguments passed to constructor is done.
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
    public final ParticipantInfo getSender() {
	ParticipantInfo result = null;
	if (sender != null)
	    result = new ParticipantInfo(sender);
	return result;
    }

    /**
     * Gets recipient of this message
     * 
     * @return Recipient of this message
     * @see ParticipantInfo
     */
    public final ParticipantInfo getRecipient() {
	ParticipantInfo result = null;
	if (recipient != null)
	    result = new ParticipantInfo(recipient);
	return result;
    }

    /**
     * Gets timestamp of this message (e.g. timestamp when this message was
     * created)
     * 
     * @return Timestamp of this message
     */
    public final Date getTimestamp() {
	return new Date(timestamp.getTime());
    }

    /**
     * Gets text block of this message. Text block can be null
     * 
     * @return Text block of this message
     */
    public final String getText() {
	return text;
    }

    ParticipantInfo sender;
    ParticipantInfo recipient;
    Date timestamp;
    String text;
}
