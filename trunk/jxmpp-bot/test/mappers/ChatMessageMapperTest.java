package mappers;

import static org.junit.Assert.assertEquals;
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
import domain.muc.ChatMessage;
import domain.muc.UserPermissions;
import domain.muc.Visit;
import exceptions.DatabaseNotConnectedException;

public class ChatMessageMapperTest extends PermissionsTest {

    @SuppressWarnings("null")
    @Test
    public void testInsert() throws NullPointerException, FileNotFoundException {
	Database db = prepareDatabase();

	assertTruncateDependentTables(db);

	ChatMessage msg = assertCreateMessage(db);

	ChatMessageMapper mapper = null;
	try {
	    mapper = new ChatMessageMapper(db);
	} catch (Exception e) {
	    fail(StackTraceUtil.toString(e));
	}

	assertNotNull(mapper);
	assertTrue(mapper.save(msg));
	assertEquals(countRecords(db, "chat_messages"), 1);
	assertTrue(msg.isPersistent());
	assertTrue(msg.getID() > 0);

	// verify insertion
	List<DatabaseRecord> records = db.getRecords("chat_messages");
	assertNotNull(records);
	assertEquals(records.size(), 1);

	DatabaseRecord record = records.get(0);
	assertNotNull(record);

	// verify data fields
	assertEquals(record.getLong("id"), (Long) msg.getID());
	assertEquals(record.getDate("timestamp"), msg.getTimestamp());
	assertEquals(record.getObject("text"), msg.getText());
	assertEquals(record.getLong("sender"), (Long) msg.getSender().getID());
	assertEquals(record.getLong("recipient"), (Long) msg.getRecipient()
		.getID());

	db.disconnect();
    }

    @SuppressWarnings("null")
    @Test
    public void testUpdate() throws NullPointerException, FileNotFoundException {
	Database db = prepareDatabase();

	assertTruncateDependentTables(db);

	ChatMessage msg = assertCreateMessage(db);

	ChatMessageMapper mapper = null;
	try {
	    mapper = new ChatMessageMapper(db);
	} catch (Exception e) {
	    fail(StackTraceUtil.toString(e));
	}

	assertNotNull(mapper);
	assertTrue(mapper.save(msg));
	assertEquals(countRecords(db, "chat_messages"), 1);
	assertTrue(msg.isPersistent());
	assertTrue(msg.getID() > 0);

	// verify insertion
	List<DatabaseRecord> records = db.getRecords("chat_messages");
	assertNotNull(records);
	assertEquals(records.size(), 1);

	DatabaseRecord record = records.get(0);
	assertNotNull(record);

	// verify data fields
	assertEquals(record.getLong("id"), (Long) msg.getID());
	assertEquals(record.getDate("timestamp"), msg.getTimestamp());
	assertEquals(record.getObject("text"), msg.getText());
	assertEquals(record.getLong("sender"), (Long) msg.getSender().getID());
	assertEquals(record.getLong("recipient"), (Long) msg.getRecipient()
		.getID());

	// update record
	msg.setText("newText");

	assertNotNull(mapper);
	assertTrue(mapper.save(msg));
	assertEquals(countRecords(db, "chat_messages"), 1);
	assertTrue(msg.isPersistent());
	assertTrue(msg.getID() > 0);

	// verify update
	records = db.getRecords("chat_messages");
	assertNotNull(records);
	assertEquals(records.size(), 1);

	record = records.get(0);
	assertNotNull(record);

	// verify data fields
	assertEquals(record.getLong("id"), (Long) msg.getID());
	assertEquals(record.getDate("timestamp"), msg.getTimestamp());
	assertEquals(record.getObject("text"), msg.getText());
	assertEquals(record.getLong("sender"), (Long) msg.getSender().getID());
	assertEquals(record.getLong("recipient"), (Long) msg.getRecipient()
		.getID());

	db.disconnect();
    }

    @SuppressWarnings("null")
    @Test
    public void testDelete() throws NullPointerException, FileNotFoundException {
	Database db = prepareDatabase();

	assertTruncateDependentTables(db);

	UserPermissionsMapper mapper = null;

	try {
	    mapper = new UserPermissionsMapper(db);
	} catch (Exception e) {
	    fail(StackTraceUtil.toString(e));
	}

	UserPermissions permissions = assertCreatePermissions(db);

	assertNotNull(permissions);

	assertTrue(mapper.save(permissions));

	Visit visit = new Visit(permissions);

	VisitMapper v_mapper = null;
	try {
	    v_mapper = new VisitMapper(db);
	} catch (Exception e) {
	    fail(StackTraceUtil.toString(e));
	}

	assertTrue(v_mapper.save(visit));

	// init chat messages
	ChatMessageMapper cm_mapper = null;

	try {
	    cm_mapper = new ChatMessageMapper(db);
	} catch (Exception e) {
	    fail(StackTraceUtil.toString(e));
	}

	assertNotNull(cm_mapper);

	final int recordsCount = 5;

	List<ChatMessage> messages = new ArrayList<ChatMessage>(recordsCount);

	for (int i = 0; i < recordsCount; ++i) {
	    ChatMessage msg = new ChatMessage("message" + Integer.toString(i),
		    new Date(), visit, visit);

	    assertTrue(cm_mapper.save(msg));
	    assertTrue(visit.isPersistent());
	    assertTrue(visit.getID() > 0);

	    messages.add(msg);
	}

	assertEquals(countRecords(db, "chat_messages"), recordsCount);

	db.disconnect();
    }

    @SuppressWarnings("null")
    @Test
    public void testChatMessageMapper() {
	Database db = null;

	try {
	    db = prepareDatabase();
	} catch (Exception e) {
	    fail(StackTraceUtil.toString(e));
	}

	assertNotNull(db);

	ChatMessageMapper mapper = null;

	try {
	    mapper = new ChatMessageMapper(db);
	} catch (Exception e) {
	    fail(StackTraceUtil.toString(e));
	}

	try {
	    mapper = new ChatMessageMapper(null);
	} catch (Exception e) {
	    assertTrue(e instanceof NullPointerException);
	}

	db.disconnect();

	try {
	    mapper = new ChatMessageMapper(db);
	} catch (Exception e) {
	    assertTrue(e instanceof DatabaseNotConnectedException);
	}

	assertNotNull(mapper);
    }

    @SuppressWarnings("null")
    private ChatMessage assertCreateMessage(Database db) {
	ChatMessage result = null;

	UserPermissionsMapper mapper = null;

	try {
	    mapper = new UserPermissionsMapper(db);
	} catch (Exception e) {
	    fail(StackTraceUtil.toString(e));
	}

	UserPermissions permissions = assertCreatePermissions(db);

	assertNotNull(permissions);

	assertTrue(mapper.save(permissions));

	Visit visit = new Visit(permissions);

	VisitMapper v_mapper = null;
	try {
	    v_mapper = new VisitMapper(db);
	} catch (Exception e) {
	    fail(StackTraceUtil.toString(e));
	}

	assertTrue(v_mapper.save(visit));

	result = new ChatMessage("testMessage", new Date(), visit, visit);

	assertNotNull(result);

	return result;
    }

}
