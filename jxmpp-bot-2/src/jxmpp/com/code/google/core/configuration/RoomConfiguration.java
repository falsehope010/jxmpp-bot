package jxmpp.com.code.google.core.configuration;

/**
 * Created by IntelliJ IDEA.
 * User: ternovykh
 * Date: 28.07.11
 * Time: 19:20
 */
public class RoomConfiguration
{
    public RoomConfiguration(String room, String nick)
    {
        this.room = room;
        this.nick = nick;
    }

    public String getRoom()
    {
        return room;
    }

    public void setRoom(String room)
    {
        this.room = room;
    }

    public String getNick()
    {
        return nick;
    }

    public void setNick(String nick)
    {
        this.nick = nick;
    }

    private String room;
    private String nick;
}
