package xmpp.listeners;

import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.muc.MultiUserChat;

import xmpp.utils.presence.PresenceCache;

/**
 * Listens for multi-user chat presence packets and updates/populates underlying
 * presence cache
 * 
 * @author tillias
 * 
 */
public class ChatPresenceListener extends AbstractChatListener {

    /**
     * Creates new listener using given presence cache and multi-user chat
     * 
     * @param cache
     *            Presence cache that will be updated/populated by this listener
     *            on any presence packet recieved
     * @param chat
     *            Multi-user chat that this listener listens for presence
     *            packets
     * @throws NullPointerException
     *             Thrown if any argument passed to constructor is null
     */
    public ChatPresenceListener(PresenceCache cache, MultiUserChat chat)
	    throws NullPointerException {
	super(cache, chat);
    }

    @Override
    public void processPacket(Packet packet) {
	if (packet instanceof Presence) {
	    String jabberID = presenceProcessor.getJabberID((Presence) packet);

	    if (jabberID != null) {
		getCache().put(packet.getFrom(), jabberID);
	    }
	}
    }
}
