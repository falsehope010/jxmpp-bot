package utils;

import java.util.Random;

public class RandomUtils {

    public static String getRandomString(int length) {
	if (length <= 0)
	    throw new IllegalArgumentException();

	StringBuilder sb = new StringBuilder();

	for (int i = 0; i < length; ++i) {
	    int index = rnd.nextInt(maxChars);
	    sb.append(chars.charAt(index));
	}

	return sb.toString();
    }

    private static final Random rnd = new Random();
    private static final String chars = "abcdefghijklmnopqrstuvwxyz"
	    + "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
	    + "01234567890!@#$%^&*()_+=-,./<>?:;[]{}";
    private static final int maxChars = chars.length();
}
