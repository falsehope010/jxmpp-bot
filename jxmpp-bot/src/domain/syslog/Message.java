package domain.syslog;

import java.util.Date;

import domain.DomainObject;
import exceptions.InvalidSyslogSessionException;

/**
 * Represents syslog message. Stores text description and additional information about any system event.
 * You must not create messages directly. Use Syslog instead.
 * @author tillias_work
 *
 */
public class Message extends DomainObject {

	
	/**
	 * Creates new syslog message. You mustn't create message directly. Use Syslog.put() method 
	 * instead. Syslog also will take care about setting correct session for message
	 * @param messageText Message text
	 * @param category Message category. Can't be null or empty string
	 * @param messageType Message type. Can't be null or empty string
	 * @param sender Message sender. Can't be null or empty string
	 * @param session Message session. Must be valid persistent session. 
	 * @throws NullPointerException If any parameters passed to constructor are null-reference 
	 * @throws IllegalArgumentException If any text-parameters passed to constructor are empty strings
	 * @throws InvalidSyslogSessionException If SyslogSession is null or non-persistent
	 */
	public Message(String messageText, String category, String messageType,
			String sender, SyslogSession session) throws NullPointerException,
			IllegalArgumentException, InvalidSyslogSessionException {

		//check all arguments first
		checkParamNotNull(messageText);
		checkParamNotNull(category);
		checkParamNotNull(messageType);
		checkParamNotNull(sender);
		checkSession(session);
		
		//fill data fields
		timestamp = new Date();
		this.text = messageText;
		this.session = session;
		this.category = category;
		this.type = messageType;
		this.sender = sender;
		
	}
	
	/**
	 * Gets message timestamp (e.g. time when it was created)
	 * @return Message timestamp
	 */
	public Date getTimestamp(){
		return timestamp;
	}
	
	/**
	 * Gets message text
	 * @return
	 */
	public String getText(){
		return text;
	}
	
	/**
	 * Gets message category
	 * @return
	 */
	public String getCategory(){
		return category;
	}
	
	/**
	 * Gets message type
	 * @return
	 */
	public String getType(){
		return type;
	}
	
	/**
	 * Gets message sender
	 * @return
	 */
	public String getSender(){
		return sender;
	}
	
	/**
	 * Gets syslog session to which message belongs
	 * @return
	 */
	public SyslogSession getSession(){
		return session;
	}
	
	private void checkParamNotNull(String str) throws NullPointerException,
			IllegalArgumentException {
		if (str == null)
			throw new NullPointerException("Argument is null-reference.");

		if (str.length() == 0)
			throw new IllegalArgumentException("Argument is empty string.");
	}
	
	private void checkSession(SyslogSession session) throws InvalidSyslogSessionException{
		if (session == null )
			throw new InvalidSyslogSessionException();
		if (!session.isPersistent())
			throw new InvalidSyslogSessionException();
	}

	Date timestamp;
	String text;
	SyslogSession session;
	String category;
	String type;
	String sender;
}
