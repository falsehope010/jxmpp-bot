package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.*;

import domain.users.AccessLevel;
import domain.users.User;
import exceptions.DatabaseSequenceNotFoundException;

public class Database {

	public Database(String fileName) throws NullPointerException,
			FileNotFoundException {

		if (fileName == null)
			throw new NullPointerException("fileName can't be null");

		File f = new File(fileName);

		if (!f.exists())
			throw new FileNotFoundException("Database file not found");

		this.fileName = fileName;
	}

	/**
	 * Opens connection to database and validates connection status by sending small fast query.
	 * If connection is already open does nothing.
	 * @return true if connection is successfully open, false otherwise
	 */
	public boolean connect() {
		boolean result = false;

		if (isConnected()) { // if already connected
			result = true;
		} else {
			conn = createConnection();

			if (conn != null && countTables() > 0) {
				result = true;

				setConnected(result);
			}
		}

		return result;
	}

	/**
	 * Closes connection to database and validates connection status by sending small fast query.
	 * If connection is already closed does nothing.
	 */
	public void disconnect() {
		try {

			if (isConnected() && conn != null) {
				conn.close();

				if (countTables() <= 0 && conn.isClosed()) {
					setConnected(false);
				}
			}
		} catch (Exception e) {
		}
	}

	/**
	 * Gets value indicating whether database connection is open. Works fast.
	 * @return true if connection is is open, false otherwise
	 */
	public boolean isConnected(){
		return connected;
	}
	
	/**
	 * Returns last inserted rowid (after insert in any table in database)
	 * @return rowid if any insert was performed before call, 0 otherwise
	 */
	public long LastInsertRowID() {
		long result = 0;

		if ( isConnected()){
			Statement stat = null;
			ResultSet rs = null;
			
			try {
				stat = conn.createStatement();
				rs = stat.executeQuery("select last_insert_rowid()");
				
				rs.next();
				result = rs.getLong(1);
				
			} catch (Exception e) {
			} finally {
				
				Cleanup(stat, rs);
			}
		}

		return result;
	}

	/**
	 * Gets underlying database connection. 
	 * @return Valid underlying database connection, null if connection isn't opened
	 */
	public Connection getConnection() {
		Connection result = null;
		if (isConnected()){
			result = conn;
		}else{
			result = null;
		}
		return result;
	}
	
