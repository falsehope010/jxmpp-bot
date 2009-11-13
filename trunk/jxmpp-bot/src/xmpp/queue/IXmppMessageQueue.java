package xmpp.queue;

import xmpp.messaging.IXmppMessage;

public interface IXmppMessageQueue {
    void add(IXmppMessage msg);

    IXmppMessage poll();
}
