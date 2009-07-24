package mappers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import utils.StackTraceUtil;
import base.DatabaseBaseTest;
import database.Database;
import database.DatabaseRecord;
import domain.muc.Room;
import exceptions.DatabaseNotConnectedException;

public class RoomMapperTest extends DatabaseBaseTest {

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
	assertTrue(truncateTable(db, "rooms"));

	RoomMapper mapper = null;

	try {
	    mapper = new RoomMapper(db);
	} catch (Exception e) {
	    fail(StackTraceUtil.toString(e));
	}

	assertNotNull(mapper);

	String roomName = "testRoom";

	Room room = new Room("testRoom");

	assertEquals(room.getName(), roomName);

	assertTrue(mapper.save(room));
	assertTrue(room.isPersistent());
	assertTrue(room.getID() > 0);
	assertEquals(countRecords(db, "rooms"), 1);

	List<DatabaseRecord> records = db.getRecords("rooms");
	assertNotNull(records);

	DatabaseRecord record = records.get(0);
	assertNotNull(record);

	assertEquals((Long) room.getID(), record.getLong("id"));
	assertEquals(room.getName(), record.getObject("name"));
	assertNull(record.getObject("description"));

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
	assertTrue(truncateTable(db, "rooms"));

	RoomMapper mapper = null;

	try {
	    mapper = new RoomMapper(db);
	} catch (Exception e) {
	    fail(StackTraceUtil.toString(e));
	}

	assertNotNull(mapper);

	String roomName = "testRoom";

	Room room = new Room("testRoom");

	assertEquals(room.getName(), roomName);

	assertTrue(mapper.save(room));
	assertTrue(room.isPersistent());
	assertTrue(room.getID() > 0);
	assertEquals(countRecords(db, "rooms"), 1);

	List<DatabaseRecord> records = db.getRecords("rooms");
	assertNotNull(records);
	assertEquals(records.size(), 1);

	DatabaseRecord record = records.get(0);
	assertNotNull(record);

	assertEquals((Long) room.getID(), record.getLong("id"));
	assertEquals(room.getName(), record.getObject("name"));
	assertNull(record.getObject("description"));

	// now update description
	String roomDescription = "testDescription";

	room.setDescription(roomDescription);
	assertEquals(room.getDescription(), roomDescription);

	assertTrue(mapper.save(room));

	assertEquals(countRecords(db, "rooms"), 1);

	records = db.getRecords("rooms");
	assertEquals(records.size(), 1);
	assertNotNull(records);

	record = records.get(0);
	assertNotNull(record);

	assertEquals(roomName, record.getObject("name"));
	assertEquals(roomDescription, record.getObject("description"));

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

	assertNotNull(db);
	assertTrue(truncateTable(db, "rooms"));

	RoomMapper mapper = null;

	try {
	    mapper = new RoomMapper(db);
	} catch (Exception e) {
	    fail(StackTraceUtil.toString(e));
	}

	assertNotNull(mapper);

	// now insert several records using mapper
	final int roomCount = 5;
	List<Room> rooms = new ArrayList<Room>(roomCount);

	for (int i = 0; i < roomCount; ++i) {
	    Room room = new Room("room" + Integer.toString(i));
	    assertTrue(mapper.save(room));
	    assertTrue(room.isPersistent());
	    assertTrue(room.getID() > 0);

	    rooms.add(room);
	}

	assertEquals(countRecords(db, "rooms"), roomCount);

	for (int i = 0; i < roomCount; ++i) {
	    Room room = rooms.get(i);

	    assertTrue(mapper.delete(room));
	    assertFalse(room.isPersistent());
	    assertEquals(room.getID(), 0);
	}

	assertEquals(countRecords(db, "rooms"), 0);

	db.disconnect();
    }

    @Test
    public void testRoomMapper() throws NullPointerException,
	    FileNotFoundException {
	RoomMapper testMapper = null;

	// test creating new instances of mapper without initializing them
	try {
	    testMapper = new RoomMapper(null);
	} catch (Exception e) {
	    assertTrue(e instanceof NullPointerException);
	}

	Database db = prepareDatabase();

	db.disconnect();

	try {
	    testMapper = new RoomMapper(db);
	} catch (Exception e) {
	    assertTrue(e instanceof DatabaseNotConnectedException);
	}

	db.connect();

	try {
	    testMapper = new RoomMapper(db);

	    assertNotNull(testMapper);
	} catch (DatabaseNotConnectedException e) {
	    fail(StackTraceUtil.toString(e));
	}

	db.disconnect();
    }

}
