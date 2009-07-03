package utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class StackTraceUtil {
	private StackTraceUtil() {
	}

	public static String toString(Throwable e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, false);

		if (e != null) {
			e.printStackTrace(pw);
			pw.flush();
		}

		return sw.toString();
	}
}
