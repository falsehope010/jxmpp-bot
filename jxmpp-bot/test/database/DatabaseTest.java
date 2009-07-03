package database;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import mappers.SyslogSessionMapper;
import utils.RandomUtils;
import utils.StackTraceUtil;
import base.DatabaseBaseTest;
import domain.syslog.SyslogSession;

public class DatabaseTest extends DatabaseBaseTest {

	public void testSetAutoCommit() throws NullPointerException,
			FileNotFoundException {

		Database db = prepareDatabase();

		assertTrue(db.isConnected());

		assertTrue("By default should return true", getAutoCommit(db));

		assertTrue(db.setAutoCommit(false));
		assertFalse("Value should be changed from true to false",
				getAutoCommit(db));

		assertTrue("Set same value, connected state", db.setAutoCommit(false));
		assertFalse("Value shouldn't be changed", getAutoCommit(db));

		boolean currentValueBeforeDisconnect = getAutoCommit(db);

		db.disconnect();

		assertFalse(db.isConnected());

		assertFalse("Datbase disconnected, value change must fail", db
				.setAutoCommit(!currentValueBeforeDisconnect));
		assertFalse("Set same value, disconnected state", db
				.setAutoCommit(!currentValueBeforeDisconnect));

		assertFalse(db.setAutoCommit(currentValueBeforeDisconnect));
		assertFalse(db.setAutoCommit(currentValueBeforeDisconnect));

		db.disconnect();
	}

	public void testGetAutoCommit() throws NullPointerException,
			FileNotFoundException {
		Database db = prepareDatabase();

		/*
		 * 1. Get current auto-commit mode, by default true on connected db 2.
		 * Change autocommit and validate changed 3. Disconnect db and validate
		 * exceptions
		 */

		assertTrue(db.isConnected());

		boolean defaultValue = true;

		for (int i = 0; i < 10; ++i)
			assertEquals(getAutoCommit(db), defaultValue);

		assertTrue(db.setAutoCommit(!defaultValue));

		for (int i = 0; i < 10; ++i)
			assertEquals(getAutoCommit(db), !defaultValue);

		db.disconnect();

		assertFalse(db.isConnected());

		try {
			assertFalse(db.getAutoCommit());
		} catch (Exception e) {
			assertTrue(e instanceof SQLException);
			assertEquals(e.getMessage(), "No connection to database");
		}
	}

	public void testCommitAutocommitEnabled() throws NullPointerException,
			FileNotFoundException {
		Database db = prepareDatabase();

		truncateTable(db, tableName);

		// by default auto-commit is set to true
		assertTrue(getAutoCommit(db));

		// insert one record, check commit failed and verify record has been
		// actually inserted
		SyslogSessionMapper mapper = getMapper(db);

		SyslogSession session = new SyslogSession();
		assertTrue(mapper.save(session));

		assertEquals(db.countRecords(tableName), 1);

		assertFalse("Commit must fail in auto-commit enabled mode", db.commit());

		db.disconnect();
	}

	public void testCommitAutocommitDisabled() throws NullPointerException,
			FileNotFoundException {
		Database db = prepareDatabase();

		truncateTable(db, tableName);
		assertEquals(db.countRecords(tableName), 0);

		assertTrue(getAutoCommit(db));
		assertTrue(db.setAutoCommit(false));

		SyslogSessionMapper mapper = getMapper(db);

		final int recordsCount = 100;

		for (int i = 0; i < recordsCount; ++i) {
			SyslogSession session = new SyslogSession();
			assertTrue(mapper.save(session));
		}

		// no records should be in database
		Database db2 = prepareDatabase();

		assertEquals(db2.countRecords(tableName), 0);

		assertTrue(db.commit());

		assertEquals(db2.countRecords(tableName), recordsCount);

		db.disconnect();

		db2.disconnect();
	}

