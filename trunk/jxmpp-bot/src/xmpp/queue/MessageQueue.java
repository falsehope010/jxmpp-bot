package xmpp.queue;

import java.util.concurrent.ConcurrentLinkedQueue;

import xmpp.messaging.base.Message;

public class MessageQueue implements IXmppMessageQueue {

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

    ConcurrentLinkedQueue<Message> body;
}
