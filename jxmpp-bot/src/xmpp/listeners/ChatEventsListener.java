package xmpp.listeners;

import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.ParticipantStatusListener;

import xmpp.core.IRoom;
import xmpp.messaging.AppearanceMessage;
import xmpp.messaging.domain.ParticipantInfo;
import xmpp.processing.IProcessor;
import xmpp.utils.presence.PresenceCache;

/**
 * Listens for multi-user chat events such as occupenat joined/leaved chat,
 * granted/revoked permissions and so on. If received such a packets converts
 * them to {@link AppearanceMessage} and redirects to underlying
 * {@link IProcessor} for futher processing
 * 
 * @author tillias
 * 
 */
public class ChatEventsListener extends AbstractChatListener implements
	ParticipantStatusListener {

    /**
     * Creates new events listener using given presence cache and multi-user
     * chat
     * 
     * @param cache
     *            Presence cache that will be used to determing occupants jabber
     *            identifier
     * @param chat
     *            Multi-user chat that this listener listens for participant
     *            status packets
     * @throws NullPointerException
     *             Thrown if any argument passed to constructor is null
     */
    public ChatEventsListener(PresenceCache cache, MultiUserChat chat,
	    IProcessor processor, IRoom room) throws NullPointerException {
	super(cache, chat);

	if (processor == null)
	    throw new NullPointerException("Processor can't be null");
	if (room == null)
	    throw new NullPointerException("Room can't be null");

	this.messageProcessor = processor;
	this.room = room;
    }

    @Override
    public void processPacket(Packet packet) {
	// Method doesn't perform any action. All processing is done in
	// inherited methods of ParticipantStatusListener
    }

    @Override
    public void adminGranted(String participant) {
	// TODO Auto-generated method stub

    }

    @Override
    public void adminRevoked(String participant) {
	// TODO Auto-generated method stub

    }

    @Override
    public void banned(String participant, String actor, String reason) {
	// TODO Auto-generated method stub

    }

    @Override
    public void joined(String participant) {
	processAppearance(participant, true);
    }

    @Override
    public void kicked(String participant, String actor, String reason) {
	// TODO Auto-generated method stub

    }

    @Override
    public void left(String participant) {
	processAppearance(participant, false);
    }

    @Override
    public void membershipGranted(String participant) {
	// TODO Auto-generated method stub

    }

    @Override
    public void membershipRevoked(String participant) {
	// TODO Auto-generated method stub

    }

    @Override
    public void moderatorGranted(String participant) {
	// TODO Auto-generated method stub

    }

    @Override
    public void moderatorRevoked(String participant) {
	// TODO Auto-generated method stub

    }

    @Override
    public void nicknameChanged(String participant, String newNickname) {
	// TODO Auto-generated method stub

    }

    @Override
    public void ownershipGranted(String participant) {
	// TODO Auto-generated method stub

    }

    @Override
    public void ownershipRevoked(String participant) {
	// TODO Auto-generated method stub

    }

    @Override
    public void voiceGranted(String participant) {
	// TODO Auto-generated method stub

    }

    @Override
    public void voiceRevoked(String participant) {
	// TODO Auto-generated method stub

    }

    private void processAppearance(String occupant, boolean isJoined) {
	try {
	    String senderJID = room.getJID(occupant);

	    if (senderJID != null) {
		ParticipantInfo sender = new ParticipantInfo(senderJID,
			occupant);
		AppearanceMessage msg = new AppearanceMessage(sender, isJoined,
			chat.getRoom());

		messageProcessor.processMessage(msg);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    IProcessor messageProcessor;
    IRoom room;
}
