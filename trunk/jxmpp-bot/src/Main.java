import java.util.regex.Pattern;

import xmpp.Connection;
import xmpp.IConnection;
import xmpp.IRoom;
import xmpp.configuration.Configuration;
import xmpp.configuration.ConnectionCredentials;
import exceptions.ConfigurationException;

public class Main {

    /**
     * @param args
     * @throws InterruptedException
     * @throws ConfigurationException
     */
    public static void main(String[] args) throws InterruptedException,
	    ConfigurationException {

	Configuration config = new Configuration();
	config.read("config.xml");

	ConnectionCredentials credentials = config.getCredentials();
	System.out.println(credentials);

	IConnection conn = new Connection(credentials);
	conn.connect();

	IRoom room = conn.createRoom(config.getRoomsCredentials()[0]);
	if (room != null)
	    room.join();
	/*
	 * System.out.println("Jid: " +
	 * room.getJID("vegatrek@conference.jabber.ru/tillias.work"));
	 */

	System.out.println(conn.isConnected());

	conn.disconnect();

	// System.out.println(conn.isConnected());

	/*
	 * RoomCredentials[] rooms = config.getRoomsCredentials(); for (int i =
	 * 0; i < rooms.length; ++i) { System.out.println(rooms[i]); }
	 */

	// XmppConnect();
    }

    static Pattern pattern = Pattern.compile("(.*)/(.*)");
    static int nickChangesCount = 0;
}
