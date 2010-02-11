package xmpp.processing;

import xmpp.messaging.base.Message;
import xmpp.queue.IMessageQueue;

public class MessageProcessor implements IProcessor {

    @Override
    public void processMessage(Message msg) {
	// TODO Auto-generated method stub

    }

    /**
     * Sets transport queue for this message processor.
     * 
     * @param transportQueue
     */
    public void setTransport(IMessageQueue transportQueue) {
	this.transportQueue = transportQueue;
    }

    IMessageQueue transportQueue;
}
