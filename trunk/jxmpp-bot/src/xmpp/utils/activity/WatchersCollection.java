package xmpp.utils.activity;

import java.util.ArrayList;
import java.util.List;

import syslog.ILog;

/**
 * Represents wrapper around collection of {@link AbstractActivityWatcher}
 * items. Doesn't allow null pointers to be stored. All items are unique (no
 * duplicates are allowed)
 * 
 * @author tillias
 * 
 */
public class WatchersCollection {

    /**
     * Creates new collection using given {@link ILog} concrete implementation
     * and poll timeout
     * 
     * @param log
     *            Logging facility that will be invoked by watchers stored in
     *            this collection
     * @param pollTimeout
     *            Common poll timeout for all watchers in this collection
     * @throws NullPointerException
     *             Thrown if log argument passed to constructor is null pointer
     * @throws IllegalArgumentException
     *             Thrown if pollTimeout passed to constructor is negative or
     *             zero
     */
    public WatchersCollection(ILog log, int pollTimeout)
	    throws NullPointerException, IllegalArgumentException {

	if (log == null)
	    throw new NullPointerException("Log can't be null");
	if (pollTimeout <= 0)
	    throw new IllegalArgumentException("Poll timeout must be positive");

	items = new ArrayList<AbstractActivityWatcher>();

	this.log = log;
	this.pollTimeout = pollTimeout;
    }

    /**
     * Adds concrete implementation of {@link AbstractActivityWatcher} into this
     * collection. If there is already such an item in collection or argument is
     * null pointer then method does nothing.
     * 
     * @param watcher
     *            Watcher to be added into this collection
     */
    public void add(AbstractActivityWatcher watcher) {
	if (watcher != null && !items.contains(watcher)) {
	    items.add(watcher);
	}
    }

    /**
     * Removes item from this collection. If there is no such an item in
     * collection or argument is null method does nothing
     * 
     * @param watcher
     *            Item to be removed from collection
     */
    public void remove(AbstractActivityWatcher watcher) {
	items.remove(watcher);
    }

    /**
     * Starts all watchers in this collection
     * 
     * @see #stop()
     */
    public void start() {
	for (AbstractActivityWatcher w : items) {
	    w.start();
	}
    }

    /**
     * Stops all watchers in this collection
     * 
     * @see #start()
     */
    public void stop() {
	for (AbstractActivityWatcher w : items) {
	    w.stop();
	}
    }

    /**
     * Gets general log for all watchers in this collection
     * 
     * @return General log for all watchers in this collection
     */
    public ILog getLog() {
	return log;
    }

    /**
     * Gets general poll timeout for all watchers in this collection
     * 
     * @return General poll timeout for all watchers in this collection
     */
    public int getPollTimeout() {
	return pollTimeout;
    }

    List<AbstractActivityWatcher> items;
    ILog log;
    int pollTimeout;

}
