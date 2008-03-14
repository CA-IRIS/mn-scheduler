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

/**
 * AbstractJob is a class for assigning an asynchronous job to an AWT event
 *
 * @author Douglas Lau
 */
abstract public class AbstractJob extends Job {

	/** Exception handler for scheduler thread */
	static public final Scheduler.ExceptionHandler HANDLER =
		new Scheduler.ExceptionHandler()
	{
		public void handleException(Exception e) {
//			new ExceptionDialog(e).setVisible(true);
		}
	};

	/** Worker thread */
	static protected final Scheduler WORKER = new Scheduler(HANDLER);

	/** Add a job to the worker scheduler */
	static public void addJob(Job job) {
		WORKER.addJob(job);
	}

	/** Scheduler to run job */
	protected final Scheduler scheduler;

	/** Create a new abstract job */
	public AbstractJob() {
		this(WORKER);
	}

	/** Create a new abstract job */
	public AbstractJob(Scheduler s) {
		super(0);
		scheduler = s;
	}

	/** Add the job to the scheduler */
	public void addToScheduler() {
		scheduler.addJob(this);
	}
}
