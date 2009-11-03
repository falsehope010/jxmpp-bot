package muc;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import domain.muc.Room;

public class RoomTest {

    @Test
    public void testEqualsFailNull() {
	Room room = new Room("roomName");
	assertFalse(room.equals(null));

	room = new Room("roomName", "description");
	assertFalse(room.equals(null));
    }

    @Test
    public void testEqualsSelf() {
	Room room = new Room("roomName");
	assertTrue(room.equals(room));

	room = new Room("roomName", "description");
	assertTrue(room.equals(room));
    }

    @Test
    public void testEqualsObject() {
	Room room = new Room("roomName");
	Room room1 = new Room("roomName");

	assertTrue(room.equals(room1));

	room = new Room("roomName", "description");
	room1 = new Room("roomName", "description");
	assertTrue(room.equals(room1));
    }

    @Test
    public void testHashCodeFailsDiffName() {
	Room room = new Room("roomName");
	Room room1 = new Room("roomName1");

	assertFalse(room.hashCode() == room1.hashCode());

	room = new Room("roomName", "description");
	room1 = new Room("roomName", "description1");
	assertFalse(room.hashCode() == room1.hashCode());

	room = new Room("roomName", "description1");
	room1 = new Room("roomName", "description");
	assertFalse(room.hashCode() == room1.hashCode());

	room = new Room("roomName1", "description");
	room1 = new Room("roomName", "description");
	assertFalse(room.hashCode() == room1.hashCode());

	room = new Room("roomName", "description");
	room1 = new Room("roomName1", "description");
	assertFalse(room.hashCode() == room1.hashCode());
    }

    @Test
    public void testHashCode() {
	Room room = new Room("roomName");
	Room room1 = new Room("roomName");

	assertTrue(room.hashCode() == room1.hashCode());

	room = new Room("roomName", "description");
	room1 = new Room("roomName", "description");
	assertTrue(room.hashCode() == room1.hashCode());

	room = new Room("roomName", null);
	room1 = new Room("roomName", null);
	assertTrue(room.hashCode() == room1.hashCode());
    }

}
