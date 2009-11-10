package xmpp;

import xmpp.message.IXmppMessage;

public interface IXmppManager {
    void processMessage(IXmppMessage msg);
}