	public void testRollbackAutocommitEnabled() throws NullPointerException,
			FileNotFoundException {
		Database db = prepareDatabase();

		truncateTable(db, tableName);

		assertTrue(getAutoCommit(db));

		// insert record, verify that it is inserted and call rollback
		SyslogSessionMapper mapper = getMapper(db);
		SyslogSession session = new SyslogSession();
		assertTrue(mapper.save(session));

		assertEquals(db.countRecords(tableName), 1);

		assertFalse(db.rollback());

		db.disconnect();
	}

	public void testRollbackAutocommitDisabled() throws NullPointerException,
			FileNotFoundException {
		Database db = prepareDatabase();

		assertTrue(db.truncateTable(tableName));

		// check auto-commit mode is disabled
		assertTrue(getAutoCommit(db));
		assertTrue(db.setAutoCommit(false));
		assertFalse(getAutoCommit(db));

		// insert several records into db and rollback
		SyslogSessionMapper mapper = getMapper(db);

		final int recordsCount = 100;

		for (int i = 0; i < recordsCount; ++i) {
			SyslogSession session = new SyslogSession();
			assertTrue(mapper.save(session));
		}

		// verify no records has been inserted since auto-commit is disabled
		Database db2 = prepareDatabase();
		assertEquals(db2.countRecords(tableName), 0);

		assertTrue(db.rollback());

		assertEquals(db2.countRecords(tableName), 0);

		db.disconnect();
		db2.disconnect();
	}

	public void testCountRecords() throws NullPointerException,
			FileNotFoundException {
		Database db = prepareDatabase();

		assertTrue(createTestTable(db));

		// insert several records
		final int recordsCount = 10;

		appendTestTable(db, recordsCount);

		// now check how many records has been inserted into test database table
		assertEquals(countRecordsSql(db, testTableName), recordsCount);

		assertTrue(dropTestTable(db));

		db.disconnect();
	}

	public void testTruncateTable() throws NullPointerException,
			FileNotFoundException {
		Database db = prepareDatabase();

		assertTrue(createTestTable(db));

		final int recordsCount = 10;

		appendTestTable(db, recordsCount);

		// now check how many records has been inserted into test database table
		assertEquals(countRecordsSql(db, testTableName), recordsCount);

		assertTrue(db.truncateTable(testTableName));

		assertEquals("Table is truncated, no records", countRecordsSql(db,
				testTableName), 0);

		db.disconnect();
	}

	public void testConnect() {

		// Also tests disconnect and isConnected()

		String nullDbName = null;
		String rndDbName = RandomUtils.getRandomString(1024);

		Database db = null;

		try {
			db = new Database(nullDbName);
		} catch (Exception e) {
			assertTrue(e instanceof NullPointerException);
		}

		try {
			db = new Database(rndDbName);
		} catch (Exception e) {
			assertTrue(e instanceof FileNotFoundException);
		}

		// create valid db
		try {
			db = new Database(testDbName);

			assertNotNull(db);
			assertFalse(db.isConnected());
		} catch (Exception e) {
			fail(StackTraceUtil.toString(e));
		}

		assertTrue(db.connect());
		assertTrue(db.isConnected());

		db.disconnect();

		assertFalse(db.isConnected());
	}

	public void testConnectNonDbFile() {
		File existingNonDbFile = findExistingNonDbFile();

		assertNotNull(existingNonDbFile);
		assertTrue(existingNonDbFile.exists());

		Database db = null;

		try {
			db = new Database(existingNonDbFile.toString());
			System.out.print(existingNonDbFile.toString());
			assertNotNull(db);
			assertFalse(db.isConnected());
		} catch (Exception e) {
			fail(StackTraceUtil.toString(e));
		}

		assertFalse(db.connected);
	}

	public void testLastInsertRowID() throws NullPointerException,
			FileNotFoundException {
		Database db = prepareDatabase();

		assertEquals("Database not connected", db.LastInsertRowID(), 0);

		db.disconnect();

		fail("Not implemented yet");
	}

