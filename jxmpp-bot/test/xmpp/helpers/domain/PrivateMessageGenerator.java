package xmpp.helpers.domain;

import xmpp.messaging.Message;
import xmpp.messaging.ParticipantInfo;
import xmpp.messaging.PrivateMessage;
import xmpp.queue.IXmppMessageQueue;

public class PrivateMessageGenerator extends AbstractMessageGenerator {

    public PrivateMessageGenerator(IXmppMessageQueue queue) {
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
