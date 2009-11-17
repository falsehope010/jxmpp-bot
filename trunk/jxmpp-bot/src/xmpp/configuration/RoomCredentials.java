package xmpp.configuration;

public class RoomCredentials {

    public RoomCredentials() {
	setNick("DigitalSoul");
	setResource("jxmpp-bot");
	setConnectTimeout(DEFAULT_CONNECT_TIMEOUT);
    }

    public RoomCredentials(RoomCredentials r) {
	setRoomName(r.getRoomName());
	setNick(r.getNick());
	setResource(r.getResource());
	setPassword(r.getPassword());
	setConnectTimeout(r.getConnectTimeout());
    }

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
	return new String(roomName);
    }

    public void setRoomName(String roomName) {
	this.roomName = roomName;
    }

    public String getNick() {
	return new String(nick);
    }

    public void setNick(String nick) {
	this.nick = nick;
    }

    public String getResource() {
	return new String(resource);
    }

    public void setResource(String resource) {
	this.resource = resource;
    }

    public String getPassword() {
	return new String(password);
    }

    public void setPassword(String password) {
	this.password = password;
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
