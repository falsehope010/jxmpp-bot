package xmpp.messaging;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;

import xmpp.messaging.domain.ParticipantInfo;

public class AppearanceMessageTest {

    @Test(expected = NullPointerException.class)
    public void testCreateNullSender() {
	AppearanceMessage msg = new AppearanceMessage(null, true, "testRoom");

	assertNull(msg);
    }

    @Test(expected = NullPointerException.class)
    public void testCreateNullRoomName() {
	ParticipantInfo sender = new ParticipantInfo("sender@xmpp.org",
		"sender@xmpp.org/client");
	AppearanceMessage msg = new AppearanceMessage(sender, true, null);

	assertNull(msg);
    }

    @Test
    public void testCreate() {
	ParticipantInfo sender = new ParticipantInfo("sender@xmpp.org",
		"sender@xmpp.org/client");
	AppearanceMessage msg = new AppearanceMessage(sender, true, "room");

	assertNotNull(msg);
    }

    @Test
    public void testGetTimestamp() {
	ParticipantInfo sender = new ParticipantInfo("sender@xmpp.org",
		"sender@xmpp.org/resource");
	AppearanceMessage msg = new AppearanceMessage(sender, true, "room");
	assertNotNull(msg);

	Date getter = msg.getTimestamp();
	Date holder = new Date(getter.getTime());

	final long delta = 1000;

	getter = new Date(getter.getTime() + delta);

	assertFalse(getter.equals(holder));
	assertTrue(holder.equals(msg.getTimestamp()));
    }

    @Test
    public void testIsJoined() {
	ParticipantInfo sender = new ParticipantInfo("sender@xmpp.org",
		"sender@xmpp.org/resource");
	AppearanceMessage msg = new AppearanceMessage(sender, true, "room");
	assertNotNull(msg);

	assertEquals(msg.isJoined, true);
    }

    @Test
    public void testEquals() {
	ParticipantInfo sender = new ParticipantInfo("sender@xmpp.org",
		"sender@xmpp.org/resource");
	AppearanceMessage a = new AppearanceMessage(sender, true, "room");
	AppearanceMessage b = new AppearanceMessage(sender, true, "room");
	AppearanceMessage c = new AppearanceMessage(sender, true, "room");

	assertTrue(a.equals(a));

	assertTrue(a.equals(b));
	assertTrue(b.equals(a));

	assertTrue(a.equals(c));
	assertTrue(a.equals(b));
	assertTrue(b.equals(c));
    }

    @Test
    public void testNotEquals() {
	ParticipantInfo sender = new ParticipantInfo("sender@xmpp.org",
		"sender@xmpp.org/resource");
	AppearanceMessage a = new AppearanceMessage(sender, true, "room1");
	AppearanceMessage b = new AppearanceMessage(sender, true, "room");
	AppearanceMessage c = new AppearanceMessage(sender, false, "room");

	assertFalse(a.equals(b));
	assertFalse(b.equals(c));

	assertFalse(a.equals(null));
    }

    @Test
    public void testHashCode() {
	ParticipantInfo sender = new ParticipantInfo("sender@xmpp.org",
		"sender@xmpp.org/resource");
	AppearanceMessage a = new AppearanceMessage(sender, true, "room");
	AppearanceMessage b = new AppearanceMessage(sender, true, "room");

	assertTrue(a.equals(a));

	assertTrue(a.equals(b));
	assertTrue(b.equals(a));

	assertTrue(a.hashCode() == b.hashCode());

	assertTrue(a.hashCode() == a.hashCode());

    }




}
