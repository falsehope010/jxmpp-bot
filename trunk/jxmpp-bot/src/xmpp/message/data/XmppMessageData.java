package xmpp.message.data;

import java.util.Date;

public class XmppMessageData {

    public String getSender() {
	return sender;
    }

    public void setSender(String sender) {
	this.sender = sender;
    }

    public Date getTimestamp() {
	return timestamp;
    }

    public void setTimestamp(Date timestamp) {
	this.timestamp = timestamp;
    }

    public String getJid() {
	return jid;
    }

    public void setJid(String jid) {
	this.jid = jid;
    }

    String sender;
    Date timestamp;
    String jid;
}
