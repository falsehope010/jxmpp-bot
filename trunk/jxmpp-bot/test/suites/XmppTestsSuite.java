package suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import xmpp.configuration.ConfigurationTest;
import xmpp.listeners.ChatMessageListenerTest;
import xmpp.listeners.PrivateMessageListenerTest;
import xmpp.messaging.ParticipantInfoTest;
import xmpp.messaging.PrivateChatMessageTest;
import xmpp.messaging.PrivateMessageTest;
import xmpp.messaging.PublicChatMessageTest;
import xmpp.queue.MessageQueueTest;
import xmpp.utils.activity.ConnectionWatcherTest;
import xmpp.utils.activity.RoomWatcherTest;
import xmpp.utils.presence.PresenceProcessorTest;

@RunWith(Suite.class)
@Suite.SuiteClasses( { ConfigurationTest.class, MessageQueueTest.class,
	ParticipantInfoTest.class, PresenceProcessorTest.class,
	PrivateMessageTest.class, PrivateChatMessageTest.class,
	PublicChatMessageTest.class, PrivateMessageListenerTest.class,
	ChatMessageListenerTest.class, ConnectionWatcherTest.class,
	RoomWatcherTest.class })
public class XmppTestsSuite {
    // nothing goes here
}
