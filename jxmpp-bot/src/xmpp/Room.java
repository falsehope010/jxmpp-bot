package xmpp;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;

import xmpp.configuration.RoomCredentials;
import xmpp.utils.PresenceCache;
import xmpp.utils.PresenceProcessor;

public class Room implements IRoom {

    public Room(RoomCredentials credentials, XMPPConnection parent) {
	if (credentials == null || parent == null)
	    throw new NullPointerException(
		    "Argument passed to Room constructor can't be null");

	this.credentials = credentials;
	this.parent = parent;

	presenceCache = new PresenceCache();
	presenceProcessor = new PresenceProcessor();
	chat = createChat();
    }

    @Override
    public String getJID(String occupantName) {
	String result = getCachedJid(occupantName);

	if (result == null) {
	    String jabberID = requestJabberID(chat, occupantName);

	    if (jabberID != null) {
		setCachedJid(occupantName, jabberID);
		result = jabberID;
	    }
	}

	return result;
    }

    @Override
    public boolean isJoined() {
	return chat.isJoined();
    }

    @Override
    public void join() {
	if (!isJoined()) {
	    DiscussionHistory history = new DiscussionHistory();
	    history.setMaxChars(0);

	    try {
		chat.join(credentials.getNick(), credentials.getPassword(),
			history, credentials.getConnectTimeout());
	    } catch (XMPPException e) {
		e.printStackTrace();
	    }
	}
    }

    @Override
    public void leave() {
	if (isJoined()) {
	    chat.leave();
	}
    }

    private MultiUserChat createChat() {
	return new MultiUserChat(parent, credentials.getRoomName());
    }

    private String requestJabberID(MultiUserChat chatRoom, String occupantName) {
	return presenceProcessor.requestJabberID(chatRoom, occupantName);
    }

    private String getCachedJid(String occupantName) {
	return presenceCache.get(occupantName);
    }

    private void setCachedJid(String occupantName, String jabberID) {
	presenceCache.put(occupantName, jabberID);
    }

    RoomCredentials credentials;
    XMPPConnection parent;

    MultiUserChat chat;

    PresenceCache presenceCache;
    PresenceProcessor presenceProcessor;
}