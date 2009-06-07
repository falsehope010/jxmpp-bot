package mappers;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import database.Database;
import domain.DomainObject;
import domain.syslog.SyslogSession;

public class SyslogSessionMapper extends AbstractMapper {
	
	static final String tableName = "syslog_sessions";

	@Override
	public boolean initialize(Database db) {
		return super.initialize(db);
	}
	
	@Override
	public boolean delete(DomainObject obj) {
		boolean result = false;

		if (obj != null && obj instanceof SyslogSession) {
			SyslogSession session = (SyslogSession) obj;

			if (deleteLinkedMessages(session)) {	//delete related syslog messages
				long recordID = session.getID();

				PreparedStatement pr = null;	//delete session itself
				try {
					Connection conn = db.getConnection();

					pr = conn.prepareStatement("delete from " + tableName
							+ " where id=?;");
					pr.setLong(1, recordID);

					int rows_affected = pr.executeUpdate();

					if (rows_affected == 1) {
						session.mapperSetPersistence(false);
						session.mapperSetID(0);

						result = true;
					}
				} catch (Exception e) {
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
	 * Retrieves most recent syslog session from database. By "latest" we mean session with 
	 * most recent start date.
	 * @return 
	 */
	public SyslogSession getLatestSession(){
		SyslogSession result = null;
		
		Statement st = null;
		ResultSet rs = null;
		
		try {
			Connection conn = db.getConnection();
			st = conn.createStatement();
			
			rs = st.executeQuery("select id,max(start_date),end_date from " + tableName + ";");
			
			if (rs.next()){
				long recordID = rs.getLong(1);
				
				if (recordID > 0){
					Date startDate = rs.getDate(2);
					Date endDate = rs.getDate(3);
					
					result = new SyslogSession();
					
					result.mapperSetID(recordID);
					result.mapperSetStartDate(startDate);
					
					if (endDate != null){
						result.close();
						result.mapperSetEndDate(endDate);
					}
					
					result.mapperSetPersistence(true);
				}
			}
		} catch (Exception e) {
		}
		finally{
			db.Cleanup(st,rs);
		}
		
		return result;
	}
	
	/**
	 * Retrieves all syslog sessions from database
	 * @return ArrayList with sessions if succeded, null-reference otherwise
	 */
	public ArrayList<SyslogSession> getSessions(){
		ArrayList<SyslogSession> result = null;
		
		Statement st = null;
		ResultSet rs = null;
		
		try {
			Connection conn = db.getConnection();
			st = conn.createStatement();
			
			rs = st.executeQuery("select id,start_date,end_date from "
					+ tableName + ";");
			
			result = new ArrayList<SyslogSession>();
			
			while (rs.next()){
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
		}
		finally{
			db.Cleanup(st, rs);
		}
		
		return result;
	}
	
	/**
	 * Inserts new (e.g. non-persistent) session into database. If successful marks 
	 * session as persistent and assigns to it valid ID  
	 * @param session SyslogSession object which will be inserted into database
	 * @return true if succeeded, false otherwise
	 */
	protected boolean insertSession(SyslogSession session){
		boolean result =  false;
		
		if (session != null && !session.isPersistent())
		{
			
			PreparedStatement pr = null;
			
			try {
				Connection conn = db.getConnection();
				pr = conn.prepareStatement("insert into " + tableName
						+ "(start_date,end_date) values(?,?);");

				Date startDate = Convert(session.getStartDate());
				Date endDate = null;
				
				if(session.isClosed()){
					endDate = Convert(session.getEndDate());
				}
				
				pr.setDate(1, startDate);
				pr.setDate(2, endDate);
				
				int rows_affected = pr.executeUpdate();
				
				if (rows_affected == 1){
					
					long rowID = db.LastInsertRowID();
					
					if (rowID > 0){
						session.mapperSetPersistence(true);
						session.mapperSetID(rowID);
						result = true;
					}
				}
				
			} catch (Exception e) {
				System.out.print(e.getMessage());
			}
			finally{
				db.Cleanup(pr);
			}
		}

		return result;
	}
	
	/**
	 * Updates session in database. Session must be persistent. 
	 * @param session SyslogSession which will be updated
	 * @return true if succeeded, false otherwise
	 */
	protected boolean updateSession(SyslogSession session){
		boolean result = false;
		
		if ( session != null && session.isPersistent()){
			PreparedStatement pr = null;
			
			try {
				Connection conn = db.getConnection();
				
				pr = conn.prepareStatement("update " + tableName
						+ " set start_date=?, end_date=? where id=?;");
				Date startDate = Convert(session.getStartDate());
				Date endDate = null;
				if (session.isClosed()){
					endDate = Convert(session.getEndDate());
				}
				
				pr.setDate(1, startDate);
				pr.setDate(2, endDate);
				pr.setLong(3, session.getID());
				
				int rows_affected = pr.executeUpdate();
				
				if (rows_affected == 1){
					result = true;
				}
				
			} catch (Exception e) {
			}
			finally{
				db.Cleanup(pr);
			}
		}
		
		return result;
	}

	/**
	 * Deletes all messages from syslog db table. Each message relates to session
	 * by sessionID. 
	 * @param session Valid syslog session. Must be persistent
	 * @return true if succeded, false otherwise
	 */
	protected boolean deleteLinkedMessages(SyslogSession session){
		boolean result = false;
		
		if ( session != null && session.isPersistent()){
			PreparedStatement pr = null;
			
			try {
				Connection conn = db.getConnection();
				
				pr = conn.prepareStatement("delete from syslog where session_id=?;");
				pr.setLong(1, session.getID());
				
				pr.executeUpdate();
				
				result = true;
			} catch (Exception e) {
			}
			finally{
				db.Cleanup(pr);
			}
		}
		return result;
	}
	
	private java.sql.Date Convert(java.util.Date date){
		java.sql.Date result = null;
		
		if ( date != null){
			result = new Date(date.getTime());
		}
		
		return result;
	}
}
