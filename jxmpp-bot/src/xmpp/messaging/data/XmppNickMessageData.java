package xmpp.messaging.data;

import java.util.regex.Matcher;

public class XmppNickMessageData extends XmppStatusMessageData {

    public synchronized String getNick() {

	/*
	 * Extracts sender nick name from sender field.
	 * 
	 * For example sender is: room@conference.xmpp.org/participant_nick
	 * 
	 * Method will return: participant_nick
	 */

	Matcher m = pattern.matcher(getSender());
	if (m.matches()) {
	    return m.group(2);
	}

	return null;
    }

    public String getNewNick() {
	return newNick;
    }

    public void setNewNick(String newNick) {
	this.newNick = newNick;
    }

    String newNick;
}
