package mappers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import utils.StackTraceUtil;
import base.PermissionsTest;
import database.Database;
import database.DatabaseRecord;
import domain.internal.UserPermissionsEntity;
import domain.muc.Room;
import domain.muc.User;
import domain.muc.UserPermissions;
import exceptions.DatabaseNotConnectedException;

public class UserPermissionsMapperTest extends PermissionsTest {

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

    @SuppressWarnings("null")
    @Test
    public void testGetEntities() throws NullPointerException,
	    FileNotFoundException {
	Database db = prepareDatabase();

	assertTrue(truncateTable(db, "rooms"));
	assertTrue(truncateTable(db, "users"));
	assertTrue(truncateTable(db, "permissions"));

	UserMapper u_mapper = null;
	RoomMapper r_mapper = null;
	UserPermissionsMapper mapper = null;

	try {
	    u_mapper = new UserMapper(db);
	    r_mapper = new RoomMapper(db);
	    mapper = new UserPermissionsMapper(db);
	} catch (Exception e) {
	    fail(StackTraceUtil.toString(e));
	}

	assertNotNull(u_mapper);
	assertNotNull(r_mapper);
	assertNotNull(mapper);

	User user = new User();
	Room room = new Room("test@conference.xmpp.org");

	assertTrue(u_mapper.save(user));
	assertTrue(r_mapper.save(room));
	assertTrue(user.isPersistent());
	assertTrue(user.getID() > 0);
	assertTrue(room.isPersistent());
	assertTrue(room.getID() > 0);

	final int recordsCount = 5;
	ArrayList<UserPermissions> list = new ArrayList<UserPermissions>(
		recordsCount);

	for (int i = 0; i < recordsCount; ++i) {
	    UserPermissions p = new UserPermissions(user, room, "john_doe"
		    + Integer.toString(i) + "@xmpp.ru");
	    assertTrue(mapper.save(p));
	    assertTrue(p.isPersistent());
	    assertTrue(p.getID() > 0);

	    list.add(p);
	}

	assertEquals(countRecords(db, "permissions"), recordsCount);
	assertEquals(list.size(), recordsCount);

	List<UserPermissionsEntity> entities = mapper
		.repositoryGetUserPermissions();

	assertNotNull(entities);
	assertEquals(entities.size(), recordsCount);

	// verify entities against object model
	for (int i = 0; i < recordsCount; ++i) {
	    UserPermissions objectItem = list.get(i);
	    UserPermissionsEntity entityItem = entities.get(i);
	    assertNotNull(objectItem);
	    assertNotNull(entityItem);

	    assertEquals(objectItem.getID(), entityItem.getID());
	    assertEquals(objectItem.getUser().getID(), entityItem.getUserID());
	    assertEquals(objectItem.getRoom().getID(), entityItem.getRoomID());
	    assertEquals(objectItem.getJabberID(), entityItem.getJabberID());
	    assertEquals(objectItem.getAccessLevel(), entityItem
		    .getAccessLevel());
	}

	db.disconnect();
    }

    @SuppressWarnings("null")
    @Test
    public void testUpdateAccessLevel() throws NullPointerException,
	    FileNotFoundException {
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

	assertTrue(mapper.updateAccessLevel(permissions));

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
}
