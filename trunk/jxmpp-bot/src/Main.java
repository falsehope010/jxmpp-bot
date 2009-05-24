
import java.util.ArrayList;

import org.jivesoftware.smack.*;

public class Main {

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		

	    	try {
	    	    Database db = new Database("test_db");
	    	    db.connect();
	    	    //User usr = new User("tillias", "123", new AccessLevel(1));
	    	    //db.insertUser("tillias", "123", new AccessLevel(1));
	    	    
	    	    //db.InsertUserJid(usrID, "tillias@jabber");
	    	    
	    	    ArrayList<User> users = db.loadAllUsers();
	    	    
	    	    if (users != null ){
	    	    	System.out.print(users.size());
	    	    	for ( User u : users){
	    	    		u.DebugPrint();
	    	    	}
	    	    }
	    	    
	    	    User use2 = null;
	    	    
	    	    System.out.print(users.get(0).equals(use2));
	    	    
	    	    
	    	    db.disconnect();
	    	    //System.out.print(usrID);
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
