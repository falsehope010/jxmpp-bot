package exceptions;

/**
 * Thrown on service initialization error.
 * 
 * @author tillias
 * 
 */
public class ServiceInitializationException extends Exception {

    public ServiceInitializationException(String message, Throwable clause) {
	super(message, clause);
    }

    /**
     * 
     */
    private static final long serialVersionUID = 2830996372746531425L;

}
