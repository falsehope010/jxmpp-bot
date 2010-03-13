package plugins;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import plugins.moc.AbstractPluginMoc;
import plugins.moc.MessageQueueMoc;
import xmpp.messaging.PublicChatMessage;
import xmpp.messaging.base.Message;
import xmpp.messaging.domain.ParticipantInfo;

/**
 * Tests behavior of abstract base class {@link AbstractPlugin}
 * 
 * @author tillias
 * 
 */
public class AbstractPluginTest {

    @Test
    public void testStart() throws InterruptedException {
	AbstractPluginMoc plugin = new AbstractPluginMoc();
	assertFalse(plugin.isAlive());

	plugin.start();
	Thread.sleep(200);
	assertTrue(plugin.isAlive());

	plugin.stop();
    }

    @Test
    public void testStop() throws InterruptedException {
	AbstractPluginMoc plugin = new AbstractPluginMoc();
	assertFalse(plugin.isAlive());

	plugin.start();

	Thread.sleep(200);

	plugin.stop();

	Thread.sleep(200);

	assertFalse(plugin.isAlive());
    }

    @Test
    public void testProcessMessagesAsync() throws InterruptedException {
	AbstractPluginMoc plugin = new AbstractPluginMoc();
	plugin.start();

	Message msg = createMessage();

	plugin.setProcessedMessagesCount(0);
	plugin.processMessage(msg);

	Thread.sleep(200);

	assertEquals(1, plugin.getProcessedMessagesCount());
    }

    @Test
    public void testPropagatesResponse() {
	MessageQueueMoc queue = new MessageQueueMoc();
	AbstractPluginMoc plugin = new AbstractPluginMoc();
	plugin.setTransport(queue);

	queue.setMessagesCount(0);

	plugin.sendResponse(createMessage());

	assertEquals(1, queue.getMessagesCount());
    }

    private PublicChatMessage createMessage() throws NullPointerException {
	ParticipantInfo info = new ParticipantInfo("person@xmpp.org",
		"room@conference.xmpp.org/resource");
	PublicChatMessage msg = new PublicChatMessage(info, "some text",
		"room@conference.xmpp.org");
	return msg;
    }
}
