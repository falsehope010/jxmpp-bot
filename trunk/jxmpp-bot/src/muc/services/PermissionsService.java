package muc.services;

import java.util.HashMap;
import java.util.List;

import muc.Repository;
import domain.muc.UserPermissions;

/**
 * Represents muc service which contains set of methods allowing to manipulate
 * with user permissions
 * 
 * @author tillias
 * 
 */
public class PermissionsService extends AbstractService {

    /**
     * Creates new instance of permissions service.
     * 
     * @param repository
     *            {@link Repository} which will be used to work with database
     *            (e.g. insert/update/delete domain objects)
     * @throws NullPointerException
     *             Thrown if repository parameter is null reference
     */
    public PermissionsService(Repository repository)
	    throws NullPointerException {
	super(repository);

	loadUserPermissions();
    }

    /**
     * Loads all {@link UserPermissions} domain objects from repository and
     * builds {@link #userPermissions} hash map
     */
    private void loadUserPermissions() {
	try {
	    userPermissions = new HashMap<JidRoomKey, UserPermissions>();

	    // TODO:
	    List<UserPermissions> lperm = null;// repository.getUserPermissions();

	    for (UserPermissions up : lperm) {
		JidRoomKey rj = generateRjPair(up);
		if (rj != null) {
		    userPermissions.put(rj, up);
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
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
