package jxmpp.com.code.google.core.processors;

import org.jivesoftware.smack.packet.Packet;

/**
 * Created by IntelliJ IDEA.
 * User: ternovykh
 * Date: 29.07.11
 * Time: 17:59
 */
public interface Processor
{
    void process(Packet packet);
}
