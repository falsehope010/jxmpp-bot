package xmpp.messaging;

/**
 * Represents direct message that occurs between two jabber users while private
 * chatting
 * 
 * @author tillias
 * 
 */
public class PrivateMessage extends Message {

    /**
     * Creates new instance of message using given sender, recipient and text
     * block. All arguments mustn't be null
     * 
     * @param sender
     *            Message sender
     * @param recipient
     *            Message recipient
     * @param text
     *            Text block of message
     * @throws NullPointerException
     *             Thrown if any argument passed to constructor is null
     */
    public PrivateMessage(ParticipantInfo sender, ParticipantInfo recipient,
	    String text) throws NullPointerException {
	super(sender, recipient, text);

	if (text == null)
	    throw new NullPointerException("Private message text can't be null");
    }

}