package mappers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import utils.StackTraceUtil;
import base.DatabaseBaseTest;
import database.Database;
import database.DatabaseRecord;
import domain.muc.Room;
import domain.muc.User;
import domain.muc.UserPermissions;
import exceptions.DatabaseNotConnectedException;

public class UserPermissionsMapperTest extends DatabaseBaseTest {

    @SuppressWarnings("null")
    @Test
    public void testInsert() {
	Database db = null;

	try {
	    db = prepareDatabase();
	} catch (Exception e) {
	    fail(StackTraceUtil.toString(e));
	}

	assertNotNull(db);
	assertTruncateDependentTables(db);

	UserPermissionsMapper mapper = null;

	try {
	    mapper = new UserPermissionsMapper(db);
	} catch (Exception e) {
	    fail(StackTraceUtil.toString(e));
	}

	UserPermissions permissions = assertCreatePermissions(db);

	assertNotNull(permissions);

	// insert into db and verify
	assertTrue(mapper.save(permissions));
	assertTrue(permissions.isPersistent());
	assertTrue(permissions.getID() > 0);

	assertEquals(countRecords(db, "permissions"), 1);
	assertEquals(countRecords(db, "users"), 1);
	assertEquals(countRecords(db, "rooms"), 1);

	List<DatabaseRecord> records = db.getRecords("permissions");

	assertNotNull(records);
	assertEquals(records.size(), 1);

	DatabaseRecord record = records.get(0);
	assertNotNull(record);

	assertEquals(record.getLong("id"), (Long) permissions.getID());
	assertEquals(record.getLong("user_id"), (Long) permissions.getUser()
		.getID());
	assertEquals(record.getLong("room_id"), (Long) permissions.getRoom()
		.getID());
	assertEquals(record.getObject("jid"), permissions.getJabberID());
	assertEquals(record.getLong("access_level"), new Long(Integer
		.toString(permissions.getAccessLevel())));

	db.disconnect();
    }

    @SuppressWarnings("null")
    @Test
    public void testUpdate() {
	Database db = null;

	try {
	    db = prepareDatabase();
	} catch (Exception e) {
	    fail(StackTraceUtil.toString(e));
	}

	assertNotNull(db);
	assertTruncateDependentTables(db);

	UserPermissionsMapper mapper = null;

	try {
	    mapper = new UserPermissionsMapper(db);
	} catch (Exception e) {
	    fail(StackTraceUtil.toString(e));
	}

	UserPermissions permissions = assertCreatePermissions(db);

	assertNotNull(permissions);

	// insert into db and verify
	assertTrue(mapper.save(permissions));
	assertTrue(permissions.isPersistent());
	assertTrue(permissions.getID() > 0);

	assertEquals(countRecords(db, "permissions"), 1);
	assertEquals(countRecords(db, "users"), 1);
	assertEquals(countRecords(db, "rooms"), 1);

	List<DatabaseRecord> records = db.getRecords("permissions");

	assertNotNull(records);
	assertEquals(records.size(), 1);

	DatabaseRecord record = records.get(0);
	assertNotNull(record);

	assertEquals(record.getLong("id"), (Long) permissions.getID());
	assertEquals(record.getLong("user_id"), (Long) permissions.getUser()
		.getID());
	assertEquals(record.getLong("room_id"), (Long) permissions.getRoom()
		.getID());
	assertEquals(record.getObject("jid"), permissions.getJabberID());
	assertEquals(record.getLong("access_level"), new Long(Integer
		.toString(permissions.getAccessLevel())));

	// update access_level and verify
	final int newAccessLevel = 100;

	permissions.setAccessLevel(newAccessLevel);

	assertEquals(permissions.getAccessLevel(), newAccessLevel);

	assertTrue(mapper.save(permissions));

	assertEquals(countRecords(db, "permissions"), 1);
	assertEquals(countRecords(db, "users"), 1);
	assertEquals(countRecords(db, "rooms"), 1);

	records = db.getRecords("permissions");

	assertNotNull(records);
	assertEquals(records.size(), 1);

	record = records.get(0);
	assertNotNull(record);

	assertEquals(record.getLong("id"), (Long) permissions.getID());
	assertEquals(record.getLong("user_id"), (Long) permissions.getUser()
		.getID());
	assertEquals(record.getLong("room_id"), (Long) permissions.getRoom()
		.getID());
	assertEquals(record.getObject("jid"), permissions.getJabberID());
	assertEquals(record.getLong("access_level"), new Long(Integer
		.toString(permissions.getAccessLevel())));

	db.disconnect();
    }

