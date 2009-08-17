package domain.moc;

import domain.DomainObject;

public class DomainObjectMoc extends DomainObject {

    public int getTag() {
	return tag;
    }

    public void setTag(int tag) {
	this.tag = tag;
    }

    private int tag;
}
