package xmpp.core;

import syslog.ILog;
import xmpp.configuration.Configuration;
import xmpp.configuration.ConnectionCredentials;
import xmpp.configuration.RoomCredentials;
import xmpp.messaging.PublicChatMessage;
import xmpp.messaging.base.Message;
import xmpp.messaging.domain.ParticipantInfo;
import xmpp.processing.IProcessor;
import xmpp.processing.MessageProcessor;
import xmpp.queue.IMessageQueue;
import xmpp.queue.TransportQueue;
import activity.ActivityUtils;
import exceptions.ConfigurationException;

public class XmppService {

    public XmppService(Configuration config, ILog log)
	    throws NullPointerException, ConfigurationException {
	if (config == null)
	    throw new NullPointerException("Configuration can't be null");
	if (log == null)
	    throw new NullPointerException("Log can't be null");

	this.config = config;
	this.log = log;

	statusWatcher = new StatusWatcher(log, 60000);

	messageProcessor = createProcessor();

	// throws exceptions
	connection = createConnection(config, messageProcessor);

	// throws exceptions
	transportQueue = new TransportQueue(connection, 100);

	messageProcessor.setTransport(transportQueue);
    }

    public void start() {
	if (!isStarted()) {

	    ActivityUtils.start(messageProcessor);
	    ActivityUtils.start(transportQueue);

	    connection.connect();
	    statusWatcher.watchConnection(connection);

	    RoomCredentials[] roomCredentials = config.getRoomsCredentials();

	    if (roomCredentials != null && roomCredentials.length > 0) {
		for (RoomCredentials rc : roomCredentials) {
		    IRoom room = connection.createRoom(rc);
		    statusWatcher.watchRoom(room);
		}
	    }

	    setStarted(true);
	}
    }

    public void shutdown() {
	if (isStarted()) {

	    connection.disconnect();

	    ActivityUtils.stop(transportQueue);
	    ActivityUtils.stop(messageProcessor);

	    transportQueue.clear();

	    statusWatcher.stop();

	    setStarted(false);
	}
    }

    /**
     * Creates new {@link Connection} using given configuration.
     * 
     * @param conf
     *            Configuration that will be used during construction of
     *            connection
     * @param processor
     *            {@link MessageProcessor} which will be used by connection
     * @return Valid {@link Connection} instance
     * @throws ConfigurationException
     *             Thrown if invalid configuration is passed
     * @throws NullPointerException
     *             Thrown if any argument passed to this method is null
     */
    private IConnection createConnection(Configuration conf,
	    IProcessor processor) throws ConfigurationException,
	    NullPointerException {

	if (conf == null || processor == null)
	    throw new NullPointerException("Arguments can't be null");

	ConnectionCredentials credentials = conf.getCredentials();

	if (credentials == null)
	    throw new ConfigurationException(
		    "Connection credentials can't be null");

	return new Connection(credentials, messageProcessor);

    }

    /**
     * TODO:
     * 
     * @return
     */
    private IProcessor createProcessor() {
	IProcessor result = new IProcessor() {

	    @Override
	    public void processMessage(Message msg) {
		System.out.println(msg);

		if (msg instanceof PublicChatMessage) {
		    PublicChatMessage pubMessage = (PublicChatMessage) msg;

		    if (!pubMessage.getSender().getJabberID().equals(
			    (config.getCredentials().getNick() + '@' + config
				    .getCredentials().getServer()))) {

			ParticipantInfo sender = new ParticipantInfo(config
				.getCredentials().getNick()
				+ '@' + config.getCredentials().getServer(),
				"vegatrek@conference.jabber.ru");

			PublicChatMessage responce = new PublicChatMessage(
				sender, "Responce!", pubMessage.getRoomName());

			boolean shutDown = false;

			if (pubMessage.getText().equals("shutdown")) {
			    responce = new PublicChatMessage(sender,
				    "Shutting down!", pubMessage.getRoomName());

			    shutdown();
			    shutDown = true;
			}

			queue.add(responce);
		    }
		}
	    }

	    @Override
	    public void setTransport(IMessageQueue queue) {
		this.queue = queue;
	    }

	    IMessageQueue queue;
	};

	return result;
    }

    private void setStarted(boolean value) {
	serviceStarted = value;
    }

    private boolean isStarted() {
	return serviceStarted;
    }

    IConnection connection;
    IProcessor messageProcessor;
    IMessageQueue transportQueue;

    StatusWatcher statusWatcher;
    Configuration config;
    ILog log;

    boolean serviceStarted;
}
