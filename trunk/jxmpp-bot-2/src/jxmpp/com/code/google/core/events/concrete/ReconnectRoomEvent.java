package jxmpp.com.code.google.core.events.concrete;

import jxmpp.com.code.google.core.events.CompositeEvent;

/**
 * Created by IntelliJ IDEA.
 * User: ternovykh
 * Date: 29.07.11
 * Time: 17:48
 */
public class ReconnectRoomEvent implements CompositeEvent
{
    public ReconnectRoomEvent(String room, String nick)
    {
        setRoom(room);
        setNick(nick);
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
