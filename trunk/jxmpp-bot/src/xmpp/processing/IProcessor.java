package xmpp.processing;

import xmpp.messaging.Message;

/**
 * Represents handler that can process {@link Message} instances and possibly
 * perform some action according to message contents
 * 
 * @author tillias
 * 
 */
public interface IProcessor {
    /**
     * Performs some action on given {@link Message}
     * 
     * @param msg
     *            Message to be processed
     */
    void processMessage(Message msg);
}
