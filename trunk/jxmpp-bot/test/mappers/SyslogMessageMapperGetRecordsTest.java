package mappers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import utils.StackTraceUtil;
import base.DatabaseBaseTest;
import database.Database;
import domain.syslog.Message;
import domain.syslog.SyslogSession;
import exceptions.DatabaseNotConnectedException;

public class SyslogMessageMapperGetRecordsTest extends DatabaseBaseTest {
    @Test
    public void testGetMessagesByTextOnly() throws NullPointerException,
	    FileNotFoundException, IllegalArgumentException {

	Database db = prepareDatabase();

	SyslogMessageMapper mapper = insertTestMessages(db);

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
	    assertTrue(mapper.save(new Message(testAttribute, testAttribute
		    + n_prefix, testAttribute + n_prefix, testAttribute
		    + n_prefix, new SyslogSession())));
	    assertTrue(mapper.save(new Message(testAttribute + n_prefix,
		    testAttribute + "_n", testAttribute + "_n", testAttribute
			    + "_n", new SyslogSession())));
	}

	assertTrue(db.commit());

	assertTrue(db.setAutoCommit(true));

	assertEquals(countRecords(db, "syslog"), messagesCount * 2);

	return mapper;
    }

    final int messagesCount = 100;
    final String testAttribute = "test";
    final String n_prefix = "_n";
}
