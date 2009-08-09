package domain.internal;

/**
 * Represents object model of single row in 'permissions' db table.
 * <p>
 * For internal use only
 * 
 * @author tillias
 * 
 */
public class UserPermissionsEntity {

    public UserPermissionsEntity(long id, long userID, long roomID,
	    String jabberID, int accessLevel) {
	this.id = id;
	this.userID = userID;
	this.roomID = roomID;
	this.jabberID = jabberID;
	this.accessLevel = accessLevel;
    }

    public long getID() {
	return id;
    }

    public void setID(long id) {
	this.id = id;
    }

    public long getUserID() {
	return userID;
    }

    public void setUserID(long userId) {
	userID = userId;
    }

    public long getRoomID() {
	return roomID;
    }

    public void setRoomID(long roomId) {
	roomID = roomId;
    }

    public String getJabberID() {
	return jabberID;
    }

    public void setJabberID(String jid) {
	this.jabberID = jid;
    }

    public int getAccessLevel() {
	return accessLevel;
    }

    public void setAccessLevel(int accessLevel) {
	this.accessLevel = accessLevel;
    }

    long id;
    long userID;
    long roomID;
    String jabberID;
    int accessLevel;
}
