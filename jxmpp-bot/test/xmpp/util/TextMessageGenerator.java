package xmpp.util;

import java.util.Date;

import xmpp.messaging.IXmppMessage;
import xmpp.messaging.XmppTextMessage;
import xmpp.messaging.data.XmppTextMessageData;
import xmpp.messaging.data.XmppTextMessageType;
import xmpp.queue.IXmppMessageQueue;

public class TextMessageGenerator extends AbstractMessageGenerator {

    public TextMessageGenerator(IXmppMessageQueue queue) {
	super(queue);
    }

    @Override
    public IXmppMessage generateMessage() {
	XmppTextMessageData data = new XmppTextMessageData();
	data.setJid("john_doe@xmpp.org");
	data.setSender("john_doe@xmpp.org");
	data.setText("text");
	data.setTimestamp(new Date());
	data.setType(XmppTextMessageType.Private);
	return new XmppTextMessage(data);
    }

}
