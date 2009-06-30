package utils;

public class ThreadUtils {
	public static Thread start(Runnable r){
		Thread result = null;
		if (r != null){
			result = new Thread(r);
			result.start();
		}
		return result;
	}
}
