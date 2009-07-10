package syslog.rotate;

import java.util.Date;

import database.Database;
import exceptions.DatabaseNotConnectedException;

/**
 * Represents log rotate strategy which updates it's rotation time using equal
 * intervals (iterations)
 * 
 * @author tillias
 * 
 */
public abstract class IterativeLogRotateStrategy extends
		AbstractLogRotateStrategy {

	/**
	 * Creates new instance of strategy and sets rotation time to current system
	 * time plus iteration time in milliseconds.
	 * 
	 * @param db
	 *            Database which will be used by strategy to perform logs
	 *            rotation
	 *            <p>
	 * @param iterationTime
	 *            Time between iterations. If value is less or equal to zero,
	 *            {@link #DEFAULT_ITERATION_TIME} used instead
	 * @throws NullPointerException
	 *             Thrown if database parameter is null reference
	 * @throws DatabaseNotConnectedException
	 *             Thrown if database parameter is passed in disconnected state
	 * @see #getIterationTime()
	 * @see #updateRotationDate()
	 */
	protected IterativeLogRotateStrategy(Database db, long iterationTime)
			throws NullPointerException, DatabaseNotConnectedException {
		super(db);

		if (iterationTime > 0) {
			this.iterationTime = iterationTime;
		} else {
			this.iterationTime = DEFAULT_ITERATION_TIME;
		}

		updateRotationDate();
	}

	@Override
	public Date getRotationDate() {
		return currentRotationTime;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Current implementation gets current system time (time is taken at the
	 * moment of method call) and adds iteration time
	 * 
	 * @see #getIterationTime()
	 */
	@Override
	public void updateRotationDate() {
		currentRotationTime = calculateRotationDate();
	}

	/**
	 * Gets iteration time in milliseconds, e.g. how much milliseconds will be
	 * added to current system time during update of next rotation time
	 * 
	 * @return Iteration time in milliseconds
	 */
	public long getIterationTime() {
		return iterationTime;
	}

	private Date calculateRotationDate() {
		long ms = System.currentTimeMillis() + iterationTime;

		return new Date(ms);
	}

	long iterationTime;
	Date currentRotationTime;

	/**
	 * Default time between rotation iterations.
	 */
	public static final long DEFAULT_ITERATION_TIME = 60000;
}
