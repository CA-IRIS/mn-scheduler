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

import java.util.Calendar;
import java.util.Date;
import java.util.TreeSet;

/**
 * Scheduler for performing jobs in a dedicated thread.
 *
 * @author Douglas Lau
 */
public final class Scheduler extends Thread {

	/** Scheduler exception handler */
	static public interface ExceptionHandler {
		public void handleException(Exception e);
	}

	/** Set of scheduled jobs to do */
	protected final TreeSet<Job> todo = new TreeSet<Job>();

	/** Scheduler's exception handler */
	protected ExceptionHandler handler;

	/** Create a new job scheduler with the default exception handler */
	public Scheduler(String name) {
		this(name, new ExceptionHandler() {
			public void handleException(Exception e) {
				e.printStackTrace();
			}
		});
	}

	/** Create a new job scheduler with a custom exception handler */
	public Scheduler(ExceptionHandler h) {
		this("Job Scheduler", h);
	}

	/** Create a new job scheduler with a custom exception handler */
	public Scheduler(String name, ExceptionHandler h) {
		super(name);
		handler = h;
		setDaemon(true);
		start();
	}

	/** Set a new exception handler */
	public void setHandler(ExceptionHandler h) {
		handler = h;
	}

	/** Get the next job on the "todo" list */
	protected synchronized Job firstJob() {
		while(todo.isEmpty()) {
			try { wait(); }
			catch(InterruptedException e) {
				handler.handleException(e);
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
			try { wait(delay); }
			catch(InterruptedException e) {
				handler.handleException(e);
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
			try { job.performTask(); }
			catch(Exception e) {
				handler.handleException(e);
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

	/** Add a (runnable) job for this worker thread to perform */
	public synchronized void addJob(Job job) {
		todo.add(job);
		notify();
	}

	/** Remove a (runnable) job from this worker thread */
	public synchronized void removeJob(Job job) {
		try {
			todo.remove(job);
			notify();
		}
		catch(ClassCastException e) {
			System.err.println("NO SUCH JOB");
		}
	}
}
