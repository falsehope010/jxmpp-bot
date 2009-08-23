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

import utils.StackTraceUtil;
import base.RepositoryBaseTest;
import database.Database;
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

    @Test
    public void testCreateUser() {
	fail("Not yet implemented");
    }

    @Test
    public void testCreateRoom() {
	fail("Not yet implemented");
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
