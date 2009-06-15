package base.tests;
import java.io.FileNotFoundException;

import junit.framework.TestCase;

import database.Database;


public class DatabaseBaseTest extends TestCase {
	
	public static final String testDbName = "test/test_db";
	
	/**
	 * Creates Database instance using test_db file, opens connection and clears all data
	 * from database tables
	 * @return Valid, clear and ready-to-use database if succeded
	 * @throws NullPointerException If test_database name is invalid
	 * @throws FileNotFoundException If test_database file not found
	 */
	protected Database prepareDatabase() throws NullPointerException, FileNotFoundException{
		// Connect to test database, clear all users and jids manually,
		Database db = new Database(testDbName);
		
		db.connect();
		
		checkDb(db);
		
		return db;
	}
	
	/**
	 * Retrieves total number of records in database table
	 * @param db Database
	 * @param tableName Table name
	 * @return value greater or equal zero if succeeded, -1 otherwise
	 */
	protected long countRecords(Database db, String tableName){

		
		checkDb(db);
		checkNullOrEmptyString(tableName);
		
		return db.countRecords(tableName);
	}
	
	/**
	 * Truncates table (e.g. deletes all records from it)
	 * @param db Database
	 * @param tableName Table name
	 * @return true if succeded, false otherwise
	 */
	protected boolean truncateTable(Database db, String tableName){ 
		checkDb(db);
		checkNullOrEmptyString(tableName);
		
		return db.truncateTable(tableName);
	}
	
	protected void checkNullOrEmptyString(String str){
		assertNotNull(str);
		assertEquals(str.length() > 0, true);
	}
	
	protected void checkDb(Database db){
		assertEquals(db != null, true);
		assertEquals(db.isConnected(), true);
	}
}
