package muc.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.junit.Test;

public class JidRoomKeyTest {

    @Test
    public void testHashCode() {

	final int counter = 100;

	JidRoomKey p1 = new JidRoomKey("john_doe@xmpp.org",
		"conference@conference.xmpp.org");
	JidRoomKey p2 = new JidRoomKey("john_doe@xmpp.org",
		"conference@conference.xmpp.org");

	for (int i = 0; i < counter; ++i) {
	    assertTrue(p1.equals(p2));
	    assertEquals(p1.hashCode(), p2.hashCode());
	}
    }

    @Test
    public void testFailHashCode() {

	String jabberID = "john_doe@xmpp.org";
	String roomName = "conference@conference.xmpp.org";
	String salt = "123";

	final int counter = 100;

	JidRoomKey p1 = new JidRoomKey(jabberID, roomName);
	JidRoomKey p2 = new JidRoomKey(jabberID + salt, roomName);

	for (int i = 0; i < counter; ++i) {
	    assertFalse(p1.equals(p2));
	    assertFalse(p1.hashCode() == p2.hashCode());
	}
    }

    @Test
    public void testEqualsObject() {
	JidRoomKey p1 = new JidRoomKey("john_doe@xmpp.org",
		"conference@conference.xmpp.org");

	// reflexiveness of equality
	assertTrue(p1.equals(p1));

	// symmetric property
	JidRoomKey p2 = new JidRoomKey("john_doe@xmpp.org",
		"conference@conference.xmpp.org");
	assertTrue(p1.equals(p2));
	assertTrue(p2.equals(p1));

	// transitive property
	JidRoomKey p3 = new JidRoomKey("john_doe@xmpp.org",
		"conference@conference.xmpp.org");
	assertTrue(p1.equals(p2));
	assertTrue(p2.equals(p3));
	assertTrue(p1.equals(p3));

	// null equality
	assertFalse(p1.equals(null));
	assertFalse(p2.equals(null));
    }

    @Test
    public void testNotEquals() {
	String jabberID = "john_doe@xmpp.org";
	String roomName = "conference@conference.xmpp.org";
	String salt = "123";

	JidRoomKey p1 = new JidRoomKey(jabberID, roomName);

	// reflexivity
	assertTrue(p1.equals(p1));

	// simm
	JidRoomKey p2 = new JidRoomKey(jabberID + salt, roomName);
	assertFalse(p1.equals(p2));
	assertFalse(p2.equals(p1));

	// transitivity
	p2 = new JidRoomKey(jabberID, roomName);
	JidRoomKey p3 = new JidRoomKey(jabberID + salt, roomName);
	assertTrue(p1.equals(p2));
	assertFalse(p2.equals(p3));
	assertFalse(p1.equals(p3));
    }

    @Test
    public void testHashMapUsersChanged() {
	HashMap<JidRoomKey, Long> hashMap = new HashMap<JidRoomKey, Long>();

	final long keysCount = hashMapItemsCount;

	for (long i = 0; i < keysCount; ++i) {
	    JidRoomKey key = new JidRoomKey(generateJid(i),
		    "room@conference.xmpp.org");
	    hashMap.put(key, i);
	}

	assertEquals(hashMap.size(), keysCount);

	// test access levels
	for (long i = 0; i < keysCount; ++i) {
	    JidRoomKey key = new JidRoomKey(generateJid(i),
		    "room@conference.xmpp.org");

	    Long value = hashMap.get(key);

	    assertNotNull(value);
	    assertEquals(
		    "Expected value for mapping should be equal to actual",
		    (Long) i, value);

	}
    }

    @Test
    public void testHashMapRoomsChanged() {
	HashMap<JidRoomKey, Long> hashMap = new HashMap<JidRoomKey, Long>();

	final long keysCount = hashMapItemsCount;

	for (long i = 0; i < keysCount; ++i) {
	    JidRoomKey key = new JidRoomKey("user@xmpp.org", generateRoom(i));
	    hashMap.put(key, i);
	}

	assertEquals(hashMap.size(), keysCount);

	// test access levels
	for (long i = 0; i < keysCount; ++i) {
	    JidRoomKey key = new JidRoomKey("user@xmpp.org", generateRoom(i));

	    Long value = hashMap.get(key);

	    assertNotNull(value);
	    assertEquals(
		    "Expected value for mapping should be equal to actual",
		    (Long) i, value);

	}
    }

    private String generateJid(Long index) {
	return "user" + Long.toString(index) + "@xmpp.org";
    }

    private String generateRoom(Long index) {
	return "room" + Long.toString(index) + "@conference.xmpp.org";
    }

    final static long hashMapItemsCount = 10000;
}
