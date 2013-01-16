/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2006-2013  Minnesota Department of Transportation
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

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * ChangeJob is a simple extension/replacement for ChangeListener
 * which passes off a job to a scheduler.
 *
 * @author Douglas Lau
 */
abstract public class ChangeJob extends Job implements ChangeListener {

	/** Scheduler */
	private final Scheduler sched;

	/** Create a new change job */
	public ChangeJob(Scheduler s) {
		sched = s;
	}

	/** State changed (from ChangeListener interface) */
	@Override public void stateChanged(ChangeEvent e) {
		sched.addJob(this);
	}
}
