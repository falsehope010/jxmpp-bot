package base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mappers.RoomMapper;
import mappers.UserMapper;
import muc.Repository;
import utils.StackTraceUtil;
import database.Database;
import domain.muc.Room;
import domain.muc.User;

public class RepositoryBaseTest extends DatabaseBaseTest {
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
