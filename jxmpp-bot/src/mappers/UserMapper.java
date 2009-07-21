package mappers;

import java.sql.Connection;
import java.sql.PreparedStatement;

import utils.DateConverter;
import database.Database;
import domain.DomainObject;
import domain.users.User;
import exceptions.DatabaseNotConnectedException;

public class UserMapper extends AbstractMapper {

    protected UserMapper(Database db) throws DatabaseNotConnectedException,
	    NullPointerException {
	super(db);
    }

    @Override
    public boolean delete(DomainObject obj) {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public boolean save(DomainObject obj) {
	boolean result = false;

	if (obj != null && obj instanceof User) {
	    if (!obj.isPersistent()) {
		result = insert(obj);
	    } else {
		// update record inside database
		User user = (User) obj;

	    }
	}

	return result;
    }

    private boolean insert(DomainObject obj) {
	boolean result = false;

	if (obj != null && obj instanceof User) {
	    User user = (User) obj;

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
