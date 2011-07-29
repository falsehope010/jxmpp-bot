package jxmpp.com.code.google.core.listeners;

import jxmpp.com.code.google.core.processors.CommonProcessor;
import org.apache.log4j.Logger;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Packet;

import javax.inject.Inject;

/**
 * Created by IntelliJ IDEA.
 * User: ternovykh
 * Date: 29.07.11
 * Time: 15:14
 */

public class CommonPacketListener extends PacketListenerBase implements PacketListener
{
    private static final Logger log = Logger.getLogger(CommonPacketListener.class.getName());

    @Inject
    public CommonPacketListener(CommonProcessor processor)
    {
        super(processor);

        log.info("Creating packet listener " + hashCode());
    }

    @Override
    public void processPacket(Packet packet)
    {
        getProcessor().process(packet);
    }
}
