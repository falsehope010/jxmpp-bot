package xmpp.watchers;

import syslog.ILog;
import xmpp.watchers.AbstractActivityWatcher;

public class ActivityWatcherMock extends AbstractActivityWatcher {

    public ActivityWatcherMock(ILog log) throws NullPointerException {
	super(log);
    }

    @Override
    public boolean checkActivityAlive() {
	return isAlive;
    }

    @Override
    public void logActivityAlive() {
	// TODO Auto-generated method stub

    }

    @Override
    public void logActivityDown() {
	// TODO Auto-generated method stub

    }

    @Override
    public void logActivityException(Exception e) {
	// TODO Auto-generated method stub

    }

    @Override
    public void startActivity() {
	isAlive = true;

    }

    boolean isAlive;
}
