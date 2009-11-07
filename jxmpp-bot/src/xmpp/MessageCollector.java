package xmpp;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

public class MessageCollector implements PacketListener {

    public MessageCollector() {
	messages = new ConcurrentLinkedQueue<Message>();
    }

    @Override
    public void processPacket(Packet packet) {

	if (packet instanceof Message) {
	    Message messagePacket = (Message) packet;

	    messages.add(messagePacket);
	}
    }

    public Message poll() {
	return messages.poll();
    }

    ConcurrentLinkedQueue<Message> messages;

}
