package xmpp.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.junit.Test;

import exceptions.ConfigurationException;

public class ConfigurationTest {

    @Test(expected = ConfigurationException.class)
    public void testReadFailFileNotExists() throws ConfigurationException {
	Configuration config = new Configuration();
	config.read(";;;;;");
    }

    @Test
    public void testRead() throws ConfigurationException {
	File f = createXmlFile(FileType.WellFormed);
	Configuration config = new Configuration();
	config.read(f.getAbsolutePath());
    }

    @Test(expected = ConfigurationException.class)
    public void testReadFailNoContents() throws ConfigurationException {
	File f = createXmlFile(FileType.MissingAllContents);
	Configuration config = new Configuration();
	config.read(f.getAbsolutePath());
    }

    @Test(expected = ConfigurationException.class)
    public void testReadFailZeroRooms() throws ConfigurationException {
	File f = createXmlFile(FileType.MissingRooms);
	Configuration config = new Configuration();
	config.read(f.getAbsolutePath());
    }

    @Test(expected = ConfigurationException.class)
    public void testReadFailNoRoomsSection() throws ConfigurationException {
	File f = createXmlFile(FileType.MissingRoomsSection);
	Configuration config = new Configuration();
	config.read(f.getAbsolutePath());
    }

    @Test(expected = ConfigurationException.class)
    public void testReadFailNoConnSection() throws ConfigurationException {
	File f = createXmlFile(FileType.MissingConnSettings);
	Configuration config = new Configuration();
	config.read(f.getAbsolutePath());
    }

    @Test(expected = ConfigurationException.class)
    public void testReadFailNoRoomName() throws ConfigurationException {
	File f = createXmlFile(FileType.IllegalRoom);
	Configuration config = new Configuration();
	config.read(f.getAbsolutePath());
    }

    public void testReadDefaultRoom() throws ConfigurationException {
	File f = createXmlFile(FileType.DefaultRoom);
	Configuration config = new Configuration();
	config.read(f.getAbsolutePath());
    }

    @Test(expected = ConfigurationException.class)
    public void testReadFailIllegalConn1() throws ConfigurationException {
	File f = createXmlFile(FileType.IllegalConn1);
	Configuration config = new Configuration();
	config.read(f.getAbsolutePath());
    }

    @Test(expected = ConfigurationException.class)
    public void testReadFailIllegalConn2() throws ConfigurationException {
	File f = createXmlFile(FileType.IllegalConn2);
	Configuration config = new Configuration();
	config.read(f.getAbsolutePath());
    }

    @Test(expected = ConfigurationException.class)
    public void testReadFailIllegalConn3() throws ConfigurationException {
	File f = createXmlFile(FileType.IllegalConn3);
	Configuration config = new Configuration();
	config.read(f.getAbsolutePath());
    }

    @Test(expected = ConfigurationException.class)
    public void testReadFailIllegalConn4() throws ConfigurationException {
	File f = createXmlFile(FileType.IllegalConn4);
	Configuration config = new Configuration();
	config.read(f.getAbsolutePath());
    }

    public void testReadDefaultPort() throws ConfigurationException {
	File f = createXmlFile(FileType.DefaultPort);
	Configuration config = new Configuration();
	config.read(f.getAbsolutePath());
    }

    @Test(expected = ConfigurationException.class)
    public void testReadFailManyConn() throws ConfigurationException {
	File f = createXmlFile(FileType.MultipleConn);
	Configuration config = new Configuration();
	config.read(f.getAbsolutePath());
    }

    @Test(expected = ConfigurationException.class)
    public void testReadFailManyRoomsSameName() throws ConfigurationException {
	File f = createXmlFile(FileType.MultipleRoomsSameName);
	Configuration config = new Configuration();
	config.read(f.getAbsolutePath());
    }

    @Test
    public void testReadManyRoomsDiffName() throws ConfigurationException {
	File f = createXmlFile(FileType.MultipleRoomsDifferentName);
	Configuration config = new Configuration();
	config.read(f.getAbsolutePath());
    }

