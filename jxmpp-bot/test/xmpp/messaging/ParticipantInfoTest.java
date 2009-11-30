package xmpp.messaging;

import static org.junit.Assert.assertEquals;
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

}
