package xmpp.messaging;

import utils.HashUtil;
import xmpp.messaging.base.ChatMessage;
import xmpp.messaging.domain.ParticipantInfo;

/**
 * Represents multi-user chat message which is addressed to some person from
 * chat in private.
 * <p>
 * Is <b>immutable</b>. Implements Value Object Pattern
 * <p>
 * Example of creation:
 * 
 * <pre>
 * ParticipantInfo sender = new ParticipantInfo(&quot;sender@xmpp.org&quot;,
 * 	&quot;room@conference.xmpp.org/sender_nick&quot;);
 * ParticipantInfo recipient = new ParticipantInfo(&quot;recipient@xmpp.org&quot;,
 * 	&quot;room@conference.xmpp.org/recipient_nick&quot;);
 * new PrivateChatMessage(sender, recipient, &quot;Hello! Test&quot;,
 * 	&quot;room@conference.xmpp.org&quot;);
 * </pre>
 * 
 * This example creates private chat message in room with name
 * <i>room@conference.xmpp.org</i>. Message is addressed to
 * <i>room@conference.xmpp.org/sender_nick</i>
 * 
 * @author tillias
 * 
 */
public class PrivateChatMessage extends ChatMessage {

    /**
     * Creates new instance of message using give sender, recipient, text block
     * and chat room name. All arguments mustn't be null otherwise
     * {@link NullPointerException} will be thrown
     * 
     * @param sender
     *            Message sender
     * @param recipient
     *            Message recipient
     * @param text
     *            Text block of message
     * @param roomName
     *            Chat room name
     */
    public PrivateChatMessage(ParticipantInfo sender,
	    ParticipantInfo recipient, String text, String roomName) {
	super(sender, recipient, text, roomName);

	if (sender == null || recipient == null)
	    throw new NullPointerException("Sender or recipient can't be null");

	if (text == null)
	    throw new NullPointerException("Message text block can't be null");
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
	sb.append('\n');
	sb.append("Room: ");
	sb.append(getRoomName());
	return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;

	if (!(obj instanceof PrivateChatMessage))
	    return false;

	PrivateChatMessage pm = (PrivateChatMessage) obj;

	boolean ok = getSender().equals(pm.getSender());
	ok &= getRecipient().equals(pm.getRecipient());
	ok &= getTimestamp().equals(pm.getTimestamp());
	ok &= getText().equals(pm.getText());
	ok &= getRoomName().equals(pm.getRoomName());

	return ok;
    }

    @Override
    public int hashCode() {
	if (fHashCode == 0) {
	    fHashCode = HashUtil.SEED;
	    fHashCode ^= HashUtil.hashInt(fHashCode, getSender().hashCode());
	    fHashCode ^= HashUtil.hashInt(fHashCode, getRecipient().hashCode());
	    fHashCode ^= HashUtil.hashInt(fHashCode, getTimestamp().hashCode());
	    fHashCode ^= HashUtil.hashString(fHashCode, getText());
	    fHashCode ^= HashUtil.hashString(fHashCode, getRoomName());
	}
	return fHashCode;
    }

    int fHashCode;
}
