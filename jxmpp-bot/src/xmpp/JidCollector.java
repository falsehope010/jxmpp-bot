package xmpp;

import java.util.concurrent.ConcurrentHashMap;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.packet.MUCUser;
import org.jivesoftware.smackx.packet.MUCUser.Item;

public class JidCollector implements PacketListener {

    public JidCollector() {
	bindings = new ConcurrentHashMap<String, String>();
    }

    @Override
    public void processPacket(Packet packet) {
	if (packet instanceof Presence) {
	    Presence presencePacket = (Presence) packet;

	    Object extension = presencePacket
		    .getExtension("http://jabber.org/protocol/muc#user");

	    if (extension instanceof MUCUser) {
		MUCUser mucUser = (MUCUser) extension;

		Item item = mucUser.getItem();

		if (item != null) {
		    try {

			String fullQualifiedNickName = presencePacket.getFrom();
			String fullQualifiedJid = item.getJid();

			bindings.put(fullQualifiedNickName, fullQualifiedJid);
		    } catch (Exception e) {
			e.printStackTrace();
		    }
		}
	    }
	}
    }

    public String getJid(String nickName) {
	return bindings.get(nickName);
    }

    /**
     * Stores bindings between nickname and it's jid
     */
    ConcurrentHashMap<String, String> bindings;
}
