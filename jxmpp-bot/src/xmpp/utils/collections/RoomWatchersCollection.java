package xmpp.utils.collections;

import java.util.ArrayList;
import java.util.List;

import syslog.ILog;
import xmpp.core.IRoom;
import xmpp.utils.activity.RoomWatcher;

/**
 * Represents wrapper around collection of {@link RoomWatcher}.
 * 
 * @author tillias
 * 
 */
public class RoomWatchersCollection {

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
    public RoomWatchersCollection(ILog log, int pollTimeout)
	    throws NullPointerException, IllegalArgumentException {

	if (log == null)
	    throw new NullPointerException("Log can't be null");
	if (pollTimeout <= 0)
	    throw new IllegalArgumentException("Poll timeout must be positive");

	items = new ArrayList<RoomWatcher>();

	this.log = log;
	this.pollTimeout = pollTimeout;
    }

    /**
     * Creates {@link RoomWatcher} for given {@link IRoom} using general log and
     * poll timeout and starts watcher if needed
     * 
     * @param room
     *            {@link IRoom} concrete implementation that should be watched.
     *            If null pointer is passed does nothing
     * @param startImmediately
     *            Specifies that room should be started watching immediately
     * @see #getLog()
     * @see #getPollTimeout()
     */
    public void watchRoom(IRoom room, boolean startImmediately) {
	if (room != null) {
	    RoomWatcher watcher = new RoomWatcher(room, getLog(),
		    getPollTimeout());
	    items.add(watcher);

	    if (startImmediately)
		watcher.start();
	}
    }

    /**
     * Starts all watchers in this collection
     * 
     * @see #stop()
     */
    public void start() {
	for (RoomWatcher w : items) {
	    w.start();
	}
    }

    /**
     * Stops all watchers in this collection
     * 
     * @see #start()
     */
    public void stop() {
	for (RoomWatcher w : items) {
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

    List<RoomWatcher> items;
    ILog log;
    int pollTimeout;
}
