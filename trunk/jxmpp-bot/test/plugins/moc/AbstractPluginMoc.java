package plugins.moc;

import plugins.AbstractPlugin;
import xmpp.messaging.base.Message;

public class AbstractPluginMoc extends AbstractPlugin {

    @Override
    protected void processMessageSynchronous(Message msg) {
	++processedMessagesCount;
    }

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

    public int getProcessedMessagesCount() {
	return processedMessagesCount;
    }

    public void setProcessedMessagesCount(int processedMessagesCount) {
	this.processedMessagesCount = processedMessagesCount;
    }

    int processedMessagesCount;
}
