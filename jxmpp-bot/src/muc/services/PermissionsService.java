package muc.services;

import java.util.HashMap;
import java.util.List;

import muc.Repository;
import domain.DomainObject;
import domain.muc.Room;
import domain.muc.User;
import domain.muc.UserPermissions;
import exceptions.ServiceInitializationException;
import exceptions.ServiceOperationException;

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

    /**
     * Grants permissions for specified user in specified chat room. If user
     * with given jabberID doesn't exist he is created. If room with given name
     * doesn't exist it is created.
     * <p>
     * If there exists permissions record already, only access level is updated
     * 
     * @param jabberID
     *            Jabber identifier for given user
     * @param roomName
     *            Chat room name
     * @param accessLevel
     *            Access level for user having given jabberID and chating in
     *            given room
     * @throws ServiceOperationException
     *             Thrown if any error has been occured during creating user
     *             permissions. See message and cause for details
     */
    public void grantPermissions(String jabberID, String roomName,
	    int accessLevel) throws ServiceOperationException {

	/* We create key and check if UserPermissions already exists */

	JidRoomKey key = new JidRoomKey(jabberID, roomName);
	UserPermissions permissions = userPermissions.get(key);

	if (permissions != null) {

	    /*
	     * Found permissions. Only access level should be updated. We should
	     * compare current access level and new value to be set in order to
	     * verify that access level has been actually changed
	     */

	    if (accessLevel != permissions.getAccessLevel()) {

		if (!repository.updateAccessLevel(permissions, accessLevel)) {
		    throw new ServiceOperationException(
			    "Can't save new access level to database", null);
		}
	    }

	} else {

	    /*
	     * Permissions record doesn't exist. We must create new permissions
	     * record
	     */

	    try {
		permissions = repository.createUserPermissions(jabberID,
			roomName, accessLevel);

		JidRoomKey newKey = generateRjPair(permissions);
		addKey(newKey, permissions);

	    } catch (Exception e) {
		ServiceOperationException ex = new ServiceOperationException(
			"Can't create new user permissions, see cause for details",
			e);
		throw ex;
	    }
	}

    }

    /**
     * Sets permissions for user having given jabberID in given chat room to
     * zero
     * 
     * @param jabberID
     *            User jabberID
     * @param roomName
     *            Char room name
     * @throws ServiceOperationException
     *             Thrown if access level can't be updated in database
     */
    public void revokePermissions(String jabberID, String roomName)
	    throws ServiceOperationException {
	JidRoomKey key = new JidRoomKey(jabberID, roomName);
	UserPermissions permissions = userPermissions.get(key);

	if (permissions != null) {
	    if (!repository.updateAccessLevel(permissions, 0))
		throw new ServiceOperationException(
			"Can't delete user permissions from database", null);
	}
    }

    /**
     * Loads all {@link UserPermissions} domain objects from repository and
     * builds {@link #userPermissions} hash map. Also builds usersIndex and
     * roomsIndex
     * 
     * @throws ServiceInitializationException
     *             Thrown on initialization error. See clause for details
     */
    protected void initializeService() throws ServiceInitializationException {
	try {
	    userPermissions = new HashMap<JidRoomKey, UserPermissions>();

	    List<UserPermissions> lperm = repository.getUserPermissions();

	    for (UserPermissions up : lperm) {
		JidRoomKey rj = generateRjPair(up);
		if (rj != null) {
		    addKey(rj, up);
		} else
		    throw new IllegalArgumentException(
			    "Invalid user permissions entity returned by Repository");
	    }
	} catch (Exception e) {
	    ServiceInitializationException exception = new ServiceInitializationException(
		    "Can't initialize service", e);
	    throw exception;

	}
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

    private void addKey(JidRoomKey key, UserPermissions value) {
	userPermissions.put(key, value);
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

}
