package syslog;

import java.util.Date;

import domain.syslog.Message;
import domain.syslog.SyslogSession;

/**
 * @author tillias_work
 *
 */
public class SysLog {
	SyslogSession currentSession;
	
	public SysLog(){
		
	}
	
	public boolean startSession(){
		closeSession(getCurrentSession());
		
		currentSession = createSession();
		
		return false;
	}
	
	public void endSession(){
		//TODO: get current session, set it's end date and map into db
	}
	
	public Message SaveMessage(String text, String sender, String category, String type){
		//TODO: don't forget to set sessionID using getCurrentSession()
		
		
		return null;
	}
	
	/**
	 * Gets current syslog session
	 * @return
	 */
	public SyslogSession getCurrentSession(){
		return currentSession;
	}
	
	private SyslogSession createSession(){
		
		//TODO: create new session and map it into db
		return null;
	}
	
	private void closeSession(SyslogSession session){
		if (session != null){
			//session.close();
			
			//TODO: save session into db using mapper
		}
	}
}
