package syslog;

import java.io.FileNotFoundException;
import java.util.List;

import syslog.moc.LogRotateStrategyMoc;
import utils.StackTraceUtil;

import base.DatabaseBaseTest;
import database.Database;
import database.DatabaseRecord;
import domain.syslog.SyslogSession;
import exceptions.DatabaseNotConnectedException;

public class SysLogTest extends DatabaseBaseTest {

	public void testSysLog() throws NullPointerException, FileNotFoundException, IllegalArgumentException, DatabaseNotConnectedException {
		Database db = prepareDatabase();
		LogRotateStrategyMoc logRotate = new LogRotateStrategyMoc();
		SysLog log = new SysLog(db,logRotate,saveLogsTimeout,logRotateTimeout);
		
		assertNotNull(log);
		assertEquals(log.getLogRotateTimeout(), logRotateTimeout);
		assertEquals(log.getSaveLogsTimeout(), saveLogsTimeout);
		
		// test invalid timeouts
		try {
			log = new SysLog(db,logRotate,0,logRotateTimeout);
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}
		
		try {
			log = new SysLog(db,logRotate,saveLogsTimeout,0);
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}
		
		try {
			log = new SysLog(db,logRotate,-10,logRotateTimeout);
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}
		
		try {
			log = new SysLog(db,logRotate,saveLogsTimeout,-10);
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}
		
		// test invalid database
		db.disconnect();
		try {
			log = new SysLog(db,logRotate,saveLogsTimeout,logRotateTimeout);
		} catch (Exception e) {
			assertTrue(e instanceof DatabaseNotConnectedException);
		}
		
		//test null-pointers
		try {
			log = new SysLog(null,logRotate,saveLogsTimeout,logRotateTimeout);
		} catch (Exception e) {
			assertTrue(e instanceof NullPointerException);
		}
		
		db.connect();
		try {
			log = new SysLog(db,null,saveLogsTimeout,logRotateTimeout);
		} catch (Exception e) {
			assertTrue(e instanceof NullPointerException);
		}
		db.disconnect();

	}

	public void testGetSaveLogsTimeout() throws NullPointerException, FileNotFoundException {
		Database db =prepareDatabase();
		
		SysLog log = prepareSyslog(db);
		
		assertNotNull(log);
		
		db.disconnect();
		
		assertEquals(log.getSaveLogsTimeout(), saveLogsTimeout);
	}

	public void testGetLogRotateTimeout() throws NullPointerException, FileNotFoundException {
		Database db = prepareDatabase();
		
		SysLog log = prepareSyslog(db);
		
		assertNotNull(log);
		
		assertEquals(log.getLogRotateTimeout(), logRotateTimeout);
		
		db.disconnect();
	}

	public void testGetCurrentSession() throws NullPointerException, FileNotFoundException {
		Database db = prepareDatabase();
		
		cleanupSyslogTables(db);
		
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

	public void testStartNewSession() throws NullPointerException, FileNotFoundException {
		Database db = prepareDatabase();
		SysLog log = prepareSyslog(db);
		
		cleanupSyslogTables(db);
		
		assertNotNull(log);
		
		assertNull(log.getCurrentSession());
		
		assertTrue(log.startNewSession());
		SyslogSession session = log.getCurrentSession();
		
		assertNotNull(session);
		assertTrue(session.isPersistent());
		assertTrue(!session.isClosed());
		
		//verify using database
		assertEquals(db.countRecords("syslog_sessions"), 1);
		assertTrue(!verifySessionClosedDb(db, session));
		
		//start new session and verify that old one is closed
		assertTrue( log.startNewSession());
		
		SyslogSession session2 = log.getCurrentSession();
		assertTrue(session2.isPersistent());
		assertTrue(!session2.isClosed());
		assertTrue(session.isClosed());
		
		assertEquals(db.countRecords("syslog_sessions"), 2);
		assertTrue(verifySessionClosedDb(db, session));
		assertTrue(!verifySessionClosedDb(db, session2));
		
		
		db.disconnect();
		
	}

	public void testStart() throws NullPointerException, FileNotFoundException, InterruptedException{
		Database db = prepareDatabase();
		
		SysLog log = prepareSyslog(db);
		
		assertNotNull(log);
		assertNull("Must return null, since no thread has been created yet", log.getThreadState());
		
		cleanupSyslogTables(db);
		
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
	
	public void testStop(){
		fail("Not implemented");
	}
	
	public void testRestart(){
		fail("Not yet implemented");
	}
	
	public void testDequeueAllMessages() {
		fail("Not yet implemented");
	}
	
	public void testPutMessage(){
		fail("Not yet implemented");
	}
	
	public void testFlushCache(){
		fail("Not yet implemented");
	}
	
	public void testEnd(){
		assertTrue(true);
	}

	private SysLog prepareSyslog(Database db){
		SysLog log = null;
		try {
			LogRotateStrategyMoc logRotate = new LogRotateStrategyMoc();
			log = new SysLog(db, logRotate, saveLogsTimeout,
					logRotateTimeout);

			assertNotNull(log);
			assertEquals(log.getLogRotateTimeout(), logRotateTimeout);
			assertEquals(log.getSaveLogsTimeout(), saveLogsTimeout);
		} catch (Exception e) {
			fail(StackTraceUtil.toString(e));
		}
		return log;
	}
	
	
	private boolean cleanupSyslogTables(Database db){ // TODO:
		boolean result = false;
		
		try {
			assertTrue( truncateTable(db, "syslog") );
			assertTrue( truncateTable(db, "syslog_sessions") );
			assertTrue( truncateTable(db, "syslog_categories") );
			assertTrue( truncateTable(db, "syslog_types") );
			assertTrue( truncateTable(db, "syslog_senders") );
		} catch (Exception e) {
			fail(StackTraceUtil.toString(e));
		}
		
		return result;
	}
	
	/**
	 * Counts syslog records in database and verifies that there are at least one
	 * session in db table
	 * @param db
	 * @return True if there is at least one syslog session in db, false otherwise
	 */
	private boolean verifySessionInserted(Database db){
		boolean result = false;
		
		try {
			result = db.countRecords("syslog_sessions") > 0;
		} catch (Exception e) {
			fail(StackTraceUtil.toString(e));
		}
		
		return result;
	}
	
	/**
	 * Retrieves last syslog session from database and verifies that it is closed.
	 * By 'last' we mean session with latest start date 
	 * @param db
	 * @return True if last session is closed, false otherwise
	 */
	private boolean verifySessionClosedDb(Database db, SyslogSession session){
		boolean result = false;
			
		try {
			if (session != null && session.isPersistent()){
				long sessionID = session.getID();
				
				List<DatabaseRecord> allSessions = 
					db.getRecords("syslog_sessions");
				
				boolean found = false;
				DatabaseRecord foundRecord = null;
				
				for (DatabaseRecord r : allSessions){
					if (r.getLong("id") == sessionID){
						found = true;
						foundRecord = r;
						break;
					}
				}
				
				if (found){
					//verify end date
				}
			}
		} catch (Exception e) {
			fail(StackTraceUtil.toString(e));
		}
		
		return result;
	}
	
	static final long saveLogsTimeout = 60000;
	static final long logRotateTimeout = 360000;
}
