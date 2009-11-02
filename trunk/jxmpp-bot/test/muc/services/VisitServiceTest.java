package muc.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;

import org.junit.Test;

import base.DatabaseBaseTest;
import database.Database;
import database.DatabaseRecord;
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

	final int permissionsID = 1024;

	UserPermissions permissions = new UserPermissions(user, room,
		"testUser@xmpp.org");
	permissions.mapperSetID(permissionsID);
	permissions.mapperSetPersistence(true);

	service.startVisit(permissions);

	// verify against database
	DatabaseRecord record = getTopRecord(db, "visits");
	assertNotNull(record);

	assertEquals((Integer) permissionsID, record.getInt("permission_id"));
	assertNotNull(record.getObject("start_date"));
	assertNull(record.getObject("end_date"));

	db.disconnect();
    }

    @Test(expected = NullPointerException.class)
    public void testFinishVisitNullPermissions() throws Exception {
	Database db = prepareDatabase();
	VisitService service = new VisitService(db);
	service.finishVisit(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFinishVisitNotPersistentPermission() throws Exception {
	Database db = prepareDatabase();
	VisitService service = new VisitService(db);
	assertNotNull(service);

	UserPermissions permissions = new UserPermissions(new User(), new Room(
		"testRoom@xmpp.org"), "testUser@xmpp.org");
	service.finishVisit(permissions);

	db.disconnect();
    }

    @Test
    public void testFinishVisit() throws Exception {
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

	final int permissionsID = 1024;

	UserPermissions permissions = new UserPermissions(user, room,
		"testUser@xmpp.org");
	permissions.mapperSetID(permissionsID);
	permissions.mapperSetPersistence(true);

	service.startVisit(permissions);

	service.finishVisit(permissions);

	// verify against database
	DatabaseRecord record = getTopRecord(db, "visits");
	assertNotNull(record);

	assertEquals((Integer) permissionsID, record.getInt("permission_id"));
	assertNotNull(record.getObject("start_date"));
	assertNotNull(record.getObject("end_date"));

	Date startDate = record.getDate("start_date");
	Date endDate = record.getDate("end_date");

	assertTrue((startDate.equals(endDate) || endDate.after(startDate)));

	db.disconnect();
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
