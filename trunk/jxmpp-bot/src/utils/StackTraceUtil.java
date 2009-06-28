package utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class StackTraceUtil {
	private StackTraceUtil() {
	}
	
	public static String toString(Throwable t){
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw,false);
		
		if ( t != null){
			t.printStackTrace(pw);
			pw.flush();
		}
		
		return sw.toString();
	}
}
