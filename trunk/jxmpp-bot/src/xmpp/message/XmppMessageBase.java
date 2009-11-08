package xmpp.message;

import java.util.Date;

import xmpp.XmppMessageType;

public class XmppMessageBase {

    String sender;
    String text;
    Date timestamp;
    XmppMessageType type;
}
