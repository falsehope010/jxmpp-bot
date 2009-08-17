import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import utils.math.PermutationGenerator;

public class Main {

    /**
     * @param args
     */
    public static void main(String[] args) {

	// long start = MemoryAnalizer.getMemoryUse();

	/*
	 * final int size = 100000;
	 * 
	 * for (int i = 0; i < 72; ++i) { System.out.println(Integer.toString(i,
	 * 36)); }
	 */

	int[] indices;
	String elements = "abcdefg";
	PermutationGenerator x = new PermutationGenerator(elements.length());
	StringBuffer permutation;
	while (x.hasMore()) {
	    permutation = new StringBuffer();
	    indices = x.getNext();
	    for (int i = 0; i < indices.length; i++) {
		permutation.append(elements.charAt(indices[i]));
	    }
	    System.out.println(permutation.toString());
	}
	// dowork

	// long total = MemoryAnalizer.getMemoryUse() - start;

	// System.out.print(total);

	// XmppConnect();
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

		ChatManager chatManager = conn.getChatManager();

		if (chatManager != null) {
		    XmppMessageListener listener = new XmppMessageListener();
		    Chat chat = chatManager.createChat("[tillias]@jabber.ru",
			    listener);

		    chat.sendMessage("Hello!");
		}

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
