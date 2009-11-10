package xmpp.message;

import java.util.Date;

import xmpp.message.data.XmppTextMessageData;
import xmpp.message.data.XmppTextMessageType;

public class XmppTextMessage implements IXmppMessage {

    public XmppTextMessage(XmppTextMessageData data) {
	if (data == null)
	    throw new NullPointerException();

	this.data = data;
    }

    @Override
    public String getJid() {
	return data.getJid();
    }

    @Override
    public String getSender() {
	return data.getSender();
    }

    @Override
    public Date getTimestamp() {
	return data.getTimestamp();
    }

    public String getText() {
	return data.getText();
    }

    public XmppTextMessageType getType() {
	return data.getType();
    }

    @Override
    public String toString() {
	StringBuffer sb = new StringBuffer();

	sb.append("Time: ");
	sb.append(getTimestamp());
	sb.append('\n');
	sb.append("From: ");
	sb.append(getSender());
	sb.append('\n');
	sb.append("Text: ");
	sb.append(getText());
	sb.append('\n');
	sb.append("Jid: ");
	sb.append(getJid());
	sb.append('\n');
	sb.append("Type: ");
	sb.append(getType());
	sb.append('\n');

	return sb.toString();
    }

    XmppTextMessageData data;

}
