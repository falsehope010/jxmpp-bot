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
	    	    
	    	    User first = users.get(0);
	    	    first.setRealName("tillias_changed");
	    	    first.setAccessLevel(new AccessLevel(333));
	    	    first.getJidCollection().clear();
	    	    first.addJID("new_jid_333@org");
	    	    db.updateUser(first);
	    	    db.updateUserJidCollection(first);
	    	    
	    	    first.DebugPrint();
	    	    
	    	    db.deleteUser(first);
	    	    
	    	    System.out.print(first.isPersistent());
	    	    
/*	    	    int[] ar1 = null;
	    	    int[] ar2 = new int[] {1,2,2,3};
	    	    int[] ar3 = new int[]{};*/
	    	    
	    	    
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
