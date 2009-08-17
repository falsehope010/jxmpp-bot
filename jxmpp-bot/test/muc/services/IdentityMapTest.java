package muc.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import domain.moc.DomainObjectMoc;

public class IdentityMapTest {

    @Test
    public void testIdentityMap() {

	/*
	 * Multi-aspects test. Tests actual use of IdentityMap class.
	 */

	IdentityMap<DomainObjectMoc> map = new IdentityMap<DomainObjectMoc>();

	// insert nothing since DomainObjects don't have valid id
	for (int i = 0; i < objectsCount; ++i) {
	    DomainObjectMoc moc = new DomainObjectMoc();
	    map.add(moc);
	}

	assertEquals(map.size(), 0);

	// insert nothing since DomainObjects are not persistent
	for (int i = 0; i < objectsCount; ++i) {
	    DomainObjectMoc moc = new DomainObjectMoc();
	    moc.mapperSetID(i);
	    map.add(moc);
	}

	assertEquals(map.size(), 0);

	// insert actual items
	for (int i = 0; i < objectsCount; ++i) {
	    DomainObjectMoc moc = new DomainObjectMoc();
	    moc.mapperSetID(i);
	    moc.mapperSetPersistence(true);

	    moc.setTag(i);

	    map.add(moc);
	}

	assertEquals(map.size(), objectsCount);

	// verify mappings
	for (int i = 0; i < objectsCount; ++i) {
	    DomainObjectMoc moc = map.get(i);
	    assertNotNull(moc);
	    assertTrue(moc.isPersistent());

	    assertEquals(moc.getID(), i);
	    assertEquals(moc.getTag(), i);
	}
    }

    @Test
    public void testAddGet() {
	IdentityMap<DomainObjectMoc> map = new IdentityMap<DomainObjectMoc>();

	final long id = 1;

	DomainObjectMoc moc = new DomainObjectMoc();
	moc.mapperSetPersistence(true);
	moc.mapperSetID(id);

	map.add(moc);

	assertEquals(map.size(), 1);

	DomainObjectMoc moc2 = map.get(id);
	assertNotNull(moc2);
	assertEquals(moc2.getID(), moc.getID());
	assertEquals(moc2, moc);
    }

    @Test
    public void testClear() {
	IdentityMap<DomainObjectMoc> map = new IdentityMap<DomainObjectMoc>();

	final long id = 1;

	DomainObjectMoc moc = new DomainObjectMoc();
	moc.mapperSetPersistence(true);
	moc.mapperSetID(id);

	map.add(moc);

	assertEquals(map.size(), 1);

	DomainObjectMoc moc2 = map.get(id);
	assertNotNull(moc2);
	assertEquals(moc2.getID(), moc.getID());
	assertEquals(moc2, moc);

	map.clear();

	assertEquals(map.size(), 0);
    }

    final static int objectsCount = 1000;
}
