package exceptions;

import domain.syslog.Message;

/**
 * Thrown by {@link Message} during it's construction to indicate that session is null or non-persistent
 * @author tillias_work
 */
public class InvalidSyslogSessionException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4405032403871107056L;

	public InvalidSyslogSessionException(){
		
	}
}
