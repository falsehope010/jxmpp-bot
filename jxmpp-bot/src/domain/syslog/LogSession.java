package domain.syslog;

import java.util.Date;
import domain.DomainObject;

public class LogSession extends DomainObject {
	Date start;
	Date end;
	
	public LogSession(Date startDate, Date endDate){
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
