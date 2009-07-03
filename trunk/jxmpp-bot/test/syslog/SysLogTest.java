package syslog;

import java.io.FileNotFoundException;
import java.lang.Thread.State;
import java.util.Date;
import java.util.List;

import syslog.moc.LogRotateStrategyMoc;
import utils.StackTraceUtil;
import base.DatabaseBaseTest;
import database.Database;
import database.DatabaseRecord;
import domain.syslog.Message;
import domain.syslog.SyslogSession;
import exceptions.DatabaseNotConnectedException;
import exceptions.SessionNotStartedException;

public class SysLogTest extends DatabaseBaseTest {

	public void testSysLog() throws NullPointerException,
			FileNotFoundException, IllegalArgumentException,
			DatabaseNotConnectedException {
		Database db = prepareDatabase();
		LogRotateStrategyMoc logRotate = new LogRotateStrategyMoc();
		SysLog log = new SysLog(db, logRotate, saveLogsTimeout,
				logRotateTimeout);

		assertNotNull(log);
		assertEquals(log.getLogRotateTimeout(), logRotateTimeout);
		assertEquals(log.getSaveLogsTimeout(), saveLogsTimeout);

		// test invalid timeouts
		try {
			log = new SysLog(db, logRotate, 0, logRotateTimeout);
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}

		try {
			log = new SysLog(db, logRotate, saveLogsTimeout, 0);
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}

		try {
			log = new SysLog(db, logRotate, -10, logRotateTimeout);
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}

		try {
			log = new SysLog(db, logRotate, saveLogsTimeout, -10);
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}

		// test invalid database
		db.disconnect();
		try {
			log = new SysLog(db, logRotate, saveLogsTimeout, logRotateTimeout);
		} catch (Exception e) {
			assertTrue(e instanceof DatabaseNotConnectedException);
		}

		// test null-pointers
		try {
			log = new SysLog(null, logRotate, saveLogsTimeout, logRotateTimeout);
		} catch (Exception e) {
			assertTrue(e instanceof NullPointerException);
		}

		db.connect();
		try {
			log = new SysLog(db, null, saveLogsTimeout, logRotateTimeout);
		} catch (Exception e) {
			assertTrue(e instanceof NullPointerException);
		}
		db.disconnect();

	}

	public void testGetSaveLogsTimeout() throws NullPointerException,
			FileNotFoundException {
		Database db = prepareDatabase();

		SysLog log = prepareSyslog(db);

		assertNotNull(log);

		db.disconnect();

		assertEquals(log.getSaveLogsTimeout(), saveLogsTimeout);
	}

	public void testGetLogRotateTimeout() throws NullPointerException,
			FileNotFoundException {
		Database db = prepareDatabase();

		SysLog log = prepareSyslog(db);

		assertNotNull(log);

		assertEquals(log.getLogRotateTimeout(), logRotateTimeout);

		db.disconnect();
	}

	public void testGetCurrentSession() throws NullPointerException,
			FileNotFoundException {
		Database db = prepareDatabase();

		assertTrue(cleanupSyslogTables(db));

		SysLog log = prepareSyslog(db);

		assertNotNull(log);

		// syslog not running, current session should be null
		assertFalse(log.isRunning());
		assertNull(log.getCurrentSession());
		assertFalse(verifySessionInserted(db));

		assertTrue(log.startNewSession());
		assertTrue(verifySessionInserted(db));

		db.disconnect();
	}

	public void testStartNewSession() throws NullPointerException,
			FileNotFoundException {
		Database db = prepareDatabase();
		SysLog log = prepareSyslog(db);

		assertTrue(cleanupSyslogTables(db));

		assertNotNull(log);

		assertNull(log.getCurrentSession());

		assertTrue(log.startNewSession());
		SyslogSession session = log.getCurrentSession();

		assertNotNull(session);
		assertTrue(session.isPersistent());
		assertTrue(!session.isClosed());

		// verify using database
		assertEquals(db.countRecords("syslog_sessions"), 1);
		assertTrue(!verifySessionClosedDb(db, session));

		// start new session and verify that old one is closed
		assertTrue(log.startNewSession());

		SyslogSession session2 = log.getCurrentSession();
		assertTrue(session2.isPersistent());
		assertTrue(!session2.isClosed());
		assertTrue(session.isClosed());

		assertEquals(db.countRecords("syslog_sessions"), 2);
		assertTrue(verifySessionClosedDb(db, session));
		assertTrue(!verifySessionClosedDb(db, session2));

		db.disconnect();

	}

