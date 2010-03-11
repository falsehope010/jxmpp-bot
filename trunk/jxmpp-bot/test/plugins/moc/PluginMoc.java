package plugins.moc;

import plugins.IPlugin;
import xmpp.messaging.base.Message;
import xmpp.queue.IMessageQueue;

/**
 * Can process any message
 * 
 * @author tillias
 * 
 */
public class PluginMoc implements IPlugin {

    @Override
    public boolean canProcess(Message msg) {
	return true;
    }

    @Override
    public String getAuthor() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public String getDescription() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public String getName() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public String getVersion() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public void processMessage(Message msg) {
	++processedMessageCount;
    }

    @Override
    public void setTransport(IMessageQueue queue) {
	this.queue = queue;
    }

    public int getProcessedMessagesCount() {
	return processedMessageCount;
    }

    public void resetProcessedMessagesCount() {
	processedMessageCount = 0;
    }

    public IMessageQueue getTransportQueue() {
	return queue;
    }

    int processedMessageCount;
    IMessageQueue queue;

}
