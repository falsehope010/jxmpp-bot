package jxmpp.com.code.google.core.configuration;

/**
 * Created by IntelliJ IDEA.
 * User: ternovykh
 * Date: 27.07.11
 * Time: 10:56
 * To change this template use File | Settings | File Templates.
 */
public interface Configuration
{
    String getHost();

    int getPort();

    String getUserName();

    String getPassword();

    String getResource();

    String[] getOwners();

    RoomConfiguration[] getRoomsConfigurations();
}
