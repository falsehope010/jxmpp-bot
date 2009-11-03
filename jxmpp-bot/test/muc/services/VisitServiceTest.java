package muc.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;

import base.DatabaseBaseTest;
import database.Database;
import database.DatabaseRecord;
import domain.muc.Room;
import domain.muc.User;
import domain.muc.UserPermissions;
import domain.muc.Visit;
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
    public void testGetVisit() throws Exception {

	/*
	 * This test creates several visits for given list of user permissions.
	 * It remembers bindings between permissions and visits using HashMap.
	 * Then it checks whether those bindings has been successfully
	 * established inside service
	 */

	Database db = prepareDatabase();
	VisitService service = new VisitService(db);
	assertNotNull(service);

	assertTruncateDependentTables(db);

	final int recordsCount = 10;
	List<UserPermissions> permissionsList = createPermissions(recordsCount);
	HashMap<UserPermissions, Visit> testMap = new HashMap<UserPermissions, Visit>();

	for (UserPermissions permissions : permissionsList) {

	    testMap.put(permissions, service.startVisit(permissions));

	    // verify against database
	    DatabaseRecord record = getTopRecord(db, "visits");
	    assertNotNull(record);

	    assertEquals((Long) permissions.getID(), record
		    .getLong("permission_id"));
	    assertNotNull(record.getObject("start_date"));
	    assertNull(record.getObject("end_date"));
	}

	for (UserPermissions permissions : permissionsList) {
	    Visit visit = service.getVisit(permissions);
	    assertNotNull(visit);
	    Visit ethalonVisit = testMap.get(permissions);
	    assertNotNull(ethalonVisit);
	    assertEquals(visit, ethalonVisit);
	}

	db.disconnect();
    }

    @Test
    public void testGetVisitStartedThenClosed() throws Exception {

	/*
	 * This test creates several visits for given list of user permissions.
	 * It remembers bindings between permissions and visits using HashMap.
	 * Then it checks whether those bindings has been successfully
	 * established inside service
	 */

	Database db = prepareDatabase();
	VisitService service = new VisitService(db);
	assertNotNull(service);

	assertTruncateDependentTables(db);

	final int recordsCount = 10;
	List<UserPermissions> permissionsList = createPermissions(recordsCount);
	HashMap<UserPermissions, Visit> testMap = new HashMap<UserPermissions, Visit>();

	for (UserPermissions permissions : permissionsList) {

	    testMap.put(permissions, service.startVisit(permissions));

	    // verify against database
	    DatabaseRecord record = getTopRecord(db, "visits");
	    assertNotNull(record);

	    assertEquals((Long) permissions.getID(), record
		    .getLong("permission_id"));
	    assertNotNull(record.getObject("start_date"));
	    assertNull(record.getObject("end_date"));
	}

	for (UserPermissions permissions : permissionsList) {
	    Visit visit = service.getVisit(permissions);
	    assertNotNull(visit);
	    Visit ethalonVisit = testMap.get(permissions);
	    assertNotNull(ethalonVisit);
	    assertSame(visit, ethalonVisit);
	}

	// now finish visits
	for (UserPermissions permissions : permissionsList) {
	    Visit visit = service.finishVisit(permissions);
	    assertNotNull(visit);

	    Visit ethalonVisit = testMap.get(permissions);
	    assertSame(ethalonVisit, visit);
	}

	db.disconnect();
    }

    private List<UserPermissions> createPermissions(int recordsCount) {
	List<UserPermissions> result = new ArrayList<UserPermissions>();

	for (int i = 0; i < recordsCount; ++i) {
	    User user = new User();
	    user.mapperSetID(i);
	    user.mapperSetPersistence(true);

	    Room room = new Room("testRoom" + String.valueOf(i) + "@xmpp.org");
	    room.mapperSetID(i);
	    room.mapperSetPersistence(true);

	    UserPermissions permissions = new UserPermissions(user, room,
		    "testUser" + String.valueOf(i) + "@xmpp.org");
	    permissions.mapperSetID(i);
	    permissions.mapperSetPersistence(true);

	    result.add(permissions);
	}

	return result;
    }

    private void assertTruncateDependentTables(Database db) {
	assertTrue(db.truncateTable("permissions"));
	assertTrue(db.truncateTable("visits"));
    }
}
