package mappers;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;

import database.Database;
import domain.DomainObject;
import domain.syslog.SyslogSession;

public class SyslogSessionMapper extends AbstractMapper {

	@Override
	public boolean Initialize(Database db) {
		return super.Initialize(db);
	}
	
	@Override
	public boolean Delete(DomainObject obj) {
		boolean result = false;

		if (obj != null && obj instanceof SyslogSession){
			SyslogSession session = (SyslogSession)obj;
			
			long recordID = session.getID();
			
			PreparedStatement pr = null;
			try {
				Connection conn = db.getConnection();
				
				pr = conn.prepareStatement("delete from syslog_sessions where id=?;");
				pr.setLong(1, recordID);
				
				int rows_affected = pr.executeUpdate();
				
				if (rows_affected == 1){
					session.mapperSetPersistence(false);
					session.mapperSetID(0);
					
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

	@Override
	public boolean Save(DomainObject obj) {
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
				pr = conn.prepareStatement("insert into syslog_sessions(start_date,end_date) values(?,?);" );

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
				
				pr = conn.prepareStatement(
						"update syslog_sessions set start_date=?, end_date=? where id=?;"
						);
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

	private java.sql.Date Convert(java.util.Date date){
		java.sql.Date result = null;
		
		if ( date != null){
			result = new Date(date.getTime());
		}
		
		return result;
	}
}
