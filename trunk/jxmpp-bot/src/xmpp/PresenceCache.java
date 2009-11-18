package xmpp;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
