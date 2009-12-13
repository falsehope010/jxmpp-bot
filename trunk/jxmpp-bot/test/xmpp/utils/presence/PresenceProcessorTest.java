package xmpp.utils.presence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.jivesoftware.smack.packet.Presence;
import org.junit.Test;

import xmpp.helpers.PacketGenerator;
import xmpp.utils.presence.PresenceProcessor;

public class PresenceProcessorTest {

    @Test
    public void testNullPresence() {
	PresenceProcessor processor = new PresenceProcessor();
	assertNull(processor.getJabberID(null));
    }

    @Test
    public void testExtractJabberID() {
	PacketGenerator generator = new PacketGenerator();

	final String jid = "john_doe@xmpp.org";

	Presence presence = generator.createPresence(jid, "nick",
		Presence.Type.available);

	PresenceProcessor processor = new PresenceProcessor();

	assertEquals(jid, processor.getJabberID(presence));
    }
}
