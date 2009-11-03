package domain.muc;

import utils.HashUtil;
import domain.DomainObject;

/**
 * Represents multi-chat room. Stores room's full name (including xmpp server
 * name ) and optionally text comment
 * 
 * @author tillias
 * 
 */
public class Room extends DomainObject {

    /**
     * Creates new instance of room using given room name
     * 
     * @param name
     *            Room name
     */
    public Room(String name) {
	this(name, null);
    }

    /**
     * Creates new instance of room using given room name and description string
     * 
     * @param name
     *            Room name. Parameter must not be null
     * @param description
     *            Room description
     * @throws NullPointerException
     *             Thrown if room name parameter passed to method is null
     *             reference
     */
    public Room(String name, String description) throws NullPointerException {
	if (name == null)
	    throw new NullPointerException(
		    "Room name must not be null reference");

	this.name = name;
	this.description = description;
    }

    /**
     * Gets room name
     * 
     * @return Room name
     */
    public String getName() {
	return name;
    }

    /**
     * Sets room name
     * 
     * @param name
     *            New room name. Parameter must not be null reference
     * @throws NullPointerException
     *             Thrown if new room name passed to method is null reference
     */
    public void setName(String name) throws NullPointerException {
	if (name == null)
	    throw new NullPointerException(
		    "Room name must no be null reference");
	this.name = name;
    }

    /**
     * Gets room description
     * 
     * @return Room description
     */
    public String getDescription() {
	return description;
    }

    /**
     * Sets room description
     * 
     * @param description
     *            New room description. Parameter can be null
     */
    public void setDescription(String description) {
	this.description = description;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;

	if (!(obj instanceof Room))
	    return false;

	Room room = (Room) obj;

	boolean areEquals = this.getID() == room.getID();

	areEquals &= name.equals(room.name);

	if (description != null)
	    areEquals &= description.equals(room.description);
	else
	    areEquals &= room.description == null;

	return areEquals;
    }

    @Override
    public int hashCode() {
	if (fHashCode == 0) {
	    int result = HashUtil.SEED;
	    result ^= HashUtil.hashLong(result, getID());
	    result ^= HashUtil.hashString(result, name);
	    result ^= HashUtil.hashString(result, description);

	    fHashCode = result;
	}

	return fHashCode;
    }

    String name;
    String description;

    int fHashCode;
}
