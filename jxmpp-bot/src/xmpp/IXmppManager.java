package xmpp;

import xmpp.message.XmppMessage;

public interface IXmppManager {
    void processMessage(XmppMessage msg);
}
