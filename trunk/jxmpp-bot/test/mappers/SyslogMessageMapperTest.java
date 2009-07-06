package mappers;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import base.DatabaseBaseTest;
import database.Database;
import database.DatabaseRecord;
import domain.syslog.Message;
import domain.syslog.SyslogSession;
import exceptions.DatabaseNotConnectedException;

public class SyslogMessageMapperTest extends DatabaseBaseTest {

    static final String tableName = "syslog";

    public void testSave() throws NullPointerException, FileNotFoundException,
	    IllegalArgumentException, DatabaseNotConnectedException {

	Database db = initDb();

	String text = "testText", category = "testCategory", type = "testType", sender = "testSender";

	// init session
	SyslogSessionMapper mapperS = new SyslogSessionMapper(db);

	SyslogSession session = new SyslogSession();

	assertTrue(mapperS.save(session));

	// 1. insert message using persistent session
	SyslogMessageMapper mapper = new SyslogMessageMapper(db);

	assertNotNull(mapper);

	Message msg = new Message(text, category, type, sender, session);

	assertNotNull(msg);

	assertTrue(mapper.save(msg));

	// 2. insert message using non-persistent session
	SyslogSession session2 = new SyslogSession();
	session2.close();

	Message msg2 = new Message(text, category, type, sender, session2);

	assertNotNull(msg2);

	assertTrue(mapper.save(msg2));

	// 3. check whether only one category,sender and type has been inserted
	// since we've used the same ones for two records

	ArrayList<String> categories = sqlGetRecords(db, "syslog_categories");
	assertNotNull(categories);
	assertTrue(categories.size() == 1);

	String dbCategory = categories.get(0);

	assertEquals(category, dbCategory);

	ArrayList<String> types = sqlGetRecords(db, "syslog_types");
	assertNotNull(types);
	assertTrue(types.size() == 1);

	String dbType = types.get(0);

	assertEquals(type, dbType);

	ArrayList<String> senders = sqlGetRecords(db, "syslog_senders");
	assertNotNull(senders);
	assertTrue(senders.size() == 1);

	String dbSender = senders.get(0);

	assertEquals(sender, dbSender);

	// 4. get all syslog messages from db and check whether they've been
	// mapped well
	ArrayList<Message> dbMessages = mapper.getMessages();

	assertNotNull(dbMessages);
	assertTrue(dbMessages.size() == 2);

	Message dbMsg = dbMessages.get(0);
	assertNotNull(dbMsg);
	assertTrue(dbMsg.isPersistent());

	assertTrue(dbMsg.getText().equals(text));
	assertTrue(dbMsg.getTimestamp().equals(msg.getTimestamp()));
	assertTrue(dbMsg.getCategoryName().equals(category));
	assertTrue(dbMsg.getMessageTypeName().equals(type));
	assertTrue(dbMsg.getSenderName().equals(sender));
	assertEquals(dbMsg.getSession(), session);

	dbMsg = dbMessages.get(1);
	assertNotNull(dbMsg);
	assertTrue(dbMsg.isPersistent());

	assertTrue(dbMsg.getText().equals(text));
	assertTrue(dbMsg.getTimestamp().equals(msg2.getTimestamp()));
	assertTrue(dbMsg.getCategoryName().equals(category));
	assertTrue(dbMsg.getMessageTypeName().equals(type));
	assertTrue(dbMsg.getSenderName().equals(sender));
	assertEquals(dbMsg.getSession(), session2);

	truncateTable(db, "syslog");
	clearDependentTables(db);

	db.disconnect();
    }

