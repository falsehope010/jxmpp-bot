package jxmpp.com.code.google.core.connectivity;

import com.google.inject.Inject;
import jxmpp.com.code.google.core.configuration.Configuration;
import jxmpp.com.code.google.core.configuration.RoomConfiguration;
import jxmpp.com.code.google.core.events.EventAggregator;
import jxmpp.com.code.google.core.listeners.MessagePacketListener;
import jxmpp.com.code.google.core.listeners.PresencePacketListener;
import jxmpp.com.code.google.core.listeners.XmppConnectionListener;
import org.apache.log4j.Logger;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;

/**
 * Created by IntelliJ IDEA.
 * User: ternovykh
 * Date: 28.07.11
 * Time: 14:39
 */
public class ConnectionDirector
{
    private static final Logger log = Logger.getLogger(ConnectionDirector.class.getName());

    @Inject
    public ConnectionDirector(Configuration configuration, XmppConnectionListener connectionListener, EventAggregator eventAggregator)
    {
        this.configuration = configuration;
        this.connectionListener = connectionListener;
        this.eventAggregator = eventAggregator;
        connection = initConnection();
    }

    public void run() throws XMPPException
    {
        log.info("Establishing connection to server");
        connection.connect();
        log.info("Connected to server");

        connection.addConnectionListener(connectionListener);
        connection.addPacketListener(new MessagePacketListener(), new PacketTypeFilter(Message.class));
        connection.addPacketListener(new PresencePacketListener(), new PacketTypeFilter(Presence.class));

        log.info("Performing authentication");
        connection.login(configuration.getUserName(), configuration.getPassword());
        log.info("Authenticated to server");

        initRooms();

        while (true)
        {
            try
            {
                Thread.sleep(100);
            } catch (InterruptedException e)
            {
                log.error(e);
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
        log.info("Joining " + rc.getRoom() + "/" + rc.getNick());
        //TODO

        MultiUserChat chat = new MultiUserChat(connection, rc.getRoom());
        DiscussionHistory history = new DiscussionHistory();
        history.setMaxStanzas(0);
        chat.join(rc.getNick(), "", history, SmackConfiguration.getPacketReplyTimeout());

        log.info("Joined room");
    }

    private XMPPConnection initConnection()
    {
        ConnectionConfiguration connConf =
                new ConnectionConfiguration(configuration.getHost(), configuration.getPort());
        connConf.setSASLAuthenticationEnabled(true);

        //debug
        //connConf.setDebuggerEnabled(true);

        connection = new XMPPConnection(connConf);

        return connection;
    }

    private Configuration configuration;
    private XMPPConnection connection;
    private EventAggregator eventAggregator;
    private XmppConnectionListener connectionListener;
}
