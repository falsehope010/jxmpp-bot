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
import base.DatabaseBaseTest;
import database.Database;
import domain.syslog.Message;
import domain.syslog.SearchSettings;
import domain.syslog.SyslogSession;
import exceptions.DatabaseNotConnectedException;

public class SyslogMessageMapperGetRecordsTest extends DatabaseBaseTest {
    @Test
    public void testGetMessagesByTextOnly() throws NullPointerException,
	    FileNotFoundException, IllegalArgumentException {

	Database db = prepareDatabase();

	SyslogMessageMapper mapper = insertTestMessages(db);

	assertEquals(countRecords(db, "syslog"), messagesCount * 2);

	// test using default message text == testAttibute
	SearchSettings settings = new SearchSettings();
	settings.setTextPattern(testAttribute);

	List<Message> messages = mapper.getMessages(settings);

	assertNotNull(messages);
	assertEquals(messages.size(), messagesCount * 2);

	// test using negotiated message text
	settings = new SearchSettings();
	settings.setTextPattern(testAttribute + n_prefix);

	messages = mapper.getMessages(settings);

	assertNotNull(messages);
	assertEquals(messages.size(), messagesCount);

	db.disconnect();
    }

    @Test
    public void testGetMessagesByDate() throws NullPointerException,
	    FileNotFoundException, InterruptedException {
	Database db = prepareDatabase();

	Date justBefore = new Date();

	Thread.sleep(100);

	SyslogMessageMapper mapper = insertTestMessages(db);

	Thread.sleep(100);

	Date justAfter = new Date();

	// ========

	SearchSettings settings = new SearchSettings();
	settings.setStartDate(justBefore);
	List<Message> messages = mapper.getMessages(settings);

	assertNotNull(messages);
	assertEquals(
		"We've retrieved all messages, using too small start date",
		messages.size(), messagesCount * 2);

	// ========

	settings = new SearchSettings();
	settings.setEndDate(justAfter);
	messages = mapper.getMessages(settings);

	assertNotNull(messages);
	assertEquals("We've retrieved all messages, using too big end date",
		messages.size(), messagesCount * 2);

	// =======

	settings = new SearchSettings();
	settings.setStartDate(justAfter);
	messages = mapper.getMessages(settings);

	assertNotNull(messages);
	assertEquals("We've retrieved nothing, using too big start date",
		messages.size(), 0);

	// ========

	settings = new SearchSettings();
	settings.setEndDate(justBefore);
	messages = mapper.getMessages(settings);

	assertNotNull(messages);
	assertEquals("We've nothing, using too small end date",
		messages.size(), 0);

	db.disconnect();
    }

    @Test
    public void testGetMessagesByCategory() throws NullPointerException,
	    FileNotFoundException {
	Database db = prepareDatabase();

	SyslogMessageMapper mapper = insertTestMessages(db);

	SearchSettings settings = new SearchSettings();
	settings.addCategory(testAttribute);

	List<Message> messages = mapper.getMessages(settings);

	assertNotNull(messages);
	assertEquals(messagesCount, messages.size());

	settings = new SearchSettings();
	settings.addCategory(testAttribute + n_prefix);

	messages = mapper.getMessages(settings);

	assertNotNull(messages);
	assertEquals(messagesCount, messages.size());

	settings = new SearchSettings();
	settings.addCategory(testAttribute);
	settings.addCategory(testAttribute + n_prefix);

	messages = mapper.getMessages(settings);

	assertNotNull(messages);
	assertEquals(messagesCount * 2, messages.size());

	settings = new SearchSettings();

	messages = mapper.getMessages(settings);

	assertNotNull(messages);
	assertEquals(0, messages.size());

	db.disconnect();
    }

    @Test
    public void testGetMessagesByType() throws NullPointerException,
	    FileNotFoundException {
	Database db = prepareDatabase();

	SyslogMessageMapper mapper = insertTestMessages(db);

	SearchSettings settings = new SearchSettings();
	settings.addType(testAttribute);

	List<Message> messages = mapper.getMessages(settings);

	assertNotNull(messages);
	assertEquals(messagesCount, messages.size());

	settings = new SearchSettings();
	settings.addType(testAttribute + n_prefix);

	messages = mapper.getMessages(settings);

	assertNotNull(messages);
	assertEquals(messagesCount, messages.size());

	settings = new SearchSettings();
	settings.addType(testAttribute);
	settings.addType(testAttribute + n_prefix);

	messages = mapper.getMessages(settings);

	assertNotNull(messages);
	assertEquals(messagesCount * 2, messages.size());

	settings = new SearchSettings();

	messages = mapper.getMessages(settings);

	assertNotNull(messages);
	assertEquals(0, messages.size());

	db.disconnect();
    }

