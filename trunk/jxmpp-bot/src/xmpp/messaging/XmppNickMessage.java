package xmpp.messaging;

import java.util.Date;

import xmpp.messaging.data.XmppNickMessageData;
import xmpp.messaging.data.XmppStatusMessageType;

public class XmppNickMessage implements IXmppMessage {

    public XmppNickMessage(XmppNickMessageData data) {
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

    public String getNewNick() {
	return data.getNewNick();
    }

    public String getNick() {
	return data.getNick();
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
	sb.append("Old nick: ");
	sb.append(getNick());
	sb.append('\n');
	sb.append("New nick: ");
	sb.append(getNewNick());

	return sb.toString();
    }

    XmppNickMessageData data;
}
