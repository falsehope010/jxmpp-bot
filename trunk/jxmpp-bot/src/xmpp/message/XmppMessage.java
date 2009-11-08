package xmpp.message;

import java.util.Date;

public class XmppMessage {

    protected XmppMessage() {

    }

    String text;
    Date timestamp;
    String jid;
    String nick;
    String roomName;
}
