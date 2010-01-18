package activity;

/**
 * Represents active class which provides methods of starting and stopping some
 * activity. All concrete implementations of this interface are <b>active
 * objects</b>.
 * <p>
 * This interface extends {@link Runnable} one
 * 
 * @author tillias
 * 
 */
public interface IActive extends Runnable {

    /**
     * Starts activity
     */
    void start();

    /**
     * Stops activity
     */
    void stop();

    /**
     * Gets value indicating that activity is currently running
     * 
     * @return True if activity is currently running false otherwise
     */
    boolean isAlive();
}
