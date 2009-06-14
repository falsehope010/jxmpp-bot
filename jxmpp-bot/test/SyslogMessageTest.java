import domain.syslog.Message;
import domain.syslog.SyslogSession;
import exceptions.InvalidSyslogSessionException;
import junit.framework.TestCase;


public class SyslogMessageTest extends TestCase {

	public void testMessage() {
		String msgText = "testText";
		String category = "testCategory";
		String type = "testType";
		String sender = "testSender";
		SyslogSession nonPersistentSession = new SyslogSession();
		SyslogSession persistentSession = new SyslogSession();
		persistentSession.mapperSetPersistence(true);
		
		Message msg7 = null;
		try {
			msg7 = new Message(msgText,null,type,sender,persistentSession);
		} catch (Exception e) {
			assertTrue(e instanceof NullPointerException);
		}
		assertNull(msg7);
		
		Message msg6 = null;
		try {
			msg6 = new Message(msgText,category,null,sender,persistentSession);
		} catch (Exception e) {
			assertTrue(e instanceof NullPointerException);
		}
		assertNull(msg6);
		
		Message msg5 = null;
		try {
			msg5 = new Message(msgText,category,type,null,persistentSession);
		} catch (Exception e) {
			assertTrue(e instanceof NullPointerException);
		}
		assertNull(msg5);
		
		Message msg4 = null;
		try {
			msg4 = new Message(msgText,category,type,sender,null);
		} catch (Exception e) {
			assertTrue(e instanceof NullPointerException);
		}
		assertNull(msg4);
		
		Message msg2 = null; 
		try {
			msg2 = new Message(msgText,category,type,sender,persistentSession);
		} catch (Exception e) {
		}
		assertNotNull(msg2);
	}
	
	public void testEnd(){
		assertTrue(true);
	}

}
