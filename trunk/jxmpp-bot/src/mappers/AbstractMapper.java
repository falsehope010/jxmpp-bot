package mappers;

import database.Database;
import domain.DomainObject;

public abstract class AbstractMapper {
	
	/**
	 * Initializes mapper using given database object. When overridden, don't forget to call 
	 * super class method to perform database validation and assignment. Database will be shared
	 * among all mappers due to jxmpp-bot architecture (sqlite, local database)
	 * @param db Database which will be used by mapper to perform its actions
	 * @return true if database is valid and connected, false otherwise
	 */
	public boolean Initialize(Database db) {
		boolean result = false;

		if (AbstractMapper.db != null) {
			result = true;
		} else {
			if (db != null && db.isConnected()) {
				try {
					AbstractMapper.db = db;
				} catch (Exception e) {
				}
			}

		}

		return result;
	}
	
	/**
	 * Performs saving of domain object into database. Domain object can be either persistent or not.
	 * If operation is succeeded, method must mark domain object as persistent.
	 * @param obj Domain object to be saved. Can be either persistent, or not
	 * @return true if succeeded, false otherwise
	 */
	public abstract boolean Save(DomainObject obj);
	
	/**
	 * Performs deletion of domain object from database. Domain object must be persistent.
	 * If operation is succeded, domain object must be marked as non-persistent
	 * @param obj Domain object to be deleted from database. Must be persistent. 
	 * @return true if succeded, false otherwise
	 */
	public abstract boolean Delete(DomainObject obj);
	
	protected static Database db;
}
