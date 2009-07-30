import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

public class Main {

    /**
     * @param args
     */
    public static void main(String[] args) {

	Connection conn = null;

	try {
	    conn = DriverManager
		    .getConnection("jdbc:derby:derbyDB;create=true");

	    PreparedStatement pr = conn
		    .prepareStatement("insert into test_table(name,surname) values(?,?)");

	    conn.setAutoCommit(false);

	    for (int i = 0; i < 100000; ++i) {
		pr.setString(1, Integer.toString(i) + "AAAAAAAAAAAAAAA");
		pr.setString(2, "12312312313212313123123123");

		pr.executeUpdate();

	    }

	    conn.commit();

	    conn.setAutoCommit(true);

	    pr.close();

	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	try {
	    if (conn != null)
		conn.close();
	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	try {
	    DriverManager.getConnection("jdbc:derby:;shutdown=true");
	} catch (SQLException e) {
	}
    }

    protected static void XmppConnect() {
	try {
	    ConnectionConfiguration configuration = new ConnectionConfiguration(
		    "jabbus.org", 5222);
	    XMPPConnection conn = new XMPPConnection(configuration);

	    conn.connect();

	    SASLAuthentication.supportSASLMechanism("PLAIN", 0);

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
