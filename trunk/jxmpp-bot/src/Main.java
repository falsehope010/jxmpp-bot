import mappers.SyslogMessageMapper;
import mappers.SyslogSessionMapper;

import org.jivesoftware.smack.*;

import syslog.SysLog;
import syslog.rotate.LogRotateBaseStrategy;

import database.*;


public class Main {

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
			
	    	try {
	    	
/*			DatabaseFactory factory = new DatabaseFactory("test_db");
			Database db = factory.createDatabase();
			db.connect();
			SyslogMessageMapper mapper = new SyslogMessageMapper(db);
			//...do any work with mapper
			db.disconnect();*/
			
	    	DatabaseFactory factory = new DatabaseFactory("test_db");
			Database db = factory.createDatabase();
			db.connect();
			
			LogRotateBaseStrategy lgs = new LogRotateBaseStrategy();
			
			SysLog sg = new SysLog(db,lgs, 10000,250000);
			sg.start();
			Thread.sleep(25000);
			sg.stop();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

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
