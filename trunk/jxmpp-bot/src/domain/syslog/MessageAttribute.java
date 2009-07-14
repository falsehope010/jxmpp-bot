package domain.syslog;

import domain.DomainObject;

/**
 * Represents base class for MessageCathegory, MessageType and MessageSender
 * 
 * @author tillias_work
 * 
 */
public class MessageAttribute extends DomainObject {

	/**
	 * Creates new non-persistent message attribute.
	 * 
	 * @param Name
	 *            Name or text of attribute
	 * @param Description
	 *            Description of attribute
	 */
	public MessageAttribute(String Name, String Description) {
		this.name = Name;
		this.description = Description;
	}

	public String getName() {
		return name;
	}

	public void setName(String Name) {
		this.name = Name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String Description) {
		this.description = Description;
	}

	String name;
	String description;
}
