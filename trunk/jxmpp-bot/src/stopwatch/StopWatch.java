package stopwatch;

/*
 Copyright (c) 2005, Corey Goldberg

 StopWatch.java is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.
 */

/**
 * Represents class which provides stop watch mechanism.
 * 
 * @author tillias_work
 * 
 */
public class StopWatch {

	private long startTime = 0;
	private long stopTime = 0;
	private boolean running = false;

	/**
	 * Starts stopWatch and remembers start time
	 */
	public void start() {
		startTime = System.currentTimeMillis();
		running = true;
	}

	/**
	 * Stops stopWatch and remembers stop time
	 */
	public void stop() {
		stopTime = System.currentTimeMillis();
		running = false;
	}

	/**
	 * Restarts stopWatch. Calls stop() and then start().
	 */
	public void restart() {
		stop();
		start();
	}

	/**
	 * Gets value indicating whether stopWatch is running. E.g. client has
	 * called {@link #start()}
	 * 
	 * @return
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * If stopWatch is running gets total number of milliseconds since it was
	 * started. Otherwise (stopWatch is stopped) gets difference in milliseconds
	 * between it's stop and start date
	 * 
	 * @return Number of milliseconds
	 * @see #getElapsedTimeSecs()
	 */
	public long getElapsedTime() {
		long elapsed;
		if (running) {
			elapsed = (System.currentTimeMillis() - startTime);
		} else {
			elapsed = (stopTime - startTime);
		}
		return elapsed;
	}

	/**
	 * If stopWatch is running gets total number of seconds since it was
	 * started. Otherwise (stopWatch is stopped) gets difference in seconds
	 * between it's stop and start date
	 * 
	 * @return Number of seconds
	 * @see #getElapsedTime()
	 */
	public long getElapsedTimeSecs() {
		long elapsed;
		if (running) {
			elapsed = ((System.currentTimeMillis() - startTime) / 1000);
		} else {
			elapsed = ((stopTime - startTime) / 1000);
		}
		return elapsed;
	}
}
