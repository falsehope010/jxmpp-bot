package muc;

import java.util.ArrayList;
import java.util.List;

import mappers.RoomMapper;
import mappers.UserMapper;
import mappers.UserPermissionsMapper;
import muc.services.IdentityMap;
import database.Database;
import domain.internal.UserPermissionsEntity;
import domain.muc.Room;
import domain.muc.User;
import domain.muc.UserPermissions;
import exceptions.RepositoryInitializationException;

/**
 * Represents repository which is used for OR-mapping of several muc tables
 * <p>
 * Repository is designed to be used as single instance in application. It
 * creates multiple identity maps during construction (e.g. users identity map,
 * rooms identity map). It also provides several high-level methods of creating
 * / updating users and rooms and manages all internal processing
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

	verifyDatabase(db);

	initMappers(db);

	this.db = db;

	usersMap = loadUsers(db);
	roomsMap = loadRooms(db);
    }

    /**
     * Retrieves {@link User} from repository using it's ID
     * 
     * @param ID
     *            User identifier
     * @return Valid, persistent User if such a user is stored in repository,
     *         null reference otherwise
     */
    public User getUser(long ID) {
	return usersMap.get(ID);
    }

    /**
     * Retrieves {@link Room} from repository using it's ID
     * 
     * @param ID
     *            Room identifier
     * @return Valid, persistent Room if such a room is stored in repository,,
     *         null reference otherwise
     */
    public Room getRoom(long ID) {
	return roomsMap.get(ID);
    }

    /**
     * Performs OR-mapping of users, rooms and permissions database tables and
     * returns all constructed {@link UserPermissions} objects.
     * 
     * @return All {@link UserPermissions} objects from database
     * @see UserPermissions
     * @see UserPermissionsMapper
     */
    public List<UserPermissions> getUserPermissions() {
	ArrayList<UserPermissions> result = new ArrayList<UserPermissions>();

	try {
	    List<UserPermissionsEntity> entites = permissionsMapper
		    .repositoryGetUserPermissions();

	    if (entites != null && entites.size() > 0) {
		for (UserPermissionsEntity entity : entites) {
		    long userID = entity.getUserID();
		    long roomID = entity.getRoomID();

		    User user = usersMap.get(userID);
		    Room room = roomsMap.get(roomID);

		    UserPermissions permissions = new UserPermissions(user,
			    room, entity.getJabberID());
		    permissions.setAccessLevel(entity.getAccessLevel());

		    permissions.mapperSetID(entity.getID());
		    permissions.mapperSetPersistence(true);

		    result.add(permissions);
		}
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	}

	return result;
    }

    /**
     * Verifies whether given database isn't null reference and is in connected
     * state
     * 
     * @param database
     *            Database to be verified
     * @throws IllegalArgumentException
     *             Thrown if database verification is failed
     */
    private void verifyDatabase(Database database)
	    throws IllegalArgumentException {
	if (database == null)
	    throw new IllegalArgumentException("Database can't be null");
	if (!database.isConnected())
	    throw new IllegalArgumentException(
		    "Database isn't in connected state");
    }

    /**
     * Loads all users from database and creates {@link IdentityMap}
     * 
     * @param database
     *            Database which stores users
     * @return Identity map of users
     * @throws RepositoryInitializationException
     *             Thrown if any error occurred during building of IdentityMap.
     *             See exeception message for details.
     * @see {@link User}
     */
    private IdentityMap<User> loadUsers(Database database)
	    throws RepositoryInitializationException {
	IdentityMap<User> result = new IdentityMap<User>();

	try {
	    List<User> users = userMapper.getUsers();

	    for (User user : users) {
		result.add(user);
	    }
	} catch (Exception e) {
	    throw new RepositoryInitializationException(
		    "Can't init users identity map. " + e.getMessage());
	}

	return result;
    }

    /**
     * Loads all rooms from database and creates {@link IdentityMap}
     * 
     * @param database
     *            Database which stores rooms
     * @return Identity map of rooms
     * @throws RepositoryInitializationException
     * @see {@link Room}
     */
    private IdentityMap<Room> loadRooms(Database database)
	    throws RepositoryInitializationException {
	IdentityMap<Room> result = new IdentityMap<Room>();

	try {
	    List<Room> rooms = roomMapper.getRooms();

	    for (Room room : rooms) {
		result.add(room);
	    }

	} catch (Exception e) {
	    throw new RepositoryInitializationException(
		    "Can't init rooms identity map");
	}

	return result;
    }

    /**
     * Initializes all internal database mappers using given database
     * 
     * @param database
     *            Must be valid {@link Database} in connected state
     * @throws RepositoryInitializationException
     *             Thrown if database not connected or error on mappers
     *             initialization (construction has occured)
     */
    private void initMappers(Database database)
	    throws RepositoryInitializationException {
	try {
	    permissionsMapper = new UserPermissionsMapper(database);
	    userMapper = new UserMapper(database);
	    roomMapper = new RoomMapper(database);
	} catch (Exception e) {
	    throw new RepositoryInitializationException(
		    "Can't initialize mapper(s)");
	}
    }

    UserPermissionsMapper permissionsMapper;
    UserMapper userMapper;
    RoomMapper roomMapper;

    Database db;
    IdentityMap<User> usersMap;
    IdentityMap<Room> roomsMap;
}
