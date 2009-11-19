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
     * 
     * @see #DEFAULT_SERVER_PORT
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

    /**
     * Performs basic validation of credentials. Checks that port number is
     * positive, text fields aren't null and owner JID is valid jabber
     * identifier (e.g. has the form of name@server.domain)
     * 
     * @return True validation has succeeded, false otherwise
     */
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

    /**
     * Gets nick name (e.g. login) which system will use while authorizing on
     * remote xmpp server
     * 
     * @return Nick name (login)
     */
    public String getNick() {
	return nick;
    }

    /**
     * Sets nick name (e.g. login) which system will use while authorizing on
     * remote xmpp server
     * 
     * @param nick
     *            New nick name (login)
     */
    public void setNick(String nick) {
	this.nick = nick;
    }

    /**
     * Gets password which system will use while authorizing on remote xmpp
     * server
     * 
     * @return Password
     */
    public String getPassword() {
	return password;
    }

    /**
     * Sets password which system will use while authorizing on remote xmpp
     * server
     * 
     * @param password
     *            New password
     */
    public void setPassword(String password) {
	this.password = password;
    }

    /**
     * Gets remote xmpp server host name
     * 
     * @return Server host name
     */
    public String getServer() {
	return server;
    }

    /**
     * Sets remote xmpp server host name
     * 
     * @param server
     *            New server host name
     */
    public void setServer(String server) {
	this.server = server;
    }

    /**
     * Gets remote xmpp server port
     * 
     * @return Server port
     */
    public int getPort() {
	return port;
    }

    /**
     * Sets remote xmpp server port
     * 
     * @param port
     *            New server port
     */
    public void setPort(int port) {
	this.port = port;
    }

    /**
     * Gets jabber identifier of system owner (administrator) that will be able
     * to perform specifical tasks
     * 
     * @return Owner jabber identifier
     */
    public String getOwnerJID() {
	return ownerJID;
    }

    /**
     * Sets jabber identifier of system owner (administrator) that will be able
     * to perform specifical tasks
     * 
     * @param ownerJID
     *            New owner jabber identifier
     */
    public void setOwnerJID(String ownerJID) {
	this.ownerJID = ownerJID;
    }

    /**
     * Default xmpp server port. Currently is 5222
     */
    public static final int DEFAULT_SERVER_PORT = 5222;

    static final Pattern regex = Pattern
	    .compile("^([\\p{Print})&&[^@]])+@(([\\p{Print})&&[^@.]])+[.])+\\w+");

    String nick;
    String password;
    String server;
    int port;
    String ownerJID;
}