	/**
	 * Closes java.sql.Statement and java.sql.ResultSet. Any can be null.
	 * 
	 * @param stat
	 *            Statement to be closed
	 * @param rs
	 *            ResultSet to be closed
	 */
	public void Cleanup(Statement stat, ResultSet rs) {
		if (stat != null) {
			try {
				stat.close();
			} catch (Exception e) {
			}
		}
		if (rs != null) {
			try {
				rs.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * Closes java.sql.Statement. It can be null.
	 * 
	 * @param stat
	 *            Statement to be closed
	 */
	public void Cleanup(Statement stat) {
		if (stat != null) {
			try {
				stat.close();
			} catch (Exception e) {
			}
		}
	}
	
	/**
	 * Truncates table in database (e.g. deletes all records)
	 * @param tableName Table name 
	 * @return true if succeded, false otherwise
	 */
	public boolean truncateTable(String tableName){
		boolean result = false;
		
		Statement st = null;
		try {
			Connection conn = getConnection();
			st = conn.createStatement();
			
			st.execute("delete from " + tableName + ";");
			
			result = true;
		} catch (Exception e) {
		}
		finally{
			Cleanup(st);
		}
		
		return result;
	}
	
	/**
	 * Retrieves total number of records in database table
	 * @param tableName Table name
	 * @return value greater (or equal) then zero if succeeded, -1 otherwise
	 */
	public long countRecords(String tableName){
		long result = -1;
		
		Statement st = null;
		ResultSet rs = null;
		try {
			Connection conn = getConnection();
			st = conn.createStatement();
			
			rs = st.executeQuery("select count(1) from " + tableName + ";");
			
			if (rs.next()){
				result = rs.getLong(1);
			}
		} catch (Exception e) {
		}
		finally{
			Cleanup(st,rs);
		}
		
		return result;
	}
	
	/**
	 * Gets database file size. Database must be initialized (e.g. isConnected())
	 * @return Database file size in bytes (greater then zero if succeeded) or -1 if failed
	 */
	public long getDbFileSize(){
		long result = -1;

		try {
			File dbFile = new File(fileName);
			if (dbFile.exists()){
				result = dbFile.length();
			}
		} catch (Exception e) {
		}
		
		return result;
	}
	
	/**
	 * Performs vacuum procedure on sqlite database
	 * @return True if succeeded, false otherwise
	 */
	public boolean vacuum(){
		boolean result = false;
		
		Statement st = null;
		try {
			Connection conn = getConnection();

			st = conn.createStatement();
			st.execute("vacuum;");
		} catch (Exception e) {
		}
		finally{
			Cleanup(st);
		}
		
		return result;
	}
	
	/**
	 * Checks whether there exists sequence with given name.
	 * @param sequenceName
	 * @return
	 */
	public boolean sequenceExists(String sequenceName){
		boolean result = false;
		
		PreparedStatement pr = null;
		ResultSet rs = null;
		
		try {
			Connection conn = getConnection();
			pr = conn.prepareStatement("select count(1) from sqlite_sequence where name=?;");
			pr.setString(1, sequenceName);
			
			rs = pr.executeQuery();
			
			if (rs.next()){
				long seqCount = rs.getInt(1);
				
				if(seqCount == 1){
					result = true;
				}
			}
			
		} catch (Exception e) {
		}
		finally{
			Cleanup(pr, rs);
		}
		
		return result;
	}
	
	/**
	 * Gets sequence current value by it's name.
	 * @param sequenceName Name of sequence
	 * @return Value greater or equal to zero if succeded, -1 otherwise (e.g. sequence not exists)
	 */
	public long getSequenceValue(String sequenceName){
		long result = -1;
		
		if (sequenceExists(sequenceName)){
			PreparedStatement pr = null;
			ResultSet rs = null;
			
			try {
				Connection conn = getConnection();
				pr = conn.prepareStatement("select seq from sqlite_sequence where name=?;");
				pr.setString(1, sequenceName);
				
				rs = pr.executeQuery();
				
				if (rs.next()){
					result = rs.getLong(1);
				}
			} catch (Exception e) {
			}
			finally{
				Cleanup(pr,rs);
			}
		}
		
		return result;
	}
	
	/**
	 * Attempts to set new sequence value. 
	 * Note: if there are any records in table with autoincrement field whose values are generated
	 * by sequence and new sequence value is smaller then autoincrement field value of any existing record
	 * then sqlite will ignore new sequence value and set up it in order to match ascending order.
	 * @param newValue New sequence value
	 * @return True if succeded, false otherwise
	 * @throws DatabaseSequenceNotFoundException If sequence with given name doesn't exist
	 */
	public boolean setSequenceValue(String sequenceName, long newValue)
			throws DatabaseSequenceNotFoundException {
		boolean result = false;
		long currentValue = getSequenceValue(sequenceName);

		if (currentValue == -1)
			throw new DatabaseSequenceNotFoundException();
		else {
			// all is ok, propagate changes into db
			PreparedStatement pr = null;
			try {
				Connection conn = getConnection();
				pr = conn
						.prepareStatement("update sqlite_sequence set seq=? where name=?;");
				pr.setLong(1, newValue);
				pr.setString(2, sequenceName);

				int rows_affected = pr.executeUpdate();

				if (rows_affected == 1) {
					result = true;
				}

			} catch (Exception e) {
			} finally {
				Cleanup(pr);
			}
		}

		return result;
	}
	
	/**
	 * Setter for isConnected()
	 * @param value
	 */
	private void setConnected(boolean value){
		connected = value;
	}
	
	/**
	 * Initializes sqlite database connection.
	 * 
	 * @return valid connection to database if succeded, null otherwise
	 */
	private Connection createConnection(){
		Connection result = null;

		try {
			Class.forName("org.sqlite.JDBC");
			result = DriverManager.getConnection("jdbc:sqlite:" + fileName);
		} catch (Exception e) {
			result = null;
		}

		return result;
	}
	
	/**
	 * Counts tables in database. Sends fast select query. Used mainly to detect
	 * whether database responds in connect()/disconnect() methods in order
	 * to validate connection status
	 * @return Value greater or equal then zero if succeeded, -1 otherwise
	 */
	private int countTables(){
		int result = -1;
		
		Statement stat = null;
		ResultSet rs = null;
		try {
			stat = conn.createStatement();
			
			rs = stat.executeQuery("SELECT count(1) FROM sqlite_master where name not like '%sequence%'");
			
			if ( rs.next()){
				int count = rs.getInt(1);
				
				if (count > 0){
					result = count;
				}
			}
		} catch (Exception e) {
		}
		finally{
			Cleanup(stat, rs);
		}
		
		return result;
	}

	
	//TODO: refactor those into userMapper
	public boolean insertUser(String realName, String JID,
			AccessLevel accessLevel) {
		boolean result = false;

		PreparedStatement prep = null;

		try {
			prep = conn
					.prepareStatement("insert into users(real_name,access_level) values (?,?);");

			prep.setString(1, realName);
			prep.setInt(2, accessLevel.getValue());

			prep.execute();

			long usrID = LastInsertRowID();

			if (usrID > 0) {
				Cleanup(prep); // clean up after previous insert

				prep = conn
						.prepareStatement("insert into jids(id_user,jid) values(?,?);");

				prep.setLong(1, usrID);
				prep.setString(2, JID);

				int rows_affected = prep.executeUpdate();

				if (rows_affected == 1)
					result = true;
			}
		} catch (Exception e) {
		} finally {
			Cleanup(prep, null);
		}

		return result;
	}

	public boolean insertUserJid(long UserID, String JID) {
		boolean result = false;

		PreparedStatement prep = null;
		Statement stat = null;
		ResultSet rs = null;

		try {
			// check whether user with given JID exists

			prep = conn
					.prepareStatement("select count(1) from users where id=?;");
			prep.setLong(1, UserID);

			rs = prep.executeQuery();

			if (rs.next()) {
				long count = rs.getLong(1);

				boolean user_exists = count > 0;

				if (user_exists) {

					Cleanup(prep);

					prep = conn
							.prepareStatement("insert into jids(id_user,jid) values(?,?);");
					prep.setLong(1, UserID);
					prep.setString(2, JID);

					int rows_affected = prep.executeUpdate();

					if (rows_affected == 1)
						result = true;
				}
			}
		} catch (Exception e) {
		} finally {
			Cleanup(prep, rs);
			Cleanup(stat);
		}

		return result;
	}

	/**
	 * Loads all users from database
	 * 
	 * @return All users from database if succeded, null if any error is occured
	 */
	public ArrayList<User> loadAllUsers() {
		ArrayList<User> result = null;

		Statement stat = null;
		ResultSet rs = null;

		try {
			//cache all jids from database into memory
			HashMap<Long, ArrayList<String>> usrJids = getUsersJids();

			if (usrJids != null) { // no errors occured

				// load all records from users table
				stat = conn.createStatement();

				rs = stat
						.executeQuery("select id,real_name,access_level from users");

				result = new ArrayList<User>();

				while (rs.next()) {
					try {
						long id = rs.getLong(1);
						String real_name = rs.getString(2);
						int access_level = rs.getInt(3);
						
						if ( usrJids.containsKey(id)){
							ArrayList<String> jidList = usrJids.get(id);
							
							if (jidList.size() > 0){
								String jid = jidList.get(0);
								User usr = new User(real_name, jid, new AccessLevel(access_level));
								usr.mapperSetID(id);

								//add additional jids if they exist
								for (int i = 1; i < jidList.size(); ++i){
									usr.addJID(jidList.get(i));
								}
								
								usr.mapperSetPersistence(true);
								
								result.add(usr);
							}
						}

					} catch (Exception e) {
					}
				}
			}
		} catch (Exception e) {
		} finally {
			Cleanup(stat, rs);
		}

		return result;
	}

	/**
	 * Updates user in database (if user isPersistent). 
	 * Note: method doesn't update jid collection of user, so if you need it, use
	 * Database.updateUserJidCollection() method
	 * @param user User to be updated
	 * @return true if succeded, false otherwise
	 */
	public boolean updateUser(User user){
		boolean result = false;
		
		if ( user != null && user.isPersistent()){
			
			PreparedStatement prep = null;
			
			try {
				//update user fields
				prep = conn.prepareStatement("update users set real_name=?,access_level=? where id=?;");
				prep.setString(1, user.getRealName());
				prep.setInt(2, user.getAccessLevel().getValue());
				prep.setLong(3, user.getID());
				
				int rows_affected = prep.executeUpdate();
				
				if ( rows_affected == 1 ){
					user.mapperSetPersistence(true);
					result = true;
				}
				
			} catch (Exception e) {
			}
			finally{
				Cleanup(prep);
			}
		}
		
		return result;
	}
	
	/**
	 * Updates jid collection in database for given user (if it is persistent).
	 * @param user User which jid collection will be updated
	 * @return True if succeded, false otherwise
	 */
	public boolean updateUserJidCollection(User user){
		boolean result = false;
		
		PreparedStatement prep = null;
		
		if ( user != null && user.isPersistent()){
			try {
				//delete all jids for given user from database
				prep = conn.prepareStatement("delete from jids where id_user=?");
				prep.setLong(1, user.getID());
				
				int rows_affected = prep.executeUpdate();
				
				if (rows_affected > 0 ){
					//now insert jid collection back to database
					prep = conn.prepareStatement("insert into jids(id_user,jid) values(?,?);");
					
					ArrayList<String> jidCollection = user.getJidCollection();
					conn.setAutoCommit(false); // prepare for batch insert jids
					
					for (String jid: jidCollection){
						prep.setLong(1, user.getID());
						prep.setString(2, jid);
						prep.addBatch();
					}
					
					int[] rows_inserted = prep.executeBatch();
					conn.commit();
					conn.setAutoCommit(true); // restore auto commit back
					
					int insertedRows = getSumElements(rows_inserted);
					if (insertedRows == jidCollection.size()){
						result = true;
					}
				}
			} catch (Exception e) {
				System.out.print(e.getMessage());
			}
			finally{
				Cleanup(prep);
			}
		}
		
		return result;
	}
	
	public boolean deleteUser(User user){
		boolean result = false;
		
		PreparedStatement prep = null;

		if ( user != null && user.isPersistent()){
			try {
				//first delete all user's jids
				prep = conn.prepareStatement("delete from jids where id_user=?;");
				prep.setLong(1, user.getID());
				
				int jids_deleted = prep.executeUpdate();
				
				if (jids_deleted == user.getJidCount()){
					//now remove user itself
					
					prep = conn.prepareStatement("delete from users where id=?" );
					prep.setLong(1, user.getID());
					
					int users_deleted = prep.executeUpdate();
					
					if (users_deleted == 1){
						user.mapperSetPersistence(false); // mark user as non persistent
						result = true;
					}
				}
				
			} catch (Exception e) {
			}
			finally{
				Cleanup(prep);
			}
		}
		
		return result;
	}
	
	/**
	 * Retrieves all records from 'jids' database table and creates mapping
	 * between userID and it's jids collection.
	 * We use jids collection because user can have multiple jids
	 * @return Mapping between userID and it's jids collection
	 */
	private HashMap<Long, ArrayList<String>> getUsersJids() {
		HashMap<Long, ArrayList<String>> result = null;

		Statement stat = null;
		ResultSet rs = null;
		try {
			stat = conn.createStatement();

			rs = stat.executeQuery("select id_user,jid from jids;");

			result = new HashMap<Long, ArrayList<String>>();

			while (rs.next()) {
				long usrID = rs.getLong(1);
				String usrJid = rs.getString(2);

				/*
				 * We've just loaded new userID, so we need create new list of
				 * jids for it
				 */
				if (!result.containsKey(usrID)) {
					ArrayList<String> jidList = new ArrayList<String>();
					jidList.add(usrJid);
					result.put(usrID, jidList);
				}
				/*
				 * usrID is already present in hashMap. Simply add jid to
				 * existing jid list
				 */
				else {
					ArrayList<String> jidList = result.get(usrID);
					if (jidList != null) {
						jidList.add(usrJid);
					}
				}
			}

		} catch (Exception e) {
			result = null;
		} finally {
			Cleanup(stat, rs);
		}

		return result;
	}
	
	/**
	 * Gets sum of elements in array
	 * @param ar Array which elements will be summed
	 * @return Sum of elements of array. -1 if ar is null or empty
	 */
	public int getSumElements(int[] ar){
		int result = -1;
		
		if ( ar != null && ar.length > 0 ){
			result = ar[0];
			
			for (int i = 1; i < ar.length; ++i){
				result += ar[i];
			}
		}
		
		return result;
	}
	

	String fileName;
	Connection conn;
	boolean connected;
	boolean isAutoCommitEnabled;
}