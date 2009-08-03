package mappers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import utils.DateConverter;
import base.DatabaseBaseTest;
import database.Database;
import domain.syslog.SyslogSession;
import exceptions.DatabaseNotConnectedException;

public class SyslogSessionMapperTest extends DatabaseBaseTest {

    static final String tableName = "syslog_sessions";
    static final int recordsCount = 5;

    @Test
    public void testSyslogSessionMapper() throws NullPointerException,
	    FileNotFoundException, DatabaseNotConnectedException {
	SyslogSessionMapper testMapper = null;

	// test creating new instances of mapper without initializing them
	try {
	    testMapper = new SyslogSessionMapper(null);
	} catch (Exception e) {
	    assertTrue(e instanceof NullPointerException);
	}

	Database db = initDb();

	db.disconnect();

	try {
	    testMapper = new SyslogSessionMapper(db);
	} catch (Exception e) {
	    assertTrue(e instanceof DatabaseNotConnectedException);
	}

	db.connect();

	testMapper = new SyslogSessionMapper(db);

	assertNotNull(testMapper);

	db.disconnect();
    }

    @Test
    public void testSave() throws NullPointerException, FileNotFoundException,
	    DatabaseNotConnectedException {
	testInsertSession();
	testUpdateSession();
    }

    @SuppressWarnings("boxing")
    @Test
    public void testGetSessions() throws NullPointerException,
	    FileNotFoundException, SQLException, DatabaseNotConnectedException {

	Database db = initDb();
	SyslogSessionMapper mapper = initMapper(db);
	Connection conn = db.getConnection();

	/*
	 * 1. Clear all records in sessions table 2. Create array of opened
	 * sessions. 3. Map them into db. 4. Load all records from db and
	 * compare with original array of opened sessions. 5. Clear all records
	 * in sessions table
	 */
	ArrayList<SyslogSession> testSessions = createArray(false, recordsCount);

	for (int i = 0; i < recordsCount; ++i) {
	    SyslogSession session = testSessions.get(i);
	    sqlInsert(conn, session);
	}

	List<SyslogSession> mappedSessions = mapper.getSessions();

	compareArrays(testSessions, mappedSessions);

	truncateTable(db);

	/*
	 * 6. Create array of closed sessions and map them into db 7. Load all
	 * records and compare with original array 8. Clear all records in
	 * sessions table
	 */

	// now test closed sessions mapping
	for (int i = 0; i < recordsCount; ++i) {
	    SyslogSession session = testSessions.get(i);
	    session.close();

	    assertEquals(session.getEndDate() != null, true);

	    sqlInsert(conn, session);
	}

	mappedSessions = mapper.getSessions();

	compareArrays(testSessions, mappedSessions);

	truncateTable(db);

	db.disconnect();
    }

    @Test
    public void testInsertSession() throws NullPointerException,
	    FileNotFoundException, DatabaseNotConnectedException {

	/*
	 * 1. Clear all records in sessions table 2. Insert several records 3.
	 * Check whether those records has been inserted 4. Clear all records
	 * from sessions table
	 */

	Database db = initDb();
	SyslogSessionMapper mapper = initMapper(db);

	ArrayList<SyslogSession> testSessions = createArray(false, recordsCount);

	for (int i = 0; i < recordsCount; ++i) {
	    SyslogSession session = testSessions.get(i);
	    assertTrue(mapper.save(session));
	}

	List<SyslogSession> mappedSessions = mapper.getSessions();

	compareArrays(testSessions, mappedSessions);

	truncateTable(db);

	db.disconnect();
    }

