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
package se.europeanspallationsource.xaos.ui.plot;


import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import javafx.util.StringConverter;
import org.apache.commons.lang3.StringUtils;
import se.europeanspallationsource.xaos.core.util.LogUtils;

import static java.util.logging.Level.WARNING;


/**
 * @author claudio.rosati@esss.se
 */
public class TimeAxis extends NumberAxis {

	private static final Logger LOGGER = Logger.getLogger(TimeAxis.class.getName());

	private TimeConverter converter = new TimeConverter();

	/**
	 * Create a a non-auto-ranging {@link NumberAxis} with time values as major
	 * tickmark label in default format HOURS:MINUTES:SECONDS.MILLISECONDS.
	 * <p>
	 * Will default to MILLISECONDS from epoch if TIMEFROMEPOCH values are not
	 * given for axis (type long or BigDecimal).</p>
	 */
	public TimeAxis() {

		super();

		setForceZeroInRange(false);

	}

	/**
	 * Create a non-auto-ranging {@link NumberAxis} with the given upper bound,
	 * lower bound and tick unit
	 *
	 * @param lowerBound The lower bound for this axis, i.e. min plottable value.
	 * @param upperBound The upper bound for this axis, i.e. max plottable value.
	 * @param tickUnit   The tick unit, i.e. space between tickmarks.
	 */
	public TimeAxis( double lowerBound, double upperBound, double tickUnit ) {

		super(lowerBound, upperBound, tickUnit);

		setForceZeroInRange(false);

	}

	/**
	 * Create a non-auto-ranging {@link NumberAxis} with the given upper bound,
	 * lower bound, tick unit and axis label.
	 *
	 * @param axisLabel  The name to display for this axis.
	 * @param lowerBound The lower bound for this axis, i.e. min plottable value.
	 * @param upperBound The upper bound for this axis, i.e. max plottable value.
	 * @param tickUnit   The tick unit, i.e. space between tickmarks.
	 */
	public TimeAxis( String axisLabel, double lowerBound, double upperBound, double tickUnit ) {

		super(axisLabel, lowerBound, upperBound, tickUnit);

		setForceZeroInRange(false);

	}

	/**
	 * Create a non-auto-ranging {@link NumberAxis} with the given minimum and
	 * maximum time unit to be used for the tick labels.
	 *
	 * @param maxUnit Maximum unit to be represented on the axis tick labels.
	 * @param minUnit Minimum unit to be represented on the axis tick labels.
	 * @see TimeConverter#TimeConverter(java.util.concurrent.TimeUnit, java.util.concurrent.TimeUnit) 
	 */
	public TimeAxis( TimeUnit maxUnit, TimeUnit minUnit ) {

		this();

		converter = new TimeConverter(minUnit, maxUnit);

	}

	@Override
	protected String getTickMarkLabel( Number value ) {
		return converter.toString(value);
	}

	/**
	 * Converts {@link Number}s to/from "[-][H…HH:][MM:]SS[.sss]" strings. Hours
	 * grater than 23 will never be converted in days, instead they can be
	 * clamped to 0..23 or not.
	 */
	@SuppressWarnings( "PublicInnerClass" )
	public static class TimeConverter extends StringConverter<Number> {

		private static final Logger LOGGER = Logger.getLogger(TimeConverter.class.getName());
		private static final DecimalFormat FORMAT2 = new DecimalFormat("###################00");
		private static final DecimalFormat FORMAT3 = new DecimalFormat("##################000");

		private final TimeUnit maxTimeUnit;
		private final TimeUnit minTimeUnit;

		public TimeConverter() {
			this(TimeUnit.MILLISECONDS, TimeUnit.DAYS);
		}

		/**
		 * Creates a new time converted with the specified minimum and maximum
		 * time units used to convert from {@link Number} to {@link String}.
		 *
		 * @param minTimeUnit From {@link TimeUnit#MILLISECONDS} up to
		 *                    {@link TimeUnit#SECONDS}, clamped otherwise.
		 * @param maxTimeUnit From {@link TimeUnit#DAYS} down to
		 *                    {@link TimeUnit#SECONDS}, clamped otherwise. If
		 *                    {@link TimeUnit#HOURS}, the hours will be clamped
		 *                    to 0..23, otherwise if {@link TimeUnit#DAYS} the
		 *                    total amount of hours will be used.
		 * @throws IllegalArgumentException If {@code minTimeUnit > maxTimeUnit}.
		 * @throws NullPointerException     If one or both parameters are {@code null}.
		 *
		 */
		@SuppressWarnings( "AssignmentToMethodParameter" )
		public TimeConverter( TimeUnit minTimeUnit, TimeUnit maxTimeUnit )
			throws IllegalArgumentException, NullPointerException
		{

			if ( Objects.isNull(minTimeUnit) ) {
				throw new NullPointerException("minTimeUnit");
			} else if ( Objects.isNull(maxTimeUnit) ) {
				throw new NullPointerException("maxTimeUnit");
			} else if ( maxTimeUnit.compareTo(minTimeUnit) < 0 ) {
				throw new IllegalArgumentException("'maxTimeUnit' must be greater than or equal to 'minTimeUnit'.");
			}

			if ( maxTimeUnit.compareTo(TimeUnit.SECONDS) < 0 ) {

				maxTimeUnit = TimeUnit.SECONDS;

				LogUtils.log(LOGGER, WARNING, "'maxTimeUnit' smaller than SECONDS are no allowed.");

			}

			if ( minTimeUnit.compareTo(TimeUnit.SECONDS) > 0 ) {

				minTimeUnit = TimeUnit.SECONDS;

				LogUtils.log(LOGGER, WARNING, "'minTimeUnit' larger than SECONDS are no allowed.");

			} else if ( minTimeUnit.compareTo(TimeUnit.MILLISECONDS) < 0 ) {

				minTimeUnit = TimeUnit.MILLISECONDS;

				LogUtils.log(LOGGER, WARNING, "'minTimeUnit' smaller than MILLISECONDS are no allowed.");

			}

			this.minTimeUnit = minTimeUnit;
			this.maxTimeUnit = maxTimeUnit;

		}

