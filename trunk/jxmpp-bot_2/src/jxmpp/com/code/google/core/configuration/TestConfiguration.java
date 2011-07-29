package jxmpp.com.code.google.core.configuration;

/**
 * Created by IntelliJ IDEA.
 * User: ternovykh
 * Date: 28.07.11
 * Time: 14:43
 */
public class TestConfiguration implements Configuration
{
    @Override
    public String getHost()
    {
        return "jabber-br.org";
    }

    @Override
    public int getPort()
    {
        return 5222;
    }

    @Override
    public String getUserName()
    {
        return "jxmpp";
    }

    @Override
    public String getPassword()
    {
        return "qwerty123";
    }

    @Override
    public String getResource()
    {
        return "TheBot";
    }

    @Override
    public String[] getOwners()
    {
        return new String[]{"tillias.work@qip.ru"};
    }

    @Override
    public RoomConfiguration[] getRoomsConfigurations()
    {
        return new RoomConfiguration[]{
                new RoomConfiguration("christian@conference.jabber.ru", "TheBot")
        };
    }
}
