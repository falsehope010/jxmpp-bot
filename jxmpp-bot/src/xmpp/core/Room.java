package xmpp.core;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;

import xmpp.configuration.RoomCredentials;
import xmpp.listeners.ChatMessageListener;
import xmpp.listeners.ChatPresenceListener;
import xmpp.processing.IProcessor;
import xmpp.utils.presence.PresenceCache;
import xmpp.utils.presence.PresenceProcessor;

/**
 * Represents chat room. Stores internal {@link PresenceCache} which is shared
 * by this room itself and all it's packet listeners.
 * <p>
 * Instances of this class should be created by {@link IConnection}
 * implementations
 * 
 * @author tillias
 * @see IConnection
 * @see Connection
 * 
 */
public class Room implements IRoom {

    /**
     * Package level constructor. {@link Connection} should be used to create
     * {@link Room} instances
     * 
     * @param credentials
     *            {@link RoomCredentials} instance which will be used to
     *            configure room
     * @param parent
     *            Parent connection
     * @param messageProcessor
     *            {@link IProcessor} implementation which will receive all group
     *            chat messages from given room
     * @throws NullPointerException
     *             Thrown if any argument passed to constructor is null
     * @see {@link IConnection}
     * @see {@link Connection}
     */
    Room(RoomCredentials credentials, XMPPConnection parent,
	    IProcessor messageProcessor) throws NullPointerException {

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
	addListeners(chat);
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

    /**
     * Creates new {@link ChatPresenceListener} and {@link ChatMessageListener}
     * and associates them with given chat room. Shared {@link PresenceCache} is
     * used.
     * 
     * @param multiUserChat
     *            Chat room
     */
    private void addListeners(MultiUserChat multiUserChat) {
	if (multiUserChat != null) {
	    listener = new ChatMessageListener(presenceCache, chat,
		    messageProcessor);
	    presenceListener = new ChatPresenceListener(presenceCache, chat);

	    multiUserChat.addMessageListener(listener);
	    multiUserChat.addParticipantListener(presenceListener);
	}
    }

    RoomCredentials credentials;
    XMPPConnection parent;

    MultiUserChat chat;
    ChatMessageListener listener;
    ChatPresenceListener presenceListener;

    IProcessor messageProcessor;

    PresenceCache presenceCache;
    PresenceProcessor presenceProcessor;

}
