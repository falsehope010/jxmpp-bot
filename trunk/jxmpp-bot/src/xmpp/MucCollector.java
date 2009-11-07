package xmpp;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.packet.MUCUser;
import org.jivesoftware.smackx.packet.MUCUser.Item;

public class MucCollector implements PacketListener {

    public MucCollector() {
	jidToNickMap = new HashMap<String, String>();
    }

    @Override
    public void processPacket(Packet packet) {
	Presence p = (Presence) packet;

	MUCUser mu = (MUCUser) p
		.getExtension("http://jabber.org/protocol/muc#user");
	Item item = mu.getItem();

	jidToNickMap.put(item.getJid(), item.getNick());
    }

    public Set<String> getJids() {
	return jidToNickMap.keySet();
    }

    public Collection<String> getNicks() {
	return jidToNickMap.values();
    }

    HashMap<String, String> jidToNickMap;

}
