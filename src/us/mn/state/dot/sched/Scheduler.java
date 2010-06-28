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

import java.util.Date;
import java.util.TreeSet;

/**
 * Scheduler for performing jobs in a dedicated thread.
 *
 * @author Douglas Lau
 */
public final class Scheduler extends Thread {

	/** Default exception handler */
	static protected ExceptionHandler HANDLER;

	/** Set the default exception handler */
	static public void setHandler(ExceptionHandler h) {
		HANDLER = h;
	}

	/** Exception handler */
	protected final ExceptionHandler handler;

	/** Handle an exception */
	protected void handleException(Exception e) {
		if(handler != null)
			handler.handle(e);
		else if(HANDLER != null)
			HANDLER.handle(e);
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
		this(name, null);
	}

	/** Create a new job scheduler */
	public Scheduler(String name, ExceptionHandler h) {
		super(name);
		handler = h;
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
	protected synchronized Job nextJob() {
		Job job = firstJob();
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
		Job job = nextJob();
		while(!isInterrupted()) {
			performJob(job);
			if(job.isRepeating())
				todo.add(job);
			job = nextJob();
		}
	}

	/** Perform a job */
	protected void performJob(Job job) {
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
