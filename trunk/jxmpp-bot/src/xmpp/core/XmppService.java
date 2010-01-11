package xmpp.core;

import syslog.ILog;
import xmpp.configuration.Configuration;
import xmpp.processing.IProcessor;

public class XmppService {

    public XmppService(Configuration config, ILog log) {
	if (config == null)
	    throw new NullPointerException("Configuration can't be null");
	if (log == null)
	    throw new NullPointerException("Log can't be null");

	this.config = config;
	this.log = log;
    }

    public void initialize() {

    }

    public void shutdown() {

    }

    IConnection connection;
    IProcessor messageProcessor;

    StatusWatcher statusWatcher;
    Configuration config;
    ILog log;
}
