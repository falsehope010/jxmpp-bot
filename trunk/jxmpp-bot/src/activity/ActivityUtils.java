package activity;

/**
 * Provides util methods for managing {@link IActive} concrete implementations
 * 
 * @author tillias
 * 
 */
public class ActivityUtils {

    /**
     * Checks whether given object implements {@link IActive} interface and if
     * so stops it by calling {@link IActive#stop()}
     * 
     * @param o
     *            Object to be stopped. If null pointer is passed does nothing
     */
    public void stop(Object o) {
	if (o instanceof IActive) {
	    IActive activeObject = (IActive) o;

	    if (activeObject.isAlive())
		activeObject.stop();
	}
    }
}
