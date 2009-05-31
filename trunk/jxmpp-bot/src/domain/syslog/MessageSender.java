package domain.syslog;

public class MessageSender extends MessageAttribute {

	public MessageSender(String Name) {
		super(Name, null);
	}

	public MessageSender(String Name, String Description) {
		super(Name, Description);
	}
}
