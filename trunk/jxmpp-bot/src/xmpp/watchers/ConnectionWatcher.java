package xmpp.watchers;

import syslog.ILog;
import xmpp.core.IConnection;

/**
 * Performs {@link IConnection} connected state tracking. If in some time
 * connection becomes closed performs reconnecting
 * 
 * @author tillias
 * @see AbstractActivityWatcher
 * 
 */
public class ConnectionWatcher extends AbstractActivityWatcher {

    /**
     * Creates new watcher using given connection, log and pollTimeout
     * 
     * @param connection
     *            {@link IConnection} that will be monitored for connected state
     * @param log
     *            {@link ILog} that will be used to log all watcher's events
     * @param pollTimeout
     *            Poll timeout for watcher
     * @throws NullPointerException
     *             Thrown if any argument passed to constructor is null pointer
     * @throws IllegalArgumentException
     *             Thrown if pollTimeout argument is negative or zero
     */
    public ConnectionWatcher(IConnection connection, ILog log, int pollTimeout)
	    throws NullPointerException, IllegalArgumentException {
	super(log, pollTimeout);

	if (connection == null)
	    throw new NullPointerException("Connection can't be null");

	this.connection = connection;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation checks whether underlying {@link IConnection} is in
     * connected state
     */
    @Override
    public boolean checkActivityAlive() {
	return connection.isConnected();
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation logs connection open state events only if connection
     * was in disconnected state on previous poll. This way we avoid waste
     * records to be put into log in the case when connection is in connected
     * state for a continuous time
     */
    @Override
    public void logActivityAlive() {
	if (getLastPollInactive())
	    getLog().putMessage("XMPP connection is up.", getLogSenderName(),
		    "Connectivity", "Up");
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation logs connection closed state events only if
     * connection was in connected state on previous poll. This way we avoid
     * waste records to be put into log in the case when connection is in
     * disconnected state for a continuous time
     */
    @Override
    public void logActivityDown() {
	boolean lastTimeActive = !getLastPollInactive();
	if (lastTimeActive)
	    log.putMessage("XMPP connection is down. Reconnecting.",
		    getLogSenderName(), "Connectivity", "Down");
    }

    @Override
    public void logActivityException(Exception e) {
	log.putMessage(e.getMessage(), getLogSenderName(), "Errors",
		"Exception");
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation opens underlying connection
     */
    @Override
    public void startActivity() {
	connection.connect();
    }

    IConnection connection;
}