    @Test
    public void testUpdateSession() throws NullPointerException,
	    FileNotFoundException, DatabaseNotConnectedException {

	/*
	 * 1. Clear all records from sessions table 2. Create array of several
	 * sessions 3. Insert them and check insertion 4. Close sessions (domain
	 * objects) 5. Save them into db again 6. Check that changes has been
	 * propagated into db table (compare values in database to in-memory
	 * domain objects' fields) 7. Clear all records from sessions table
	 */
	Database db = initDb();
	SyslogSessionMapper mapper = initMapper(db);

	ArrayList<SyslogSession> sessions = new ArrayList<SyslogSession>();

	for (int i = 0; i < recordsCount; ++i) {
	    SyslogSession session = new SyslogSession();
	    assertTrue(mapper.save(session));
	}

	List<SyslogSession> mappedSessions = mapper.getSessions();

	compareArrays(sessions, mappedSessions);

	for (int i = 0; i < recordsCount; ++i) {
	    SyslogSession session = mappedSessions.get(i);
	    session.close();

	    assertTrue(mapper.save(session));
	}

	List<SyslogSession> updatedSessions = mapper.getSessions();

	compareArrays(mappedSessions, updatedSessions);

	truncateTable(db);

	db.disconnect();
    }

    @Test
    public void testInsert10Records() throws NullPointerException,
	    FileNotFoundException, DatabaseNotConnectedException {
	Database db = initDb();
	SyslogSessionMapper mapper = new SyslogSessionMapper(db);

	truncateTable(db);

	for (int i = 0; i < 10; ++i) {
	    SyslogSession session = new SyslogSession();
	    session.close();

	    assertTrue(mapper.save(session));
	}

	truncateTable(db);

	db.disconnect();
    }

    @Test
    public void testGetLatestSession() throws NullPointerException,
	    FileNotFoundException, DatabaseNotConnectedException {
	Database db = initDb();

	SyslogSessionMapper mapper = initMapper(db);

	/*
	 * 1. Truncate table and get latest session using mapper. Session must
	 * be null 2. Create several sessions and map them into db. 3. Find
	 * session with latest start date 4. Retieved latest session from db
	 * using mapper 5. Compare retrieved record and stored in memory
	 */
	SyslogSession latestSession = mapper.getLatestSession();

	assertNull(latestSession);

	ArrayList<SyslogSession> testSessions = new ArrayList<SyslogSession>();

	java.util.Date now = new java.util.Date();
	long now_ms = now.getTime();

	for (int i = 0; i < recordsCount; ++i) {
	    SyslogSession s = new SyslogSession();
	    s.mapperSetStartDate(new Date(now_ms + 100 * i));
	    testSessions.add(s);
	}

	// find max by startDate
	latestSession = testSessions.get(0);

	assertNotNull(latestSession);

	for (SyslogSession s : testSessions) {
	    if (s.getStartDate().after(latestSession.getStartDate())) {
		latestSession = s;
	    }
	}

	assertNotNull(latestSession);

	// map records into database
	for (SyslogSession s : testSessions) {
	    assertTrue(mapper.save(s));
	    assertTrue(s.isPersistent());
	}

	SyslogSession testSession = mapper.getLatestSession();

	assertNotNull(testSession);

	assertEquals(testSession.getID(), latestSession.getID());

	db.disconnect();
    }

    @Test
    public void testDelete() throws NullPointerException,
	    FileNotFoundException, DatabaseNotConnectedException {
	Database db = initDb();
	SyslogSessionMapper mapper = new SyslogSessionMapper(db);

	assertTrue(db.truncateTable("syslog"));
	assertTrue(db.countRecords("syslog") == 0);

	SyslogSession session = new SyslogSession();
	session.close();

	assertTrue(mapper.save(session));

	int relatedMessagesCount = sqlInsertMessages(session, db);

	assertTrue(relatedMessagesCount > 0);
	assertTrue(db.countRecords("syslog") == relatedMessagesCount);

	// deletion
	assertTrue(mapper.delete(session));

	assertTrue(db.countRecords("syslog") == 0);
	assertTrue(db.countRecords("syslog_sessions") == 0);

	db.disconnect();
    }

    /**
     * Initializes and checks database. Removes all records from syslog sessions
     * table
     * 
     * @return
     * @throws NullPointerException
     * @throws FileNotFoundException
     */
    private Database initDb() throws NullPointerException,
	    FileNotFoundException {
	Database db = prepareDatabase();

	assertTrue(truncateTable(db, tableName));
	assertEquals(countRecords(db, tableName), 0);

	return db;
    }

