import java.util.regex.Pattern;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;

import xmpp.AbstractXmppManager;
import xmpp.MessageQueue;
import xmpp.listeners.XmppPacketListener;

public class Main {

    /**
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
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
	    MessageQueue queue = new MessageQueue();
	    XmppPacketListener packetListener = new XmppPacketListener(queue);
	    conn.addPacketListener(packetListener, null);

	    MultiUserChat chat = null;

	    if (conn.isConnected()) {

		System.out.print("Logged in!\n");

		chat = new MultiUserChat(conn, "vegatrek@conference.jabber.ru");

		DiscussionHistory history = new DiscussionHistory();
		history.setMaxChars(0);

		chat.addParticipantStatusListener(packetListener);

		chat.join("DigitalSoul", null, history, 25000);

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

	    AbstractXmppManager manager = new AbstractXmppManager(queue);
	    Thread thrd = new Thread(manager);
	    thrd.start();

	    Thread.sleep(600000);

	    manager.stop();
	    /*
	     * while (true) { Thread.sleep(100); IXmppMessage msg =
	     * queue.poll(); if (msg != null) { System.out.println(msg); } }
	     */

	    chat.leave();
	    conn.disconnect();

	} catch (XMPPException ex) {
	    System.out.print(ex.getMessage());
	    System.out.print(ex.getStackTrace());
	}
    }

    static Pattern pattern = Pattern.compile("(.*)/(.*)");
    static int nickChangesCount = 0;
}
