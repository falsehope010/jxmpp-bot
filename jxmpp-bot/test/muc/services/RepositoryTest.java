package muc.services;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;

import muc.Repository;

import org.junit.Test;

import utils.StackTraceUtil;
import base.DatabaseBaseTest;
import database.Database;

public class RepositoryTest extends DatabaseBaseTest {

    @Test
    public void testRepositoryDbIsNull() {
	try {
	    @SuppressWarnings("unused")
	    Repository repo = new Repository(null);
	} catch (Exception e) {
	    assertTrue(e instanceof IllegalArgumentException);
	}
    }

    @Test
    public void testRepositoryDbNotConnected() throws NullPointerException,
	    FileNotFoundException {
	Database db = prepareDatabase();

	db.disconnect();

	try {
	    @SuppressWarnings("unused")
	    Repository repo = new Repository(db);
	} catch (Exception e) {
	    assertTrue(e instanceof IllegalArgumentException);
	}

    }

    @Test
    public void testRepository() throws NullPointerException,
	    FileNotFoundException {
	Database db = prepareDatabase();

	assertTruncateDependentTables(db);

	Repository repo = null;

	try {
	    repo = new Repository(db);
	} catch (Exception e) {
	    fail(StackTraceUtil.toString(e));
	}

	assertNotNull(repo);

	db.disconnect();
    }

    @Test
    public void testGetUser() {
	fail("Not yet implemented");
    }

    @Test
    public void testGetRoom() {
	fail("Not yet implemented");
    }

    @Test
    public void testGetUserPermissions() {
	fail("Not yet implemented");
    }

    protected void assertTruncateDependentTables(Database db) {
	assertNotNull(db);
	assertTrue(db.isConnected());

	assertTrue(truncateTable(db, "users"));
	assertTrue(truncateTable(db, "rooms"));
	assertTrue(truncateTable(db, "permissions"));
    }
}
