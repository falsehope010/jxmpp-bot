package mappers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import muc.Repository;
import database.Database;
import domain.DomainObject;
import domain.internal.UserPermissionsEntity;
import domain.muc.UserPermissions;
import exceptions.DatabaseNotConnectedException;

public class UserPermissionsMapper extends AbstractMapper {

    public UserPermissionsMapper(Database db)
	    throws DatabaseNotConnectedException, NullPointerException {
	super(db);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation deletes {@link UserPermissions} from database. No
     * business logic is performed.
     * 
     * <p>
     * Parameter passed to this method is considered to be an instance of
     * {@link UserPermissions}. If it isn't so or record is not persistent
     * method does nothing and returns false.
     */
    @Override
    public boolean delete(DomainObject obj) {
	boolean result = false;

	if (obj != null && obj instanceof UserPermissions) {
	    UserPermissions permissions = (UserPermissions) obj;

	    if (permissions.isPersistent() && permissions.validate()) {
		PreparedStatement pr = null;

		try {
		    Connection conn = db.getConnection();

		    pr = conn.prepareStatement("delete from permissions "
			    + "where id=?");
		    pr.setLong(1, permissions.getID());

		    int rows_affected = pr.executeUpdate();

		    if (rows_affected == 1) {
			permissions.mapperSetID(0);
			permissions.mapperSetPersistence(false);

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
     * This implementation performs mapping of {@link UserPermissions} into
     * database.
     * <p>
     * If record isn't persistent it is simply inserted into corresponding
     * database table. If record is persistent it is updated. No additional
     * business logic is performed.
     * <p>
     * Parameter passed to this method is considered to be an instance of
     * {@link UserPermissions}. If this isn't so method does nothing and returns
     * false
     * 
     * @param obj
     *            {@link UserPermissions} instance to be mapped into database
     * 
     */
    @Override
    public boolean save(DomainObject obj) {
	boolean result = false;

	if (obj != null && obj instanceof UserPermissions) {
	    UserPermissions permissions = (UserPermissions) obj;

	    if (permissions.validate()) {
		if (!permissions.isPersistent()) {
		    result = insertRecord(permissions);
		} else {
		    result = updateRecord(permissions);
		}
	    }
	}

	return result;
    }

    /**
     * Loads all user permission entities from database.
     * <p>
     * For internal use. Method is used by {@link Repository} in order to load
     * high level domain objects of {@link UserPermissions} type
     * 
     * @return List of all entities from database
     * 
     * @see Repository
     * @see UserPermissionsEntity
     */
    public List<UserPermissionsEntity> repositoryGetUserPermissions() {

	ArrayList<UserPermissionsEntity> result = new ArrayList<UserPermissionsEntity>();

	Statement st = null;
	ResultSet rs = null;

	try {
	    Connection conn = db.getConnection();

	    st = conn.createStatement();

	    rs = st
		    .executeQuery("select id,user_id,room_id,jid,access_level from permissions;");

	    long recordID, userID, roomID;
	    String jabberID;
	    int accessLevel;

	    while (rs.next()) {
		recordID = rs.getLong(1);
		userID = rs.getLong(2);
		roomID = rs.getLong(3);
		jabberID = rs.getString(4);
		accessLevel = rs.getInt(5);

		if (recordID > 0 && userID > 0 && roomID > 0) {
		    if (jabberID != null && jabberID.length() > 0) {

			UserPermissionsEntity entity = new UserPermissionsEntity(
				recordID, userID, roomID, jabberID, accessLevel);

			result.add(entity);
		    }
		}
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    db.Cleanup(st, rs);
	}

	return result;
    }

    /**
     * Insert new record in database (if record is not persistent)
     * 
     * @param permissions
     *            Record to be inserted
     * @return True if succeeded, false otherwise
     */
    private boolean insertRecord(UserPermissions permissions) {
	boolean result = false;

	if (permissions != null && !permissions.isPersistent()) {
	    PreparedStatement pr = null;

	    try {
		Connection conn = db.getConnection();

		pr = conn
			.prepareStatement("insert into permissions(user_id,room_id,jid,access_level) "
				+ "values(?,?,?,?)");
		pr.setLong(1, permissions.getUser().getID());
		pr.setLong(2, permissions.getRoom().getID());
		pr.setString(3, permissions.getJabberID());
		pr.setInt(4, permissions.getAccessLevel());

		int rows_affected = pr.executeUpdate();

		if (rows_affected == 1) {
		    long recordID = db.LastInsertRowID();

		    if (recordID > 0) {
			permissions.mapperSetID(recordID);
			permissions.mapperSetPersistence(true);

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

    /**
     * Updates existing (e.g. persistent) record in database
     * 
     * @param permissions
     *            Record to be updated
     * @return True if succeded, false otherwise
     */
    private boolean updateRecord(UserPermissions permissions) {
	boolean result = false;

	if (permissions != null && permissions.isPersistent()) {
	    PreparedStatement pr = null;

	    try {
		Connection conn = db.getConnection();

		pr = conn.prepareStatement("update permissions set user_id=?, "
			+ "room_id=?, jid=?, access_level=? " + "where id=?");

		pr.setLong(1, permissions.getUser().getID());
		pr.setLong(2, permissions.getRoom().getID());
		pr.setString(3, permissions.getJabberID());
		pr.setInt(4, permissions.getAccessLevel());
		pr.setLong(5, permissions.getID());

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
}
