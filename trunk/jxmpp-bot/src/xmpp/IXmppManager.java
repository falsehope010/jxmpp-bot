package xmpp;

import xmpp.message.IXmppMessage;

public interface IXmppManager {
    void processTextMessage(IXmppMessage msg);
}
