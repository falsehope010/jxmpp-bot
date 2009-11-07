import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.ParticipantStatusListener;

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

		// MucCollector manager = new MucCollector();
		chat
			.addParticipantStatusListener(new ParticipantStatusListener() {

			    @Override
			    public void voiceRevoked(String participant) {
				// TODO Auto-generated method stub

			    }

			    @Override
			    public void voiceGranted(String participant) {
				// TODO Auto-generated method stub

			    }

			    @Override
			    public void ownershipRevoked(String participant) {
				// TODO Auto-generated method stub

			    }

			    @Override
			    public void ownershipGranted(String participant) {
				// TODO Auto-generated method stub

			    }

			    @Override
			    public void nicknameChanged(String participant,
				    String newNickname) {
				// TODO Auto-generated method stub

			    }

			    @Override
			    public void moderatorRevoked(String participant) {
				// TODO Auto-generated method stub

			    }

			    @Override
			    public void moderatorGranted(String participant) {
				// TODO Auto-generated method stub

			    }

			    @Override
			    public void membershipRevoked(String participant) {
				// TODO Auto-generated method stub

			    }

			    @Override
			    public void membershipGranted(String participant) {
				// TODO Auto-generated method stub

			    }

			    @Override
			    public void left(String participant) {
				System.out.println("Left: " + participant);

			    }

			    @Override
			    public void kicked(String participant,
				    String actor, String reason) {
				// TODO Auto-generated method stub

			    }

			    @Override
			    public void joined(String participant) {
				System.out.println("Joined: " + participant);

			    }

			    @Override
			    public void banned(String participant,
				    String actor, String reason) {
				// TODO Auto-generated method stub

			    }

			    @Override
			    public void adminRevoked(String participant) {
				// TODO Auto-generated method stub

			    }

			    @Override
			    public void adminGranted(String participant) {
				// TODO Auto-generated method stub

			    }
			});

		chat.join("DigitalSoul");

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
