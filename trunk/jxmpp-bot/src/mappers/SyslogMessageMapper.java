package mappers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

import database.Database;
import domain.DomainObject;
import domain.syslog.MessageCategory;
import domain.syslog.MessageSender;
import domain.syslog.MessageType;
import domain.syslog.Message;

/**
 * @author tillias_work
 *
 */
public class SyslogMessageMapper extends AbstractMapper {
	
	public SyslogMessageMapper(){
	}

	@Override
	public boolean initialize(Database db) {
		
		boolean result = false;
		
		if (super.initialize(db)){
			if (categories_cache != null) {
				categories_cache.clear();
			}

			categories_cache = LoadCategories();
			
			if (types_cache != null){
				types_cache.clear();
			}
			
			types_cache = LoadTypes();
			
			if (senders_cache != null)
				senders_cache.clear();
			
			senders_cache = LoadSenders();
			
			result = true;
		}

		return result;
	}

	@Override
	public boolean save(DomainObject obj) {
		boolean result = false;
		
		if ( !obj.isPersistent()){
			result = Insert(obj);
		}
		else{
			if ( obj instanceof Message){
				Message msg = (Message) obj;
			}
		}
		return result;
	}
	
	@Override
	public boolean delete(DomainObject obj) {
		// TODO Auto-generated method stub
		return false;
	}
	
	protected boolean Insert(DomainObject obj){
		return false;
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
	
	private void cacheCategory(MessageCategory category){
		if (category != null && category.isPersistent() ){
			String name = category.getName();
			if(!categories_cache.containsKey(name)){
				categories_cache.put(name, category);
			}
		}
	}
	
	private void cacheType(MessageType type){
		if (type != null && type.isPersistent() ){
			String name = type.getName();
			if(!types_cache.containsKey(name)){
				types_cache.put(name, type);
			}
		}
	}
	
	private void cacheSender(MessageSender sender){
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
	static HashMap<String,MessageCategory> categories_cache = new HashMap<String, MessageCategory>();
	
	static HashMap<String, MessageSender> senders_cache = new HashMap<String, MessageSender>(); 
	
	static HashMap<String, MessageType> types_cache = new HashMap<String, MessageType>(); 
	
}
