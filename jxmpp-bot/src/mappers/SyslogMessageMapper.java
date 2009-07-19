package mappers;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import utils.DateConverter;
import database.Database;
import domain.DomainObject;
import domain.syslog.Message;
import domain.syslog.MessageCategory;
import domain.syslog.MessageSender;
import domain.syslog.MessageType;
import domain.syslog.SyslogSession;
import exceptions.DatabaseNotConnectedException;

/**
 * @author tillias_work
 * 
 */
public class SyslogMessageMapper extends AbstractMapper {

    /**
     * Creates new instance of mapper using given database.
     * 
     * @param db
     *            Database which will be used by mapper.
     * @throws NullPointerException
     *             Thrown if database is null-reference
     * @throws DatabaseNotConnectedException
     *             Thrown if database is in disconnected state. You must call
     *             {@link Database#connect()} before passing database into
     *             mapper's constructor
     */
    public SyslogMessageMapper(Database db) throws NullPointerException,
	    DatabaseNotConnectedException {
	super(db);

	if (categories_cache == null)
	    LoadCategories();
	if (types_cache == null)
	    LoadTypes();
	if (senders_cache == null)
	    LoadSenders();
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * This implementation performs mapping of syslog {@link Message} into
     * database. Currently supports only insertion of new messages, but no
     * updates.
     * 
     * <p>
     * Parameter passed to this method is considered to be an instance of
     * {@link Message}. If this isn't so method does nothing and returns false
     * 
     * @param obj
     *            Instance of {@link Message} (not-null)
     */
    @Override
    public boolean save(DomainObject obj) {
	boolean result = false;

	if (!obj.isPersistent()) {
	    result = insertMessage(obj);
	} else {
	    /*
	     * Current architecture doesn't allow to update syslog messages
	     */
	}
	return result;
    }

    /**
     * Saves collection of syslog messages. Before saving disables auto-commit
     * mode for database due-to performance reasons and restores this mode to
     * previous state after manual commit()
     * 
     * @param messages
     *            Collection of syslog messages to be saved into database
     * @return true if succeded, false otherwise
     */
    public boolean save(Collection<Message> messages) {
	boolean result = false;

	if (messages != null) {
	    if (messages.size() > 0) {

		// remember current auto-commit mode
		boolean currentMode = getAutoCommitState();

		// disable auto-commit
		boolean success = true;
		if (currentMode) {
		    success = db.setAutoCommit(false);
		}

		boolean insertedAll = true;
		if (success) {
		    // save all syslog messages into database
		    for (Message m : messages) {
			if (!save(m)) {
			    insertedAll = false;
			    break;
			}
		    }

		    if (insertedAll) {
			// restore auto-commit state
			result = db.setAutoCommit(currentMode);
		    }
		}
	    } else {
		result = true; // nothing to save
	    }
	}

	return result;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation considers that parameter passed to method is instance
     * of {@link Message}
     */
    @Override
    public boolean delete(DomainObject obj) {
	boolean result = false;

	if (obj != null && obj instanceof Message) {
	    Message msg = (Message) obj;

	    if (msg.isPersistent()) {
		PreparedStatement pr = null;
		try {
		    Connection conn = db.getConnection();
		    pr = conn
			    .prepareStatement("delete from syslog where id=?;");
		    pr.setLong(1, msg.getID());

		    int rows_affected = pr.executeUpdate();

		    if (rows_affected == 1) {
			msg.mapperSetID(0);
			msg.mapperSetPersistence(false);

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
     * Leaves only given number of latest syslog messages in database and
     * deletes all others.
     * 
     * @param count
     *            Total number of latest syslog messages that should not be
     *            deleted from database
     * @return true if succeded, false otherwise
     */
    public boolean deleteBelow(long count) {
	boolean result = false;

	try {
	    PreparedStatement pr = null;

	    try {
		Connection conn = db.getConnection();
		pr = conn.prepareStatement("delete from syslog where id < "
			+ "(select min(id) from (select id "
			+ "from syslog order by id desc limit ?))");
		pr.setLong(1, count);

		pr.executeUpdate();

		result = true;
	    } catch (Exception e) {
		e.printStackTrace();
	    } finally {
		db.Cleanup(pr);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}

	return result;
    }

    /**
     * Deletes all syslog messages from database with date older then given one
     * 
     * @param timestamp
     *            Date border
     * @return True if succeeded, false otherwise
     */
    public boolean deleteOlder(java.util.Date timestamp) {
	boolean result = false;

	PreparedStatement pr = null;
	try {
	    Connection conn = db.getConnection();
	    pr = conn
		    .prepareStatement("delete from syslog where timestamp < ?;");
	    pr.setDate(1, DateConverter.Convert(timestamp));

	    pr.executeUpdate();

	    result = true;
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    db.Cleanup(pr);
	}

	return result;
    }

    /**
     * Retrieves all syslog messages from database. No caching is performed
     * 
     * @return All syslog messages from database. If any error occurs returns
     *         empty list
     */
    public List<Message> getMessages() {
	List<Message> result = null;

	/*
	 * select a.id, a.timestamp, a.text, a.session_id, b.start_date,
	 * b.end_date, a.category_id, c.name, a.type_id, d.name, a.sender_id,
	 * e.name from syslog as a, syslog_sessions as b, syslog_categories as
	 * c, syslog_types as d, syslog_senders as e where a.session_id = b.id
	 * and a.category_id = c.id and a.type_id = d.id and a.sender_id = e.id
	 */
	StringBuilder sb = new StringBuilder();
	sb.append("select a.id, a.timestamp, a.text, ");
	sb.append("a.session_id, ");
	sb.append("c.name, ");
	sb.append("d.name, ");
	sb.append("e.name ");
	sb
		.append("from syslog as a, syslog_sessions as b, syslog_categories as c,");
	sb.append("syslog_types as d, syslog_senders as e ");
	sb.append("where a.session_id = b.id and a.category_id = c.id ");
	sb.append("and a.type_id = d.id and a.sender_id = e.id;");

	String sql = sb.toString();

	result = executeGetQuery(sql);

	return result;
    }

    /**
     * Retrieves syslog messages from database using given search settings.
     * <p>
     * NOTE: This is temporary method until we implement query object pattern
     * 
     * @param settings
     * @return
     * @see SearchSettings
     */
    public List<Message> getMessages(SearchSettings settings) {
	List<Message> result = new ArrayList<Message>();

	if (settings != null) {
	    try {
		StringBuilder sb = new StringBuilder();
		sb.append("select a.id, a.timestamp, a.text, ");
		sb.append("a.session_id, ");
		sb.append("c.name, ");
		sb.append("d.name, ");
		sb.append("e.name ");
		sb
			.append("from syslog as a, syslog_sessions as b, syslog_categories as c,");
		sb.append("syslog_types as d, syslog_senders as e ");
		sb
			.append("where a.session_id = b.id and a.category_id = c.id ");
		sb.append("and a.type_id = d.id and a.sender_id = e.id ");

		sb.append(" and ");

		boolean hasTextFilter = false, hasStartDate = false, hasEndDate = false;

		// process text filtering if needed
		String textFilter = settings.getTextPattern();

		if (textFilter != null) {
		    sb.append("a.text like '%");
		    sb.append(textFilter);
		    sb.append("%'");

		    hasTextFilter = true;
		}

		// process start date if needed
		java.util.Date startDate = settings.getStartDate();

		if (startDate != null) {
		    if (hasTextFilter) {
			sb.append(" AND ");
		    }

		    sb.append("timestamp >= ");
		    sb.append(startDate.getTime());
		    sb.append(' ');

		    hasStartDate = true;
		}

		// process end date if needed
		java.util.Date endDate = settings.getEndDate();

		if (endDate != null) {
		    if (hasTextFilter || hasStartDate) {
			sb.append(" AND ");
		    }

		    sb.append("timestamp <= ");
		    sb.append(endDate.getTime());
		    sb.append(' ');

		    hasEndDate = true;
		}

		// process categories if needed
		boolean hasCategories = false;

		List<String> categoryNames = settings.getCategories();

		if (categoryNames != null && categoryNames.size() > 0) {
		    if (hasTextFilter || hasStartDate || hasEndDate) {
			sb.append(" AND ");
		    }

		    /*
		     * Retrieve categories from internal static cache using
		     * their name. After that we produce such a string:
		     * 'a.catrgory_id in (id1, id2, id3, ... idn)' and append it
		     * to our sqlQuery builder
		     */
		    List<Long> ids = getCategoriesID(categoryNames);
		    if (ids.size() > 0) {
			sb.append(" a.category_id in (");
			for (Long id : ids) {
			    sb.append(id);
			    sb.append(',');
			}
			sb.append("-1)");
		    }

		    hasCategories = true;
		}

		boolean hasTypes = false;

		List<String> typeNames = settings.getTypes();

		if (typeNames != null && typeNames.size() > 0) {
		    if (hasTextFilter || hasStartDate || hasEndDate
			    || hasCategories) {
			sb.append(" AND ");
		    }

		    /*
		     * Retrieve types from internal static cache using their
		     * name. After that we produce such a string: 'a.type_id in
		     * (id1, id2, id3, ... idn)' and append it to our sqlQuery
		     * builder
		     */
		    List<Long> ids = getTypesID(typeNames);

		    if (ids.size() > 0) {
			sb.append(" a.type_id in (");
			for (Long id : ids) {
			    sb.append(id);
			    sb.append(',');
			}
			sb.append("-1)");
		    }

		    hasTypes = true;
		}

		boolean hasSenders = false;

		List<String> senderNames = settings.getSenders();

		if (senderNames != null && senderNames.size() > 0) {
		    if (hasTextFilter || hasStartDate || hasEndDate
			    || hasCategories || hasTypes) {
			sb.append(" AND ");
		    }

		    /*
		     * Retrieve types from internal static cache using their
		     * name. After that we produce such a string: 'a.type_id in
		     * (id1, id2, id3, ... idn)' and append it to our sqlQuery
		     * builder
		     */
		    List<Long> ids = getSendersID(senderNames);

		    if (ids.size() > 0) {
			sb.append(" a.sender_id in (");
			for (Long id : ids) {
			    sb.append(id);
			    sb.append(',');
			}
			sb.append("-1)");
		    }

		    hasSenders = true;
		}

		List<SyslogSession> sessions = settings.getSessions();

		if (sessions != null && sessions.size() > 0) {
		    if (hasTextFilter || hasStartDate || hasEndDate
			    || hasCategories || hasTypes || hasSenders) {
			sb.append(" AND ");

			sb.append(" a.session_id in (");
			for (SyslogSession session : sessions) {
			    sb.append(session.getID());
			    sb.append(',');
			}
			sb.append("-1)");
		    }
		}

		// TODO: remove debug print
		System.out.println(sb);

		String sql = sb.toString();

		result = executeGetQuery(sql);
	    } catch (Exception e) {
		// nothing to do
	    }
	}
	return result;
    }

    /**
     * Gets total number of syslog messages which are stored in database
     * 
     * @return Number of messages in database
     */
    public long getPersistentMessagesCount() {
	return db.countRecords("syslog");
    }

    private boolean insertMessage(DomainObject obj) {
	boolean result = false;

	if (obj != null && obj instanceof Message) {
	    Message msg = (Message) obj;
	    if (mapAttributes(msg)) {
		PreparedStatement pr = null;
		try {
		    Connection conn = db.getConnection();

		    String sql = "insert into syslog(timestamp,text,session_id,category_id,type_id,sender_id)"
			    + " values(?,?,?,?,?,?);";

		    pr = conn.prepareStatement(sql);

		    pr.setDate(1, DateConverter.Convert(msg.getTimestamp()));
		    pr.setString(2, msg.getText());
		    pr.setLong(3, msg.getSession().getID());
		    pr.setLong(4, msg.getCategory().getID());
		    pr.setLong(5, msg.getMessageType().getID());
		    pr.setLong(6, msg.getSender().getID());

		    int rows_affected = pr.executeUpdate();

		    if (rows_affected == 1) {
			long recordID = db.LastInsertRowID();

			if (recordID > 0) {
			    msg.mapperSetID(recordID);
			    msg.mapperSetPersistence(true);
			    result = true;
			}
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
     * Maps message attributes (category,sender,type) so they become persistent
     * 
     * @param msg
     *            Message which attributes should be mapped
     * @return true if succeeded, false otherwise
     */
    private boolean mapAttributes(Message msg) {
	boolean result = false;
	if (msg != null) {
	    if (mapCategory(msg) && mapType(msg) && mapSender(msg)
		    && mapSession(msg)) {
		result = true;
	    }
	}
	return result;
    }

    /**
     * Maps given category to it's database representation
     * 
     * @param msg
     *            Syslog message
     * @return true if succeeded, false otherwise
     */
    private boolean mapCategory(Message msg) {
	boolean result = false;

	if (msg != null && msg.getCategory() != null) {
	    // get current message category
	    MessageCategory msgCategory = msg.getCategory();

	    // get persistent category
	    MessageCategory persistentCategory = getOrInsertCategory(msgCategory
		    .getName());

	    if (persistentCategory != null && persistentCategory.isPersistent()) {
		// replace message category with persistent one
		msg.mapperSetCategory(persistentCategory);
		result = true;
	    }
	}

	return result;
    }

    /**
     * Maps given category to it's database representation
     * 
     * @param msg
     *            Syslog message
     * @return true if succeeded, false otherwise
     */
    private boolean mapType(Message msg) {
	boolean result = false;

	if (msg != null && msg.getMessageType() != null) {
	    // get current message type
	    MessageType msgType = msg.getMessageType();

	    // get persistent type
	    MessageType persistentType = getOrInsertType(msgType.getName());

	    if (persistentType != null && persistentType.isPersistent()) {
		// replace message type with persistent one
		msg.mapperSetType(persistentType);
		result = true;
	    }
	}

	return result;
    }

    /**
     * Maps given sender to it's database representation
     * 
     * @param s
     *            Sender to be mapped
     * @return true if succeeded, false otherwise
     */
    private boolean mapSender(Message msg) {
	boolean result = false;

	if (msg != null && msg.getSender() != null) {
	    // get current message sender
	    MessageSender msgSender = msg.getSender();

	    // get persistent sender
	    MessageSender persistentSender = getOrInsertSender(msgSender
		    .getName());

	    if (persistentSender != null && persistentSender.isPersistent()) {
		// replace message sender with persistent one
		msg.mapperSetSender(persistentSender);
		result = true;
	    }
	}

	return result;
    }

    /**
     * Maps given syslog session into database. Session can be either persistent
     * or not.
     * 
     * @param msg
     * @return true if succeeded, false otherwise
     */
    private boolean mapSession(Message msg) {
	boolean result = false;

	if (msg != null && msg.getSession() != null)
	    try {
		SyslogSession msgSession = msg.getSession();

		if (!msgSession.isPersistent()) { // insert session into
		    // database
		    SyslogSessionMapper mapper = new SyslogSessionMapper(db);
		    if (mapper.save(msgSession)) {
			result = true;
		    }
		} else
		    result = true;

	    } catch (Exception e) {
		e.printStackTrace();
	    }

	return result;
    }

    /*
     * We use separate implementation for categories, types and senders since
     * each of them can change in future. (e.g. we might want additional
     * properties for MessageCategory, MessageType or MessageSender) No code
     * duplicating is in mind.
     */

    private void LoadCategories() {
	categories_cache = new HashMap<String, MessageCategory>();

	ResultSet rs = null;
	Statement st = null;

	try {
	    Connection conn = db.getConnection();
	    st = conn.createStatement();

	    rs = st.executeQuery("select id,name,description from "
		    + "syslog_categories;");

	    long recordID;
	    String recordName = null;
	    String recordDescription = null;

	    while (rs.next()) {
		recordID = rs.getLong(1);
		recordName = rs.getString(2);
		recordDescription = rs.getString(3);

		if (!categories_cache.containsKey(recordName)) {
		    MessageCategory record = new MessageCategory(recordName,
			    recordDescription);
		    record.mapperSetID(recordID);
		    record.mapperSetPersistence(true);

		    cacheCategory(record);
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    db.Cleanup(st, rs);
	}
    }

    private void LoadTypes() {
	types_cache = new HashMap<String, MessageType>();

	ResultSet rs = null;
	Statement st = null;

	try {
	    Connection conn = db.getConnection();
	    st = conn.createStatement();

	    rs = st.executeQuery("select id,name,description from "
		    + "syslog_types;");

	    long recordID;
	    String recordName = null;
	    String recordDescription = null;

	    while (rs.next()) {
		recordID = rs.getLong(1);
		recordName = rs.getString(2);
		recordDescription = rs.getString(3);

		if (!types_cache.containsKey(recordName)) {
		    MessageType record = new MessageType(recordName,
			    recordDescription);
		    record.mapperSetID(recordID);
		    record.mapperSetPersistence(true);

		    cacheType(record);
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    db.Cleanup(st, rs);
	}
    }

    private void LoadSenders() {
	senders_cache = new HashMap<String, MessageSender>();

	ResultSet rs = null;
	Statement st = null;

	try {
	    Connection conn = db.getConnection();
	    st = conn.createStatement();

	    rs = st.executeQuery("select id,name,description from "
		    + "syslog_senders;");

	    long recordID;
	    String recordName = null;
	    String recordDescription = null;

	    while (rs.next()) {
		recordID = rs.getLong(1);
		recordName = rs.getString(2);
		recordDescription = rs.getString(3);

		if (!senders_cache.containsKey(recordName)) {
		    MessageSender record = new MessageSender(recordName,
			    recordDescription);
		    record.mapperSetID(recordID);
		    record.mapperSetPersistence(true);

		    cacheSender(record);
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    db.Cleanup(st, rs);
	}
    }

    /**
     * Attempts to get category with given name from cache or creates new one
     * (if no such category in cache) and inserts it into database
     * 
     * @param Name
     *            Category name
     * @return Persistent message category if succeded, null-reference otherwise
     */
    private MessageCategory getOrInsertCategory(String Name) {
	MessageCategory result = null;

	if (!categories_cache.containsKey(Name)) {

	    PreparedStatement pr = null;
	    try {

		Connection conn = db.getConnection();

		pr = conn
			.prepareStatement("insert into syslog_categories(name) values(?);");
		pr.setString(1, Name);

		int rows_affected = pr.executeUpdate();

		/*
		 * If performing batch insert ID of record will be left. So we
		 * force commit changes to db
		 */
		if (!db.getAutoCommit()) {
		    db.commit();
		}

		if (rows_affected == 1) { // category has been saved

		    long recordID = db.LastInsertRowID();
		    if (recordID > 0) { // category has valid id in db
			result = new MessageCategory(Name);
			result.mapperSetID(recordID);
			result.mapperSetPersistence(true);

			cacheCategory(result);
		    }
		}
	    } catch (Exception e) {
		result = null;
	    } finally {
		db.Cleanup(pr);
	    }
	} else {
	    result = categories_cache.get(Name);
	}

	return result;
    }

    private MessageType getOrInsertType(String Name) {
	MessageType result = null;

	if (!types_cache.containsKey(Name)) {

	    PreparedStatement pr = null;
	    try {

		Connection conn = db.getConnection();

		pr = conn
			.prepareStatement("insert into syslog_types(name) values(?);");
		pr.setString(1, Name);

		int rows_affected = pr.executeUpdate();

		/*
		 * If performing batch insert ID of record will be left. So we
		 * force commit changes to db
		 */
		if (!db.getAutoCommit()) {
		    db.commit();
		}

		if (rows_affected == 1) { // category has been saved

		    long recordID = db.LastInsertRowID();
		    if (recordID > 0) { // category has valid id in db
			result = new MessageType(Name);
			result.mapperSetID(recordID);
			result.mapperSetPersistence(true);

			cacheType(result);
		    }
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    } finally {
		db.Cleanup(pr);
	    }
	} else {
	    result = types_cache.get(Name);
	}

	return result;
    }

    private MessageSender getOrInsertSender(String Name) {
	MessageSender result = null;

	if (!senders_cache.containsKey(Name)) {

	    PreparedStatement pr = null;
	    try {

		Connection conn = db.getConnection();

		pr = conn
			.prepareStatement("insert into syslog_senders(name) values(?);");
		pr.setString(1, Name);

		int rows_affected = pr.executeUpdate();

		/*
		 * If performing batch insert ID of record will be left. So we
		 * force commit changes to db
		 */
		if (!db.getAutoCommit()) {
		    db.commit();
		}

		if (rows_affected == 1) { // category has been saved

		    long recordID = db.LastInsertRowID();
		    if (recordID > 0) { // category has valid id in db
			result = new MessageSender(Name);
			result.mapperSetID(recordID);
			result.mapperSetPersistence(true);

			cacheSender(result);
		    }
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    } finally {
		db.Cleanup(pr);
	    }
	} else {
	    result = senders_cache.get(Name);
	}

	return result;
    }

    private MessageCategory getCategory(String name) {
	return categories_cache.get(name);
    }

    private MessageType getType(String name) {
	return types_cache.get(name);
    }

    private MessageSender getSender(String name) {
	return senders_cache.get(name);
    }

    /**
     * Gets identifiers of categories using their names
     * 
     * @param categoryNames
     *            List of category names
     * @return List of categories identifiers
     */
    private List<Long> getCategoriesID(List<String> categoryNames) {
	List<Long> result = new ArrayList<Long>();

	if (categoryNames != null && categoryNames.size() > 0) {
	    for (String name : categoryNames) {
		MessageCategory category = getCategory(name);

		if (category != null && category.isPersistent()) {
		    result.add(category.getID());
		}
	    }
	}

	return result;
    }

    /**
     * Gets identifiers of types using their names
     * 
     * @param typeNames
     *            List of types names
     * @return List of types identifiers
     */
    private List<Long> getTypesID(List<String> typeNames) {
	List<Long> result = new ArrayList<Long>();

	if (typeNames != null && typeNames.size() > 0) {
	    for (String name : typeNames) {
		MessageType type = getType(name);

		if (type != null && type.isPersistent()) {
		    result.add(type.getID());
		}
	    }
	}

	return result;
    }

    /**
     * Gets identifiers of senders using their names
     * 
     * @param senderNames
     *            List of senders names
     * @return List of senders identifiers
     */
    private List<Long> getSendersID(List<String> senderNames) {
	List<Long> result = new ArrayList<Long>();

	if (senderNames != null && senderNames.size() > 0) {
	    for (String name : senderNames) {
		MessageSender sender = getSender(name);

		if (sender != null && sender.isPersistent()) {
		    result.add(sender.getID());
		}
	    }
	}

	return result;
    }

    /**
     * Internal method which attempts to execute fetch query in order to
     * retrieve syslog messages from database
     * 
     * @param sql
     *            Query to be executed
     * @return Messages that are fetched from database. If any error occurs
     *         returns empty list
     */
    private List<Message> executeGetQuery(String sql) {
	ArrayList<Message> result = new ArrayList<Message>();
	Statement st = null;
	ResultSet rs = null;

	try {
	    Connection conn = db.getConnection();
	    st = conn.createStatement();

	    rs = st.executeQuery(sql);

	    while (rs.next()) {
		long messageID = rs.getLong(1);
		Date timestamp = rs.getDate(2);
		String text = rs.getString(3);
		long sessionID = rs.getLong(4);

		SyslogSession session = SyslogSessionMapper.getByID(sessionID);

		String categoryName = rs.getString(5);
		MessageCategory category = getOrInsertCategory(categoryName);

		String typeName = rs.getString(6);
		MessageType type = getOrInsertType(typeName);

		String senderName = rs.getString(7);
		MessageSender sender = getOrInsertSender(senderName);

		Message msg = new Message(text, categoryName, typeName,
			senderName, session);
		msg.mapperSetID(messageID);
		msg.mapperSetTimestamp(timestamp);
		msg.mapperSetCategory(category);
		msg.mapperSetType(type);
		msg.mapperSetSender(sender);
		msg.mapperSetPersistence(true);

		result.add(msg);

	    }
	} catch (Exception e) {
	    result.clear();
	} finally {
	    db.Cleanup(st, rs);
	}

	return result;
    }

    private static void cacheCategory(MessageCategory category) {
	if (category != null && category.isPersistent()) {
	    String name = category.getName();
	    if (!categories_cache.containsKey(name)) {
		categories_cache.put(name, category);
	    }
	}
    }

    private static void cacheType(MessageType type) {
	if (type != null && type.isPersistent()) {
	    String name = type.getName();
	    if (!types_cache.containsKey(name)) {
		types_cache.put(name, type);
	    }
	}
    }

    private static void cacheSender(MessageSender sender) {
	if (sender != null && sender.isPersistent()) {
	    String name = sender.getName();
	    if (!senders_cache.containsKey(name)) {
		senders_cache.put(name, sender);
	    }
	}
    }

    /*
     * Categories, senders and types are cached by their name. This means that
     * there can't be two senders (types,categories) with the same name
     */
    static HashMap<String, MessageCategory> categories_cache = null;
    static HashMap<String, MessageSender> senders_cache = null;
    static HashMap<String, MessageType> types_cache = null;
}
