package syslog.rotate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;

import mappers.SyslogMessageMapper;

import org.junit.Test;

import utils.StackTraceUtil;
import base.DatabaseBaseTest;
import database.Database;
import exceptions.DatabaseNotConnectedException;

public class CountdownLogRotateStrategyTest extends DatabaseBaseTest {

	@Test
	public void testRotate() throws NullPointerException,
			FileNotFoundException, DatabaseNotConnectedException {
		CountdownLogRotateStrategy s = prepareStrategy();
		assertNotNull(s);

		// prepare database for test
		Database db = prepareDatabase();
		assertTrue(truncateTable(db, "syslog"));
		SyslogMessageMapper mapper = new SyslogMessageMapper(db);
		assertTrue(insertTestSyslogMessages(db, mapper, keptMessagesCount * 2));
		assertEquals(countRecords(db, "syslog"), keptMessagesCount * 2);

		// test it
		assertTrue(s.rotate());
		assertEquals(countRecords(db, "syslog"), keptMessagesCount);

		db.disconnect();
	}

	@Test
	public void testCountdownLogRotateStrategy() {
		CountdownLogRotateStrategy s = prepareStrategy();

		assertNotNull(s);
	}

	@Test
	public void testCreateWithNullDatabase() {
		try {
			@SuppressWarnings("unused")
			CountdownLogRotateStrategy s = new CountdownLogRotateStrategy(null,
					10, 1000);
		} catch (Exception e) {
			assertTrue(e instanceof NullPointerException);
		}
	}

	@Test
	public void testCreateWithNotConnectedDatabase()
			throws NullPointerException, FileNotFoundException {
		Database db = prepareDatabase();
		db.disconnect();

		try {
			@SuppressWarnings("unused")
			CountdownLogRotateStrategy s = new CountdownLogRotateStrategy(db,
					10, 1000);
		} catch (Exception e) {
			assertTrue(e instanceof DatabaseNotConnectedException);
		}
	}

	@Test
	public void testConstructorInvalidKeptCountPassed()
			throws NullPointerException, DatabaseNotConnectedException,
			FileNotFoundException {
		Database db = prepareDatabase();

		final long invalidKeptCount = -10;
		final long zeroKeptCount = 0;

		CountdownLogRotateStrategy s = new CountdownLogRotateStrategy(db,
				invalidKeptCount, 1000);

		assertEquals(s.getKeptMessagesCount(),
				CountdownLogRotateStrategy.DEFAULT_MESSAGES_KEPT);

		s = new CountdownLogRotateStrategy(db, zeroKeptCount, 1000);

		assertEquals(s.getKeptMessagesCount(),
				CountdownLogRotateStrategy.DEFAULT_MESSAGES_KEPT);

	}

	private CountdownLogRotateStrategy prepareStrategy() {
		CountdownLogRotateStrategy result = null;

		try {
			Database db = prepareDatabase();
			result = new CountdownLogRotateStrategy(db, keptMessagesCount, 1000);
		} catch (Exception e) {
			fail(StackTraceUtil.toString(e));
		}

		return result;
	}

	final static int keptMessagesCount = 100;
}
