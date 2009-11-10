package xmpp.listeners;

import xmpp.IXmppMessageQueue;
import xmpp.message.IXmppMessage;

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
