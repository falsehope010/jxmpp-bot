package exceptions;

/**
 * Thrown by any database mapper if client attempts to
 * create new instances of mapper without initializing them first
 * using static initialize() method
 * @author tillias_work
 *
 */
public class MapperNotInitializedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7697464486477360558L;

}
