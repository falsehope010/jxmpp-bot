package xmpp.core;

import java.util.HashMap;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;

import xmpp.configuration.ConnectionCredentials;
import xmpp.configuration.RoomCredentials;
import xmpp.listeners.PrivateMessageListener;
import xmpp.processing.IProcessor;

public class Connection implements IConnection {

    public Connection(ConnectionCredentials credentials,
	    IProcessor messageProcessor) throws NullPointerException {

	if (credentials == null)
	    throw new NullPointerException(
		    "Connection credentials can't be null");

	if (messageProcessor == null)
	    throw new NullPointerException("Message processor can't be null");

	this.credentials = credentials;
	this.messageProcessor = messageProcessor;

	rooms = new HashMap<String, IRoom>();
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation opens connection to remote xmpp server and starts
     * listening for incoming text messages. Valid packets are sent to
     * underlying {@link IProcessor} of this connection for futher processing.
     * Method blocks until connection process succeeds or fails. If already
     * connected does nothing
     */
    @Override
    public void connect() {
	if (conn == null || !isConnected()) {
	    try {
		conn = createXmppConnection(credentials);
		conn.connect();
		conn.login(credentials.getNick(), credentials.getPassword(),
			resource);

		conn.addPacketListener(new PrivateMessageListener(this,
			messageProcessor), null);

	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
    }

    @Override
    public IRoom createRoom(RoomCredentials roomCredentials) {
	IRoom result = null;

	if (isConnected()) {
	    try {
		result = new Room(roomCredentials, conn, messageProcessor);
		rooms.put(roomCredentials.getRoomName(), result);
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}

	return result;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation closes connection to remote xmpp server. If already
     * disconnected does nothing
     */
    @Override
    public void disconnect() {
	if (isConnected())
	    conn.disconnect();
    }

    @Override
    public boolean isConnected() {
	return conn.isConnected();
    }

    @Override
    public IRoom getRoom(String roomName) {
	return rooms.get(roomName);
    }

    private XMPPConnection createXmppConnection(
	    ConnectionCredentials connectionCredentials) {
	ConnectionConfiguration config = new ConnectionConfiguration(
		connectionCredentials.getServer(), connectionCredentials
			.getPort());
	return new XMPPConnection(config);
    }

    ConnectionCredentials credentials;

    static final String resource = "Digital";

    XMPPConnection conn;
    IProcessor messageProcessor;
    HashMap<String, IRoom> rooms;

}
