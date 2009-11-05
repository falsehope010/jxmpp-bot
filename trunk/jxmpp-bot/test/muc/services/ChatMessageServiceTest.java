package muc.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import base.DatabaseBaseTest;
import database.Database;
import database.DatabaseRecord;
import domain.muc.ChatMessage;
import domain.muc.UserPermissions;
import domain.muc.Visit;
import exceptions.DatabaseNotConnectedException;

public class ChatMessageServiceTest extends DatabaseBaseTest {

    @Test(expected = NullPointerException.class)
    public void testChatMessageServiceNullDatabase() {
	ChatMessageService service = new ChatMessageService(null);
	assertNull(service);
    }

    @Test(expected = DatabaseNotConnectedException.class)
    public void testChatMessageServiceDatabaseDisconnected() throws Exception {
	Database db = prepareDatabase();
	db.disconnect();

	ChatMessageService service = new ChatMessageService(db);
	assertNull(service);
    }

    @Test
    public void testChatMessageService() throws Exception {
	Database db = prepareDatabase();

	ChatMessageService service = new ChatMessageService(db);
	assertNotNull(service);

	db.disconnect();
    }

    @Test
    public void testChatMessageServiceDefaultCapacity() throws Exception {
	Database db = prepareDatabase();

	ChatMessageService service = new ChatMessageService(db);
	assertNotNull(service);

	assertEquals(service.getCapacity(), ChatMessageService.DEFAULT_CAPACITY);

	db.disconnect();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testChatMessageServiceZeroCapacity() throws Exception {
	Database db = prepareDatabase();

	ChatMessageService service = new ChatMessageService(db, 0);
	assertNull(service);

	db.disconnect();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testChatMessageServiceCapacitySmallerZero() throws Exception {
	Database db = prepareDatabase();

	ChatMessageService service = new ChatMessageService(db, -5);
	assertNull(service);

	db.disconnect();
    }

    @Test
    public void testChatMessageServiceNormalCapacity() throws Exception {
	Database db = prepareDatabase();

	ChatMessageService service = new ChatMessageService(db, 5);
	assertNotNull(service);

	db.disconnect();
    }

    @Test
    public void testFlush() throws Exception {
	final int bufferSize = 10;
	Database db = prepareDatabase();

	assertTruncateDependentTables(db);

	ChatMessageService service = new ChatMessageService(db, bufferSize);
	assertNotNull(service);

	final int lessThenBufferCapacity = bufferSize - 1;
	List<ChatMessage> msgList = createMessages(lessThenBufferCapacity);
	assertNotNull(msgList);
	assertEquals(lessThenBufferCapacity, msgList.size());

	for (ChatMessage msg : msgList) {
	    service.save(msg);
	}

	/*
	 * Check that no messages has been saved into database and all of them
	 * has been stored inside service buffer instead
	 */
	assertEquals(0, db.countRecords("chat_messages"));

	service.flush();

	List<DatabaseRecord> records = db.getRecords("chat_messages");
	assertNotNull(records);
	assertEquals(lessThenBufferCapacity, records.size());

	for (int i = 0; i < lessThenBufferCapacity; ++i) {
	    DatabaseRecord record = records.get(i);
	    ChatMessage msg = msgList.get(i);

	    assertTrue(msg.isPersistent());
	    assertTrue(msg.getID() > 0);

	    assertEquals(record.getLong("id"), (Long) msg.getID());
	    assertEquals(record.getLong("sender"), (Long) msg.getSender()
		    .getID());
	    assertEquals(record.getLong("recipient"), (Long) msg.getRecipient()
		    .getID());
	    assertEquals(record.getString("text"), msg.getText());
	    assertEquals(record.getDate("timestamp"), msg.getTimestamp());
	}

	db.disconnect();
    }

    @Test
    public void testFlushZeroItemsInBuffer() throws Exception {
	final int bufferSize = 10;
	Database db = prepareDatabase();

	assertTruncateDependentTables(db);

	ChatMessageService service = new ChatMessageService(db, bufferSize);
	assertNotNull(service);

	/*
	 * Check that no messages has been saved into database and all of them
	 * has been stored inside service buffer instead
	 */
	assertEquals(0, db.countRecords("chat_messages"));

	service.flush();

	assertEquals(0, db.countRecords("chat_messages"));

	db.disconnect();
    }

    @Test
    public void testAutoFlush() throws Exception {
	final int bufferSize = 50;
	Database db = prepareDatabase();

	assertTruncateDependentTables(db);

	ChatMessageService service = new ChatMessageService(db, bufferSize);
	assertNotNull(service);

	final int delta = 25;
	final int msgCount = bufferSize + delta;
	List<ChatMessage> msgList = createMessages(msgCount);
	assertNotNull(msgList);
	assertEquals(msgCount, msgList.size());

	for (ChatMessage msg : msgList) {
	    service.save(msg);
	}

	assertEquals(bufferSize, countRecords(db, "chat_messages"));

	db.disconnect();
    }

    private void assertTruncateDependentTables(Database db) {
	db.truncateTable("chat_messages");
    }

    /**
     * Creates list of non-persistent chat messages
     * 
     * @param recordsCount
     * @return
     */
    private List<ChatMessage> createMessages(int recordsCount) {
	List<ChatMessage> result = new ArrayList<ChatMessage>();

	List<UserPermissions> permissionsList = createPermissions(recordsCount);

	for (int i = 0; i < recordsCount; ++i) {

	    Visit senderVisit = new Visit(permissionsList.get(0));
	    Visit recipientVisit = new Visit(permissionsList.get(0));

	    senderVisit.mapperSetID(i);
	    senderVisit.mapperSetPersistence(true);
	    recipientVisit.mapperSetID(i);
	    recipientVisit.mapperSetPersistence(true);

	    ChatMessage msg = new ChatMessage("text" + String.valueOf(i),
		    new Date(), senderVisit, recipientVisit);

	    result.add(msg);
	}

	return result;
    }
}
