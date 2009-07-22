package mappers;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import utils.DateConverter;
import database.Database;
import domain.DomainObject;
import domain.syslog.SyslogSession;
import exceptions.DatabaseNotConnectedException;

public class SyslogSessionMapper extends AbstractMapper {

    /**
     * Creates new instance of mapper using given database.
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
    public SyslogSessionMapper(Database db) throws NullPointerException,
	    DatabaseNotConnectedException {
	super(db);

	if (cache == null) {
	    loadSessions();
	}
    }

    /**
     * Gets syslog session by it's id.
     * 
     * @param sessionID
     *            Session id
     * @return Valid, persistent syslog session if succeded, null-reference
     *         otherwise
     */
    public static SyslogSession getByID(long sessionID) {
	SyslogSession result = null;

	if (cache.containsKey(sessionID)) {
	    result = cache.get(sessionID);
	}

	return result;
    }

    @Override
    public boolean delete(DomainObject obj) {
	boolean result = false;

	if (obj != null && obj instanceof SyslogSession) {
	    SyslogSession session = (SyslogSession) obj;

	    if (deleteLinkedMessages(session)) { // delete related syslog
		// messages
		long recordID = session.getID();

		PreparedStatement pr = null; // delete session itself
		try {
		    Connection conn = db.getConnection();

		    pr = conn.prepareStatement("delete from " + tableName
			    + " where id=?;");
		    pr.setLong(1, recordID);

		    int rows_affected = pr.executeUpdate();

		    if (rows_affected == 1) {
			session.mapperSetPersistence(false);
			session.mapperSetID(0);

			uncacheSession(session);

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
     * 
     * <p>
     * This implementation performs mapping of {@link SyslogSession} into
     * database.
     * 
     * <p>
     * Parameter passed to this method is considered to be an instance of
     * {@link SyslogSession}. If this isn't so method does nothing and returns
     * false
     * 
     * @param obj
     *            Instance of {@link SyslogSession} (not null)
     */
    @Override
    public boolean save(DomainObject obj) {
	boolean result = false;

	if (obj != null && obj instanceof SyslogSession) {
	    SyslogSession session = (SyslogSession) obj;

	    if (session.isPersistent()) {
		result = updateSession(session);
	    } else {
		result = insertSession(session);
	    }
	}

	return result;
    }

    /**
     * Retrieves most recent syslog session from database. By "latest" we mean
     * session with most recent start date.
     * 
     * @return
     */
    public SyslogSession getLatestSession() {
	SyslogSession result = null;

	Statement st = null;
	ResultSet rs = null;

	try {
	    Connection conn = db.getConnection();
	    st = conn.createStatement();

	    rs = st.executeQuery("select id,max(start_date),end_date from "
		    + tableName + ";");

	    if (rs.next()) {
		Long recordID = rs.getLong(1);

		if (recordID > 0) {
		    if (cache.containsKey(recordID)) {
			result = cache.get(recordID);
		    } else {
			Date startDate = rs.getDate(2);
			Date endDate = rs.getDate(3);

			result = new SyslogSession();

			result.mapperSetID(recordID);
			result.mapperSetStartDate(startDate);

			if (endDate != null) {
			    result.close();
			    result.mapperSetEndDate(endDate);
			}

			result.mapperSetPersistence(true);

			cacheSession(result);
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
     * Retrieves all syslog sessions from database
     * 
     * @return ArrayList with sessions if succeded, null-reference otherwise
     */
    public ArrayList<SyslogSession> getSessions() {
	ArrayList<SyslogSession> result = null;

	Statement st = null;
	ResultSet rs = null;

	try {
	    Connection conn = db.getConnection();
	    st = conn.createStatement();

	    rs = st.executeQuery("select id,start_date,end_date from "
		    + tableName + ";");

	    result = new ArrayList<SyslogSession>();

	    while (rs.next()) {
		long recordID = rs.getLong(1);
		Date startDate = rs.getDate(2);
		Date endDate = rs.getDate(3);

		SyslogSession session = new SyslogSession();
		session.mapperSetID(recordID);
		session.mapperSetStartDate(startDate);
		session.mapperSetEndDate(endDate);
		session.mapperSetPersistence(true);

		result.add(session);
	    }

	} catch (Exception e) {
	    result = null;
	} finally {
	    db.Cleanup(st, rs);
	}

	return result;
    }

    /**
     * Inserts new (e.g. non-persistent) session into database. If successful
     * marks session as persistent and assigns to it valid ID
     * 
     * @param session
     *            SyslogSession object which will be inserted into database
     * @return true if succeeded, false otherwise
     */
    protected boolean insertSession(SyslogSession session) {
	boolean result = false;

	if (session != null && !session.isPersistent()) {

	    PreparedStatement pr = null;

	    try {
		Connection conn = db.getConnection();
		pr = conn.prepareStatement("insert into " + tableName
			+ "(start_date,end_date) values(?,?);");

		Date startDate = DateConverter.Convert(session.getStartDate());
		Date endDate = null;

		if (session.isClosed()) {
		    endDate = DateConverter.Convert(session.getEndDate());
		}

		pr.setDate(1, startDate);
		pr.setDate(2, endDate);

		int rows_affected = pr.executeUpdate();

		if (rows_affected == 1) {

		    long rowID = db.LastInsertRowID();

		    if (rowID > 0) {
			session.mapperSetPersistence(true);
			session.mapperSetID(rowID);

			cacheSession(session);

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
     * Updates session in database. Session must be persistent.
     * 
     * @param session
     *            SyslogSession which will be updated
     * @return true if succeeded, false otherwise
     */
    protected boolean updateSession(SyslogSession session) {
	boolean result = false;

	if (session != null && session.isPersistent()) {
	    PreparedStatement pr = null;

	    try {
		Connection conn = db.getConnection();

		pr = conn.prepareStatement("update " + tableName
			+ " set start_date=?, end_date=? where id=?;");
		Date startDate = DateConverter.Convert(session.getStartDate());
		Date endDate = null;
		if (session.isClosed()) {
		    endDate = DateConverter.Convert(session.getEndDate());
		}

		pr.setDate(1, startDate);
		pr.setDate(2, endDate);
		pr.setLong(3, session.getID());

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
     * Deletes all messages from syslog db table. Each message relates to
     * session by sessionID.
     * 
     * @param session
     *            Valid syslog session. Must be persistent
     * @return true if succeded, false otherwise
     */
    private boolean deleteLinkedMessages(SyslogSession session) {
	boolean result = false;

	if (session != null && session.isPersistent()) {
	    PreparedStatement pr = null;

	    try {
		Connection conn = db.getConnection();

		pr = conn
			.prepareStatement("delete from syslog where session_id=?;");
		pr.setLong(1, session.getID());

		pr.executeUpdate();

		result = true;
	    } catch (Exception e) {
		e.printStackTrace();
	    } finally {
		db.Cleanup(pr);
	    }
	}
	return result;
    }

    private void loadSessions() {
	cache = new HashMap<Long, SyslogSession>();

	Statement st = null;
	ResultSet rs = null;

	try {
	    Connection conn = db.getConnection();
	    st = conn.createStatement();

	    rs = st
		    .executeQuery("select id, start_date, end_date from syslog_sessions;");

	    while (rs.next()) {
		long sessionID = rs.getLong(1);

		if (sessionID > 0) {
		    Date startDate = rs.getDate(2);
		    Date endDate = rs.getDate(3);

		    SyslogSession session = new SyslogSession();
		    session.mapperSetID(sessionID);
		    session
			    .mapperSetStartDate(DateConverter
				    .Convert(startDate));

		    if (endDate != null) {
			session.close();
			session
				.mapperSetEndDate(DateConverter
					.Convert(endDate));
		    }

		    session.mapperSetPersistence(true);

		    cacheSession(session);
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    db.Cleanup(st, rs);
	}
    }

    private static void cacheSession(SyslogSession session) {
	if (session != null && session.isPersistent()) {
	    long sessionID = session.getID();

	    if (!cache.containsKey(sessionID)) {
		cache.put(sessionID, session);
	    }
	}
    }

    private static void uncacheSession(SyslogSession session) {
	if (session != null && session.isPersistent()) {
	    Long sessionID = session.getID();
	    if (cache.containsKey(sessionID))
		cache.remove(sessionID);
	}
    }

    static final String tableName = "syslog_sessions";

    static HashMap<Long, SyslogSession> cache = null;
}
