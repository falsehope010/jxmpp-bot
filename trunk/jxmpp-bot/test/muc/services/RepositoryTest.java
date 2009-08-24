package muc.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import mappers.UserPermissionsMapper;
import muc.Repository;

import org.junit.Test;

import utils.RandomUtils;
import utils.StackTraceUtil;
import base.RepositoryBaseTest;
import database.Database;
import database.DatabaseRecord;
import domain.muc.Room;
import domain.muc.User;
import domain.muc.UserPermissions;

public class RepositoryTest extends RepositoryBaseTest {

    @Test
    public void testRepositoryDbIsNull() {
	try {
	    @SuppressWarnings("unused")
	    Repository repo = new Repository(null);
	} catch (Exception e) {
	    assertTrue(e instanceof IllegalArgumentException);
	}
    }

    @Test
    public void testRepositoryDbNotConnected() throws NullPointerException,
	    FileNotFoundException {
	Database db = prepareDatabase();

	db.disconnect();

	try {
	    @SuppressWarnings("unused")
	    Repository repo = new Repository(db);
	} catch (Exception e) {
	    assertTrue(e instanceof IllegalArgumentException);
	}

    }

    @Test
    public void testRepository() throws NullPointerException,
	    FileNotFoundException {
	Database db = prepareDatabase();

	assertTruncateDependentTables(db);

	assertInitRepository(db);

	db.disconnect();
    }

    @Test
    public void testGetUserPermissions() throws NullPointerException,
	    FileNotFoundException {
	Database db = prepareDatabase();

	assertTruncateDependentTables(db);

	final int recordsCount = 10;

	List<UserPermissions> list = assertInsertPermissions(db, recordsCount);

	assertNotNull(list);

	// create new repository. repo will load all users/rooms, build identity
	// maps etc
	Repository repo = assertInitRepository(db);

	// retrieve all permissions using repo
	List<UserPermissions> repoList = repo.getUserPermissions();

	assertNotNull(repoList);
	assertEquals(repoList.size(), recordsCount);

	// verify records one by one
	for (int i = 0; i < recordsCount; ++i) {
	    UserPermissions ethalon = list.get(i);
	    UserPermissions perm = repoList.get(i);

	    assertNotNull(ethalon);
	    assertNotNull(perm);

	    assertTrue(perm.isPersistent());
	    assertTrue(perm.getID() > 0);

	    assertEquals(ethalon.getAccessLevel(), perm.getAccessLevel());
	    assertEquals(ethalon.getID(), perm.getID());
	    assertEquals(ethalon.getJabberID(), perm.getJabberID());

	    // verify room equality
	    assertEquals(ethalon.getRoom().getID(), perm.getRoom().getID());
	    assertEquals(ethalon.getRoom().getName(), perm.getRoom().getName());
	    assertEquals(ethalon.getRoom().getDescription(), perm.getRoom()
		    .getDescription());

	    // verify user equality
	    assertEquals(ethalon.getUser().getID(), perm.getUser().getID());
	}

	db.disconnect();
    }

