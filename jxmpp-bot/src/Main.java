import java.util.regex.Pattern;

import syslog.ILog;
import xmpp.configuration.Configuration;
import xmpp.core.XmppService;
import exceptions.ConfigurationException;

public class Main {

    /**
     * @param args
     * @throws InterruptedException
     * @throws ConfigurationException
     */
    public static void main(String[] args) throws InterruptedException,
	    ConfigurationException {
	Configuration config = new Configuration();
	config.read("config.xml");

	XmppService service = new XmppService(config, new ILog() {

	    @Override
	    public boolean putMessage(String text, String sender,
		    String category, String type) {
		System.out.println(text);

		return true;
	    }
	});

	while (true) {

	    service.start();

	    Thread.sleep(10000);
	}

    }

    /*
     * private static void connectJabber() throws ConfigurationException,
     * NullPointerException, InterruptedException { IRoom room = null;
     * 
     * Configuration config = new Configuration(); config.read("config.xml");
     * 
     * ConnectionCredentials credentials = config.getCredentials();
     * System.out.println(credentials);
     * 
     * IConnection conn = new Connection(credentials, new IProcessor() {
     * 
     * @Override public void processMessage(Message msg) {
     * System.out.println(msg); System.out.println('\n'); } }); conn.connect();
     * 
     * room = conn.createRoom(config.getRoomsCredentials()[0]); if (room !=
     * null) room.join();
     * 
     * System.out.println(conn.isConnected());
     * 
     * Thread.sleep(300000000);
     * 
     * conn.disconnect(); }
     */

    static Pattern pattern = Pattern.compile("(.*)/(.*)");
    static int nickChangesCount = 0;
}