    @Test
    public void testGetMessagesBySender() throws NullPointerException,
	    FileNotFoundException {
	Database db = prepareDatabase();

	SyslogMessageMapper mapper = insertTestMessages(db);

	SearchSettings settings = new SearchSettings();
	settings.addSender(testAttribute);

	List<Message> messages = mapper.getMessages(settings);

	assertNotNull(messages);
	assertEquals(messagesCount, messages.size());

	settings = new SearchSettings();
	settings.addSender(testAttribute + n_prefix);

	messages = mapper.getMessages(settings);

	assertNotNull(messages);
	assertEquals(messagesCount, messages.size());

	settings = new SearchSettings();
	settings.addSender(testAttribute);
	settings.addSender(testAttribute + n_prefix);

	messages = mapper.getMessages(settings);

	assertNotNull(messages);
	assertEquals(messagesCount * 2, messages.size());

	settings = new SearchSettings();

	messages = mapper.getMessages(settings);

	assertNotNull(messages);
	assertEquals(0, messages.size());

	db.disconnect();
    }

    @SuppressWarnings("null")
    @Test
    public void testGetMessagesComplexTypesCategoriesSenders()
	    throws NullPointerException, FileNotFoundException {
	Database db = prepareDatabase();

	truncateTable(db, "syslog");

	// prepare attributes
	final int attributesCount = 10;
	List<String> categories = createAttributes(attributesCount, "category");
	List<String> types = createAttributes(attributesCount, "type");
	List<String> senders = createAttributes(attributesCount, "sender");
	SyslogSession session = new SyslogSession();

	// insert messages
	assertTrue(db.setAutoCommit(false));

	final int maxMessagesCount = 50;

	SyslogMessageMapper mapper = null;
	try {
	    mapper = new SyslogMessageMapper(db);
	} catch (Exception e) {
	    fail(StackTraceUtil.toString(e));
	}

	for (int i = 0; i < maxMessagesCount; ++i) {
	    for (int j = 0; j < attributesCount; ++j) {
		String currentCategory = categories.get(j);

		for (int k = 0; k < attributesCount; ++k) {
		    String currentType = types.get(k);

		    for (int l = 0; l < attributesCount; ++l) {
			String currentSender = senders.get(l);

			Message msg = new Message("test", currentCategory,
				currentType, currentSender, session);

			assertTrue(mapper.save(msg));
		    }
		}
	    }
	}

	final int totalMessages = maxMessagesCount * attributesCount
		* attributesCount * attributesCount;

	assertEquals(totalMessages, countRecords(db, "syslog"));

	SearchSettings settings = new SearchSettings();

	settings.addCategory("category1");
	settings.addCategory("category2");
	settings.addType("type1");
	settings.addType("type2");
	settings.addSender("sender1");
	settings.addSender("sender2");

	List<Message> messages = mapper.getMessages(settings);

	assertEquals(messages.size(), 400); // hand calculated value

	settings = new SearchSettings();

	settings.addCategories(categories);
	settings.addTypes(types);
	settings.addSenders(senders);

	messages = mapper.getMessages(settings);

	assertEquals(messages.size(), totalMessages);

	/*
	 * There will be: 1. maxMessagesCount * attributesCount *
	 * attributesCount * attributesCount -- total messages
	 */

	assertTrue(db.commit());
	assertTrue(db.setAutoCommit(true));

	assertTrue(performDeepCleanup(db));

	db.disconnect();
    }

    @SuppressWarnings("null")
    public SyslogMessageMapper insertTestMessages(Database db) {
	assertTrue(truncateTable(db, "syslog"));

	SyslogMessageMapper mapper = null;
	try {
	    mapper = new SyslogMessageMapper(db);
	} catch (DatabaseNotConnectedException e) {
	    fail(StackTraceUtil.toString(e));
	}

	assertTrue(db.setAutoCommit(false));

	for (int i = 0; i < messagesCount; ++i) {
	    assertTrue(mapper.save(new Message(testAttribute, testAttribute,
		    testAttribute, testAttribute, new SyslogSession())));
	    assertTrue(mapper.save(new Message(testAttribute + n_prefix,
		    testAttribute + "_n", testAttribute + "_n", testAttribute
			    + "_n", new SyslogSession())));
	}

	assertTrue(db.commit());

	assertTrue(db.setAutoCommit(true));

	assertEquals(countRecords(db, "syslog"), messagesCount * 2);

	return mapper;
    }

    private List<String> createAttributes(int attributesCount, String prefix) {
	List<String> result = new ArrayList<String>();

	for (int i = 0; i < attributesCount; ++i) {
	    result.add(prefix + Integer.toString(i));
	}

	return result;
    }

    private boolean performDeepCleanup(Database db) {
	boolean result = true;

	result &= truncateTable(db, "syslog");
	result &= truncateTable(db, "syslog_sessions");
	result &= truncateTable(db, "syslog_senders");
	result &= truncateTable(db, "syslog_types");
	result &= truncateTable(db, "syslog_categories");

	result &= db.vacuum();

	return result;
    }

    final int messagesCount = 100;
    final String testAttribute = "test";
    final String n_prefix = "_n";
}
