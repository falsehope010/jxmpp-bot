package syslog.rotate;

import java.util.Date;

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
     * Gets latest date when log rotation was performed
     * 
     * @return Latest date of log rotation. If rotation was never performed
     *         returns null
     */
    public Date getLastRotateDate() {
	return lastRunDate;
    }

    /**
     * Gets value indicating how many times log rotation was performed
     * 
     * @return Total number of executions
     */
    public int getRotationsCount() {
	return rotationsCounter;
    }

    public abstract boolean rotate();

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
     * Sets latest date when log rotation was performed
     * 
     * @param latestDate
     *            Log rotation latest date
     */
    protected void setLastRotateDate(Date latestDate) {
	lastRunDate = latestDate;
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
    Date lastRunDate;
    int rotationsCounter;
    SyslogMessageMapper messageMapper;
}
