import junit.framework.TestCase;
import java.io.*;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;

import database.*;
import domain.*;

public class ArrTest extends TestCase {
	public static final String testDbName = "test/test_db";

	public void testGetSumElements() throws NullPointerException, FileNotFoundException {
		
			Database db = new Database(testDbName);
			assertNotNull(db);
			
			int[] source = new int[] {1,2,3,4,5,6,7,8,9,10};
			
			int sum = 0;
			
			for ( int i = 0; i < source.length; ++i){
				sum += source[i];
			}
			
			int test_sum = db.getSumElements(source);
			
			assertEquals(sum, test_sum);
			
			source = new int[] {};
			assertEquals(db.getSumElements(source), -1);
			
			source = null;
			assertEquals(db.getSumElements(source), -1);
	}
	
	public void testConnect() throws NullPointerException, FileNotFoundException{
		Database db = new Database(testDbName);
		
		db.connect();
		
		assertEquals(db.isConnected(), true);
		
		assertNotSame(db.countTables(), -1);
		
		db.disconnect();
		
		assertEquals(db.isConnected(), false);
		
		assertEquals(db.countTables(), -1);
		
		//send test query to sqlite database
		
		//Statement
	}

	public void testLoadAllUsers() throws NullPointerException, FileNotFoundException{

		Database db = PrepareDatabase();
		
		//create several test users
		String jid1 = "user1@jabber.org";
		String jid2 = "user2@jabber.org";
		String jid3 = "user3@jabber.org";
		AccessLevel lvl1 = new AccessLevel(100);
		AccessLevel lvl2 = new AccessLevel(200);
		AccessLevel lvl3 = new AccessLevel(300);
		
		User usr = new User("user1", jid1, lvl1);
		User usr2 = new User("user2", jid2, lvl2);
		User usr3 = new User("user3", jid3, lvl3);
		
		//insert them into db
		assertEquals(db.insertUser(usr.getRealName(), jid1, lvl1), true);
		assertEquals(db.insertUser(usr2.getRealName(), jid2, lvl2), true);
		assertEquals(db.insertUser(usr3.getRealName(), jid3, lvl3), true);
		
		//now retrieve all inserted users from db
		ArrayList<User> usersDb = db.loadAllUsers();

		assertEquals(usersDb.size(), 3);
		
		//check that all users are set to persistent
		for (User u: usersDb){
			assertEquals(u.isPersistent(), true); // check persistence by method
			assertNotSame(usr.getID(), 0); // check persistence by db_id
		}
		
		usr.setID(usersDb.get(0).getID());
		usr2.setID(usersDb.get(1).getID());
		usr3.setID(usersDb.get(2).getID());
		
		assertEquals(usr.equals(usersDb.get(0)), true );
		assertEquals(usr2.equals(usersDb.get(1)), true );
		assertEquals(usr3.equals(usersDb.get(2)), true );
		
		
		db.disconnect();
	}
	
	/**
	 * Creates Database instance using test_db file, opens connection and clears all data
	 * from database tables
	 * @return Valid, clear and ready-to-use database if succeded
	 * @throws NullPointerException If test_database name is invalid
	 * @throws FileNotFoundException If test_database file not found
	 */
	protected Database PrepareDatabase() throws NullPointerException, FileNotFoundException{
		// Connect to test database, clear all users and jids manually,
		Database db = new Database(testDbName);
		
		db.connect();
		
		Statement stat = null;
		Connection conn = db.getConnection();
		
		try {
			stat = conn.createStatement();
			stat.executeUpdate("delete from users where id > 0");
			
			stat = conn.createStatement();
			stat.executeUpdate("delete from jids where id_user > 0");
			
		} catch (Exception e) {
			fail(e.getMessage());
		}
		finally{
			db.Cleanup(stat);
		}
		
		return db;
	}
}
