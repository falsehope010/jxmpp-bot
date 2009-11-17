package xmpp.configuration;

import java.util.regex.Pattern;

/**
 * Stores credentials information that is needed to establish connection with
 * xmpp server as well as owner's jabberID.
 * 
 * @author tillias
 * 
 */
public class ConnectionCredentials {

    /**
     * Creates new instance of credentials using default server port
     */
    public ConnectionCredentials() {
	setPort(DEFAULT_SERVER_PORT);
    }

    /**
     * Copy constructor
     * 
     * @param c
     *            Connection credentials whose fields will be copied to current
     *            instance
     */
    public ConnectionCredentials(ConnectionCredentials c) {
	setNick(c.getNick());
	setPassword(c.getPassword());
	setServer(c.getServer());
	setPort(c.getPort());
	setOwnerJID(c.getOwnerJID());
    }

    public boolean validate() {
	boolean result = getPort() > 0;
	result &= getNick() != null;
	result &= getPassword() != null;
	result &= getServer() != null;
	result &= getOwnerJID() != null;

	// validate nick and owner nick
	result &= regex.matcher(getOwnerJID()).matches();

	return result;
    }

    @Override
    public String toString() {
	StringBuilder sb = new StringBuilder();

	sb.append("Jid: ");
	sb.append(getNick() + '\n');
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

    public String getNick() {
	return nick;
    }

    public void setNick(String nick) {
	this.nick = new String(nick);
    }

    public String getPassword() {
	return password;
    }

    public void setPassword(String password) {
	this.password = new String(password);
    }

    public String getServer() {
	return server;
    }

    public void setServer(String server) {
	this.server = new String(server);
    }

    public int getPort() {
	return port;
    }

    public void setPort(int port) {
	this.port = port;
    }

    public String getOwnerJID() {
	return ownerJID;
    }

    public void setOwnerJID(String ownerJID) {
	this.ownerJID = new String(ownerJID);
    }

    public static final int DEFAULT_SERVER_PORT = 5222;
    static final Pattern regex = Pattern
	    .compile("^([\\p{Print})&&[^@]])+@(([\\p{Print})&&[^@.]])+[.])+\\w+");

    String nick;
    String password;
    String server;
    int port;
    String ownerJID;
}
