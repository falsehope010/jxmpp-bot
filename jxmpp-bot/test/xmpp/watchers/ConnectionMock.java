package xmpp.watchers;

import xmpp.configuration.RoomCredentials;
import xmpp.core.IConnection;
import xmpp.core.IRoom;
import xmpp.messaging.base.Message;

public class ConnectionMock implements IConnection {

    @Override
    public void connect() {
	isConnected = true;
    }

    @Override
    public IRoom createRoom(RoomCredentials credentials) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public void disconnect() {
	isConnected = false;
    }

    @Override
    public IRoom getRoom(String roomName) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public boolean isConnected() {
	return isConnected;
    }

    @Override
    public IRoom[] getRooms() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public void send(Message msg) {
	// TODO Auto-generated method stub

    }

    boolean isConnected;
}
