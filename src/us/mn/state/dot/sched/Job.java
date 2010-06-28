/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2010  Minnesota Department of Transportation
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package us.mn.state.dot.sched;

import java.util.Calendar;
import java.util.Date;

/**
 * Job for the scheduler to perform.  When the scheduler is ready, the perform
 * method is called.  The perform method must be implemented by any subclasses.
 *
 * @author Douglas Lau
 */
abstract public class Job implements Comparable<Job> {

	/** Calculate a time interval from a Calendar field and value.
	 * @param field java.util.Calendar field constant.
	 * @param value Amount of specified field.
	 * @return Number of milliseconds in interval. */
	static protected long calculateInterval(int field, int value) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(0);
		c.add(field, value);
		return c.getTimeInMillis();
	}

	/** Job identifier */
	static protected long job_id = 0;

	/** Unique job identifier */
	protected final long id = job_id++;

	/** Next time this job must be performed */
	protected final Date nextTime;

	/** Time interval to perform this job, in milliseconds.  For
	 * non-repeating jobs, this must be 0. */
	protected final long interval;

	/** Time offset from whole interval boundary, in milliseconds. */
	protected final long offset;

	/** Count of how many times the job has completed */
	protected int n_complete = 0;

	/**
	 * Create a new scheduler job.
	 * @param iField java.util.Calendar field for time interval
	 * @param i Time interval to schedule the job
	 * @param oField java.util.Calendar field for interval offset
	 * @param o Time interval offset to schedule the job
	 */
	public Job(int iField, int i, int oField, int o) {
		assert i >= 0;
		assert o >= 0;
		interval = calculateInterval(iField, i);
		offset = calculateInterval(oField, o);
		assert offset < interval;
		nextTime = new Date();
		computeNextTime();
	}

	/**
	 * Create a new scheduler job.
	 * @param iField java.util.Calendar field for time interval
	 * @param i Time interval to schedule the job
	 */
	public Job(int iField, int i) {
		this(iField, i, Calendar.SECOND, 0);
	}

	/** Create a one-shot scheduler job */
	public Job(int milliseconds) {
		interval = 0;
		offset = 0;
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MILLISECOND, milliseconds);
		nextTime = c.getTime();
	}

	/** Create a one-shot job to schedule immediately */
	public Job() {
		this(0);
	}

	/** Check if this is a repeating job */
	public boolean isRepeating() {
		return interval > 0;
	}

	/** Get the delay time before performing the job, in milliseconds */
	public long delay() {
		return nextTime.getTime() - System.currentTimeMillis();
	}

	/** Compute the next time this job will be scheduled.  Warning: the
	 * sort order for the Comparable interface will change. */
	public void computeNextTime() {
		nextTime.setTime(computePastTime() + interval);
	}

	/** Compute the most recently past scheduled time */
	protected long computePastTime() {
		Calendar c = Calendar.getInstance();
		long now = c.getTime().getTime();
		// We need to adjust the offset time by the time zone offset
		// and the DST offset so that daily jobs are performed at the
		// proper hour during DST changes.
		long off = offset - c.get(Calendar.ZONE_OFFSET) -
			c.get(Calendar.DST_OFFSET);
		return (now - off) / interval * interval + off;
	}

	/** Perform the task for this job */
	public void performTask() throws Exception {
		try {
			perform();
		}
		finally {
			complete();
			synchronized(this) {
				n_complete++;
				notify();
			}
		}
	}

	/** Actual "job" to be performed */
	abstract public void perform() throws Exception;

	/** Do this upon completion of the job */
	public void complete() {}

	/** Compare this job with another one */
	public int compareTo(Job other) {
		long c = nextTime.compareTo(other.nextTime);
		if(c == 0)
			c = interval - other.interval;
		if(c == 0)
			c = offset - other.offset;
		if(c == 0)
			c = id - other.id;
		if(c < 0)
			return -1;
		if(c > 0)
			return 1;
		return 0;
	}

	/** Wait for the job to complete */
	public synchronized void waitForCompletion() {
		while(n_complete == 0) {
			try {
				wait(1000);
			}
			catch(InterruptedException e) {
				// keep waiting
			}
		}
	}
}
