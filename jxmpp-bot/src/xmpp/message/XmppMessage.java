package xmpp.message;

import java.util.Date;


public class XmppMessage {

    public XmppMessage(XmppMessageData data) {
	if (data == null)
	    throw new NullPointerException();

	this.data = data;
    }

    public String getSender() {
	return data.getSender();
    }

    public String getText() {
	return data.getText();
    }

    public Date getTimestamp() {
	return data.getTimestamp();
    }

    public XmppMessageType getType() {
	return data.getType();
    }

    public String getJid() {
	return data.getJid();
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

    XmppMessageData data;
}
