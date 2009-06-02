package domain.syslog;

import java.util.Date;
import domain.DomainObject;

/**
 * Represents Syslog session object. Stores session id, session start and end date.
 * Session is constructed and stored by Syslog instance. Normally when application
 * starts it creates Syslog instance and calls Syslog.startSession() to create log session.
 * When application is about to close, it calls Syslog.endSession()
 * @see syslog.SysLog#startSession()
 * @see syslog.SysLog#endSession()
 * @author tillias_work
 *
 */
public class SyslogSession extends DomainObject {
	Date start;
	Date end;
	
	public SyslogSession(Date startDate, Date endDate){
		this.start = startDate;
		this.end = endDate;
	}
	
	public Date getStartDate(){
		return start;
	}
	
	public void setStartDate(Date startDate){
		this.start = startDate;
		
		mapperSetPersistence(false);
	}
	
	public Date getEndDate(){
		return end;
	}
	
	public void setEndDate(Date endDate){
		this.end = endDate;
		
		mapperSetPersistence(false);
	}
}
