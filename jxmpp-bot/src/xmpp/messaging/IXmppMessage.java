package xmpp.messaging;

import java.util.Date;

public interface IXmppMessage {
    Date getTimestamp();

    String getSender();

    String getJid();
}
