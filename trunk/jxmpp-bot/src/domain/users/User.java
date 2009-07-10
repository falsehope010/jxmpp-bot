package domain.users;

import java.util.ArrayList;

import domain.DomainObject;

public class User extends DomainObject {
	String _real_name;
	AccessLevel _accessLevel;
	ArrayList<String> _jidCollection;

	/**
	 * Creates new User
	 * 
	 * @param realName
	 *            User real name (any combination of name, surname, lastname)
	 * @param jid
	 *            User JID (jabber identifier)
	 * @param accessLevel
	 *            Access level of user (affects permissions)
	 * @throws NullPointerException
	 *             Thrown if accessLevel or jid parameter is null
	 */
	public User(String realName, String jid, AccessLevel accessLevel)
			throws NullPointerException {

		if (accessLevel == null) {
			throw new NullPointerException("accessLevel can't be null");
		}
		if (jid == null) {
			throw new NullPointerException("jid can't be null");
		}

		mapperSetPersistence(false);
		_real_name = realName;

		_accessLevel = accessLevel;

		_jidCollection = new ArrayList<String>();
		_jidCollection.add(jid);
	}

	// getters

	/**
	 * Gets user real name. Can be null
	 * 
	 * @return User's real name (any combination of name, surname, last name and
	 *         so on
	 */
	public String getRealName() {
		return _real_name;
	}

	/**
	 * Gets user access level. Can't be null
	 * 
	 * @return User access level
	 */
	public AccessLevel getAccessLevel() {
		return _accessLevel;
	}

	/**
	 * Sets new user real name. Can be null
	 * 
	 * @param realName
	 *            New user's real name
	 */
	public void setRealName(String realName) {
		_real_name = realName;
	}

	/**
	 * Sets new user access level. Can't be null
	 * 
	 * @param accessLevel
	 *            New user's access level.
	 * @throws NullPointerException
	 *             Thrown if you passed null accessLevel parameter
	 */
	public void setAccessLevel(AccessLevel accessLevel)
			throws NullPointerException {

		if (accessLevel == null) {
			throw new NullPointerException("accessLevel can't be null");
		}

		_accessLevel = accessLevel;
	}

	/**
	 * Gets collection of JIDs for user
	 * 
	 * @return JID collection of user
	 */
	public ArrayList<String> getJidCollection() {
		return _jidCollection;
	}

	/**
	 * Gets jid count. User can have multiple jids
	 * 
	 * @return Jid count
	 */
	public int getJidCount() {
		return _jidCollection.size();
	}

	/**
	 * Adds JID to user's jid collection. Duplicates will be ignored
	 * 
	 * @param JID
	 *            JID to be added into user's jid collection. Can't be null
	 * @throws NullPointerException
	 *             Thrown if JID to be added is null
	 */
	public void addJID(String JID) throws NullPointerException {

		if (JID == null)
			throw new NullPointerException("JID can't be null reference");

		if (!_jidCollection.contains(JID)) {
			_jidCollection.add(JID);
		}
	}

	/**
	 * Removes JID from user's jid collection.
	 * 
	 * @param JID
	 *            JID to be removed, can be null
	 */
	public void removeJID(String JID) {
		if (_jidCollection.contains(JID)) {
			_jidCollection.remove(JID);
		}
	}

	/**
	 * Checks whether user has given JID
	 * 
	 * @param JID
	 *            JID to be checked
	 * @return True if user has given JID, false otherwise
	 */
	public boolean hasJID(String JID) {
		return _jidCollection.contains(JID);
	}

	public void DebugPrint() {
		System.out.print("User id: " + getID() + '\n');
		System.out.print("User name: " + _real_name + '\n');
		System.out.print("User access level: " + _accessLevel.value + '\n');

		for (String jid : _jidCollection) {
			System.out.print("   " + jid + '\n');
		}
	}

	/**
	 * Checks two users for equality
	 * 
	 * @param rhs
	 *            User with which equality will be checked
	 * @return True if users are equal, false otherwise
	 */
	public boolean equals(User rhs) {
		boolean result = false;

		try {

			if (getID() == rhs.getID() && _real_name.equals(rhs.getRealName())) {
				int rhs_level = rhs.getAccessLevel().getValue();

				if (_accessLevel.getValue() == rhs_level) {

					ArrayList<String> rhsJidCollection = rhs.getJidCollection();
					// compare jids
					if (getJidCount() == rhsJidCollection.size()) {

						// compare each JID one by one
						result = (_jidCollection.containsAll(rhsJidCollection) && _jidCollection
								.containsAll(rhsJidCollection));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

}
