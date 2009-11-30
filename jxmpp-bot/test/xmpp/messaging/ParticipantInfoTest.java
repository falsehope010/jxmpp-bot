package xmpp.messaging;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import xmpp.messaging.domain.ParticipantInfo;

public class ParticipantInfoTest {

    @Test(expected = NullPointerException.class)
    public void testCreateNullJabberID() {
	ParticipantInfo info = new ParticipantInfo(null, "adress");
	assertNull(info);
    }

    @Test(expected = NullPointerException.class)
    public void testCreateNullAdress() {
	ParticipantInfo info = new ParticipantInfo("jid", null);
	assertNull(info);
    }

    @Test
    public void testCreateCopyingConstructor() {
	ParticipantInfo base = new ParticipantInfo("jid", "adress");
	ParticipantInfo dest = new ParticipantInfo(base);
	assertNotNull(dest);

	assertEquals(base.getAdress(), dest.getAdress());
	assertEquals(base.getJabberID(), dest.getJabberID());
	assertTrue(base.equals(dest));
	assertEquals(base.hashCode(), dest.hashCode());
    }

    @Test
    public void testEquals() {
	ParticipantInfo a = new ParticipantInfo("jid", "adress");
	ParticipantInfo b = new ParticipantInfo("jid", "adress");

	assertTrue(a.equals(a));

	assertTrue(a.equals(b));
	assertTrue(b.equals(a));

	ParticipantInfo c = new ParticipantInfo("jid", "adress");
	assertTrue(a.equals(b));
	assertTrue(b.equals(c));
	assertTrue(a.equals(c));

	assertFalse(a.equals(null));
	assertFalse(a.equals(new Object()));

	ParticipantInfo d = new ParticipantInfo("jid1", "adress");
	assertFalse(a.equals(d));

	ParticipantInfo e = new ParticipantInfo("jid", "adress1");
	assertFalse(a.equals(e));
    }

    @Test
    public void testHashCode() {
	ParticipantInfo a = new ParticipantInfo("jid", "adress");
	ParticipantInfo b = new ParticipantInfo("jid", "adress");
	assertEquals(a.hashCode(), b.hashCode());
	assertEquals(b.hashCode(), a.hashCode());

	ParticipantInfo c = new ParticipantInfo("jid1", "adress");
	assertFalse(a.hashCode() == c.hashCode());

	ParticipantInfo d = new ParticipantInfo("jid", "adress1");
	assertFalse(a.hashCode() == d.hashCode());
    }

}
