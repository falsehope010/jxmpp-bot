package xmpp.transport;

import xmpp.messaging.IXmppMessage;

public interface IXmppTransport {
    void send(IXmppMessage msg);
}
