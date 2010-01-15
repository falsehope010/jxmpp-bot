package xmpp.utils.activity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class WatchersCollectionTest {

    @Test(expected = NullPointerException.class)
    public void testCreateNullLog() {
	WatchersCollection coll = new WatchersCollection(null, 10);
	assertNull(coll);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNegativeTimeout() {
	LogMock mock = new LogMock();
	WatchersCollection coll = new WatchersCollection(mock, -10);
	assertNull(coll);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateZeroTimeout() {
	LogMock mock = new LogMock();
	WatchersCollection coll = new WatchersCollection(mock, 0);
	assertNull(coll);
    }

    @Test
    public void testCreate() {

	int pollTimeout = 10;

	assertTrue(pollTimeout > 0);

	LogMock mock = new LogMock();
	WatchersCollection coll = new WatchersCollection(mock, pollTimeout);
	assertNotNull(coll);
    }

    @Test
    public void testAdd() {
	int pollTimeout = 10;

	assertTrue(pollTimeout > 0);

	LogMock log = new LogMock();
	WatchersCollection coll = new WatchersCollection(log, pollTimeout);
	assertNotNull(coll);

	assertEquals(0, coll.size());

	coll.add(null);
	assertEquals(0, coll.size());

	ActivityWatcherMock item1 = new ActivityWatcherMock(log);

	coll.add(item1);
	assertEquals(1, coll.size());

	coll.add(item1);
	assertEquals(1, coll.size());

	ActivityWatcherMock item2 = new ActivityWatcherMock(log);

	coll.add(item2);
	assertEquals(2, coll.size());
    }

    @Test
    public void testRemove() {
	int pollTimeout = 10;

	assertTrue(pollTimeout > 0);

	LogMock log = new LogMock();
	WatchersCollection coll = new WatchersCollection(log, pollTimeout);
	assertNotNull(coll);

	assertEquals(0, coll.size());

	ActivityWatcherMock item1 = new ActivityWatcherMock(log);
	ActivityWatcherMock item2 = new ActivityWatcherMock(log);

	coll.add(item1);
	coll.add(item2);

	assertEquals(2, coll.size());

	coll.remove(null);
	assertEquals(2, coll.size());

	coll.remove(item1);
	assertEquals(1, coll.size());

	coll.remove(item2);
	assertEquals(0, coll.size());
    }

    @Test
    public void testStart() {
	int pollTimeout = 10;

	assertTrue(pollTimeout > 0);

	LogMock log = new LogMock();
	WatchersCollection coll = new WatchersCollection(log, pollTimeout);
	assertNotNull(coll);

	assertEquals(0, coll.size());

	ActivityWatcherMock item1 = new ActivityWatcherMock(log);
	ActivityWatcherMock item2 = new ActivityWatcherMock(log);

	coll.add(item1);
	coll.add(item2);

	assertEquals(2, coll.size());

	// activities
	assertFalse(item1.isAlive());
	assertFalse(item2.isAlive());

	coll.start();

	assertFalse(item1.isAlive());
	assertFalse(item2.isAlive());
    }

    @Test
    public void testStop() {
	fail("Not yet implemented");
    }

    @Test
    public void testGetLog() {
	int pollTimeout = 10;

	assertTrue(pollTimeout > 0);

	LogMock mock = new LogMock();
	WatchersCollection coll = new WatchersCollection(mock, pollTimeout);
	assertNotNull(coll);

	assertSame(mock, coll.getLog());
	assertEquals(pollTimeout, coll.getPollTimeout());
    }
}
