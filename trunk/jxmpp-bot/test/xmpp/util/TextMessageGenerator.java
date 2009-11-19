package xmpp.util;

import xmpp.messaging.Message;
import xmpp.queue.IXmppMessageQueue;

public class TextMessageGenerator extends AbstractMessageGenerator {

    public TextMessageGenerator(IXmppMessageQueue queue) {
	super(queue);
    }

    @Override
    public Message generateMessage() {
	return new Message() {
	    // empty class
	};
    }

}
