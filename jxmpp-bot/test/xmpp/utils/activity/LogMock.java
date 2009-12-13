package xmpp.utils.activity;

import syslog.ILog;

public class LogMock implements ILog {

    @Override
    public boolean putMessage(String text, String sender, String category,
	    String type) {
	++itemsCount;
	return true;
    }

    public int getItemsCount() {
	return itemsCount;
    }

    public void clear() {
	itemsCount = 0;
    }

    int itemsCount;
}
