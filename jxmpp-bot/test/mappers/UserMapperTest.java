package mappers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import utils.StackTraceUtil;
import base.DatabaseBaseTest;
import database.Database;
import database.DatabaseRecord;
import domain.muc.User;
import exceptions.DatabaseNotConnectedException;

public class UserMapperTest extends DatabaseBaseTest {

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
	assertTrue(truncateTable(db, "users"));

	UserMapper mapper = null;

	try {
	    mapper = new UserMapper(db);
	} catch (Exception e) {
	    fail(StackTraceUtil.toString(e));
	}

	assertNotNull(mapper);

	// perform insertion

	// insert user with all attributes set to null
	User user = new User();

	assertNotNull(user);
	assertTrue(mapper.save(user));
	assertTrue(user.isPersistent());
	assertTrue(user.getID() > 0);

	assertTrue(db.countRecords("users") == 1);

	List<DatabaseRecord> records = db.getRecords("users");
	assertNotNull(records);
	assertEquals(records.size(), 1);

	DatabaseRecord record = records.get(0);
	assertNotNull(record);
	assertTrue(record.isNull("real_name"));
	assertTrue(record.isNull("job"));
	assertTrue(record.isNull("position"));
	assertTrue(record.isNull("birthday"));
	assertTrue(record.isNull("comments"));

	// insert user with all attributes not null
	assertTrue(truncateTable(db, "users"));

	String name = "testName";
	String job = "testJob";
	String position = "testPosition";
	Date birthday = new Date();
	String comments = "testComment";

	User user2 = new User();
	user2.setRealName(name);
	user2.setJob(job);
	user2.setPosition(position);
	user2.setBirthday(birthday);
	user2.setComments(comments);

	// verify attributes are set correctly
	assertEquals(user2.getRealName(), name);
	assertEquals(user2.getJob(), job);
	assertEquals(user2.getPosition(), position);
	assertEquals(user2.getBirthday(), birthday);
	assertEquals(user2.getComments(), comments);

	assertTrue(mapper.save(user2));

	assertEquals(countRecords(db, "users"), 1);

	records = db.getRecords("users");
	assertNotNull(records);
	assertEquals(records.size(), 1);

	record = records.get(0);
	assertNotNull(record);
	assertEquals(record.getObject("real_name"), name);
	assertEquals(record.getObject("job"), job);
	assertEquals(record.getObject("position"), position);
	assertEquals(record.getDate(("birthday")), birthday);
	assertEquals(record.getObject("comments"), comments);

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

	assertTrue(truncateTable(db, "users"));

	UserMapper mapper = null;

	try {
	    mapper = new UserMapper(db);
	} catch (Exception e) {
	    fail(StackTraceUtil.toString(e));
	}

	assertNotNull(mapper);

	// insert record first
	String name = "testName";
	String job = "testJob";
	String position = "testPosition";
	Date birthday = new Date();
	String comments = "testComment";

	User user = new User();
	user.setRealName(name);
	user.setJob(job);
	user.setPosition(position);
	user.setBirthday(birthday);
	user.setComments(comments);

	// verify attributes are set correctly
	assertEquals(user.getRealName(), name);
	assertEquals(user.getJob(), job);
	assertEquals(user.getPosition(), position);
	assertEquals(user.getBirthday(), birthday);
	assertEquals(user.getComments(), comments);

	assertTrue(mapper.save(user));

	assertEquals(countRecords(db, "users"), 1);

	List<DatabaseRecord> records = db.getRecords("users");
	assertNotNull(records);
	assertEquals(records.size(), 1);

	DatabaseRecord record = records.get(0);
	assertNotNull(record);
	assertEquals(record.getObject("real_name"), name);
	assertEquals(record.getObject("job"), job);
	assertEquals(record.getObject("position"), position);
	assertEquals(record.getDate(("birthday")), birthday);
	assertEquals(record.getObject("comments"), comments);

	// update record fields and check again

	name = "newName";
	job = "newJob";
	position = "newPosition";
	birthday = new Date();
	comments = "newComments";

	user.setRealName(name);
	user.setJob(job);
	user.setPosition(position);
	user.setBirthday(birthday);
	user.setComments(comments);

	// verify attributes are set correctly
	assertEquals(user.getRealName(), name);
	assertEquals(user.getJob(), job);
	assertEquals(user.getPosition(), position);
	assertEquals(user.getBirthday(), birthday);
	assertEquals(user.getComments(), comments);

	assertTrue(mapper.save(user));

	assertEquals(countRecords(db, "users"), 1);

	records = db.getRecords("users");
	assertNotNull(records);
	assertEquals(records.size(), 1);

	record = records.get(0);
	assertNotNull(record);
	assertEquals(record.getObject("real_name"), name);
	assertEquals(record.getObject("job"), job);
	assertEquals(record.getObject("position"), position);
	assertEquals(record.getDate(("birthday")), birthday);
	assertEquals(record.getObject("comments"), comments);

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
	assertTrue(truncateTable(db, "users"));

	UserMapper mapper = null;

	try {
	    mapper = new UserMapper(db);
	} catch (Exception e) {
	    fail(StackTraceUtil.toString(e));
	}

	assertNotNull(mapper);

	final int usersCount = 5;

	List<User> users = new ArrayList<User>(5);

	for (int i = 0; i < usersCount; ++i) {
	    User user = new User();
	    users.add(user);

	    assertTrue(mapper.save(user));

	    assertTrue(user.isPersistent());
	    assertTrue(user.getID() > 0);
	}

	// check users count it database
	assertEquals(countRecords(db, "users"), usersCount);

	// delete users
	for (User user : users) {
	    assertNotNull(user);

	    assertTrue(mapper.delete(user));

	    assertFalse(user.isPersistent());
	    assertEquals(user.getID(), 0);
	}

	assertEquals(countRecords(db, "users"), 0);

	db.disconnect();
    }

    @Test
    public void testUserMapper() throws NullPointerException,
	    FileNotFoundException {
	UserMapper testMapper = null;

	// test creating new instances of mapper without initializing them
	try {
	    testMapper = new UserMapper(null);
	} catch (Exception e) {
	    assertTrue(e instanceof NullPointerException);
	}

	Database db = prepareDatabase();

	db.disconnect();

	try {
	    testMapper = new UserMapper(db);
	} catch (Exception e) {
	    assertTrue(e instanceof DatabaseNotConnectedException);
	}

	db.connect();

	try {
	    testMapper = new UserMapper(db);

	    assertNotNull(testMapper);
	} catch (DatabaseNotConnectedException e) {
	    fail(StackTraceUtil.toString(e));
	}

	db.disconnect();
    }

}
