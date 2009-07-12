package utils;

import java.sql.Date;

public class DateConverter {

    public static java.sql.Date Convert(java.util.Date date) {
	synchronized (guard) {
	    java.sql.Date result = null;
	    if (date != null) {
		result = new Date(date.getTime());
	    }
	    return result;
	}
    }

    public static java.util.Date Convert(java.sql.Date date) {
	synchronized (guard) {
	    java.util.Date result = null;
	    if (date != null) {
		result = new java.util.Date(date.getTime());
	    }
	    return result;
	}
    }

    private static Object guard = new Object();

}
