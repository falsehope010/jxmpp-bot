package xmpp.utils.presence;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.packet.MUCUser;
import org.jivesoftware.smackx.packet.MUCUser.Item;

/**
 * Provides the set of helper methods for processing {@link Presence} packets
 * 
 * @author tillias
 * 
 */
public class PresenceProcessor {

    /**
     * Requests {@link Presence} packet or given occupant in given chat room
     * 
     * @param chatRoom
     *            Chat room where occupant possibly participates
     * @param occupantName
     *            Occupant name (e.g. john_doe@conference.xmpp.org/xmpp_client)
     * @return Well-formed jabber identifier for given occupant if succeded,
     *         null pointer otherwise
     */
    public String requestJabberID(MultiUserChat chatRoom, String occupantName) {
	String result = null;

	if (chatRoom != null && occupantName != null) {
	    try {

		Presence presence = requestPresence(chatRoom, occupantName);
		result = getJabberID(presence);

	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
	return result;
    }

    /**
     * Attempts to extract jabber identifier from given {@link Presence} object
     * 
     * @param presence
     *            Presence object which possible store jabber identifier.
     * @return Valid jabber identifier if succeded, null pointer otherwise
     */
    public String getJabberID(Presence presence) {

	String result = null;

	if (presence != null) {

	    Object extension = presence
		    .getExtension("http://jabber.org/protocol/muc#user");

	    if (extension instanceof MUCUser) {
		MUCUser mucUser = (MUCUser) extension;

		Item item = mucUser.getItem();

		if (item != null) {

		    String sender = presence.getFrom();
		    String fullQualifiedJid = item.getJid();

		    // debug
		    // System.out.println(fullQualifiedJid);

		    if (sender != null && fullQualifiedJid != null) {
			Matcher m = pattern.matcher(fullQualifiedJid);
			if (m.matches()) {
			    {
				result = m.group(1); // JID is here
			    }
			}
		    }
		}
	    }
	}

	return result;
    }

    protected Presence requestPresence(MultiUserChat chatRoom,
	    String occupantName) {

	Presence result = null;

	if (chatRoom != null)
	    result = chatRoom.getOccupantPresence(occupantName);

	return result;
    }

    final Pattern pattern = Pattern.compile("(.*)/(.*)");
}
