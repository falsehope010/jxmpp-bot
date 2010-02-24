package xmpp.processing;

import xmpp.messaging.base.Message;
import xmpp.queue.IMessageQueue;

public class MessageProcessor implements IProcessor {

    @Override
    public void processMessage(Message msg) {
	// TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * Sets transport queue for this message processor.
     * 
     * @param transportQueue
     */
    @Override
    public void setTransport(IMessageQueue transportQueue) {
	this.transportQueue = transportQueue;
    }

    IMessageQueue transportQueue;
}
