package xmpp.messaging.data;


public class XmppTextMessageData extends XmppMessageData {

    public String getText() {
	return text;
    }

    public void setText(String text) {
	this.text = text;
    }

    public XmppTextMessageType getType() {
	return type;
    }

    public void setType(XmppTextMessageType type) {
	this.type = type;
    }

    String text;
    XmppTextMessageType type;
}
