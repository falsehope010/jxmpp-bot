package mappers;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import utils.DateConverter;

import database.Database;
import domain.DomainObject;
import domain.syslog.MessageCategory;
import domain.syslog.MessageSender;
import domain.syslog.MessageType;
import domain.syslog.Message;
import domain.syslog.SyslogSession;
import exceptions.DatabaseNotConnectedException;

/**
 * @author tillias_work
 *
 */
public class SyslogMessageMapper extends AbstractMapper {
	

	/**
	 * Creates new instance of mapper using given database.
	 * @param db Database which will be used by mapper.
	 * @throws NullPointerException Thrown if database is null-reference
	 * @throws DatabaseNotConnectedException Thrown if database is in disconnected state. 
	 * 		   You must call {@link Database#connect()} before passing database into mapper's constructor
	 */
	public SyslogMessageMapper(Database db) throws NullPointerException, DatabaseNotConnectedException{
		super(db);
		
		if (categories_cache == null)
			categories_cache = LoadCategories();
		if (types_cache == null)
			types_cache = LoadTypes();
		if (senders_cache==null)
			senders_cache = LoadSenders();
	}

	@Override
	public boolean save(DomainObject obj) {
		boolean result = false;
		
		if ( !obj.isPersistent()){
			result = insertMessage(obj);
		}
		else{
			/*
			 * Current architecture doesn't allow to update syslog messages
			 */
		}
		return result;
	}
	
	@Override
	public boolean delete(DomainObject obj) {
		boolean result = false;

		if (obj != null && obj instanceof Message) {
			Message msg = (Message) obj;

			if (msg.isPersistent()) {
				PreparedStatement pr = null;
				try {
					Connection conn = db.getConnection();
					pr = conn.prepareStatement("delete from syslog where id=?;");
					pr.setLong(1, msg.getID());
					
					int rows_affected = pr.executeUpdate();
					
					if (rows_affected == 1){
						msg.mapperSetID(0);
						msg.mapperSetPersistence(false);
						
						result = true;
					}
				} catch (Exception e) {
				} finally {
					db.Cleanup(pr);
				}
			}
		}
		return result;
	}

	/**
	 * Loads all syslog messages from database
	 * @return All syslog messages from database
	 */
	public ArrayList<Message> getMessages(){
		ArrayList<Message> result = null;
		
		/*
		select a.id, a.timestamp, a.text, 
		a.session_id, b.start_date, b.end_date,
		a.category_id, c.name,
		a.type_id, d.name,
		a.sender_id, e.name
		from syslog as a, syslog_sessions as b, syslog_categories as c, syslog_types as d, syslog_senders as e
		where a.session_id = b.id and a.category_id = c.id and a.type_id = d.id and a.sender_id = e.id
		*/
		StringBuilder sb = new StringBuilder();
		sb.append("select a.id, a.timestamp, a.text, ");
		sb.append("a.session_id, ");
		sb.append("c.name, ");
		sb.append("d.name, ");
		sb.append("e.name ");
		sb.append("from syslog as a, syslog_sessions as b, syslog_categories as c,");
		sb.append("syslog_types as d, syslog_senders as e ");
		sb.append("where a.session_id = b.id and a.category_id = c.id ");
		sb.append("and a.type_id = d.id and a.sender_id = e.id;");

		String sql = sb.toString();
		
		Statement st = null;
		ResultSet rs = null;
		
		try {
			Connection conn = db.getConnection();
			st = conn.createStatement();
			
			rs = st.executeQuery(sql);
			
			result = new ArrayList<Message>();
			
			while (rs.next()){
				long messageID = rs.getLong(1);
				Date timestamp = rs.getDate(2);
				String text = rs.getString(3);
				long sessionID = rs.getLong(4);
				
				SyslogSession session = SyslogSessionMapper.getByID(sessionID);
				
				String categoryName = rs.getString(5);
				MessageCategory category = getCategory(categoryName);
				
				String typeName = rs.getString(6);
				MessageType type = getType(typeName);
				
				String senderName = rs.getString(7);
				MessageSender sender = getSender(senderName);
				
				Message msg = new Message(text,categoryName,typeName,senderName,session);
				msg.mapperSetID(messageID);
				msg.mapperSetTimestamp(timestamp);
				msg.mapperSetCategory(category);
				msg.mapperSetType(type);
				msg.mapperSetSender(sender);
				msg.mapperSetPersistence(true);
				
				result.add(msg);
				
			}
		} catch (Exception e) {
			result = null;
		}
		finally{
			db.Cleanup(st,rs);
		}
		
		return result;
	}
	
