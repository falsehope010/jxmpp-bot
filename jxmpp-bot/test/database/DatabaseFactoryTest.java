package database;
import java.io.FileNotFoundException;

import utils.RandomUtils;
import database.Database;
import database.DatabaseFactory;
import junit.framework.TestCase;


public class DatabaseFactoryTest extends TestCase {
	
	final String dbName = "test_db";

	public void testDatabaseFactory() throws FileNotFoundException, NullPointerException {
		
		DatabaseFactory factory = null;
		
		try {
			factory = new DatabaseFactory(null);
		} catch (Exception e) {
			assertTrue(e instanceof NullPointerException);
		}
		
		try {
			String randomFileName = RandomUtils.getRandomString(128);
			factory = new DatabaseFactory(randomFileName);
		} catch (Exception e) {
			assertTrue(e instanceof FileNotFoundException);
		}
		
		factory = new DatabaseFactory(dbName);
		
		assertNotNull(factory);
	}

	public void testCreateDatabase() throws FileNotFoundException, NullPointerException {
		DatabaseFactory factory = new DatabaseFactory(dbName);
		
		Database db = factory.createDatabase();
		
		assertNotNull(db);
	}

}
