package xmpp.configuration;

import java.util.regex.Pattern;

/**
 * Stores configuration data that is needed to establish connection with xmpp
 * server as well as owner jabber id
 * 
 * @author tillias
 * 
 */
public class ConnectionCredentials {

    public ConnectionCredentials() {
	setPort(DEFAULT_SERVER_PORT);
    }

    public ConnectionCredentials(ConnectionCredentials c) {
	setJID(c.getJID());
	setPassword(c.getPassword());
	setServer(c.getServer());
	setPort(c.getPort());
	setOwnerJID(c.getOwnerJID());
    }

    public boolean validate() {
	boolean result = getPort() > 0;
	result &= getJID() != null;
	result &= getPassword() != null;
	result &= getServer() != null;
	result &= getOwnerJID() != null;

	// validate jid and owner jid
	result &= regex.matcher(getJID()).matches();
	result &= regex.matcher(getOwnerJID()).matches();

	return result;
    }

    @Override
    public String toString() {
	StringBuilder sb = new StringBuilder();

	sb.append("Jid: ");
	sb.append(getJID() + '\n');
	sb.append("Password: ");
	sb.append(getPassword() + '\n');
	sb.append("Server: ");
	sb.append(getServer() + '\n');
	sb.append("Port: ");
	sb.append(getPort());
	sb.append('\n');
	sb.append("Owner: ");
	sb.append(getOwnerJID());

	return sb.toString();
    }

    public String getJID() {
	return new String(JID);
    }

    public void setJID(String jID) {
	JID = jID;
    }

    public String getPassword() {
	return new String(password);
    }

    public void setPassword(String password) {
	this.password = password;
    }

    public String getServer() {
	return new String(server);
    }

    public void setServer(String server) {
	this.server = server;
    }

    public int getPort() {
	return port;
    }

    public void setPort(int port) {
	this.port = port;
    }

    public String getOwnerJID() {
	return new String(ownerJID);
    }

    public void setOwnerJID(String ownerJID) {
	this.ownerJID = ownerJID;
    }

    public static final int DEFAULT_SERVER_PORT = 5222;
    static final Pattern regex = Pattern
	    .compile("^([\\p{Print})&&[^@]])+@(([\\p{Print})&&[^@.]])+[.])+\\w+");

    String JID;
    String password;
    String server;
    int port;
    String ownerJID;
}
