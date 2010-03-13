package plugins;

import xmpp.messaging.base.Message;
import xmpp.processing.IProcessor;
import activity.IActive;

/**
 * Represents base contract that every plugin should follow.
 * <p>
 * Concrete implementations of this interface are managed by
 * {@link PluginManager}. If plugin needs to use it's own thread(s) then
 * additionally {@link IActive} interface should be implemented.
 * <p>
 * Plugin manager during initializations checks whether given plugin implements
 * {@link IActive} interface and if so calls {@link IActive#start()} method.
 * When the plugin manager is stopped it calls {@link IActive#stop()} method.
 * <p>
 * See {@link AbstractPlugin} for skeletal implementation of this interface
 * 
 * @see AbstractPlugin
 * @see PluginManager
 * @see PluginManager#start()
 * @see PluginManager#stop()
 * 
 * @author tillias
 * 
 */
public interface IPlugin extends IProcessor {

    /**
     * Gets value indicating that this plugin can process given message.
     * <p>
     * When {@link PluginManager} receives new {@link Message} to be processed
     * it calls this method on every plugin in order to determine which plugin
     * (or plugins) is (are) responsible for processing message
     * 
     * @param msg
     *            {@link Message} to be checked
     * @return True if plugin can process given message, false otherwise
     */
    boolean canProcess(Message msg);

    /**
     * Gets plugin name
     * 
     * @return Plugin name
     */
    String getName();

    /**
     * Gets plugin version
     * 
     * @return Plugin version
     */
    String getVersion();

    /**
     * Gets plugin description
     * 
     * @return Plugin description
     */
    String getDescription();

    /**
     * Gets the author of this plugin
     * 
     * @return Plugin author
     */
    String getAuthor();
}
