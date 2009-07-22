package domain.muc;

import domain.DomainObject;

public class Room extends DomainObject {

    public Room(String name) {
	this(name, null);
    }

    public Room(String name, String description) {
	this.name = name;
	this.description = description;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    String name;
    String description;
}
