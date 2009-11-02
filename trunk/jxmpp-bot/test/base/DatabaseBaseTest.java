package base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;

import mappers.SyslogMessageMapper;
import mappers.SyslogSessionMapper;
import utils.StackTraceUtil;
import database.Database;
import database.DatabaseFactory;
import domain.syslog.Message;
import domain.syslog.SyslogSession;

public class DatabaseBaseTest {

    public static final String testDbName = "test/test_db";

    /**
     * Creates Database instance using test_db file and opens connection
     * 
     * @return Valid, clear and ready-to-use database if succeded
     * @throws NullPointerException
     *             If test_database name is invalid
     * @throws FileNotFoundException
     *             If test_database file not found
     */
    protected Database prepareDatabase() throws NullPointerException,
	    FileNotFoundException {
	// Connect to test database, clear all users and jids manually,
	DatabaseFactory factory = new DatabaseFactory(testDbName);

	Database db = factory.createDatabase();// new Database(testDbName);

	db.connect();

	checkDb(db);

	return db;
    }

    /**
     * Retrieves total number of records in database table
     * 
     * @param db
     *            Database
     * @param tableName
     *            Table name
     * @return value greater or equal zero if succeeded, -1 otherwise
     */
    protected long countRecords(Database db, String tableName) {

	checkDb(db);
	checkNullOrEmptyString(tableName);

	return db.countRecords(tableName);
    }

    /**
     * Truncates table (e.g. deletes all records from it)
     * 
     * @param db
     *            Database
     * @param tableName
     *            Table name
     * @return true if succeded, false otherwise
     */
    protected boolean truncateTable(Database db, String tableName) {
	checkDb(db);
	checkNullOrEmptyString(tableName);

	return db.truncateTable(tableName);
    }

    protected void checkNullOrEmptyString(String str) {
	assertNotNull(str);
	assertEquals(str.length() > 0, true);
    }

    protected void checkDb(Database db) {
	assertNotNull(db);
	assertTrue(db.isConnected());
    }

    /**
     * Inserts specified count of syslog messages into database
     * 
     * @param db
     *            Database which will be used to create session and assertation
     * @param mapper
     *            Mapper which will be used to insert messages into database
     * @param recordsCount
     *            Total number of records that should be inserted into database
     * @return true if succeeded, false otherwise
     */
    protected boolean insertTestSyslogMessages(Database db,
	    SyslogMessageMapper mapper, int recordsCount) {
	boolean result = false;

	if (recordsCount >= 0) {
	    try {
		String text = "testMessage";
		String category = "category";
		String sender = "sender";
		String type = "type";

		SyslogSessionMapper sessionMapper = new SyslogSessionMapper(db);
		SyslogSession session = new SyslogSession();
		assertTrue(sessionMapper.save(session));

		assertTrue(db.setAutoCommit(false));
		for (int i = 0; i < recordsCount; ++i) {
		    Message msg = new Message(text, category, type, sender,
			    session);
		    assertTrue(mapper.save(msg));
		}
		assertTrue(db.commit());
		assertTrue(db.setAutoCommit(true));

		// assertEquals(db.countRecords("syslog"), recordsCount);

		result = true;
	    } catch (Exception e) {
		fail(StackTraceUtil.toString(e));
	    }
	}

	return result;
    }
}