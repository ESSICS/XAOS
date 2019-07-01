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


import java.sql.Timestamp;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import se.europeanspallationsource.xaos.core.util.LogUtils;

import static java.util.logging.Level.WARNING;


/**
 * @author claudio.rosati@esss.se
 */
public class TimeAxis extends NumberAxis {

	private static final Logger LOGGER = Logger.getLogger(TimeAxis.class.getName());

	private TimeUnit maxTimeUnit = TimeUnit.HOURS;
	private TimeUnit minTimeUnit = TimeUnit.MILLISECONDS;

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
	 */
	public TimeAxis( TimeUnit maxUnit, TimeUnit minUnit ) {

		super();

		if ( Objects.isNull(maxUnit) ) {
			throw new NullPointerException("maxUnit");
		} else if ( Objects.isNull(minUnit) ) {
			throw new NullPointerException("minUnit");
		} else if ( maxUnit.compareTo(minUnit) < 0 ) {
			throw new IllegalArgumentException("'maxUnit' must be greater than 'minUnit'.");
		}

		maxTimeUnit = maxUnit;
		minTimeUnit = minUnit;

		if ( maxTimeUnit.compareTo(TimeUnit.HOURS) > 0 ) {

			maxTimeUnit = TimeUnit.HOURS;

			LogUtils.log(LOGGER, WARNING, "Maximum TimeUnits larger than HOURS are no allowed.");

		} else if ( maxTimeUnit.compareTo(TimeUnit.SECONDS) < 0 ) {

			maxTimeUnit = TimeUnit.SECONDS;

			LogUtils.log(LOGGER, WARNING, "Maximum TimeUnits smaller than SECONDS are no allowed.");

		}

		if ( minTimeUnit.compareTo(TimeUnit.MINUTES) > 0 ) {

			minTimeUnit = TimeUnit.MINUTES;

			LogUtils.log(LOGGER, WARNING, "Minimum TimeUnits larger than MINUTES are no allowed.");

		} else if ( minTimeUnit.compareTo(TimeUnit.MILLISECONDS) < 0 ) {

			minTimeUnit = TimeUnit.MILLISECONDS;

			LogUtils.log(LOGGER, WARNING, "Minimum TimeUnits smaller than MILLISECONDS are no allowed.");

		}

		setForceZeroInRange(false);

	}

	@Override
	protected String getTickMarkLabel( Number value ) {

		String tickLabel;

		if ( ( maxTimeUnit == null ) && ( minTimeUnit == null ) ) {
			tickLabel = new Timestamp(value.longValue()).toString().substring(11);
		} else {
			tickLabel = timeStringParser(value);
		}

		return tickLabel;

	}

	private String timeStringParser( Number value ) {

		String tickLabel = new Timestamp(value.longValue()).toString();
		int upperStringLimit;

		switch ( maxTimeUnit ) {
			case DAYS:
			case HOURS:
				upperStringLimit = 11;
				break;
			case MINUTES:
				upperStringLimit = 14;
				break;
			default:
				upperStringLimit = 17;
				break;
		}

		int lowerStringLimit;

		switch ( minTimeUnit ) {
			case MICROSECONDS:
			case MILLISECONDS:
				lowerStringLimit = 23;
				break;
			case SECONDS:
				lowerStringLimit = 19;
				break;
			default:
				lowerStringLimit = 16;
				break;
		}

		return tickLabel.substring(upperStringLimit, Math.min(lowerStringLimit, tickLabel.length()));

	}

}
