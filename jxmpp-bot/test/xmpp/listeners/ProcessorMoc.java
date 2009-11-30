package xmpp.listeners;

import java.util.LinkedList;

import xmpp.messaging.PrivateChatMessage;
import xmpp.messaging.PrivateMessage;
import xmpp.messaging.base.Message;
import xmpp.processing.IProcessor;

/**
 * Represents mock around {@link IProcessor} interface. When processMessage() is
 * called the message itself is put into internal queue, so you can use get()
 * method on this mock to get a message
 * 
 * @author tillias
 * 
 */
public class ProcessorMoc implements IProcessor {

    public ProcessorMoc() {
	queue = new LinkedList<Message>();
    }

    @Override
    public void processMessage(Message msg) {
	if (msg instanceof PrivateMessage || msg instanceof PrivateChatMessage)
	    queue.add(msg);

    }

    public Message get() {
	return queue.poll();
    }

    LinkedList<Message> queue;
}
