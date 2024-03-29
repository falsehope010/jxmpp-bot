package xmpp.helpers;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.packet.MUCUser;
import org.jivesoftware.smackx.packet.MUCUser.Item;

public class PacketGenerator {
    public Message createPrivateMessage() {

	return createPrivateMessage("sender@server.domain", "sender_resource",
		"recipient@server.domain", "recipient_resource");
    }

    public Message createPrivateMessage(Message.Type msgType) {
	Message result = createPrivateMessage();
	result.setType(msgType);

	return result;
    }

    public Message createPrivateMessage(String senderJID,
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

    public Message createPrivateChatMessage(String occupantName) {
	Message result = new Message();
	result.setType(Message.Type.chat);
	result.setFrom(occupantName);
	result.setTo("testroom@xmpp.org/recipient");
	result.setBody("Test text");
	return result;
    }

    public Presence createPresence() {
	return createPresence("participant@server.domain", "participant",
		Presence.Type.available);
    }

    public Presence createPresence(String jabberID, String nick,
	    Presence.Type type) {
	Presence result = new Presence(type);
	result.setFrom(jabberID + '/' + "resource");

	Item item = new Item(null, null);
	item.setJid(jabberID + '/' + "resource");
	item.setNick(nick);

	MUCUser mucUser = new MUCUser();
	mucUser.setItem(item);

	result.addExtension(mucUser);

	return result;
    }

}
