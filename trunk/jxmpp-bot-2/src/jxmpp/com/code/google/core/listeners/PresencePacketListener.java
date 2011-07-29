package jxmpp.com.code.google.core.listeners;

import org.apache.log4j.Logger;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;

/**
 * Created by IntelliJ IDEA.
 * User: ternovykh
 * Date: 28.07.11
 * Time: 16:47
 */
public class PresencePacketListener implements PacketListener
{
    private static final Logger log = Logger.getLogger(PresencePacketListener.class.getName());

    @Override
    public void processPacket(Packet packet)
    {
        if (packet instanceof Presence)
        {
            Presence presence = (Presence) packet;
            //log.info(presence.getFrom());
        }
    }
}
