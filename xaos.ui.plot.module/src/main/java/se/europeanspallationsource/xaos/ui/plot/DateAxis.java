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


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.chart.Axis;
import javafx.util.Duration;
import javafx.util.StringConverter;


/**
 * An axis that displays date and time values.
 * <p>
 * Tick labels are usually automatically set and calculated depending on the
 * range unless you explicitly
 * {@link #setTickLabelFormatter(StringConverter) set a formatter}. You also
 * have the chance to specify fix lower and upper bounds, otherwise they are
 * calculated by the chart data.</p>
 * <pre>
 * ObservableList&lt;XYChart.Series&lt;Date, Number&gt;&gt; series = FXCollections.observableArrayList();
 *
 * ObservableList&lt;XYChart.Data&lt;Date, Number&gt;&gt; series1Data = FXCollections.observableArrayList();
 * series1Data.add(new XYChart.Data&lt;Date, Number&gt;(new GregorianCalendar(2012, 11, 15).getTime(), 2));
 * series1Data.add(new XYChart.Data&lt;Date, Number&gt;(new GregorianCalendar(2014, 5, 3).getTime(), 4));
 *
 * ObservableList&lt;XYChart.Data&lt;Date, Number&gt;&gt; series2Data = FXCollections.observableArrayList();
 * series2Data.add(new XYChart.Data&lt;Date, Number&gt;(new GregorianCalendar(2014, 0, 13).getTime(), 8));
 * series2Data.add(new XYChart.Data&lt;Date, Number&gt;(new GregorianCalendar(2014, 7, 27).getTime(), 4));
 *
 * series.add(new XYChart.Series&lt;&gt;("Series1", series1Data));
 * series.add(new XYChart.Series&lt;&gt;("Series2", series2Data));
 *
 * NumberAxis numberAxis = new NumberAxis();
 * DateAxis dateAxis = new DateAxis();
 * LineChart&lt;Date, Number&gt; lineChart = new LineChart&lt;&gt;(dateAxis, numberAxis, series);</pre>
 *
 * @author Christian Schudt (original author).
 * @author Diego Cirujano (original author).
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( "ClassWithoutLogger" )
public final class DateAxis extends Axis<Date> {

	private Interval actualInterval = Interval.DECADE;
	private final ChartLayoutAnimator animator = new ChartLayoutAnimator(this);
	private Object currentAnimationID;
	/**
	 * Stores the max date of the list of dates which is used. If
	 * {@link #isAutoRanging()} is {@code true}, the value is used as upper
	 * bound.
	 */
	private Date maxDate;
	/**
	 * Stores the min date of the list of dates which is used. If
	 * {@link #isAutoRanging()} is {@code true}, the value is used as lower
	 * bound.
	 */
	private Date minDate;

	/* *********************************************************************** *
	 * START OF JAVAFX PROPERTIES                                              *
	 * *********************************************************************** */

	/*
	 * ---- currentLowerBound --------------------------------------------------
	 */
	private final LongProperty currentLowerBound = new SimpleLongProperty(this, "currentLowerBound");

	/*
	 * ---- currentUpperBound --------------------------------------------------
	 */
	private final LongProperty currentUpperBound = new SimpleLongProperty(this, "currentUpperBound");

	/*
	 * ---- lowerBound ---------------------------------------------------------
	 */
	private final ObjectProperty<Date> lowerBound = new SimpleObjectProperty<>(this, "lowerBound") {
		@Override
		protected void invalidated() {
			invalidateAndLayout();
		}
	};

	/**
	 * Gets the lower bound of the axis.
	 *
	 * @return The property.
	 * @see #getLowerBound()
	 * @see #setLowerBound(java.util.Date)
	 */
	public ObjectProperty<Date> lowerBoundProperty() {
		return lowerBound;
	}

	/**
	 * Gets the lower bound of the axis.
	 *
	 * @return The lower bound.
	 * @see #lowerBoundProperty()
	 */
	public Date getLowerBound() {
		return lowerBound.get();
	}

	/**
	 * Sets the lower bound of the axis.
	 *
	 * @param date The lower bound date.
	 * @see #lowerBoundProperty()
	 */
	public void setLowerBound( Date date ) {
		lowerBound.set(date);
	}

	/*
	 * ---- tickLabelFormatter -------------------------------------------------
	 */
	private final ObjectProperty<StringConverter<Date>> tickLabelFormatter = new SimpleObjectProperty<>(this, "tickLabelFormatter") {
		@Override
		protected void invalidated() {
			invalidateAndLayout();
		}
	};

	/**
	 * Gets the tick label formatter for the ticks.
	 *
	 * @return The property.
	 */
	public ObjectProperty<StringConverter<Date>> tickLabelFormatterProperty() {
		return tickLabelFormatter;
	}

	/**
	 * Gets the tick label formatter for the ticks.
	 *
	 * @return The converter.
	 */
	public StringConverter<Date> getTickLabelFormatter() {
		return tickLabelFormatter.getValue();
	}

	/**
	 * Sets the tick label formatter for the ticks.
	 *
	 * @param value The converter.
	 */
	public void setTickLabelFormatter( StringConverter<Date> value ) {
		tickLabelFormatter.setValue(value);
	}

	/*
	 * ---- upperBound ---------------------------------------------------------
	 */
	private final ObjectProperty<Date> upperBound = new SimpleObjectProperty<>(this, "upperBound") {
		@Override
		protected void invalidated() {
			invalidateAndLayout();
		}
	};

	/**
	 * Gets the upper bound of the axis.
	 *
	 * @return The property.
	 * @see #getUpperBound() ()
	 * @see #setUpperBound(java.util.Date)
	 */
	public ObjectProperty<Date> upperBoundProperty() {
		return upperBound;
	}

	/**
	 * Gets the upper bound of the axis.
	 *
	 * @return The upper bound.
	 * @see #upperBoundProperty()
	 */
	public Date getUpperBound() {
		return upperBound.get();
	}

	/**
	 * Sets the upper bound of the axis.
	 *
	 * @param date The upper bound date.
	 * @see #upperBoundProperty() ()
	 */
	public void setUpperBound( Date date ) {
		upperBound.set(date);
	}

	/* *********************************************************************** *
	 * END OF JAVAFX PROPERTIES                                                *
	 * *********************************************************************** */

	/**
	 * Constructs a default date axis where the lower and upper bound are
	 * calculated by the data.
	 */
	public DateAxis() {
		//	Nothing to do.
	}

	/**
	 * Constructs a date axis with fix lower and upper bounds.
	 *
	 * @param lowerBound The lower bound.
	 * @param upperBound The upper bound.
	 */
	public DateAxis( Date lowerBound, Date upperBound ) {

		this();

		setAutoRanging(false);
		setLowerBound(lowerBound);
		setUpperBound(upperBound);

	}

	/**
	 * Constructs a date axis with a label and fix lower and upper bounds.
	 *
	 * @param axisLabel  The label for the axis.
	 * @param lowerBound The lower bound.
	 * @param upperBound The upper bound.
	 */
	public DateAxis( String axisLabel, Date lowerBound, Date upperBound ) {

		this(lowerBound, upperBound);

		setLabel(axisLabel);

	}

	/**
	 * @return The actual interval between ticks as {@link Calendar} codes.
	 *         Returned value will be one of the following: {@link Calendar#YEAR},
	 *         {@link Calendar#MONTH}, {@link Calendar#WEEK_OF_YEAR},
	 *         {@link Calendar#DAY_OF_MONTH}, {@link Calendar#HOUR},
	 *         {@link Calendar#MINUTE}, {@link Calendar#SECOND}, and
	 *         {@link Calendar#MILLISECOND}.
	 */
	public int getActualInterval() {
		return actualInterval.interval;
	}

	/**
	 * @return A {@link DateFormat} for the {@link #getActualInterval() actual interval}.
	 */
	public DateFormat getDateFormat() {
		switch ( actualInterval.interval ) {
			case Calendar.YEAR:
				return new SimpleDateFormat("yyyy");
			case Calendar.MONTH:
				return new SimpleDateFormat("yyyy.MM");
			case Calendar.WEEK_OF_YEAR:
			case Calendar.DAY_OF_MONTH:
				return new SimpleDateFormat("yyyy.MM.dd");
			case Calendar.HOUR:
				return new SimpleDateFormat("yyyy.MM.dd HH");
			case Calendar.MINUTE:
				return new SimpleDateFormat("yyyy.MM.dd HH:mm");
			case Calendar.SECOND:
				return new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
			case Calendar.MILLISECOND:
				return new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS");
			default:
				return new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS s");
		}
	}

	@Override
	public double getDisplayPosition( Date date ) {

		final double length = getSide().isHorizontal() ? getWidth() : getHeight();

		//	Get the difference between the max and min date.
		double diff = currentUpperBound.get() - currentLowerBound.get();

		//	Get the actual range of the visible area.
		//	The minimal date should start at the zero position, that's why we
		//	subtract it.
		double range = length - getZeroPosition();

		//	Then get the difference from the actual date to the min date and
		//	divide it by the total difference.
		//	We get a value between 0 and 1, if the date is within the min and
		//	max date.
		double d = ( date.getTime() - currentLowerBound.get() ) / diff;

		//	Multiply this percent value with the range and add the zero offset.
		if ( getSide().isVertical() ) {
			return getHeight() - d * range + getZeroPosition();
		} else {
			return d * range + getZeroPosition();
		}

	}

	@Override
	public Date getValueForDisplay( double displayPosition ) {

		final double length = getSide().isHorizontal() ? getWidth() : getHeight();

		//	Get the difference between the max and min date.
		double diff = currentUpperBound.get() - currentLowerBound.get();

		//	Get the actual range of the visible area.
		//	The minimal date should start at the zero position, that's why we
		//	subtract it.
		double range = length - getZeroPosition();

		if ( getSide().isVertical() ) {
			return new Date((long) ( ( displayPosition - getZeroPosition() - getHeight() ) / -range * diff + currentLowerBound.get() ));
		} else {
			return new Date((long) ( ( displayPosition - getZeroPosition() ) / range * diff + currentLowerBound.get() ));
		}

	}

	@Override
	public double getZeroPosition() {
		return 0;
	}

	@Override
	@SuppressWarnings( "NestedAssignment" )
	public void invalidateRange( List<Date> list ) {

		super.invalidateRange(list);

		Collections.sort(list);

		if ( list.isEmpty() ) {
			minDate = maxDate = new Date();
		} else if ( list.size() == 1 ) {
			minDate = maxDate = list.get(0);
		} else if ( list.size() > 1 ) {
			minDate = list.get(0);
			maxDate = list.get(list.size() - 1);
		}

	}

	@Override
	public boolean isValueOnAxis( Date date ) {
		return date.getTime() > currentLowerBound.get() && date.getTime() < currentUpperBound.get();
	}

	@Override
	public double toNumericValue( Date date ) {
		return date.getTime();
	}

	@Override
	public Date toRealValue( double v ) {
		return new Date((long) v);
	}

	@Override
	protected Object autoRange( double length ) {

		if ( isAutoRanging() ) {
			return new Object[] { minDate, maxDate };
		} else {

			if ( getLowerBound() == null || getUpperBound() == null ) {
				throw new IllegalArgumentException("If autoRanging is false, a lower and upper bound must be set.");
			}

			return getRange();

		}

	}

	@Override
	protected List<Date> calculateTickValues( double v, Object range ) {

		Object[] r = (Object[]) range;
		Date lower = (Date) r[0];
		Date upper = (Date) r[1];
		List<Date> dateList = new ArrayList<>(100);
		List<Date> previousDateList = new ArrayList<>(100);
		Interval previousInterval = Interval.values()[0];
		Calendar calendar = Calendar.getInstance();
		//	The preferred gap which should be between two tick marks.
		double averageTickGap = 100;
		double averageTicks = v / averageTickGap;

		// Starting with the greatest interval, add one of each calendar unit.
		for ( Interval interval : Interval.values() ) {

			//	Reset the calendar.
			calendar.setTime(lower);

			//	Clear the list.
			dateList.clear();
			previousDateList.clear();

			actualInterval = interval;

			//	Loop as long we exceeded the upper bound.
			while ( calendar.getTime().getTime() <= upper.getTime() ) {
				dateList.add(calendar.getTime());
				calendar.add(interval.interval, interval.amount);
			}

			//	Then check the size of the list. If it is greater than the
			//	amount of ticks, take that list.
			if ( dateList.size() > averageTicks ) {

				calendar.setTime(lower);

				//	Recheck if the previous interval is better suited.
				while ( calendar.getTime().getTime() <= upper.getTime() ) {
					previousDateList.add(calendar.getTime());
					calendar.add(previousInterval.interval, previousInterval.amount);
				}

				break;

			}

			previousInterval = interval;

		}

		if ( previousDateList.size() - averageTicks > averageTicks - dateList.size() ) {
			dateList = previousDateList;
			actualInterval = previousInterval;
		}

		//	At last add the upper bound.
		dateList.add(upper);

		List<Date> evenDateList = makeDatesEven(dateList, calendar);

		//	If there are at least three dates, check if the gap between the
		//	lower date and the second date is at least half the gap of the
		//	second and third date.
		//	Do the same for the upper bound.
		//	If gaps between dates are to small, remove one of them.
		//	This can occur, e.g. if the lower bound is 25.12.2013 and years are
		//	shown. Then the next year shown would be 2014 (01.01.2014) which
		//	would be too narrow to 25.12.2013.
		if ( evenDateList.size() > 2 ) {

			Date secondDate = evenDateList.get(1);
			Date thirdDate = evenDateList.get(2);
			Date lastDate = evenDateList.get(dateList.size() - 2);
			Date previousLastDate = evenDateList.get(dateList.size() - 3);

			//	If the second date is too near by the lower bound, remove it.
			if ( secondDate.getTime() - lower.getTime() < ( thirdDate.getTime() - secondDate.getTime() ) / 2 ) {
				evenDateList.remove(secondDate);
			}

			//	If difference from the upper bound to the last date is less than
			//	the half of the difference of the previous two dates, we better
			//	remove the last date, as it comes to close to the upper bound.
			if ( upper.getTime() - lastDate.getTime() < ( lastDate.getTime() - previousLastDate.getTime() ) / 2 ) {
				evenDateList.remove(lastDate);
			}
		}

		return evenDateList;
		
	}

	@Override
	protected Object getRange() {
		return new Object[] { getLowerBound(), getUpperBound() };
	}

	@Override
	protected String getTickMarkLabel( Date date ) {

		StringConverter<Date> converter = getTickLabelFormatter();
		
		if ( converter != null ) {
			return converter.toString(date);
		}

		return getDateFormat().format(date);

	}

	@Override
	protected void layoutChildren() {
		
		if ( !isAutoRanging() ) {
			currentLowerBound.set(getLowerBound().getTime());
			currentUpperBound.set(getUpperBound().getTime());
		}

		super.layoutChildren();

	}

	@Override
	protected void setRange( Object range, boolean animating ) {

		Object[] r = (Object[]) range;
		Date oldLowerBound = getLowerBound();
		Date oldUpperBound = getUpperBound();
		Date lower = (Date) r[0];
		Date upper = (Date) r[1];

		setLowerBound(lower);
		setUpperBound(upper);

		if ( animating ) {
			animator.stop(currentAnimationID);
			currentAnimationID = animator.animate(
				new KeyFrame(
					Duration.ZERO,
					new KeyValue(currentLowerBound, oldLowerBound.getTime()),
					new KeyValue(currentUpperBound, oldUpperBound.getTime())
				),
				new KeyFrame(
					Duration.millis(700),
					new KeyValue(currentLowerBound, lower.getTime()),
					new KeyValue(currentUpperBound, upper.getTime())
				)
			);
		} else {
			currentLowerBound.set(getLowerBound().getTime());
			currentUpperBound.set(getUpperBound().getTime());
		}

	}

	private void invalidateAndLayout() {
		if ( !isAutoRanging() ) {
			invalidateRange();
			requestAxisLayout();
		}
	}

	/**
	 * Makes dates even, in the sense of that years always begin in January,
	 * months always begin on the 1st and days always at midnight.
	 *
	 * @param dates The list of dates.
	 * @return The new list of dates.
	 */
	private List<Date> makeDatesEven( List<Date> dates, Calendar calendar ) {

		//	If the dates contain more dates than just the lower and upper bounds,
		//	make the dates in between even.
		if ( dates.size() > 2 ) {

			List<Date> evenDates = new ArrayList<>(dates.size());

			//	For each interval, modify the date slightly by a few millis, to
			//	make sure they are different days.
			//	This is because Axis stores each value and won't update the tick
			//	labels, if the value is already known.
			//	This happens if you display days and then add a date many years
			//	in the future the tick label will still be displayed as day.
			for ( int i = 0; i < dates.size(); i++ ) {

				calendar.setTime(dates.get(i));

				switch ( actualInterval.interval ) {
					case Calendar.YEAR:
						//	If its not the first or last date (lower and upper
						//	bound), make the year begin with first month and let
						//	the months begin with first day.
						if ( i != 0 && i != dates.size() - 1 ) {
							calendar.set(Calendar.MONTH, 0);
							calendar.set(Calendar.DAY_OF_MONTH, 1);
						}
						calendar.set(Calendar.HOUR_OF_DAY, 0);
						calendar.set(Calendar.MINUTE, 0);
						calendar.set(Calendar.SECOND, 0);
						calendar.set(Calendar.MILLISECOND, 6);
						break;
					case Calendar.MONTH:
						//	If its not the first or last date (lower and upper
						//	bound), make the months begin with first day.
						if ( i != 0 && i != dates.size() - 1 ) {
							calendar.set(Calendar.DAY_OF_MONTH, 1);
						}
						calendar.set(Calendar.HOUR_OF_DAY, 0);
						calendar.set(Calendar.MINUTE, 0);
						calendar.set(Calendar.SECOND, 0);
						calendar.set(Calendar.MILLISECOND, 5);
						break;
					case Calendar.WEEK_OF_YEAR:
						//	Make weeks begin with first day of week?
						calendar.set(Calendar.HOUR_OF_DAY, 0);
						calendar.set(Calendar.MINUTE, 0);
						calendar.set(Calendar.SECOND, 0);
						calendar.set(Calendar.MILLISECOND, 4);
						break;
					case Calendar.DAY_OF_MONTH:
						calendar.set(Calendar.HOUR_OF_DAY, 0);
						calendar.set(Calendar.MINUTE, 0);
						calendar.set(Calendar.SECOND, 0);
						calendar.set(Calendar.MILLISECOND, 3);
						break;
					case Calendar.HOUR:
						if ( i != 0 && i != dates.size() - 1 ) {
							calendar.set(Calendar.MINUTE, 0);
							calendar.set(Calendar.SECOND, 0);
						}
						calendar.set(Calendar.MILLISECOND, 2);
						break;
					case Calendar.MINUTE:
						if ( i != 0 && i != dates.size() - 1 ) {
							calendar.set(Calendar.SECOND, 0);
						}
						calendar.set(Calendar.MILLISECOND, 1);
						break;
					case Calendar.SECOND:
						calendar.set(Calendar.MILLISECOND, 0);
						break;

				}

				evenDates.add(calendar.getTime());

			}

			return evenDates;

		} else {
			return dates;
		}

	}

	/**
	 * The intervals, which are used for the tick labels. Beginning with the
	 * largest interval, the axis tries to calculate the tick values for this
	 * interval. If a smaller interval is better suited for, that one is taken.
	 */
	private enum Interval {

		DECADE(Calendar.YEAR, 10),
		YEAR(Calendar.YEAR, 1),
		MONTH_6(Calendar.MONTH, 6),
		MONTH_3(Calendar.MONTH, 3),
		MONTH_1(Calendar.MONTH, 1),
		WEEK(Calendar.WEEK_OF_YEAR, 1),
		DAY(Calendar.DAY_OF_MONTH, 1),
		HOUR_12(Calendar.HOUR, 12),
		HOUR_6(Calendar.HOUR, 6),
		HOUR_3(Calendar.HOUR, 3),
		HOUR_1(Calendar.HOUR, 1),
		MINUTE_15(Calendar.MINUTE, 15),
		MINUTE_5(Calendar.MINUTE, 5),
		MINUTE_1(Calendar.MINUTE, 1),
		SECOND_15(Calendar.SECOND, 15),
		SECOND_5(Calendar.SECOND, 5),
		SECOND_1(Calendar.SECOND, 1),
		MILLISECOND(Calendar.MILLISECOND, 1);

		final int amount;
		final int interval;

		private Interval( int interval, int amount ) {
			this.interval = interval;
			this.amount = amount;
		}

	}

}
