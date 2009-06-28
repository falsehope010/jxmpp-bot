package utils;
import java.io.*;
import java.util.ArrayList;

import base.DatabaseBaseTest;

import database.Database;
import domain.users.AccessLevel;
import domain.users.User;

public class ArrTest extends DatabaseBaseTest {
	

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
	


	public void testLoadAllUsers() throws NullPointerException, FileNotFoundException{

		Database db = prepareDatabase();
		
		assertEquals( truncateTable(db, "users"), true);
		assertEquals( truncateTable(db, "jids"), true);
		
		assertEquals(countRecords(db, "users"), 0);
		assertEquals(countRecords(db, "jids"), 0);
		
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
		
		usr.mapperSetID(usersDb.get(0).getID());
		usr2.mapperSetID(usersDb.get(1).getID());
		usr3.mapperSetID(usersDb.get(2).getID());
		
		assertEquals(usr.equals(usersDb.get(0)), true );
		assertEquals(usr2.equals(usersDb.get(1)), true );
		assertEquals(usr3.equals(usersDb.get(2)), true );
		
		
		db.disconnect();
	}
	

}
