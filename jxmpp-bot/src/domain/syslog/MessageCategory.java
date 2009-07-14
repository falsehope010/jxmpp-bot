package domain.syslog;

public class MessageCategory extends MessageAttribute {

	public MessageCategory(String Name) {
		super(Name, null);
	}

	public MessageCategory(String Name, String Description) {
		super(Name, Description);
	}
}