    @SuppressWarnings("null")
    @Test
    public void testCreatePermissions() throws NullPointerException,
	    FileNotFoundException {
	Database db = prepareDatabase();

	assertTruncateDependentTables(db);

	Repository repo = assertInitRepository(db);

	String jabberID = "john_doe@xmpp.org";
	String roomName = "room@conference.xmpp.org";
	int accessLevel = 10;

	UserPermissions permissions = null;
	try {
	    permissions = repo.createUserPermissions(jabberID, roomName,
		    accessLevel);
	} catch (Exception e) {
	    fail("Can't create permissions using Repository. "
		    + StackTraceUtil.toString(e));
	}

	assertNotNull(permissions);
	assertTrue(permissions.isPersistent());
	assertTrue(permissions.getID() > 0);

	// verify records are mapped into database
	assertEquals(countRecords(db, "permissions"), 1);
	assertEquals(countRecords(db, "users"), 1);
	assertEquals(countRecords(db, "rooms"), 1);

	List<DatabaseRecord> listPermissions = db.getRecords("permissions");
	List<DatabaseRecord> listUsers = db.getRecords("users");
	List<DatabaseRecord> listRooms = db.getRecords("rooms");

	assertNotNull(listPermissions);
	assertEquals(listPermissions.size(), 1);
	assertNotNull(listUsers);
	assertEquals(listUsers.size(), 1);
	assertNotNull(listRooms);
	assertEquals(listRooms.size(), 1);

	// verify permissions record fields
	DatabaseRecord dPermissions = listPermissions.get(0);
	assertNotNull(dPermissions);

	assertEquals((Long) permissions.getID(), dPermissions.getLong("id"));
	assertEquals((Integer) permissions.getAccessLevel(), dPermissions
		.getInt("access_level"));
	assertEquals(permissions.getJabberID(), dPermissions.getString("jid"));

	// verify user
	User user = permissions.getUser();
	assertNotNull(user);
	assertTrue(user.isPersistent());
	assertTrue(user.getID() > 0);

	DatabaseRecord dUser = listUsers.get(0);
	assertNotNull(dUser);

	assertEquals((Long) user.getID(), dUser.getLong("id"));

	// verify room
	Room room = permissions.getRoom();
	assertNotNull(room);
	assertTrue(room.isPersistent());
	assertTrue(room.getID() > 0);

	DatabaseRecord dRoom = listRooms.get(0);
	assertNotNull(dRoom);

	assertEquals((Long) room.getID(), dRoom.getLong("id"));
	assertEquals(room.getName(), dRoom.getString("name"));

	// additional verification
	assertEquals(dPermissions.getLong("user_id"), (Long) user.getID());
	assertEquals(dPermissions.getLong("room_id"), (Long) room.getID());

	db.disconnect();
    }

    @Test
    public void testCreateSeveralPermissions() throws NullPointerException,
	    FileNotFoundException {
	Database db = prepareDatabase();

	assertTruncateDependentTables(db);

	Repository repo = assertInitRepository(db);

	List<UserPermissions> listPermissions = new ArrayList<UserPermissions>();
	UserPermissions permissions = null;
	final int recordsCount = 10;

	try {
	    for (int i = 0; i < recordsCount; ++i) {
		permissions = repo.createUserPermissions(RandomUtils
			.getRandomMail(32, 32), RandomUtils.getRandomMail(32,
			32), RandomUtils.getRandomNumber(0, 100));

		assertNotNull(permissions);
		assertTrue(permissions.isPersistent());
		assertTrue(permissions.getID() > 0);

		listPermissions.add(permissions);
	    }
	} catch (Exception e) {
	    fail("Can't create permissions using Repository. "
		    + StackTraceUtil.toString(e));
	}

	// verify records are mapped into database
	assertEquals(countRecords(db, "permissions"), recordsCount);
	assertEquals(countRecords(db, "users"), recordsCount);
	assertEquals(countRecords(db, "rooms"), recordsCount);

	// verify records by fields
	List<DatabaseRecord> dPermissionsList = db.getRecords("permissions");
	assertNotNull(dPermissionsList);
	assertEquals(dPermissionsList.size(), recordsCount);

	List<DatabaseRecord> dUsers = db.getRecords("users");
	assertNotNull(dUsers);
	assertEquals(dUsers.size(), recordsCount);

	List<DatabaseRecord> dRooms = db.getRecords("rooms");
	assertNotNull(dRooms);
	assertEquals(dRooms.size(), recordsCount);

	for (int i = 0; i < recordsCount; ++i) {
	    DatabaseRecord dPermissions = dPermissionsList.get(i);
	    permissions = listPermissions.get(i);

	    assertEquals(dPermissions.getLong("id"), (Long) permissions.getID());
	    assertEquals(dPermissions.getString("jid"), permissions
		    .getJabberID());
	    assertEquals(dPermissions.getInt("access_level"),
		    (Integer) permissions.getAccessLevel());

	    // verify user
	    DatabaseRecord dUser = dUsers.get(i);
	    assertNotNull(dUser);

	    User user = permissions.getUser();
	    assertNotNull(user);
	    assertTrue(user.isPersistent());
	    assertTrue(user.getID() > 0);
	    assertEquals((Long) user.getID(), dUser.getLong("id"));

	    // vefify room
	    DatabaseRecord dRoom = dRooms.get(i);
	    assertNotNull(dRoom);

	    Room room = permissions.getRoom();
	    assertNotNull(room);
	    assertTrue(room.isPersistent());
	    assertTrue(room.getID() > 0);

	    assertEquals((Long) room.getID(), dRoom.getLong("id"));
	    assertEquals(room.getName(), dRoom.getString("name"));

	    // additional verification
	    assertEquals(dPermissions.getLong("user_id"), (Long) user.getID());
	    assertEquals(dPermissions.getLong("room_id"), (Long) room.getID());
	}
    }

