package xmpp.core;

import xmpp.messaging.base.Message;

/**
 * Sends message to recipient
 * 
 * @author tillias
 * @see Message
 * @see Message#getRecipient()
 * 
 */
public interface ITransport {

    /**
     * Sends message to recipient
     * 
     * @param msg
     *            Message to be sent
     */
    void send(Message msg);
}
