package mappers;

import java.sql.Connection;
import java.sql.PreparedStatement;

import utils.DateConverter;
import database.Database;
import domain.DomainObject;
import domain.muc.ChatMessage;
import exceptions.DatabaseNotConnectedException;

public class ChatMessageMapper extends AbstractMapper {

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
    public ChatMessageMapper(Database db) throws DatabaseNotConnectedException,
	    NullPointerException {
	super(db);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation deletes {@link ChatMessage} from database. No
     * business logic is performed.
     * 
     * <p>
     * Parameter passed to this method is considered to be an instance of
     * {@link ChatMessage}. If it isn't so or record is not persistent method
     * does nothing and returns false.
     */

    @Override
    public boolean delete(DomainObject obj) {
	boolean result = false;

	if (obj != null && obj instanceof ChatMessage) {
	    ChatMessage message = (ChatMessage) obj;

	    if (message.isPersistent()) {
		PreparedStatement pr = null;

		try {
		    Connection conn = db.getConnection();

		    pr = conn
			    .prepareStatement("delete from chat_messages where id=?");

		    pr.setLong(1, message.getID());

		    int rows_affected = pr.executeUpdate();

		    if (rows_affected == 1) {
			message.mapperSetID(0);
			message.mapperSetPersistence(false);

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
     * This implementation performs mapping of {@link ChatMessage} into
     * database.
     * <p>
     * If record isn't persistent it is simply inserted into corresponding
     * database table. If record is persistent it is updated. No additional
     * business logic is performed.
     * <p>
     * Parameter passed to this method is considered to be an instance of
     * {@link ChatMessage}. If this isn't so method does nothing and returns
     * false
     * 
     * @param obj
     *            {@link ChatMessage} instance to be mapped into database
     * 
     */
    @Override
    public boolean save(DomainObject obj) {
	boolean result = false;

	if (obj != null && obj instanceof ChatMessage) {
	    ChatMessage message = (ChatMessage) obj;

	    if (!message.isPersistent()) {
		result = insetMessage(message);
	    } else {
		result = updateMessage(message);
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
    private boolean insetMessage(ChatMessage message) {
	boolean result = false;

	if (message != null && !message.isPersistent()) {
	    PreparedStatement pr = null;

	    try {
		Connection conn = db.getConnection();

		pr = conn
			.prepareStatement("inser into chat_messages(timestamp,text,sender,recipient) "
				+ "values(?,?,?,?)");
		pr.setDate(1, DateConverter.Convert(message.getTimestamp()));
		pr.setString(2, message.getText());
		pr.setLong(3, message.getSender().getID());
		pr.setLong(4, message.getRecipient().getID());

		int rows_affected = pr.executeUpdate();

		if (rows_affected == 1) {
		    long recordID = db.LastInsertRowID();

		    if (recordID > 0) {
			message.mapperSetID(recordID);
			message.mapperSetPersistence(true);

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
     * @return True if succeed, false otherwise
     */
    private boolean updateMessage(ChatMessage message) {
	boolean result = false;

	if (message != null && message.isPersistent()) {
	    PreparedStatement pr = null;

	    try {
		Connection conn = db.getConnection();

		pr = conn.prepareStatement("update chat_messages set "
			+ "timestamp=?,text=?,sender=?,recipient=? "
			+ "where id=?");
		pr.setDate(1, DateConverter.Convert(message.getTimestamp()));
		pr.setString(2, message.getText());
		pr.setLong(3, message.getSender().getID());
		pr.setLong(4, message.getRecipient().getID());
		pr.setLong(5, message.getID());

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
