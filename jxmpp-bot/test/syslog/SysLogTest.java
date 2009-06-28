package syslog;

import java.io.FileNotFoundException;

import syslog.moc.LogRotateStrategyMoc;
import utils.StackTraceUtil;

import base.DatabaseBaseTest;
import database.Database;
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
		
		SysLog log = prepareSyslog(db);
		
		assertNotNull(log);
		
		
		// syslog not running, current session should be null
		assertFalse(log.isRunning());
		assertNull(log.getCurrentSession());
		
		
		compareSession(db);
		
		db.disconnect();
	}

	public void testStartNewSession() {
		fail("Not yet implemented"); // TODO
	}

	public void testDequeueAllMessages() {
		fail("Not yet implemented"); // TODO
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
	
	private void compareSession(Database db){
		cleanupSyslogTables(db);
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
	
	static final long saveLogsTimeout = 60000;
	static final long logRotateTimeout = 360000;
}
