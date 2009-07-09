package database;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;

import org.junit.Test;

import utils.RandomUtils;

public class DatabaseFactoryTest {

    final String dbName = "test_db";

    @Test
    public void testDatabaseFactory() throws FileNotFoundException,
	    NullPointerException {

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

    @Test
    public void testCreateDatabase() throws FileNotFoundException,
	    NullPointerException {
	DatabaseFactory factory = new DatabaseFactory(dbName);

	Database db = factory.createDatabase();

	assertNotNull(db);
    }

}
