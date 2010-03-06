package activity;

/**
 * Provides utility methods for managing {@link IActive} concrete
 * implementations
 * 
 * @author tillias
 * 
 */
public class ActivityUtils {

    /**
     * Checks that given object implements {@link IActive} interface and if so
     * stops it by calling {@link IActive#stop()}
     * 
     * @param o
     *            Object to be stopped. If null pointer is passed does nothing
     */
    public static void stop(Object o) {
	if (o instanceof IActive) {
	    IActive activeObject = (IActive) o;

	    if (activeObject.isAlive())
		activeObject.stop();
	}
    }

    /**
     * Checks whether given object implements {@link IActive} interface and if
     * so starts in by calling {@link IActive#start()}
     * 
     * @param o
     *            Object to be started. If null pointer is passed does nothing
     */
    public static void start(Object o) {
	if (o instanceof IActive) {
	    IActive activeObject = (IActive) o;

	    if (!activeObject.isAlive())
		activeObject.start();
	}
    }
}
