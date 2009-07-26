package mappers;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;

import org.junit.Test;

import utils.StackTraceUtil;
import base.PermissionsTest;
import database.Database;
import domain.muc.UserPermissions;
import exceptions.DatabaseNotConnectedException;

public class VisitMapperTest extends PermissionsTest {

    @Test
    public void testInsert() throws NullPointerException, FileNotFoundException {
	Database db = prepareDatabase();

	UserPermissions permissions = assertCreatePermissions(db);

	db.disconnect();
    }

    @Test
    public void testUpdate() {
	fail("Not yet implemented"); // TODO
    }

    @Test
    public void testDelete() {
	fail("Not yet implemented"); // TODO
    }

    @SuppressWarnings("null")
    @Test
    public void testVisitMapper() {
	Database db = null;

	try {
	    db = prepareDatabase();
	} catch (Exception e) {
	    fail(StackTraceUtil.toString(e));
	}

	assertNotNull(db);

	VisitMapper mapper = null;

	try {
	    mapper = new VisitMapper(db);
	} catch (Exception e) {
	    fail(StackTraceUtil.toString(e));
	}

	try {
	    mapper = new VisitMapper(null);
	} catch (Exception e) {
	    assertTrue(e instanceof NullPointerException);
	}

	db.disconnect();

	try {
	    mapper = new VisitMapper(db);
	} catch (Exception e) {
	    assertTrue(e instanceof DatabaseNotConnectedException);
	}

	assertNotNull(mapper);

    }

}
