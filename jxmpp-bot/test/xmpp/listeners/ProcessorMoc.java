package xmpp.listeners;

import java.util.LinkedList;

import xmpp.messaging.Message;
import xmpp.messaging.PrivateMessage;
import xmpp.processing.IProcessor;

public class ProcessorMoc implements IProcessor {

    public ProcessorMoc() {
	queue = new LinkedList<PrivateMessage>();
    }

    @Override
    public void processMessage(Message msg) {
	if (msg instanceof PrivateMessage)
	    queue.add((PrivateMessage) msg);

    }

    public PrivateMessage get() {
	return queue.poll();
    }

    LinkedList<PrivateMessage> queue;
}
