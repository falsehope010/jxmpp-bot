package xmpp.helpers.domain;

import java.util.concurrent.atomic.AtomicBoolean;

import xmpp.messaging.base.Message;
import xmpp.queue.IMessageQueue;

public abstract class AbstractMessageGenerator extends Thread {

    public AbstractMessageGenerator(IMessageQueue queue) {
	if (queue == null)
	    throw new NullPointerException();

	this.queue = queue;
	terminate = new AtomicBoolean();
    }

    public void terminate() {
	terminate.set(false);
    }

    public abstract Message generateMessage();

    @Override
    public void run() {
	while (!terminate.get()) {
	    try {
		Message msg = generateMessage();
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
    IMessageQueue queue;
}
