package xmpp.message;

import java.util.Date;

import xmpp.message.data.XmppStatusMessageData;
import xmpp.message.data.XmppStatusMessageType;

public class XmppStatusMessage implements IXmppMessage {

    public XmppStatusMessage(XmppStatusMessageData data) {
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

    public String getRoomName() {
	return data.getRoomName();
    }

    public XmppStatusMessageType getType() {
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
	sb.append("Jid: ");
	sb.append(getJid());
	sb.append('\n');
	sb.append("Type: ");
	sb.append(getType());
	sb.append('\n');

	return sb.toString();
    }

    XmppStatusMessageData data;

}
