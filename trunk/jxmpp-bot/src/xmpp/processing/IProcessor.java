package xmpp.processing;

import xmpp.messaging.base.Message;
import xmpp.queue.IMessageQueue;

/**
 * Represents handler that can process {@link Message} instances and possibly
 * perform some action according to message contents
 * 
 * @author tillias
 * 
 */
public interface IProcessor {
    /**
     * Performs some action on given {@link Message}
     * 
     * @param msg
     *            Message to be processed
     */
    void processMessage(Message msg);

    /**
     * Sets transport queue for given processor. Processor may need to send
     * responses when processing messages. To do so processor should create
     * message with response and put it into transport queue
     * 
     * @param queue
     *            Transport queue
     */
    void setTransport(IMessageQueue queue);
}
