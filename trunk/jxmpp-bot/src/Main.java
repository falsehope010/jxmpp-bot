import java.io.IOException;
import java.util.Formatter;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import database.Database;
import database.DatabaseFactory;

public class Main {

    /**
     * @param args
     */
    public static void main(String[] args) {

	Logger lg = Logger.getLogger("main.sys");

	SimpleFormatter fmt = new SimpleFormatter();

	FileHandler fh = null;
	try {
	    fh = new FileHandler("app.log");
	    fh.setFormatter(fmt);
	    lg.addHandler(fh);
	} catch (SecurityException e1) {
	    e1.printStackTrace();
	} catch (IOException e1) {
	    e1.printStackTrace();
	}

	try {

	    /*
	     * DatabaseFactory factory = new DatabaseFactory("test_db");
	     * Database db = factory.createDatabase(); db.connect();
	     * SyslogMessageMapper mapper = new SyslogMessageMapper(db); //...do
	     * any work with mapper db.disconnect();
	     */

	    DatabaseFactory factory = new DatabaseFactory("test_db");
	    Database db = factory.createDatabase();

	    db.connect();

	    lg.log(Level.INFO, "connected...");

	    db.disconnect();

	    lg.log(Level.INFO, "diconnected...");

	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    if (fh != null) {
		fh.flush();
		fh.close();
	    }
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
