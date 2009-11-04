package muc.services;

import mappers.ChatMessageMapper;
import database.Database;
import domain.muc.ChatMessage;
import exceptions.DatabaseNotConnectedException;
import exceptions.ServiceOperationException;

public class ChatMessageService {

    /**
     * Creates new instance of service
     * 
     * @param db
     *            {@link Database} object which will be used by service
     * @throws NullPointerException
     *             Thrown if argument passed to constructor is null
     * @throws DatabaseNotConnectedException
     *             Thrown if database passed to constructor is in disconnected
     *             state
     */
    public ChatMessageService(Database db) throws NullPointerException,
	    DatabaseNotConnectedException {
	if (db == null)
	    throw new NullPointerException();
	if (!db.isConnected())
	    throw new DatabaseNotConnectedException();

	mapper = new ChatMessageMapper(db);
	initBuffer(DEFAULT_CAPACITY);
    }

    /**
     * Immediately saves all messages from internal buffer into database and
     * sets buffer size to zero
     * 
     * @throws ServiceOperationException
     *             Thrown if service is unable to save messages into database
     */
    public void flush() throws ServiceOperationException {
	if (itemsCount > 0) {

	    mapper.beginBatchOperation();

	    for (int i = 0; i < itemsCount; ++i) {
		if (!mapper.save(buffer[i])) {

		    mapper.endBatchOperation(); // force restore previous state

		    throw new ServiceOperationException(
			    "Can't save chat message to database", null);
		}
	    }

	    for (int i = 0; i < itemsCount; ++i)
		buffer[i] = null;

	    itemsCount = 0;

	    mapper.endBatchOperation();
	}
    }

    /**
     * Saves message into database. Uses buffering
     * 
     * @param msg
     *            Message to be saved to database
     * @throws ServiceOperationException
     *             Thrown if
     * @see {@link #flush()}
     */
    public void save(ChatMessage msg) throws ServiceOperationException {
	if (msg != null) {
	    if (bufferIsFull()) {
		flush();
	    }

	    buffer[itemsCount] = msg;
	    ++itemsCount;
	}
    }

    /**
     * Allocates new internal buffer of service needed to store char messages
     * 
     * @param size
     *            New size of internal buffer of service
     * @throws IllegalArgumentException
     *             Thrown if size argument passed to this method is not positive
     */
    private void initBuffer(int size) {
	if (size <= 0)
	    throw new IllegalArgumentException(
		    "Items count for internal buffer must be positive but passed = "
			    + String.valueOf(size));
	maxItemsCount = size;
	itemsCount = 0;
	buffer = new ChatMessage[maxItemsCount];
    }

    /**
     * Gets value indicating that internal buffer of service is full
     * 
     * @return True if buffer is full, false otherwise
     */
    private boolean bufferIsFull() {
	if (itemsCount >= maxItemsCount)
	    return true;

	return false;
    }

    int maxItemsCount;
    int itemsCount;
    ChatMessage[] buffer;

    ChatMessageMapper mapper;

    public static final int DEFAULT_CAPACITY = 256;
}
