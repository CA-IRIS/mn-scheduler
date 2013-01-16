/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2005-2013  Minnesota Department of Transportation
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

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * ListSelectionJob is a simple extension/replacement for ListSelectionListener
 * which passes off a job to a scheduler.
 *
 * @author Douglas Lau
 */
abstract public class ListSelectionJob extends Job
	implements ListSelectionListener
{
	/** Scheduler */
	private final Scheduler sched;

	/** Create a new list selection job */
	public ListSelectionJob(Scheduler s) {
		sched = s;
	}

	/** List selection changed (from ListSelectionListener interface) */
	@Override public void valueChanged(ListSelectionEvent e) {
		if(!e.getValueIsAdjusting())
			sched.addJob(this);
	}
}
