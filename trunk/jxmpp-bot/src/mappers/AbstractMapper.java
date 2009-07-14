package mappers;

import database.Database;
import domain.DomainObject;
import exceptions.DatabaseNotConnectedException;

/**
 * Base class for all database mappers.
 * 
 * @author tillias_work
 * 
 */
public abstract class AbstractMapper {

	/**
	 * Sole constructor. For invocation by subclass constructors.
	 * 
	 * @param db
	 *            Database which will be used by mapper
	 * @throws DatabaseNotConnectedException
	 *             Thrown if database passed to cunstructor is in disconnected
	 *             state
	 * @throws NullPointerException
	 *             Thrown if database passed to constructor is null reference
	 */
	protected AbstractMapper(Database db) throws DatabaseNotConnectedException,
			NullPointerException {
		if (db == null)
			throw new NullPointerException(
					"Database passed to mapper is null-reference");
		if (!db.isConnected())
			throw new DatabaseNotConnectedException();

		this.db = db;
	}

	/**
	 * Performs saving of domain object into database. Domain object can be
	 * either persistent or not. If operation is succeeded, method must mark
	 * domain object as persistent.
	 * 
	 * @param obj
	 *            Domain object to be saved. Can be either persistent, or not
	 * @return true if succeeded, false otherwise
	 */
	public abstract boolean save(DomainObject obj);

	/**
	 * Performs deletion of domain object from database. Domain object must be
	 * persistent. If operation is succeded, domain object must be marked as
	 * non-persistent
	 * 
	 * @param obj
	 *            Domain object to be deleted from database. Must be persistent.
	 * @return true if succeded, false otherwise
	 */
	public abstract boolean delete(DomainObject obj);

	/**
	 * Gets value indicating whether auto-commit mode is enable for mapper's
	 * database
	 * 
	 * @return True if succeded, false otherwise
	 * @see Database#getAutoCommit()
	 */
	protected boolean getAutoCommitState() {
		boolean result = false;

		try {
			if (db != null && db.isConnected()) {
				result = db.getAutoCommit();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	protected Database db;
}
