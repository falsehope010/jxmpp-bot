package xmpp.messaging;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;

import xmpp.messaging.domain.ParticipantInfo;

public class PrivateChatMessageTest {

    @Test(expected = NullPointerException.class)
    public void testCreateNullSender() {
	ParticipantInfo recipient = new ParticipantInfo("recipient@xmpp.org",
		"recipient@xmpp.org/resource");
	PrivateChatMessage msg = new PrivateChatMessage(null, recipient,
		"sometext", "room@xmpp.org");
	assertNull(msg);
    }

    @Test(expected = NullPointerException.class)
    public void testCreateNullRecipient() {
	ParticipantInfo sender = new ParticipantInfo("sender@xmpp.org",
		"sender@xmpp.org/resource");
	PrivateChatMessage msg = new PrivateChatMessage(sender, null,
		"sometext", "room@xmpp.org");
	assertNull(msg);
    }

    @Test(expected = NullPointerException.class)
    public void testCreateNullText() {
	ParticipantInfo sender = new ParticipantInfo("sender@xmpp.org",
		"sender@xmpp.org/resource");
	ParticipantInfo recipient = new ParticipantInfo("recipient@xmpp.org",
		"recipient@xmpp.org/resource");
	PrivateChatMessage msg = new PrivateChatMessage(sender, recipient,
		null, "room@xmpp.org");
	assertNull(msg);
    }

    @Test(expected = NullPointerException.class)
    public void testCreateNullRoomName() {
	ParticipantInfo sender = new ParticipantInfo("sender@xmpp.org",
		"sender@xmpp.org/resource");
	ParticipantInfo recipient = new ParticipantInfo("recipient@xmpp.org",
		"recipient@xmpp.org/resource");
	PrivateChatMessage msg = new PrivateChatMessage(sender, recipient,
		"sometext", null);
	assertNull(msg);
    }

    @Test
    public void testCreate() {
	ParticipantInfo sender = new ParticipantInfo("sender@xmpp.org",
		"sender@xmpp.org/resource");
	ParticipantInfo recipient = new ParticipantInfo("recipient@xmpp.org",
		"recipient@xmpp.org/resource");
	PrivateChatMessage msg = new PrivateChatMessage(sender, recipient,
		"sometext", "room@xmpp.org");
	assertNotNull(msg);
    }

    @Test
    public void testGetRoomName() {
	ParticipantInfo sender = new ParticipantInfo("sender@xmpp.org",
		"sender@xmpp.org/resource");
	ParticipantInfo recipient = new ParticipantInfo("recipient@xmpp.org",
		"recipient@xmpp.org/resource");

	final String roomName = "room@xmpp.org";

	PrivateChatMessage msg = new PrivateChatMessage(sender, recipient,
		"sometext", roomName);
	assertNotNull(msg);
	assertEquals(roomName, msg.getRoomName());
    }

    @Test
    public void testGetSender() {
	ParticipantInfo sender = new ParticipantInfo("sender@xmpp.org",
		"sender@xmpp.org/resource");
	ParticipantInfo recipient = new ParticipantInfo("recipient@xmpp.org",
		"recipient@xmpp.org/resource");
	PrivateChatMessage msg = new PrivateChatMessage(sender, recipient,
		"sometext", "room@xmpp.org");
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
		"sender@xmpp.org/resource");
	ParticipantInfo recipient = new ParticipantInfo("recipient@xmpp.org",
		"recipient@xmpp.org/resource");
	PrivateChatMessage msg = new PrivateChatMessage(sender, recipient,
		"sometext", "room@xmpp.org");
	assertNotNull(msg);

	ParticipantInfo getter = msg.getRecipient();

	assertTrue(recipient.equals(getter));

	getter = new ParticipantInfo("new_sender@xmpp.org",
		"new_sender@xmpp.org/resource");

