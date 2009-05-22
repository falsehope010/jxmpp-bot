import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.*;

public class Database {
    
    String fileName;
    
    public Database(String fileName) 
    throws NullPointerException, FileNotFoundException{
	
	if ( fileName == null )
	    throw new NullPointerException("fileName can't be null");

	File f = new File(fileName);
	
	if ( !f.exists())
	    throw new FileNotFoundException("Database file not found");
	
	this.fileName = fileName;
    }
    
    public boolean InsertUser(String realName, String JID,
			AccessLevel accessLevel) {
		boolean result = false;

		Connection conn = null;
		PreparedStatement prep = null;
		
		try {
			conn = getConnection();

			prep = 
				conn.prepareStatement("insert into users(real_name,access_level) values (?,?);");
			
			prep.setString(1, realName);
			prep.setInt(2, accessLevel.getValue());
			
			result = prep.execute();

			prep.close();
			
			//TODO: Select inserted user id and insert userJID into jids table 
			
		} catch (Exception e) {
			CloseConnection(conn);
		}

		return result;
	}
    
    public boolean InsertUserJid(long UserID, String JID){
    	boolean result = false;
    	
    	Connection conn = null;
    	PreparedStatement prep = null;
    	
    	try {
    		conn = getConnection();
    		
    		prep = 
    			conn.prepareStatement("insert into jids(id_user,jid) values(?,?);");
    		prep.setLong(1, UserID);
    		prep.setString(2, JID);
    		
    		result = prep.execute();
    		
    		prep.close();
			
		} catch (Exception e) {
			CloseConnection(conn);
		}
    	
    	return result;
    }
    
    protected Connection getConnection() {
		Connection result = null;

		try {
			Class.forName("org.sqlite.JDBC");
			result = DriverManager.getConnection("jdbc:sqlite:" + fileName);
		} catch (Exception e) {
			result = null;
		}

		return result;
	}
    protected void CloseConnection(Connection conn){
    	if ( conn != null){
    		try {
				conn.close();
			} catch (Exception e) {}
    	}
    }
    
    protected long GetLastRecordID(){
    	return -1;
    }
}
