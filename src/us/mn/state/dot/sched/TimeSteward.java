/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2010  Minnesota Department of Transportation
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

/**
 * The time steward provides static methods dealing with time sources.
 * To use this class correctly, there are several standard library methods
 * which must be avoided (or used only carefully).
 *
 * @see java.lang.System.currentTimeMillis()
 * @see java.lang.Object.wait(long)
 * @see java.lang.Object.wait(long, int)
 * @see java.lang.Thread.join(long)
 * @see java.lang.Thread.join(long, int)
 * @see java.lang.Thread.sleep(long)
 * @see java.lang.Thread.sleep(long, int)
 * @see java.net.DatagramSocket.setSoTimeout(int)
 * @see java.net.Socket.connect(java.net.SocketAddress, int)
 * @see java.net.Socket.setSoTimeout(int)
 * @see java.net.URLConnection.setConnectTimeout(int)
 * @see java.net.URLConnection.setReadTimeout(int)
 * @see java.util.Calendar.getInstance()
 * @see java.util.Date.Date()
 *
 * @author Douglas Lau
 */
public final class TimeSteward {

	/** Time source */
	static protected TimeSource source = new SystemTimeSource();

	/** Don't allow instantiation */
	private TimeSteward() { }

	/** Set the time source */
	static public void setTimeSource(TimeSource ts) {
		assert ts != null;
		source = ts;
	}

	/** Get the current time */
	static public long currentTimeMillis() {
		return source.currentTimeMillis();
	}

	/** Get a date instance from the time source */
	static public Date getDateInstance() {
		return new Date(currentTimeMillis());
	}

	/** Get a calendar instance from the time source */
	static public Calendar getCalendarInstance() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(currentTimeMillis());
		return cal;
	}
}
