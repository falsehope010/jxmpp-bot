package xmpp.processing;

import xmpp.messaging.IXmppMessage;

/**
 * Represents an object which can process {@link IXmppMessage} instances and
 * possibly perform some action according to message contents
 * 
 * @author tillias
 * 
 */
public interface IXmppProcessor {
    /**
     * Performs some action on given {@link IXmppMessage}
     * 
     * @param msg
     *            Message to be processed
     */
    void processMessage(IXmppMessage msg);
}