	public void testStart() throws NullPointerException, FileNotFoundException,
			InterruptedException {
		Database db = prepareDatabase();

		SysLog log = prepareSyslog(db);

		assertNotNull(log);
		assertNull("Must return null, since no thread has been created yet",
				log.getThreadState());

		assertTrue(cleanupSyslogTables(db));

		log.start();

		Thread.sleep(500);

		assertNotNull(log.getThreadState());
		assertNotSame(log.getThreadState(), Thread.State.TERMINATED);

		assertTrue("Syslog should be running", log.isRunning());
		assertFalse("Terminate must be set to false", log.getTerminate());

		Thread.sleep(500);

		assertTrue(verifySessionInserted(db));

		log.stop();

		db.disconnect();
	}

	public void testStop() throws NullPointerException, FileNotFoundException,
			InterruptedException {
		Database db = prepareDatabase();

		assertTrue(cleanupSyslogTables(db));

		SysLog log = prepareSyslog(db);

		assertNotNull(log);
		assertNull("Must return null, since no thread has been created yet",
				log.getThreadState());

		log.start();

		Thread.sleep(500);

		assertNotNull(log.getThreadState());
		assertNotSame(log.getThreadState(), Thread.State.TERMINATED);

		assertTrue("Syslog should be running", log.isRunning());
		assertFalse("Terminate must be set to false", log.getTerminate());

		Thread.sleep(500);

		assertTrue(verifySessionInserted(db));

		log.stop();

		assertTrue(log.getTerminate());

		Thread.sleep(1000);

		assertFalse(log.isRunning());
		assertTrue(log.getThreadState() == State.TERMINATED);

		// verify last session was closed
		SyslogSession session = log.getCurrentSession();

		assertNotNull(session);
		assertTrue("Session must be closed, since we've stopped syslog",
				session.isClosed());
		assertEquals(db.countRecords("syslog_sessions") == 1, true);
		assertTrue(verifySessionClosedDb(db, session));

		db.disconnect();
	}

	public void testRestart() throws NullPointerException,
			FileNotFoundException, InterruptedException {
		// start-stop 5-10 times and verify sessions mapping

		final int iterations = 5;

		Database db = prepareDatabase();
		SysLog log = prepareSyslog(db);

		assertTrue(cleanupSyslogTables(db));

		for (int i = 0; i < iterations; ++i) {
			assertTrue(log.start());

			Thread.sleep(200);

			assertTrue(log.isRunning());
			assertNotNull(log.getThreadState());
			assertNotSame(log.getThreadState(), Thread.State.TERMINATED);

			log.stop();

			Thread.sleep(1000);

			assertFalse(log.isRunning());
			assertTrue(log.getThreadState() == State.TERMINATED);
		}

		// verify sessions count and that they all are closed
		assertEquals(db.countRecords("syslog_sessions"), iterations);
		assertTrue(verifyAllSessionsClosed(db));

		db.disconnect();
	}

	public void testDequeueAllMessages() throws NullPointerException,
			FileNotFoundException {
		final int itemsCount = 1000;
		String categoryName = "category";
		String senderName = "sender";
		String typeName = "type";

		Database db = prepareDatabase();
		SysLog log = prepareSyslog(db);
		assertTrue(log.startNewSession());

		assertTrue(fillCache(log, itemsCount, categoryName, senderName,
				typeName));

		// test items added
		List<Message> messages = log.getCachedMessages(itemsCount);
		assertEquals(log.getCacheSize(), 0);
		assertNotNull(messages);
		assertEquals(messages.size(), itemsCount);

		// test messages one by one
		for (int i = 0; i < itemsCount; ++i) {
			Message msg = messages.get(i);
			assertEquals(Integer.toString(i), msg.getText());
			assertEquals(msg.getCategoryName(), categoryName);
			assertEquals(msg.getMessageTypeName(), typeName);
			assertEquals(msg.getSenderName(), senderName);
		}

		db.disconnect();
	}

