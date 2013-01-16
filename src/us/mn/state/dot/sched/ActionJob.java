/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2013  Minnesota Department of Transportation
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * ActionJob is a simple extension/replacement for ActionListener
 * which passes off a job to a scheduler.
 *
 * @author Douglas Lau
 */
abstract public class ActionJob extends Job implements ActionListener {

	/** Scheduler */
	private final Scheduler sched;

	/** Create a new action job for an alternate scheduler */
	public ActionJob(Scheduler s) {
		sched = s;
	}

	/** Action performed (from ActionListener interface) */
	@Override public void actionPerformed(ActionEvent e) {
		sched.addJob(this);
	}
}
