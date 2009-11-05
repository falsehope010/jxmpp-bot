import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.MultiUserChat;

public class Main {

    /**
     * @param args
     */
    public static void main(String[] args) {

	XmppConnect();
    }

    protected static void XmppConnect() {
	try {
	    ConnectionConfiguration configuration = new ConnectionConfiguration(
		    "jabbus.org", 5222);
	    XMPPConnection conn = new XMPPConnection(configuration);

	    conn.connect();

	    // SASLAuthentication.supportSASLMechanism("PLAIN", 0);

	    conn.login("tillias", "DJ!u[Fc0i5@Z-13FNKK{Ykqj", "test");

	    if (conn.isConnected()) {

		System.out.print("Logged in!\n");

		MultiUserChat chat = new MultiUserChat(conn,
			"vegatrek@conference.jabber.ru");
		chat.join("test");

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

	    boolean flag = false;

	    while (!flag) {
		try {
		    Thread.sleep(1000);
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
	    }

	    conn.disconnect();

	    // System.out.print(conn.isConnected());
	} catch (XMPPException ex) {
	    System.out.print(ex.getMessage());
	}
    }

}
