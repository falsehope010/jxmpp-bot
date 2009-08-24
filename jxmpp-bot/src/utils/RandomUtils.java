package utils;

import java.util.Random;

public class RandomUtils {

    /**
     * Generates random string using characters with codes from 33 to 126
     * (ASCII)
     * 
     * @param length
     *            Length of generated string
     * @return String containing random characters
     * @throws IllegalArgumentException
     *             Thrown if specified length of string is less or equal then
     *             zero
     */
    public static String getRandomString(int length)
	    throws IllegalArgumentException {
	if (length <= 0)
	    throw new IllegalArgumentException("Invalid length parameter");

	StringBuilder sb = new StringBuilder();

	for (int i = 0; i < length; ++i) {
	    int rndChar = RandomUtils.getRandomNumber(33, 126);
	    sb.append((char) rndChar);
	}

	return sb.toString();
    }

    /**
     * Generates random integer number from interval [min;max]
     * 
     * @param min
     *            Lower bound of interval
     * @param max
     *            Higher bound of interval
     * @return Random integer number from interval [min;max]
     */
    public static int getRandomNumber(int min, int max) {
	return rnd.nextInt(max - min + 1) + min;
    }

    /**
     * Generates random e-mail address using format: login@server.domain
     * 
     * @param loginLength
     *            Length of login
     * @param serverLength
     *            Length of server
     * @return Random email address
     */
    public static String getRandomMail(int loginLength, int serverLength) {
	if (loginLength <= 0 || serverLength <= 0)
	    throw new IllegalArgumentException(
		    "Invalid login length and/or server length parameters");

	StringBuilder sb = new StringBuilder();

	// generate login
	for (int i = 0; i < loginLength; ++i) {
	    int rndChar = RandomUtils.getRandomNumber(97, 122); // [a..z]
	    sb.append((char) rndChar);
	}

	sb.append('@');

	// generate server
	for (int i = 0; i < serverLength; ++i) {
	    int rndChar = RandomUtils.getRandomNumber(97, 122); // [a..z]
	    sb.append((char) rndChar);
	}

	sb.append('.');

	// append domain
	for (int i = 0; i < 2; ++i) {
	    int rndChar = RandomUtils.getRandomNumber(97, 122); // [a..z]
	    sb.append((char) rndChar);
	}

	return sb.toString();
    }

    private static Random rnd = new Random();
}
