import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
    
    public boolean InsertUser(String realName, String JID, AccessLevel accessLevel ){
	boolean result = false;
	
	try {
	    
	    Connection conn = getConnection();
	    
	    PreparedStatement prep = conn.prepareStatement(
		    "insert into users(real_name,access_level) values (?,?);");
	    prep.setString(1, realName);
	    prep.setInt(2, accessLevel.getValue());
	    
	    System.out.print(prep.execute());
	    prep.close();

	    //Statement stat = conn.createStatement();
	    //System.out.print(stat.execute("insert into users(real_name,access_level) values('1','2')"));
	    
	    conn.close();
	    
	} catch (Exception e) {
    	    System.out.print(e.getMessage());
	}
	
	return result;
    }
    
    protected Connection getConnection(){
	Connection result = null;
	
	try {
	    Class.forName("org.sqlite.JDBC");
	    result = DriverManager.getConnection("jdbc:sqlite:" + fileName);
	} catch (Exception e) {
	    result = null;
	}
	
	return result;
    }
}
