package plugins;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import plugins.moc.ActivePluginMoc;
import plugins.moc.MessageQueueMoc;
import plugins.moc.PluginMoc;
import plugins.moc.PluginMocThrowsExceptions;
import syslog.moc.LogMock;
import xmpp.messaging.PublicChatMessage;
import xmpp.messaging.base.Message;
import xmpp.messaging.domain.ParticipantInfo;

public class PluginManagerTest {

    @Test(expected = NullPointerException.class)
    public void testCreateNullLog() {
	PluginManager m = new PluginManager(null);
	assertNull(m);
    }

    @Test
    public void testCreate() {
	PluginManager m = new PluginManager(new LogMock());
	assertNotNull(m);
    }

    @Test
    public void testRegisterPlugin() {
	PluginMoc plugin1 = new PluginMoc();
	PluginMoc plugin2 = new PluginMoc();

	PluginManager pm = new PluginManager(new LogMock());

	assertEquals(0, pm.getPluginsCount());

	pm.registerPlugin(plugin1);
	pm.registerPlugin(plugin2);

	assertEquals(2, pm.getPluginsCount());
    }

    @Test
    public void testNotRegisterPluginDuplicate() {
	PluginMoc plugin1 = new PluginMoc();
	PluginMoc plugin2 = new PluginMoc();

	PluginManager pm = new PluginManager(new LogMock());

	assertEquals(0, pm.getPluginsCount());

	pm.registerPlugin(plugin1);
	pm.registerPlugin(plugin2);

	assertEquals(2, pm.getPluginsCount());

	pm.registerPlugin(plugin1);

	assertEquals(2, pm.getPluginsCount());

	pm.registerPlugin(plugin2);

	assertEquals(2, pm.getPluginsCount());
    }

    @Test
    public void testPluginMocsHasZeroProcessedMessages() {
	PluginMoc plugin1 = new PluginMoc();
	PluginMoc plugin2 = new PluginMoc();

	assertEquals(0, plugin1.getProcessedMessagesCount());
	assertEquals(0, plugin2.getProcessedMessagesCount());
    }

    @Test
    public void testMessageIsCreated() {
	Message msg = createMessage();
	assertNotNull(msg);
    }

    @Test
    public void testProcessMessage() {
	PluginMoc plugin1 = new PluginMoc();
	PluginMoc plugin2 = new PluginMoc();

	PluginManager pm = new PluginManager(new LogMock());

	pm.registerPlugin(plugin1);
	pm.registerPlugin(plugin2);

	pm.processMessage(createMessage());

	assertEquals(1, plugin1.getProcessedMessagesCount());
	assertEquals(1, plugin2.getProcessedMessagesCount());
    }

    @Test
    public void testProcessNullMessage() {
	PluginMoc plugin1 = new PluginMoc();
	PluginMoc plugin2 = new PluginMoc();

	PluginManager pm = new PluginManager(new LogMock());

	pm.registerPlugin(plugin1);
	pm.registerPlugin(plugin2);

	pm.processMessage(null);

	assertEquals(0, plugin1.getProcessedMessagesCount());
	assertEquals(0, plugin2.getProcessedMessagesCount());
    }

    @Test
    public void testSetTransportAffectsAllPlugins() {
	PluginMoc plugin1 = new PluginMoc();
	PluginMoc plugin2 = new PluginMoc();

	assertNull(plugin1.getTransportQueue());
	assertNull(plugin2.getTransportQueue());

	PluginManager pm = new PluginManager(new LogMock());

	pm.registerPlugin(plugin1);
	pm.registerPlugin(plugin2);

	pm.setTransport(new MessageQueueMoc());

	assertNotNull(plugin1.getTransportQueue());
	assertNotNull(plugin2.getTransportQueue());
    }

    @Test
    public void testPluginsNotStartOnRegistration() {
	ActivePluginMoc plugin1 = new ActivePluginMoc();
	ActivePluginMoc plugin2 = new ActivePluginMoc();

	assertFalse(plugin1.isAlive());
	assertFalse(plugin2.isAlive());

	PluginManager pm = new PluginManager(new LogMock());

	pm.registerPlugin(plugin1);
	pm.registerPlugin(plugin2);

	assertFalse(plugin1.isAlive());
	assertFalse(plugin2.isAlive());
    }

    @Test
    public void testStartAffectsItself() {
	PluginManager pm = new PluginManager(new LogMock());

	assertFalse(pm.isAlive());

	pm.start();

	assertTrue(pm.isAlive());
    }

    @Test
    public void testStopAffectsItself() {
	PluginManager pm = new PluginManager(new LogMock());

	assertFalse(pm.isAlive());

	pm.start();

	pm.stop();

	assertFalse(pm.isAlive());
    }

    @Test
    public void testStartAffectsAllPlugins() {
	ActivePluginMoc plugin1 = new ActivePluginMoc();
	ActivePluginMoc plugin2 = new ActivePluginMoc();

	assertFalse(plugin1.isAlive());
	assertFalse(plugin2.isAlive());

	PluginManager pm = new PluginManager(new LogMock());

	pm.registerPlugin(plugin1);
	pm.registerPlugin(plugin2);

	pm.start();

	assertTrue(plugin1.isAlive());
	assertTrue(plugin2.isAlive());
    }

    @Test
    public void testStopAffectsAllPlugins() {
	ActivePluginMoc plugin1 = new ActivePluginMoc();
	ActivePluginMoc plugin2 = new ActivePluginMoc();

	assertFalse(plugin1.isAlive());
	assertFalse(plugin2.isAlive());

	PluginManager pm = new PluginManager(new LogMock());

	pm.registerPlugin(plugin1);
	pm.registerPlugin(plugin2);

	pm.start();

	assertTrue(plugin1.isAlive());
	assertTrue(plugin2.isAlive());

	pm.stop();

	assertFalse(plugin1.isAlive());
	assertFalse(plugin2.isAlive());
    }

    @Test(expected = RuntimeException.class)
    public void testPluginThrowsException() {
	IPlugin p = new PluginMocThrowsExceptions();
	p.processMessage(null);
    }

    @Test
    public void testPluginManagerSupressesExceptions() {
	IPlugin p = new PluginMocThrowsExceptions();

	PluginManager pm = new PluginManager(new LogMock());
	pm.registerPlugin(p);

	pm.processMessage(createMessage());

    }

    @Test
    public void testPluginManagerLoggsExceptions() {
	IPlugin p = new PluginMocThrowsExceptions();
	LogMock log = new LogMock();

	assertEquals(0, log.getItemsCount());

	PluginManager pm = new PluginManager(log);
	pm.registerPlugin(p);

	pm.processMessage(createMessage());

	assertEquals(1, log.getItemsCount());
    }

    private PublicChatMessage createMessage() throws NullPointerException {
	ParticipantInfo info = new ParticipantInfo("person@xmpp.org",
		"room@conference.xmpp.org/resource");
	PublicChatMessage msg = new PublicChatMessage(info, "some text",
		"room@conference.xmpp.org");
	return msg;
    }

}
