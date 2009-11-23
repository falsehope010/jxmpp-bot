import java.util.regex.Pattern;

import xmpp.configuration.Configuration;
import xmpp.configuration.ConnectionCredentials;
import xmpp.core.Connection;
import xmpp.core.IConnection;
import xmpp.core.IRoom;
import xmpp.messaging.base.Message;
import xmpp.processing.IProcessor;
import exceptions.ConfigurationException;

public class Main {

    /**
     * @param args
     * @throws InterruptedException
     * @throws ConfigurationException
     */
    public static void main(String[] args) throws InterruptedException,
	    ConfigurationException {

	IRoom room = null;

	Configuration config = new Configuration();
	config.read("config.xml");

	ConnectionCredentials credentials = config.getCredentials();
	System.out.println(credentials);

	IConnection conn = new Connection(credentials, new IProcessor() {

	    @Override
	    public void processMessage(Message msg) {
		System.out.println(msg);
		System.out.println('\n');
	    }
	});
	conn.connect();

	room = conn.createRoom(config.getRoomsCredentials()[0]);
	if (room != null)
	    room.join();
	/*
	 * System.out.println("Jid: " +
	 * room.getJID("vegatrek@conference.jabber.ru/tillias.work"));
	 */

	System.out.println(conn.isConnected());

	Thread.sleep(30000000);

	conn.disconnect();
    }

    static Pattern pattern = Pattern.compile("(.*)/(.*)");
    static int nickChangesCount = 0;
}
