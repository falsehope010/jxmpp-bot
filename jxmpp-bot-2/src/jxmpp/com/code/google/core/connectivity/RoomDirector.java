package jxmpp.com.code.google.core.connectivity;

import jxmpp.com.code.google.core.configuration.Configuration;
import jxmpp.com.code.google.core.configuration.RoomConfiguration;
import jxmpp.com.code.google.core.events.CompositeEvent;
import jxmpp.com.code.google.core.events.EventAggregator;
import jxmpp.com.code.google.core.events.EventSubscriber;
import jxmpp.com.code.google.core.events.concrete.ReconnectEvent;
import jxmpp.com.code.google.core.events.concrete.ReconnectRoomEvent;
import jxmpp.com.code.google.core.listeners.CommonPacketListener;
import org.apache.log4j.Logger;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;

import javax.inject.Inject;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: ternovykh
 * Date: 29.07.11
 * Time: 10:33
 */
public class RoomDirector implements EventSubscriber
{
    private static final Logger log = Logger.getLogger(RoomDirector.class.getName());

    @Inject
    private RoomDirector(EventAggregator eventAggregator, Configuration configuration, CommonPacketListener listener)
    {
        this.eventAggregator = eventAggregator;
        this.configuration = configuration;
        this.listener = listener;

        rooms = new ArrayList<MultiUserChat>();

        subscribeEvents();
    }

    public void run(XMPPConnection connection) throws XMPPException
    {
        this.connection = connection;
        initRooms();
    }

    @Override
    public void processEvent(CompositeEvent event)
    {
        if (event instanceof ReconnectEvent)
        {
            log.info("Caught reconnect event");

            for (MultiUserChat r : rooms)
            {
                RoomConfiguration rc = findRoomConfiguration(r.getRoom());
                if (rc != null)
                {
                    try
                    {
                        joinChat(r, rc.getNick());
                    } catch (XMPPException e)
                    {
                        log.error("Error while joining room", e);
                    }
                }
            }
        }

        if (event instanceof ReconnectRoomEvent)
        {
            ReconnectRoomEvent re = (ReconnectRoomEvent) event;
            MultiUserChat room = findRoom(re.getRoom());
            if (room != null)
            {
                try
                {
                    joinChat(room, re.getNick());
                } catch (XMPPException e)
                {
                    log.error("Error while joining room", e);
                }
            }
        }
    }

    private void initRooms() throws XMPPException
    {
        log.info("Started joining groupchats");

        RoomConfiguration[] roomConfigurations = configuration.getRoomsConfigurations();
        for (RoomConfiguration rc : roomConfigurations)
        {
            initRoom(rc);
        }

        log.info("All groupchats are joined");
    }

    private void initRoom(RoomConfiguration rc) throws XMPPException
    {
        MultiUserChat chat = new MultiUserChat(connection, rc.getRoom());
        chat.addMessageListener(listener);
        joinChat(chat, rc.getNick());

        rooms.add(chat);
    }

    private void subscribeEvents()
    {
        eventAggregator.subscribe(this, ReconnectEvent.class);
        eventAggregator.subscribe(this, ReconnectRoomEvent.class);
    }

    private RoomConfiguration findRoomConfiguration(String room)
    {
        for (RoomConfiguration rc : configuration.getRoomsConfigurations())
        {
            if (rc.getRoom().equals(room))
            {
                return rc;
            }
        }

        return null;
    }

    private MultiUserChat findRoom(String room)
    {
        for (MultiUserChat r : rooms)
        {
            if (r.getRoom().equals(room))
            {
                return r;
            }
        }

        return null;
    }

    private void joinChat(MultiUserChat chat, String nick) throws XMPPException
    {
        log.info("Joining " + chat.getRoom() + "/" + nick);

        DiscussionHistory history = new DiscussionHistory();
        history.setMaxStanzas(0);
        chat.join(nick, "", history, SmackConfiguration.getPacketReplyTimeout());

        log.info("Joined room");
    }

    private XMPPConnection connection;
    private EventAggregator eventAggregator;
    private Configuration configuration;
    private CommonPacketListener listener;

    private ArrayList<MultiUserChat> rooms;
}
