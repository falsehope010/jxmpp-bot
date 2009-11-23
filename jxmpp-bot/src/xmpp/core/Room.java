package xmpp.core;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;

import xmpp.configuration.RoomCredentials;
import xmpp.listeners.ChatMessageListener;
import xmpp.processing.IProcessor;
import xmpp.utils.presence.PresenceCache;
import xmpp.utils.presence.PresenceProcessor;

public class Room implements IRoom {

    public Room(RoomCredentials credentials, XMPPConnection parent,
	    IProcessor messageProcessor) {
	if (credentials == null || parent == null)
	    throw new NullPointerException(
		    "Argument passed to Room constructor can't be null");

	if (messageProcessor == null)
	    throw new NullPointerException("Message processor can't be null");

	this.credentials = credentials;
	this.parent = parent;
	this.messageProcessor = messageProcessor;

	presenceCache = new PresenceCache();
	presenceProcessor = new PresenceProcessor();
	chat = createChat();
	addMessageListener(chat);
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

    @Override
    public String getName() {
	return credentials.getRoomName();
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

    private void addMessageListener(MultiUserChat multiUserChat) {
	if (multiUserChat != null) {
	    listener = new ChatMessageListener(chat, messageProcessor);
	    multiUserChat.addMessageListener(listener);
	}
    }

    RoomCredentials credentials;
    XMPPConnection parent;

    MultiUserChat chat;
    ChatMessageListener listener;
    IProcessor messageProcessor;

    PresenceCache presenceCache;
    PresenceProcessor presenceProcessor;

}
