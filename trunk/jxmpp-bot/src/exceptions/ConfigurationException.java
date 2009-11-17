package exceptions;

import xmpp.configuration.Configuration;

/**
 * Represents exception that is thrown if any error has occurred during loading
 * {@link Configuration} from file or any validation error
 * 
 * @author tillias
 * @see Configuration
 * 
 */
public class ConfigurationException extends Exception {
    /**
     * 
     */
    private static final long serialVersionUID = 4757120132833816243L;

    public ConfigurationException(String message) {
	super(message);
    }
}
