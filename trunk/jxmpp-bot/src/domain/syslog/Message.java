package domain.syslog;

import java.util.Date;

import syslog.SysLog;
import domain.DomainObject;

/**
 * Represents syslog message. Stores text description and additional information
 * about any system event. You must not create messages directly. Use Syslog
 * instead.
 * 
 * @see SysLog#putMessage(String, String, String, String)
 * @author tillias_work
 * 
 */
public class Message extends DomainObject {

    /**
     * Creates new syslog message. You mustn't create message directly. Use
     * Syslog.put() method instead. Syslog also will take care about setting
     * correct session for message
     * 
     * @param messageText
     *            Message text
     * @param category
     *            Message category. Can't be null or empty string
     * @param messageType
     *            Message type. Can't be null or empty string
     * @param sender
     *            Message sender. Can't be null or empty string
     * @param session
     *            Message session. Must be valid persistent session.
     * @throws NullPointerException
     *             If any parameter passed to constructor is null-reference
     * @throws IllegalArgumentException
     *             If any text-parameters passed to constructor are empty
     *             strings
     * @throws InvalidSyslogSessionException
     *             If SyslogSession is null or non-persistent
     */
    public Message(String messageText, String category, String messageType,
	    String sender, SyslogSession session) throws NullPointerException,
	    IllegalArgumentException {

	// check all arguments first
	checkParamNotNull(messageText);
	checkParamNotNull(category);
	checkParamNotNull(messageType);
	checkParamNotNull(sender);
	checkSession(session);

	// fill data fields
	timestamp = new Date();
	this.text = messageText;
	this.session = session;
	this.category = new MessageCategory(category);
	this.type = new MessageType(messageType);
	this.sender = new MessageSender(sender);
    }

    /**
     * Gets message timestamp (e.g. time when it was created)
     * 
     * @return Message timestamp
     */
    public Date getTimestamp() {
	return timestamp;
    }

    /**
     * Gets message text
     * 
     * @return
     */
    public String getText() {
	return text;
    }

    /**
     * Gets message category name
     * 
     * @return
     */
    public String getCategoryName() {
	return category.getName();
    }

    /**
     * Gets message type name
     * 
     * @return
     */
    public String getMessageTypeName() {
	return type.getName();
    }

    /**
     * Gets message sender name
     * 
     * @return
     */
    public String getSenderName() {
	return sender.getName();
    }

    /**
     * Gets message category
     * 
     * @return
     */
    public MessageCategory getCategory() {
	return category;
    }

    /**
     * Gets message type
     * 
     * @return
     */
    public MessageType getMessageType() {
	return type;
    }

    /**
     * Gets message sender
     * 
     * @return
     */
    public MessageSender getSender() {
	return sender;
    }

    /**
     * Gets syslog session to which message belongs
     * 
     * @return
     */
    public SyslogSession getSession() {
	return session;
    }

    /**
     * You mustn't use this method directly. For internal mapping domain object
     * from/to db
     * 
     * @param timestamp
     *            Message timestamp
     */
    public void mapperSetTimestamp(Date timestamp) {
	if (timestamp != null) {
	    this.timestamp = timestamp;
	}
    }

    /**
     * You mustn't use this method directly. For internal mapping domain object
     * from/to db
     * 
     * @param c
     *            Message category
     */
    public void mapperSetCategory(MessageCategory c) {
	if (c != null && c.isPersistent()) {
	    category = c;
	}
    }

    /**
     * You mustn't use this method directly. For internal mapping domain object
     * from/to db
     * 
     * @param t
     *            Message type
     */
    public void mapperSetType(MessageType t) {
	if (t != null && t.isPersistent()) {
	    type = t;
	}
    }

    /**
     * You mustn't use this method directly. For internal mapping domain object
     * from/to db
     * 
     * @param s
     *            Message sender
     */
    public void mapperSetSender(MessageSender s) {
	if (s != null && s.isPersistent()) {
	    sender = s;
	}
    }

    /**
     * You mustn't use this method directly. For internal mapping domain object
     * from/to db
     * 
     * @param session
     *            Message's syslog session
     */
    public void mapperSetSession(SyslogSession session) {
	if (session != null && session.isPersistent()) {
	    this.session = session;
	}
    }

    private void checkParamNotNull(String str) throws NullPointerException,
	    IllegalArgumentException {
	if (str == null)
	    throw new NullPointerException("Argument is null-reference.");

	if (str.length() == 0)
	    throw new IllegalArgumentException("Argument is empty string.");
    }

    private void checkSession(SyslogSession session)
	    throws NullPointerException {
	if (session == null)
	    throw new NullPointerException();
    }

    Date timestamp;
    String text;
    SyslogSession session;
    MessageCategory category;
    MessageType type;
    MessageSender sender;
}
