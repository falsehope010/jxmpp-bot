package syslog;

/**
 * Represents a logging facility that can store text messages inside some
 * storage for a continuous time
 * 
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
