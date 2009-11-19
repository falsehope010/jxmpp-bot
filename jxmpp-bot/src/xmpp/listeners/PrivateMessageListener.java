package xmpp.listeners;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import xmpp.messaging.PrivateMessage;
import xmpp.processing.IProcessor;

/**
 * Represents {@link PacketListener} implementation that converts
 * <code>SMACK</code> {@link Message} packets into {@link PrivateMessage}
 * instances
 * <p>
 * When <code>SMACK</code> packet is converted it is sent to {@link IProcessor}
 * for further processing
 * 
 * @author tillias
 * 
 */
public class PrivateMessageListener implements PacketListener {

    public PrivateMessageListener(IProcessor messageProcessor) {
	if (messageProcessor == null)
	    throw new NullPointerException(
		    "Message processor passed to listener can't be null");

	this.messageProcessor = messageProcessor;
    }

    @Override
    public void processPacket(Packet packet) {
	// TODO verify that packet is smakx.Message and construct
	// PrivateMessage. Then pass it to message processor
    }

    IProcessor messageProcessor;
}
