package xmpp.listeners;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import xmpp.core.IConnection;
import xmpp.helpers.PacketGenerator;
import xmpp.messaging.PrivateChatMessage;
import xmpp.messaging.PrivateMessage;
import xmpp.messaging.base.Message;
import xmpp.messaging.domain.ParticipantInfo;
import xmpp.processing.IProcessor;

public class PrivateMessageListenerTest {

    @Test(expected = NullPointerException.class)
    public void testPrivateMessageListenerFailNullProcessor() {
	IConnection conn = new ConnectionMoc();
	PrivateMessageListener listener = new PrivateMessageListener(conn, null);
	assertNull(listener);
    }

    @Test(expected = NullPointerException.class)
    public void testPrivateMessageListenerFailNullParentConnection() {
	IProcessor processor = new ProcessorMoc();
	PrivateMessageListener listener = new PrivateMessageListener(null,
		processor);
	assertNull(listener);
    }

    @Test
    public void testPrivateMessageListener() {
	IConnection conn = new ConnectionMoc();
	IProcessor processor = new ProcessorMoc();
	PrivateMessageListener listener = new PrivateMessageListener(conn,
		processor);
	assertNotNull(listener);
    }

    @Test
    public void testProcessNullPacket() {
	IConnection conn = new ConnectionMoc();
	ProcessorMoc processor = new ProcessorMoc();
	PrivateMessageListener listener = new PrivateMessageListener(conn,
		processor);
	assertNotNull(listener);

	listener.processPacket(null);
	assertNull(processor.get());
    }

    @Test
    public void testProcessPrivatePacketSimple() {
	IConnection conn = new ConnectionMoc();
	ProcessorMoc processor = new ProcessorMoc();
	PrivateMessageListener listener = new PrivateMessageListener(conn,
		processor);
	assertNotNull(listener);

	PacketGenerator generator = new PacketGenerator();

	org.jivesoftware.smack.packet.Message msg = generator
		.createPrivateMessage();
	assertNotNull(msg);

	listener.processPacket(msg);

	Message processedMsg = processor.get();
	assertNotNull(processedMsg);
	assertTrue(processedMsg instanceof PrivateMessage);
    }

    @Test
    public void processPrivateChatPacketSimple() {

	final String roomName = "room@xmpp.org";
	final String occupantName = "room@xmpp.org/occupant";
	final String occupantJid = "occupant@xmpp.org/resource";

	ConnectionMoc conn = new ConnectionMoc();
	RoomMock roomMock = new RoomMock(roomName);
	roomMock.addJid(occupantName, occupantJid);
	conn.addRoom(roomMock);

	ProcessorMoc processor = new ProcessorMoc();
	PrivateMessageListener listener = new PrivateMessageListener(conn,
		processor);
	assertNotNull(listener);

	PacketGenerator generator = new PacketGenerator();

	org.jivesoftware.smack.packet.Message msg = generator
		.createPrivateChatMessage(occupantName);
	assertNotNull(msg);

	listener.processPacket(msg);

	Message processedMsg = processor.get();
	assertNotNull(processedMsg);
	assertTrue(processedMsg instanceof PrivateChatMessage);
    }

