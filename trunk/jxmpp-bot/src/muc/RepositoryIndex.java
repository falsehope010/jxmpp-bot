package muc;

import java.util.List;

import domain.DomainObject;
import domain.muc.Room;
import domain.muc.User;
import domain.muc.UserPermissions;

/**
 * Represents {@link Repository} index. Stores mappings between {@link Room} and
 * it's name; mappings between {@link User} and it's jabberID
 * 
 * @author tillias
 * 
 */
public class RepositoryIndex {

    /**
     * Creates new instance of index
     * 
     * @param permissions
     *            List of {@link UserPermissions} which will be used to build
     *            index
     * @throws IllegalArgumentException
     *             Thrown if permissions parameter passed to constructor is null
     *             reference or any error occurred while building index. See
     *             message for details
     */
    public RepositoryIndex(List<UserPermissions> permissions)
	    throws IllegalArgumentException {

	if (permissions == null)
	    throw new IllegalArgumentException("Permissions is null pointer");

	usersIndex = new StringHashMap<User>();
	roomsIndex = new StringHashMap<Room>();

	buildIndeces(permissions);
    }

    /**
     * Gets {@link Room} from index using room's name
     * 
     * @param roomName
     *            Room's name
     * @return Valid persistent {@link Room} if such a room exists in index,
     *         null reference otherwise
     */
    public Room getRoom(String roomName) {
	return roomsIndex.get(roomName);
    }

    /**
     * Puts {@link Room} into index. If room is already associated with given
     * room name then room will be replaced
     * 
     * @param roomName
     *            Room's name
     * @param room
     *            Room to be associated with it's name
     */
    public void putRoom(String roomName, Room room) {
	if (roomName != null && isValid(room)) {
	    roomsIndex.put(roomName, room);
	}
    }

    /**
     * Removes the mapping for the specified room name if present
     * 
     * @param roomName
     *            Chat room name
     */
    public void removeRoom(String roomName) {
	roomsIndex.remove(roomName);
    }

    /**
     * Gets {@link User} from index using user's jabberID
     * 
     * @param jabberID
     *            User's jabberID
     * @return Valid persistent {@link User} is such a user exists in index,
     *         null reference otherwise
     */
    public User getUser(String jabberID) {
	return usersIndex.get(jabberID);
    }

    /**
     * Puts {@link User} into index. If user is already associated with given
     * jabberID then user is replaced
     * 
     * @param jabberID
     *            User's jabberID
     * @param user
     *            User to be associated with it's jabberID
     */
    public void putUser(String jabberID, User user) {
	if (jabberID != null && isValid(user)) {
	    usersIndex.put(jabberID, user);
	}
    }

    /**
     * Removes mapping for the specified user's jabberID if present
     * 
     * @param jabberID
     *            User jabberID
     */
    public void removeUser(String jabberID) {
	usersIndex.remove(jabberID);
    }

    /**
     * Builds index. Method is invoked in constructor
     * 
     * @param permissions
     *            List of {@link UserPermissions} which will be used to build
     *            index
     * @throws IllegalArgumentException
     *             Thrown on any error during index building. See message for
     *             details
     */
    private void buildIndeces(List<UserPermissions> permissions)
	    throws IllegalArgumentException {
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
    }

    /**
     * Checks whether given domain object isn't null reference and is persistent
     * 
     * @param obj
     *            {@link DomainObject} to be verified
     * @return True if given domain object isn't null reference and is
     *         persistent, false otherwise
     */
    private boolean isValid(DomainObject obj) {
	return obj != null && obj.isPersistent();
    }

    StringHashMap<User> usersIndex;
    StringHashMap<Room> roomsIndex;
}
