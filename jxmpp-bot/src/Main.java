import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;

import xmpp.JidCollector;
import xmpp.MessageCollector;
import xmpp.XmppMessage;

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

	    conn.login("tillias", "DJ!u[Fc0i5@Z-13FNKK{Ykqj", "test");

	    MultiUserChat chat = null;

	    MessageCollector msgCollector = new MessageCollector();
	    JidCollector jidCollector = new JidCollector();

	    if (conn.isConnected()) {

		System.out.print("Logged in!\n");

		chat = new MultiUserChat(conn, "vegatrek@conference.jabber.ru");
		/*
		 * chat.addMessageListener(new PacketListener() {
		 * 
		 * @Override if (packet instanceof Message) { Message msg =
		 * (Message) packet; // System.out.println(msg.getBody());
		 * 
		 * } else System.out.println(packet.getClass()); } });
		 */

		chat.addMessageListener(msgCollector);
		chat.addParticipantListener(jidCollector);

		DiscussionHistory history = new DiscussionHistory();
		history.setMaxChars(0);
		chat.join("DigitalSoul", null, history, 5000);

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

	    while (true) {
		Thread.sleep(100);
		XmppMessage msg = msgCollector.poll();

		if (msg != null) {
		    String nickName = msg.getFrom();
		    String jid = jidCollector.getJid(nickName);

		    if (jid != null) {
			System.out.println(msg.getTimestamp() + " " + jid
				+ ":   " + msg.getText());
		    }
		}
	    }

	    // chat.leave();
	    // conn.disconnect();

	    // System.out.print(conn.isConnected());
	} catch (XMPPException ex) {
	    System.out.print(ex.getMessage());
	    System.out.print(ex.getStackTrace());
	}
    }
}