    @Test
    public void testProcessPrivatePacketCompare() {
	ProcessorMoc processor = new ProcessorMoc();
	assertNotNull(processor);

	IConnection conn = new ConnectionMoc();

	PrivateMessageListener listener = new PrivateMessageListener(conn,
		processor);
	assertNotNull(listener);

	PacketGenerator generator = new PacketGenerator();
	org.jivesoftware.smack.packet.Message source = generator
		.createPrivateMessage();
	listener.processPacket(source);

	Message msg = processor.get();
	assertTrue(msg instanceof PrivateMessage);

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
    public void testProcessPrivateChatPacketCompare() {
	final String roomName = "room@xmpp.org";
	final String occupantName = "room@xmpp.org/occupant";
	final String occupantJid = "occupant@xmpp.org/resource";

	ConnectionMoc conn = new ConnectionMoc();
	RoomMock roomMock = new RoomMock(roomName);
	roomMock.addJid(occupantName, occupantJid);
	conn.addRoom(roomMock);

	ProcessorMoc processor = new ProcessorMoc();
	PrivateMessageListener listener = new PrivateMessageListener(conn,
		processor);
	assertNotNull(listener);

	PacketGenerator generator = new PacketGenerator();

	org.jivesoftware.smack.packet.Message msg = generator
		.createPrivateChatMessage(occupantName);
	assertNotNull(msg);

	listener.processPacket(msg);

	Message processedMsg = processor.get();
	assertNotNull(processedMsg);
	assertTrue(processedMsg instanceof PrivateChatMessage);

	// compare fields
	assertEquals(occupantJid, processedMsg.getSender().getJabberID());

	PrivateChatMessage privateChatMsg = (PrivateChatMessage) processedMsg;
	assertNotNull(privateChatMsg);

	assertEquals(roomName, privateChatMsg.getRoomName());
	assertEquals(occupantName, privateChatMsg.getSender().getAdress());
	assertEquals(msg.getBody(), privateChatMsg.getText());
	assertNotNull(privateChatMsg.getTimestamp());
    }

    @Test
    public void testProcessOnlyPrivateChatOrPrivateMessages() {

	ProcessorMoc processor = new ProcessorMoc();
	assertNotNull(processor);

	IConnection conn = new ConnectionMoc();

	PrivateMessageListener listener = new PrivateMessageListener(conn,
		processor);
	assertNotNull(listener);

	PacketGenerator generator = new PacketGenerator();

	org.jivesoftware.smack.packet.Message msg = generator
		.createPrivateMessage(org.jivesoftware.smack.packet.Message.Type.error);
	assertNotNull(msg);
	listener.processPacket(msg);
	assertNull(processor.get());

	msg = generator
		.createPrivateMessage(org.jivesoftware.smack.packet.Message.Type.groupchat);
	assertNotNull(msg);
	listener.processPacket(msg);
	assertNull(processor.get());

	msg = generator
		.createPrivateMessage(org.jivesoftware.smack.packet.Message.Type.headline);
	assertNotNull(msg);
	listener.processPacket(msg);
	assertNull(processor.get());

	msg = generator
		.createPrivateMessage(org.jivesoftware.smack.packet.Message.Type.normal);
	assertNotNull(msg);
	listener.processPacket(msg);
	assertNull(processor.get());

	msg = generator
		.createPrivateMessage(org.jivesoftware.smack.packet.Message.Type.chat);
	assertNotNull(msg);
	listener.processPacket(msg);
	assertNotNull(processor.get());
    }

    @Test
    public void testEmptySenderMessageNotComeToProcessor() {
	ProcessorMoc processor = new ProcessorMoc();
	assertNotNull(processor);

	IConnection conn = new ConnectionMoc();

	PrivateMessageListener listener = new PrivateMessageListener(conn,
		processor);
	assertNotNull(listener);

	PacketGenerator generator = new PacketGenerator();
	listener.processPacket(generator.createPrivateMessage(null, null,
		"john_doe@xmpp.org", "resource"));
	assertNull(processor.get());

	listener.processPacket(generator.createPrivateMessage(null,
		"sender_resource", "john_doe@xmpp.org", "resource"));
	assertNull(processor.get());

	listener.processPacket(generator.createPrivateMessage(
		"sender@xmpp.org", null, "john_doe@xmpp.org", "resource"));
	assertNull(processor.get());
    }

    @Test
    public void testEmptyRecipientMessageNotComeToProcessor() {
	ProcessorMoc processor = new ProcessorMoc();
	assertNotNull(processor);

	IConnection conn = new ConnectionMoc();

	PrivateMessageListener listener = new PrivateMessageListener(conn,
		processor);
	assertNotNull(listener);

	PacketGenerator generator = new PacketGenerator();
	listener.processPacket(generator.createPrivateMessage(
		"sender@xmpp.org", "sender_resource", null, null));
	assertNull(processor.get());

	listener.processPacket(generator.createPrivateMessage(
		"sender@xmpp.org", "sender_resource", null, "resource"));
	assertNull(processor.get());

	listener.processPacket(generator.createPrivateMessage(
		"sender@xmpp.org", "sender_resource", "recipient@xmpp.org",
		null));
	assertNull(processor.get());
    }

    @Test
    public void testEmptyBodyMessageNotComeToProcessor() {
	ProcessorMoc processor = new ProcessorMoc();
	assertNotNull(processor);

	IConnection conn = new ConnectionMoc();

	PrivateMessageListener listener = new PrivateMessageListener(conn,
		processor);
	assertNotNull(listener);

	PacketGenerator generator = new PacketGenerator();
	org.jivesoftware.smack.packet.Message msg = generator
		.createPrivateMessage();
	msg.setBody(null);

	listener.processPacket(msg);
	assertNull(processor.get());
    }

    @Test
    public void testIllegalSenderNotComeToProcessor() {
	ProcessorMoc processor = new ProcessorMoc();
	assertNotNull(processor);

	IConnection conn = new ConnectionMoc();

	PrivateMessageListener listener = new PrivateMessageListener(conn,
		processor);
	assertNotNull(listener);

	PacketGenerator generator = new PacketGenerator();

	listener.processPacket(generator.createPrivateMessage(
		"sender@xmpp.org", null, "john_doe@xmpp.org", "resource"));
	assertNull(processor.get());

	listener.processPacket(generator.createPrivateMessage(null,
		"sender_resource", "john_doe@xmpp.org", "resource"));
	assertNull(processor.get());

    }

    @Test
    public void testIllegalRecipientNotComeToProcessor() {
	ProcessorMoc processor = new ProcessorMoc();
	assertNotNull(processor);

	IConnection conn = new ConnectionMoc();

	PrivateMessageListener listener = new PrivateMessageListener(conn,
		processor);
	assertNotNull(listener);

	PacketGenerator generator = new PacketGenerator();

	listener.processPacket(generator.createPrivateMessage(
		"sender@xmpp.org", "sender_resource", null, "resource"));
	assertNull(processor.get());

	listener.processPacket(generator.createPrivateMessage(
		"sender@xmpp.org", "sender_resource", "recipient@xmpp.org",
		null));
	assertNull(processor.get());
    }
}
