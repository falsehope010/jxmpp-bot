package suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import xmpp.configuration.ConfigurationTest;
import xmpp.listeners.PrivateMessageListenerTest;
import xmpp.messaging.ParticipantInfoTest;
import xmpp.messaging.PrivateMessageTest;
import xmpp.queue.MessageQueueTest;
import xmpp.utils.PresenceProcessorTest;

@RunWith(Suite.class)
@Suite.SuiteClasses( { ConfigurationTest.class, MessageQueueTest.class,
	ParticipantInfoTest.class, PresenceProcessorTest.class,
	PrivateMessageTest.class, PrivateMessageListenerTest.class })
public class XmppTestsSuite {
    // nothing goes here
}
