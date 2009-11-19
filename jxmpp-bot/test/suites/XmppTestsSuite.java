package suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import xmpp.configuration.ConfigurationTest;
import xmpp.listeners.PrivateMessageListenerTest;
import xmpp.messaging.ParticipantInfoTest;
import xmpp.messaging.PrivateMessageTest;
import xmpp.queue.MessageQueueTest;

@RunWith(Suite.class)
@Suite.SuiteClasses( { ConfigurationTest.class, MessageQueueTest.class,
	ParticipantInfoTest.class, PrivateMessageTest.class,
	PrivateMessageListenerTest.class })
public class XmppTestsSuite {
    // nothing goes here
}
