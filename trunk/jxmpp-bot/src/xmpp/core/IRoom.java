package xmpp.core;

/**
 * Represents xmpp multi-user chat room
 * 
 * @author tillias
 * 
 */
public interface IRoom {
    /**
     * Joins xmpp multi-user chat room. If already joined does nothing
     * 
     * @see #isJoined()
     */
    void join();

    /**
     * Leaves xmpp multi-user chat room. If already left does nothing
     * 
     * @see #isJoined()
     */
    void leave();

    /**
     * Gets value indicating that {@link IRoom} is in joined state
     * 
     * @return True if is in joined state, false otherwise
     * @see #join()
     * @see #leave()
     */
    boolean isJoined();

    /**
     * Gets room name
     * 
     * @return Room name
     */
    String getName();

    /**
     * Gets jabber identifier of room's occupant using his fully qualified nick
     * name inside this room
     * 
     * @param occupantName
     *            Fully qualified nick name of user inside this room
     * @return Jabber identifier of the participant
     */
    String getJID(String occupantName);
}
