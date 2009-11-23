package xmpp.listeners;

import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.muc.MultiUserChat;

import xmpp.utils.presence.PresenceCache;
import xmpp.utils.presence.PresenceProcessor;

public class ChatPresenceListener extends AbstractChatListener {

    public ChatPresenceListener(PresenceCache cache, MultiUserChat chat)
	    throws NullPointerException {
	super(cache, chat);
	this.presenceProcessor = new PresenceProcessor();
    }

    @Override
    public void processPacket(Packet packet) {
	if (packet instanceof Presence) {
	    String jabberID = presenceProcessor.getJabberID((Presence) packet);

	    // debug
	    // System.out.println("\n\nRecieved presence: " + jabberID + " "
	    // + packet.getFrom() + "\n\n");

	    if (jabberID != null) {
		getCache().put(packet.getFrom(), jabberID);
	    }
	}
    }
}