	public void testPutMessage() throws NullPointerException,
			FileNotFoundException {

		Database db = prepareDatabase();
		SysLog log = prepareSyslog(db);

		String categoryName = "category";
		String senderName = "sender";
		String typeName = "type";
		String msgText = "testMessage";

		// put message without starting session
		try {
			log.putMessage(msgText, senderName, categoryName, typeName);
		} catch (Exception e) {
			assertTrue(e instanceof SessionNotStartedException);
		}

		assertTrue(log.startNewSession());

		// put message with invalid sender/category/type
		try {
			log.putMessage(null, senderName, categoryName, typeName);
		} catch (Exception e) {

			assertTrue(e instanceof NullPointerException);
		}
		try {
			log.putMessage("", senderName, categoryName, typeName);
		} catch (Exception e) {

			assertTrue(e instanceof IllegalArgumentException);
		}
		try {
			log.putMessage(msgText, null, categoryName, typeName);
		} catch (Exception e) {

			assertTrue(e instanceof NullPointerException);
		}
		try {
			log.putMessage(msgText, "", categoryName, typeName);
		} catch (Exception e) {

			assertTrue(e instanceof IllegalArgumentException);
		}
		try {
			log.putMessage(msgText, senderName, null, typeName);
		} catch (Exception e) {

			assertTrue(e instanceof NullPointerException);
		}
		try {
			log.putMessage(msgText, senderName, "", typeName);
		} catch (Exception e) {

			assertTrue(e instanceof IllegalArgumentException);
		}
		try {
			log.putMessage(msgText, senderName, categoryName, null);
		} catch (Exception e) {

			assertTrue(e instanceof NullPointerException);
		}
		try {
			log.putMessage(msgText, senderName, categoryName, "");
		} catch (Exception e) {

			assertTrue(e instanceof IllegalArgumentException);
		}

		// put valid message and check whether it's in cache
		try {
			for (int j = 0; j < 1000; ++j) {
				int itemsCount = 1000;

				for (int i = 0; i < itemsCount; ++i) {
					assertTrue(log.putMessage(msgText, senderName,
							categoryName, typeName));
				}

				List<Message> messages = log.getCachedMessages(itemsCount * 2);
				assertTrue(log.getCacheSize() == 0);
				assertNotNull(messages);
				assertEquals(messages.size(), itemsCount);

				for (Message m : messages) {
					assertEquals(m.getText(), msgText);
					assertEquals(m.getCategoryName(), categoryName);
					assertEquals(m.getMessageTypeName(), typeName);
					assertEquals(m.getSenderName(), senderName);
				}
			}
		} catch (Exception e) {
			fail(StackTraceUtil.toString(e));
		}

		db.disconnect();
	}

	public void testFlushCache() throws NullPointerException,
			FileNotFoundException {
		Database db = prepareDatabase();
		SysLog log = prepareSyslog(db);

		truncateTable(db, "syslog");

		String categoryName = "category";
		String senderName = "sender";
		String typeName = "type";
		String msgText = "testMessage";

		assertTrue(log.startNewSession());

		try {
			final int messagesCount = 100;
			for (int i = 0; i < messagesCount; ++i) {
				assertTrue(log.putMessage(msgText, senderName, categoryName,
						typeName));
			}
			assertTrue(log.flushCache());
			assertEquals(db.countRecords("syslog"), messagesCount);
		} catch (SessionNotStartedException e) {
			fail(StackTraceUtil.toString(e));
		}

		db.disconnect();
	}