		@Override
		@SuppressWarnings( "AssignmentToMethodParameter" )
		public Number fromString( String value ) {

			if ( StringUtils.isBlank(value) ) {
				//	If the specified value is null, zero-length or blank, return null.
				return null;
			}

			value = value.trim();

			boolean negative = false;

			if ( value.startsWith("-") ) {
				negative = true;
				value = value.substring(1);
			} else if ( value.startsWith("+") ) {
				value = value.substring(1);
			}

			String hours = null;
			String minutes = null;
			String seconds;
			String milliseconds = null;
			int hoursSeparator = value.indexOf(':');
			int minutesSeparator = value.lastIndexOf(':');
			int millisecondsSeparator = value.lastIndexOf('.');

			if ( hoursSeparator != -1 ) {
				//	Both hoursSeparator AND minutesSeparator are different from -1.

				if ( hoursSeparator != minutesSeparator ) {
					hours = value.substring(0, hoursSeparator).trim();
					minutes = value.substring(hoursSeparator + 1, minutesSeparator).trim();
				} else {
					minutes = value.substring(0, minutesSeparator).trim();
				}

				if ( millisecondsSeparator != -1 ) {
					seconds = value.substring(minutesSeparator + 1, millisecondsSeparator).trim();
					milliseconds = value.substring(millisecondsSeparator + 1).trim();
				} else {
					seconds = value.substring(minutesSeparator + 1).trim();
				}

			} else {
				//	Both hoursSeparator AND minutesSeparator are equal to -1.

				if ( millisecondsSeparator != -1 ) {
					seconds = value.substring(0, millisecondsSeparator).trim();
					milliseconds = value.substring(millisecondsSeparator + 1).trim();
				} else {
					seconds = value;
				}

			}

			try {

				long number = 0L;

				if ( StringUtils.isNotBlank(milliseconds) ) {
					number += FORMAT3.parse(milliseconds).longValue();
				}

				if ( StringUtils.isNotBlank(seconds) ) {
					number += 1000L * FORMAT2.parse(seconds).longValue();
				}

				if ( StringUtils.isNotBlank(minutes) ) {
					number += 60L * 1000L * FORMAT2.parse(minutes).longValue();
				}

				if ( StringUtils.isNotBlank(hours) ) {
					number += 60L * 60L * 1000L * FORMAT2.parse(hours).longValue();
				}

				if ( negative ) {
					number = -number;
				}

				return number;

			} catch ( ParseException ex ) {
				throw new RuntimeException(ex);
			}

		}

		@Override
		public String toString( Number value ) {

			if ( value == null ) {
				//	If the specified value is null, return a zero-length String.
				return "";
			}

			long longValue = value.longValue();
			String sign = "";

			if ( longValue < 0 ) {
				sign = "-";
				longValue = -longValue;
			}

			StringBuilder builder = new StringBuilder(sign);
			int milliseconds = (int) ( longValue % 1000 );
			longValue /= 1000;
			int seconds = (int) ( longValue % 60 );
			longValue /= 60;
			int minutes = (int) ( longValue % 60 );
			longValue /= 60;
			long hours = ( maxTimeUnit == TimeUnit.DAYS ) ? longValue : ( longValue % 24 );

			if ( TimeUnit.HOURS.compareTo(maxTimeUnit) <= 0 ) {
				builder.append(FORMAT2.format(hours)).append(':');
			}

			if ( TimeUnit.MINUTES.compareTo(maxTimeUnit) <= 0 ) {
				builder.append(FORMAT2.format(minutes)).append(':');
			}

			if ( TimeUnit.SECONDS.compareTo(maxTimeUnit) <= 0 && TimeUnit.SECONDS.compareTo(minTimeUnit) >= 0 ) {
				builder.append(FORMAT2.format(seconds));
			}

			if ( TimeUnit.MILLISECONDS.compareTo(minTimeUnit) >= 0 ) {
				builder.append('.').append(FORMAT3.format(milliseconds));
			}

			return builder.toString();

		}

	}

}
