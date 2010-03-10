package xmpp.core;

import plugins.PluginManager;
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

/**
 * Service which is responsible for handling xmpp issues: establishing
 * connection to remote server, keeping connection alive, managing multi-user
 * chat rooms, transporting.
 * <p>
 * Creates and manages {@link PluginManager}
 * <p>
 * Service can be started and stopped multiple times.
 * 
 * @author tillias
 * 
 */
public class XmppService {

    /**
     * Creates new service using given {@link Configuration} and system log
     * implementation
     * 
     * @param config
     *            Configuration of service
     * @param log
     *            System log which will be used for logging various events,
     *            errors etc
     * @throws NullPointerException
     *             Thrown if any argument passed to constructor is null
     * @throws ConfigurationException
     *             Thrown if configuration of service is invalid
     */
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

    /**
     * Starts service, attempts to establish connection to remote xmpp server
     * and joins all chat rooms enlisted in configuration.
     * <p>
     * If service is already started does nothing
     * 
     * @return True if service has been started, false otherwise
     * @see #isRunning()
     * @see #shutdown()
     */
    public boolean start() {
	boolean result = false;

	if (!isRunning()) {

	    ActivityUtils.start(messageProcessor);
	    ActivityUtils.start(transportQueue);

	    connection.connect();

	    if (connection.isConnected()) {
		statusWatcher.watchConnection(connection);

		RoomCredentials[] roomCredentials = config
			.getRoomsCredentials();

		if (roomCredentials != null && roomCredentials.length > 0) {
		    for (RoomCredentials rc : roomCredentials) {
			IRoom room = connection.createRoom(rc);
			statusWatcher.watchRoom(room);
		    }
		}

		setRunning(true);
	    }
	}

	return result;
    }

    /**
     * Stops service (if already started), closes connection to remote server
     * and leaves all chat rooms listed in configuration. After service is
     * stopped it can be stared once again.
     * 
     * @see #start()
     * @see #isRunning()
     */
    public void shutdown() {
	if (isRunning()) {

	    connection.disconnect();

	    ActivityUtils.stop(transportQueue);
	    ActivityUtils.stop(messageProcessor);

	    transportQueue.clear();

	    statusWatcher.stop();

	    setRunning(false);
	}
    }

    /**
     * Gets value indicating that service has been started and is running.
     * 
     * @return True if service is started and running, false otherwise
     * @see #start()
     * @see #shutdown()
     */
    public boolean isRunning() {
	return serviceRunning;
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

    private void setRunning(boolean value) {
	serviceRunning = value;
    }

    IConnection connection;
    IProcessor messageProcessor;
    IMessageQueue transportQueue;

    StatusWatcher statusWatcher;
    Configuration config;
    ILog log;

    boolean serviceRunning;
}
