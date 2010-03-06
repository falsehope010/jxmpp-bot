package xmpp.core;

import syslog.ILog;
import xmpp.watchers.ConnectionWatcher;
import xmpp.watchers.RoomWatcher;
import xmpp.watchers.WatchersCollection;

/**
 * Represents {@link IConnection} and {@link IRoom} state watcher. Can manage
 * multiple connections and rooms simultaneously.
 * <p>
 * If any connection that is under control by this watcher looses it's connected
 * state (e.g. temporary network failure) performs automatic reconnection. If
 * any room that is under control by this watcher loosed it's joined state (e.g.
 * temporary xmpp server failure) performs automatic rejoin.
 * <p>
 * All reconnection/rejoin attempts are logged using {@link ILog}. After object
 * is no longer needed, {@link #stop()} should be called.
 * 
 * @author tillias
 * 
 */
public class StatusWatcher {

    /**
     * Creates new instance of watcher using given log and poll timeout
     * 
     * @param log
     *            Will be used for logging reconnect/rejoin attempts and other
     *            events
     * @param pollTimeout
     *            Timeout before watcher polls objects for their
     *            connected/joined status
     * @throws NullPointerException
     *             Thrown of any argument passed to constructor is null
     * @throws IllegalArgumentException
     *             Thrown if poll timeout argument is negative or zero
     */
    public StatusWatcher(ILog log, int pollTimeout)
	    throws NullPointerException, IllegalArgumentException {
	if (log == null)
	    throw new NullPointerException("Log can't be null");

	if (pollTimeout <= 0)
	    throw new IllegalArgumentException("Poll timeout must be positive");

	this.log = log;
	this.pollTimeout = pollTimeout;

	watchers = new WatchersCollection(log, pollTimeout);
    }

    /**
     * Begins watching for connected stated of given connection. Once connected
     * state is lost performs automatic reconnect.
     * <p>
     * If argument passed to this method is null does nothing
     * 
     * @param conn
     *            Connection to be watched
     */
    public void watchConnection(IConnection conn) {
	if (conn != null) {
	    ConnectionWatcher watcher = new ConnectionWatcher(conn, log,
		    pollTimeout);
	    watcher.start();
	    watchers.add(watcher);
	}
    }

    /**
     * Begins watching for joined state of given room. Once joined state is lost
     * performs automatic rejoin
     * <p>
     * If argument passed to this method is null does nothing
     * 
     * @param room
     *            Room to be watched
     */
    public void watchRoom(IRoom room) {
	if (room != null) {
	    RoomWatcher watcher = new RoomWatcher(room, log, pollTimeout);
	    watcher.start();
	    watchers.add(watcher);
	}
    }

    /**
     * Stops watching for all objects which are managed by this watcher
     */
    public void stop() {
	watchers.stop();
    }

    WatchersCollection watchers;
    ILog log;
    int pollTimeout;
}
