package plugins.moc;

import xmpp.messaging.base.Message;
import xmpp.queue.IMessageQueue;

public class MessageQueueMoc implements IMessageQueue {

    @Override
    public void add(Message msg) {
	++messagesCount;
    }

    @Override
    public void clear() {
	messagesCount = 0;
    }

    @Override
    public Message poll() {
	--messagesCount;
	return null;
    }

    public int getMessagesCount() {
	return messagesCount;
    }

    public void setMessagesCount(int messagesCount) {
	this.messagesCount = messagesCount;
    }

    int messagesCount;

}
