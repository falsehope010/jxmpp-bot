package jxmpp.com.code.google.core.processors;

import jxmpp.com.code.google.core.configuration.Configuration;
import jxmpp.com.code.google.core.configuration.RoomConfiguration;
import jxmpp.com.code.google.core.events.EventAggregator;
import jxmpp.com.code.google.core.events.concrete.ReconnectRoomEvent;
import org.apache.log4j.Logger;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: ternovykh
 * Date: 29.07.11
 * Time: 17:57
 */
public class PresenceProcessor implements Processor
{
    private static final Logger log = Logger.getLogger(PresenceProcessor.class.getName());

    @Inject
    public PresenceProcessor(EventAggregator eventAggregator, Configuration configuration)
    {
        this.eventAggregator = eventAggregator;

        roomsSet = buildRoomsSet(configuration);
    }

    private HashSet<String> buildRoomsSet(Configuration configuration)
    {
        HashSet<String> result = new HashSet<String>();

        for (RoomConfiguration rc : configuration.getRoomsConfigurations())
        {
            result.add(rc.getRoom() + "/" + rc.getNick());
        }

        return result;
    }

    @Override
    public void process(Packet packet)
    {
        Presence presence = (Presence) packet;
        if (presence.getType() != Presence.Type.available &&
                roomsSet.contains(presence.getFrom()))
        {
            log.info("Reconnecting to room " + presence.getFrom());
            Matcher m = jidPattern.matcher(presence.getFrom());
            if (m.find())
            {
                ReconnectRoomEvent event = new ReconnectRoomEvent(m.group(1), m.group(2));
                eventAggregator.publish(event);
            }
        }
    }

    private HashSet<String> roomsSet;
    private EventAggregator eventAggregator;

    private static final Pattern jidPattern = Pattern.compile("(.+)/(.+)");
}
