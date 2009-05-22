
import org.jivesoftware.smack.*;

public class Main {

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		

	    	try {
	    	    Database db = new Database("test_db");
	    	    //User usr = new User("tillias", "123", new AccessLevel(1));
	    	    db.InsertUser("tillias", "123", new AccessLevel(1));
		} catch (Exception e) {
		    System.out.print(e.getMessage());
		}
		
		//db.Connect();
		//System.out.print( System.getProperty("user.dir") );
	}
	
	protected static void XmppConnect(){
		try{
			ConnectionConfiguration configuration = 
				new ConnectionConfiguration("jabbus.org",5222);
			XMPPConnection conn = new XMPPConnection(configuration);
			
			conn.connect();
			
			SASLAuthentication.supportSASLMechanism("PLAIN",0);
			
			conn.login("tillias", "DJ!u[Fc0i5@Z-13FNKK{Ykqj","test");
			
			if ( conn.isConnected()){
				
				System.out.print("Logged in!\n");
				
				ChatManager chatManager = conn.getChatManager();
				
				if ( chatManager != null){
					XmppMessageListener listener = new XmppMessageListener();
					Chat chat = chatManager.createChat("[tillias]@jabber.ru", listener);
					
					chat.sendMessage("Hello!");
				}
				
			}else{
				System.out.print("Can't login\n");
			}
			
			boolean flag = false;
			
			while ( !flag){
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			conn.disconnect();
			
			//System.out.print(conn.isConnected());
		}
		catch(XMPPException ex){
			System.out.print(ex.getMessage());
		}
	}

}
