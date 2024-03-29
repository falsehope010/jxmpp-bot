package xmpp.helpers.domain;

import xmpp.messaging.PrivateMessage;
import xmpp.messaging.base.Message;
import xmpp.messaging.domain.ParticipantInfo;
import xmpp.queue.IMessageQueue;

public class PrivateMessageGenerator extends AbstractMessageGenerator {

    public PrivateMessageGenerator(IMessageQueue queue) {
	super(queue);
    }

    @Override
    public Message generateMessage() {
	ParticipantInfo sender = new ParticipantInfo("john_doe@xmpp.org",
		"john_doe@xmpp.org/client");
	ParticipantInfo recipient = new ParticipantInfo("john_doe2@xmpp.org",
		"john_doe2@xmpp.org/client");
	return new PrivateMessage(sender, recipient, "text");
    }

}
