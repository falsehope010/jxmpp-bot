package syslog;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import mappers.SyslogSessionMapper;

import stopwatch.BoundedStopWatch;
import syslog.rotate.ILogRotateStrategy;

import database.Database;
import domain.syslog.Message;
import domain.syslog.SyslogSession;
import exceptions.DatabaseNotConnectedException;
import exceptions.SessionNotStartedException;

/**
 * Represents system logger subsystem which allows to save system events, messages, failures etc
 * into database and retrieve them for analysis later. Syslog manages it's own internal cache, so when
 * you put log message it doesn't go into database immediately, but is stored in this cache. 
 * Syslog waits for specified amount of time ( see {@link #getSaveLogsTimeout()} ) and then
 * saves all messages from this cache into database.
 * <p>
 * Syslog subsystem also manages log rotation. During construction you must specify log rotation
 * strategy and time interval in milliseconds ( see {@link #getLogRotateTimeout()} 
 * in which syslog will perform log rotating.
 * @author tillias_work
 *
 */
public class SysLog implements Runnable {
	
	
	/**
	 * Creates new syslog instance. Call {@link #start()} in order to begin logging 
	 * system events, messages etc.
	 * @param db Database which will be used to store system logs
	 * @param strategy Strategy which will be used to rotate system logs
	 * @param saveLogsTimeout Specifies interval in milliseconds after which syslog will save all syslog messages
	 * 		from it's internal buffer into database
	 * @param logRotateTimeout Specifies interval in milliseconds after which syslog will perform log rotate
	 * 		procedure (e.g. logs cleanup)
	 * @throws IllegalArgumentException Thrown if timeout(s) passed to constructor are smaller or equal to zero
	 * @throws DatabaseNotConnectedException Thrown if database passed to constructor isn't connected to datasource
	 * @throws NullPointerException Thrown if database or log rotation strategy is null-reference
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
	 * Puts text message with given attributes (sender,category and type) into system log.
	 * @param text Message text
	 * @param sender Sender of message
	 * @param category Message Category
	 * @param type Message Type
	 * @return
	 * @throws SessionNotStartedException
	 */
	public Message putMessage(String text, String sender, String category,
			String type) throws SessionNotStartedException {
		Message result = null;
		SyslogSession currentSession = getCurrentSession();

		if (currentSession == null) {
			throw new SessionNotStartedException(); // session must be started
		} else {
			result = new Message(text, category, type, sender, currentSession);
			enqueueMessage(result); // put into internal queue
		}

		return result;
	}
	
	/**
	 * Gets total number of milliseconds which SysLog will wait before saving all messages from it's
	 * internal cache into database 
	 * @return Number of milliseconds 
	 */
	public long getSaveLogsTimeout(){
		return saveLogsWatch.getBound();
	}
	
	/**
	 * Gets total number of milliseconds which SysLog will wait before performing log rotate
	 * @return Number of miliseconds
	 */
	public long getLogRotateTimeout(){
		return logRotateWatch.getBound();
	}
	
	
	/**
	 * Saves all messages from internal messages cache into database
	 * @return
	 */
	public boolean saveMessages(){
		boolean result = false;
		
		//AtomicLong
		
		return result;
	}
	
	/*
	 * Session management
	 */
	
	/**
	 * Gets current syslog session. If syslog is in unstarted state, returns null
	 * @return Current session
	 */
	public SyslogSession getCurrentSession(){
		return currentSession;
	}
	
	/**
	 * Starts new syslog session and maps it into db. If there is already session started,
	 * closes it before creating and starting new one.
	 * @return true if succeded, false otherwise
	 */
	public boolean startNewSession() {
		boolean result = false;

		try {
			closeSession(getCurrentSession());

			currentSession = createSession();

			if (currentSession != null && currentSession.isPersistent())
				result = true;
			else
				currentSession = null; // if session isn't persistent set it to null
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
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
				
				//TODO: Put syslog message with stacktrace as text
			}
		}
		
