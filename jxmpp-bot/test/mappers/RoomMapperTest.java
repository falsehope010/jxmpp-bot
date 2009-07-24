package mappers;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;

import org.junit.Test;

import utils.StackTraceUtil;
import base.DatabaseBaseTest;
import database.Database;
import exceptions.DatabaseNotConnectedException;

public class RoomMapperTest extends DatabaseBaseTest {

    @Test
    public void testInsert() {
	fail("Not yet implemented");
    }

    @Test
    public void testUpdate() {
	fail("Not yet implemented");
    }

    @Test
    public void testDelete() {
	fail("Not yet implemented");
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
