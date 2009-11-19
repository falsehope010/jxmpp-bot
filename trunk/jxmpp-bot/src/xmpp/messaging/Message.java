package xmpp.messaging;

import java.util.Date;

public abstract class Message {
    ParticipantInfo sender;
    ParticipantInfo recipient;
    Date timestamp;
    String text;
}
