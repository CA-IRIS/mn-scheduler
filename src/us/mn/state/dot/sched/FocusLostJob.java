/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2008-2013  Minnesota Department of Transportation
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

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * FocusLostJob is a simple extension/replacement for FocusListener
 * which passes off a job to a scheduler.
 *
 * @author Douglas Lau
 */
abstract public class FocusLostJob extends Job implements FocusListener {

	/** Scheduler */
	private final Scheduler sched;

	/** Create a new focus lost job */
	public FocusLostJob(Scheduler s) {
		sched = s;
	}

	/** Focus gained (from FocusListener interface) */
	@Override public void focusGained(FocusEvent e) {
		// We only care about focus lost events
	}

	/** Focus lost (from FocusListener interface) */
	@Override public void focusLost(FocusEvent e) {
		sched.addJob(this);
	}
}
