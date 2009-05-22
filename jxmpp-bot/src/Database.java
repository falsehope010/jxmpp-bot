import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.io.*;

public class Database {

	String fileName;
	Connection conn;

	public Database(String fileName) throws NullPointerException,
			FileNotFoundException {

		if (fileName == null)
			throw new NullPointerException("fileName can't be null");

		File f = new File(fileName);

		if (!f.exists())
			throw new FileNotFoundException("Database file not found");

		this.fileName = fileName;
	}

	public boolean Connect() {
		boolean result = false;

		conn = getConnection();

		if (conn != null ) {
		}

		return result;
	}

	public void Disconnect() {
		try {
			conn.close();
		} catch (Exception e) {
		}
	}

	public boolean InsertUser(String realName, String JID,
			AccessLevel accessLevel) {
		boolean result = false;

		PreparedStatement prep = null;
		Statement id_fetch_st = null;
		ResultSet rs = null;

		try {
			prep = conn
					.prepareStatement("insert into users(real_name,access_level) values (?,?);");

			prep.setString(1, realName);
			prep.setInt(2, accessLevel.getValue());

			prep.execute();

			// extract id for inserted user
			id_fetch_st = conn.createStatement();
			rs = id_fetch_st.executeQuery("select last_insert_rowid()");

			if (rs.next()) {
				long usrID = rs.getLong(1);

				rs.close();

				if (usrID > 0) {
					Cleanup(prep); // clean up after previous insert

					prep = conn
							.prepareStatement("insert into jids(id_user,jid) values(?,?);");

					prep.setLong(1, usrID);
					prep.setString(2, JID);

					prep.execute();

					result = true;
				}
			}
		} catch (Exception e) {
		} finally {
			Cleanup(prep, rs);
			Cleanup(id_fetch_st);
		}

		return result;
	}

	public boolean InsertUserJid(long UserID, String JID) {
		boolean result = false;

		PreparedStatement prep = null;
		Statement stat = null;
		ResultSet rs = null;

		try {
			// check whether user with given JID exists

			prep = conn
					.prepareStatement("select count(1) from users where id=?;");
			prep.setLong(1, UserID);

			rs = prep.executeQuery();

			if (rs.next()) {
				long count = rs.getLong(1);

				boolean user_exists = count > 0;

				if (user_exists) {

					Cleanup(prep);

					prep = conn
							.prepareStatement("insert into jids(id_user,jid) values(?,?);");
					prep.setLong(1, UserID);
					prep.setString(2, JID);

					prep.execute();

					result = true;
				}
			}
		} catch (Exception e) {
		} finally {
			Cleanup(prep, rs);
			Cleanup(stat);
		}

		return result;
	}

	public long LastInsertRowID() {
		long result = 0;

		Statement stat = null;
		ResultSet rs = null;

		try {
			stat = conn.createStatement();
			rs = stat.executeQuery("select last_insert_rowid()");

			rs.next();
			result = rs.getLong(1);

		} catch (Exception e) {
		} finally {
			
			Cleanup(stat, rs);
		}

		return result;
	}

	protected Connection getConnection() {
		Connection result = null;

		try {
			Class.forName("org.sqlite.JDBC");
			result = DriverManager.getConnection("jdbc:sqlite:" + fileName);
		} catch (Exception e) {
			result = null;
		}

		return result;
	}

	protected void Cleanup(Statement stat, ResultSet rs) {
		if (stat != null) {
			try {
				stat.close();
			} catch (Exception e) {
			}
		}
		if (rs != null) {
			try {
				rs.close();
			} catch (Exception e) {
			}
		}
	}

	protected void Cleanup(Statement stat) {
		if (stat != null) {
			try {
				stat.close();
			} catch (Exception e) {
			}
		}
	}
}
