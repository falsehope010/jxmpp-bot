package mappers;

import domain.DomainObject;

/**
 * Base class for all database mappers. Default behavior is to initialize all needed
 * @author tillias_work
 *
 */
public abstract class AbstractMapper {

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
}