    /**
     * Inits SyslogSessionMapper using given database
     * 
     * @param db
     *            Database which will be used by mapper
     * @return
     * @throws DatabaseNotConnectedException
     * @throws NullPointerException
     * @throws MapperNotInitializedException
     */
    private SyslogSessionMapper initMapper(Database db)
	    throws NullPointerException, DatabaseNotConnectedException {

	SyslogSessionMapper mapper = new SyslogSessionMapper(db);

	return mapper;
    }

    /**
     * Removes all records from syslog sessions table
     * 
     * @param db
     */
    @SuppressWarnings("boxing")
    private void truncateTable(Database db) {
	assertEquals(truncateTable(db, tableName), true);
    }

    /**
     * Creates array of non-persistent SyslogSessions
     * 
     * @param areClosedSessions
     *            Controls whether sessions will be closed
     * @param sessionsCount
     *            Total number of records in array
     * @return Array of non-persistent SyslogSessions
     */
    private ArrayList<SyslogSession> createArray(boolean areClosedSessions,
	    int sessionsCount) {
	ArrayList<SyslogSession> result = new ArrayList<SyslogSession>(
		sessionsCount);

	for (int i = 0; i < sessionsCount; ++i) {
	    SyslogSession session = new SyslogSession();

	    if (areClosedSessions)
		session.close();

	    result.add(session);
	}

	return result;
    }

    /**
     * Performs manual insertion of syslog session into dabase.
     * 
     * @param conn
     *            Database connection
     * @param session
     *            Session object
     * @throws SQLException
     */
    private void sqlInsert(Connection conn, SyslogSession session)
	    throws SQLException {
	PreparedStatement pr = null;
	try {
	    pr = conn.prepareStatement("insert into " + tableName
		    + "(start_date,end_date) values(?,?);");
	    pr.setDate(1, DateConverter.Convert(session.getStartDate()));
	    pr.setDate(2, DateConverter.Convert(session.getEndDate()));

	    int rows_affected = pr.executeUpdate();

	    assertEquals(rows_affected, 1);
	} catch (Exception e) {
	    System.out.print(e.getMessage());
	} finally {
	    if (pr != null)
		pr.close();
	}
    }

    @SuppressWarnings("boxing")
    private void compareArrays(List<SyslogSession> testArray,
	    List<SyslogSession> persistentArray) {
	if (testArray != null && testArray.size() > 0) {
	    assertNotNull(persistentArray);

	    assertEquals(testArray.size() == persistentArray.size(), true);

	    for (int i = 0; i < recordsCount; ++i) {
		SyslogSession lhs = testArray.get(i);
		SyslogSession rhs = persistentArray.get(i);

		assertEquals(rhs.isPersistent(), true);
		assertEquals(rhs.getID() > 0, true);

		assertEquals(lhs.getStartDate().equals(rhs.getStartDate()),
			true);

		if (lhs.isClosed()) {
		    assertEquals(rhs.getEndDate() != null, true);
		    assertEquals(lhs.getEndDate().equals(rhs.getEndDate()),
			    true);
		} else { // end date is null
		    assertEquals(lhs.getEndDate() == rhs.getEndDate(), true);
		}
	    }
	}
    }

    /**
     * Inserts test messages into database using given parent session.
     * 
     * @param parentSession
     * @param db
     * @return number of inserted records (greater then zero if succeded)
     */
    private int sqlInsertMessages(SyslogSession parentSession, Database db) {
	int result = 0;

	assertNotNull(parentSession);
	assertTrue(parentSession.isPersistent());

	PreparedStatement pr = null;
	try {
	    long sessionID = parentSession.getID();

	    assertTrue(sessionID > 0);

	    Connection conn = db.getConnection();

	    pr = conn
		    .prepareStatement("insert into syslog(timestamp,text,session_id,category_id,type_id,sender_id)"
			    + "values(?,?,?,1,1,1)");

	    conn.setAutoCommit(false);

	    for (int i = 0; i < 10; ++i) {
		pr.setDate(1, DateConverter.Convert(new java.util.Date()));
		pr.setString(2, "unitTestMessage");
		pr.setLong(3, sessionID);

		result += pr.executeUpdate();
	    }

	    conn.commit();
	    conn.setAutoCommit(true);

	} catch (Exception e) {
	    result = 0;
	    fail(e.getMessage());
	} finally {
	    db.Cleanup(pr);
	}

	return result;
    }
}
