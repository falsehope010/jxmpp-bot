package xmpp.queue.moc;

import xmpp.core.ITransport;
import xmpp.messaging.base.Message;

public class TransportMoc implements ITransport {

    public TransportMoc() {
	sentMessageCount = 0;
    }

    @Override
    public void send(Message msg) {

	if (msg != null)
	    ++sentMessageCount;
    }

    public int getSentMessageCount() {
	return sentMessageCount;
    }

    int sentMessageCount;
}