		setRunning(false); // thread finishes it's work here and exits
	}
	
	/**
	 * Starts syslog. It will be able to save messages into database and perform logs cleanup.
	 * <p>During startup syslog creates new session and saves it into database. All messages
	 * will be written into db using this session.
	 * If syslog is already running does nothing.
	 * @return
	 * @see #isRunning()
	 * @see #stop()
	 */
	public boolean start() {
		boolean result = false;

		if (!isRunning()) {
			if (thread == null) {	//starting syslog first time
				
				thread = new Thread(this);
				setTerminate(false); // otherwise thread will exit immediately
				thread.start();

				if (startNewSession()){
					result = true;
					
					setRunning(true);
					
				}
			}
			else{	
				/*
				 * We are restarting syslog.
				 * Since isRunning is false, thread has finished it's work and exited.
				 * So we can close current session (e.g. call startNewSession),
				 * create new thread and start it
				 */
				startNewSession();
				
				//TODO: compare to previous section and if possible merge them together
			}
		}

		return result;
	}
	
	public void stop(){ //TODO: add comment
		setTerminate(true);
		terminate = true;
	}
	
	/**
	 * Gets value indicating whether Syslog is currently running.
	 * @see #start()
	 * @see #stop()
	 * @return
	 */
	public boolean isRunning(){
		return running;
	}
	
	/*
	 * Protected and private methods
	 */
	
	/**
	 * Dequeues maximum maxItems from given queue. Actual count of dequeued items may be smaller
	 * then count specified by <b>maxItems</b> parameter
	 * @param queue Source queue which will supply items
	 * @param maxItems Maximum number of items to be dequeued
	 * @return Dequeued messages
	 */
	protected ArrayList<Message> dequeueAllMessages(ConcurrentLinkedQueue<Message> queue, int maxItems){ 
		//TODO: unit testing
		ArrayList<Message> result = new ArrayList<Message>();
		
		if (queue != null){
			int i = 0;
			Message msg = internalQueue.poll();
			
			while (msg != null && i < maxDequeueItems){
				result.add(msg);
				
				msg = internalQueue.poll();
				++i;
			}
		}
		
		return result;
	}
	
	/**
	 * Creates new syslog session and saves it into database (endDate is null, since 
	 * session isn't closed
	 * @return Persistent syslog session if succeeded, null-reference otherwise
	 */
	private SyslogSession createSession(){
		SyslogSession result = null;
		try {
			SyslogSession session = new SyslogSession();
			SyslogSessionMapper mapper = new SyslogSessionMapper(db);
			
			if (mapper.save(session)){
				result = session;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * Closes given session and saves it into database
	 * @param session Session to be closed and saved
	 * @return true if succeded, false otherwise
	 */
	private boolean closeSession(SyslogSession session){
		boolean result = false;
		if (session != null) {
			session.close();

			try {
				SyslogSessionMapper mapper = new SyslogSessionMapper(db);

				if (mapper.save(session)) {
					result = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	private void enqueueMessage(Message msg){
		if (msg != null){
			internalQueue.add(msg);
		}
	}
	
	private void setRunning(boolean value){
		running = value;
	}
	
	private void setTerminate(boolean value){
		terminate = value;
	}
	
	/*
	 * Data fields
	 */
	
	/**
	 * Each time syslog performs saving of messages into database from internal cache
	 * it dequeues messages. Maximum allowed messages count to be
	 * dequeued at once is controller by this constant
	 */
	static final int maxDequeueItems = 100;
	
	/**
	 * Valid persistent session. All messages written into database use this session's
	 * ID field
	 */
	SyslogSession currentSession;
	
	/**
	 * Used as memory cache for syslog messages. Syslog itself runs separate thread which in
	 * specified intervals dequeues all messages from internal queue and maps them into database
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
