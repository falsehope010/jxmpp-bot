package xmpp.utils.activity;

import syslog.ILog;

public class ConnectionWatcher extends AbstractActivityWatcher {

    public ConnectionWatcher(ILog log, int pollTimeout) throws NullPointerException,
	    IllegalArgumentException {
	super(log, pollTimeout);
	// TODO Auto-generated constructor stub
    }

    @Override
    public boolean checkActivityAlive() {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public void logActivityAlive() {
	getLog().putMessage("XMPP connection is up.", logSenderName, "Connectivity",
		"Up");
    }

    @Override
    public void logActivityDown() {
	log.putMessage("XMPP connection is down. Reconnecting.", logSenderName,
		"Connectivity", "Down");
    }

    @Override
    public void logActivityException(Exception e) {

	log.putMessage(e.getMessage(), logSenderName, "Errors", "Exception");

    }

    @Override
    public void startActivity() {
	System.out.println("Starting activity");

    }

}