	private boolean fillCache(SysLog log, int itemsCount, String categoryName,
			String senderName, String typeName) {
		boolean result = false;

		if (log != null && itemsCount > 0) {
			try {
				for (int i = 0; i < itemsCount; ++i) {
					assertTrue(log.putMessage(Integer.toString(i), senderName,
							categoryName, typeName));
				}

				// verify items count
				assertEquals(log.getCacheSize(), itemsCount);

				result = true;
			} catch (Exception e) {
				fail(StackTraceUtil.toString(e));
			}
		}

		return result;
	}

	private SysLog prepareSyslog(Database db) {
		SysLog log = null;
		try {
			LogRotateStrategyMoc logRotate = new LogRotateStrategyMoc();
			log = new SysLog(db, logRotate, saveLogsTimeout, logRotateTimeout);

			assertNotNull(log);
			assertEquals(log.getLogRotateTimeout(), logRotateTimeout);
			assertEquals(log.getSaveLogsTimeout(), saveLogsTimeout);
		} catch (Exception e) {
			fail(StackTraceUtil.toString(e));
		}
		return log;
	}

	private boolean cleanupSyslogTables(Database db) { // TODO:
		boolean result = false;

		try {
			assertTrue(truncateTable(db, "syslog"));
			assertTrue(truncateTable(db, "syslog_sessions"));
			assertTrue(truncateTable(db, "syslog_categories"));
			assertTrue(truncateTable(db, "syslog_types"));
			assertTrue(truncateTable(db, "syslog_senders"));

			result = true;
		} catch (Exception e) {
			fail(StackTraceUtil.toString(e));
		}

		return result;
	}

	/**
	 * Counts syslog records in database and verifies that there are at least
	 * one session in db table
	 * 
	 * @param db
	 * @return True if there is at least one syslog session in db, false
	 *         otherwise
	 */
	private boolean verifySessionInserted(Database db) {
		boolean result = false;

		try {
			result = db.countRecords("syslog_sessions") > 0;
		} catch (Exception e) {
			fail(StackTraceUtil.toString(e));
		}

		return result;
	}

	/**
	 * Retrieves last syslog session from database and verifies that it is
	 * closed. By 'last' we mean session with latest start date
	 * 
	 * @param db
	 * @return True if last session is closed, false otherwise
	 */
	private boolean verifySessionClosedDb(Database db, SyslogSession session) {
		boolean result = false;

		try {
			if (session != null && session.isPersistent()) {
				long sessionID = session.getID();

				List<DatabaseRecord> allSessions = db
						.getRecords("syslog_sessions");

				boolean found = false;
				DatabaseRecord foundRecord = null;

				for (DatabaseRecord r : allSessions) {
					if (r.getLong("id") == sessionID) {
						found = true;
						foundRecord = r;
						break;
					}
				}

				if (found) {
					assertFalse(foundRecord.isNull("start_date"));
					Date startDate = foundRecord.getDate("start_date");

					if (!foundRecord.isNull("end_date")) {
						Date endDate = foundRecord.getDate("end_date");
						System.out.println(startDate);
						System.out.println(endDate);
						assertFalse(endDate.before(startDate));

						result = true;
					}
				}
			}
		} catch (Exception e) {
			fail(StackTraceUtil.toString(e));
		}

		return result;
	}

	/**
	 * Checks whether all sessions in database are closed. If no sessions in
	 * database considers that all sessions are closed
	 * 
	 * @param db
	 * @return True if all sessions are closed, false otherwise
	 */
	private boolean verifyAllSessionsClosed(Database db) {
		boolean result = false;

		try {
			List<DatabaseRecord> allSessions = db.getRecords("syslog_sessions");
			assertNotNull(allSessions);

			if (allSessions.size() == 0) {
				result = true; // no sessions to verify
			} else {
				boolean ok = true;
				for (DatabaseRecord r : allSessions) {
					if (r.isNull("end_date")) {
						ok = false;
						break;
					}
				}
				result = ok;
			}
		} catch (Exception e) {
			fail(StackTraceUtil.toString(e));
		}

		return result;
	}

	static final long saveLogsTimeout = 60000;
	static final long logRotateTimeout = 360000;
}