    @Test
    public void testGetCredentials() throws ConfigurationException {
	File f = createXmlFile(FileType.WellFormed);
	Configuration config = new Configuration();
	config.read(f.getAbsolutePath());

	ConnectionCredentials credentials = config.getCredentials();
	assertNotNull(credentials);

	assertEquals(credentials.getJID(), "john_doe@xmpp.org");
	assertEquals(credentials.getOwnerJID(), "owner@xmpp.org");
	assertEquals(credentials.getPassword(), "qwerty");
	assertEquals(credentials.getPort(), 5222);
	assertEquals(credentials.getServer(), "xmpp.org");
    }

    @Test
    public void testGetRoomsCredentials() throws ConfigurationException {
	File f = createXmlFile(FileType.WellFormed);
	Configuration config = new Configuration();
	config.read(f.getAbsolutePath());

	RoomCredentials[] rooms = config.getRoomsCredentials();
	assertNotNull(rooms);
	assertEquals(1, rooms.length);

	RoomCredentials room = rooms[0];
	assertNotNull(room);

	assertEquals(room.getConnectTimeout(), 25000);
	assertEquals(room.getNick(), "DigitalSoul2");
	assertEquals(room.getPassword(), "");
	assertEquals(room.getResource(), "home");
	assertEquals(room.getRoomName(), "conference@conference.xmpp.org");
    }

    private File createXmlFile(FileType type) {
	switch (type) {
	case WellFormed:
	    return new File("test\\xmpp\\configuration\\files\\well_formed.xml");
	case MissingAllContents:
	    return new File("test\\xmpp\\configuration\\files\\empty.xml");
	case MissingRooms:
	    return new File("test\\xmpp\\configuration\\files\\no_rooms.xml");
	case MissingRoomsSection:
	    return new File(
		    "test\\xmpp\\configuration\\files\\no_rooms_section.xml");
	case MissingConnSettings:
	    return new File("test\\xmpp\\configuration\\files\\no_conn.xml");
	case IllegalRoom:
	    return new File(
		    "test\\xmpp\\configuration\\files\\illegal_room.xml");
	case DefaultRoom:
	    return new File(
		    "test\\xmpp\\configuration\\files\\default_room.xml");
	case IllegalConn1:
	    return new File(
		    "test\\xmpp\\configuration\\files\\illegal_conn1.xml");
	case IllegalConn2:
	    return new File(
		    "test\\xmpp\\configuration\\files\\illegal_conn2.xml");
	case IllegalConn3:
	    return new File(
		    "test\\xmpp\\configuration\\files\\illegal_conn3.xml");
	case IllegalConn4:
	    return new File(
		    "test\\xmpp\\configuration\\files\\illegal_conn4.xml");
	case DefaultPort:
	    return new File(
		    "test\\xmpp\\configuration\\files\\default_port.xml");
	case MultipleConn:
	    return new File(
		    "test\\xmpp\\configuration\\files\\multiple_conn.xml");
	case MultipleRoomsSameName:
	    return new File(
		    "test\\xmpp\\configuration\\files\\rooms_same_name.xml");
	case MultipleRoomsDifferentName:
	    return new File(
		    "test\\xmpp\\configuration\\files\\well_formed_many_rooms.xml");
	default:
	    return null;
	}
    }

}

enum FileType {
    WellFormed, MissingAllContents, MissingRooms, MissingRoomsSection, MissingConnSettings, // block1
    IllegalRoom, DefaultRoom, // block2
    /**
     * Missing JID
     */
    IllegalConn1,
    /**
     * Missing password
     */
    IllegalConn2,
    /**
     * Missing server
     */
    IllegalConn3, DefaultPort,
    /**
     * Missing owner
     */
    IllegalConn4, MultipleRoomsDifferentName, MultipleRoomsSameName, MultipleConn

}
