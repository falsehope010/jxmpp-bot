package xmpp;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

public class MessageCollector implements PacketListener {

    public MessageCollector() {
	messages = new ConcurrentLinkedQueue<XmppMessage>();
    }

    @Override
    public void processPacket(Packet packet) {

	if (packet instanceof Message) {
	    Message messagePacket = (Message) packet;
	    XmppMessage msg = new XmppMessage(messagePacket);
	    messages.add(msg);
	}
    }

    public XmppMessage poll() {
	return messages.poll();
    }

    ConcurrentLinkedQueue<XmppMessage> messages;

}
