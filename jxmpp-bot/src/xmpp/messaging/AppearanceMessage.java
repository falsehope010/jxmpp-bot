package xmpp.messaging;

import utils.HashUtil;
import xmpp.messaging.base.ChatMessage;
import xmpp.messaging.domain.ParticipantInfo;

/**
 * Represents multi-user chat message that stores boolean marker that indicates
 * that user entered (joined) or leaved multi-user chat. Those messages are
 * created by remote xmpp server when user joins/leaves multi-user chat.
 * <p>
 * Is <b>immutable</b>. Implements Value Object Pattern
 * 
 * @author tillias
 * 
 */
public class AppearanceMessage extends ChatMessage {

    /**
     * Creates new instance of message using given sender (who entered or left
     * chat), status marker and room name
     * 
     * @param sender
     *            Person who caused the creation of this message by server
     * @param isJoined
     *            Marker that indicates that person joined (true) or left
     *            (false) chat room
     * @param roomName
     *            Multi-user chat room
     * @throws NullPointerException
     *             Thrown if any argument passed to constructor is null
     */
    public AppearanceMessage(ParticipantInfo sender, boolean isJoined,
	    String roomName) throws NullPointerException {
	super(sender, null, null, roomName);

	if (sender == null)
	    throw new NullPointerException("Sender can't be null");
	if (roomName == null)
	    throw new NullPointerException("Room name can't be null");

	this.isJoined = isJoined;
    }

    /**
     * Gets marker indicating whether person joined or left multi-user chat
     * room.
     * 
     * @return True if person has joined chat, false if left
     */
    public boolean isJoined() {
	return isJoined;
    }

    @Override
    public String toString() {
	StringBuilder sb = new StringBuilder();
	sb.append("Sender: \n");
	sb.append(getSender().toString());
	sb.append('\n');
	sb.append("Timestamp: ");
	sb.append(getTimestamp());
	sb.append('\n');
	sb.append("Text: ");
	sb.append(getText());
	sb.append('\n');
	sb.append("Room: ");
	sb.append(getRoomName());
	sb.append('\n');
	sb.append("Status: ");
	sb.append(isJoined());
	return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;

	if (!(obj instanceof AppearanceMessage))
	    return false;

	AppearanceMessage am = (AppearanceMessage) obj;

	boolean ok = getSender().equals(am.getSender());
	ok &= am.getRecipient() == null;
	ok &= getTimestamp().equals(am.getTimestamp());
	ok &= am.getText() == null;
	ok &= getRoomName().equals(am.getRoomName());
	ok &= isJoined() == am.isJoined();

	return ok;
    }

    @Override
    public int hashCode() {
	if (fHashCode == 0) {
	    fHashCode = HashUtil.SEED;
	    fHashCode ^= HashUtil.hashInt(fHashCode, getSender().hashCode());
	    fHashCode ^= HashUtil.hashInt(fHashCode, getTimestamp().hashCode());
	    fHashCode ^= HashUtil.hashString(fHashCode, getRoomName());

	    int ival = 0;
	    if (isJoined)
		ival = 1;

	    fHashCode ^= HashUtil.hashInt(fHashCode, ival);
	}

	return fHashCode;
    }

    int fHashCode;
    boolean isJoined;
}
