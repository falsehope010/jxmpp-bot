package xmpp.queue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import xmpp.core.ITransport;
import xmpp.messaging.PrivateMessage;
import xmpp.messaging.base.Message;
import xmpp.messaging.domain.ParticipantInfo;
import xmpp.queue.moc.TransportMoc;

public class TransportQueueTest {

    @Test(expected = NullPointerException.class)
    public void testCreateNullTransport() {
	TransportQueue queue = new TransportQueue(null, 10);
	assertNull(queue);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNegativePollTimeout() {
	ITransport transport = new TransportMoc();
	TransportQueue queue = new TransportQueue(transport, -10);
	assertNull(queue);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateZeroPollTimeout() {
	ITransport transport = new TransportMoc();
	TransportQueue queue = new TransportQueue(transport, 0);
	assertNull(queue);
    }

    @Test
    public void testCreate() {
	ITransport transport = new TransportMoc();
	TransportQueue queue = new TransportQueue(transport, 10);
	assertNotNull(queue);
    }

    @Test
    public void testPerformActionEmptyQueue() {
	TransportMoc transport = new TransportMoc();
	TransportQueue queue = new TransportQueue(transport, 10);
	assertNotNull(queue);

	queue.performAction();
	assertEquals("Queue should be empty and no messages should be sent", 0,
		transport.getSentMessageCount());
    }

    @Test
    public void testPerformActionFilledQueue() {
	TransportMoc transport = new TransportMoc();
	TransportQueue queue = new TransportQueue(transport, 10);
	assertNotNull(queue);

	queue.add(createMessage());
	queue.add(createMessage());

	queue.performAction();
	assertEquals("Queue should not be empty", 1, transport
		.getSentMessageCount());
	queue.performAction();
	assertEquals("Queue should not be empty", 2, transport
		.getSentMessageCount());
	queue.performAction();
	assertEquals("Queue should be empty now", 2, transport
		.getSentMessageCount());
    }

    @Test
    public void testAddPoll() {
	TransportMoc transport = new TransportMoc();
	TransportQueue queue = new TransportQueue(transport, 10);
	assertNotNull(queue);

	queue.add(createMessage());

	assertNotNull(queue.poll());

	assertNull("Queue should be empty", queue.poll());
    }

    private Message createMessage() {
	ParticipantInfo sender = new ParticipantInfo("sender@xmpp.org",
		"sender@xmpp.org/resource");
	ParticipantInfo recipient = new ParticipantInfo("recipient@xmpp.org",
		"recipient@xmpp.org/resource");
	PrivateMessage msg = new PrivateMessage(sender, recipient, "sometext");
	return msg;
    }

}