	private boolean insertMessage(DomainObject obj) {
		boolean result = false;

		if (obj != null && obj instanceof Message) {
			Message msg = (Message) obj;
			if (mapAttributes(msg)) {
				PreparedStatement pr = null;
				try {
					Connection conn = db.getConnection();
					
					String sql = "insert into syslog(timestamp,text,session_id,category_id,type_id,sender_id)"
							+ " values(?,?,?,?,?,?);";
					
					pr = conn.prepareStatement(sql);
					
					pr.setDate(1, DateConverter.Convert( msg.getTimestamp()));
					pr.setString(2,msg.getText());
					pr.setLong(3, msg.getSession().getID());
					pr.setLong(4, msg.getCategory().getID());
					pr.setLong(5, msg.getMessageType().getID());
					pr.setLong(6, msg.getSender().getID());
					
					int rows_affected = pr.executeUpdate();
					
					if (rows_affected == 1){
						long recordID = db.LastInsertRowID();
						
						if (recordID > 0){
							msg.mapperSetID(recordID);
							msg.mapperSetPersistence(true);
							result = true;
						}
					}
				} catch (Exception e) {
				}
				finally{
					db.Cleanup(pr);
				}
			}
		}

		return result;
	}
	
	/**
	 * Maps message attributes (category,sender,type) so they become persistent
	 * @param msg Message which attributes should be mapped
	 * @return true if succeeded, false otherwise
	 */
	private boolean mapAttributes(Message msg){
		boolean result = false;
		if (msg != null){
			if (mapCategory(msg) && mapType(msg) && mapSender(msg)
					&& mapSession(msg)) {
				result = true;
			}
		}
		return result;
	}
	
	/**
	 * Maps given category to it's database representation
	 * @param msg Syslog message
	 * @return true if succeeded, false otherwise
	 */
	private boolean mapCategory(Message msg) {
		boolean result = false;

		if (msg != null && msg.getCategory() != null) {
			// get current message category
			MessageCategory msgCategory = msg.getCategory();
			
			// get persistent category
			MessageCategory persistentCategory = getCategory(msgCategory.getName());

			if (persistentCategory != null && persistentCategory.isPersistent()) {
				//replace message category with persistent one
				msg.mapperSetCategory(persistentCategory);
				result = true;
			}
		}

		return result;
	}
	
	/**
	 * Maps given category to it's database representation
	 * @param msg Syslog message
	 * @return true if succeeded, false otherwise
	 */
	private boolean mapType(Message msg){
		boolean result = false;
		
		if (msg != null && msg.getMessageType() != null){
			//get current message type
			MessageType msgType = msg.getMessageType();
			
			//get persistent type
			MessageType persistentType = getType(msgType.getName());
			
			if (persistentType != null && persistentType.isPersistent()){
				//replace message type with persistent one
				msg.mapperSetType(persistentType);
				result = true;
			}
		}
		
		return result;
	}
	
	/**
	 * Maps given sender to it's database representation
	 * @param s Sender to be mapped
	 * @return true if succeeded, false otherwise
	 */
	private boolean mapSender(Message msg){
		boolean result = false;
		
		if(msg != null && msg.getSender() != null){
			//get current message sender
			MessageSender msgSender = msg.getSender();
			
			//get persistent sender
			MessageSender persistentSender = getSender(msgSender.getName());
			
			if (persistentSender != null && persistentSender.isPersistent()){
				//replace message sender with persistent one
				msg.mapperSetSender(persistentSender);
				result = true;
			}
		}
		
		return result;
	}
	
