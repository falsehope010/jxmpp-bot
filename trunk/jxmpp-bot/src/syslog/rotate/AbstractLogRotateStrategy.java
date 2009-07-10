package syslog.rotate;

import mappers.SyslogMessageMapper;
import database.Database;
import exceptions.DatabaseNotConnectedException;

/**
 * Skeletal implementation class for ILogRotateStrategy
 * 
 * @author tillias_work
 * 
 */
public abstract class AbstractLogRotateStrategy implements ILogRotateStrategy {

	/**
	 * Gets value indicating how many times log rotation was performed
	 * 
	 * @return Total number of executions
	 */
	public int getRotationsCount() {
		return rotationsCounter;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * For invocation by subclasses. Does nothing except setting correct
	 * lastRunDate and rotations counter
	 * 
	 * @see #getLastRotateDate()
	 * @see #getRotationsCount()
	 */
	public boolean rotate() {
		boolean result = true;

		++rotationsCounter;

		return result;
	}

	/**
	 * Sole constructor. For invocation by subclass constructors, typically
	 * implicit.
	 * <p>
	 * Checks database and creates {@link SyslogMessageMapper} which will be
	 * possible used by subclasses
	 * 
	 * @param db
	 *            Database instance which will be used by strategy
	 * @throws DatabaseNotConnectedException
	 *             Thrown if database parameter passed to constructor is not in
	 *             connected state
	 * @throws NullPointerException
	 *             Thrown if database parameter passed to constructor is null
	 *             reference
	 * @see #getMessageMapper()
	 */
	protected AbstractLogRotateStrategy(Database db)
			throws NullPointerException, DatabaseNotConnectedException {
		this.db = db;
		messageMapper = new SyslogMessageMapper(db);
	}

	/**
	 * Sets log rotations counter.
	 * 
	 * @param count
	 */
	protected void setRotationsCount(int count) {
		rotationsCounter = count;
	}

	/**
	 * Gets underlying syslog message mapper.
	 * 
	 * @return Valid {@link SyslogMessageMapper}
	 */
	protected SyslogMessageMapper getMessageMapper() {
		return messageMapper;
	}

	Database db;
	int rotationsCounter;
	SyslogMessageMapper messageMapper;
}
