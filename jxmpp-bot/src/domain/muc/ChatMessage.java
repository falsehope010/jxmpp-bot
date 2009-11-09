package domain.muc;

import java.util.Date;

import domain.DomainObject;

/**
 * Represents multi-chat message.
 * <p>
 * Stores message text and timestamp as well as sender's and recipient
 * information. Sender's and recipient's information is stored using
 * {@link Visit} objects.
 * 
 * @see Visit
 * @author tillias
 * 
 */
public class ChatMessage extends DomainObject {

    /**
     * Creates new instance of chat message.
     * 
     * @param text
     *            Message text
     * @param timestamp
     *            Message timestamp.
     * @param senderVisit
     *            Visit object associated with message sender
     * @param recipientVisit
     *            Visit object associated with recipient
     * @throws NullPointerException
     *             Thrown if senderVisit or recipientVisit passed to constructor
     *             is null reference
     * @throws IllegalArgumentException
     *             Thrown if senderVisit or recipientVisit passed to constructor
     *             is not valid persistent domain object
     * @see Visit
     */
    public ChatMessage(String text, Date timestamp, Visit senderVisit)
	    throws NullPointerException, IllegalArgumentException {
	if (senderVisit == null)
	    throw new NullPointerException(
		    "senderVisit or recipientVisit can't be null");

	if (!senderVisit.isPersistent())
	    throw new IllegalArgumentException("senderVisit must be persistent"
		    + "domain objects");

	this.text = text;
	this.timestamp = timestamp;
	this.sender = senderVisit;
    }

    /**
     * Gets text associated with chat message
     * 
     * @return Text of chat message
     */
    public String getText() {
	return text;
    }

    /**
     * Gets timestamp of chat message
     * 
     * @return Timestamp of chat message
     */
    public Date getTimestamp() {
	return timestamp;
    }

    /**
     * Gets sender visit
     * 
     * @return Sender visit
     * @see Visit
     */
    public Visit getSender() {
	return sender;
    }

    /**
     * Sets message text
     * 
     * @param text
     *            New message text. Parameter can't be null
     * @throws NullPointerException
     *             Thrown if parameter passed to method is null reference
     */
    public void setText(String text) throws NullPointerException {
	if (text == null)
	    throw new NullPointerException("Message text can't be null");
	this.text = text;
    }

    String text;
    Date timestamp;
    Visit sender;
}
