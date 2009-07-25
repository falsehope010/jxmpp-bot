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

    /**
     * {@inheritDoc}
     * <p>
     * This implementation deletes {@link Room} from database. No business logic
     * is performed.
     * 
     * <p>
     * Parameter passed to this method is considered to be an instance of
     * {@link Room}. If it isn't so or record is not persistent method does
     * nothing and returns false.
     */
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

    /**
     * {@inheritDoc}
     * <p>
     * This implementation performs mapping of {@link Room} into database.
     * <p>
     * If record isn't persistent it is simply inserted into corresponding
     * database table. If record is persistent it is updated. No additional
     * business logic is performed.
     * <p>
     * Parameter passed to this method is considered to be an instance of
     * {@link Room}. If this isn't so method does nothing and returns false
     * 
     * @param obj
     *            {@link Room} instance to be mapped into database
     * 
     */
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

    /**
     * Inserts room into database.
     * 
     * @param room
     *            Room to be inserted
     * @return True if succeeded, false otherwise
     */
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

    /**
     * Updates existing (e.g. persistent) room in database
     * 
     * @param room
     *            Room to be updated
     * @return True if succeded, false otherwise
     */
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
