package utils;

public class MemoryAnalizer {

    public static long getMemoryUse() {
	putOutTheGarbage();
	long totalMemory = Runtime.getRuntime().totalMemory();

	putOutTheGarbage();
	long freeMemory = Runtime.getRuntime().freeMemory();

	return (totalMemory - freeMemory);
    }

    private static void putOutTheGarbage() {
	collectGarbage();
	collectGarbage();
    }

    @SuppressWarnings("static-access")
    private static void collectGarbage() {
	try {
	    System.gc();
	    Thread.currentThread().sleep(fSLEEP_INTERVAL);
	    System.runFinalization();
	    Thread.currentThread().sleep(fSLEEP_INTERVAL);
	} catch (InterruptedException ex) {
	    ex.printStackTrace();
	}
    }

    final static long fSLEEP_INTERVAL = 100;
}