    @SuppressWarnings("null")
    @Test
    public void testDelete() {
	Database db = null;

	try {
	    db = prepareDatabase();
	} catch (Exception e) {
	    fail(StackTraceUtil.toString(e));
	}

	assertTruncateDependentTables(db);

	// persist user and room
	User user = null;
	Room room = null;

	try {
	    user = new User();
	    room = new Room("testRoom");

	    UserMapper mapper = new UserMapper(db);
	    RoomMapper r_mapper = new RoomMapper(db);

	    assertTrue(mapper.save(user));
	    assertTrue(r_mapper.save(room));

	    assertTrue(user.isPersistent());
	    assertTrue(room.isPersistent());
	    assertTrue(user.getID() > 0);
	    assertTrue(room.getID() > 0);
	} catch (Exception e) {
	    fail(StackTraceUtil.toString(e));
	}

	UserPermissionsMapper mapper = null;

	try {
	    mapper = new UserPermissionsMapper(db);
	} catch (Exception e) {
	    fail(StackTraceUtil.toString(e));
	}

	assertNotNull(mapper);

	// create and rememeber several permissions
	final int recordsCount = 5;
	List<UserPermissions> list = new ArrayList<UserPermissions>(
		recordsCount);

	for (int i = 0; i < recordsCount; ++i) {
	    UserPermissions permissions = new UserPermissions(user, room,
		    "john_doe" + Integer.toString(i) + "@xmpp.com");

	    assertTrue(mapper.save(permissions));

	    assertTrue(permissions.isPersistent());
	    assertTrue(permissions.getID() > 0);

	    list.add(permissions);
	}

	assertEquals(countRecords(db, "permissions"), recordsCount);
	assertEquals(countRecords(db, "users"), 1);
	assertEquals(countRecords(db, "rooms"), 1);

	// delete records
	for (UserPermissions p : list) {
	    assertNotNull(p);
	    assertTrue(p.isPersistent());

	    assertTrue(mapper.delete(p));
	    assertFalse(p.isPersistent());
	    assertEquals(p.getID(), 0);
	}

	assertEquals(countRecords(db, "permissions"), 0);
	assertEquals(countRecords(db, "users"), 1);
	assertEquals(countRecords(db, "rooms"), 1);

	assertNotNull(db);

	db.disconnect();
    }

    @SuppressWarnings("null")
    @Test
    public void testUserPermissionsMapper() {
	Database db = null;

	try {
	    db = prepareDatabase();
	} catch (Exception e) {
	    fail(StackTraceUtil.toString(e));
	}

	assertNotNull(db);

	UserPermissionsMapper mapper = null;

	try {
	    mapper = new UserPermissionsMapper(db);
	} catch (Exception e) {
	    fail(StackTraceUtil.toString(e));
	}

	try {
	    mapper = new UserPermissionsMapper(null);
	} catch (Exception e) {
	    assertTrue(e instanceof NullPointerException);
	}

	db.disconnect();

	try {
	    mapper = new UserPermissionsMapper(db);
	} catch (Exception e) {
	    assertTrue(e instanceof DatabaseNotConnectedException);
	}

	assertNotNull(mapper);

    }

    private UserPermissions assertCreatePermissions(Database db) {

	assertNotNull(db);
	assertTrue(db.isConnected());

	UserPermissions result = null;

	User user = null;
	Room room = null;

	try {
	    user = new User();
	    room = new Room("testRoom");

	    // persist objects
	    UserMapper mapper = new UserMapper(db);
	    RoomMapper r_mapper = new RoomMapper(db);

	    assertTrue(mapper.save(user));
	    assertTrue(r_mapper.save(room));

	    assertTrue(user.isPersistent());
	    assertTrue(room.isPersistent());
	    assertTrue(user.getID() > 0);
	    assertTrue(room.getID() > 0);
	} catch (Exception e) {
	    fail(StackTraceUtil.toString(e));
	}

	result = new UserPermissions(user, room, "john_doe@xmpp.com");

	return result;
    }

    /**
     * Truncates "users", "rooms" and "permissions" tables
     * 
     * @param db
     */
    private void assertTruncateDependentTables(Database db) {
	assertNotNull(db);
	assertTrue(db.isConnected());

	assertTrue(truncateTable(db, "users"));
	assertTrue(truncateTable(db, "rooms"));
	assertTrue(truncateTable(db, "permissions"));
    }
}
