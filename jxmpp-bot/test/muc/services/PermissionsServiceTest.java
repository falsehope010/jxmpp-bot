package muc.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import muc.Repository;

import org.junit.Test;

import base.DatabaseBaseTest;
import database.Database;
import domain.muc.UserPermissions;
import exceptions.RepositoryInitializationException;
import exceptions.ServiceInitializationException;

public class PermissionsServiceTest extends DatabaseBaseTest {

    @Test(expected = NullPointerException.class)
    public void testConstructorFailNullArgument() throws NullPointerException,
	    ServiceInitializationException {
	PermissionsService service = new PermissionsService(null);
	assertNull(service);
    }

    @Test
    public void testConstructorSuccess() throws NullPointerException,
	    FileNotFoundException, IllegalArgumentException,
	    RepositoryInitializationException, ServiceInitializationException {
	Database db = prepareDatabase();
	assertTrue(db.connect());

	assertTruncateDependentTables(db);

	Repository repo = getRepository(db);
	PermissionsService service = new PermissionsService(repo);
	assertNotNull(service);

	db.disconnect();
    }

    @Test
    public void testGetPermissionsExists() throws Exception {
	Database db = prepareDatabase();
	assertTrue(db.connect());

	assertTruncateDependentTables(db);

	// populate database with test data
	Repository repo = getRepository(db);
	List<UserPermissions> listPermissions = assertPopulateDatabase(repo);

	// init another repo in order to fecth populated data from database
	Repository repo2 = getRepository(db);

	PermissionsService service = new PermissionsService(repo2);
	assertNotNull(service);

	// verify permissions using etalon
	for (UserPermissions perm : listPermissions) {
	    UserPermissions test_perm = service.getPermissions(perm
		    .getJabberID(), perm.getRoom().getName());
	    assertNotNull(test_perm);

	    assertTrue(comparePermissions(perm, test_perm));
	}

	db.disconnect();
    }

    @Test
    public void testGetPermissionsDoesntExist() throws Exception {
	Database db = prepareDatabase();
	assertTrue(db.connect());

	assertTruncateDependentTables(db);

	Repository repo = getRepository(db);

	PermissionsService service = new PermissionsService(repo);
	assertNotNull(service);

	UserPermissions perm = service
		.getPermissions("testJid", "testRoomName");
	assertNull(perm); // no users at all in database/repository

	db.disconnect();
    }

    @Test
    public void testGrantPermissions() throws Exception {

	/*
	 * This test assumes that database is empty and checks whether
	 * grantPermissions() also inserts user/room into database as well as
	 * permissions object itself. Then it updates accessLevel and checks how
	 * it is being updated inside database
	 */

	Database db = prepareDatabase();
	assertTrue(db.connect());

	assertTruncateDependentTables(db);

	Repository repo = getRepository(db);

	PermissionsService service = new PermissionsService(repo);
	assertNotNull(service);

	final String jid = "testJid";
	final String roomName = "testRoomName";
	final int accessLevel = 10;

	UserPermissions perm = service.getPermissions(jid, roomName);
	assertNull(perm);

	service.grantPermissions(jid, roomName, accessLevel);
	perm = service.getPermissions(jid, roomName);
	assertNotNull(perm);
	assertTrue(perm.isPersistent());
	assertTrue(perm.getRoom().isPersistent());
	assertTrue(perm.getUser().isPersistent());
	assertEquals(jid, perm.getJabberID());
	assertEquals(roomName, perm.getRoom().getName());
	assertEquals(accessLevel, perm.getAccessLevel());

	// update access level
	final int newAccessLevel = accessLevel * 2;
	service.grantPermissions(jid, roomName, newAccessLevel);
	perm = service.getPermissions(jid, roomName);
	assertNotNull(perm);
	assertTrue(perm.isPersistent());
	assertTrue(perm.getRoom().isPersistent());
	assertTrue(perm.getUser().isPersistent());
	assertEquals(jid, perm.getJabberID());
	assertEquals(roomName, perm.getRoom().getName());
	assertEquals(newAccessLevel, perm.getAccessLevel());

	// todo check in database
	fail("Not yet implemented");

	db.disconnect();
    }

    @Test
    public void testRevokePermissions() {
	fail("Not yet implemented");
    }

    protected Repository getRepository(Database db)
	    throws IllegalArgumentException, RepositoryInitializationException {
	return new Repository(db);
    }

    protected void assertTruncateDependentTables(Database db) {
	assertTrue(truncateTable(db, "users"));
	assertTrue(truncateTable(db, "rooms"));
	assertTrue(truncateTable(db, "permissions"));
    }

    protected List<UserPermissions> assertPopulateDatabase(Repository repo)
	    throws Exception {
	List<UserPermissions> result = new ArrayList<UserPermissions>();

	final int recordsCount = 10;
	final int defaultAccessLevel = 0;

	for (int i = 0; i < recordsCount; ++i) {
	    UserPermissions perm = repo.createUserPermissions("user"
		    + Integer.toString(i), "room" + Integer.toString(i),
		    defaultAccessLevel);
	    assertNotNull(perm);
	    assertTrue(perm.isPersistent());

	    result.add(perm);
	}

	return result;
    }

    protected boolean comparePermissions(UserPermissions lhs,
	    UserPermissions rhs) {
	boolean result = false;

	if (lhs == rhs)
	    result = true;
	else {
	    boolean ok = lhs.getAccessLevel() == rhs.getAccessLevel();
	    ok &= lhs.getID() == rhs.getID();
	    ok &= lhs.getJabberID().equals(rhs.getJabberID());
	    ok &= lhs.getRoom().getID() == rhs.getRoom().getID();
	    ok &= lhs.getUser().getID() == rhs.getUser().getID();

	    result = ok;
	}

	return result;
    }
}
