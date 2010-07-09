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
import java.util.HashSet;

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
	public synchronized void reset(Calendar s) {
		if(ready && !completed) {
			if(tasks.size() > 0) {
				debug("incomplete");
				debugTasks();
			}
			doComplete();
		}
		stamp = s;
		ready = false;
		completed = false;
	}

	/** Flag to determine whether the completer is ready to test */
	protected boolean ready = false;

	/** Make completer ready to test */
	public synchronized void makeReady() {
		assert ready == false;
		ready = true;
	}

	/** Flag to determine whether the completer has completed since reset */
	protected boolean completed = false;

	/** Set of all tasks to be completed */
	protected final HashSet<String> tasks = new HashSet<String>();

	/** Register a task for the completer to wait for.
	 * @param key Task key.
	 * @return True if the task did not already exist. */
	public synchronized boolean beginTask(String key) {
		assert ready == false;
		boolean t = tasks.add(key);
		if(!t)
			debug("task esists: " + key);
		return t;
	}

	/** Complete a previously registered task.
	 * @param key Task key.
	 * @return True if the task existed. */
	public synchronized boolean completeTask(String key) {
		boolean t = tasks.remove(key);
		if(t) {
			if(shouldComplete())
				doComplete();
		} else
			debug("unknown task: " + key);
		return t;
	}

	/** Test if the compltion job should be performed */
	protected boolean shouldComplete() {
		return ready && tasks.isEmpty() && !completed;
	}

	/** Perform the completion job */
	protected void doComplete() {
		scheduler.addJob(job);
		completed = true;
	}

	/** Debug the completer */
	protected void debug(String status) {
		System.err.println(new Date().toString() + " " + name + " " +
			status + ": " + tasks.size());
	}

	/** Debug the tasks */
	protected void debugTasks() {
		for(String t: tasks)
			System.err.println("\ttask: " + t);
	}
}