    @Test
    public void testCreateDuplicatePermissions() throws NullPointerException,
	    FileNotFoundException {
	/*
	 * This test verifies how is working internal caching in repository.
	 * Test creates multiple permissions records using the same user/room
	 * information. So only one room and user should be mapped, but multiple
	 * permissions
	 */

	Database db = prepareDatabase();

	assertTruncateDependentTables(db);

	Repository repo = assertInitRepository(db);

	String jabberID = "john_doe@xmpp.org";
	String roomName = "room@conference.xmpp.org";
	int accessLevel = 10;

	List<UserPermissions> listPermissions = new ArrayList<UserPermissions>();
	UserPermissions permissions = null;
	final int recordsCount = 10;

	try {
	    for (int i = 0; i < recordsCount; ++i) {
		permissions = repo.createUserPermissions(jabberID, roomName,
			accessLevel);

		assertNotNull(permissions);
		assertTrue(permissions.isPersistent());
		assertTrue(permissions.getID() > 0);

		listPermissions.add(permissions);
	    }
	} catch (Exception e) {
	    fail("Can't create permissions using Repository. "
		    + StackTraceUtil.toString(e));
	}

	// verify records are mapped into database
	assertEquals(countRecords(db, "permissions"), recordsCount);
	assertEquals(countRecords(db, "users"), 1);
	assertEquals(countRecords(db, "rooms"), 1);
    }

    protected void assertTruncateDependentTables(Database db) {
	assertNotNull(db);
	assertTrue(db.isConnected());

	assertTrue(truncateTable(db, "users"));
	assertTrue(truncateTable(db, "rooms"));
	assertTrue(truncateTable(db, "permissions"));
    }

    @SuppressWarnings("null")
    protected List<UserPermissions> assertInsertPermissions(Database db,
	    int recordsCount) {
	assertTrue(recordsCount > 0);

	ArrayList<UserPermissions> result = new ArrayList<UserPermissions>();

	List<User> users = assertInsertUsers(db, recordsCount);
	List<Room> rooms = assertInsertRooms(db, recordsCount);
	List<String> jids = getJids(recordsCount);

	assertEquals(users.size(), recordsCount);
	assertEquals(rooms.size(), recordsCount);

	// init mapper
	UserPermissionsMapper mapper = null;

	try {
	    mapper = new UserPermissionsMapper(db);
	} catch (Exception e) {
	    fail(StackTraceUtil.toString(e));
	}

	assertNotNull(mapper);

	// insert permissions
	for (int i = 0; i < recordsCount; ++i) {
	    User u = users.get(i);
	    Room r = rooms.get(i);
	    String jid = jids.get(i);

	    assertNotNull(u);
	    assertNotNull(r);
	    assertNotNull(jid);

	    UserPermissions permissions = new UserPermissions(u, r, jid);

	    assertTrue(mapper.save(permissions));
	    assertTrue(permissions.isPersistent());
	    assertTrue(permissions.getID() > 0);

	    result.add(permissions);
	}

	assertEquals(countRecords(db, "permissions"), recordsCount);

	return result;
    }

}
