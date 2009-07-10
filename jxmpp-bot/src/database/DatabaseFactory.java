package database;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Provides methods of creating of Database objects
 * 
 * @author tillias
 * 
 */
public class DatabaseFactory {

	/**
	 * Creates new instance of factory using given file name.
	 * 
	 * @param fileName
	 *            File name for all databases which will be produced by factory
	 *            instance.
	 * @throws FileNotFoundException
	 *             Thrown if invalid file name was given (e.g. file not exists)
	 * @throws NullPointerException
	 *             Thrown if instead of valid file name null-reference was
	 *             passed
	 */
	public DatabaseFactory(String fileName) throws FileNotFoundException,
			NullPointerException {

		File f = new File(fileName);

		if (!f.exists())
			throw new FileNotFoundException("Database file " + fileName
					+ " not found");

		this.fileName = fileName;
	}

	/**
	 * Creates new database instance
	 * 
	 * @return Valid sqlite database if succeded, null-reference otherwise (e.g.
	 *         invalid database file)
	 */
	public Database createDatabase() {
		Database result = null;

		try {
			result = new Database(fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	String fileName;
}
