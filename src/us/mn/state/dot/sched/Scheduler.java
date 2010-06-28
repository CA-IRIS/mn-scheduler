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

	/** Process all scheduled jobs */
	public void run() {
		try {
			performJobs();
		}
		catch(InterruptedException e) {
			handleException(e);
		}
		System.err.println("STOPPING THREAD: " + getName());
	}

	/** Perform jobs as they are scheduled */
	protected void performJobs() throws InterruptedException {
		Job job = waitJob();
		while(!isInterrupted()) {
			performJob(job);
			if(job.isRepeating())
				todo.add(job);
			job = waitJob();
		}
	}

	/** Wait until the next job needs to be performed.
	 * @return Job to be performed. */
	protected synchronized Job waitJob() throws InterruptedException {
		Job job = nextJob();
		long delay = job.delay();
		while(delay > 0) {
			wait(delay);
			// We need to check the next job here in case the job
			// was removed or a new job was added while we were
			// waiting
			job = nextJob();
			delay = job.delay();
		}
		todo.remove(job);
		return job;
	}

	/** Get the next job on the "todo" list */
	protected synchronized Job nextJob() throws InterruptedException {
		while(todo.isEmpty()) {
			wait();
		}
		return todo.first();
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
