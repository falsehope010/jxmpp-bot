package xmpp.util;

import java.util.concurrent.atomic.AtomicBoolean;

import xmpp.messaging.IXmppMessage;
import xmpp.queue.IXmppMessageQueue;

public abstract class AbstractMessageGenerator extends Thread {

    public AbstractMessageGenerator(IXmppMessageQueue queue) {
	if (queue == null)
	    throw new NullPointerException();

	this.queue = queue;
	terminate = new AtomicBoolean();
    }

    public void terminate() {
	terminate.set(false);
    }

    public abstract IXmppMessage generateMessage();

    @Override
    public void run() {
	while (!terminate.get()) {
	    try {
		IXmppMessage msg = generateMessage();
		if (msg != null) {
		    queue.add(msg);
		}
		Thread.sleep(100);
	    } catch (InterruptedException e) {
		e.printStackTrace();
		terminate();
	    }
	}
    }

    AtomicBoolean terminate;
    IXmppMessageQueue queue;
}
