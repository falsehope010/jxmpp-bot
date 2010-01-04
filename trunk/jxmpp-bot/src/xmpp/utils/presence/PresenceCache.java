package xmpp.utils.presence;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stores mappings between occupant's name and his jabber identifier.
 * <p>
 * Each occupant of multiuser group chat has fully qualified name and jabber
 * identifier.
 * 
 * @author tillias
 * 
 */
public class PresenceCache {

    public PresenceCache() {
	bindings = new ConcurrentHashMap<String, String>();
    }

    public void put(String occupantName, String jabberID) {
	if (occupantName != null && jabberID != null)
	    bindings.put(occupantName, jabberID);
    }

    public String get(String occupantName) {
	return bindings.get(occupantName);
    }

    Map<String, String> bindings;
}
