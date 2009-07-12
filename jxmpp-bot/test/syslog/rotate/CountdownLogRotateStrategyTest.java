package syslog.rotate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.util.Date;

import mappers.SyslogMessageMapper;

import org.junit.Test;

import utils.StackTraceUtil;
import base.DatabaseBaseTest;
import database.Database;
import exceptions.DatabaseNotConnectedException;

public class CountdownLogRotateStrategyTest extends DatabaseBaseTest {

    @Test
    public void testRotate() throws NullPointerException,
	    FileNotFoundException, DatabaseNotConnectedException {
	Database db2 = prepareDatabase();

	CountdownLogRotateStrategy s = prepareStrategy(db2);
	assertNotNull(s);

	// prepare database for test
	Database db = prepareDatabase();
	assertTrue(truncateTable(db, "syslog"));
	SyslogMessageMapper mapper = new SyslogMessageMapper(db);
	assertTrue(insertTestSyslogMessages(db, mapper, keptMessagesCount * 2));
	assertEquals(countRecords(db, "syslog"), keptMessagesCount * 2);

	// test it
	assertTrue(s.rotate());
	assertEquals(countRecords(db, "syslog"), keptMessagesCount);

	db.disconnect();

	db2.disconnect();
    }

    @Test
    public void testCountdownLogRotateStrategy() throws NullPointerException,
	    FileNotFoundException {
	Database db = prepareDatabase();

	CountdownLogRotateStrategy s = prepareStrategy(db);

	assertNotNull(s);

	db.disconnect();
    }

    @Test
    public void testCreateWithNullDatabase() {
	try {
	    @SuppressWarnings("unused")
	    CountdownLogRotateStrategy s = new CountdownLogRotateStrategy(null,
		    10, 1000);
	} catch (Exception e) {
	    assertTrue(e instanceof NullPointerException);
	}
    }

    @Test
    public void testCreateWithNotConnectedDatabase()
	    throws NullPointerException, FileNotFoundException {
	Database db = prepareDatabase();
	db.disconnect();

	try {
	    @SuppressWarnings("unused")
	    CountdownLogRotateStrategy s = new CountdownLogRotateStrategy(db,
		    10, 1000);
	} catch (Exception e) {
	    assertTrue(e instanceof DatabaseNotConnectedException);
	}
    }

    @Test
    public void testConstructorInvalidKeptCountPassed()
	    throws NullPointerException, DatabaseNotConnectedException,
	    FileNotFoundException {
	Database db = prepareDatabase();

	final long invalidKeptCount = -10;
	final long zeroKeptCount = 0;

	CountdownLogRotateStrategy s = new CountdownLogRotateStrategy(db,
		invalidKeptCount, 1000);

	assertEquals(s.getKeptMessagesCount(),
		CountdownLogRotateStrategy.DEFAULT_MESSAGES_KEPT);

	s = new CountdownLogRotateStrategy(db, zeroKeptCount, 1000);

	assertEquals(s.getKeptMessagesCount(),
		CountdownLogRotateStrategy.DEFAULT_MESSAGES_KEPT);

    }

    // IterativeLogRotateStrategy tests

    @Test
    public void testInitialRotationDate() throws NullPointerException,
	    FileNotFoundException {

	Database db = prepareDatabase();

	Date startDate = new Date();

	final long intervalTime = 60000;

	CountdownLogRotateStrategy s = prepareStrategy(db, intervalTime);
	assertNotNull(s);

	final long delta = 500;

	long strategyRotateTime = s.getRotationDate().getTime();
	long expectedRotateTime = startDate.getTime() + intervalTime;

	long datesDelta = Math.abs(strategyRotateTime - expectedRotateTime);
	assertTrue(datesDelta < delta);

	System.out.print(datesDelta);

	db.disconnect();
    }

    @Test
    @SuppressWarnings("null")
    public void testUpdateRotationDate() throws NullPointerException,
	    FileNotFoundException, InterruptedException {
	Database db = prepareDatabase();

	final long iterationTime = 300;
	final long delta = 100;

	CountdownLogRotateStrategy s = null;
	try {
	    s = new CountdownLogRotateStrategy(db, keptMessagesCount,
		    iterationTime);
	} catch (Exception e) {
	    fail(StackTraceUtil.toString(e));
	}

	int cnt = 0;
	int maxCnt = 20;

	// now we update rotation date maxCnt times and check whether expected
	// rotation date
	// is almost equal to actual rotation date

	do {
	    Thread.sleep(iterationTime);

	    // compare current system time and rotation time
	    long strategyRotateTime = s.getRotationDate().getTime();
	    long expectedRotateTime = System.currentTimeMillis();

	    long actualDelta = Math
		    .abs(strategyRotateTime - expectedRotateTime);

	    assertTrue(actualDelta < delta);

	    s.updateRotationDate();

	    ++cnt;
	} while (cnt < maxCnt);

	assertNotNull(s);
    }

    private CountdownLogRotateStrategy prepareStrategy(Database db) {
	return prepareStrategy(db, 1000);
    }

    private CountdownLogRotateStrategy prepareStrategy(Database db,
	    long iterationTime) {
	CountdownLogRotateStrategy result = null;

	try {
	    result = new CountdownLogRotateStrategy(db, keptMessagesCount,
		    iterationTime);
	} catch (Exception e) {
	    fail(StackTraceUtil.toString(e));
	}

	return result;
    }

    final static int keptMessagesCount = 100;
}
