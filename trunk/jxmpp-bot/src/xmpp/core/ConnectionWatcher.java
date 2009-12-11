package xmpp.core;

import syslog.ILog;

/**
 * Represents active object which polls the {@link IConnection} that is under
 * control for it's status. If connection is in the disconnected state at the
 * moment of poll than watcher attempts to reconnect.
 * <p>
 * All reconnection attempts are logged using {@link ILog} implementation that
 * is passed to watcher's constructor.
 * <p>
 * Normally {@link #start()} should be called after watcher creation in order to
 * initiate regular polling of {@link IConnection} state under controll. Current
 * status of watcher can be determined using {@link #isAlive()} method. After
 * watcher is not needed anymore {@link #stop()} method should be called.
 * 
 * @author tillias
 * 
 */
public class ConnectionWatcher implements Runnable {

    /**
     * Default connection poll timeout in milliseconds
     */
    public static final int DEFAULT_POLL_TIMEOUT = 30000;

    public ConnectionWatcher(IConnection connection, ILog log)
	    throws NullPointerException {
	this(connection, log, DEFAULT_POLL_TIMEOUT);
    }

    public ConnectionWatcher(IConnection connection, ILog log, int pollTimeout)
	    throws NullPointerException, IllegalArgumentException {
	if (connection == null || log == null)
	    throw new NullPointerException(
		    "Connection or log arguments can't be null");
	if (pollTimeout <= 0)
	    throw new IllegalArgumentException("pollTimeout must be positive");

	this.connection = connection;
	this.log = log;
	this.pollTimeout = pollTimeout;

	terminate = false;
	logSenderName = getLogSenderName();
	pollCounter = 0;
	lastPollDisconnected = false;
    }

    @Override
    public void run() {
	while (!terminate) {
	    try {
		// do work
		boolean isConnected = getConnection().isConnected();

		if (!isConnected) {
		    log.putMessage("XMPP connection is down. Reconnecting.",
			    logSenderName, "Connectivity", "Down");

		    if (!lastPollDisconnected) {
			lastPollDisconnected = true;
		    }

		    connection.connect();
		} else {
		    log.putMessage("XMPP connection is up.", logSenderName,
			    "Connectivity", "Up");

		    lastPollDisconnected = false;
		}

		++pollCounter;

		Thread.sleep(getPollTimeout());

	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
    }

    /**
     * Starts watcher. If watcher is already running does nothing
     * 
     * @see #isAlive()
     */
    public void start() {
	if (!isAlive()) {
	    thread = new Thread(this);
	    thread.start();
	}
    }

    public void stop() {
	terminate = true;
    }

    public int getPollTimeout() {
	return pollTimeout;
    }

    public int getPollCount() {
	return pollCounter;
    }

    /**
     * Gets value indicating whether watcher is running
     * 
     * @return True if watcher is running false otherwise
     */
    public boolean isAlive() {
	boolean result = false;

	if (thread != null && thread.isAlive()) {
	    result = true;
	}

	return result;
    }

    private IConnection getConnection() {
	return connection;
    }

    private String getLogSenderName() {
	return getClass().getSimpleName();
    }

    IConnection connection;
    ILog log;

    boolean terminate;
    Thread thread;
    int pollTimeout;
    int pollCounter;
    boolean lastPollDisconnected;

    String logSenderName;
}
