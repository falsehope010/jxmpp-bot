package xmpp.queue;

import xmpp.messaging.base.Message;

public interface IXmppMessageQueue {
    void add(Message msg);

    Message poll();
}
