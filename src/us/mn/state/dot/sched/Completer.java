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
 * Completer keeps track of a set of "tasks", which may be performed on any
 * number of different threads. When all of the tasks have been completed, it
 * adds a completion job to a scheduler.
 *
 * @author Douglas Lau
 */
public class Completer {

	/** Name of this completer */
	protected final String name;

	/** Scheduler to perform the job at completion */
	protected final Scheduler scheduler;

	/** Job for scheduler to perform at completion */
	protected final Job job;

	/** Time stamp of current task */
	protected Calendar stamp;

	/** Get the completer time stamp */
	public Calendar getStamp() {
		return stamp;
	}

	/** Create a new completer.
	 * @param n Name of completer, for debugging.
	 * @param s Scheduler for job on completion.
	 * @param j Job to be performed on completion. */
	public Completer(String n, Scheduler s, Job j) {
		assert j.isRepeating() == false;
		name = n;
		scheduler = s;
		job = j;
	}

	/** Reset the state of the completer */
	public void reset(Calendar s) {
		stamp = s;
		ready = false;
		checked = false;
		total = 0;
		complete = 0;
	}

	/** Flag to determine whether the completer is ready to test */
	protected boolean ready = false;

	/** Make completer ready to test */
	public synchronized void makeReady() {
		assert ready == false;
		ready = true;
		if(isComplete())
			done();
	}

	/** Flag to determine whether completion has been checked yet */
	protected boolean checked = false;

	/** Total count of tasks for this completer */
	protected int total = 0;

	/** Count of completed tasks */
	protected int complete = 0;

	/** Test if all the tasks are complete */
	protected synchronized boolean isComplete() {
		return ready && (total <= complete);
	}

	/** Move the completion counter up */
	public synchronized void up() {
		assert ready == false;
		total++;
	}

	/** Move the completion counter down */
	public synchronized void down() {
		if(isComplete()) {
			System.err.println(name + " CORRUPT @ " + new Date());
			System.err.println("Total tasks: " + total);
			return;
		}
		complete++;
		if(isComplete())
			done();
	}

	/** Done with the completer */
	protected void done() {
		if(checked)
			debug("complete");
		scheduler.addJob(job);
	}

	/** Check if all the tasks are complete */
	public boolean checkComplete() {
		if(!ready)
			return true;
		checked = true;
		boolean c = isComplete();
		if(!c)
			debug("incomplete");
		return c;
	}

	/** Debug the completer */
	protected void debug(String status) {
		System.err.println(new Date().toString() + " " + name + " " +
			status + ": " + complete + ", total: " + total);
	}
}
