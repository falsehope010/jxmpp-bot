package syslog;

import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import mappers.SyslogMessageMapper;
import mappers.SyslogSessionMapper;
import stopwatch.BoundedStopWatch;
import syslog.rotate.ILogRotateStrategy;
import utils.StackTraceUtil;
import database.Database;
import domain.syslog.Message;
import domain.syslog.SyslogSession;
import exceptions.DatabaseNotConnectedException;
import exceptions.SessionNotStartedException;

/**
 * Represents system logger subsystem which allows to save system events,
 * messages, failures etc into database and retrieve them for analysis later.
 * Syslog manages it's own internal cache, so when you put log message it
 * doesn't go into database immediately, but is stored in this cache. Syslog
 * waits for specified amount of time ( see {@link #getSaveLogsTimeout()} ) and
 * then saves all messages from this cache into database.
 * <p>
 * Syslog subsystem also manages log rotation. During construction you must
 * specify log rotation strategy and time interval in milliseconds ( see
 * {@link #getLogRotateTimeout()} in which syslog will perform log rotating.
 * 
 * @author tillias_work
 * 
 */
public class SysLog implements Runnable {

	/**
	 * Creates new syslog instance. Call {@link #start()} in order to begin
	 * logging system events, messages etc.
	 * 
	 * @param db
	 *            Database which will be used to store system logs
	 * @param strategy
	 *            Strategy which will be used to rotate system logs
	 * @param saveLogsTimeout
	 *            Specifies interval in milliseconds after which syslog will
	 *            save all syslog messages from it's internal buffer into
	 *            database
	 * @param logRotateTimeout
	 *            Specifies interval in milliseconds after which syslog will
	 *            perform log rotate procedure (e.g. logs cleanup)
	 * @throws IllegalArgumentException
	 *             Thrown if timeout(s) passed to constructor are smaller or
	 *             equal to zero
	 * @throws DatabaseNotConnectedException
	 *             Thrown if database passed to constructor isn't connected to
	 *             datasource
	 * @throws NullPointerException
	 *             Thrown if database or log rotation strategy is null-reference
	 */
	public SysLog(Database db, ILogRotateStrategy strategy,
			long saveLogsTimeout, long logRotateTimeout)
			throws IllegalArgumentException, DatabaseNotConnectedException,
			NullPointerException {

		if (db == null || strategy == null)
			throw new NullPointerException(
					"Null-reference database or logRotateStrategy");

		if (!db.isConnected())
			throw new DatabaseNotConnectedException();

		if (saveLogsTimeout <= 0 || logRotateTimeout <= 0)
			throw new IllegalArgumentException("Invalid syslog timeout(s)");

		internalQueue = new ConcurrentLinkedQueue<Message>();
		saveLogsWatch = new BoundedStopWatch(saveLogsTimeout);
		logRotateWatch = new BoundedStopWatch(logRotateTimeout);
		logRotateStrategy = strategy;

		this.db = db;
	}

	/**
	 * Puts text message with given attributes (sender,category and type) into
	 * system log. Attributes must not be null or empty strings, otherwise
	 * method will fail without throwing any exception
	 * <p>
	 * This is asynchronous operation. It may take some time while syslog will
	 * flush it's internal cache and actually put message into database
	 * 
	 * @param text
	 *            Message text
	 * @param sender
	 *            Sender of message
	 * @param category
	 *            Message Category
	 * @param type
	 *            Message Type
	 * @return True if succeded, false otherwise
	 * @throws SessionNotStartedException
	 *             Thrown if you attempted to put message without starting
	 *             syslog
	 * @see #start()
	 * @see #isRunning()
	 * @see #getSaveLogsTimeout()
	 */
	public boolean putMessage(String text, String sender, String category,
			String type) throws SessionNotStartedException {
		boolean result = false;

		if (currentSession == null)
			throw new SessionNotStartedException(); // session must be started

		try {
			Message msg = new Message(text, category, type, sender,
					currentSession);
			enqueueMessage(msg); // put into internal queue
			result = true;
		} catch (Exception e) {
			System.out.print(StackTraceUtil.toString(e));
		}

		return result;
	}

	/**
	 * Gets total number of milliseconds which SysLog will wait before saving
	 * all messages from it's internal cache into database
	 * 
	 * @return Number of milliseconds
	 */
	public long getSaveLogsTimeout() {
		return saveLogsWatch.getBound();
	}

	/**
	 * Gets total number of milliseconds which SysLog will wait before
	 * performing log rotate
	 * 
	 * @return Number of miliseconds
	 */
	public long getLogRotateTimeout() {
		return logRotateWatch.getBound();
	}

	/**
	 * Gets current syslog session. If syslog is in unstarted state, returns
	 * null
	 * 
	 * @return Current session
	 */
	public SyslogSession getCurrentSession() {
		return currentSession;
	}

	/*
	 * Threading
	 */

	@Override
	public void run() {
		saveLogsWatch.start();
		logRotateWatch.start();

		while (!terminate) {
			try {
				if (saveLogsWatch.breaksBound()) {
					System.out.println("running "
							+ saveLogsWatch.getElapsedTime());
				} else {
					System.out.println("log save hit!");
					saveLogsWatch.restart();
				}
				Thread.sleep(250);
			} catch (Exception e) {
				e.printStackTrace();

				// TODO: Put syslog message with stacktrace as text
			}
		}

		setRunning(false); // thread finishes it's work here and exits
	}

