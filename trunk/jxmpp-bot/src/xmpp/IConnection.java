package xmpp;

import xmpp.configuration.RoomCredentials;

/**
 * Represents connection to remote xmpp server
 * 
 * @author tillias
 * 
 */
public interface IConnection {
    /**
     * Establishes connection to remote xmpp server. If already connected does
     * nothing
     * 
     * @see #isConnected()
     */
    void connect();

    /**
     * Disconnects from remote xmpp server. If already disconnected does nothing
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
     * Creates new {@link IRoom} instance using given {@link RoomCredentials}
     * object
     * 
     * @param credentials
     *            Security information which is used during room creation
     * @return {@link IRoom} instance if succeded, false otherwise
     */
    IRoom createRoom(RoomCredentials credentials);
}
