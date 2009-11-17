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
	setNick("DigitalSoul");
	setResource("jxmpp-bot");
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

    public String getRoomName() {
	return roomName;
    }

    public void setRoomName(String roomName) {
	this.roomName = new String(roomName);
    }

    public String getNick() {
	return nick;
    }

    public void setNick(String nick) {
	this.nick = new String(nick);
    }

    public String getResource() {
	return resource;
    }

    public void setResource(String resource) {
	this.resource = new String(resource);
    }

    public String getPassword() {
	return password;
    }

    public void setPassword(String password) {
	this.password = new String(password);
    }

    public int getConnectTimeout() {
	return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
	this.connectTimeout = connectTimeout;
    }

    public static final int DEFAULT_CONNECT_TIMEOUT = 10000;

    String roomName;
    String nick;
    String resource;
    String password;
    int connectTimeout;
}
