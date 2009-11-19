package xmpp.queue;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import xmpp.util.PrivateMessageGenerator;

public class MessageQueueTest {

    @Test
    public void testAddPollPoll() {
	MessageQueue queue = new MessageQueue();
	PrivateMessageGenerator generator = new PrivateMessageGenerator(queue);

	final int itemsCount = 10;

	for (int i = 0; i < itemsCount; ++i)
	    queue.add(generator.generateMessage());

	for (int i = 0; i < itemsCount; ++i)
	    assertNotNull(queue.poll());

	assertNull(queue.poll());
    }
}
