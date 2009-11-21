package xmpp.helpers;

import org.jivesoftware.smack.packet.Message;

public class PacketGenerator {
    public Message createPrivateChatMessage() {

	return createPrivateChatMessage("sender@server.domain",
		"sender_resource", "recipient@server.domain",
		"recipient_resource");
    }

    public Message createPrivateChatMessage(Message.Type msgType) {
	Message result = createPrivateChatMessage();
	result.setType(msgType);

	return result;
    }

    public Message createPrivateChatMessage(String senderJID,
	    String senderResource, String recipientJID, String recipientResource) {
	Message result = new Message();

	if (recipientJID != null && recipientResource != null)
	    result.setTo(recipientJID + '/' + recipientResource);
	else {
	    if (recipientJID == null)
		result.setTo(recipientResource);
	    if (recipientResource == null)
		result.setTo(recipientJID);
	}

	if (senderJID != null && senderResource != null)
	    result.setFrom(senderJID + '/' + senderResource);
	else {
	    if (senderJID == null)
		result.setFrom(recipientResource);
	    if (senderResource == null)
		result.setFrom(recipientJID);
	}

	result.setBody("Test private chat message");
	result.setType(Message.Type.chat);
	return result;
    }
}
