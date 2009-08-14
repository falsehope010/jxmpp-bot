import java.util.HashMap;

import muc.services.JidRoomKey;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

public class Main {

    /**
     * @param args
     */
    public static void main(String[] args) {
	int jidsCount = 100;
	int roomsCount = 2;

	// long start = MemoryAnalizer.getMemoryUse();

	HashMap<JidRoomKey, Long> map = new HashMap<JidRoomKey, Long>();

	map.put(new JidRoomKey("Peter_brown@gmail.com", "Chat_room1"), 50l);
	map.put(new JidRoomKey("peter@gmail.com", "Chat_room2"), 50l);
	map.put(new JidRoomKey("brown@gmail.com", "Chat_room3"), 10l);
	map.put(new JidRoomKey("liza@yahoo.com", "Chat_room1"), 10l);
	map.put(new JidRoomKey("white@yahoo.com", "Chat_room2"), 10l);
	map.put(new JidRoomKey("mGrey@gmail.com", "Chat_room3"), 50l);
	map.put(new JidRoomKey("johnDoe@gmail.com", "Chat_room2"), 10l);
	map.put(new JidRoomKey("mcCoy@gmail.com", "Chat_room3"), 10l);

	System.out.println(map.get(new JidRoomKey("mGrey@gmail.com",
		"Chat_room3")));

	JidRoomKey key1 = new JidRoomKey("account1", "room1");
	JidRoomKey key2 = new JidRoomKey("account1", "room1");

	if (key1.equals(key2)) {
	    System.out.println("Objects are equal");
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
