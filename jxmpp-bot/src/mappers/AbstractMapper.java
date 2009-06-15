package mappers;

import database.Database;
import domain.DomainObject;
import exceptions.DatabaseNotConnectedException;

/**
 * Base class for all database mappers. Default behavior is to initialize all needed
 * @author tillias_work
 *
 */
public abstract class AbstractMapper {

	/**
	 * Creates new ins
	 * @param db
	 * @throws DatabaseNotConnectedException
	 * @throws NullPointerException
	 */
	protected AbstractMapper(Database db) throws DatabaseNotConnectedException, NullPointerException{
		if (db == null)
			throw new NullPointerException();
		if (!db.isConnected())
			throw new DatabaseNotConnectedException(); 
		
		this.db = db;
	}
	/**
	 * Performs saving of domain object into database. Domain object can be either persistent or not.
	 * If operation is succeeded, method must mark domain object as persistent.
	 * @param obj Domain object to be saved. Can be either persistent, or not
	 * @return true if succeeded, false otherwise
	 */
	public abstract boolean save(DomainObject obj);
	
	/**
	 * Performs deletion of domain object from database. Domain object must be persistent.
	 * If operation is succeded, domain object must be marked as non-persistent
	 * @param obj Domain object to be deleted from database. Must be persistent. 
	 * @return true if succeded, false otherwise
	 */
	public abstract boolean delete(DomainObject obj);
	
	protected Database db;
}
