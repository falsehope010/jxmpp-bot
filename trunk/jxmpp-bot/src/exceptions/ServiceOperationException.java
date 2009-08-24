package exceptions;

/**
 * Thrown on any service operation exception
 * 
 * @author tillias
 * 
 */
public class ServiceOperationException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -6097898762558850462L;

    public ServiceOperationException(String message, Throwable cause) {
	super(message, cause);
    }

}
