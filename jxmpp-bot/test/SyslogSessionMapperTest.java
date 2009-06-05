import java.io.FileNotFoundException;

import mappers.SyslogSessionMapper;

import database.Database;
import domain.syslog.SyslogSession;


public class SyslogSessionMapperTest extends DatabaseBaseTest {

	static final String syslogTableName = "syslog_sessions";
	
	public void testSave() {
		fail("Not yet implemented"); // TODO
	}

	public void testInsertSession() throws NullPointerException, FileNotFoundException {
		
		/*
		 * 1. Clear all records from sessions table
		 * 2. Insert several records
		 * 3. Check whether those records has been inserted
		 * 4. Clear all records from sessions table
		 */
		
		Database db = prepareDatabase();
		
		assertEquals(truncateTable(db, syslogTableName), true);
		assertEquals(countRecords(db, syslogTableName), 0);
		
		SyslogSessionMapper mapper = new SyslogSessionMapper();
		assertEquals(mapper.initialize(db), true);
		
		int recordsCount = 5;
		
		for ( int i = 0; i < recordsCount; ++i){
			SyslogSession session = new SyslogSession();
			assertEquals(mapper.save(session), true);
			
			assertEquals(session.isPersistent(), true);
			assertEquals(session.getID() > 0, true);
		}
		
		assertEquals(countRecords(db, syslogTableName), recordsCount);
		
		assertEquals(truncateTable(db, syslogTableName), true);
	}

	public void testUpdateSession() {
		
		/*
		 * 1. Clear all records from sessions table
		 * 2. Create array of several sessions
		 * 3. Insert them and check insertion
		 * 4. Close sessions (domain objects)
		 * 5. Save them into db again
		 * 6. Check that changes has been propagated into db table
		 * 	(compare values in database to in-memory domain objects' fields)
		 * 7. Clear all records from sessions table
		 */
		fail("Not yet implemented"); // TODO
	}

}
