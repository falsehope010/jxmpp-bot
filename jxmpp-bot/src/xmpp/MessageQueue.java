package xmpp;

import java.util.concurrent.ConcurrentLinkedQueue;

import xmpp.message.IXmppMessage;

public class MessageQueue implements IXmppMessageQueue {

    public MessageQueue() {
	body = new ConcurrentLinkedQueue<IXmppMessage>();
    }

    @Override
    public IXmppMessage poll() {
	return body.poll();
    }

    @Override
    public void add(IXmppMessage msg) {
	body.add(msg);
    }

    ConcurrentLinkedQueue<IXmppMessage> body;
}
