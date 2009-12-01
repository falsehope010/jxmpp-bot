package xmpp.messaging;

import utils.HashUtil;
import xmpp.messaging.base.ChatMessage;
import xmpp.messaging.domain.ParticipantInfo;

/**
 * Represents multi-user public chat message which is addressed to all
 * participants of the chat. Recipient field is always null pointer
 * <p>
 * Is <b>immutable</b>. Implements Value Object pattern
 * 
 * @author tillias
 * 
 */
public class PublicChatMessage extends ChatMessage {

    /**
     * Creates new public chat message using given sender, text block and chat
     * room name. All arguments mustn't be null otherwise
     * {@link NullPointerException} will be thrown
     * 
     * @param sender
     *            Message sender
     * @param text
     *            Text block of message
     * @param roomName
     *            Room name to which message belongs
     * @throws NullPointerException
     *             Thrown if any argument passed to constructor is null
     */
    public PublicChatMessage(ParticipantInfo sender, String text,
	    String roomName) throws NullPointerException {
	super(sender, null, text, roomName);

	if (sender == null)
	    throw new NullPointerException("Sender can't be null");
	if (text == null)
	    throw new NullPointerException(
		    "Message text or room name can't be null");
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

	if (!(obj instanceof PublicChatMessage))
	    return false;

	PublicChatMessage pm = (PublicChatMessage) obj;

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
