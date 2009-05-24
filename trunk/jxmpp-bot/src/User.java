import java.util.ArrayList;

public class User {
	long _id;
	String _real_name;
	AccessLevel _accessLevel;
	boolean _isPersistent;

	ArrayList<String> _jidCollection;

	
	/**
	 * Creates new User
	 * @param realName User real name (any combination of name, surname, lastname)
	 * @param jid User JID (jabber identifier)
	 * @param accessLevel Access level of user (affects permissions)
	 * @throws NullPointerException Thrown if accessLevel or jid parameter is null
	 */
	public User(String realName, String jid, AccessLevel accessLevel)
			throws NullPointerException {

		if (accessLevel == null) {
			throw new NullPointerException("accessLevel can't be null");
		}
		if (jid == null) {
			throw new NullPointerException("jid can't be null");
		}

		_isPersistent = false;
		_real_name = realName;

		_accessLevel = accessLevel;

		_jidCollection = new ArrayList<String>();
		_jidCollection.add(jid);
	}

	// getters
	public long getID() {
		return _id;
	}

	public String getRealName() {
		return _real_name;
	}

	public AccessLevel getAccessLevel() {
		return _accessLevel;
	}

	public boolean isPersistent() {
		return _isPersistent;
	}

	// setters
	public void setID(long ID) {
		_id = ID;

		setPersistence(false);
	}

	public void setRealName(String realName) {
		_real_name = realName;

		setPersistence(false);
	}

	public void setAccessLevel(AccessLevel accessLevel)
			throws NullPointerException {

		if (accessLevel == null) {
			throw new NullPointerException("accessLevel can't be null");
		}

		_accessLevel = accessLevel;
		setPersistence(false);
	}

	public void setPersistence(boolean value) {
		_isPersistent = value;
	}

	// JID methods
	public ArrayList<String> getJidCollection() {
		return _jidCollection;
	}

	public void addJID(String JID) throws NullPointerException {

		if (JID == null)
			throw new NullPointerException("JID can't be null reference");

		if (!_jidCollection.contains(JID)) {
			_jidCollection.add(JID);

			setPersistence(false);
		}
	}

	public void removeJID(String JID) {
		if (_jidCollection.contains(JID)) {
			_jidCollection.remove(JID);

			setPersistence(false);
		}
	}

	public boolean hasJID(String JID) {
		return _jidCollection.contains(JID);
	}
	
	public void DebugPrint(){
		System.out.print("User id: " + _id + '\n');
		System.out.print("User name: " + _real_name + '\n');
		System.out.print("User access level: " + _accessLevel.value + '\n');
		
		for ( String jid : _jidCollection){
			System.out.print("   " + jid + '\n');
		}
	}

	public boolean equals(User rhs){
		boolean result = false;
		
		try {
			
			
		} catch (Exception e) {
		}
		
		return result;
	}
}
