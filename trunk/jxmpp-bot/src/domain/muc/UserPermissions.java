package domain.muc;

import mappers.UserPermissionsMapper;
import domain.DomainObject;

/**
 * Stores access level for multi-chat user in the specified chat room.
 * 
 * @see User
 * @see Room
 * @author tillias
 * 
 */
public class UserPermissions extends DomainObject {

    /**
     * Default access level, which is assigned to {@link User} for the specified
     * {@link Room}
     */
    public static final int DEFAULT_ACCESS_LEVEL = 0;

    /**
     * Creates new instance using given user and room. Set's access level to
     * default value - zero.
     * 
     * @param user
     *            User who owns given jabberID
     * @param room
     *            Chat room
     * @param jabberID
     *            jabberID of given user
     * @throws IllegalArgumentException
     *             Thrown if user or room passed to constructor aren't
     *             persistent domain objects
     * @throws NullPointerException
     *             Thrown if any parameter passed to constructor is null
     *             reference
     * @see #validate()
     * @see #DEFAULT_ACCESS_LEVEL
     */
    public UserPermissions(User user, Room room, String jabberID) {
	this(user, room, jabberID, 0);
    }

    /**
     * Creates new instance using given user and room and grants given access
     * level.
     * 
     * @param user
     *            User who owns given jabberID
     * @param room
     *            Chat room
     * @param jabberID
     *            jabberID of given user
     * @param accessLevel
     *            Access level of given user for the specified room
     * @throws IllegalArgumentException
     *             Thrown if user or room passed to constructor aren't
     *             persistent domain objects
     * @throws NullPointerException
     *             Thrown if any parameter passed to constructor is null
     *             reference
     * @see #validate()
     */
    public UserPermissions(User user, Room room, String jabberID,
	    int accessLevel) throws IllegalArgumentException,
	    NullPointerException {
	if (user == null || room == null || jabberID == null)
	    throw new NullPointerException(
		    "Some argument passed to constructor is null-reference");

	if (!user.isPersistent())
	    throw new IllegalArgumentException(
		    "User must be persistent domain object");
	if (!room.isPersistent())
	    throw new IllegalArgumentException(
		    "Room must be persistent domain object");

	this.user = user;
	this.room = room;
	this.jabberID = jabberID;
	this.accessLevel = accessLevel;
    }

    /**
     * Gets jabber ID
     * 
     * @return
     */
    public String getJabberID() {
	return jabberID;
    }

    /**
     * Sets jabberID for given user.
     * 
     * @param jabberID
     *            Jabber identifier for given user. Parameter must not be null
     *            reference
     * @throws NullPointerException
     *             Thrown if jabberID parameter passed to method is null
     *             reference
     */
    public void setJabberID(String jabberID) throws NullPointerException {
	if (jabberID == null)
	    throw new NullPointerException(
		    "JabberID must not be null reference");

	this.jabberID = jabberID;
    }

    /**
     * Gets access level for given user
     * 
     * @return Access level for given user
     */
    public int getAccessLevel() {
	return accessLevel;
    }

    /**
     * Sets access level for given user.
     * 
     * @param accessLevel
     *            Access level for given user
     */
    public void setAccessLevel(int accessLevel) {
	this.accessLevel = accessLevel;
    }

    /**
     * Gets associated user
     * 
     * @return User
     */
    public User getUser() {
	return user;
    }

    /**
     * Gets associated room
     * 
     * @return Room
     */
    public Room getRoom() {
	return room;
    }

    /**
     * Gets value indicating whether underlying user and room aren't null
     * references and both are in persistent state.
     * 
     * <p>
     * Though it isn't possible to construct UserPermissions instance using
     * non-persistent User and Room objects, additional check is performed.
     * Method is used by {@link UserPermissionsMapper}
     * 
     * @return True if underlying user and room aren't null references and both
     *         are persistent domain objects.
     * @see #UserPermissions(User, Room, String)
     */
    public boolean validate() {
	boolean result = false;

	if (user != null && room != null) {
	    if (user.isPersistent() && room.isPersistent()) {
		result = true;
	    }
	}

	return result;
    }

    User user;
    Room room;
    String jabberID;
    int accessLevel;
}
