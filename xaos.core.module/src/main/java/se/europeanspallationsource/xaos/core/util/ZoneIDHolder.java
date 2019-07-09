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
package se.europeanspallationsource.xaos.core.util;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;

import static java.util.logging.Level.FINE;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.WARNING;


/**
 * Holds the current {@link ZoneId} and provides methods to work with it.
 *
 * @author claudio.rosati@esss.se
 */
public class ZoneIDHolder {

	public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
	private static final Logger LOGGER = Logger.getLogger(ZoneIDHolder.class.getName());

	public static ZoneIDHolder getInstance() {
		return SingletonHolder.INSTANCE;
	}

	/**
	 * @param milliseconds The milliseconds from the Java epoch of
	 *                     1970-01-01T00:00:00Z.
	 * @return A new {@link Date} object built using the given parameter.
	 */
	public static Date toDate( long milliseconds ) {
		return toDate(toInstant(milliseconds));
	}

	/**
	 * @param instant The {@link Instant} object to be converted.
	 * @return A new {@link Date} object built using the given parameter.
	 */
	public static Date toDate( Instant instant ) {
		return Date.from(instant);
	}

	/**
	 * @param milliseconds The milliseconds from the Java epoch of
	 *                     1970-01-01T00:00:00Z.
	 * @return A new {@link Instant} object built using the given parameter.
	 */
	public static Instant toInstant( long milliseconds ) {
		return Instant.ofEpochMilli(milliseconds);
	}

	/**
	 * @param date The {@link Date} object to be converted.
	 * @return A new {@link Instant} object built using the given parameter.
	 */
	public static Instant toInstant( Date date ) {
		return date.toInstant();
	}

	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
	private final ZoneId utcZoneID = ZoneId.of("UTC");
	private ZoneId zoneID = ZoneId.systemDefault();

	private ZoneIDHolder() {
		//	Nothing to do.
	}

	public ZoneId getZoneID() {

		lock.readLock().lock();

		try {
			return zoneID;
		} finally {
			lock.readLock().unlock();
		}

	}

	public void setZoneID( ZoneId zoneID ) {

		if ( zoneID == null ) {
			LogUtils.log(LOGGER, WARNING, "Null zone identifier. Default/previous one used.");
		}

		lock.writeLock().lock();

		try {
			this.zoneID = zoneID;
		} finally {
			lock.writeLock().unlock();
		}

	}

	public String toString( Instant instant ) {

		if ( instant == null ) {
			throw new NullPointerException("instant");
		}

		lock.readLock().lock();

		try {
			return LocalDateTime.ofInstant(instant, zoneID).format(DATE_TIME_FORMATTER);
		} finally {
			lock.readLock().unlock();
		}

	}

	public String toString( Date date ) {
		return toString(date.toInstant());
	}

	public String toString( long milliseconds ) {
		return toString(Instant.ofEpochMilli(milliseconds));
	}

	public String toUTCString( Instant instant ) {

		if ( instant == null ) {
			throw new NullPointerException("instant");
		}

		lock.readLock().lock();

		try {
			return LocalDateTime.ofInstant(instant, utcZoneID).format(DATE_TIME_FORMATTER);
		} finally {
			lock.readLock().unlock();
		}

	}

	public String toUTCString( Date date ) {
		return toUTCString(date.toInstant());
	}

	public String toUTCString( long milliseconds ) {
		return toUTCString(Instant.ofEpochMilli(milliseconds));
	}

	/**
	 * Sets the date of a Linux machine to the given one.
	 * <p>
	 * The following command will be used: {@code date}, {@code hwclock}, and
	 * {@code /usr/local/sbin/rtc_set}.</p>
	 *
	 * @param serverDateTime The new date to be set. The format should be the
	 *                       one accepted by {@code date} command.
	 * @return {@code true} id the operation succeeded.
	 */
	public boolean updateSystemDateAndTime( String serverDateTime ) {

		if ( StringUtils.isEmpty(serverDateTime) ) {
			return false;
		}

		String localDateTime = LocalDateTime.ofInstant(Instant.now(), utcZoneID).format(DATE_TIME_FORMATTER);
		String reducedSDT = serverDateTime.substring(0, serverDateTime.lastIndexOf(':'));
		String reducedLDT = localDateTime.substring(0, serverDateTime.lastIndexOf(':'));

		if ( reducedLDT.equals(reducedSDT) ) {
			return false;
		}

		try {
			LogUtils.log(LOGGER, INFO, "Synchronizing date with server to {0}…", serverDateTime);
			LogUtils.log(LOGGER, FINE, "Executing ''date -s \"{0}\"'\n    {1}", serverDateTime, SystemUtils.execute("date", "-s", serverDateTime));
			LogUtils.log(LOGGER, FINE, "Executing ''hwclock --systohc''\n    {0}", SystemUtils.execute("hwclock", "--systohc"));
			LogUtils.log(LOGGER, FINE, "Executing ''/usr/local/sbin/rtc_set''\n    {0}", SystemUtils.execute("/usr/local/sbin/rtc_set"));
		} catch ( Exception ex ) {
			LogUtils.log(LOGGER, WARNING, "Problems synchronizing date with server: {0}", ex.getMessage());
		}

		return true;

	}

	private static interface SingletonHolder {

		static final ZoneIDHolder INSTANCE = new ZoneIDHolder();

	}

}
