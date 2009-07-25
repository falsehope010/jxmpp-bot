package domain.muc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import mappers.RoomMapper;
import mappers.UserMapper;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import utils.StackTraceUtil;
import base.DatabaseBaseTest;
import database.Database;
import database.DatabaseFactory;

@SuppressWarnings("unused")
public class UserPermissionsTest extends DatabaseBaseTest {

    @Test
    public void testUserPermissions() {
	UserPermissions perm = new UserPermissions(persistentUser,
		persistentRoom, jabberID);
    }

    @Test(expected = NullPointerException.class)
    public void createUsingNullUser() {

	UserPermissions perm = new UserPermissions(null, persistentRoom,
		jabberID);

    }

    @Test(expected = NullPointerException.class)
    public void createUsingNullRoom() {
	UserPermissions perm = new UserPermissions(persistentUser, null,
		jabberID);
    }

    @Test(expected = NullPointerException.class)
    public void createUsingNullJID() {
	UserPermissions perm = new UserPermissions(persistentUser,
		persistentRoom, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createUsingNotPersistentRoom() {
	UserPermissions perm = new UserPermissions(persistentUser, new Room(
		"testRoom"), jabberID);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createUsingNotPersistentUser() {
	UserPermissions perm = new UserPermissions(new User(), persistentRoom,
		jabberID);
    }

    @Test
    public void testGetJabberID() {
	UserPermissions perm = new UserPermissions(persistentUser,
		persistentRoom, jabberID);

	assertEquals(perm.getJabberID(), jabberID);
    }

    @Test
    public void testSetJabberID() {
	UserPermissions perm = new UserPermissions(persistentUser,
		persistentRoom, jabberID);

	assertEquals(perm.getJabberID(), jabberID);

	final String newJID = "newJID";

	perm.setJabberID(newJID);

	assertEquals(perm.getJabberID(), newJID);
    }

    @Test(expected = NullPointerException.class)
    public void testSetNullJID() {
	UserPermissions perm = new UserPermissions(persistentUser,
		persistentRoom, jabberID);

	assertEquals(perm.getJabberID(), jabberID);

	perm.setJabberID(null);
    }

    @Test
    public void testGetAccessLevel() {
	UserPermissions perm = new UserPermissions(persistentUser,
		persistentRoom, jabberID);

	assertEquals(perm.getAccessLevel(), 0);
    }

    @Test
    public void testSetAccessLevel() {
	UserPermissions perm = new UserPermissions(persistentUser,
		persistentRoom, jabberID);

	assertEquals(perm.getAccessLevel(), 0);

	final int newAccessLevel = 100;

	perm.setAccessLevel(newAccessLevel);

	assertEquals(perm.getAccessLevel(), newAccessLevel);
    }

    @Test
    public void testGetUser() {
	UserPermissions perm = new UserPermissions(persistentUser,
		persistentRoom, jabberID);

	assertEquals(perm.getUser(), persistentUser);
    }

    @Test
    public void testGetRoom() {
	UserPermissions perm = new UserPermissions(persistentUser,
		persistentRoom, jabberID);

	assertEquals(perm.getRoom(), persistentRoom);
    }

    @Test
    public void testValidate() {
	UserPermissions perm = new UserPermissions(persistentUser,
		persistentRoom, jabberID);

	assertTrue(perm.validate());
    }

    @BeforeClass
    public static void prepareResources() {

	try {
	    DatabaseFactory factory = new DatabaseFactory(testDbName);

	    db = factory.createDatabase();// new Database(testDbName);

	    db.connect();

	    assertNotNull(db);
	    assertTrue(db.isConnected());

	    // persist objects
	    persistentUser = new User();
	    persistentRoom = new Room("testRoom");

	    UserMapper mapper = new UserMapper(db);
	    assertTrue(mapper.save(persistentUser));
	    assertTrue(persistentUser.isPersistent());
	    assertTrue(persistentUser.getID() > 0);

	    RoomMapper r_mapper = new RoomMapper(db);
	    assertTrue(r_mapper.save(persistentRoom));
	    assertTrue(persistentRoom.isPersistent());
	    assertTrue(persistentRoom.getID() > 0);
	} catch (Exception e) {
	    fail(StackTraceUtil.toString(e));
	}

    }

    @AfterClass
    public static void cleanUpResources() {

	assertTrue(db.truncateTable("users"));
	assertTrue(db.truncateTable("rooms"));
	assertTrue(db.truncateTable("permissions"));

	if (db != null)
	    db.disconnect();

    }

    static User persistentUser;
    static Room persistentRoom;
    static Database db;
    static String jabberID = "john_doe@xmpp.com";
}
