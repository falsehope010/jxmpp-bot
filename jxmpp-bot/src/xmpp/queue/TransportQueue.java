package xmpp.queue;

import activity.async.AsyncWorker;
import xmpp.core.ITransport;
import xmpp.messaging.base.Message;

/**
 * Represents active queue object. Provides thread-safe {@link #add(Message)}
 * method which adds message to the tail of queue. Once {@link #start()} is
 * called this queue polls itself periodically for new messages and redirects
 * them to underlying {@link ITransport}
 * <p>
 * This queue is designed to be populated by multiple threads. But underlying
 * {@link ITransport} is invoked only by this queue. This way the access to the
 * transport is synchronized
 * 
 * @author tillias
 * 
 */
public class TransportQueue extends AsyncWorker implements IMessageQueue {

    /**
     * Creates new instance of queue using given transport and invocation
     * timeout
     * 
     * @param transport
     *            Concrete implementation of {@link ITransport} interface which
     *            will be used to send messages from this queue
     * @param invocationTimeout
     *            Timeout between invocations of underlying transport by this
     *            active queue
     * @throws NullPointerException
     *             Thrown if transport argument passed to constructor is null
     * @throws IllegalArgumentException
     *             Thrown if invocation timeout argument is not positive
     */
    public TransportQueue(ITransport transport, int invocationTimeout)
	    throws NullPointerException, IllegalArgumentException {
	super(invocationTimeout);

	if (transport == null)
	    throw new NullPointerException("Transport can't be null");

	this.transport = transport;
	queue = new MessageQueue();
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation polls queue for the next message and if the poll is
     * successful (e.g. poll returned not null element from the head of the
     * queue) invokes underlying transport to deliver this message to recipient.
     * If queue is empty does nothing
     */
    @Override
    public void performAction() {
	Message msg = poll();
	if (msg != null) {
	    transport.send(msg);
	}
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation is thread-safe
     */
    @Override
    public void add(Message msg) {
	if (msg != null)
	    queue.add(msg);
    }

    @Override
    public Message poll() {
	return queue.poll();
    }

    MessageQueue queue;
    ITransport transport;
}
