package domain.syslog;

import java.util.Date;
import domain.DomainObject;

/**
 * Represents Syslog session object. Stores session id, session start and end date.
 * Session is constructed and stored by Syslog instance. Normally when application
 * starts it creates Syslog instance and calls Syslog.startSession() to create log session.
 * When application is about to close, it calls Syslog.endSession()
 * @see syslog.SysLog#startNewSession()
 * @see syslog.SysLog#endSession()
 * @author tillias_work
 *
 */
public class SyslogSession extends DomainObject {

	
	/**
	 * Creates new instance and sets it's start date as the date when instance was created.
	 * SyslogSession becomes opened (e.g. it has no end date). You can close session 
	 * using close() method
	 */
	public SyslogSession(){
		start = new Date();
	}
	
	/**
	 * Gets session start date (e.g. date when it was created)
	 * @return Session start date. Return value can't be null
	 */
	public Date getStartDate(){
		return start;
	}
	
	/**
	 * Gets session end date (e.g. date when it was closed).
	 * @return Session end date (if it was closed ) or null-reference if it was never closed
	 */
	public Date getEndDate(){
		return end;
	}
	
	/**
	 * Gets value indicating whether session was closed (e.g. it has valid end date)
	 * @return True if session is closed, false otherwise
	 */
	public boolean isClosed(){
		return closed;
	}
	
	/**
	 * Closes session, sets it's end date with the date of closing and marks
	 * session as closed
	 */
	public void close(){
		end = new Date();
		setClosed(true);
	}
	
	/**
	 * You mustn't use this method directly. For internal mapping domain object from/to db
	 * @param startDate Start date of session
	 */
	public void mapperSetStartDate(Date startDate){
		this.start = startDate;
	}
	
	/**
	 * You mustn't use this method directly. For internal mapping domain object from/to db
	 * @param endDate End date of session
	 */
	public void mapperSetEndDate(Date endDate){
		this.end = endDate;
	}
	
	protected void setClosed(boolean value){
		closed = value;
	}
	
	Date start;
	Date end;
	boolean closed;
}