    public void testSaveMany() throws NullPointerException,
	    FileNotFoundException, DatabaseNotConnectedException {
	Database db = prepareDatabase();

	clearDependentTables(db);
	assertTrue(db.truncateTable(tableName));

	String msgText = "testMessage";
	String categoryName = "testCategory";
	String typeName = "testType";
	String senderName = "testSender";

	SyslogMessageMapper mapper = new SyslogMessageMapper(db);

	assertNotNull(mapper);

	// test inserting null-collection
	ArrayList<Message> nullCollection = null;
	assertFalse(mapper.save(nullCollection));

	final int messagesCount = 100;
	ArrayList<Message> messages = new ArrayList<Message>();
	SyslogSessionMapper s_mapper = new SyslogSessionMapper(db);
	SyslogSession session = new SyslogSession();
	session.close();

	assertTrue(s_mapper.save(session));

	for (int i = 0; i < messagesCount; ++i) {
	    Message msg = new Message(msgText, categoryName, typeName,
		    senderName, session);
	    messages.add(msg);
	}

	assertTrue(mapper.save(messages));

	// verify using plain sql
	assertEquals(db.countRecords(tableName), messagesCount);
	List<DatabaseRecord> records = db.getRecords("syslog");
	assertNotNull(records);
	assertEquals(records.size(), messagesCount);

	db.disconnect();
    }

    public void testDelete() throws NullPointerException,
	    FileNotFoundException, IllegalArgumentException,
	    DatabaseNotConnectedException {
	// 1. insert several records into db
	Database db = initDb();

	SyslogMessageMapper mapper = new SyslogMessageMapper(db);

	int recordsCount = 5;

	for (int i = 0; i < recordsCount; ++i) {
	    String messageTxt = "Message" + i;
	    Message msg = new Message(messageTxt, "newCategory", "newType",
		    "newSender", new SyslogSession());
	    assertNotNull(msg);
	    assertTrue(mapper.save(msg));
	}

	assertEquals(db.countRecords(tableName), recordsCount);

	// 2. load all records using mapper
	ArrayList<Message> dbMessages = mapper.getMessages();

	assertNotNull(dbMessages);
	assertEquals(dbMessages.size(), recordsCount);

	for (int i = 0; i < dbMessages.size(); ++i) {
	    Message msg = dbMessages.get(i);
	    assertTrue(mapper.delete(msg));
	    assertNotSame(msg.isPersistent(), true);
	    assertTrue(msg.getID() == 0);
	}

	assertTrue(db.countRecords(tableName) == 0);

	db.disconnect();
    }

    public void testSyslogMessageMapper() throws NullPointerException,
	    FileNotFoundException, IllegalArgumentException,
	    DatabaseNotConnectedException {
	SyslogMessageMapper testMapper = null;

	// test creating new instances of mapper without initializing them
	try {
	    testMapper = new SyslogMessageMapper(null);
	} catch (Exception e) {
	    assertTrue(e instanceof NullPointerException);
	}

	Database db = initDb();

	db.disconnect();

	try {
	    testMapper = new SyslogMessageMapper(db);
	} catch (Exception e) {
	    assertTrue(e instanceof DatabaseNotConnectedException);
	}

	db.connect();

	testMapper = new SyslogMessageMapper(db);

	assertNotNull(testMapper);

	clearDependentTables(db);

	db.disconnect();
    }

