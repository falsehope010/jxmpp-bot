package jxmpp.com.code.google.core.processors;

import jxmpp.com.code.google.core.configuration.Configuration;
import jxmpp.com.code.google.core.events.EventAggregator;
import org.apache.log4j.Logger;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by IntelliJ IDEA.
 * User: ternovykh
 * Date: 29.07.11
 * Time: 16:59
 */
@Singleton
public class CommonProcessor implements Processor
{
    private static final Logger log = Logger.getLogger(CommonProcessor.class.getName());

    @Inject
    public CommonProcessor(EventAggregator eventAggregator, Configuration configuration, PresenceProcessor presenceProcessor)
    {
        this.presenceProcessor = presenceProcessor;
    }

    @Override
    public void process(Packet packet)
    {
        if (packet instanceof Presence)
        {
            presenceProcessor.process(packet);
        }

        if (packet instanceof Message)
        {
            Message message = (Message) packet;
            log.info(("Received packet message: " + message.getFrom() + " " + message.getBody()));
        }
    }

    private PresenceProcessor presenceProcessor;
}
