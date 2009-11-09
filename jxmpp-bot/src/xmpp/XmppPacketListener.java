package xmpp;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.packet.MUCUser;
import org.jivesoftware.smackx.packet.MUCUser.Item;

import xmpp.message.XmppMessage;
import xmpp.message.XmppMessageData;
import xmpp.message.XmppMessageType;

public class XmppPacketListener implements PacketListener {

    public XmppPacketListener(IXmppManager manager) {

	if (manager == null)
	    throw new NullPointerException();

	this.xmppManager = manager;
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

	XmppMessageType messageType = getMessageType(messagePacket);
	String jid = getSenderJid(messagePacket, messageType);

	if (messageType != XmppMessageType.Unknown && jid != null) {
	    XmppMessageData data = new XmppMessageData();
	    data.setJid(jid);
	    data.setSender(messagePacket.getFrom());
	    data.setText(messagePacket.getBody());
	    data.setTimestamp(new Date());
	    data.setType(messageType);

	    XmppMessage msg = new XmppMessage(data);
	    xmppManager.processMessage(msg);
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

    private String getSenderJid(Message msg, XmppMessageType type) {
	String result = null;

	if (msg != null) {
	    switch (type) {
	    case GroupChat:
	    case PrivateChat:
		result = groupChatBindings.get(msg.getFrom());
		break;
	    case Private:
		Matcher m = pattern.matcher(msg.getFrom());
		if (m.matches()) {
		    result = m.group(1);
		}
		break;
	    default:
		break;
	    }
	}

	return result;
    }

    IXmppManager xmppManager;
    /**
     * Stores bindings between packet sender and it's jid. Managed using
     * {@link Presence} packets that are sent by xmpp server
     */
    ConcurrentHashMap<String, String> groupChatBindings;

    Pattern pattern = Pattern.compile("(.*)/(.*)");
}
