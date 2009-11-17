package xmpp;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;

import xmpp.configuration.ConnectionCredentials;
import xmpp.configuration.RoomCredentials;

public class Connection implements IConnection {

    public Connection(ConnectionCredentials credentials)
	    throws NullPointerException {
	if (credentials == null)
	    throw new NullPointerException(
		    "Connection credentials can't be null");

	this.credentials = credentials;
	this.conn = createXmppConnection(credentials);
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
	    conn.login(credentials.getNick(), credentials.getPassword(),
		    resource);

	} catch (Exception e) {
	    // nothing to do here
	}
    }

    @Override
    public IRoom createRoom(RoomCredentials credentials) {
	return null; // TODO
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
	    ConnectionCredentials conn_credentials) {
	ConnectionConfiguration config = new ConnectionConfiguration(
		conn_credentials.getServer(), conn_credentials.getPort());
	return new XMPPConnection(config);
    }

    ConnectionCredentials credentials;
    static final String resource = "Digital";
    XMPPConnection conn;
}
