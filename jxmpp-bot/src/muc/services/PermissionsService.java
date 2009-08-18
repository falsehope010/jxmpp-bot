package muc.services;

import java.util.HashMap;
import java.util.List;

import muc.Repository;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
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

	loadUserPermissions();
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

    public void grantPermissions(UserPermissions permissions) {
	throw new NotImplementedException();
    }

    public void revokePermissions(UserPermissions permissions) {
	throw new NotImplementedException();
    }

    /**
     * Loads all {@link UserPermissions} domain objects from repository and
     * builds {@link #userPermissions} hash map
     * 
     * @throws ServiceInitializationException
     *             Thrown on initialization error. See clause for details
     */
    private void loadUserPermissions() throws ServiceInitializationException {
	try {
	    userPermissions = new HashMap<JidRoomKey, UserPermissions>();

	    List<UserPermissions> lperm = repository.getUserPermissions();// repository.getUserPermissions();

	    for (UserPermissions up : lperm) {
		JidRoomKey rj = generateRjPair(up);
		if (rj != null) {
		    userPermissions.put(rj, up);
		}
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
	    result = new JidRoomKey(up.getJabberID(), up.getRoom().getName());
	}

	return result;
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
