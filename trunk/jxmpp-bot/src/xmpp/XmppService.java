package xmpp;

import xmpp.configuration.Configuration;
import xmpp.utils.collections.RoomWatchersCollection;

public class XmppService {

    public XmppService(Configuration config) {
	if (config == null)
	    throw new NullPointerException("Configuration can't be null");

	this.config = config;
    }

    Configuration config;
    RoomWatchersCollection watchers;
}
