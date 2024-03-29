package exceptions;

/**
 * Thrown if client attempts to use database in disconnected state. E.g. to
 * create any database mapper object using database in disconnected state
 * 
 * @author tillias
 * 
 */
public class DatabaseNotConnectedException extends RuntimeException {

    /**
	 * 
	 */
    private static final long serialVersionUID = -13775494908920874L;

}
