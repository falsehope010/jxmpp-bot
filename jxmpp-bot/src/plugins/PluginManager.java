package plugins;

import java.util.ArrayList;
import java.util.List;

import syslog.ILog;
import xmpp.messaging.base.Message;
import xmpp.processing.IProcessor;
import xmpp.queue.IMessageQueue;
import activity.ActivityUtils;
import activity.IActive;

public class PluginManager implements IProcessor, IActive {

    public PluginManager(ILog log) {
	if (log == null)
	    throw new NullPointerException("Log can't be null");

	this.log = log;

	pluginsCollection = new ArrayList<IPlugin>();

	loadPlugins();
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation loops through all underlying plugins and checks
     * whether there are any which can process given message or not. If there
     * are found any plugins which can process given message method call is
     * redirected to them
     * <p>
     * If any exception is thrown by plugin it is caught inside this method and
     * detailed message is put into log
     */
    @Override
    public void processMessage(Message msg) {
	if (msg != null) {
	    for (IPlugin p : pluginsCollection) {
		if (p.canProcess(msg)) {
		    try {
			p.processMessage(msg);
		    } catch (Exception e) {
			log.putMessage("Exception in plugin has been caught. "
				+ e.getMessage(), p.getName() + " "
				+ p.getVersion(), "Plugins", "Exception");
		    }
		}
	    }
	}
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation sets transport queue for all underlying plugins
     */
    @Override
    public void setTransport(IMessageQueue queue) {
	for (IPlugin p : pluginsCollection) {
	    p.setTransport(queue);
	}
    }

    @Override
    public boolean isAlive() {
	return isRunning;
    }

    /**
     * Attempts to start all plugins which are managed by this plugin manager.
     * Plugins must implement {@link IActive} interface in order to be started
     * correctly
     */
    @Override
    public void start() {
	for (IPlugin p : pluginsCollection) {
	    ActivityUtils.start(p);
	}

	setRunning(true);
    }

    /**
     * Attempts to stop all plugins which are managed by this plugin manager.
     * Plugins must implement {@link IActive} interface in order to be stopped
     * correctly
     */
    @Override
    public void stop() {
	for (IPlugin p : pluginsCollection) {
	    ActivityUtils.stop(p);
	}
	setRunning(false);
    }

    @Override
    public void run() {
	// Nothing todo here. Plugin uses pluginsCollection to manage active
	// objects currently
    }

    /**
     * Loads all plugins into internal collection.
     * 
     * @see #registerPlugin(IPlugin)
     */
    protected void loadPlugins() {
	// stub method for future needs
    }

    /**
     * Registers given plugin inside internal collection of plugins of this
     * manager. If argument is null or plugin is already registered method does
     * nothing.
     * 
     * @param plugin
     *            Plugin to be registered
     * @see #loadPlugins()
     */
    protected void registerPlugin(IPlugin plugin) {
	if (plugin != null && !pluginsCollection.contains(plugin)) {
	    pluginsCollection.add(plugin);
	}
    }

    /**
     * Gets total number of plugins controlled by this plugin manager
     * 
     * @return Total number of plugins controlled by plugin manager
     */
    protected int getPluginsCount() {
	return pluginsCollection.size();
    }

    private void setRunning(boolean value) {
	this.isRunning = value;
    }

    ILog log;
    List<IPlugin> pluginsCollection;

    boolean isRunning;

}
