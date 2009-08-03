package muc;

import java.util.ArrayList;
import java.util.List;

import mappers.UserPermissionsMapper;
import database.Database;
import domain.muc.Room;
import domain.muc.User;
import domain.muc.UserPermissions;
import exceptions.RepositoryInitializationException;

/**
 * Represents repository which is used for OR-mapping of several muc tables
 * <p>
 * {@link UserPermissionsMapper} uses repository for mapping users and rooms to
 * user permissions objects
 * 
 * @author tillias
 * 
 */
public class Repository {

    /**
     * Creates new instance of repository and initializes it.
     * 
     * @param db
     *            Database instance which will be used by repository for
     *            OR-mapping
     * @throws RepositoryInitializationException
     *             Thrown if any error has occurred during repository's
     *             initialization
     * @throws IllegalArgumentException
     *             Thrown if database passed to constructor is null or isn't in
     *             connected state
     */
    public Repository(Database db) throws RepositoryInitializationException,
	    IllegalArgumentException {
	if (db == null)
	    throw new IllegalArgumentException("Database can't be null");
	if (!db.isConnected())
	    throw new IllegalArgumentException(
		    "Database isn't in connected state");
	this.db = db;

	if (!initRepository())
	    throw new RepositoryInitializationException(
		    "Can't initialize repository");
    }

    public List<UserPermissions> getUserPermissions() {
	return new ArrayList<UserPermissions>();

	// TODO:
    }

    // TODO:
    /**
     * Retrieves {@link User} from repository using it's ID
     * 
     * @param id
     *            User identifier
     * @return Valid, persistent User if such a user is stored in repository,
     *         null reference otherwise
     */
    public User getUser(long id) {
	return null;
    }

    // TODO:
    /**
     * Retrieves {@link Room} from repository using it's ID
     * 
     * @param id
     *            Room identifier
     * @return Valid, persistent Room if such a room is stored in repository,,
     *         null reference otherwise
     */
    public Room getRoom(long id) {
	return null;
    }

    private boolean initRepository() {
	return loadUsers() && loadRooms();
    }

    private boolean loadUsers() {
	return false;
    }

    private boolean loadRooms() {
	return false;
    }

    private Database db;
}
