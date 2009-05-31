package domain.syslog;

public class MessageCathegory extends MessageAttribute{

	public MessageCathegory(String Name){
		super(Name,null);
	}

	public MessageCathegory(String Name, String Description){
		super(Name,Description);
	}
}
