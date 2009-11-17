import java.util.regex.Pattern;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;

import xmpp.listeners.XmppPacketListener;
import xmpp.messaging.IXmppMessage;
import xmpp.processing.IXmppProcessor;
import exceptions.ConfigurationException;

public class Main {

    /**
     * @param args
     * @throws InterruptedException
     * @throws ConfigurationException
     */
    public static void main(String[] args) throws InterruptedException,
	    ConfigurationException {

	/*
	 * Configuration config = new Configuration();
	 * config.read("config.xml");
	 * 
	 * ConnectionCredentials credentials = config.getCredentials();
	 * System.out.println(credentials);
	 * 
	 * RoomCredentials[] rooms = config.getRoomsCredentials(); for (int i =
	 * 0; i < rooms.length; ++i) { System.out.println(rooms[i]); }
	 */

	XmppConnect();
    }

    protected static void XmppConnect() throws InterruptedException {
	try {

	    ConnectionConfiguration configuration = new ConnectionConfiguration(
		    "jabbus.org", 5222);
	    XMPPConnection conn = new XMPPConnection(configuration);

	    conn.connect();

	    // SASLAuthentication.supportSASLMechanism("PLAIN", 0);

	    conn.login("tillias", "DJ!u[Fc0i5@Z-13FNKK{Ykqj", "Digital");
	    XmppPacketListener packetListener = new XmppPacketListener(
		    new IXmppProcessor() {

			@Override
			public void processMessage(IXmppMessage msg) {
			    System.out.println(msg);
			}
		    });
	    conn.addPacketListener(packetListener, null);

	    MultiUserChat chat = null;
	    MultiUserChat chat2 = null;

	    if (conn.isConnected()) {

		System.out.print("Logged in!\n");

		chat = new MultiUserChat(conn, "vegatrek@conference.jabber.ru");
		chat2 = new MultiUserChat(conn,
			"christian@conference.jabber.ru");

		DiscussionHistory history = new DiscussionHistory();
		history.setMaxChars(0);

		chat.addParticipantStatusListener(packetListener);
		chat2.addParticipantStatusListener(packetListener);

		chat.join("DigitalSoul", null, history, 25000);
		// chat2.join("DigitalSoul", null, history, 25000);

		/*
		 * for (Affiliate a : chat.getOwners()) {
		 * chat.sendMessage("Owner: " + a.getJid()); }
		 */

		/*
		 * chat.sendMessage("Hello this is me!");
		 * 
		 * Message msg = new Message(
		 * "vegatrek@conference.jabber.ru/tillias");
		 * msg.setBody("This is PM"); chat.sendMessage(msg);
		 */

		/*
		 * ChatManager chatManager = conn.getChatManager();
		 * 
		 * if (chatManager != null) { XmppMessageListener listener = new
		 * XmppMessageListener(); Chat chat =
		 * chatManager.createChat("tillias@jabber.org", listener);
		 * 
		 * chat.sendMessage("Hello!"); }
		 */

	    } else {
		System.out.print("Can't login\n");
	    }

	    Thread.sleep(100000);

	    /*
	     * while (true) { Thread.sleep(100); IXmppMessage msg =
	     * queue.poll(); if (msg != null) { System.out.println(msg); } }
	     */

	    chat.leave();
	    chat2.leave();
	    conn.disconnect();

	} catch (XMPPException ex) {
	    System.out.print(ex.getMessage());
	    System.out.print(ex.getStackTrace());
	}
    }

    static Pattern pattern = Pattern.compile("(.*)/(.*)");
    static int nickChangesCount = 0;
}
