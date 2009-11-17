package xmpp.listeners;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.muc.ParticipantStatusListener;
import org.jivesoftware.smackx.packet.MUCUser;
import org.jivesoftware.smackx.packet.MUCUser.Item;

import xmpp.messaging.XmppNickMessage;
import xmpp.messaging.XmppStatusMessage;
import xmpp.messaging.XmppTextMessage;
import xmpp.messaging.data.XmppNickMessageData;
import xmpp.messaging.data.XmppStatusMessageData;
import xmpp.messaging.data.XmppStatusMessageType;
import xmpp.messaging.data.XmppTextMessageData;
import xmpp.messaging.data.XmppTextMessageType;
import xmpp.processing.IXmppProcessor;

public class XmppPacketListener extends AbstractXmppListener implements
	PacketListener, ParticipantStatusListener {

    public XmppPacketListener(IXmppProcessor processor) {
	super(processor);
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

    @Override
    public void adminGranted(String participant) {
	processMessage(createStatusMessage(participant,
		XmppStatusMessageType.AdminGranted));
    }

    @Override
    public void adminRevoked(String participant) {
	processMessage(createStatusMessage(participant,
		XmppStatusMessageType.AdminRevoked));
    }

    @Override
    public void banned(String participant, String actor, String reason) {
	processMessage(createStatusMessage(participant,
		XmppStatusMessageType.Banned));
    }

    @Override
    public void joined(String participant) {
	processMessage(createStatusMessage(participant,
		XmppStatusMessageType.JoinedGroupChat));
    }

    @Override
    public void kicked(String participant, String actor, String reason) {
	processMessage(createStatusMessage(participant,
		XmppStatusMessageType.Kicked));
    }

    @Override
    public void left(String participant) {
	processMessage(createStatusMessage(participant,
		XmppStatusMessageType.LeftGroupChat));
    }

    @Override
    public void membershipGranted(String participant) {
	processMessage(createStatusMessage(participant,
		XmppStatusMessageType.MembershipGranted));
    }

    @Override
    public void membershipRevoked(String participant) {
	processMessage(createStatusMessage(participant,
		XmppStatusMessageType.MembershipRevoked));
    }

    @Override
    public void moderatorGranted(String participant) {
	processMessage(createStatusMessage(participant,
		XmppStatusMessageType.ModeratorGranted));
    }

    @Override
    public void moderatorRevoked(String participant) {
	processMessage(createStatusMessage(participant,
		XmppStatusMessageType.ModeratorRevoked));
    }

    @Override
    public void nicknameChanged(String participant, String newNickname) {
	processMessage(createNickMessage(participant, newNickname));
    }

    @Override
    public void ownershipGranted(String participant) {
	processMessage(createStatusMessage(participant,
		XmppStatusMessageType.OwnershipGranted));
    }

    @Override
    public void ownershipRevoked(String participant) {
	processMessage(createStatusMessage(participant,
		XmppStatusMessageType.OwnershipRevoked));
    }

    @Override
    public void voiceGranted(String participant) {
	processMessage(createStatusMessage(participant,
		XmppStatusMessageType.VoiceGranted));
    }

    @Override
    public void voiceRevoked(String participant) {
	processMessage(createStatusMessage(participant,
		XmppStatusMessageType.VoiceRevoked));
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
		    if (sender != null && fullQualifiedJid != null) {
			Matcher m = pattern.matcher(fullQualifiedJid);
			if (m.matches()) {
			    groupChatBindings.put(sender, m.group(1));
			}
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	}
    }

    private void processeMessagePacket(Packet packet) {
	Message messagePacket = (Message) packet;

	XmppTextMessageType messageType = getMessageType(messagePacket);
	String jid = getSenderJid(messagePacket, messageType);

	if (messageType != XmppTextMessageType.Unknown && jid != null) {
	    XmppTextMessageData data = new XmppTextMessageData();
	    data.setJid(jid);
	    data.setSender(messagePacket.getFrom());
	    data.setText(messagePacket.getBody());
	    data.setTimestamp(new Date());
	    data.setType(messageType);

	    XmppTextMessage msg = new XmppTextMessage(data);
	    processMessage(msg);
	}
    }

    private XmppTextMessageType getMessageType(Message msg) {
	XmppTextMessageType result = XmppTextMessageType.Unknown;

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
		    result = XmppTextMessageType.GroupChat;
		break;
	    case chat:
		if (hasSentGroupChatPresence)
		    result = XmppTextMessageType.PrivateChat;
		else
		    result = XmppTextMessageType.Private;
		break;
	    default:
		// result is already set to unknown
		break;
	    }
	}

	return result;
    }

    private String getSenderJid(Message msg, XmppTextMessageType type) {
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

    private XmppStatusMessage createStatusMessage(String sender,
	    XmppStatusMessageType type) {
	XmppStatusMessageData data = new XmppStatusMessageData();
	data.setSender(sender);
	data.setJid(groupChatBindings.get(sender));
	data.setTimestamp(new Date());
	data.setType(type);
	return new XmppStatusMessage(data);
    }

    private XmppNickMessage createNickMessage(String sender, String newNick) {
	XmppNickMessageData data = new XmppNickMessageData();
	data.setSender(sender);
	data.setNewNick(newNick);
	data.setJid(groupChatBindings.get(sender));
	data.setTimestamp(new Date());
	data.setType(XmppStatusMessageType.NicknameChanged);
	return new XmppNickMessage(data);
    }

    /**
     * Stores bindings between packet sender and it's jid. Managed using
     * {@link Presence} packets that are sent by xmpp server
     */
    ConcurrentHashMap<String, String> groupChatBindings;

    Pattern pattern = Pattern.compile("(.*)/(.*)");

}