    public void testGetMessages() throws NullPointerException,
	    FileNotFoundException, IllegalArgumentException,
	    InterruptedException, DatabaseNotConnectedException {
	Database db = initDb();

	// 1. insert several records into db

	int recordsCount = 5;
	String[] msgTexts = new String[] { "message1", "message2", "message3",
		"message4", "message5" };
	String[] msgCategories = new String[] { "c1", "c2", "c3", "c4", "c5" };
	String[] msgTypes = new String[] { "t1", "t2", "t3", "t4", "t5" };
	String[] msgSenders = new String[] { "s1", "s2", "s3", "s4", "s5" };
	SyslogSession[] msgSessions = new SyslogSession[5];
	for (int i = 0; i < recordsCount; ++i) {
	    msgSessions[i] = new SyslogSession();

	    Thread.sleep(10);
	}

	ArrayList<Message> messages = new ArrayList<Message>();
	SyslogMessageMapper mapper = new SyslogMessageMapper(db);
	for (int i = 0; i < recordsCount; ++i) {
	    Message msg = new Message(msgTexts[i], msgCategories[i],
		    msgTypes[i], msgSenders[i], msgSessions[i]);
	    assertNotNull(msg);
	    messages.add(msg);

	    assertTrue(mapper.save(msg));

	    assertTrue(msgSessions[i].isPersistent());
	    assertTrue(msgSessions[i].getID() > 0);
	}

	assertEquals(db.countRecords(tableName), recordsCount);

	// 2. load records from db and compare with inserted ones
	ArrayList<Message> dbMessages = mapper.getMessages();

	assertNotNull(dbMessages);
	assertEquals(dbMessages.size(), recordsCount);

	for (int i = 0; i < recordsCount; ++i) {
	    Message dbMsg = dbMessages.get(i);
	    assertNotNull(dbMsg);
	    assertTrue(dbMsg.isPersistent());
	    assertTrue(dbMsg.getID() != 0);

	    // compare fields
	    assertEquals(dbMsg.getText(), msgTexts[i]);
	    assertEquals(dbMsg.getCategoryName(), msgCategories[i]);
	    assertEquals(dbMsg.getMessageTypeName(), msgTypes[i]);
	    assertEquals(dbMsg.getSenderName(), msgSenders[i]);

	    assertEquals(dbMsg.getSession().getID(), msgSessions[i].getID());
	}

	clearDependentTables(db);

	db.disconnect();
    }

    public void testEnd() {
	assertTrue(true);
    }

    /**
     * Initializes and checks database. Removes all records from syslog table.
     * Also clears all dependent tables (e.g. sessions,senders,types,categories)
     * 
     * @return
     * @throws NullPointerException
     * @throws FileNotFoundException
     * @throws MapperNotInitializedException
     * @throws InvalidSyslogSessionException
     * @throws IllegalArgumentException
     */
    private Database initDb() throws NullPointerException,
	    FileNotFoundException, IllegalArgumentException {
	Database db = prepareDatabase();

	assertTrue(truncateTable(db, tableName));
	assertEquals(countRecords(db, tableName), 0);

	clearDependentTables(db);

	return db;
    }

    private void clearDependentTables(Database db) {

	assertTrue(truncateTable(db, "syslog_categories"));
	assertEquals(countRecords(db, "syslog_categories"), 0);

	assertTrue(truncateTable(db, "syslog_types"));
	assertEquals(countRecords(db, "syslog_types"), 0);

	assertTrue(truncateTable(db, "syslog_senders"));
	assertEquals(countRecords(db, "syslog_senders"), 0);

	assertTrue(truncateTable(db, "syslog_sessions"));
	assertEquals(countRecords(db, "syslog_sessions"), 0);
    }

    /**
     * Internal method, used to return all strings from given table. Actually it
     * looks for (id,name) attributes and returns only 'name' attribute values
     * 
     * @param rtableName
     *            Table name from which records will be retrieved
     * @return
     * @throws NullPointerException
     * @throws FileNotFoundException
     * @throws InvalidSyslogSessionException
     * @throws MapperNotInitializedException
     * @throws IllegalArgumentException
     */
    private ArrayList<String> sqlGetRecords(Database db, String rtableName)
	    throws NullPointerException, FileNotFoundException,
	    IllegalArgumentException {

	assertNotNull(db);
	assertTrue(db.isConnected());

	ArrayList<String> result = new ArrayList<String>();

	ResultSet rs = null;
	Statement st = null;

	try {
	    Connection conn = db.getConnection();
	    st = conn.createStatement();

	    rs = st.executeQuery("select id,name from " + rtableName + ";");

	    while (rs.next()) {
		String attributeName = rs.getString(2);
		result.add(attributeName);
	    }
	} catch (Exception e) {
	    result = null;
	    fail(e.getMessage());
	} finally {
	    db.Cleanup(st, rs);
	}

	return result;
    }
}
