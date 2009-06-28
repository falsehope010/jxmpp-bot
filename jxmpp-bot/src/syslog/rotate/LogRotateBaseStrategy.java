package syslog.rotate;

import java.util.Date;

/**
 * Base class for all system logs rotation strategies.
 * @author tillias_work
 *
 */
public class LogRotateBaseStrategy {
	
	/**
	 * Gets latest date when log rotation procedure was performed
	 * @return Latest date if log rotation was performed at least once, null reference otherwise
	 */
	public Date getLastRunDate(){
		return lastRunDate;
	}
	
	/**
	 * Gets value indicating how many times log rotation strategy was executed.
	 * @return Total number of executions
	 */
	public int getExecutionsCount(){
		return executionsCount;
	}
	
	Date lastRunDate;
	int executionsCount;
}
