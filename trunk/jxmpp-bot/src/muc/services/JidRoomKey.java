package muc.services;

import utils.HashUtil;

/**
 * Stores jabberID and roomName. Both fields are stored as {@link String}
 * <p>
 * Implements Value Object. Overrides equals() and hashCode() methods
 * correspondingly.
 * 
 * @author tillias
 * 
 */
public class JidRoomKey {

    public JidRoomKey(String jabberID, String roomName) {
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
	if (this == obj)
	    return true;

	if (!(obj instanceof JidRoomKey))
	    return false;

	JidRoomKey rj = (JidRoomKey) obj;

	return jabberID.equals(rj.getJabberID())
		&& roomName.equals(rj.getRoomName());
    }

    @Override
    public int hashCode() {
	if (fHashCode == 0) {
	    int result = HashUtil.SEED;
	    result ^= HashUtil.hashString(result, jabberID);
	    result ^= HashUtil.hashString(result, roomName);

	    fHashCode = result;
	}

	return fHashCode;
    }

    final String jabberID;
    final String roomName;
    int fHashCode;
}
