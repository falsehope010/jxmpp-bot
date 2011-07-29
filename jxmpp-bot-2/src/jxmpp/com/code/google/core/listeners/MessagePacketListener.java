package jxmpp.com.code.google.core.listeners;

import org.apache.log4j.Logger;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

/**
 * Created by IntelliJ IDEA.
 * User: ternovykh
 * Date: 28.07.11
 * Time: 16:36
 */
public class MessagePacketListener implements PacketListener
{
    private static final Logger log = Logger.getLogger(MessagePacketListener.class.getName());

    @Override
    public void processPacket(Packet packet)
    {
        if (packet instanceof Message)
        {
            Message message = (Message) packet;
            //log.info(message.getClass().getName() + " " + message.getType() + " " + message.getFrom() + " " + message.getBody());
        }
    }
}
