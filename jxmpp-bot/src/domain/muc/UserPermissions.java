package domain.muc;

import domain.DomainObject;

/**
 * Stores access level for bot's user in the specified chat room.
 * 
 * @see User
 * @see Room
 * @author tillias
 * 
 */
public class UserPermissions extends DomainObject {

    /**
     * Creates new instance using given user and room. Set's access level to
     * zero. You can manually change access level after creating instance using
     * {@link #setAccessLevel(int)}
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
     */
    public UserPermissions(User user, Room room, String jabberID)
	    throws IllegalArgumentException, NullPointerException {
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
	this.accessLevel = 0;
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

    User user;
    Room room;
    String jabberID;
    int accessLevel;
}
