package xmpp;

import xmpp.message.IXmppMessage;

public interface IXmppMessageQueue {
    void add(IXmppMessage msg);

    IXmppMessage poll();
}
