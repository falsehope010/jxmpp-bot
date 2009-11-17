package xmpp.queue;

import java.util.concurrent.atomic.AtomicBoolean;

import xmpp.messaging.IXmppMessage;
import xmpp.processing.IXmppProcessor;

public abstract class AbstractQueueManager extends Thread implements
	IXmppProcessor {

    public AbstractQueueManager(IXmppMessageQueue queue) {
	if (queue == null)
	    throw new NullPointerException();

	this.queue = queue;
	terminate = new AtomicBoolean();
    }

    public void terminate() {
	terminate.set(true);
	start();
    }

    @Override
    public void run() {
	while (!terminate.get()) {
	    try {
		IXmppMessage msg = queue.poll();
		if (msg != null)
		    processMessage(msg);

		Thread.sleep(50);
	    } catch (InterruptedException e) {
		e.printStackTrace();
		terminate();
	    }
	}
    }

    AtomicBoolean terminate;
    IXmppMessageQueue queue;
}
