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
	 * Gets value indicating that domain object persists in database (e.g. is persistent)
	 * If object is persistent, then it has valid ID, greater then zero
	 * @return True if domain object is persistent, false otherwise
	 * false otherwise
	 */
	public boolean isPersistent() {
		return isPersistent;
	}
	
	/**
	 * Sets domain object id. You must not change id directly,
	 * use instead corresponding database mapper to insert, update or delete domain object.
	 * @param ID New domain object's id
	 */
	public void mapperSetID(long ID){
		this.id = ID;
	}

	/**
	 * Sets persistence of domain object. You must not use this method directly,
	 * use instead corresponding database mapper to insert, update or delete domain object.
	 * @param value New domain object's persistence value
	 */
	public void mapperSetPersistence(boolean value) {
		isPersistent = value;
	}
}
