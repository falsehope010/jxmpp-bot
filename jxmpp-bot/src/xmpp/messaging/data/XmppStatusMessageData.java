package xmpp.messaging.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmppStatusMessageData extends XmppMessageData {

    public synchronized String getRoomName() {

	/*
	 * Extracts conference room name from sender field.
	 * 
	 * For example sender is: room@conference.xmpp.org/participant_nick
	 * 
	 * Method will return: room@conference.xmpp.org
	 */
	Matcher m = pattern.matcher(getSender());
	if (m.matches()) {
	    return m.group(1);
	}

	return null;
    }

    public XmppStatusMessageType getType() {
	return messageType;
    }

    public void setType(XmppStatusMessageType messageType) {
	this.messageType = messageType;
    }

    XmppStatusMessageType messageType;
    static Pattern pattern = Pattern.compile("(.+)/(.+)");
}
