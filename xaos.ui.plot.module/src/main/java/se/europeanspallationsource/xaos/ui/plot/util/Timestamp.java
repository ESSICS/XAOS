/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (C) 2018-2019 by European Spallation Source ERIC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package se.europeanspallationsource.xaos.ui.plot.util;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A {@link Comparable} timestamp.
 *
 * @author claudio.rosati@esss.se
 */
public class Timestamp implements Comparable<Timestamp> {

	/**
	 * Format an integer to have nine digits including leading zeros as necessary.
	 */
	static final DecimalFormat NANOSECOND_FORMATTER = new DecimalFormat("000000000");

	/**
	 * Constant for 1000.
	 */
	static final BigDecimal THOUSAND = new BigDecimal(1000);

	/**
	 * Date formatter.
	 */
	static final SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat("MMM d, yyyy HH:mm:ss");

	/**
	 * Convert a {@link java.sql.Timestamp} to {@link BigDecimal} time.
	 *
	 * @param timestamp The {@link java.sql.Timestamp} timestamp.
	 * @return the timestamp as a BigDecimal in seconds since the Java epoch
	 */
	static private BigDecimal toBigDecimal( java.sql.Timestamp timestamp ) {

		BigDecimal seconds = new BigDecimal(timestamp.getTime() / 1000);
		BigDecimal nanoSeconds = new BigDecimal(timestamp.getNanos());

		return seconds.add(nanoSeconds.movePointLeft(9)).setScale(9, RoundingMode.HALF_UP);

	}

	/**
	 * The timestamp information in seconds since the Java epoch.
	 */
	protected final BigDecimal timeStamp;

	/**
	 * Primary constructor from the full precision seconds since the Java epoch.
	 *
	 * @param timeStamp The number of seconds since the Java epoch.
	 */
	public Timestamp( final BigDecimal timeStamp ) {
		this.timeStamp = timeStamp;
	}

	/**
	 * Copy constructor.
	 *
	 * @param source The {@link Timestamp} to be copied.
	 */
	public Timestamp( final Timestamp source ) {
		this.timeStamp = source.timeStamp;
	}

	/**
	 * Construct a {@link Timestamp} from a {@link java.sql.Timestamp}.
	 *
	 * @param timeStamp The SQL timestamp.
	 */
	public Timestamp( final java.sql.Timestamp timeStamp ) {
		this(Timestamp.toBigDecimal(timeStamp));
	}

	/**
	 * Compare this timestamp with another timestamp.
	 *
	 * @param other The timestamp with which to compare this one.
	 * @return 0 if the timestamps are the same, negative if this is earlier
	 *         than the supplied one and positive otherwise.
	 */
	@Override
	public int compareTo( final Timestamp other ) {
		return timeStamp.compareTo(other.timeStamp);
	}

	/**
	 * Determine if the given timestamp equals this one.
	 *
	 * @param timeStamp The {@link Timestamp} to compare with.
	 * @return {@code true} if they are equals and {@code false} if not.
	 */
	@Override
	public boolean equals( final Object timeStamp ) {
		if ( timeStamp != null && timeStamp instanceof Timestamp ) {
			return this.timeStamp.equals(( (Timestamp) timeStamp ).timeStamp);
		} else {
			return false;
		}
	}

	/**
	 * Generate a string representation of this timestamp using the specified
	 * time format for the time format up to seconds. Subsecond time is appended
	 * using a decimal point.
	 *
	 * @param timeFormat {@link DateFormat} used to generate the string up to
	 *                   the subsecond part.
	 * @return Formatted string representation of this timestamp.
	 */
	public String format( final DateFormat timeFormat ) {

		final long nanoseconds = timeStamp.subtract(timeStamp.setScale(0, RoundingMode.DOWN)).movePointRight(9).longValue();

		return timeFormat.format(toDate()) + "." + NANOSECOND_FORMATTER.format(nanoseconds);

	}

	/**
	 * Override the {@code hashcode} as required when overriding equals.
	 * Equality implies equality of the underlying {@code timeStamp} instance
	 * variables.
	 */
	@Override
	public int hashCode() {
		return timeStamp.hashCode();
	}

	/**
	 * Get the time in seconds with full precision since the Java epoch.
	 *
	 * @return The time in seconds since the Java epoch.
	 */
	public BigDecimal toBigDecimal() {
		return timeStamp;
	}

	/**
	 * Get the times tamp as a Java {@link Date}.
	 * Some precision is lost since the Date class only supports millisecond
	 * resolution.
	 *
	 * @return The time stamp as a {@link Date} object.
	 */
	public Date toDate() {
		return new Date(toMilliseconds());
	}

	/**
	 * Get the timestamp in milliseconds relative to the Java epoch.
	 *
	 * @return The time in milliseconds since the Java epoch.
	 */
	public long toMilliseconds() {
		return timeStamp.multiply(THOUSAND).longValue();
	}

	/**
	 * Get the SQL {@link java.sql.Timestamp} equivalent of this instance.
	 *
	 * @return The SQL {@link java.sql.Timestamp} equivalent of this instance.
	 */
	public java.sql.Timestamp toSQLTimestamp() {

		java.sql.Timestamp sqlTimestamp = new java.sql.Timestamp(toMilliseconds());
		int nanoseconds = timeStamp.subtract(timeStamp.setScale(0, RoundingMode.DOWN)).movePointRight(9).intValue();

		sqlTimestamp.setNanos(nanoseconds);

		return sqlTimestamp;

	}

	/**
	 * Get the time in seconds since the Java epoch.
	 *
	 * @return The time in seconds since the Java epoch.
	 */
	public double toSeconds() {
		return timeStamp.doubleValue();
	}

	/**
	 * Get a string representation of the Timestamp.
	 *
	 * @return a string representation of the Timestamp
	 */
	@Override
	public String toString() {
		return format(TIME_FORMATTER);
	}

}
