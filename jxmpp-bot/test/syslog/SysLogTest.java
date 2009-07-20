package syslog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.lang.Thread.State;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.junit.Test;

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

    @Test
    public void testSysLog() throws NullPointerException,
	    FileNotFoundException, IllegalArgumentException,
	    DatabaseNotConnectedException {
	Database db = prepareDatabase();
	LogRotateStrategyMoc logRotate = new LogRotateStrategyMoc();
	SysLog log = new SysLog(db, logRotate, saveLogsTimeout);

	assertNotNull(log);
	assertEquals(log.getSaveLogsTimeout(), saveLogsTimeout);

	// test invalid timeouts
	try {
	    log = new SysLog(db, logRotate, 0);
	} catch (Exception e) {
	    assertTrue(e instanceof IllegalArgumentException);
	}

	try {
	    log = new SysLog(db, logRotate, -10);
	} catch (Exception e) {
	    assertTrue(e instanceof IllegalArgumentException);
	}

	// test invalid database
	db.disconnect();
	try {
	    log = new SysLog(db, logRotate, saveLogsTimeout);
	} catch (Exception e) {
	    assertTrue(e instanceof DatabaseNotConnectedException);
	}

	// test null-pointers
	try {
	    log = new SysLog(null, logRotate, saveLogsTimeout);
	} catch (Exception e) {
	    assertTrue(e instanceof NullPointerException);
	}

	db.connect();
	try {
	    log = new SysLog(db, null, saveLogsTimeout);
	} catch (Exception e) {
	    assertTrue(e instanceof NullPointerException);
	}
	db.disconnect();

    }

    @Test
    public void testGetSaveLogsTimeout() throws NullPointerException,
	    FileNotFoundException {
	Database db = prepareDatabase();

	SysLog log = prepareSyslog(db);

	assertNotNull(log);

	db.disconnect();

	assertEquals(log.getSaveLogsTimeout(), saveLogsTimeout);
    }

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
    public void testDequeueAllMessagesSmallPortions()
	    throws NullPointerException, FileNotFoundException {
	final int itemsCount = 15000;
	String categoryName = "category";
	String senderName = "sender";
	String typeName = "type";

	Database db = prepareDatabase();
	SysLog log = prepareSyslog(db);
	assertTrue(log.startNewSession());

	assertTrue(fillCache(log, itemsCount, categoryName, senderName,
		typeName));

	int maxIterations = 15000;
	int currentIteration = 0;
	int oldCapacity = 0;
	int newCapacity = 0;
	Random rnd = new Random();

	do {
	    ++currentIteration;
	    int getItemsCount = rnd.nextInt(100);

	    if (getItemsCount == 0)
		++getItemsCount;

	    oldCapacity = log.getCacheSize();

	    List<Message> messages = log.getCachedMessages(getItemsCount);

	    newCapacity = log.getCacheSize();

	    assertNotNull(messages);

	    if (oldCapacity >= getItemsCount) {
		assertEquals(messages.size(), getItemsCount);
	    }
	} while (currentIteration < maxIterations && newCapacity > 0);

	assertEquals(log.getCacheSize(), 0);

	db.disconnect();
    }

    @Test
    public void testPutMessage() throws NullPointerException,
	    FileNotFoundException {

	Database db = prepareDatabase();
	SysLog log = prepareSyslog(db);

	assertTrue(truncateTable(db, "syslog"));

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

	Database db2 = prepareDatabase();

	// put valid message and check whether it's in cache
	final int retries = 1000;
	try {
	    for (int j = 0; j < retries; ++j) {
		int itemsCount = 1000;

		for (int i = 0; i < itemsCount; ++i) {
		    assertTrue(log.putMessage(msgText, senderName,
			    categoryName, typeName));
		}

		// check no messages has been inserted into db
		assertEquals(countRecords(db2, "syslog"), 0);

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

		// check no messages has been inserted into db
		assertEquals(countRecords(db2, "syslog"), 0);
	    }
	} catch (Exception e) {
	    fail(StackTraceUtil.toString(e));
	}

	db.disconnect();
	db2.disconnect();
    }

    @Test
    public void testPutMessageSmallPortions() throws NullPointerException,
	    FileNotFoundException {
	Database db = prepareDatabase();
	SysLog log = prepareSyslog(db);

	assertTrue(truncateTable(db, "syslog"));

	assertTrue(log.startNewSession());

	final int retries = 1000;
	Random rnd = new Random();

	Database db2 = prepareDatabase();

	for (int i = 0; i < retries; ++i) {
	    int itemsCount = rnd.nextInt(250);
	    if (itemsCount == 0)
		++itemsCount;

	    // check no messages has been inserted into db
	    assertEquals(countRecords(db2, "syslog"), 0);

	    for (int j = 0; j < itemsCount; ++j) {
		try {
		    assertTrue(log.putMessage("testMessage", "testSender",
			    "testCategory", "testType"));
		} catch (SessionNotStartedException e) {
		    fail(StackTraceUtil.toString(e));
		}
	    }

	    assertEquals(log.getCacheSize(), itemsCount);

	    List<Message> messages = log.getCachedMessages(itemsCount);

	    assertNotNull(messages);
	    assertEquals(messages.size(), itemsCount);
	    assertEquals(log.getCacheSize(), 0);

	    // check no messages has been inserted into db
	    assertEquals(countRecords(db2, "syslog"), 0);

	}

	db.disconnect();
	db2.disconnect();
    }

    @Test
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

    @Test
    public void testFlushCacheSmallPortions() throws NullPointerException,
	    FileNotFoundException {

	Database db = prepareDatabase();
	SysLog log = prepareSyslog(db);

	truncateTable(db, "syslog");

	assertTrue(log.startNewSession());

	final int retries = 25;
	final int maxRecordsCount = SysLog.maxDequeueItems;
	int totalItemsCount = 0;
	Random rnd = new Random();
	Database db2 = prepareDatabase();

	for (int i = 0; i < retries; ++i) {
	    // put several messages, flush cache
	    int itemsCount = rnd.nextInt(maxRecordsCount);
	    if (itemsCount == 0)
		++itemsCount;

	    totalItemsCount += itemsCount;

	    for (int j = 0; j < itemsCount; ++j) {
		try {
		    assertTrue(log.putMessage("test", "sender", "category",
			    "type"));
		} catch (SessionNotStartedException e) {
		    fail(StackTraceUtil.toString(e));
		}
	    }

	    assertEquals(log.getCacheSize(), itemsCount);

	    assertTrue(log.flushCache());

	    assertEquals(0, log.getCacheSize());

	    assertEquals(countRecords(db2, "syslog"), totalItemsCount);
	}

	// clean test database, since many data has been inserted during test
	assertTrue(truncateTable(db2, "syslog"));
	assertTrue(db2.vacuum());

	db.disconnect();
	db2.disconnect();
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
	    log = new SysLog(db, logRotate, saveLogsTimeout);

	    assertNotNull(log);
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
    @SuppressWarnings("null")
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

}
