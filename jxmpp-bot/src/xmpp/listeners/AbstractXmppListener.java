package xmpp.listeners;

import xmpp.messaging.IXmppMessage;
import xmpp.queue.IXmppMessageQueue;

public class AbstractXmppListener {
    public AbstractXmppListener(IXmppMessageQueue queue) {
	if (queue == null)
	    throw new NullPointerException();

	this.queue = queue;
    }

    public void add(IXmppMessage msg) {
	if (msg != null)
	    queue.add(msg);
    }

    IXmppMessageQueue queue;
}
