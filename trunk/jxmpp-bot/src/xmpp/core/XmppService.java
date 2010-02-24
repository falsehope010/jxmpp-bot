package xmpp.core;

import syslog.ILog;
import xmpp.configuration.Configuration;
import xmpp.configuration.ConnectionCredentials;
import xmpp.configuration.RoomCredentials;
import xmpp.messaging.base.Message;
import xmpp.processing.IProcessor;
import xmpp.queue.IMessageQueue;
import xmpp.queue.TransportQueue;
import activity.ActivityUtils;

public class XmppService {

    public XmppService(Configuration config, ILog log)
	    throws NullPointerException {
	if (config == null)
	    throw new NullPointerException("Configuration can't be null");
	if (log == null)
	    throw new NullPointerException("Log can't be null");

	this.config = config;
	this.log = log;
	this.statusWatcher = new StatusWatcher(log, 60000);

	connection = createConnection(config);

	transportQueue = new TransportQueue(connection, 100);
	ActivityUtils.start(transportQueue);

	messageProcessor.setTransport(transportQueue);

    }

    public void start() {
	connection.connect();
	statusWatcher.watchConnection(connection);

	if (connection != null && config != null) {
	    RoomCredentials[] roomCredentials = config.getRoomsCredentials();

	    if (roomCredentials != null && roomCredentials.length > 0) {
		for (RoomCredentials rc : roomCredentials) {
		    IRoom room = connection.createRoom(rc);
		    statusWatcher.watchRoom(room);
		}
	    }
	}
    }

    public void shutdown() {

    }

    private IConnection createConnection(Configuration config) {
	IConnection result = null;

	if (config != null) {

	    messageProcessor = createProcessor();

	    ConnectionCredentials credentials = config.getCredentials();
	    result = new Connection(credentials, messageProcessor);
	}

	return result;
    }

    /**
     * TODO:
     * 
     * @return
     */
    private IProcessor createProcessor() {
	return new IProcessor() {

	    @Override
	    public void processMessage(Message msg) {
		System.out.println(msg);
	    }

	    @Override
	    public void setTransport(IMessageQueue queue) {
		// stub method
	    }
	};
    }

    IConnection connection;
    IProcessor messageProcessor;
    IMessageQueue transportQueue;

    StatusWatcher statusWatcher;
    Configuration config;
    ILog log;
}
