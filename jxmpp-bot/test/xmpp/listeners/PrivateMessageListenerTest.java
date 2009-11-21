package xmpp.listeners;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import xmpp.helpers.PacketGenerator;
import xmpp.messaging.Message;
import xmpp.messaging.ParticipantInfo;
import xmpp.messaging.PrivateMessage;
import xmpp.processing.IProcessor;

public class PrivateMessageListenerTest {

    @Test(expected = NullPointerException.class)
    public void testPrivateMessageListenerFailNullProcessor() {
	PrivateMessageListener listener = new PrivateMessageListener(null);
	assertNull(listener);
    }

    @Test
    public void testPrivateMessageListener() {
	PrivateMessageListener listener = getListener();
	assertNotNull(listener);
    }

    @Test
    public void testProcessNullPacket() {
	ProcessorMoc processor = new ProcessorMoc();
	assertNotNull(processor);

	PrivateMessageListener listener = new PrivateMessageListener(processor);
	assertNotNull(listener);

	listener.processPacket(null);
	assertNull(processor.get());
    }

    @Test
    public void testProcessPacket() {
	PrivateMessageListener listener = getListener();
	assertNotNull(listener);

	PacketGenerator generator = new PacketGenerator();

	org.jivesoftware.smack.packet.Message msg = generator
		.createPrivateChatMessage();
	assertNotNull(msg);

	listener.processPacket(msg);
    }

    @Test
    public void testProcessPacketCompareSource() {
	ProcessorMoc processor = new ProcessorMoc();
	assertNotNull(processor);

	PrivateMessageListener listener = new PrivateMessageListener(processor);
	assertNotNull(listener);

	PacketGenerator generator = new PacketGenerator();
	org.jivesoftware.smack.packet.Message source = generator
		.createPrivateChatMessage();
	listener.processPacket(source);

	PrivateMessage msg = processor.get();
	assertNotNull(msg);

	assertEquals(source.getBody(), msg.getText());
	assertNotNull(msg.getTimestamp());

	ParticipantInfo sender = msg.getSender();
	assertNotNull(sender);
	assertEquals(sender.getAdress(), source.getFrom());

	ParticipantInfo recipient = msg.getRecipient();
	assertNotNull(recipient);
	assertEquals(recipient.getAdress(), source.getTo());
    }

    @Test
    public void testProcessOnlyTextMessages() {

	ProcessorMoc processor = new ProcessorMoc();
	assertNotNull(processor);

	PrivateMessageListener listener = new PrivateMessageListener(processor);
	assertNotNull(listener);

	PacketGenerator generator = new PacketGenerator();

	org.jivesoftware.smack.packet.Message msg = generator
		.createPrivateChatMessage(org.jivesoftware.smack.packet.Message.Type.error);
	assertNotNull(msg);
	listener.processPacket(msg);
	assertNull(processor.get());

	msg = generator
		.createPrivateChatMessage(org.jivesoftware.smack.packet.Message.Type.groupchat);
	assertNotNull(msg);
	listener.processPacket(msg);
	assertNull(processor.get());

	msg = generator
		.createPrivateChatMessage(org.jivesoftware.smack.packet.Message.Type.headline);
	assertNotNull(msg);
	listener.processPacket(msg);
	assertNull(processor.get());

	msg = generator
		.createPrivateChatMessage(org.jivesoftware.smack.packet.Message.Type.normal);
	assertNotNull(msg);
	listener.processPacket(msg);
	assertNull(processor.get());

	msg = generator
		.createPrivateChatMessage(org.jivesoftware.smack.packet.Message.Type.chat);
	assertNotNull(msg);
	listener.processPacket(msg);
	assertNotNull(processor.get());
    }

    @Test
    public void testEmptySenderMessageNotComeToProcessor() {
	ProcessorMoc processor = new ProcessorMoc();
	assertNotNull(processor);

	PrivateMessageListener listener = new PrivateMessageListener(processor);
	assertNotNull(listener);

	PacketGenerator generator = new PacketGenerator();
	listener.processPacket(generator.createPrivateChatMessage(null, null,
		"john_doe@xmpp.org", "resource"));
	assertNull(processor.get());

	listener.processPacket(generator.createPrivateChatMessage(null,
		"sender_resource", "john_doe@xmpp.org", "resource"));
	assertNull(processor.get());

	listener.processPacket(generator.createPrivateChatMessage(
		"sender@xmpp.org", null, "john_doe@xmpp.org", "resource"));
	assertNull(processor.get());
    }

    @Test
    public void testEmptyRecipientMessageNotComeToProcessor() {
	ProcessorMoc processor = new ProcessorMoc();
	assertNotNull(processor);

	PrivateMessageListener listener = new PrivateMessageListener(processor);
	assertNotNull(listener);

	PacketGenerator generator = new PacketGenerator();
	listener.processPacket(generator.createPrivateChatMessage(
		"sender@xmpp.org", "sender_resource", null, null));
	assertNull(processor.get());

	listener.processPacket(generator.createPrivateChatMessage(
		"sender@xmpp.org", "sender_resource", null, "resource"));
	assertNull(processor.get());

	listener.processPacket(generator.createPrivateChatMessage(
		"sender@xmpp.org", "sender_resource", "recipient@xmpp.org",
		null));
	assertNull(processor.get());
    }

    @Test
    public void testEmptyBodyMessageNotComeToProcessor() {
	ProcessorMoc processor = new ProcessorMoc();
	assertNotNull(processor);

	PrivateMessageListener listener = new PrivateMessageListener(processor);
	assertNotNull(listener);

	PacketGenerator generator = new PacketGenerator();
	org.jivesoftware.smack.packet.Message msg = generator
		.createPrivateChatMessage();
	msg.setBody(null);

	listener.processPacket(msg);
	assertNull(processor.get());
    }

    @Test
    public void testIllegalSenderNotComeToProcessor() {
	ProcessorMoc processor = new ProcessorMoc();
	assertNotNull(processor);

	PrivateMessageListener listener = new PrivateMessageListener(processor);
	assertNotNull(listener);

	PacketGenerator generator = new PacketGenerator();

	listener.processPacket(generator.createPrivateChatMessage(
		"sender@xmpp.org", null, "john_doe@xmpp.org", "resource"));
	assertNull(processor.get());

	listener.processPacket(generator.createPrivateChatMessage(null,
		"sender_resource", "john_doe@xmpp.org", "resource"));
	assertNull(processor.get());

    }

    @Test
    public void testIllegalRecipientNotComeToProcessor() {
	ProcessorMoc processor = new ProcessorMoc();
	assertNotNull(processor);

	PrivateMessageListener listener = new PrivateMessageListener(processor);
	assertNotNull(listener);

	PacketGenerator generator = new PacketGenerator();

	listener.processPacket(generator.createPrivateChatMessage(
		"sender@xmpp.org", "sender_resource", null, "resource"));
	assertNull(processor.get());

	listener.processPacket(generator.createPrivateChatMessage(
		"sender@xmpp.org", "sender_resource", "recipient@xmpp.org",
		null));
	assertNull(processor.get());
    }

    private PrivateMessageListener getListener() {
	PrivateMessageListener listener = new PrivateMessageListener(
		new IProcessor() {

		    @Override
		    public void processMessage(Message msg) {
			// method stun
		    }
		});
	assertNotNull(listener);
	return listener;
    }
}
