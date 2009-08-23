package muc.services;

import java.util.HashMap;
import java.util.List;

import muc.Repository;
import muc.StringHashMap;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import domain.DomainObject;
import domain.muc.Room;
import domain.muc.User;
import domain.muc.UserPermissions;
import exceptions.ServiceInitializationException;

/**
 * Represents muc service which contains set of methods allowing to manipulate
 * with user permissions
 * 
 * @author tillias
 * 
 */
public class PermissionsService extends AbstractService {

    /**
     * Creates new instance of user permissions service.
     * 
     * @param repository
     *            {@link Repository} which will be used to work with database
     *            (e.g. insert/update/delete domain objects)
     * @throws NullPointerException
     *             Thrown if repository parameter is null reference
     * @throws ServiceInitializationException
     *             Thrown on service initialization error. See exception clause
     *             for details
     */
    public PermissionsService(Repository repository)
	    throws NullPointerException, ServiceInitializationException {
	super(repository);

	userPermissions = new HashMap<JidRoomKey, UserPermissions>();
	usersIndex = new StringHashMap<User>();
	roomsIndex = new StringHashMap<Room>();

	initializeService();
    }

    /**
     * Gets {@link UserPermissions} record using user's jabberID and multichat
     * room name
     * <p>
     * Method is not thread-safe. Works fast due-to caching.
     * 
     * @param jabberID
     *            User's jabber ID (e.g. xmpp account name)
     * @param roomName
     *            Room name where user is chatting
     * @return {@link UserPermissions} for given user chatting in give chat
     *         room. If there is no such a user/room in database returns null
     *         pointer
     */
    public UserPermissions getPermissions(String jabberID, String roomName) {
	JidRoomKey key = new JidRoomKey(jabberID, roomName);

	return userPermissions.get(key);
    }

    public void grantPermissions(String jabberID, String roomName,
	    int accessLevel) {
	throw new NotImplementedException();
    }

    public void revokePermissions(String jabberID, String roomName) {
	throw new NotImplementedException();
    }

    /**
     * Loads all {@link UserPermissions} domain objects from repository and
     * builds {@link #userPermissions} hash map. Also builds usersIndex and
     * roomsIndex
     * 
     * @throws ServiceInitializationException
     *             Thrown on initialization error. See clause for details
     */
    private void initializeService() throws ServiceInitializationException {
	try {

	    List<UserPermissions> lperm = repository.getUserPermissions();// repository.getUserPermissions();

	    for (UserPermissions up : lperm) {
		JidRoomKey rj = generateRjPair(up);
		if (rj != null) {
		    userPermissions.put(rj, up);
		} else
		    throw new IllegalArgumentException(
			    "Invalid user permissions entity returned by Repository");
	    }

	    if (!buildIndices(lperm)) {
		throw new Exception("Can't build user/room indexes");
	    }
	} catch (Exception e) {
	    ServiceInitializationException exception = new ServiceInitializationException(
		    "Can't initialize service", e);
	    throw exception;

	}
    }

    private boolean buildIndices(List<UserPermissions> permissions)
	    throws IllegalArgumentException {
	boolean result = false;

	for (UserPermissions p : permissions) {
	    if (p != null && p.isPersistent()) {
		User user = p.getUser();
		Room room = p.getRoom();
		String jabberID = p.getJabberID();

		if (isValid(room) && isValid(user) && jabberID != null) {
		    usersIndex.put(jabberID, user);
		    roomsIndex.put(room.getName(), room);
		} else {
		    throw new IllegalArgumentException(
			    "Invalid permissions data field. Must be persistent");
		}
	    } else {
		throw new IllegalArgumentException(
			"Permission record isn't persistent domain objecy");
	    }
	}

	return result;
    }

    /**
     * Generates {@link JidRoomKey} using given {@link UserPermissions}
     * 
     * @param up
     *            {@link UserPermissions} instance which will be used to
     *            generate result. Parameter must be persistent domain object
     * @return Generated {@link JidRoomKey} if succeded, null reference
     *         otherwise
     */
    private JidRoomKey generateRjPair(UserPermissions up) {
	JidRoomKey result = null;

	if (up != null && up.isPersistent()) {
	    Room room = up.getRoom();
	    User user = up.getUser();
	    String jabberID = up.getJabberID();

	    if (isValid(room) && isValid(user) && jabberID != null) {
		result = new JidRoomKey(jabberID, room.getName());
	    }
	}

	return result;
    }

    private boolean isValid(DomainObject obj) {
	return obj != null && obj.isPersistent();
    }

    /**
     * Represents mapping between {@link JidRoomKey} and corresponding
     * {@link UserPermissions} object.
     * <p>
     * Implemented using {@link HashMap}. All values in hash map are persistent
     * domain objects
     * 
     * @see JidRoomKey
     * @see UserPermissions
     */
    HashMap<JidRoomKey, UserPermissions> userPermissions;

    StringHashMap<User> usersIndex;
    StringHashMap<Room> roomsIndex;
}
