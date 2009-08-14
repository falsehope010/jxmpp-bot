package utils;

public class StringHasher {
    public static int hashString(int aSeed, String str) {

	int result = aSeed;

	if (str == null) {
	    result = hashInt(result, 0);
	} else {
	    result = hashInt(result, str.hashCode());
	}
	return result;
    }

    private static int hashInt(int aSeed, int ival) {
	/*
	 * Implementation Note Note that byte and short are handled by this
	 * method, through implicit conversion.
	 */
	return firstTerm(aSeed) + ival;
    }

    private static int firstTerm(int aSeed) {
	return fODD_PRIME_NUMBER * aSeed;
    }

    public static final int SEED = 23;
    protected static final int fODD_PRIME_NUMBER = 37;
}
