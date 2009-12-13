package xmpp.utils.activity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RoomWatcherTest {

    @Test(expected = NullPointerException.class)
    public void testCreateNullRoom() {
	LogMock log = new LogMock();
	RoomWatcher w = new RoomWatcher(null, log, 10);
	assertNull(w);
    }

    @Test(expected = NullPointerException.class)
    public void testCreateNullLog() {
	RoomMock room = new RoomMock();
	RoomWatcher w = new RoomWatcher(room, null, 10);
	assertNull(w);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNegativePollTimeout() {
	LogMock log = new LogMock();
	RoomMock room = new RoomMock();
	RoomWatcher w = new RoomWatcher(room, log, -10);
	assertNull(w);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateZeroPollTimeout() {
	LogMock log = new LogMock();
	RoomMock room = new RoomMock();
	RoomWatcher w = new RoomWatcher(room, log, 0);
	assertNull(w);
    }

    @Test
    public void testCreate() {
	final int positiveTimeout = 10;

	LogMock log = new LogMock();
	RoomMock room = new RoomMock();
	RoomWatcher w = new RoomWatcher(room, log, positiveTimeout);
	assertNotNull(w);
    }

    @Test
    public void testCheckActivityAlive() {
	final int positiveTimeout = 10;

	LogMock log = new LogMock();
	RoomMock room = new RoomMock();
	RoomWatcher w = new RoomWatcher(room, log, positiveTimeout);
	assertNotNull(w);

	assertFalse(room.isJoined());
	assertFalse(w.checkActivityAlive());

	room.join();

	assertTrue(room.isJoined());
	assertTrue(w.checkActivityAlive());
    }

    @Test
    public void testStartActivity() {
	final int positiveTimeout = 10;

	LogMock log = new LogMock();
	RoomMock room = new RoomMock();
	RoomWatcher w = new RoomWatcher(room, log, positiveTimeout);
	assertNotNull(w);

	assertFalse(room.isJoined);
	assertFalse(w.checkActivityAlive());

	w.startActivity();

	assertTrue(room.isJoined());
	assertTrue(w.checkActivityAlive());
    }

    @Test
    public void testLogActivityAliveLastPollInactive() {
	final int positiveTimeout = 10;

	LogMock log = new LogMock();
	RoomMock room = new RoomMock();
	RoomWatcher w = new RoomWatcher(room, log, positiveTimeout);
	assertNotNull(w);

	assertFalse(room.isJoined);
	assertFalse(w.checkActivityAlive());

	w.setLastPollInactive(true);

	assertEquals(0, log.getItemsCount());

	w.logActivityAlive();

	assertTrue(log.getItemsCount() > 0);
    }

    @Test
    public void testLogActivityAliveLastPollActive() {
	final int positiveTimeout = 10;

	LogMock log = new LogMock();
	RoomMock room = new RoomMock();
	RoomWatcher w = new RoomWatcher(room, log, positiveTimeout);
	assertNotNull(w);

	assertFalse(room.isJoined);
	assertFalse(w.checkActivityAlive());

	w.setLastPollInactive(false);

	assertEquals(0, log.getItemsCount());

	w.logActivityAlive();

	assertEquals(0, log.getItemsCount());
    }

    @Test
    public void testLogActivityDownLastPollInactive() {
	final int positiveTimeout = 10;

	LogMock log = new LogMock();
	RoomMock room = new RoomMock();
	RoomWatcher w = new RoomWatcher(room, log, positiveTimeout);
	assertNotNull(w);

	assertFalse(room.isJoined);
	assertFalse(w.checkActivityAlive());

	w.setLastPollInactive(true);

	assertEquals(0, log.getItemsCount());

	w.logActivityDown();

	assertEquals(0, log.getItemsCount());
    }

    @Test
    public void testLogActivityDownLastPollActive() {
	final int positiveTimeout = 10;

	LogMock log = new LogMock();
	RoomMock room = new RoomMock();
	RoomWatcher w = new RoomWatcher(room, log, positiveTimeout);
	assertNotNull(w);

	assertFalse(room.isJoined);
	assertFalse(w.checkActivityAlive());

	w.setLastPollInactive(false);

	assertEquals(0, log.getItemsCount());

	w.logActivityDown();

	assertTrue(log.getItemsCount() > 0);
    }

    @Test
    public void testLogActivityException() {
	final int positiveTimeout = 10;

	LogMock log = new LogMock();
	RoomMock room = new RoomMock();
	RoomWatcher w = new RoomWatcher(room, log, positiveTimeout);
	assertNotNull(w);

	assertFalse(room.isJoined);
	assertFalse(w.checkActivityAlive());

	w.setLastPollInactive(false);

	assertEquals(0, log.getItemsCount());

	w.logActivityException(new Exception());

	assertTrue(log.getItemsCount() > 0);
    }

    @Test
    public void testStart() throws InterruptedException {
	final int positiveTimeout = 10;

	LogMock log = new LogMock();
	RoomMock room = new RoomMock();
	RoomWatcher w = new RoomWatcher(room, log, positiveTimeout);
	assertNotNull(w);

	assertFalse(w.isAlive());

	w.start();

	Thread.sleep(100);

	assertTrue(w.isAlive());

	w.stop();
    }

    @Test
    public void testMultipleStart() throws InterruptedException {
	final int positiveTimeout = 10;

	LogMock log = new LogMock();
	RoomMock room = new RoomMock();
	RoomWatcher w = new RoomWatcher(room, log, positiveTimeout);
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
	final int positiveTimeout = 10;

	LogMock log = new LogMock();
	RoomMock room = new RoomMock();
	RoomWatcher w = new RoomWatcher(room, log, positiveTimeout);
	assertNotNull(w);

	assertFalse(w.isAlive());

	w.start();
	Thread.sleep(100);

	assertTrue(w.isAlive());

	w.stop();

	Thread.sleep(100);

	assertFalse(w.isAlive());
    }

    @Test
    public void testGetPollTimeout() {
	final int positiveTimeout = 10;

	LogMock log = new LogMock();
	RoomMock room = new RoomMock();
	RoomWatcher w = new RoomWatcher(room, log, positiveTimeout);
	assertNotNull(w);

	assertSame(positiveTimeout, w.getPollTimeout());
    }

    @Test
    public void testGetPollCount() throws InterruptedException {
	final int positiveTimeout = 10;

	LogMock log = new LogMock();
	RoomMock room = new RoomMock();
	RoomWatcher w = new RoomWatcher(room, log, positiveTimeout);
	assertNotNull(w);

	assertEquals(0, w.getPollCount());

	w.start();

	Thread.sleep(300);

	w.stop();

	assertTrue(w.getPollCount() > 0);
    }

}
