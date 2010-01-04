package xmpp.utils.activity;

import syslog.ILog;
import xmpp.core.IRoom;

/**
 * Performs {@link IRoom} joined state tracking. If in some time room looses
 * joined state performs rejoin. Logs several events into {@link ILog} while
 * watching
 * 
 * @author tillias
 * @see AbstractActivityWatcher
 * 
 */
public class RoomWatcher extends AbstractActivityWatcher {

    /**
     * Creates new instance of watcher using given room log and pollTimeout
     * 
     * @param room
     *            {@link IRoom} that will be monitored for joined state
     * @param log
     *            {@link ILog} that will be used to log all watcher's events
     * @param pollTimeout
     *            Poll timeout for watcher
     * @throws NullPointerException
     *             Thrown if any argument passed to constructor is null
     * @throws IllegalArgumentException
     *             Thrown if pollTimeout is negative or zero
     */
    public RoomWatcher(IRoom room, ILog log, int pollTimeout)
	    throws NullPointerException, IllegalArgumentException {
	super(log, pollTimeout);

	if (room == null)
	    throw new NullPointerException("Room can't be null");

	this.room = room;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation checks whether underlying {@link IRoom} is in joined
     * state
     */
    @Override
    public boolean checkActivityAlive() {
	return room.isJoined();
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation logs room joined state only if it was in left state
     * on previous poll. This way we avoid waste log records in the case when
     * the room is in joined state for a continuous time
     */
    @Override
    public void logActivityAlive() {
	if (getLastPollInactive()) {
	    getLog().putMessage("Chat room [" + room.getName() + "]",
		    getLogSenderName(), "Connectivity", "Joined");
	}
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation logs room left state only if it was in joined state
     * on previous poll. This way we avoid waste log records in the case when
     * the room is in left state for a continuous time
     */
    @Override
    public void logActivityDown() {
	if (!getLastPollInactive()) {
	    getLog().putMessage("Chat room [" + room.getName() + "]",
		    getLogSenderName(), "Connectivity", "Left");
	}

    }

    @Override
    public void logActivityException(Exception e) {
	getLog().putMessage(e.getMessage(), getLogSenderName(), "Errors",
		"Exception");

    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation joins underlying {@link IRoom}
     */
    @Override
    public void startActivity() {
	room.join();
    }

    IRoom room;
}
