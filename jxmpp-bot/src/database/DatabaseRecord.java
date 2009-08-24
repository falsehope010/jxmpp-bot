package database;

import java.util.ArrayList;
import java.util.Date;

import utils.DateConverter;

/**
 * Universal database record. It can contains any number of
 * {@link DatabaseRecordField}
 * <p>
 * DatabaseRecord is heavily used by unit testing. You can get field values
 * using {@link #getObject(String)}, add new fields and update existing ones
 * <p>
 * Currently record stores it's fields as ArrayList. HashMap I think is too
 * outweighed, and if record has 10-20 fields, performance won't be too slow.
 * 
 * @see Database#getRecords(String)
 * @see DatabaseRecordField
 * @version 1.0
 * @author tillias
 * 
 */
public class DatabaseRecord {

    /**
     * Creates new empty database record. You can use
     * {@link #setFieldValue(String, Object)} to populate record with fields
     */
    public DatabaseRecord() {
	fields = new ArrayList<DatabaseRecordField>();
    }

    /**
     * Sets field value. If field does not exist creates new one, otherwise
     * updates it's value
     * 
     * @param fieldName
     *            Field name
     * @param value
     *            Field value
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
     * Gets value indicating whether field with given name holds null-reference
     * 
     * @param fieldName
     *            Field name
     * @return True if field with given name holds null-reference, otherwise
     *         false
     * @throws IllegalArgumentException
     *             Thrown if field with given name doesn't exist
     */
    public boolean isNull(String fieldName) throws IllegalArgumentException {
	Object fieldValue = getObject(fieldName);

	return fieldValue == null;
    }

    /**
     * Gets field value.
     * 
     * @param fieldName
     *            Field name
     * @return Field value if field with given name exists
     * @throws IllegalArgumentException
     *             Thrown if field with given name doesn't exist
     */
    public Object getObject(String fieldName) throws IllegalArgumentException {
	Object result = null;

	DatabaseRecordField field = getField(fieldName);

	if (field != null) {
	    result = field.getValue();
	} else
	    throw new IllegalArgumentException("Field " + fieldName
		    + " doesn't exist");

	return result;
    }

    /**
     * Gets field value as Long
     * 
     * @param fieldName
     *            Field name
     * @return Field value as Long
     * @throws IllegalArgumentException
     *             Thrown if field with given name doesn't exist
     * @throws ClassCastException
     *             Thrown if field value can't be casted to Long
     */
    public Long getLong(String fieldName) throws IllegalArgumentException,
	    ClassCastException {
	Object fieldValue = getObject(fieldName);

	if (fieldValue instanceof Integer || fieldValue instanceof Byte
		|| fieldValue instanceof Short) {
	    Integer int_val = (Integer) fieldValue;
	    return new Long(int_val.longValue());
	}

	if (fieldValue instanceof Long) {
	    return (Long) fieldValue;
	}

	throw new ClassCastException("Can't cast field value=["
		+ fieldValue.toString() + "] to Long");
    }

    public Integer getInt(String fieldName) {
	Object fieldValue = getObject(fieldName);

	if (fieldValue instanceof Integer || fieldValue instanceof Byte
		|| fieldValue instanceof Short) {
	    return (Integer) fieldValue;
	}

	throw new ClassCastException("Can't cast field value=["
		+ fieldValue.toString() + "] to Integer");
    }

    public String getString(String fieldName) {
	Object fieldValue = getObject(fieldName);

	if (fieldValue instanceof String) {
	    return (String) fieldValue;
	}

	throw new ClassCastException("Can't cast field value=["
		+ fieldValue.toString() + "] to String");
    }

    /**
     * Gets field value as {@link Date}
     * 
     * @param fieldName
     *            Field name
     * @return Field value as Date
     * @throws IllegalArgumentException
     *             Thrown if field with given name doesn't exist
     * @throws ClassCastException
     *             Thrown if field value can't be casted to Long
     */
    public Date getDate(String fieldName) throws IllegalArgumentException,
	    ClassCastException {
	Object fieldValue = getObject(fieldName);

	if (fieldValue == null)
	    return null;

	if (fieldValue instanceof Long) {
	    Long long_val = (Long) fieldValue;
	    return new Date(long_val);
	}

	if (fieldValue instanceof Integer) {
	    Long long_val = ((Integer) fieldValue).longValue();
	    return new Date(long_val);
	}

	if (fieldValue instanceof Date) {
	    return (Date) fieldValue;
	}

	if (fieldValue instanceof java.sql.Date) {
	    return DateConverter.Convert((java.sql.Date) fieldValue);
	}

	if (fieldValue instanceof char[]) {
	    char[] char_val = (char[]) fieldValue;
	    long long_val = -1;
	    try {
		long_val = Long.parseLong(new String(char_val));
	    } catch (Exception e) {
		// nothing todo here
	    }

	    if (long_val != -1) {
		return new Date(long_val);
	    }
	}

	if (fieldValue instanceof String) {
	    String str_val = (String) fieldValue;
	    long long_val = -1;
	    try {
		long_val = Long.parseLong(str_val);
	    } catch (Exception e) {
		// nothing todo here
	    }

	    if (long_val != -1) {
		return new Date(long_val);
	    }
	}

	throw new ClassCastException("Can't cast field value=["
		+ fieldValue.toString() + "] to Date");
    }

    /**
     * Finds field using it's name in internal fields collection
     * 
     * @param name
     *            Field name
     * @return Valid field if exists, null-reference otherwise
     */
    private DatabaseRecordField getField(String name) {
	DatabaseRecordField result = null;

	for (DatabaseRecordField f : fields) {
	    if (f.getName().equals(name)) {
		result = f;
		break;
	    }
	}

	return result;
    }

    ArrayList<DatabaseRecordField> fields;
}
