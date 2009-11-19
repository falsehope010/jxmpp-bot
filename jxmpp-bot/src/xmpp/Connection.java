package xmpp;

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

	this.conn_credentials = credentials;
	this.conn = createXmppConnection(credentials);

	this.conn.addPacketListener(new PrivateMessageListener(messageProcessor),
		null);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation blocks until connection process succeeds or fails
     */
    @Override
    public void connect() {
	try {
	    conn.connect();
	    conn.login(conn_credentials.getNick(), conn_credentials
		    .getPassword(), resource);

	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    @Override
    public IRoom createRoom(RoomCredentials credentials) {
	IRoom result = null;

	try {
	    result = new Room(credentials, conn);
	} catch (Exception e) {
	    e.printStackTrace();
	}

	return result;
    }

    @Override
    public void disconnect() {
	conn.disconnect();
    }

    @Override
    public boolean isConnected() {
	return conn.isConnected();
    }

    private XMPPConnection createXmppConnection(
	    ConnectionCredentials credentials) {
	ConnectionConfiguration config = new ConnectionConfiguration(
		credentials.getServer(), credentials.getPort());
	return new XMPPConnection(config);
    }

    ConnectionCredentials conn_credentials;

    static final String resource = "Digital";
    XMPPConnection conn;
}
