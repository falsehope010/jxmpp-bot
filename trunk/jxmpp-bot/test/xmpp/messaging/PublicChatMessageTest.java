package xmpp.messaging;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;

import xmpp.messaging.domain.ParticipantInfo;

public class PublicChatMessageTest {

    @Test(expected = NullPointerException.class)
    public void testCreateNullSender() {
	PublicChatMessage msg = new PublicChatMessage(null, "message_text",
		"room@xmpp.org");
	assertNull(msg);
    }

    @Test(expected = NullPointerException.class)
    public void testCreateNullText() {
	ParticipantInfo sender = new ParticipantInfo("sender@xmpp.org",
		"room@xmpp.org/occupant_nickname");
	PublicChatMessage msg = new PublicChatMessage(sender, null,
		"room@xmpp.org");
	assertNull(msg);
    }

    @Test(expected = NullPointerException.class)
    public void testCreateNullRoomName() {
	ParticipantInfo sender = new ParticipantInfo("sender@xmpp.org",
		"room@xmpp.org/occupant_nickname");
	PublicChatMessage msg = new PublicChatMessage(sender, "message_text",
		null);
	assertNull(msg);
    }

    @Test
    public void testCreate() {
	ParticipantInfo sender = new ParticipantInfo("sender@xmpp.org",
		"room@xmpp.org/occupant_nickname");
	PublicChatMessage msg = new PublicChatMessage(sender, "message_text",
		"room@xmpp.org");
	assertNotNull(msg);
    }

    @Test
    public void testGetRoomName() {
	ParticipantInfo sender = new ParticipantInfo("sender@xmpp.org",
		"room@xmpp.org/occupant_nickname");

	final String roomName = "room@xmpp.org";

	PublicChatMessage msg = new PublicChatMessage(sender, "message_text",
		roomName);
	assertNotNull(msg);

	assertEquals(roomName, msg.getRoomName());
    }

    @Test
    public void testGetSender() {
	ParticipantInfo sender = new ParticipantInfo("sender@xmpp.org",
		"sender@xmpp.org/resource");
	PublicChatMessage msg = new PublicChatMessage(sender, "sometext",
		"room@xmpp.org");
	assertNotNull(msg);

	ParticipantInfo getter = msg.getSender();

	assertTrue(sender.equals(getter));

	getter = new ParticipantInfo("new_sender@xmpp.org",
		"new_sender@xmpp.org/resource");

	assertTrue(sender.equals(msg.getSender()));
    }

    @Test
    public void testGetRecipient() {
	ParticipantInfo sender = new ParticipantInfo("sender@xmpp.org",
		"room@xmpp.org/occupant_nickname");
	PublicChatMessage msg = new PublicChatMessage(sender, "message_text",
		"room@xmpp.org");
	assertNotNull(msg);

	assertNull(msg.getRecipient());
	assertNull(msg.getRecipient());
    }

    @Test
    public void testGetTimestamp() {
	ParticipantInfo sender = new ParticipantInfo("sender@xmpp.org",
		"sender@xmpp.org/resource");
	PublicChatMessage msg = new PublicChatMessage(sender, "sometext",
		"room@xmpp.org");
	assertNotNull(msg);

	Date getter = msg.getTimestamp();
	Date holder = new Date(getter.getTime());

	final long delta = 1000;

	getter = new Date(getter.getTime() + delta);

	assertFalse(getter.equals(holder));
	assertTrue(holder.equals(msg.getTimestamp()));
    }

    @Test
    public void testGetText() {
	ParticipantInfo sender = new ParticipantInfo("sender@xmpp.org",
		"room@xmpp.org/occupant_nickname");

	final String messageText = "message_text";

	PublicChatMessage msg = new PublicChatMessage(sender, messageText,
		"room@xmpp.org");
	assertNotNull(msg);

	assertEquals(messageText, msg.getText());
    }

    @Test
    public void testEquals() {
	ParticipantInfo sender = new ParticipantInfo("sender@xmpp.org",
		"sender@xmpp.org/resource");
	PublicChatMessage a = new PublicChatMessage(sender, "sometext",
		"room@xmpp.org");
	PublicChatMessage b = new PublicChatMessage(sender, "sometext",
		"room@xmpp.org");
	PublicChatMessage c = new PublicChatMessage(sender, "sometext",
		"room@xmpp.org");

	assertFalse(a.equals(null));
	assertTrue(a.equals(a));

	assertTrue(a.equals(b));
	assertTrue(b.equals(a));

	assertTrue(a.equals(b));
	assertTrue(b.equals(c));
	assertTrue(a.equals(c));

	// test not equals
	ParticipantInfo newSender = new ParticipantInfo("new_sender@xmpp.org",
		"sender@xmpp.org/resource");
	assertFalse(sender.equals(newSender));

	PublicChatMessage d = new PublicChatMessage(newSender, "sometext",
		"room@xmpp.org");

	assertFalse(a.equals(d));

	PublicChatMessage e = new PublicChatMessage(sender, "new_sometext",
		"room@xmpp.org");
	assertFalse(a.equals(e));

	PublicChatMessage f = new PublicChatMessage(sender, "sometext",
		"new_room@xmpp.org");
	assertFalse(a.equals(f));

	PublicChatMessage g = new PublicChatMessage(newSender, "new_sometext",
		"new_room@xmpp.org");
	assertFalse(a.equals(g));
    }

    @Test
    public void testHashCode() {
	ParticipantInfo sender = new ParticipantInfo("sender@xmpp.org",
		"sender@xmpp.org/resource");
	PublicChatMessage a = new PublicChatMessage(sender, "sometext",
		"room@xmpp.org");
	PublicChatMessage b = new PublicChatMessage(sender, "sometext",
		"room@xmpp.org");

	assertEquals(a.hashCode(), b.hashCode());
	assertEquals(b.hashCode(), a.hashCode());

	// test not equals hash code
	ParticipantInfo newSender = new ParticipantInfo("new_sender@xmpp.org",
		"sender@xmpp.org/resource");
	assertFalse(sender.equals(newSender));

	PublicChatMessage d = new PublicChatMessage(newSender, "sometext",
		"room@xmpp.org");

	assertFalse(a.hashCode() == d.hashCode());

	PublicChatMessage e = new PublicChatMessage(sender, "new_sometext",
		"room@xmpp.org");
	assertFalse(a.hashCode() == e.hashCode());

	PublicChatMessage f = new PublicChatMessage(sender, "sometext",
		"new_room@xmpp.org");
	assertFalse(a.hashCode() == f.hashCode());

	PublicChatMessage g = new PublicChatMessage(newSender, "new_sometext",
		"new_room@xmpp.org");
	assertFalse(a.hashCode() == g.hashCode());
    }
}