	/**
	 * Starts syslog. It will be able to save messages into database and perform
	 * logs cleanup.
	 * <p>
	 * During startup syslog creates new session and saves it into database. All
	 * messages will be written into db using this session. If syslog is already
	 * running does nothing.
	 * <p>
	 * This is synchronous operation.
	 * 
	 * @return True if succeded, false otherwise
	 * @see #isRunning()
	 * @see #stop()
	 */
	public boolean start() {
		boolean result = false;

		if (!isRunning()) {

			thread = new Thread(this);
			setTerminate(false); // otherwise thread will exit immediately
			thread.start();

			if (startNewSession()) {
				result = true;

				setRunning(true);
			}
		}
		return result;
	}

	/**
	 * Stops syslog. If syslog is already stopped does nothing.
	 * <p>
	 * This is asynchronous operation and it can take a while until syslog
	 * actually stops it's underlying thread. You can detect that syslog has
	 * stopped it's thread by checking {@link #isRunning()}
	 */
	public void stop() {
		if (isRunning()) {
			setTerminate(true);
			closeSession(currentSession); // is running guarantees that session
			// isn't null
		}
	}

	/**
	 * Gets value indicating whether Syslog is currently running.
	 * 
	 * @see #start()
	 * @see #stop()
	 * @return
	 */
	public boolean isRunning() {
		return running;
	}

	/*
	 * Protected and private methods
	 */

	/**
	 * Saves all messages from internal messages cache into database.
	 * 
	 * @return True if succeded, false otherwise
	 */
	protected boolean flushCache() {
		boolean result = false;

		try {
			List<Message> messages = getCachedMessages(maxDequeueItems);

			SyslogMessageMapper mapper = new SyslogMessageMapper(db);

			boolean ok = true;

			if (messages != null) {
				for (Message m : messages) {
					if (!mapper.save(m)) {
						ok = false;
						break;
					}
				}
			}

			result = ok;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * Starts new syslog session and maps it into db. If there is already
	 * session started, closes it before creating and starting new one.
	 * 
	 * @return true if succeded, false otherwise
	 */
	protected boolean startNewSession() {
		boolean result = false;

		try {
			boolean session_closed = true;

			if (currentSession != null) {
				session_closed = closeSession(currentSession);
			}

			if (session_closed) {
				currentSession = createSession();

				if (currentSession != null && currentSession.isPersistent())
					result = true;
				else
					currentSession = null; // if session isn't persistent set it
				// to null
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * Dequeues maximum maxItems from given queue. Actual count of dequeued
	 * items may be smaller then count specified by <b>maxItems</b> parameter
	 * 
	 * @param queue
	 *            Source queue which will supply items
	 * @param maxItems
	 *            Maximum number of items to be dequeued
	 * @return Dequeued messages
	 */
	protected List<Message> getCachedMessages(int maxItems) {
		ArrayList<Message> result = new ArrayList<Message>();

		try {
			if (internalQueue != null) {
				int i = 0;
				Message msg = internalQueue.poll();

				while (msg != null && i < maxItems) {
					result.add(msg);

					msg = internalQueue.poll();
					++i;
				}
			}
		} catch (Exception e) {
			System.out.print(StackTraceUtil.toString(e));
		}

		return result;
	}

	/**
	 * Enqueues message into internal cache
	 * 
	 * @param msg
	 *            Message
	 */
	protected void enqueueMessage(Message msg) {
		if (msg != null) {
			internalQueue.add(msg);
		}
	}

	/**
	 * Gets total number items in cache
	 * 
	 * @return Total number of items in cache
	 */
	protected int getCacheSize() {
		return internalQueue.size();
	}

	/**
	 * Gets value indicating whether syslog thread should terminate
	 * 
	 * @return True if syslog thread should terminate false otherwise
	 */
	protected boolean getTerminate() {
		return terminate;
	}

	/**
	 * Gets underlying thread state. If syslog was never started returns null.
	 * If syslog has ever been started returns valid {@link Thread.State}
	 * 
	 * @return Underlying thread state.
	 */
	protected State getThreadState() {
		State result = null;

		if (thread != null)
			result = thread.getState();

		return result;
	}

	/**
	 * Creates new syslog session and saves it into database (endDate is null,
	 * since session isn't closed)
	 * 
	 * @return Persistent syslog session if succeeded, null-reference otherwise
	 */
	private SyslogSession createSession() {
		SyslogSession result = null;
		try {
			SyslogSession session = new SyslogSession();
			SyslogSessionMapper mapper = new SyslogSessionMapper(db);

			if (mapper.save(session)) {
				result = session;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Closes given session and saves it into database
	 * 
	 * @param session
	 *            Session to be closed and saved
	 * @return true if succeded, false otherwise
	 */
	private boolean closeSession(SyslogSession session) {
		boolean result = false;
		if (session != null) {
			if (!session.isClosed()) {
				session.close();

				try {
					SyslogSessionMapper mapper = new SyslogSessionMapper(db);

					if (mapper.save(session)) {
						result = true;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				// session is already closed
				result = true;
			}
		}
		return result;
	}

	private void setRunning(boolean value) {
		running = value;
	}

	private void setTerminate(boolean value) {
		terminate = value;
	}

	/*
	 * Data fields
	 */

	/**
	 * Each time syslog performs saving of messages into database from internal
	 * cache it dequeues messages. Maximum allowed messages count to be dequeued
	 * at once is controller by this constant
	 */
	static final int maxDequeueItems = 100;

	/**
	 * Valid persistent session. All messages written into database use this
	 * session's ID field
	 */
	SyslogSession currentSession;

	/**
	 * Used as memory cache for syslog messages. Syslog itself runs separate
	 * thread which in specified intervals dequeues all messages from internal
	 * queue and maps them into database
	 */
	ConcurrentLinkedQueue<Message> internalQueue;

	Thread thread;
	boolean terminate;
	boolean running;
	Database db;

	BoundedStopWatch saveLogsWatch;
	BoundedStopWatch logRotateWatch;

	ILogRotateStrategy logRotateStrategy;
}
