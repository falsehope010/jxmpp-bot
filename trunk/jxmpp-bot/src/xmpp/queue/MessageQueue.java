package xmpp.queue;

import java.util.concurrent.ConcurrentLinkedQueue;

import xmpp.messaging.base.Message;

/**
 * Thread-safe queue which can handle XMPP {@link Message} items
 * 
 * @author tillias
 * 
 */
public class MessageQueue implements IMessageQueue {

    /**
     * Creates new instance of message queue
     */
    public MessageQueue() {
	body = new ConcurrentLinkedQueue<Message>();
    }

    @Override
    public Message poll() {
	return body.poll();
    }

    @Override
    public void add(Message msg) {
	body.add(msg);
    }

    @Override
    public void clear() {
	body.clear();
    }

    ConcurrentLinkedQueue<Message> body;

}
