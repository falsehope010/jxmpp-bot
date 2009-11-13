package xmpp.processing;

import xmpp.messaging.IXmppMessage;

public interface IXmppProcessor {
    void processMessage(IXmppMessage msg);
}
