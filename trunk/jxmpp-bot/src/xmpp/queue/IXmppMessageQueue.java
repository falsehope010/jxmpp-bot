package xmpp.queue;

import xmpp.messaging.Message;

public interface IXmppMessageQueue {
    void add(Message msg);

    Message poll();
}
