package xmpp.core;

import xmpp.configuration.RoomCredentials;

/**
 * Represents connection to remote xmpp server. Allows to create {@link IRoom}
 * implementations (e.g. implements Abstract Factory Pattern) and stores the set
 * of all {@link IRoom} created by this connection. Allows to get concrete room
 * using it's name.
 * <p>
 * Provides set of methods for connecting and disconnecting from remote server
 * 
 * @author tillias
 * 
 */
public interface IConnection extends ITransport {
    /**
     * Establishes connection to remote xmpp server. If already connected does
     * nothing.
     * <p>
     * Method <b>mustn't throw</b> any exceptions even unchecked ones. This
     * behavior forces concrete implementations to use internal logging for
     * debugging purposes if needed
     * 
     * @see #isConnected()
     */
    void connect();

    /**
     * Disconnects from remote xmpp server. If already disconnected does nothing
     * <p>
     * Method <b>mustn't throw</b> any exceptions even unchecked ones. This
     * behavior forces concrete implementations to use internal logging for
     * debugging purposes if needed
     * 
     * @see #isConnected()
     */
    void disconnect();

    /**
     * Gets value indicating that connection with remote xmpp server is
     * established via successful call of {@link #connect()} method
     * 
     * @return True if connection with remote xmpp server is established false
     *         otherwise
     * @see #connect()
     * @see #disconnect()
     */
    boolean isConnected();

    /**
     * Gets room managed by this connection using room's name.
     * 
     * @param roomName
     *            Room name
     * @return {@link IRoom} if there exists the room with given name and it is
     *         associated with this connection null otherwise
     * @see #createRoom(RoomCredentials)
     */
    IRoom getRoom(String roomName);

    /**
     * Gets array of rooms which are managed by this connection
     * 
     * @return Array of rooms which are managed by this connection
     */
    IRoom[] getRooms();

    /**
     * Creates new {@link IRoom} instance using given {@link RoomCredentials}
     * object and associates it with this connection.
     * 
     * @param credentials
     *            Security information which is used during room creation
     * @return {@link IRoom} instance if succeeded, null pointer otherwise
     * @see #getRoom(String)
     */
    IRoom createRoom(RoomCredentials credentials);
}
