package utils;

import java.util.Random;

public class RandomUtils {

    public static String getRandomString(int length) {
	if (length <= 0)
	    throw new IllegalArgumentException();

	StringBuilder sb = new StringBuilder();

	for (int i = 0; i < length; ++i) {
	    int rndChar = RandomUtils.randomNumber(33, 126);
	    sb.append((char) rndChar);
	}

	return sb.toString();
    }

    public static int randomNumber(int min, int max) {
	return rnd.nextInt(max - min + 1) + min;
    }

    private static Random rnd = new Random();
}
