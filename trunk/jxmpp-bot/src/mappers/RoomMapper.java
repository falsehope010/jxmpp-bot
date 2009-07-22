package mappers;

import java.sql.Connection;
import java.sql.PreparedStatement;

import database.Database;
import domain.DomainObject;
import domain.muc.Room;
import exceptions.DatabaseNotConnectedException;

public class RoomMapper extends AbstractMapper {

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
    public RoomMapper(Database db) throws DatabaseNotConnectedException,
	    NullPointerException {
	super(db);
    }

    @Override
    public boolean delete(DomainObject obj) {
	boolean result = false;

	if (obj != null && obj instanceof Room) {
	    Room room = (Room) obj;

	    if (room.isPersistent()) {
		PreparedStatement pr = null;

		try {
		    Connection conn = db.getConnection();

		    pr = conn.prepareStatement("delete from rooms where id=?");

		    pr.setLong(1, room.getID());

		    int rows_affected = pr.executeUpdate();

		    if (rows_affected == 1) {
			room.mapperSetID(0);
			room.mapperSetPersistence(false);

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

    @Override
    public boolean save(DomainObject obj) {
	boolean result = false;

	if (obj != null && obj instanceof Room) {
	    Room room = (Room) obj;

	    if (!room.isPersistent()) {
		result = insertRoom(room);
	    } else {
		result = updateRoom(room);
	    }
	}

	return result;
    }

    private boolean insertRoom(Room room) {
	boolean result = false;

	if (room != null && !room.isPersistent()) {
	    PreparedStatement pr = null;

	    try {
		Connection conn = db.getConnection();

		pr = conn
			.prepareStatement("insert into rooms(name,description) "
				+ "values(?,?)");

		pr.setString(1, room.getName());
		pr.setString(2, room.getDescription());

		int rows_affected = pr.executeUpdate();

		if (rows_affected == 1) {
		    long recordID = db.LastInsertRowID();

		    if (recordID > 0) {
			room.mapperSetID(recordID);
			room.mapperSetPersistence(true);

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

    private boolean updateRoom(Room room) {
	boolean result = false;

	if (room != null && room.isPersistent()) {
	    PreparedStatement pr = null;

	    try {
		Connection conn = db.getConnection();

		pr = conn
			.prepareStatement("update rooms set name=?,description=? where id=?");

		pr.setString(1, room.getName());
		pr.setString(2, room.getDescription());
		pr.setLong(3, room.getID());

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
