package domain.muc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;

import domain.muc.User;

public class UserTest {

    @Test
    public void testEqualsFailToNull() {
	User user = new User();
	assertFalse(user.equals(null));
    }

    @Test
    public void testEqualsSelf() {
	User user = new User();
	assertTrue(user.equals(user));
    }

    @Test
    public void testEqualsObject() {
	User user1 = new User("user", "job", "position", new Date(), "comments");
	User user2 = new User("user", "job", "position", user1.getBirthday(),
		"comments");
	assertTrue(user1.equals(user2));

	User user3 = new User();
	User user4 = new User();
	assertTrue(user3.equals(user4));
    }

    @Test
    public void testEqualsObjectMultipleFields() {
	User user1 = new User(null, "job", "position", new Date(), "comments");
	User user2 = new User(null, "job", "position", user1.getBirthday(),
		"comments");
	assertTrue(user1.equals(user2));

	user1 = new User("user", null, "position", user1.getBirthday(),
		"comments");
	User user3 = new User("user", null, "position", user1.getBirthday(),
		"comments");
	assertTrue(user1.equals(user3));

	user1 = new User("user", "job", null, user1.getBirthday(), "comments");
	User user4 = new User("user", "job", null, user1.getBirthday(),
		"comments");
	assertTrue(user1.equals(user4));

	user1 = new User("user", "job", "position", null, "comments");
	User user5 = new User("user", "job", "position", null, "comments");
	assertTrue(user1.equals(user5));

	user1 = new User("user", "job", "position", user1.getBirthday(), null);
	User user6 = new User("user", "job", "position", user1.getBirthday(),
		null);
	assertTrue(user1.equals(user6));
    }

    @Test
    public void testEqualsFailFieldsVary() {
	User user1 = new User("user", "job", "position", new Date(), "comments");
	User user2 = new User(null, "job", "position", user1.getBirthday(),
		"comments");
	assertFalse(user1.equals(user2));

	User user3 = new User("user", null, "position", user1.getBirthday(),
		"comments");
	assertFalse(user1.equals(user3));

	User user4 = new User("user", "job", null, user1.getBirthday(),
		"comments");
	assertFalse(user1.equals(user4));

	User user5 = new User("user", "job", "position", null, "comments");
	assertFalse(user1.equals(user5));

	User user6 = new User("user", "job", "position", user1.getBirthday(),
		null);
	assertFalse(user1.equals(user6));

    }

    public void testEqualsMathProps() {
	User user1 = new User("user", "job", "position", new Date(), "comments");
	User user2 = new User("user", "job", "position", user1.getBirthday(),
		"comments");

	User user3 = new User("user", "job", "position", user1.getBirthday(),
		"comments");

	assertTrue(user1.equals(user2));
	assertTrue(user2.equals(user1));

	assertTrue(user1.equals(user3));
	assertTrue(user2.equals(user3));
	assertTrue(user1.equals(user2));
    }

    @Test
    public void testHashCode() {
	User user1 = new User("user", "job", "position", new Date(), "comments");
	User user2 = new User("user", "job", "position", user1.getBirthday(),
		"comments");

	assertEquals(user1.hashCode(), user2.hashCode());

	user1 = new User("user", null, "position", user1.getBirthday(),
		"comments");
	User user3 = new User("user", null, "position", user1.getBirthday(),
		"comments");
	assertEquals(user1.hashCode(), user3.hashCode());

	user1 = new User("user", "job", null, user1.getBirthday(), "comments");
	User user4 = new User("user", "job", null, user1.getBirthday(),
		"comments");
	assertEquals(user1.hashCode(), user4.hashCode());

	user1 = new User("user", "job", "position", null, "comments");
	User user5 = new User("user", "job", "position", null, "comments");
	assertEquals(user1.hashCode(), user5.hashCode());

	user1 = new User("user", "job", "position", user1.getBirthday(), null);
	User user6 = new User("user", "job", "position", user1.getBirthday(),
		null);
	assertEquals(user1.hashCode(), user6.hashCode());
    }

    @Test
    public void testHashCodeFails() {
	User user1 = new User("user", "job", "position", new Date(), "comments");
	User user2 = new User(null, "job", "position", user1.getBirthday(),
		"comments");
	assertFalse(user1.hashCode() == user2.hashCode());

	User user3 = new User("user", null, "position", user1.getBirthday(),
		"comments");
	assertFalse(user1.hashCode() == user3.hashCode());

	User user4 = new User("user", "job", null, user1.getBirthday(),
		"comments");
	assertFalse(user1.hashCode() == user4.hashCode());

	User user5 = new User("user", "job", "position", null, "comments");
	assertFalse(user1.hashCode() == user5.hashCode());

	User user6 = new User("user", "job", "position", user1.getBirthday(),
		null);
	assertFalse(user1.hashCode() == user6.hashCode());
    }

}
