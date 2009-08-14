package muc.services;

import java.lang.reflect.Array;

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

    protected static final int SEED = 23;
    protected static final int fODD_PRIME_NUMBER = 37;

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
	if (!(obj instanceof JidRoomKey))
	    return false;

	JidRoomKey rj = (JidRoomKey) obj;

	return jabberID.equals(rj.getJabberID())
		&& roomName.equals(rj.getRoomName());
    }

    @Override
    public int hashCode() {
	if (fHashCode == 0) {
	    int result = JidRoomKey.SEED;
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
