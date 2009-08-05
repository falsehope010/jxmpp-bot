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

    public UserPermissionsEntity(long id, long user_id, long room_id,
	    String jid, int accessLevel) {
	this.id = id;
	this.user_id = user_id;
	this.room_id = room_id;
	this.jid = jid;
	this.access_level = accessLevel;
    }

    public long getId() {
	return id;
    }

    public void setId(long id) {
	this.id = id;
    }

    public long getUser_id() {
	return user_id;
    }

    public void setUser_id(long userId) {
	user_id = userId;
    }

    public long getRoom_id() {
	return room_id;
    }

    public void setRoom_id(long roomId) {
	room_id = roomId;
    }

    public String getJid() {
	return jid;
    }

    public void setJid(String jid) {
	this.jid = jid;
    }

    public int getAccess_level() {
	return access_level;
    }

    public void setAccess_level(int accessLevel) {
	access_level = accessLevel;
    }

    long id;
    long user_id;
    long room_id;
    String jid;
    int access_level;
}
