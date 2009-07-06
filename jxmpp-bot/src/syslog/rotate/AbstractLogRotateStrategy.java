package syslog.rotate;

import java.util.Date;

import database.Database;

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
     * 
     * @param db
     *            Database instance which will be used by strategy
     */
    protected AbstractLogRotateStrategy(Database db) {
	this.db = db;
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

    Database db;
    Date lastRunDate;
    int rotationsCounter;
}
