package utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class StackTraceUtil {
    private StackTraceUtil() {
	// nothing todo here
    }

    public static String toString(Throwable e) {
	synchronized (guard) {
	    StringWriter sw = new StringWriter();
	    PrintWriter pw = new PrintWriter(sw, false);

	    if (e != null) {
		e.printStackTrace(pw);
		pw.flush();
	    }

	    return sw.toString();
	}
    }

    private static Object guard = new Object();
}
