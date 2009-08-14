import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import muc.services.JidRoomKey;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import utils.MemoryAnalizer;
import utils.RandomUtils;

public class Main {

    /**
     * @param args
     */
    public static void main(String[] args) {
	int jidsCount = 100;
	int roomsCount = 2;

	long start = MemoryAnalizer.getMemoryUse();

	HashMap<JidRoomKey, Long> map = new HashMap<JidRoomKey, Long>();
	List<JidRoomKey> keys = new ArrayList<JidRoomKey>(jidsCount
		* roomsCount);

	Long value = 0l;

	for (int i = 0; i < jidsCount; ++i) {
	    String jidName = RandomUtils.getRandomString(64);
	    for (int j = 0; j < roomsCount; ++j) {
		String roomName = RandomUtils.getRandomString(128);

		JidRoomKey key = new JidRoomKey(jidName, roomName);

		map.put(key, value);
		keys.add(key);
	    }
	}

	System.out.println(map.keySet().size());
	for (JidRoomKey key : keys) {
	    if (map.get(key) != value) {
		System.out.println("error");
	    }
	}

	// dowork

	long total = MemoryAnalizer.getMemoryUse() - start;

	System.out.print(total);

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