	/**
	 * Maps given syslog session into database. Session can be either persistent or not.
	 * @param msg
	 * @return true if succeeded, false otherwise
	 */
	private boolean mapSession(Message msg){
		boolean result = false;
		
		if (msg != null && msg.getSession() != null)
		try {
			SyslogSession msgSession = msg.getSession();
			
			if (!msgSession.isPersistent()){ // insert session into database
				SyslogSessionMapper mapper = new SyslogSessionMapper(db);
				if (mapper.save(msgSession) ){
					result = true;
				}
			}
			else
				result = true;
			
		} catch (Exception e) {
		}
		
		return result;
	}
	
	/*
	 * We use separate implementation for categories, types and senders
	 * since each of them can change in future. (e.g. we might want additional
	 * properties for MessageCategory, MessageType or MessageSender)
	 * No code duplicating is in mind.
	 */
	
	private HashMap<String,MessageCategory> LoadCategories(){
		HashMap<String, MessageCategory> result = new HashMap<String, MessageCategory>();
		
		ResultSet rs = null;
		Statement st = null;
		
		try {
			Connection conn = db.getConnection();
			st = conn.createStatement();
			
			rs = st.executeQuery("select id,name,description from " + "syslog_categories;");
			
			long recordID;
			String recordName = null;
			String recordDescription = null;
			
			while (rs.next()){
				recordID = rs.getLong(1);
				recordName = rs.getString(2);
				recordDescription = rs.getString(3);
				
				if (!result.containsKey(recordName)) {
					MessageCategory record = new MessageCategory(recordName,
							recordDescription);
					record.mapperSetID(recordID);
					record.mapperSetPersistence(true);
					
					cacheCategory(record);
				}
			}
		} catch (Exception e) {
			System.out.print(e.getMessage());
		}
		finally{
			db.Cleanup(st, rs);
		}
		
		return result;
	}
	
	private HashMap<String,MessageType> LoadTypes(){
		HashMap<String, MessageType> result = new HashMap<String, MessageType>();
		
		ResultSet rs = null;
		Statement st = null;
		
		try {
			Connection conn = db.getConnection();
			st = conn.createStatement();
			
			rs = st.executeQuery("select id,name,description from " + "syslog_types;");
			
			long recordID;
			String recordName = null;
			String recordDescription = null;
			
			while (rs.next()){
				recordID = rs.getLong(1);
				recordName = rs.getString(2);
				recordDescription = rs.getString(3);
				
				if (!result.containsKey(recordName)) {
					MessageType record = new MessageType(recordName,
							recordDescription);
					record.mapperSetID(recordID);
					record.mapperSetPersistence(true);

					cacheType(record);
				}
			}
		} catch (Exception e) {
		}
		finally{
			db.Cleanup(st, rs);
		}
		
		return result;
	}
	
	private HashMap<String,MessageSender> LoadSenders(){
		HashMap<String, MessageSender> result = new HashMap<String, MessageSender>();
		
		ResultSet rs = null;
		Statement st = null;
		
		try {
			Connection conn = db.getConnection();
			st = conn.createStatement();
			
			rs = st.executeQuery("select id,name,description from " + "syslog_senders;");
			
			long recordID;
			String recordName = null;
			String recordDescription = null;
			
			while (rs.next()){
				recordID = rs.getLong(1);
				recordName = rs.getString(2);
				recordDescription = rs.getString(3);
				
				if (!result.containsKey(recordName)) {
					MessageSender record = new MessageSender(recordName,
							recordDescription);
					record.mapperSetID(recordID);
					record.mapperSetPersistence(true);
					
					cacheSender(record);
				}
			}
		} catch (Exception e) {
		}
		finally{
			db.Cleanup(st, rs);
		}
		
		return result;
	}
	
