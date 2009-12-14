package xmpp.listeners;

import java.util.HashMap;

import xmpp.configuration.RoomCredentials;
import xmpp.core.IConnection;
import xmpp.core.IRoom;
import xmpp.messaging.base.Message;

public class ConnectionMoc implements IConnection {

    public ConnectionMoc() {
	rooms = new HashMap<String, RoomMock>();
    }

    @Override
    public void connect() {
	isConnected = true;
    }

    @Override
    public IRoom createRoom(RoomCredentials credentials) {
	throw new UnsupportedOperationException();
    }

    @Override
    public void disconnect() {
	isConnected = false;
    }

    public void addRoom(RoomMock room) {
	if (room != null)
	    rooms.put(room.getName(), room);
    }

    @Override
    public IRoom getRoom(String roomName) {
	return rooms.get(roomName);
    }

    @Override
    public boolean isConnected() {
	return isConnected;
    }

    @Override
    public void send(Message msg) {
	// TODO Auto-generated method stub

    }

    boolean isConnected = true;
    HashMap<String, RoomMock> rooms;
}
