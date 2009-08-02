package muc.services;

import muc.Repository;

/**
 * Represents base class for all muc services.
 * 
 * @author tillias
 * 
 */
public class AbstractService {

    /**
     * Sole constructor. For invocation by subclass constructors.
     * 
     * @param repository
     * @throws NullPointerException
     *             Thrown if repository parameter is null reference
     */
    protected AbstractService(Repository repository)
	    throws NullPointerException {
	if (repository == null)
	    throw new NullPointerException("Repository can't be null");

	this.repository = repository;
    }

    protected Repository repository;
}
