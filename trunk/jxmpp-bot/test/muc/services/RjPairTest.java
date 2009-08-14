package muc.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RjPairTest {

    @Test
    public void testHashCode() {

	final int counter = 100;

	JidRoomKey p1 = new JidRoomKey("john_doe@xmpp.org",
		"conference@conference.xmpp.org");
	JidRoomKey p2 = new JidRoomKey("john_doe@xmpp.org",
		"conference@conference.xmpp.org");

	for (int i = 0; i < counter; ++i) {
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
	    assertFalse(p1.hashCode() == p2.hashCode());
	}
    }

    @Test
    public void testEqualsObject() {
	String jabberID = "john_doe@xmpp.org";
	String roomName = "conference@conference.xmpp.org";

	JidRoomKey p1 = new JidRoomKey(jabberID, roomName);

	// reflexiveness
	assertTrue(p1.equals(p1));

	// symmetric property
	JidRoomKey p2 = new JidRoomKey(jabberID, roomName);
	assertTrue(p1.equals(p2));
	assertTrue(p2.equals(p1));

	// transitivity
	JidRoomKey p3 = new JidRoomKey(jabberID, roomName);
	assertTrue(p1.equals(p2));
	assertTrue(p2.equals(p3));
	assertTrue(p1.equals(p3));
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
}
