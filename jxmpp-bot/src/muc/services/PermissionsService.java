package muc.services;

import java.lang.reflect.Array;
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
	    userPermissions = new HashMap<RjPair, UserPermissions>();

	    // TODO:
	    List<UserPermissions> lperm = null;// repository.getUserPermissions();

	    for (UserPermissions up : lperm) {
		RjPair rj = generateRjPair(up);
		if (rj != null) {
		    userPermissions.put(rj, up);
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    /**
     * Generates {@link RjPair} using given {@link UserPermissions}
     * 
     * @param up
     *            {@link UserPermissions} instance which will be used to
     *            generate result. Parameter must be persistent domain object
     * @return Generated {@link RjPair} if succeded, null reference otherwise
     */
    private RjPair generateRjPair(UserPermissions up) {
	RjPair result = null;

	if (up != null && up.isPersistent()) {
	    result = new RjPair(up.getJabberID(), up.getRoom().getName());
	}

	return result;
    }

    /**
     * Represents mapping between {@link RjPair} and corresponding
     * {@link UserPermissions} object.
     * <p>
     * Implemented using {@link HashMap}. All values in hash map are persistent
     * domain objects
     * 
     * @see RjPair
     * @see UserPermissions
     */
    HashMap<RjPair, UserPermissions> userPermissions;
}

/**
 * Stores jabberID and roomName. Both fields are stored as {@link String}
 * <p>
 * Implements Value Object. Overrides equals() and hashCode() methods
 * correspondingly.
 * 
 * @author tillias
 * 
 */
class RjPair {

    protected static final int SEED = 23;
    protected static final int fODD_PRIME_NUMBER = 37;

    public RjPair(String jabberID, String roomName) {
	this.jabberID = jabberID;
	this.roomName = roomName;

	fHashCode = 0;
    }

    public String getJabberID() {
	return jabberID;
    }

    public String getRoomName() {
	return roomName;
    }

    @Override
    public boolean equals(Object obj) {
	if (!(obj instanceof RjPair))
	    return false;

	RjPair rj = (RjPair) obj;

	return jabberID.equals(rj.getJabberID())
		&& roomName.equals(rj.getRoomName());
    }

    @Override
    public int hashCode() {
	if (fHashCode == 0) {
	    int result = RjPair.SEED;
	    result = hash(result, jabberID);
	    result = hash(result, roomName);

	    fHashCode = result;
	}

	return fHashCode;
    }

    private int hash(int aSeed, Object aObject) {

	int result = aSeed;
	if (aObject == null) {
	    result = hash(result, 0);
	} else if (!isArray(aObject)) {
	    result = hash(result, aObject.hashCode());
	} else {
	    int length = Array.getLength(aObject);
	    for (int idx = 0; idx < length; ++idx) {
		Object item = Array.get(aObject, idx);
		// recursive call!
		result = hash(result, item);
	    }
	}
	return result;
    }

    private int hash(int aSeed, int aInt) {
	/*
	 * Implementation Note Note that byte and short are handled by this
	 * method, through implicit conversion.
	 */
	return firstTerm(aSeed) + aInt;
    }

    private boolean isArray(Object aObject) {
	return aObject.getClass().isArray();
    }

    private int firstTerm(int aSeed) {
	return fODD_PRIME_NUMBER * aSeed;
    }

    final String jabberID;
    final String roomName;
    int fHashCode;
}