	/**
	 * Attempts to get category with given name from cache or creates new one
	 * (if no such category in cache) and inserts it into database
	 * @param Name Category name
	 * @return Persistent message category if succeded, null-reference otherwise
	 */
	private MessageCategory getCategory(String Name){
		MessageCategory result = null;
		
		if (!categories_cache.containsKey(Name)) {
			
			PreparedStatement pr = null;
			try {

				Connection conn = db.getConnection();
				
				pr = conn.prepareStatement("insert into syslog_categories(name) values(?);");
				pr.setString(1, Name);
				
				int rows_affected = pr.executeUpdate();
				
				if ( rows_affected == 1 ){ // category has been saved
					
					long recordID = db.LastInsertRowID();
					if (recordID > 0){	// category has valid id in db
						result = new MessageCategory(Name);
						result.mapperSetID(recordID);
						result.mapperSetPersistence(true);
						
						cacheCategory(result);
					}
				}
			} catch (Exception e) {
				result = null;
			}
			finally{
				db.Cleanup(pr);
			}
		} else{
			result = categories_cache.get(Name);
		}
		
		return result;
	}
	
	private MessageType getType(String Name){
		MessageType result = null;
		
		if (!types_cache.containsKey(Name)) {
			
			PreparedStatement pr = null;
			try {

				Connection conn = db.getConnection();
				
				pr = conn.prepareStatement("insert into syslog_types(name) values(?);");
				pr.setString(1, Name);
				
				int rows_affected = pr.executeUpdate();
				
				if ( rows_affected == 1 ){ // category has been saved
					
					long recordID = db.LastInsertRowID();
					if (recordID > 0){	// category has valid id in db
						result = new MessageType(Name);
						result.mapperSetID(recordID);
						result.mapperSetPersistence(true);
						
						cacheType(result);
					}
				}
			} catch (Exception e) {
			}
			finally{
				db.Cleanup(pr);
			}
		} else{
			result = types_cache.get(Name);
		}
		
		return result;
	}
	
	private MessageSender getSender(String Name){
		MessageSender result = null;
		
		if (!senders_cache.containsKey(Name)) {
			
			PreparedStatement pr = null;
			try {

				Connection conn = db.getConnection();
				
				pr = conn.prepareStatement("insert into syslog_senders(name) values(?);");
				pr.setString(1, Name);
				
				int rows_affected = pr.executeUpdate();
				
				if ( rows_affected == 1 ){ // category has been saved
					
					long recordID = db.LastInsertRowID();
					if (recordID > 0){	// category has valid id in db
						result = new MessageSender(Name);
						result.mapperSetID(recordID);
						result.mapperSetPersistence(true);
						
						cacheSender(result);
					}
				}
			} catch (Exception e) {
			}
			finally{
				db.Cleanup(pr);
			}
		} else{
			result = senders_cache.get(Name);
		}
		
		return result;
	}
	
	private static void cacheCategory(MessageCategory category){
		if (category != null && category.isPersistent() ){
			String name = category.getName();
			if(!categories_cache.containsKey(name)){
				categories_cache.put(name, category);
			}
		}
	}
	
	private static void cacheType(MessageType type){
		if (type != null && type.isPersistent() ){
			String name = type.getName();
			if(!types_cache.containsKey(name)){
				types_cache.put(name, type);
			}
		}
	}
	
	private static void cacheSender(MessageSender sender){
		if (sender != null && sender.isPersistent() ){
			String name = sender.getName();
			if(!senders_cache.containsKey(name)){
				senders_cache.put(name, sender);
			}
		}
	}
	
	/* 
	 * Categories, senders and types are cached by their name.
	 * This means that there can't be two senders (types,categories) 
	 * with the same name
	 */
	static HashMap<String,MessageCategory> categories_cache = null;
	static HashMap<String, MessageSender> senders_cache = null; 
	static HashMap<String, MessageType> types_cache = null;
}
