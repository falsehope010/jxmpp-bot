package database;

import java.io.FileNotFoundException;
import java.sql.SQLException;

import mappers.SyslogSessionMapper;
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

	private boolean getAutoCommit(Database db) {
		boolean result = false;
		try {
			result = db.getAutoCommit();
		} catch (Exception e) {
			fail(StackTraceUtil.toString(e));
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

	final String tableName = "syslog_sessions";
}
