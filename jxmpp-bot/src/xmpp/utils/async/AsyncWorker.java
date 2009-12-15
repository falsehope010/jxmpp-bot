package xmpp.utils.async;

/**
 * Abstract implementation of {@link IAsyncWorker}. Provides the set of methods
 * for starting, stopping and monitoring asynchronous action.
 * <p>
 * Concrete implementations should override {@link #performAction()} which will
 * be invoked periodically by this worker.
 * 
 * @author tilllias
 * 
 */
public abstract class AsyncWorker implements Runnable, IAsyncWorker {

    public AsyncWorker(int actionTimeout) {
	terminate = false;
	this.actionTimeout = actionTimeout;
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
	return actionTimeout;
    }

    /**
     * Gets underlying thread id if worker has been started, -1 otherwise
     * 
     * @return Identifier of underlying thread
     */
    public long getThreadID() {
	if (thread != null)
	    return thread.getId();

	return -1;
    }

    boolean terminate;
    Thread thread;
    int actionTimeout;
}
