package base;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import mappers.RoomMapper;
import mappers.UserMapper;
import utils.StackTraceUtil;
import database.Database;
import domain.muc.Room;
import domain.muc.User;
import domain.muc.UserPermissions;

public class PermissionsTest extends DatabaseBaseTest {

    protected UserPermissions assertCreatePermissions(Database db) {

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
     * Truncates "users", "rooms", "visits", "nicks", "chat_messages" and
     * "permissions" tables
     * 
     * @param db
     */
    protected void assertTruncateDependentTables(Database db) {
	assertNotNull(db);
	assertTrue(db.isConnected());

	assertTrue(truncateTable(db, "users"));
	assertTrue(truncateTable(db, "rooms"));
	assertTrue(truncateTable(db, "visits"));
	assertTrue(truncateTable(db, "nicks"));
	assertTrue(truncateTable(db, "chat_messages"));
	assertTrue(truncateTable(db, "permissions"));
    }
}
