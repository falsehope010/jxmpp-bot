package xmpp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.packet.MUCUser;
import org.jivesoftware.smackx.packet.MUCUser.Item;

import xmpp.configuration.RoomCredentials;

public class Room implements IRoom {

    public Room(RoomCredentials credentials, XMPPConnection parent) {
	if (credentials == null || parent == null)
	    throw new NullPointerException(
		    "Argument passed to Room constructor can't be null");

	this.credentials = credentials;
	this.parent = parent;

	presenceCache = new PresenceCache();
	chatRoom = createChat();
    }

    @Override
    public String getJID(String occupantName) {
	String result = getCachedJid(occupantName);

	if (result == null) {
	    String jabberID = requestJabberID(occupantName);

	    if (jabberID != null) {
		setCachedJid(occupantName, jabberID);
		result = jabberID;
	    }
	}

	return result;
    }

    @Override
    public boolean isJoined() {
	return chatRoom.isJoined();
    }

    @Override
    public void join() {
	if (!isJoined()) {
	    DiscussionHistory history = new DiscussionHistory();
	    history.setMaxChars(0);

	    try {
		chatRoom.join(credentials.getNick(), credentials.getPassword(),
			history, credentials.getConnectTimeout());
	    } catch (XMPPException e) {
		e.printStackTrace();
	    }
	}
    }

    @Override
    public void leave() {
	if (isJoined()) {
	    chatRoom.leave();
	}
    }

    private MultiUserChat createChat() {
	return new MultiUserChat(parent, credentials.getRoomName());
    }

    private String requestJabberID(String occupantName) {
	String result = null;

	if (occupantName != null) {
	    Presence presence = chatRoom.getOccupantPresence(occupantName);
	    if (presence != null) {

		Object extension = presence
			.getExtension("http://jabber.org/protocol/muc#user");

		if (extension instanceof MUCUser) {
		    MUCUser mucUser = (MUCUser) extension;

		    Item item = mucUser.getItem();

		    if (item != null) {
			try {

			    String sender = presence.getFrom();
			    String fullQualifiedJid = item.getJid();

			    // debug
			    // System.out.println(sender + " " +
			    // fullQualifiedJid);

			    if (sender != null && fullQualifiedJid != null) {
				Matcher m = pattern.matcher(fullQualifiedJid);
				if (m.matches()) {
				    {
					result = m.group(1); // JID is here
				    }
				}
			    }
			} catch (Exception e) {
			    e.printStackTrace();
			}
		    }
		}
	    }
	}

	return result;
    }

    private String getCachedJid(String occupantName) {
	return presenceCache.get(occupantName);
    }

    private void setCachedJid(String occupantName, String jabberID) {
	presenceCache.put(occupantName, jabberID);
    }

    RoomCredentials credentials;
    XMPPConnection parent;

    MultiUserChat chatRoom;

    PresenceCache presenceCache;
    Pattern pattern = Pattern.compile("(.*)/(.*)");
}
