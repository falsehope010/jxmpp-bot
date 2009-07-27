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
import base.PermissionsTest;
import database.Database;
import database.DatabaseRecord;
import domain.muc.UserPermissions;
import domain.muc.Visit;
import exceptions.DatabaseNotConnectedException;

public class VisitMapperTest extends PermissionsTest {

    @SuppressWarnings("null")
    @Test
    public void testInsert() throws NullPointerException, FileNotFoundException {
	Database db = prepareDatabase();

	assertTruncateDependentTables(db);

	Visit visit = assertCreateNewVisit(db);

	assertNotNull(visit);

	VisitMapper mapper = null;

	try {
	    mapper = new VisitMapper(db);
	} catch (Exception e) {
	    fail(StackTraceUtil.toString(e));
	}

	assertNotNull(mapper);

	assertTrue(mapper.save(visit));
	assertTrue(visit.isPersistent());
	assertTrue(visit.getID() > 0);

	// verify insertion
	assertEquals(countRecords(db, "visits"), 1);

	List<DatabaseRecord> records = db.getRecords("visits");
	assertNotNull(records);
	assertEquals(records.size(), 1);

	DatabaseRecord record = records.get(0);
	assertNotNull(record);

	assertEquals(record.getLong("id"), (Long) visit.getID());
	assertEquals(record.getDate("start_date"), visit.getStartDate());
	assertEquals(record.getDate("end_date"), visit.getEndDate());
	assertEquals(record.getLong("permission_id"), (Long) visit
		.getPermissions().getID());

	db.disconnect();
    }

    @SuppressWarnings("null")
    @Test
    public void testUpdate() throws NullPointerException, FileNotFoundException {
	Database db = prepareDatabase();

	assertTruncateDependentTables(db);

	Visit visit = assertCreateNewVisit(db);

	assertNotNull(visit);

	VisitMapper mapper = null;

	try {
	    mapper = new VisitMapper(db);
	} catch (Exception e) {
	    fail(StackTraceUtil.toString(e));
	}

	assertNotNull(mapper);

	assertTrue(mapper.save(visit));
	assertTrue(visit.isPersistent());
	assertTrue(visit.getID() > 0);

	// verify insertion
	assertEquals(countRecords(db, "visits"), 1);

	List<DatabaseRecord> records = db.getRecords("visits");
	assertNotNull(records);
	assertEquals(records.size(), 1);

	DatabaseRecord record = records.get(0);
	assertNotNull(record);

	assertEquals(record.getLong("id"), (Long) visit.getID());
	assertEquals(record.getDate("start_date"), visit.getStartDate());
	assertEquals(record.getDate("end_date"), visit.getEndDate());
	assertEquals(record.getLong("permission_id"), (Long) visit
		.getPermissions().getID());

	// update visit
	visit.setEndDate(new Date());

	assertTrue(mapper.save(visit));

	records = db.getRecords("visits");
	assertNotNull(records);
	assertEquals(records.size(), 1);

	record = records.get(0);
	assertNotNull(record);

	assertEquals(record.getLong("id"), (Long) visit.getID());
	assertEquals(record.getDate("start_date"), visit.getStartDate());
	assertEquals(record.getDate("end_date"), visit.getEndDate());
	assertEquals(record.getLong("permission_id"), (Long) visit
		.getPermissions().getID());

	db.disconnect();
    }

    @SuppressWarnings("null")
    @Test
    public void testDelete() throws NullPointerException, FileNotFoundException {
	Database db = prepareDatabase();

	assertTruncateDependentTables(db);

	UserPermissions permissions = assertCreatePermissions(db);

	try {
	    UserPermissionsMapper p_mapper = new UserPermissionsMapper(db);

	    assertTrue(p_mapper.save(permissions));
	} catch (Exception e) {
	    fail(StackTraceUtil.toString(e));
	}

	VisitMapper mapper = null;
	try {
	    mapper = new VisitMapper(db);
	} catch (DatabaseNotConnectedException e) {
	    fail(StackTraceUtil.toString(e));
	}
	assertNotNull(mapper);

	// init several visits
	final int recordsCount = 5;
	List<Visit> visits = new ArrayList<Visit>(recordsCount);
	for (int i = 0; i < recordsCount; ++i) {
	    Visit visit = new Visit(permissions);

	    assertTrue(mapper.save(visit));

	    assertTrue(visit.isPersistent());
	    assertTrue(visit.getID() > 0);

	    visits.add(visit);
	}

	// verify
	assertEquals(countRecords(db, "visits"), recordsCount);

	for (Visit v : visits) {
	    assertTrue(mapper.delete(v));
	    assertFalse(v.isPersistent());
	    assertEquals(v.getID(), 0);
	}

	assertEquals(countRecords(db, "visits"), 0);

	db.disconnect();
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

    private Visit assertCreateNewVisit(Database db) {
	UserPermissions permissions = assertCreatePermissions(db);

	try {
	    UserPermissionsMapper p_mapper = new UserPermissionsMapper(db);

	    assertTrue(p_mapper.save(permissions));
	} catch (Exception e) {
	    fail(StackTraceUtil.toString(e));
	}

	Visit visit = new Visit(permissions);

	return visit;
    }
}
