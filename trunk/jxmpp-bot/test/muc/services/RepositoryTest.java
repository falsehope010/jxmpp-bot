package muc.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mappers.RoomMapper;
import mappers.UserMapper;
import mappers.UserPermissionsMapper;
import muc.Repository;

import org.junit.Test;

import utils.StackTraceUtil;
import base.DatabaseBaseTest;
import database.Database;
import domain.muc.Room;
import domain.muc.User;
import domain.muc.UserPermissions;

public class RepositoryTest extends DatabaseBaseTest {

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
    public void testGetUser() throws NullPointerException,
	    FileNotFoundException {
	Database db = prepareDatabase();

	assertTruncateDependentTables(db);

	final int usersCount = 10;

	List<User> users = assertInsertUsers(db, usersCount);

	assertEquals(users.size(), usersCount);

	Repository repo = assertInitRepository(db);

	for (User user : users) {
	    User repoUser = repo.getUser(user.getID());

	    assertNotNull(repoUser);

	    assertTrue(repoUser.isPersistent());
	    assertEquals(user.getID(), repoUser.getID());
	    assertEquals(user.getBirthday(), repoUser.getBirthday());
	    assertEquals(user.getComments(), repoUser.getComments());
	    assertEquals(user.getJob(), repoUser.getJob());
	    assertEquals(user.getPosition(), repoUser.getPosition());
	    assertEquals(user.getRealName(), repoUser.getRealName());
	}

	db.disconnect();
    }

    @Test
    public void testGetRoom() throws NullPointerException,
	    FileNotFoundException {
	Database db = prepareDatabase();

	assertTruncateDependentTables(db);

	final int roomsCount = 10;

	List<Room> rooms = assertInsertRooms(db, roomsCount);

	assertEquals(rooms.size(), roomsCount);

	Repository repo = assertInitRepository(db);

	for (Room room : rooms) {
	    Room repoRoom = repo.getRoom(room.getID());

	    assertNotNull(repoRoom);

	    assertTrue(repoRoom.isPersistent());
	    assertEquals(room.getID(), repoRoom.getID());
	    assertEquals(room.getName(), repoRoom.getName());
	    assertEquals(room.getDescription(), repoRoom.getDescription());
	}

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

    protected Repository assertInitRepository(Database db) {
	Repository repo = null;

	try {
	    repo = new Repository(db);
	} catch (Exception e) {
	    fail(StackTraceUtil.toString(e));
	}

	assertNotNull(repo);

	return repo;
    }

    @SuppressWarnings("null")
    protected List<User> assertInsertUsers(Database db, int usersCount) {
	ArrayList<User> result = new ArrayList<User>();

	assertNotNull(db);
	assertTrue(db.isConnected());
	assertTrue(usersCount > 0);

	UserMapper mapper = null;

	try {
	    mapper = new UserMapper(db);
	} catch (Exception e) {
	    fail(StackTraceUtil.toString(e));
	}

	assertNotNull(mapper);

	for (int i = 0; i < usersCount; ++i) {
	    String suffix = Integer.toString(i);

	    User user = new User();
	    user.setRealName("name" + suffix);
	    user.setJob("job" + suffix);
	    user.setPosition("position" + suffix);
	    user.setComments("comments" + suffix);
	    user.setBirthday(new Date());

	    assertTrue(mapper.save(user));
	    assertTrue(user.isPersistent());
	    assertTrue(user.getID() > 0);

	    result.add(user);
	}

	assertEquals(countRecords(db, "users"), usersCount);
	assertEquals(result.size(), usersCount);

	return result;
    }

    @SuppressWarnings("null")
    protected List<Room> assertInsertRooms(Database db, int roomsCount) {
	ArrayList<Room> result = new ArrayList<Room>();

	assertNotNull(db);
	assertTrue(db.isConnected());
	assertTrue(roomsCount > 0);

	RoomMapper mapper = null;

	try {
	    mapper = new RoomMapper(db);
	} catch (Exception e) {
	    fail(StackTraceUtil.toString(e));
	}

	assertNotNull(mapper);

	for (int i = 0; i < roomsCount; ++i) {
	    String suffix = Integer.toString(i);

	    Room room = new Room("room" + suffix);
	    room.setDescription("description" + suffix);

	    assertTrue(mapper.save(room));
	    assertTrue(room.isPersistent());
	    assertTrue(room.getID() > 0);

	    result.add(room);
	}

	assertEquals(countRecords(db, "rooms"), roomsCount);
	assertEquals(result.size(), roomsCount);

	return result;
    }

    protected List<String> getJids(int recordsCount) {
	ArrayList<String> result = new ArrayList<String>(recordsCount);

	for (int i = 0; i < recordsCount; ++i) {
	    result.add("user" + Integer.toString(i) + "@xmpp.org");
	}

	return result;
    }
}
