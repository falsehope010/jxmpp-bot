package database;

/**
 * Represents field of {@link DatabaseRecord}. 
 * <p>Stores field name and value
 * @author tillias
 *
 */
public class DatabaseRecordField {
	
	public DatabaseRecordField(String name, Object value){
		this.name = name;
		this.value = value;
	}
	
	public String getName(){
		return name;
	}
	
	public Object getValue(){
		return value;
	}
	
	public void setValue(Object newValue){
		value = newValue;
	}
	
	String name;
	Object value;
}
