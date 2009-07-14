import database.Database;
import database.DatabaseFactory;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

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

			db.disconnect();

		} catch (Exception e) {
			e.printStackTrace();
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
