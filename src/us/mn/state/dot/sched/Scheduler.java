/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2008  Minnesota Department of Transportation
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

import java.util.Date;
import java.util.TreeSet;

/**
 * Scheduler for performing jobs in a dedicated thread.
 *
 * @author Douglas Lau
 */
public final class Scheduler extends Thread {

	/** Exception handler */
	static protected ExceptionHandler handler = null;

	/** Set the exception handler */
	static public void setHandler(ExceptionHandler h) {
		handler = h;
	}

	/** Handle an exception */
	static protected void handleException(Exception e) {
		if(handler != null)
			handler.handle(e);
		else
			e.printStackTrace();
	}

	/** Set of scheduled jobs to do */
	protected final TreeSet<Job> todo = new TreeSet<Job>();

	/** Create a new job scheduler */
	public Scheduler() {
		this("Job Scheduler");
	}

	/** Create a new job scheduler */
	public Scheduler(String name) {
		super(name);
		setDaemon(true);
		start();
	}

	/** Get the next job on the "todo" list */
	protected synchronized Job firstJob() {
		while(todo.isEmpty()) {
			try {
				wait();
			}
			catch(InterruptedException e) {
				handleException(e);
			}
		}
		return todo.first();
	}

	/** Get the next job for the scheduler to perform */
	protected synchronized Job nextJob(Job job) {
		if(job != null && job.interval > 0)
			todo.add(job);
		job = firstJob();
		long delay = job.nextTime.getTime() -
			System.currentTimeMillis();
		while(delay > 0) {
			try {
				wait(delay);
			}
			catch(InterruptedException e) {
				handleException(e);
			}
			job = firstJob();
			delay = job.nextTime.getTime() -
				System.currentTimeMillis();
		}
		todo.remove(job);
		return job;
	}

	/** Process all scheduled jobs */
	public void run() {
		Job job = nextJob(null);
		while(!isInterrupted()) {
			try {
				job.performTask();
			}
			catch(Exception e) {
				handleException(e);
			}
			catch(VirtualMachineError e) {
				System.err.println("VIRTUAL MACHINE ERROR");
				e.printStackTrace();
				System.err.println("FATAL: RESTARTING");
				System.exit(1);
			}
			job = nextJob(job);
		}
	}

	/** Add a job for this scheduler to perform */
	public synchronized void addJob(Job job) {
		todo.add(job);
		notify();
	}

	/** Remove a job from this scheduler */
	public synchronized void removeJob(Job job) {
		todo.remove(job);
		notify();
	}
}
