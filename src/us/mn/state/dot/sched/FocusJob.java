/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2008  Minnesota Department of Transportation
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
import javax.swing.JComponent;

/**
 * FocusJob is a simple extension/replacement for FocusListener
 * which passes off a job to a scheduler.
 *
 * @author Douglas Lau
 */
abstract public class FocusJob extends AbstractJob implements FocusListener {

	/** Create a new focus job */
	public FocusJob(JComponent c) {
		c.addFocusListener(this);
	}

	/** Most recent focus event */
	protected FocusEvent event;

	/** Focus gained (from FocusListener interface) */
	public void focusGained(FocusEvent e) {
		event = e;
		addToScheduler();
	}

	/** Focus lost (from FocusListener interface) */
	public void focusLost(FocusEvent e) {
		event = e;
		addToScheduler();
	}

	/** Test if the focus was gained */
	public boolean wasGained() {
		return event.getID() == FocusEvent.FOCUS_GAINED;
	}

	/** Test if the focus was lost */
	public boolean wasLost() {
		return event.getID() == FocusEvent.FOCUS_LOST;
	}
}
