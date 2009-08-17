import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

public class XmppMessageListener implements MessageListener {

    @Override
    public void processMessage(Chat chat, Message message) {
	System.out.print('[' + message.getType().toString() + "]  "
		+ message.getBody() + '\n');
    }

}
