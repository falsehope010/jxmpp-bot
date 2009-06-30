package database;

import java.util.ArrayList;
import java.util.Date;

/**
 * Universal database record. It can contains any number of {@link DatabaseRecordField}
 * <p>DatabaseRecord is heavily used by unit testing. You can get field values using 
 * {@link #getObject(String)}, add new fields and update existing ones
 * <p>Currently record stores it's fields as ArrayList. HashMap I think is too outweighed,
 * and if record has 10-20 fields, performance won't be too slow.
 * @see Database#getRecords(String)
 * @see DatabaseRecordField
 * @version 1.0
 * @author tillias
 *
 */
public class DatabaseRecord {
	
	/**
	 * Creates new empty database record. You can use {@link #setFieldValue(String, Object)} to
	 * populate record with fields
	 */
	public DatabaseRecord(){
		fields = new ArrayList<DatabaseRecordField>();
	}
	
	/**
	 * Sets field value. If field does not exist creates new one, otherwise
	 * updates it's value
	 * @param fieldName Field name
	 * @param value Field value
	 */
	public void setFieldValue(String fieldName, Object value) {
		DatabaseRecordField field = getField(fieldName);

		if (field != null) {
			field.setValue(value);
		} else {
			DatabaseRecordField newField = new DatabaseRecordField(fieldName,
					value);
			fields.add(newField);
		}
	}
	
	/**
	 * Gets field value.
	 * @param fieldName Field name
	 * @return Field value if field with given name exists
	 * @throws IllegalArgumentException Thrown if field with given name doesn't exist
	 */
	public Object getObject(String fieldName) throws IllegalArgumentException{
		Object result = null;
		
		DatabaseRecordField field = getField(fieldName);
		
		if (field != null){
			result = field.getValue();
		}else
			throw new IllegalArgumentException("Field " + fieldName + " doesn't exist");
		
		return result;
	}
	
	/**
	 * Gets field value as Long
	 * @param fieldName Field name
	 * @return Field value as Long
	 * @throws IllegalArgumentException Thrown if field with given name doesn't exist
	 * @throws ClassCastException Thrown if field value can't be casted to Long
	 */
	public Long getLong(String fieldName) throws IllegalArgumentException, ClassCastException {
		Object fieldValue = getObject(fieldName);

		if (fieldValue instanceof Long || fieldValue instanceof Integer) {
			return new Long(fieldValue.toString()); //(Long) fieldValue;
		} else {
			throw new ClassCastException("Can't cast field value=["
					+ fieldValue.toString() + "] to Long");
		}
	}
	
	public Date getDate(String fieldName){
		Object fieldValue = getObject(fieldName);

		/*
		if (fieldValue instanceof Long || fieldValue instanceof Integer) {
			return new Long(fieldValue.toString()); //(Long) fieldValue;
		} else {
			throw new ClassCastException("Can't cast field value=["
					+ fieldValue.toString() + "] to Long");
		}*/
	}
	
	/**
	 * Finds field using it's name in internal fields collection 
	 * @param name Field name
	 * @return Valid field if exists, null-reference otherwise
	 */
	private DatabaseRecordField getField(String name){
		DatabaseRecordField result = null;
		
		for (DatabaseRecordField f : fields){
			if ( f.getName().equals(name)){
				result = f;
				break;
			}
		}
		
		return result;
	}
	
	
	ArrayList<DatabaseRecordField> fields;
}
