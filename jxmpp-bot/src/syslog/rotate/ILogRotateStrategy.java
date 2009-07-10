package syslog.rotate;

import java.util.Date;

/**
 * Syslog rotate strategy. Implements method of cleaning old system logs.
 * <p>
 * Additionally provides getter for rotation date (the date when rotation should
 * be actually performed).
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

	/**
	 * Calculates next log rotation date and remembers it, so
	 * {@link #getRotationDate()} will be returning this updated value
	 * 
	 * @see #getRotationDate()
	 */
	public void updateRotationDate();

	/**
	 * Gets log rotation date when operation should be performed
	 * 
	 * @return Current log rotation date (if calculated), null reference
	 *         otherwise
	 * @see #calculateNextRotationDate()
	 */
	public Date getRotationDate();
}
