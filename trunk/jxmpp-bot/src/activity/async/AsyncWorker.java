package activity.async;

import activity.IActive;

/**
 * Abstract implementation of {@link IAsyncWorker}. Provides the set of methods
 * for starting, stopping and monitoring asynchronous action.
 * <p>
 * Concrete implementations should override {@link #performAction()} which will
 * be invoked periodically by this worker. This overridden method can throw
 * exceptions which will be absorbed by worker silently
 * 
 * Time period between invocations can be defined during construction of worker.
 * 
 * @author tilllias
 * 
 */
public abstract class AsyncWorker implements IActive, IAsyncWorker {

    /**
     * Creates new instance of worker using given time period between
     * invocations of {@link #performAction()}
     * 
     * @param invocationTimeout
     *            Time period between invocation of action
     * @throws IllegalArgumentException
     *             Thrown if invocationTimeout passed to constructor is not
     *             positive
     */
    public AsyncWorker(int invocationTimeout) throws IllegalArgumentException {
	if (invocationTimeout <= 0)
	    throw new IllegalArgumentException(
		    "InvocationTimeout can't be negative");
	terminate = false;
	this.actionTimeout = invocationTimeout;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation starts worker (if it is already started does
     * nothing). Worker will be invoking {@link #performAction()} periodically
     * until {@link #stop()} will be called.
     * 
     * @see #isAlive()
     * @see #stop()
     */
    public void start() {
	if (!isAlive()) {
	    thread = new Thread(this);
	    thread.start();
	}
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation stops worker. Method is asynchronous and doesn't
     * guarantee that worker has been stopped immediately.
     * 
     * @see #start()
     * @see #isAlive()
     */
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

    /**
     * {@inheritDoc}
     * <p>
     * This implementation gets value indicating whether worker is alive (e.g.
     * running). If worker has been created but not started returns false. If
     * worker has been created and then stopped returns false.
     * 
     * @return Value indicating that worker is running
     * @see #start()
     * @see #stop()
     */
    public boolean isAlive() {
	boolean result = false;

	if (thread != null && thread.isAlive()) {
	    result = true;
	}

	return result;
    }

    /**
     * Gets value indicating time period between invocations of
     * {@link #performAction()} method by worker
     * 
     * @return Time period between invocations of worker's action
     */
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
