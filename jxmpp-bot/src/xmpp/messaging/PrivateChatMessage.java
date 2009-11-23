package xmpp.messaging;

import xmpp.messaging.base.ChatMessage;
import xmpp.messaging.domain.ParticipantInfo;

/**
 * Represents multi-user chat message which is addressed to some person from
 * chat in private
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

	if (roomName == null)
	    throw new NullPointerException("Chat room can't be null");
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
	sb.append("Text: ");
	sb.append(getText());
	sb.append('\n');
	sb.append("Room: ");
	sb.append(getRoomName());
	return sb.toString();
    }

}
