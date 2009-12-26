package xmpp.queue;

import xmpp.messaging.base.Message;

/**
 * Implements queue of XMPP {@link Message} items
 * 
 * @author tillias
 * 
 */
public interface IMessageQueue {
    /**
     * Puts message to the tail of the queue
     * 
     * @param msg
     *            {@link Message} to be added
     */
    void add(Message msg);

    /**
     * Retrieves and removes the head of this queue, or returns null if this
     * queue is empty.
     * 
     * @return Head of the queue or null if queue is empty
     */
    Message poll();
}
