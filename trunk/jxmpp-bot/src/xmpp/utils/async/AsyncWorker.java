package xmpp.utils.async;

public abstract class AsyncWorker implements Runnable, IAsyncWorker {

    public AsyncWorker(int timeout) {
	terminate = false;
	this.timeout = timeout;
    }

    public void start() {
	if (!isAlive()) {
	    thread = new Thread(this);
	    thread.start();
	}
    }

    public void stop() {
	terminate = true;
    }

    @Override
    public void run() {
	try {
	    while (!terminate) {
		performAction();

		Thread.sleep(getTimeout());
	    }
	} catch (Exception e) {
	    // nothing todo here
	}
    }

    public boolean isAlive() {
	boolean result = false;

	if (thread != null && thread.isAlive()) {
	    result = true;
	}

	return result;
    }

    public int getTimeout() {
	return timeout;
    }

    boolean terminate;
    Thread thread;
    int timeout;
}
