package xmpp;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Message.Type;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.packet.MUCUser;
import org.jivesoftware.smackx.packet.MUCUser.Item;

public class XmppPacketListener implements PacketListener {

    public XmppPacketListener(XMPPConnection connection, MultiUserChat chat) {

	if (connection == null || chat == null)
	    throw new NullPointerException();

	this.connection = connection;
	this.chat = chat;

	groupChatBindings = new ConcurrentHashMap<String, String>();
    }

    @Override
    public void processPacket(Packet packet) {
	if (packet instanceof Presence) {
	    processePresencePacket(packet);
	}

	if (packet instanceof Message) {
	    processeMessagePacket(packet);
	}
    }

    public String getJid(String nickName) {
	return groupChatBindings.get(nickName);
    }

    private void processePresencePacket(Packet packet) {
	Presence presencePacket = (Presence) packet;

	Object extension = presencePacket
		.getExtension("http://jabber.org/protocol/muc#user");

	if (extension instanceof MUCUser) {
	    MUCUser mucUser = (MUCUser) extension;

	    Item item = mucUser.getItem();

	    if (item != null) {
		try {

		    String sender = presencePacket.getFrom();
		    String fullQualifiedJid = item.getJid();

		    Matcher m = pattern.matcher(fullQualifiedJid);
		    if (m.matches()) {
			groupChatBindings.put(sender, m.group(1));
		    }

		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	}
    }

    private void processeMessagePacket(Packet packet) {
	Message messagePacket = (Message) packet;

	XmppMessageType type = getMessageType(messagePacket);
	System.out.println(type);

	// obsolete code, remove and reuse where needed
	if (false) {
	    try {
		switch (messagePacket.getType()) {
		case groupchat:
		    /*
		     * If we've received group-chat message, then there is
		     * already ready binding between sender and it's jid since
		     * presence message arrives before any group chat message
		     */
		    if (messagePacket.getBody().equals("\\date")) {
			Message msg = chat.createMessage();
			msg.setBody((new Date()).toString());
			msg.setTo(messagePacket.getFrom());
			msg.setType(Type.chat);
			chat.sendMessage(msg);
		    }

		    System.out.println(groupChatBindings.get(messagePacket
			    .getFrom())
			    + ":  " + messagePacket.getBody());

		    break;
		case chat:

		    String sender = groupChatBindings.get(messagePacket
			    .getFrom());

		    if (sender == null) {

			// we've just received private message, not from any
			// chat
			Message msg = new Message();
			msg.setTo(messagePacket.getFrom());
			msg.setType(Type.chat);
			msg.setBody("answer!");
			connection.sendPacket(msg);
		    } else {

			/*
			 * We've recieved PM message from group-chat's user
			 * since getFrom() matched some JID which has been sent
			 * before this packet
			 */
			Chat p_chat = chat.createPrivateChat(messagePacket
				.getFrom(), null);
			Message msg = new Message();
			msg.setTo(messagePacket.getFrom());
			msg.setType(Type.chat);
			msg.setBody("answer!");
			p_chat.sendMessage(msg);
			p_chat = null;
		    }
		    break;
		case error:
		    System.out.println(messagePacket.getBody());
		    break;
		default:
		    System.out.println("Type: " + messagePacket.getType());
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
    }

    private XmppMessageType getMessageType(Message msg) {
	XmppMessageType result = XmppMessageType.Unknown;

	if (msg != null) {
	    Message.Type internalType = msg.getType();
	    String sender = msg.getFrom();

	    /*
	     * Value bellow indicates that there had been sent Presence packet
	     * before and therefore there is binding between current message
	     * sender and it's JID
	     */
	    boolean hasSentGroupChatPresence = groupChatBindings
		    .containsKey(sender);

	    switch (internalType) {
	    case groupchat:
		if (hasSentGroupChatPresence)
		    result = XmppMessageType.GroupChat;
		break;
	    case chat:
		if (hasSentGroupChatPresence)
		    result = XmppMessageType.PrivateChat;
		else
		    result = XmppMessageType.Private;
		break;
	    default:
		// result is already set to unknown
		break;
	    }
	}

	return result;
    }

    XMPPConnection connection;
    MultiUserChat chat;

    /**
     * Stores bindings between packet sender and it's jid. Managed using
     * {@link Presence} packets that are sent by xmpp server
     */
    ConcurrentHashMap<String, String> groupChatBindings;

    Pattern pattern = Pattern.compile("(.*)/(.*)");
}