	public void testGetConnection() {
		fail("Not implemented");
	}

	public void testGetDbFileSize() {
		fail("Not implemented");
	}

	public void testVacuum() {
		fail("Not implemented");
	}

	public void testSequenceExists() {
		fail("Not implemented");
	}

	public void testGetSequenceValue() {
		fail("Not implemented");
	}

	public void testSetSequenceValue() {
		fail("Not implemented");
	}

	public void testGetAttributeNames() {
		fail("Not implemented");
	}

	public void testGetRecords() {
		fail("Not implemented");
	}

	private boolean getAutoCommit(Database db) {
		boolean result = false;
		try {
			result = db.getAutoCommit();
		} catch (Exception e) {
			fail(StackTraceUtil.toString(e));
		}
		return result;
	}

	/**
	 * Creates new table in database.
	 * 
	 * @return True if succeded, false otherwise
	 */
	private boolean createTestTable(Database db) {
		boolean result = false;

		String createTableSql = "CREATE TABLE IF NOT EXISTS \"" + testTableName
				+ "\" (\"id\" INTEGER);";

		Statement st = null;

		try {
			Connection conn = db.getConnection();
			st = conn.createStatement();

			st.execute(createTableSql);

			result = true;
		} catch (Exception e) {
			fail(StackTraceUtil.toString(e));
		} finally {
			db.Cleanup(st);
		}

		return result;
	}

	/**
	 * Drop test table with.
	 * 
	 * @param db
	 * @return
	 */
	private boolean dropTestTable(Database db) {
		boolean result = false;

		String createTableSql = "DROP TABLE test_table;";

		Statement st = null;

		try {
			Connection conn = db.getConnection();
			st = conn.createStatement();

			st.execute(createTableSql);

			result = true;
		} catch (Exception e) {
			fail(StackTraceUtil.toString(e));
		} finally {
			db.Cleanup(st);
		}

		return result;
	}

	private boolean appendTestTable(Database db, int recordsCount) {
		boolean result = false;

		if (recordsCount >= 0) {
			PreparedStatement pr = null;
			try {
				Connection conn = db.getConnection();
				conn.setAutoCommit(false);
				pr = conn.prepareStatement("insert into " + testTableName
						+ "(id) values(?);");

				for (int i = 0; i < recordsCount; ++i) {
					pr.setInt(1, i);
					pr.executeUpdate();
				}

				conn.commit();
				conn.setAutoCommit(true);
			} catch (Exception e) {
				fail(StackTraceUtil.toString(e));
			} finally {
				db.Cleanup(pr);
			}
		}

		return result;
	}

	private long countRecordsSql(Database db, String tableName) {
		long result = -1;

		Statement st = null;
		ResultSet rs = null;

		try {
			Connection conn = db.getConnection();
			st = conn.createStatement();

			rs = st.executeQuery("select count(1) from " + testTableName + ";");

			if (rs.next()) {
				long tmpCnt = rs.getLong(1);

				if (tmpCnt >= 0) {
					result = tmpCnt;
				}
			}
		} catch (Exception e) {
			fail(StackTraceUtil.toString(e));
		} finally {
			db.Cleanup(st, rs);
		}

		return result;
	}

	private SyslogSessionMapper getMapper(Database db) {
		SyslogSessionMapper result = null;

		try {
			result = new SyslogSessionMapper(db);
		} catch (Exception e) {
			fail(StackTraceUtil.toString(e));
		}

		assertNotNull(result);

		return result;
	}

	private File findExistingNonDbFile() {
		File result = null;

		try {
			result = File.createTempFile("name", ".ext");
			result.deleteOnExit();
		} catch (Exception e) {
			fail(StackTraceUtil.toString(e));
		}

		return result;
	}

	final String tableName = "syslog_sessions";
	final String testTableName = "test_table";
}
