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

	buildIndex();

	this.db = db;
    }

    /**
     * Performs OR-mapping of users, rooms and permissions database tables and
     * returns all constructed {@link UserPermissions} objects.
     * <p>
     * Items guaranteed to be persistent domain objects
     * 
     * @return All {@link UserPermissions} objects from database
     * @see UserPermissions
     * @see UserPermissionsMapper
     */
    public List<UserPermissions> getUserPermissions() {
	ArrayList<UserPermissions> result = new ArrayList<UserPermissions>();

	try {
	    IdentityMap<User> usersMap = loadUsers(db);
	    IdentityMap<Room> roomsMap = loadRooms(db);

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
     * Creates new {@link UserPermissions} instance and maps it into database.
     * Automatically creates new {@link User} and {@link Room} if needed
     * <p>
     * Note: if called multiple times creates and maps {@link UserPermissions}
     * into database also multiple times. So uniqueness should be controlled by
     * method caller
     * 
     * @param jabberID
     *            User's jabberID.
     * @param roomName
     *            Multichat room's name
     * @param accessLevel
     *            Access level for given user in given room
     * @return Valid persistent {@link UserPermissions} instance if succeed.
     * @throws Exception
     *             Thrown on any error while creating and mapping
     *             {@link UserPermissions} instance. See message for details
     */
    public UserPermissions createUserPermissions(String jabberID,
	    String roomName, int accessLevel) throws Exception {
	UserPermissions result = null;

	try {

	    // verify whether user exists and if not create it
	    User user = index.getUser(jabberID);
	    if (user == null) {
		user = createUser();
	    }

	    // verify whether room exists and if not create it
	    Room room = index.getRoom(roomName);
	    if (room == null) {
		room = createRoom(roomName);
	    }

	    result = new UserPermissions(user, room, jabberID, accessLevel);
	    if (!permissionsMapper.save(result)) {
		throw new Exception("Can't save permissions to database");
	    }

	    // add to index
	    index.putUser(jabberID, user);
	    index.putRoom(roomName, room);

	} catch (Exception e) {
	    Exception ex = new Exception(
		    "Error creating permissions. See cause", e);
	    throw ex;
	}

	return result;
    }

    /**
     * Updates access level for given {@link UserPermissions} in database.
     * Parameter passed to this method must be valid persistent domain object.
     * If it isn't persistent does nothing
     * 
     * @param permissions
     *            User permissions to be updated. Must be valid persistent
     *            domain object
     * @return True if succeeded, false otherwise
     */
    public boolean updateAccessLevel(UserPermissions permissions,
	    int newAccessLevel) {
	boolean result = false;

	if (permissions != null && permissions.isPersistent()) {
	    result = permissionsMapper.updateAccessLevel(permissions);
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
     * Builds internal Repository's index
     * 
     * @throws RepositoryInitializationException
     *             Thrown on any error occured during index build
     */
    private void buildIndex() throws RepositoryInitializationException {
	try {
	    List<UserPermissions> permissions = getUserPermissions();
	    index = new RepositoryIndex(permissions);
	} catch (Exception e) {
	    throw new RepositoryInitializationException(
		    "Can't build repository index");
	}
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

    /**
     * Creates new {@link User} and maps it into database
     * 
     * @return Valid persistent domain object (User)
     * @throws Exception
     *             Thrown if user can't be mapped into database
     */
    private User createUser() throws Exception {
	User result = new User();

	if (!userMapper.save(result)) {
	    throw new Exception("Can't save user to database");
	}

	return result;
    }

    /**
     * Creates new {@link Room} and maps it into database
     * 
     * @param roomName
     *            Room name
     * @return Valid persistent domain object (Room)
     * @throws Exception
     *             Thrown if room can't be mapped into database
     */
    private Room createRoom(String roomName) throws Exception {
	Room result = new Room(roomName);

	if (!roomMapper.save(result)) {
	    throw new Exception("Can't save room to database");
	}

	// add to index
	index.putRoom(roomName, result);

	return result;
    }

    UserPermissionsMapper permissionsMapper;
    UserMapper userMapper;
    RoomMapper roomMapper;

    RepositoryIndex index;

    Database db;

}
