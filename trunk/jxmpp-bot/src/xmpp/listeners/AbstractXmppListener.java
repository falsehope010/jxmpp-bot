package xmpp.listeners;

import xmpp.messaging.IXmppMessage;
import xmpp.processing.IXmppProcessor;

/**
 * Base class for all xmpp listeners
 * 
 * @author tillias
 * @see XmppPacketListener
 * 
 */
public class AbstractXmppListener {
    /**
     * Creates new instance of listener using given {@link IXmppProcessor}
     * 
     * @param processor
     *            Processor which will be used to process new
     *            {@link IXmppMessage} instances when they will be created by
     *            this listener
     * @throws NullPointerException
     *             Thrown if processor argument passed to constructor is null
     */
    public AbstractXmppListener(IXmppProcessor processor)
	    throws NullPointerException {
	if (processor == null)
	    throw new NullPointerException();

	this.processor = processor;
    }

    /**
     * Forwards message to the underlying {@link IXmppProcessor}
     * 
     * @param msg
     *            Message to be forwared
     */
    public void processMessage(IXmppMessage msg) {
	if (msg != null)
	    processor.processMessage(msg);
    }

    IXmppProcessor processor;
}
