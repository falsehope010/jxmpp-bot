package xmpp.utils.activity;

import syslog.ILog;

/**
 * Base class for all activity watchers ( xmpp connections, chat rooms and so
 * on). Activity watcher is active object. On each iteration it polls some
 * underlying activity using {@link #checkActivityAlive()} method which should
 * be <b> overridden </b> in subclasses. If activity is alive watcher sends
 * appropriative message to log using {@link #logActivityAlive()} method. If
 * activity isn't alive (e.g. down) watcher sends message to log using
 * {@link #logActivityDown()} and calls {@link #startActivity()}.
 * <p>
 * If any exception is generated during activity start up or activity lifetime
 * {@link #logActivityException(Exception)} is called
 * 
 * <p>
 * Abstract methods which must be be overridden in subclasses:
 * <ul>
 * <li><code>checkActivityAlive()</code>
 * <li><code>startActivity()</code>
 * <li><code>logActivityAlive()</code>
 * <li><code>logActivityDown()</code>
 * <li><code>logActivityException()</code>
 * </ul>
 * 
 * @author tilllias
 * 
 */
public abstract class AbstractActivityWatcher implements Runnable {
    /**
     * Default connection poll timeout in milliseconds
     */
    public static final int DEFAULT_POLL_TIMEOUT = 30000;

    public AbstractActivityWatcher(ILog log)
	    throws NullPointerException {
	this(log, DEFAULT_POLL_TIMEOUT);
    }

    public AbstractActivityWatcher(ILog log, int pollTimeout)
	    throws NullPointerException, IllegalArgumentException {
	if (log == null)
	    throw new NullPointerException(
		    "Log argument can't be null");
	if (pollTimeout <= 0)
	    throw new IllegalArgumentException("pollTimeout must be positive");

	this.log = log;
	this.pollTimeout = pollTimeout;

	terminate = false;
	logSenderName = getLogSenderName();
	pollCounter = 0;
	lastPollInactive = false;
    }

    /**
     * Gets value indicating that activity under control is alive
     * 
     * @return True if activity is alive, false otherwise
     */
    public abstract boolean checkActivityAlive();

    /**
     * Starts activity
     */
    public abstract void startActivity();

    /**
     * Is called by watcher if activity is alive on next poll
     */
    public abstract void logActivityAlive();

    /**
     * Is called by watcher if activity is down on next poll
     */
    public abstract void logActivityDown();

    /**
     * Is called by watcher if activity has thrown any exception during start up
     * or it's run
     * 
     * @param e
     *            Exception thrown by activity
     */
    public abstract void logActivityException(Exception e);

    /**
     * This implementation defines main lifeline of watcher.
     */
    @Override
    public void run() {
	while (!terminate) {
	    try {
		// do work
		boolean isAlive = checkActivityAlive();

		if (!isAlive) {
		    restartActivity();
		} else {

		    logActivityAlive();

		    lastPollInactive = false;
		}

		++pollCounter;

		Thread.sleep(getPollTimeout());

	    } catch (Exception e) {
		logActivityException(e);

	    }
	}

	pollCounter = 0;
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

    /**
     * Stops watcher. Method returns immediately but doesn't guarantee that
     * watcher will be stopped immediately.
     * 
     * @see #isAlive()
     */
    public void stop() {
	terminate = true;
    }

    /**
     * Gets total number of milliseconds that watcher waits between polls
     * 
     * @return Total number of milliseconds
     */
    public int getPollTimeout() {
	return pollTimeout;
    }

    /**
     * Gets total number of polls since watcher has been started
     * 
     * @return Total number of polls since watcher has been started
     */
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

    protected ILog getLog() {
	return log;
    }

    private String getLogSenderName() {
	return getClass().getSimpleName();
    }

    private void restartActivity() {
	logActivityDown();

	if (!lastPollInactive) {
	    lastPollInactive = true;
	}

	startActivity();
    }

    ILog log;

    boolean terminate;
    Thread thread;
    int pollTimeout;
    int pollCounter;
    boolean lastPollInactive;

    String logSenderName;
}
