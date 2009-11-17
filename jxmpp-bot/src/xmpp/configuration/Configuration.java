package xmpp.configuration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import exceptions.ConfigurationException;

/**
 * Stores application configuration and provides method of reading it from XML
 * file.
 * 
 * @author tillias
 * @see #read(String)
 * 
 */
public class Configuration {

    /**
     * Loads configuration from XML file.
     * 
     * @param fileName
     *            Name of XML file where configuration is stored in
     * @throws ConfigurationException
     *             Thrown if any error has occurred during configuration reading
     */
    public void read(String fileName) throws ConfigurationException {
	File f = new File(fileName);

	if (!f.exists())
	    throw new ConfigurationException(
		    "Configuration file doesn't exist: " + fileName);

	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	try {
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    Document doc = builder.parse(f);

	    Element root = doc.getDocumentElement();

	    oneCredentialsSectionValidate(root);

	    NodeList nodes = root.getChildNodes();

	    for (int i = 0; i < nodes.getLength(); ++i) {
		Node currentNode = nodes.item(i);
		String nodeName = currentNode.getNodeName();

		if (nodeName.equals("credentials")) {
		    credentials = parseCredentials(currentNode);
		}

		if (nodeName.equals("rooms")) {
		    roomsCredentials = parseRoomsCredentials(currentNode);
		}
	    }

	    if (credentials == null)
		throw new ConfigurationException(
			"Missing configuration section");
	    roomsDiffNameValidate(roomsCredentials);

	} catch (ParserConfigurationException e) {
	    throw new ConfigurationException("Can't initialize XML parser");
	} catch (SAXException e) {
	    throw new ConfigurationException("Can't parse configuration file");
	} catch (IOException e) {
	    throw new ConfigurationException(
		    "I/O Error while reading configuration file");
	}

    }

    /**
     * Gets copy of connection credentials stored inside configuration
     * 
     * @return Connection credentials
     * @see ConnectionCredentials
     */
    public ConnectionCredentials getCredentials() {

	if (credentials == null)
	    return null;

	return new ConnectionCredentials(credentials);
    }

    /**
     * Gets copy of rooms credentials stored inside configuration
     * 
     * @return Rooms credentials
     * @see RoomCredentials
     */
    public RoomCredentials[] getRoomsCredentials() {
	if (roomsCredentials == null)
	    return null;

	int size = roomsCredentials.length;

	RoomCredentials[] result = new RoomCredentials[size];

	for (int i = 0; i < size; ++i) {
	    result[i] = new RoomCredentials(roomsCredentials[i]);
	}

	return result;
    }

    private void oneCredentialsSectionValidate(Element root)
	    throws ConfigurationException {
	NodeList nodes = root.getElementsByTagName("credentials");
	if (nodes == null)
	    throw new ConfigurationException(
		    "Illegal configuration section. Missing");
	if (nodes.getLength() != 1)
	    throw new ConfigurationException(
		    "Only one credentials section is allowed");
    }

    private void roomsDiffNameValidate(RoomCredentials[] rooms)
	    throws ConfigurationException {
	if (roomsCredentials == null || roomsCredentials.length == 0)
	    throw new ConfigurationException(
		    "No room credentials has been specified inside configuration");

	int size = rooms.length;

	HashSet<String> roomNames = new HashSet<String>();
	for (int i = 0; i < size; ++i) {
	    String roomName = rooms[i].getRoomName();
	    if (roomNames.contains(roomName))
		throw new ConfigurationException(
			"Two or more rooms has equal names. Rooms must have different names");
	    roomNames.add(roomName);
	}
    }

    private ConnectionCredentials parseCredentials(Node node)
	    throws ConfigurationException {
	ConnectionCredentials result = null;

	if (node != null) {
	    NodeList childNodes = node.getChildNodes();
	    result = new ConnectionCredentials();

	    try {
		for (int i = 0; i < childNodes.getLength(); ++i) {
		    Node currentNode = childNodes.item(i);
		    String nodeName = currentNode.getNodeName();

		    if (nodeName.equals("#text"))
			continue;

		    switch (CredentialsItem.valueOf(nodeName)) {
		    case nick:
			result.setNick(currentNode.getTextContent());
			break;
		    case password:
			result.setPassword(currentNode.getTextContent());
			break;
		    case server:
			result.setServer(currentNode.getTextContent());
			break;
		    case port:
			result.setPort(Integer.valueOf(currentNode
				.getTextContent()));
			break;
		    case owner:
			result.setOwnerJID(currentNode.getTextContent());
			break;
		    default:
			break;
		    }

		}

		if (!result.validate())
		    throw new ConfigurationException(
			    "Invalid connection credentials");
	    } catch (Exception e) {
		result = null;
	    }

	}

	return result;
    }

    private RoomCredentials[] parseRoomsCredentials(Node node)
	    throws ConfigurationException {
	ArrayList<RoomCredentials> result = new ArrayList<RoomCredentials>();

	if (node != null) {
	    NodeList childNodes = node.getChildNodes();

	    if (childNodes.getLength() == 0)
		throw new ConfigurationException("Missing rooms configuration");

	    for (int i = 0; i < childNodes.getLength(); ++i) {
		Node currentNode = childNodes.item(i);
		String nodeName = currentNode.getNodeName();

		if (nodeName.equals("room")) {
		    RoomCredentials roomCredentials = parseSingleRoomCredentials(currentNode);

		    if (roomCredentials == null)
			throw new ConfigurationException(
				"Can't parse room configuration");

		    result.add(roomCredentials);
		}
	    }

	}

	return result.toArray(new RoomCredentials[] {});
    }

    private RoomCredentials parseSingleRoomCredentials(Node node)
	    throws ConfigurationException {
	RoomCredentials result = null;

	if (node != null) {
	    try {
		NodeList childNodes = node.getChildNodes();

		result = new RoomCredentials();

		for (int i = 0; i < childNodes.getLength(); ++i) {
		    Node currentNode = childNodes.item(i);
		    String nodeName = currentNode.getNodeName();

		    if (nodeName.equals("#text"))
			continue;

		    switch (RoomCredentialsItem.valueOf(nodeName)) {
		    case name:
			result.setRoomName(currentNode.getTextContent());
			break;
		    case nick:
			result.setNick(currentNode.getTextContent());
			break;
		    case resource:
			result.setResource(currentNode.getTextContent());
			break;
		    case password:
			result.setPassword(currentNode.getTextContent());
			break;
		    case connect_timeout:
			result.setConnectTimeout(Integer.valueOf(currentNode
				.getTextContent()));
			break;
		    default:
			break;
		    }
		}

		if (!result.validate())
		    throw new ConfigurationException(
			    "Invalid room(s) configuration");
	    } catch (Exception e) {
		throw new ConfigurationException(
			"Unknown error while parsing room credentials");
	    }
	}

	return result;
    }

    ConnectionCredentials credentials;
    RoomCredentials[] roomsCredentials;
}

enum CredentialsItem {
    nick, password, server, port, owner
}

enum RoomCredentialsItem {
    name, nick, resource, password, connect_timeout
}
