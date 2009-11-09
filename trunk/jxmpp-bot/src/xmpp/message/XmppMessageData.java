package xmpp.message;

import java.util.Date;


public class XmppMessageData {

    public String getSender() {
	return sender;
    }

    public void setSender(String sender) {
	this.sender = sender;
    }

    public String getText() {
	return text;
    }

    public void setText(String text) {
	this.text = text;
    }

    public Date getTimestamp() {
	return timestamp;
    }

    public void setTimestamp(Date timestamp) {
	this.timestamp = timestamp;
    }

    public XmppMessageType getType() {
	return type;
    }

    public void setType(XmppMessageType type) {
	this.type = type;
    }

    public String getJid() {
	return jid;
    }

    public void setJid(String jid) {
	this.jid = jid;
    }

    String sender;
    String text;
    Date timestamp;
    XmppMessageType type;
    String jid;
}
