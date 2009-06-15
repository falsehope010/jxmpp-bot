package syslog;

import java.util.concurrent.ConcurrentLinkedQueue;

import domain.syslog.Message;
import domain.syslog.SyslogSession;
import exceptions.SessionNotStartedException;

/**
 * @author tillias_work
 *
 */
public class SysLog {
	
	
	public SysLog(){
		internalQueue = new ConcurrentLinkedQueue<Message>();
	}
	
	public boolean startSession(){
		closeSession(getCurrentSession());
		
		currentSession = createSession();
		
		return false;
	}
	
	public void endSession(){
		//TODO: get current session, set it's end date and map into db
	}
	
	/**
	 * Puts text message with given attributes (sender,category and type) into system log
	 * @param text Message text
	 * @param sender Sender of message
	 * @param category Message Category
	 * @param type Message Type
	 * @return
	 * @throws SessionNotStartedException
	 */
	public Message putMessage(String text, String sender, String category, String type) throws SessionNotStartedException{
		Message result = null;
		SyslogSession currentSession = getCurrentSession();
		
		if (currentSession == null){
			throw new SessionNotStartedException(); // session must be started
		}else{
			result = new Message(text,category,type,sender,currentSession);
			enqueueMessage(result); // put into internal queue
		}
		
		return result;
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
	
	private void enqueueMessage(Message msg){
		if (msg != null){
			internalQueue.add(msg);
		}
	}
	
	SyslogSession currentSession;
	
	/**
	 * Uses as memory cache for syslog messages. Syslog itself runs separate thread which in
	 * specified intervals dequeues all messages from internal queue and maps them into database
	 */
	ConcurrentLinkedQueue<Message> internalQueue;
}
