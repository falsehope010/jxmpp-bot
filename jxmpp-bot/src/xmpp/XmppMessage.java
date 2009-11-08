package xmpp;

import java.util.Date;

import org.jivesoftware.smack.packet.Message;

public class XmppMessage {

    public XmppMessage(Message body) {

	if (body == null)
	    throw new NullPointerException();

	this.body = body;
	this.timestamp = new Date();
    }

    public Date getTimestamp() {
	return timestamp;
    }

    public String getText() {
	return body.getBody();
    }

    public String getFrom() {
	return body.getFrom();
    }

    public String getTo() {
	return body.getTo();
    }

    Message body;
    Date timestamp;
}
