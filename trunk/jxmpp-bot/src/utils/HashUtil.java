package utils;


public class HashUtil {
    public static int hashString(int initial, String str) {

	int result = initial;

	if (str == null) {
	    result = hashInt(result, 0);
	} else {
	    result = hashInt(result, str.hashCode());
	}
	return result;
    }

    public static int hashInt(int initial, int ival) {
	/*
	 * Implementation Note Note that byte and short are handled by this
	 * method, through implicit conversion.
	 */
	return ODD_PRIME_NUMBER * initial + ival;
    }

    public static int hashLong(int initial, long lval) {
	return ODD_PRIME_NUMBER * initial + (int) (lval ^ (lval >>> 32));
    }

    public static final int SEED = 23;
    static final int ODD_PRIME_NUMBER = 37;
}
