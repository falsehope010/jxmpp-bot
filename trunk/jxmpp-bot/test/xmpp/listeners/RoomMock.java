package xmpp.listeners;

import java.util.HashMap;

import xmpp.core.IRoom;
import xmpp.messaging.base.Message;

public class RoomMock implements IRoom {

    public RoomMock(String roomName) {
	this.name = roomName;
	bindings = new HashMap<String, String>();
    }

    public void addJid(String occupantName, String jid) {
	if (occupantName != null && jid != null)
	    bindings.put(occupantName, jid);
    }

    @Override
    public String getJID(String occupantName) {
	return bindings.get(occupantName);
    }

    @Override
    public String getName() {
	return name;
    }

    @Override
    public boolean isJoined() {
	return isJoined;
    }

    @Override
    public void join() {
	isJoined = true;
    }

    @Override
    public void leave() {
	isJoined = false;
    }

    @Override
    public void send(Message msg) {
	// TODO Auto-generated method stub

    }

    String name;
    boolean isJoined;
    HashMap<String, String> bindings;

}
