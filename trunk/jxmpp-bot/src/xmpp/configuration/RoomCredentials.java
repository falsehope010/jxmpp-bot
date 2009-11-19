package xmpp.configuration;

/**
 * Contains credentials information that is needed to join multi-user chat room
 * 
 * @author tillias
 * @see Configuration
 * 
 */
public class RoomCredentials {

    /**
     * Creates new instance of credentials using default nick name, resource and
     * connection timeout
     */
    public RoomCredentials() {
	setNick(DEFAULT_NICK);
	setResource(DEFAULT_RESOURCE);
	setConnectTimeout(DEFAULT_CONNECT_TIMEOUT);
    }

    /**
     * Copy constructor.
     * 
     * @param r
     *            Room credentials whose fields will be copied to current
     *            instance
     */
    public RoomCredentials(RoomCredentials r) {
	setRoomName(r.getRoomName());
	setNick(r.getNick());
	setResource(r.getResource());
	setPassword(r.getPassword());
	setConnectTimeout(r.getConnectTimeout());
    }

    /**
     * Validates that room name isn't null and connect timeout is positive
     * 
     * @return True if validation is succeeded, false otherwise
     */
    public boolean validate() {
	boolean result = getRoomName() != null;
	result &= connectTimeout >= 0;
	return result;
    }

    @Override
    public String toString() {
	StringBuilder sb = new StringBuilder();

	sb.append("Room name: ");
	sb.append(getRoomName() + '\n');
	sb.append("Nick: ");
	sb.append(getNick() + '\n');
	sb.append("Resource: ");
	sb.append(getResource() + '\n');
	sb.append("Password: ");
	sb.append(getPassword() + '\n');
	sb.append("Connect timeout: ");
	sb.append(getConnectTimeout());

	return sb.toString();
    }

    /**
     * Gets room name. (Format: room@server.domain)
     * 
     * @return Room name
     */
    public String getRoomName() {
	return roomName;
    }

    /**
     * Sets room name. (Format: room@server.domain)
     * 
     * @param roomName
     *            New room name
     */
    public void setRoomName(String roomName) {
	this.roomName = roomName;
    }

    /**
     * Gets nick name which will be used on room's join and while performing
     * tasks inside room
     * 
     * @return Nick name
     */
    public String getNick() {
	return nick;
    }

    /**
     * Sets nick name which will be used on room's join and while performing
     * tasks inside room
     * 
     * @param nick
     *            New nick name
     */
    public void setNick(String nick) {
	this.nick = nick;
    }

    /**
     * Get's optional field value
     * 
     * @return Optional field value
     */
    public String getResource() {
	return resource;
    }

    /**
     * Sets optional field value
     * 
     * @param resource
     *            New optional field value
     */
    public void setResource(String resource) {
	this.resource = resource;
    }

    /**
     * Gets room password which will be used on room's join. If no password
     * required return null
     * 
     * @return Room password
     */
    public String getPassword() {
	return password;
    }

    /**
     * Sets room password which will be used on room's join. If no password
     * required null should be passed
     * 
     * @param password
     *            New rooms password
     */
    public void setPassword(String password) {
	this.password = password;
    }

    /**
     * Gets room's connect timeout.
     * 
     * @return Room connect timeout
     */
    public int getConnectTimeout() {
	return connectTimeout;
    }

    /**
     * Gets room's connect timeout.
     * 
     * @param connectTimeout
     *            New room connect timeout
     */
    public void setConnectTimeout(int connectTimeout) {
	this.connectTimeout = connectTimeout;
    }

    public static final int DEFAULT_CONNECT_TIMEOUT = 10000;
    public static final String DEFAULT_NICK = "DigitalSoul";
    public static final String DEFAULT_RESOURCE = "jxmpp-bot";

    String roomName;
    String nick;
    String resource;
    String password;
    int connectTimeout;
}
