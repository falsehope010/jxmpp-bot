package mappers;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import database.Database;
import domain.DomainObject;
import domain.syslog.MessageAttribute;
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
		
		boolean result = super.Initialize(db);
		
		if( result ){
			ClearCaches();
			//TODO: Fill all caches from database
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
	
	private void ClearCaches(){
		categories_cache.clear();
		senders_cache.clear();
		types_cache.clear();
	}

	/**
	 * Loads all categories or types or senders from corresponding database table.
	 * Table name is deduced using tp parameter as well as type of items in resulting records set.
	 * @param tp
	 * @return
	 */
	private ArrayList<MessageAttribute> LoadAttributes(MessageAttributeType tp){
		ArrayList<MessageAttribute> result = null;
		
		String tableName = null;
		
		switch (tp){
		case Cathegory:
			tableName = "syslog_categories";
			break;
		case Sender:
			tableName = "syslog_senders";
			break;
		case Type:
			tableName = "syslog_types";
			break;
		}
		
		if (tableName != null){
			//TODO:
		}
		
		return result;
	}
	
	/* 
	 * Categories, senders and types are cached by their name.
	 * This means that there can't be two senders (types,categories) 
	 * with the same name
	 */
	static HashMap<String,MessageCategory> categories_cache = 
		new HashMap<String, MessageCategory>();
	
	static HashMap<String, MessageSender> senders_cache = 
		new HashMap<String, MessageSender>();
	
	static HashMap<String, MessageType> types_cache = 
		new HashMap<String, MessageType>();
}

enum MessageAttributeType{
	Cathegory,
	Sender,
	Type
}
