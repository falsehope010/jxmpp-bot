package syslog;

/**
 * Represents a logging utility which can store text messages inside database,
 * file
 * 
 * @author tillias
 * 
 */
public interface ILog {
    /**
     * Puts text message with given attributes (sender,category and type) into
     * system log.
     * 
     * @param text
     *            Message text
     * @param sender
     *            Message sender
     * @param category
     *            Message category
     * @param type
     *            Message type
     * @return true if succeded false otherwise
     */
    public boolean putMessage(String text, String sender, String category,
	    String type);
}
