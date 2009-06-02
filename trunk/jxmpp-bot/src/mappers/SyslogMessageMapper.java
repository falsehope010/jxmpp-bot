package mappers;

import java.sql.Connection;
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
	public boolean Initialize(Database db) {
		
		boolean result = false;
		
		if (db != null && db.isConnected()) {
			SyslogMessageMapper.db = db;

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
	public boolean Save(DomainObject obj) {
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
	public boolean Delete(DomainObject obj) {
		// TODO Auto-generated method stub
		return false;
	}
	
	protected boolean Insert(DomainObject obj){
		return false;
	}
	
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

					result.put(recordName, record);
				}
			}
		} catch (Exception e) {
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

					result.put(recordName, record);
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

					result.put(recordName, record);
				}
			}
		} catch (Exception e) {
		}
		finally{
			db.Cleanup(st, rs);
		}
		
		return result;
	}
	
	/* 
	 * Categories, senders and types are cached by their name.
	 * This means that there can't be two senders (types,categories) 
	 * with the same name
	 */
	static HashMap<String,MessageCategory> categories_cache = null;
	
	static HashMap<String, MessageSender> senders_cache = null; 
	
	static HashMap<String, MessageType> types_cache = null; 
	
	protected static Database db;
}
