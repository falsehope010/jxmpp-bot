package domain.syslog;

public class MessageType extends MessageAttribute {

	public MessageType(String Name) {
		super(Name, null);
	}
	
	public MessageType(String Name, String Description) {
		super(Name, Description);
	}

}
