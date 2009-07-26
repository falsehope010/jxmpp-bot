package mappers;

import java.sql.Connection;
import java.sql.PreparedStatement;

import utils.DateConverter;
import database.Database;
import domain.DomainObject;
import domain.muc.Visit;
import exceptions.DatabaseNotConnectedException;

public class VisitMapper extends AbstractMapper {

    public VisitMapper(Database db) throws DatabaseNotConnectedException,
	    NullPointerException {
	super(db);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation deletes {@link Visit} from database. No business
     * logic is performed.
     * 
     * <p>
     * Parameter passed to this method is considered to be an instance of
     * {@link Visit}. If it isn't so or record is not persistent method does
     * nothing and returns false.
     */

    @Override
    public boolean delete(DomainObject obj) {
	boolean result = false;

	if (obj != null && obj instanceof Visit) {
	    Visit visit = (Visit) obj;

	    if (visit.isPersistent()) {
		PreparedStatement pr = null;

		try {
		    Connection conn = db.getConnection();

		    pr = conn.prepareStatement("delete from visits where id=?");

		    pr.setLong(1, visit.getID());

		    int rows_affected = pr.executeUpdate();

		    if (rows_affected == 1) {
			visit.mapperSetID(0);
			visit.mapperSetPersistence(true);

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
     * This implementation performs mapping of {@link Visit} into database.
     * <p>
     * If record isn't persistent it is simply inserted into corresponding
     * database table. If record is persistent it is updated. No additional
     * business logic is performed.
     * <p>
     * Parameter passed to this method is considered to be an instance of
     * {@link Visit}. If this isn't so method does nothing and returns false
     * 
     * @param obj
     *            {@link Visit} instance to be mapped into database
     * 
     */
    @Override
    public boolean save(DomainObject obj) {
	boolean result = false;

	if (obj != null && obj instanceof Visit) {
	    Visit visit = (Visit) obj;

	    if (!visit.isPersistent()) {
		result = insertVisit(visit);
	    } else {
		result = updateVisit(visit);
	    }
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
    private boolean insertVisit(Visit visit) {
	boolean result = false;

	if (visit != null && !visit.isPersistent()) {
	    PreparedStatement pr = null;

	    try {
		Connection conn = db.getConnection();

		pr = conn
			.prepareStatement("insert into visits(start_date,end_date,permission_id) "
				+ "values(?,?,?)");
		pr.setDate(1, DateConverter.Convert(visit.getStartDate()));
		pr.setDate(2, DateConverter.Convert(visit.getEndDate()));
		pr.setLong(3, visit.getPermissions().getID());

		int rows_affected = pr.executeUpdate();

		if (rows_affected == 1) {
		    long recordID = db.LastInsertRowID();

		    if (recordID > 0) {
			visit.mapperSetID(recordID);
			visit.mapperSetPersistence(true);

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
    private boolean updateVisit(Visit visit) {
	boolean result = false;

	if (visit != null && visit.isPersistent()) {
	    PreparedStatement pr = null;

	    try {
		Connection conn = db.getConnection();

		pr = conn.prepareStatement("update visits set start_date=?,"
			+ "end_date=?,permission_id=? where id=?");
		pr.setDate(1, DateConverter.Convert(visit.getStartDate()));
		pr.setDate(2, DateConverter.Convert(visit.getEndDate()));
		pr.setLong(3, visit.getPermissions().getID());
		pr.setLong(4, visit.getID());

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
