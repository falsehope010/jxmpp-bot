package mappers;

import java.sql.Connection;
import java.sql.PreparedStatement;

import utils.DateConverter;
import database.Database;
import domain.DomainObject;
import domain.muc.User;
import exceptions.DatabaseNotConnectedException;

public class UserMapper extends AbstractMapper {

    /**
     * Creates new instance of mapper using given database
     * 
     * @param db
     *            Database which will be used by mapper.
     * @throws DatabaseNotConnectedException
     *             Thrown if database is in disconnected state. You must call
     *             {@link Database#connect()} before passing database into
     *             mapper's constructor
     * @throws NullPointerException
     *             Thrown if database is null-reference
     */
    public UserMapper(Database db) throws DatabaseNotConnectedException,
	    NullPointerException {
	super(db);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation deletes {@link User} from database. No business logic
     * is performed.
     * 
     * <p>
     * Parameter passed to this method is considered to be an instance of
     * {@link User}. If it isn't so or record is not persistent method does
     * nothing and returns false.
     */
    @Override
    public boolean delete(DomainObject obj) {
	boolean result = false;

	if (obj != null && obj instanceof User) {
	    User user = (User) obj;

	    if (user.isPersistent()) {
		PreparedStatement pr = null;

		try {
		    Connection conn = db.getConnection();

		    pr = conn.prepareStatement("delete from users where id=?");

		    pr.setLong(1, user.getID());

		    int rows_affected = pr.executeUpdate();

		    if (rows_affected == 1) {
			user.mapperSetID(0);
			user.mapperSetPersistence(false);

			result = true;
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		} finally {
		    db.Cleanup(pr);
		}
	    }
	}

	return result;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation performs mapping of {@link User} into database.
     * <p>
     * If record isn't persistent it is simply inserted into corresponding
     * database table. If record is persistent it is updated. No additional
     * business logic is performed.
     * <p>
     * Parameter passed to this method is considered to be an instance of
     * {@link User}. If this isn't so method does nothing and returns false
     * 
     * @param obj
     *            {@link User} to be mapped into database
     * 
     */
    @Override
    public boolean save(DomainObject obj) {
	boolean result = false;

	if (obj != null && obj instanceof User) {
	    User user = (User) obj;

	    if (!obj.isPersistent()) {
		result = insertUser(user);
	    } else {
		result = updateUser(user);
	    }
	}

	return result;
    }

    /**
     * Updates user in database. User must be persistent
     * 
     * @param user
     *            User to be updated
     * @return true if succeded, false otherwise
     */
    private boolean updateUser(User user) {
	boolean result = false;

	if (user != null && user.isPersistent()) {

	    PreparedStatement pr = null;

	    try {
		Connection conn = db.getConnection();

		pr = conn
			.prepareStatement("update users set real_name=?, job=?,"
				+ "position=?, birthday=?, comments=? "
				+ "where id=?");
		pr.setString(1, user.getRealName());
		pr.setString(2, user.getJob());
		pr.setString(3, user.getPosition());
		pr.setDate(4, DateConverter.Convert(user.getBirthday()));
		pr.setString(5, user.getComments());
		pr.setLong(6, user.getID());

		int rows_affected = pr.executeUpdate();

		if (rows_affected == 1) {
		    result = true;
		}

	    } catch (Exception e) {
		e.printStackTrace();
	    } finally {
		db.Cleanup(pr);
	    }
	}

	return result;
    }

    /**
     * Inserts new (e.g. non-persistent) user into database. If succeeded marks
     * user as persistent and assigns to it valid ID
     * 
     * @param user
     *            User object to be inserted into database
     * @return True if succeded, false otherwise
     */
    private boolean insertUser(User user) {
	boolean result = false;

	if (user != null && !user.isPersistent()) {

	    PreparedStatement pr = null;

	    try {
		Connection conn = db.getConnection();

		pr = conn
			.prepareStatement("insert into users(real_name,job,position,birthday,comments) "
				+ "values(?,?,?,?,?)");
		pr.setString(1, user.getRealName());
		pr.setString(2, user.getJob());
		pr.setString(3, user.getPosition());
		pr.setDate(4, DateConverter.Convert(user.getBirthday()));
		pr.setString(5, user.getComments());

		int rows_affected = pr.executeUpdate();

		if (rows_affected == 1) {
		    long recordID = db.LastInsertRowID();

		    if (recordID > 0) {
			user.mapperSetID(recordID);
			user.mapperSetPersistence(true);

			result = true;
		    }
		}

	    } catch (Exception e) {
		e.printStackTrace();
	    } finally {
		db.Cleanup(pr);
	    }
	}

	return result;
    }

}
