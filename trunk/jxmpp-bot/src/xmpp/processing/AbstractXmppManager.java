package xmpp.processing;

import xmpp.messaging.IXmppMessage;
import xmpp.queue.IXmppMessageQueue;

public class AbstractXmppManager implements IXmppProcessor, Runnable {

    public AbstractXmppManager(IXmppMessageQueue queue) {
	if (queue == null)
	    throw new NullPointerException();

	this.queue = queue;
    }

    @Override
    public void processMessage(IXmppMessage msg) {
	System.out.println(msg + "\n");
    }

    public void stop() {
	terminate = true;
    }

    @Override
    public void run() {
	while (!terminate) {
	    try {
		IXmppMessage msg = queue.poll();
		if (msg != null)
		    processMessage(msg);

		Thread.sleep(100);
	    } catch (InterruptedException e) {
		e.printStackTrace();
		stop();
	    }
	}
    }

    boolean terminate = false;
    IXmppMessageQueue queue;
}
