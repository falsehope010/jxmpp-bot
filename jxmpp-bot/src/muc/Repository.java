package muc;

import java.util.List;

import mappers.RoomMapper;
import mappers.UserMapper;
import database.Database;
import domain.IdentityMap;
import domain.muc.Room;
import domain.muc.User;
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
	if (db == null)
	    throw new IllegalArgumentException("Database can't be null");
	if (!db.isConnected())
	    throw new IllegalArgumentException(
		    "Database isn't in connected state");
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
	    UserMapper mapper = new UserMapper(database);

	    List<User> users = mapper.getUsers();

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
	    RoomMapper mapper = new RoomMapper(database);

	    List<Room> rooms = mapper.getRooms();

	    for (Room room : rooms) {
		result.add(room);
	    }

	} catch (Exception e) {
	    throw new RepositoryInitializationException(
		    "Can't init rooms identity map");
	}

	return result;
    }

    Database db;
    IdentityMap<User> usersMap;
    IdentityMap<Room> roomsMap;
}
