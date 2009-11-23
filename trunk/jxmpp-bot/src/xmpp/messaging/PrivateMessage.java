package xmpp.messaging;

import xmpp.messaging.base.Message;
import xmpp.messaging.domain.ParticipantInfo;

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
     * block. All arguments mustn't be null otherwise
     * {@link NullPointerException} will be thrown
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

	if (sender == null || recipient == null)
	    throw new NullPointerException("Sender or recipient can't be null");

	if (text == null)
	    throw new NullPointerException("Private message text can't be null");
    }

    @Override
    public String toString() {
	StringBuilder sb = new StringBuilder();
	sb.append("Sender: \n");
	sb.append(getSender().toString());
	sb.append('\n');
	sb.append("Recipient: \n");
	sb.append(getRecipient());
	sb.append('\n');
	sb.append("Timestamp: ");
	sb.append(getTimestamp());
	sb.append('\n');
	sb.append("Text: ");
	sb.append(getText());
	return sb.toString();
    }
}
