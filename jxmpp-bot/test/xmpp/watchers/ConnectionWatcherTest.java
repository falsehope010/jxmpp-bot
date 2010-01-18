package xmpp.watchers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import xmpp.watchers.ConnectionWatcher;

public class ConnectionWatcherTest {

    @Test(expected = NullPointerException.class)
    public void testCreateNullConnection() {
	LogMock log = new LogMock();
	ConnectionWatcher watcher = new ConnectionWatcher(null, log, 10);
	assertNull(watcher);
    }

    @Test(expected = NullPointerException.class)
    public void testCreateNullLog() {
	ConnectionMock conn = new ConnectionMock();
	ConnectionWatcher watcher = new ConnectionWatcher(conn, null, 10);
	assertNull(watcher);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNegativePollTimeout() {
	ConnectionMock conn = new ConnectionMock();
	LogMock log = new LogMock();
	ConnectionWatcher watcher = new ConnectionWatcher(conn, log, -5);
	assertNull(watcher);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateZeroPollTimeout() {
	ConnectionMock conn = new ConnectionMock();
	LogMock log = new LogMock();
	ConnectionWatcher watcher = new ConnectionWatcher(conn, log, 0);
	assertNull(watcher);
    }

    @Test
    public void testCreate() {
	ConnectionMock conn = new ConnectionMock();
	LogMock log = new LogMock();
	ConnectionWatcher watcher = new ConnectionWatcher(conn, log, 10000);
	assertNotNull(watcher);
	assertFalse(watcher.isAlive());
    }

    @Test
    public void testCheckActivityAlive() {
	ConnectionMock conn = new ConnectionMock();
	LogMock log = new LogMock();
	ConnectionWatcher watcher = new ConnectionWatcher(conn, log, 1000);
	assertNotNull(watcher);
	assertFalse(watcher.isAlive());

	assertFalse(watcher.checkActivityAlive());

	conn.connect();

	assertTrue(watcher.checkActivityAlive());

	conn.disconnect();

	assertFalse(watcher.checkActivityAlive());
    }

    @Test
    public void testStartActivity() {
	ConnectionMock conn = new ConnectionMock();
	LogMock log = new LogMock();
	ConnectionWatcher watcher = new ConnectionWatcher(conn, log, 1000);
	assertNotNull(watcher);
	assertFalse(watcher.isAlive());

	assertFalse(watcher.checkActivityAlive());

	watcher.startActivity();
	assertTrue(watcher.checkActivityAlive());
	conn.disconnect(); // imitate activity has been stopped
	assertFalse(watcher.checkActivityAlive());
    }

    @Test
    public void testLogActivityAlivePreviolslyDisconnected()
	    throws InterruptedException {
	ConnectionMock conn = new ConnectionMock();
	LogMock log = new LogMock();
	ConnectionWatcher watcher = new ConnectionWatcher(conn, log, 100);
	assertNotNull(watcher);
	assertFalse(watcher.isAlive());

	watcher.setLastPollInactive(false);

	watcher.logActivityAlive();

	assertEquals(0, log.getItemsCount());
    }

    @Test
    public void testLogActivityAlivePreviolslyConnected()
	    throws InterruptedException {
	ConnectionMock conn = new ConnectionMock();
	LogMock log = new LogMock();
	ConnectionWatcher watcher = new ConnectionWatcher(conn, log, 100);
	assertNotNull(watcher);
	assertFalse(watcher.isAlive());

	watcher.setLastPollInactive(true);

	watcher.logActivityAlive();

	assertEquals(1, log.getItemsCount());
    }

    @Test
    public void testLogActivityDownPrevioslyDisconnected() {
	ConnectionMock conn = new ConnectionMock();
	LogMock log = new LogMock();
	ConnectionWatcher watcher = new ConnectionWatcher(conn, log, 100);
	assertNotNull(watcher);
	assertFalse(watcher.isAlive());

	watcher.setLastPollInactive(true);

	watcher.logActivityDown();

	assertEquals(0, log.getItemsCount());
    }

    @Test
    public void testLogActivityDownPrevioslyConnected() {
	ConnectionMock conn = new ConnectionMock();
	LogMock log = new LogMock();
	ConnectionWatcher watcher = new ConnectionWatcher(conn, log, 100);
	assertNotNull(watcher);
	assertFalse(watcher.isAlive());

	watcher.setLastPollInactive(false);

	watcher.logActivityDown();

	assertEquals(1, log.getItemsCount());
    }

    @Test
    public void testLogActivityException() {
	ConnectionMock conn = new ConnectionMock();
	LogMock log = new LogMock();
	ConnectionWatcher watcher = new ConnectionWatcher(conn, log, 100);
	assertNotNull(watcher);
	assertFalse(watcher.isAlive());

	assertFalse(watcher.checkActivityAlive());
	assertEquals(0, log.getItemsCount());

	conn.connect();

	assertTrue(watcher.checkActivityAlive());
	assertEquals(0, log.getItemsCount());

	watcher.logActivityException(new Exception());

	assertEquals(1, log.getItemsCount());
    }

    @Test
    public void testStart() throws InterruptedException {
	ConnectionMock conn = new ConnectionMock();
	LogMock log = new LogMock();
	ConnectionWatcher watcher = new ConnectionWatcher(conn, log, 1000);
	assertNotNull(watcher);
	assertFalse(watcher.isAlive());

	watcher.start();

	Thread.sleep(100);

	assertTrue(watcher.isAlive());

	watcher.stop();
    }

    @Test
    public void testMultipleStart() throws InterruptedException {
	final int positiveTimeout = 10;

	LogMock log = new LogMock();
	ConnectionMock connection = new ConnectionMock();
	ConnectionWatcher w = new ConnectionWatcher(connection, log,
		positiveTimeout);
	assertNotNull(w);

	assertFalse(w.isAlive());

	w.start();
	Thread.sleep(100);
	long threadID = w.getThreadID();

	assertTrue(w.isAlive());

	w.start();
	Thread.sleep(100);
	long threadID2 = w.getThreadID();

	assertTrue(w.isAlive());

	w.start();
	Thread.sleep(100);
	long threadID3 = w.getThreadID();

	assertTrue(w.isAlive());

	w.stop();

	assertSame(threadID, threadID2);
	assertSame(threadID2, threadID3);
    }

    @Test
    public void testStop() throws InterruptedException {
	ConnectionMock conn = new ConnectionMock();
	LogMock log = new LogMock();
	ConnectionWatcher watcher = new ConnectionWatcher(conn, log, 100);
	assertNotNull(watcher);
	assertFalse(watcher.isAlive());

	watcher.start();

	Thread.sleep(100);

	assertTrue(watcher.isAlive());

	watcher.stop();

	Thread.sleep(200);

	assertFalse(watcher.isAlive());
    }

    @Test
    public void testGetPollTimeout() {

	final int pollTimeout = 100;

	ConnectionMock conn = new ConnectionMock();
	LogMock log = new LogMock();
	ConnectionWatcher watcher = new ConnectionWatcher(conn, log,
		pollTimeout);
	assertNotNull(watcher);

	assertEquals(pollTimeout, watcher.getPollTimeout());

    }

    @Test
    public void testGetPollCount() throws InterruptedException {
	final int pollTimeout = 100;

	ConnectionMock conn = new ConnectionMock();
	LogMock log = new LogMock();
	ConnectionWatcher watcher = new ConnectionWatcher(conn, log,
		pollTimeout);
	assertNotNull(watcher);

	assertEquals(0, watcher.getPollCount());

	watcher.start();

	Thread.sleep(300);

	assertTrue(watcher.getPollCount() > 0);

	watcher.stop();

	Thread.sleep(200);

	assertFalse(watcher.isAlive());

	assertTrue(watcher.getPollCount() > 0);

    }
}
