package syslog.rotate;

import mappers.SyslogMessageMapper;
import database.Database;
import exceptions.DatabaseNotConnectedException;

/**
 * Implements {@link ILogRotateStrategy}.
 * <p>
 * After cleanup only specified number of latest messages will be kept in
 * database.
 * 
 * @author tillias
 * 
 */
public class CountdownLogRotateStrategy extends AbstractLogRotateStrategy {

    /**
     * Creates new instance of strategy.
     * 
     * @param db
     *            Database which will be used to perform log rotate
     * @param keptMessages
     *            Total number of messages which will be kept in database. If
     *            parameter is less or equal to zero
     *            {@link #DEFAULT_MESSAGES_KEPT} will be used
     * @throws NullPointerException
     *             Thrown if database parameter passed to constructor is not in
     *             connected state
     * @throws DatabaseNotConnectedException
     *             Thrown if database parameter passed to constructor is null
     *             reference
     */
    public CountdownLogRotateStrategy(Database db, long keptMessages)
	    throws NullPointerException, DatabaseNotConnectedException {
	super(db);

	if (keptMessages > 0)
	    this.keptMessages = keptMessages;
	else
	    this.keptMessages = DEFAULT_MESSAGES_KEPT;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Current implementation first counts total number of persistent messages
     * in syslog, and if it is greater then specified number erases obsolete
     * messages
     * 
     * @see #getKeptMessagesCount()
     */
    @Override
    public boolean rotate() {
	boolean result = false;

	try {
	    SyslogMessageMapper mapper = getMessageMapper();

	    long messagesCount = mapper.getPersistentMessagesCount();

	    if (messagesCount > keptMessages) { // we do need to perform cleanup
		result = mapper.deleteBelow(keptMessages);
	    } else {
		result = true;
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}

	return result;
    }

    /**
     * Gets total number of messages which will be kept in database after
     * cleanup
     * 
     * @return
     */
    public long getKeptMessagesCount() {
	return keptMessages;
    }

    long keptMessages;

    /**
     * Default number of syslog messages which will be kept in database
     */
    public static final long DEFAULT_MESSAGES_KEPT = 1024;
}
