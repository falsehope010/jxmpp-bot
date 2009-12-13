package xmpp.utils.activity;

import xmpp.core.IRoom;

public class RoomMock implements IRoom {

    @Override
    public String getJID(String occupantName) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public String getName() {
	// TODO Auto-generated method stub
	return null;
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

    boolean isJoined;
}
