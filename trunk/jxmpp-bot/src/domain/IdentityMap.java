package domain;

import java.util.HashMap;

/**
 * Implements IdentityMap pattern. Stores mappings between {@link DomainObject}
 * and their ID
 * 
 * @author tillias
 * 
 * @param <T>
 *            The type of elements maintained by this IdentityMap. Can be any
 *            subclass of {@link DomainObject} class
 */
public class IdentityMap<T extends DomainObject> {

    public IdentityMap() {
	body = new HashMap<Long, T>();
    }

    /**
     * Adds new domain object into IdentityMap
     * 
     * @param object
     *            Domain object to be added into identity map. Must be
     *            persistent. If null pointer or non-persistent domain object is
     *            passed does nothing
     */
    public void add(T object) {
	if (object != null && object.isPersistent()) {
	    body.put(object.getID(), object);
	}
    }

    /**
     * Retrieves domain object from IdentityMap using domain object's identifier
     * 
     * @param ID
     *            Domain object's identifier
     * @return Valid domain object if it is stored in IdentityMap, null pointer
     *         otherwise
     */
    public T get(long ID) {
	return body.get(ID);
    }

    HashMap<Long, T> body;
}
