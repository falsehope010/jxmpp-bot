package domain;

public class DomainObject {
	
	long id;
	boolean isPersistent;
	
	/**
	 * Gets domain object id as it is stored in database.
	 * @return value greater then zero if domain object is persistent, 0 otherwise
	 */
	public long getID(){
		return id;
	}
	
	/**
	 * Sets domain object id. Normally you mustn't change id manually. 
	 * Use Database class to insert domain object in database in order to make it persistent
	 * and assign valid id.
	 * @param ID New domain object id
	 */
	public void setID(long ID){
		this.id = ID;
	}

	/**
	 * Sets persistence of domain object. Normally you must not use this method.
	 * Call Database class methods to obtain domain objects from database, insert, update or delete
	 * @param value
	 */
	public void setPersistence(boolean value) {
		isPersistent = value;
	}
	
	/**
	 * Gets value indicating that domain object persists in database (e.g. is persistent)
	 * If object is persistent, then it has valid ID, greater then zero
	 * @return True if domain object is persistent, false otherwise
	 * false otherwise
	 */
	public boolean isPersistent() {
		return isPersistent;
	}
}
