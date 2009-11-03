package muc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import domain.muc.Room;
import domain.muc.User;
import domain.muc.UserPermissions;

public class UserPermissionsTest {

    @Test
    public void testEqualsFailNull() {
	UserPermissions permissions = createPermissions();
	assertNotNull(permissions);

	assertFalse(permissions.equals(null));
    }

    @Test
    public void testEqualsSelf() {
	UserPermissions permissions = createPermissions();
	assertNotNull(permissions);

	assertTrue(permissions.equals(permissions));
    }

    @Test
    public void testEqualsObject() {
	UserPermissions permissions = createPermissions();
	assertNotNull(permissions);
	UserPermissions permissions2 = createPermissions();
	assertNotNull(permissions2);

	assertTrue(permissions.equals(permissions2));
	assertTrue(permissions2.equals(permissions));

	UserPermissions permissions3 = createPermissions();
	assertNotNull(permissions3);
	assertTrue(permissions.equals(permissions3));
	assertTrue(permissions2.equals(permissions3));
    }

    @Test
    public void testHashCodeFails() {
	Room room = new Room("room");
	room.mapperSetID(1);
	room.mapperSetPersistence(true);

	User user = new User();
	user.mapperSetID(1);
	user.mapperSetPersistence(true);

	UserPermissions permissions = new UserPermissions(user, room,
		"test@jid");

	UserPermissions permissions2 = new UserPermissions(user, room,
		"test2@jid");

	assertFalse(permissions.equals(permissions2));
	assertTrue(permissions.hashCode() != permissions2.hashCode());
    }

    @Test
    public void testHashCode() {
	UserPermissions permissions = createPermissions();
	assertNotNull(permissions);
	UserPermissions permissions2 = createPermissions();
	assertNotNull(permissions2);

	assertEquals(permissions.hashCode(), permissions2.hashCode());
    }

    private UserPermissions createPermissions() {
	Room room = new Room("room");
	room.mapperSetID(1);
	room.mapperSetPersistence(true);

	User user = new User();
	user.mapperSetID(1);
	user.mapperSetPersistence(true);

	UserPermissions permissions = new UserPermissions(user, room,
		"test@jid");

	return permissions;
    }
}