	assertTrue(recipient.equals(msg.getRecipient()));
    }

    @Test
    public void testGetTimestamp() {
	ParticipantInfo sender = new ParticipantInfo("sender@xmpp.org",
		"sender@xmpp.org/resource");
	ParticipantInfo recipient = new ParticipantInfo("recipient@xmpp.org",
		"recipient@xmpp.org/resource");
	PrivateChatMessage msg = new PrivateChatMessage(sender, recipient,
		"sometext", "room@xmpp.org");
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
		"sender@xmpp.org/resource");
	ParticipantInfo recipient = new ParticipantInfo("recipient@xmpp.org",
		"recipient@xmpp.org/resource");
	final String text = "sometext";
	PrivateChatMessage msg = new PrivateChatMessage(sender, recipient,
		text, "room@xmpp.org");
	assertNotNull(msg);

	assertEquals(text, msg.getText());
    }

    @Test
    public void testEquals() {
	ParticipantInfo sender = new ParticipantInfo("sender@xmpp.org",
		"sender@xmpp.org/resource");
	ParticipantInfo recipient = new ParticipantInfo("recipient@xmpp.org",
		"recipient@xmpp.org/resource");

	PrivateChatMessage a = new PrivateChatMessage(sender, recipient,
		"sometext", "room@xmpp.org");
	PrivateChatMessage b = new PrivateChatMessage(sender, recipient,
		"sometext", "room@xmpp.org");
	PrivateChatMessage c = new PrivateChatMessage(sender, recipient,
		"sometext", "room@xmpp.org");

	assertFalse(a.equals(null));

	assertTrue(a.equals(a));
	assertTrue(b.equals(b));

	assertTrue(a.equals(b));
	assertTrue(b.equals(a));

	assertTrue(a.equals(b));
	assertTrue(b.equals(c));
	assertTrue(a.equals(c));

	// test not equals
	final String newText = "sometext" + "sometext";
	PrivateChatMessage d = new PrivateChatMessage(sender, recipient,
		newText, "room@xmpp.org");
	assertFalse(a.equals(d));

	ParticipantInfo newSender = new ParticipantInfo("new_sender@xmpp.org",
		"sender@xmpp.org/resource");
	PrivateChatMessage e = new PrivateChatMessage(newSender, recipient,
		newText, "room@xmpp.org");
	assertFalse(a.equals(e));

	ParticipantInfo newRecipient = new ParticipantInfo(
		"recipient@xmpp.org", "new_recipient@xmpp.org/resource");
	PrivateChatMessage f = new PrivateChatMessage(sender, newRecipient,
		newText, "room@xmpp.org");
	assertFalse(a.equals(f));

	PrivateMessage g = new PrivateMessage(newSender, newRecipient, newText);
	assertFalse(a.equals(g));

	final String newRoom = "newRoom@xmpp.org";
	assertFalse(newRoom.equals("room@xmpp.org"));
	PrivateChatMessage h = new PrivateChatMessage(sender, recipient,
		"sometext", newRoom);
	assertFalse(a.equals(h));
    }

    @Test
    public void testHashCode() {
	ParticipantInfo sender = new ParticipantInfo("sender@xmpp.org",
		"sender@xmpp.org/resource");
	ParticipantInfo recipient = new ParticipantInfo("recipient@xmpp.org",
		"recipient@xmpp.org/resource");

	PrivateChatMessage a = new PrivateChatMessage(sender, recipient,
		"sometext", "room@xmpp.org");
	PrivateChatMessage b = new PrivateChatMessage(sender, recipient,
		"sometext", "room@xmpp.org");

	assertEquals(a.hashCode(), b.hashCode());

	// test fail hashCode() equality
	final String newText = "sometext" + "sometext";
	PrivateChatMessage d = new PrivateChatMessage(sender, recipient,
		newText, "room@xmpp.org");
	assertFalse(a.hashCode() == d.hashCode());

	ParticipantInfo newSender = new ParticipantInfo("new_sender@xmpp.org",
		"sender@xmpp.org/resource");
	PrivateChatMessage e = new PrivateChatMessage(newSender, recipient,
		newText, "room@xmpp.org");
	assertFalse(a.hashCode() == e.hashCode());

	ParticipantInfo newRecipient = new ParticipantInfo(
		"recipient@xmpp.org", "new_recipient@xmpp.org/resource");
	PrivateChatMessage f = new PrivateChatMessage(sender, newRecipient,
		newText, "room@xmpp.org");
	assertFalse(a.hashCode() == f.hashCode());

	PrivateMessage g = new PrivateMessage(newSender, newRecipient, newText);
	assertFalse(a.hashCode() == g.hashCode());

	final String newRoom = "newRoom@xmpp.org";
	assertFalse(newRoom.equals("room@xmpp.org"));
	PrivateChatMessage h = new PrivateChatMessage(sender, recipient,
		"sometext", newRoom);
	assertFalse(a.hashCode() == h.hashCode());
    }
}
