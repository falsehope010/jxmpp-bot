package xmpp.messaging;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;

import xmpp.messaging.domain.ParticipantInfo;

/**
 * Main purpose of tests is to confirm that {@link PrivateMessage} is immutable
 * 
 * @author tillias
 * 
 */
public class PrivateMessageTest {

    @Test(expected = NullPointerException.class)
    public void testCreateNullSender() {
	ParticipantInfo recipient = new ParticipantInfo("recipient@xmpp.org",
		"recipient@xmpp.org/resource");
	PrivateMessage msg = new PrivateMessage(null, recipient, "sometext");
	assertNull(msg);
    }

    @Test(expected = NullPointerException.class)
    public void testCreateNullRecipient() {
	ParticipantInfo sender = new ParticipantInfo("sender@xmpp.org",
		"sender@xmpp.org/resource");
	PrivateMessage msg = new PrivateMessage(sender, null, "sometext");
	assertNull(msg);
    }

    @Test(expected = NullPointerException.class)
    public void testCreateNullText() {
	ParticipantInfo sender = new ParticipantInfo("sender@xmpp.org",
		"sender@xmpp.org/resource");
	ParticipantInfo recipient = new ParticipantInfo("recipient@xmpp.org",
		"recipient@xmpp.org/resource");
	PrivateMessage msg = new PrivateMessage(sender, recipient, null);
	assertNull(msg);
    }

    @Test
    public void testCreate() {
	ParticipantInfo sender = new ParticipantInfo("sender@xmpp.org",
		"sender@xmpp.org/resource");
	ParticipantInfo recipient = new ParticipantInfo("recipient@xmpp.org",
		"recipient@xmpp.org/resource");
	PrivateMessage msg = new PrivateMessage(sender, recipient, "sometext");
	assertNotNull(msg);
    }

    @Test
    public void testGetSender() {
	ParticipantInfo sender = new ParticipantInfo("sender@xmpp.org",
		"sender@xmpp.org/resource");
	ParticipantInfo recipient = new ParticipantInfo("recipient@xmpp.org",
		"recipient@xmpp.org/resource");
	PrivateMessage msg = new PrivateMessage(sender, recipient, "sometext");
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
	PrivateMessage msg = new PrivateMessage(sender, recipient, "sometext");
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
	PrivateMessage msg = new PrivateMessage(sender, recipient, "sometext");
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
	PrivateMessage msg = new PrivateMessage(sender, recipient, text);
	assertNotNull(msg);

	assertEquals(text, msg.getText());
    }

    @Test
    public void testEquals() {
	ParticipantInfo sender = new ParticipantInfo("sender@xmpp.org",
		"sender@xmpp.org/resource");
	ParticipantInfo recipient = new ParticipantInfo("recipient@xmpp.org",
		"recipient@xmpp.org/resource");

	PrivateMessage a = new PrivateMessage(sender, recipient, "sometext");
	PrivateMessage b = new PrivateMessage(sender, recipient, "sometext");
	PrivateMessage c = new PrivateMessage(sender, recipient, "sometext");

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
	PrivateMessage d = new PrivateMessage(sender, recipient, newText);
	assertFalse(a.equals(d));

	ParticipantInfo newSender = new ParticipantInfo("new_sender@xmpp.org",
		"sender@xmpp.org/resource");
	PrivateMessage e = new PrivateMessage(newSender, recipient, newText);
	assertFalse(a.equals(e));

	ParticipantInfo newRecipient = new ParticipantInfo(
		"recipient@xmpp.org", "new_recipient@xmpp.org/resource");
	PrivateMessage f = new PrivateMessage(sender, newRecipient, newText);
	assertFalse(a.equals(f));

	PrivateMessage g = new PrivateMessage(newSender, newRecipient, newText);
	assertFalse(a.equals(g));
    }

    @Test
    public void testHashCode() {
	ParticipantInfo sender = new ParticipantInfo("sender@xmpp.org",
		"sender@xmpp.org/resource");
	ParticipantInfo recipient = new ParticipantInfo("recipient@xmpp.org",
		"recipient@xmpp.org/resource");
	final String text = "sometext";

	PrivateMessage a = new PrivateMessage(sender, recipient, text);
	PrivateMessage b = new PrivateMessage(sender, recipient, text);

	assertEquals(a.hashCode(), b.hashCode());

	final String newText = text + text;
	PrivateMessage d = new PrivateMessage(sender, recipient, newText);
	assertFalse(a.hashCode() == d.hashCode());

	ParticipantInfo newSender = new ParticipantInfo("new_sender@xmpp.org",
		"sender@xmpp.org/resource");
	PrivateMessage e = new PrivateMessage(newSender, recipient, newText);
	assertFalse(a.hashCode() == e.hashCode());

	ParticipantInfo newRecipient = new ParticipantInfo(
		"recipient@xmpp.org", "new_recipient@xmpp.org/resource");
	PrivateMessage f = new PrivateMessage(sender, newRecipient, newText);
	assertFalse(a.hashCode() == f.hashCode());

	PrivateMessage g = new PrivateMessage(newSender, newRecipient, newText);
	assertFalse(a.hashCode() == g.hashCode());
    }

}
