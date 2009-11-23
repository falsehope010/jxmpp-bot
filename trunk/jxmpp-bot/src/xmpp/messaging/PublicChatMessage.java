package xmpp.messaging;

import xmpp.messaging.base.ChatMessage;
import xmpp.messaging.domain.ParticipantInfo;

/**
 * Represents multi-user public chat message which is addressed to all
 * participants of the chat. Recipient field is always null pointer
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
	if (text == null || roomName == null)
	    throw new NullPointerException(
		    "Message text or room name can't be null");
    }
}
