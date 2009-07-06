package syslog.rotate;

/**
 * Syslog rotate strategy. Implements method of cleaning old system logs
 * (messages)
 * 
 * @author tillias
 * 
 */
public interface ILogRotateStrategy {
    /**
     * Performs log rotation.
     * 
     * @return True if succeded, false otherwise
     */
    public boolean rotate();
}
