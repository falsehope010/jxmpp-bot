package utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;

import org.junit.Test;

import base.DatabaseBaseTest;
import database.Database;

public class ArrTest extends DatabaseBaseTest {

    @Test
    public void testGetSumElements() throws NullPointerException,
	    FileNotFoundException {

	Database db = new Database(testDbName);
	assertNotNull(db);

	int[] source = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };

	int sum = 0;

	for (int i = 0; i < source.length; ++i) {
	    sum += source[i];
	}

	int test_sum = db.getSumElements(source);

	assertEquals(sum, test_sum);

	source = new int[] {};
	assertEquals(db.getSumElements(source), -1);

	source = null;
	assertEquals(db.getSumElements(source), -1);
    }

}
