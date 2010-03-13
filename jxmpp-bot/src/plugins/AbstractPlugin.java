package plugins;

import xmpp.messaging.base.Message;
import xmpp.queue.IMessageQueue;
import xmpp.queue.MessageQueue;
import activity.IActive;

/**
 * Skeletal implementation of {@link IPlugin} interface. Active class. Manages
 * it's own internal thread to perform asynchronous processing of messages.
 * Concrete implementations should override
 * {@link #processMessageSynchronous(Message)} method which does actual work.
 * 
 * @author tillias
 * @see IPlugin
 * @see IActive
 * @see PluginManager
 * 
 */
public abstract class AbstractPlugin implements IPlugin, IActive {

    /**
     * Sole constructor for invocation by subclasses
     */
    public AbstractPlugin() {
	messageQueue = new MessageQueue();
	thread = new Thread(this);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This is <b>asynchronous</b> implementation.
     * <p>
     * Puts given message (if not null) into internal plugin's message queue for
     * further processing. Concrete subclasses should override
     * {@link #processMessageSynchronous(Message)} which will do actual work
     */
    @Override
    public void processMessage(Message msg) {
	if (msg != null)
	    messageQueue.add(msg);
    }

    @Override
    public void setTransport(IMessageQueue queue) {
	this.transportQueue = queue;
    }

    /**
     * Starts plugin so it is able to process messages
     * 
     * @see #isAlive()
     * @see #stop()
     */
    @Override
    public void start() {
	if (!isAlive())
	    thread.start();
    }

    /**
     * Stops plugin.
     * 
     * @see #isAlive()
     * @see #start()
     */
    @Override
    public void stop() {
	if (isAlive())
	    setTerminate(true);
    }

    /**
     * Gets value indicating that plugin is currently running. It means that
     * plugin was started using {@link #start()} method but hasn't been yet
     * stopped
     * 
     * @see #start()
     * @see #stop()
     */
    @Override
    public boolean isAlive() {
	return thread.isAlive();
    }

    @Override
    public void run() {
	while (!terminate) {
	    try {
		Message msg = messageQueue.poll();

		if (msg != null) {
		    setProcessingMessage(true);

		    processMessageSynchronous(msg);

		    setProcessingMessage(false);
		}

		Thread.sleep(100);
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	}
    }

    /**
     * Gets value indicating that this plugin processing message at the moment
     * the method is called
     * 
     * @return True if this plugin processing message, false otherwise
     */
    public boolean isProcessingMessage() {
	return isProcessingMessage;
    }

    /**
     * Performs actual work on given message. Method must be overridden in
     * concrete subclasses.
     * 
     * @param msg
     *            Message to be processed by plugin
     * @see #sendResponse(Message)
     */
    protected abstract void processMessageSynchronous(Message msg);

    /**
     * Sends given message to XMPP server. Using this method plugin can send
     * response back to chat room, private chat or directly to another user
     * 
     * @param msg
     *            Message to be sent
     */
    protected void sendResponse(Message msg) {
	if (msg != null && transportQueue != null)
	    transportQueue.add(msg);
    }

    private void setTerminate(boolean value) {
	this.terminate = value;
    }

    private void setProcessingMessage(boolean value) {
	this.isProcessingMessage = value;
    }

    IMessageQueue transportQueue;
    IMessageQueue messageQueue;

    Thread thread;
    boolean terminate;

    boolean isProcessingMessage;
}
