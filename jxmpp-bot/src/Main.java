import java.util.regex.Pattern;

import xmpp.configuration.Configuration;
import xmpp.configuration.ConnectionCredentials;
import xmpp.core.Connection;
import xmpp.core.IConnection;
import xmpp.core.IRoom;
import xmpp.messaging.PublicChatMessage;
import xmpp.messaging.base.Message;
import xmpp.messaging.domain.ParticipantInfo;
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

	/*
	 * ParticipantInfo sender = new ParticipantInfo("tillias@jabber.org",
	 * "vegatrek@conference.jabber.ru/TheBot"); ParticipantInfo recipient =
	 * new ParticipantInfo("[tillias]@jabber.ru",
	 * "vegatrek@conference.jabber.ru/tillias"); conn.send(new
	 * PrivateChatMessage(sender, recipient, "Hello! Test",
	 * "vegatrek@conference.jabber.ru"));
	 */
	ParticipantInfo sender = new ParticipantInfo("tillias@jabber.org",
		"vegatrek@conference.jabber.ru/TheBot");
	conn.send(new PublicChatMessage(sender, "Test",
		"vegatrek@conference.jabber.ru"));

	Thread.sleep(300000000);

	conn.disconnect();
    }

    static Pattern pattern = Pattern.compile("(.*)/(.*)");
    static int nickChangesCount = 0;
}
