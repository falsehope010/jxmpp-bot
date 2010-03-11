package plugins.moc;

import plugins.IPlugin;
import xmpp.messaging.base.Message;
import xmpp.queue.IMessageQueue;

public class PluginMocThrowsExceptions implements IPlugin {

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
	throw new RuntimeException();
    }

    @Override
    public void setTransport(IMessageQueue queue) {
	// TODO Auto-generated method stub

    }
}
