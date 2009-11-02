package muc.services;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import base.DatabaseBaseTest;
import database.Database;
import domain.muc.Room;
import domain.muc.User;
import domain.muc.UserPermissions;
import exceptions.DatabaseNotConnectedException;

public class VisitServiceTest extends DatabaseBaseTest {

    @Test(expected = NullPointerException.class)
    public void testVisitServiceNullDatabase() throws Exception {
	VisitService service = new VisitService(null);
	assertNull(service);
    }

    @Test(expected = DatabaseNotConnectedException.class)
    public void testVisitServiceDisconnectedDb() throws Exception {
	Database db = prepareDatabase();
	db.disconnect();

	assertNotNull(db);
	assertTrue(!db.isConnected());

	VisitService service = new VisitService(db);
	assertNull(service);
    }

    @Test
    public void testVisitService() throws Exception {
	Database db = prepareDatabase();
	VisitService service = new VisitService(db);
	assertNotNull(service);

	db.disconnect();
    }

    @Test(expected = NullPointerException.class)
    public void testStartVisitNullPermissions() throws Exception {
	Database db = prepareDatabase();
	VisitService service = new VisitService(db);
	assertNotNull(service);

	service.startVisit(null);

	db.disconnect();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStartVisitNotPersistentPermissions() throws Exception {
	Database db = prepareDatabase();
	VisitService service = new VisitService(db);
	assertNotNull(service);

	UserPermissions permissions = new UserPermissions(new User(), new Room(
		"testRoom@xmpp.org"), "testUser@xmpp.org");

	service.startVisit(permissions);

	db.disconnect();
    }

    @Test
    public void testStartVisit() throws Exception {
	Database db = prepareDatabase();
	VisitService service = new VisitService(db);
	assertNotNull(service);

	assertTruncateDependentTables(db);

	User user = new User();
	user.mapperSetID(1);
	user.mapperSetPersistence(true);

	Room room = new Room("testRoom@xmpp.org");
	room.mapperSetID(1);
	room.mapperSetPersistence(true);

	UserPermissions permissions = new UserPermissions(user, room,
		"testUser@xmpp.org");
	permissions.mapperSetID(1);
	permissions.mapperSetPersistence(true);

	service.startVisit(permissions);

	// verify against database

	fail("Not yet implementd");
    }

    @Test
    public void testFinishVisit() {
	fail("Not yet implemented");
    }

    @Test
    public void testGetVisit() {
	fail("Not yet implemented");
    }

    private void assertTruncateDependentTables(Database db) {
	assertTrue(db.truncateTable("permissions"));
	assertTrue(db.truncateTable("visits"));
    }
}
