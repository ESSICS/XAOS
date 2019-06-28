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


import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableDoubleProperty;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.css.converter.SizeConverter;
import javafx.geometry.Dimension2D;
import javafx.scene.chart.ValueAxis;
import javafx.util.Duration;
import javafx.util.StringConverter;
import se.europeanspallationsource.xaos.core.util.LogUtils;

import static java.util.logging.Level.WARNING;


/**
 * A axis class that plots a range of numbers with major tick marks every
 * "tickUnit". You can use any {@link Number} type with this axis, {@link Long},
 * {@link Double}, {@link BigDecimal} etc.
 *
 * @author claudio.rosati@esss.se
 */
public class NumberAxis extends ValueAxis<Number> {

	private static final Logger LOGGER = Logger.getLogger(NumberAxis.class.getName());
	private static final int MAX_TICK_COUNT = 20;

	/**
	 * @return The CssMetaData associated with this class, which may include the
	 *         CssMetaData of its super classes.
	 */
	public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
		return StyleableProperties.STYLEABLES;
	}

	private final Timeline animator = new Timeline();
	//	Not initialized here because mast be done after all fields initialization.
	private final DefaultFormatter defaultFormatter;

	/* *********************************************************************** *
	 * START OF JAVAFX PROPERTIES                                              *
	 * *********************************************************************** */

	/*
	 * ---- autoRangePadding ---------------------------------------------------
	 */
	private DoubleProperty autoRangePadding = new SimpleDoubleProperty(this, "autoRangePadding", 0.01);

	public DoubleProperty autoRangePaddingProperty() {
		return autoRangePadding;
	}

	public double getAutoRangePadding() {
		return autoRangePadding.get();
	}

	public void setAutoRangePadding( double value ) {
		autoRangePadding.set(value);
	}

	/*
	 * ---- currentTickFormat --------------------------------------------------
	 */
	private final StringProperty currentTickFormat = new SimpleStringProperty(this, "currentTickFormat", "");

	/*
	 * ---- forceZeroInRange ---------------------------------------------------
	 */
	private BooleanProperty forceZeroInRange = new SimpleBooleanProperty(this, "forceZeroInRange", true) {
		@Override
		protected void invalidated() {
			if ( isAutoRanging() ) {
				requestAxisLayout();
				invalidateRange();
			}
		}
	};

	/**
	 * When true zero is always included in the visible range. This only has
	 * effect if auto-ranging is on.
	 *
	 * @return A {@link BooleanProperty}.
	 */
	public final BooleanProperty forceZeroInRangeProperty() {
		return forceZeroInRange;
	}

	public final boolean isForceZeroInRange() {
		return forceZeroInRange.getValue();
	}

	public final void setForceZeroInRange( boolean value ) {
		forceZeroInRange.setValue(value);
	}

	/*
	 * ---- scaleBinding -------------------------------------------------------
	 */
	/** Used to update scale property in ValueAxis (that is read-only). */
	private final DoubleProperty scaleBinding = new SimpleDoubleProperty(this, "scaleBinding", getScale()){
		@Override
		protected void invalidated() {
			setScale(get());
		}
	};

	/*
	 * ---- scaleBinding -------------------------------------------------------
	 */
	private DoubleProperty tickUnit = new SimpleStyleableDoubleProperty(StyleableProperties.TICK_UNIT, this, "tickUnit", 5.0) {
		@Override protected void invalidated() {
			if ( !isAutoRanging() ) {
				invalidateRange();
				requestAxisLayout();
			}
		}
	};


	/**
	 * The value between each major tick mark in data units.This is
	 * automatically set if we are auto-ranging.
	 *
	 * @return A {@link DoubleProperty}.
	 */
	public final DoubleProperty tickUnitProperty() {
		return tickUnit;
	}

	public final double getTickUnit() {
		return tickUnit.get();
	}

	public final void setTickUnit( double value ) {
		tickUnit.set(value);
	}

	/* *********************************************************************** *
	 * END OF JAVAFX PROPERTIES                                                *
	 * *********************************************************************** */

	/**
	 * Create a auto-ranging number axis.
	 */
	public NumberAxis() {

		defaultFormatter = new DefaultFormatter(this);

		bindToBounds();

	}

	/**
	 * Create a non-auto-ranging number axis with the given upper bound, lower
	 * bound and tick unit.
	 *
	 * @param lowerBound The lower bound for this axis, i.e. min plottable value.
	 * @param upperBound The upper bound for this axis, i.e. max plottable value.
	 * @param tickUnit   The tick unit, i.e. space between tick marks.
	 */
	public NumberAxis( double lowerBound, double upperBound, double tickUnit ) {

		super(lowerBound, upperBound);

		defaultFormatter = new DefaultFormatter(this);

		setTickUnit(tickUnit);
		bindToBounds();

	}

	/**
	 * Create a non-auto-ranging number axis with the given upper bound, lower
	 * bound and tick unit.
	 *
	 * @param axisLabel  The name to display for this axis.
	 * @param lowerBound The lower bound for this axis, i.e. min plottable value.
	 * @param upperBound The upper bound for this axis, i.e. max plottable value.
	 * @param tickUnit   The tick unit, i.e. space between tick marks.
	 */
	public NumberAxis( String axisLabel, double lowerBound, double upperBound, double tickUnit ) {

		this(lowerBound, upperBound, tickUnit);

		setLabel(axisLabel);

	}

	@Override
	public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
		return getClassCssMetaData();
	}

	/**
	 * Called to set the upper and lower bound and anything else that needs to
	 * be auto-ranged.
	 *
	 * @param minValue  The min data value that needs to be plotted on this axis.
	 * @param maxValue  The max data value that needs to be plotted on this axis.
	 * @param length    The length of the axis in display coordinates.
	 * @param labelSize The approximate average size a label takes along the axis.
	 * @return The calculated range.
	 */
	@Override
	protected Object autoRange( double minValue, double maxValue, double length, double labelSize ) {

		//	Check if we need to force zero into range.
		double min = isForceZeroInRange() && minValue > 0 ? 0 : minValue;
		double max = isForceZeroInRange() && maxValue < 0 ? 0 : maxValue;
		double range = max - min;
		double padding = range * getAutoRangePadding();
		double paddedMin = min - padding;
		double paddedMax = max + padding;

		if ( ( paddedMin < 0 && min >= 0 ) || ( paddedMin > 0 && min <= 0 ) ) {
			//	Padding pushed min above or below zero so clamp to 0.
			paddedMin = 0;
		}

		if ( ( paddedMax < 0 && max >= 0 ) || ( paddedMax > 0 && max <= 0 ) ) {
			//	Padding pushed min above or below zero so clamp to 0.
			paddedMax = 0;
		}

		return computeRange(paddedMin, paddedMax, length, labelSize);

	}

	/**
	 * Calculate a list of the data values for every minor tick mark.
	 *
	 * @return List of data values where to draw minor tick marks.
	 */
	@Override
	protected List<Number> calculateMinorTickMarks() {

		final List<Number> minorTickMarks = new ArrayList<>(50);
		final double lowerBound = getLowerBound();
		final double upperBound = getUpperBound();
		final double tUnit = getTickUnit();
		final double minorUnit = tUnit / Math.max(1, getMinorTickCount());

		if ( tUnit > 0 ) {

			if ( ( ( upperBound - lowerBound ) / minorUnit ) > 10000 ) {

				//	This is a ridiculous amount of major tick marks, something
				//	has probably gone wrong.
				LogUtils.log(
					LOGGER,
					WARNING,
					"Warning we tried to create more than 10000 minor tick marks on a NumberAxis "
				  + "[lower bound: {0}, upper bound: {1}, tick unit: {2}].",
					getLowerBound(),
					getUpperBound(),
					tUnit
				);

				return minorTickMarks;

			}

			final boolean tickUnitIsInteger = Math.rint(tUnit) == tUnit;

			if ( tickUnitIsInteger ) {
				for ( double minor = Math.floor(lowerBound) + minorUnit; minor < Math.ceil(lowerBound); minor += minorUnit ) {
					if ( minor > lowerBound ) {
						minorTickMarks.add(minor);
					}
				}
			}

			double major = tickUnitIsInteger ? Math.ceil(lowerBound) : lowerBound;

			for ( ; major < upperBound; major += tUnit ) {

				final double next = Math.min(major + tUnit, upperBound);

				for ( double minor = major + minorUnit; minor < next; minor += minorUnit ) {
					minorTickMarks.add(minor);
				}

			}

		}

		return minorTickMarks;

	}

	/**
	 * Calculate a list of all the data values for each tick mark in range.
	 *
	 * @param length   The length of the axis in display units.
	 * @param rangeObj A range object returned from {@code autoRange(...)}.
	 * @return A list of tick marks that fit along the axis if it was the given
	 * length.
	 */
	@Override
	protected List<Number> calculateTickValues( double length, Object rangeObj ) {

		Range range = (Range) rangeObj;
		List<Number> tickValues = new ArrayList<>(50);

		if ( range.lowerBound == range.upperBound ) {
			tickValues.add(range.lowerBound);
		} else if ( range.tickUnit <= 0 ) {
			tickValues.add(range.lowerBound);
			tickValues.add(range.upperBound);
		} else if ( range.tickUnit > 0 ) {

			tickValues.add(range.lowerBound);

			if ( ( ( range.upperBound - range.lowerBound ) / range.tickUnit ) > 2000 ) {
				LogUtils.log(
					LOGGER,
					WARNING,
					"Warning we tried to create more than 2000 major tick marks on a NumberAxis "
				  + "[lower bound: {0}, upper bound: {1}, tick unit: {2}].",
					range.lowerBound,
					range.upperBound,
					tickUnit
				);
			} else {
				if ( range.lowerBound + range.tickUnit < range.upperBound ) {

					//	If tickUnit is integer, start with the nearest integer.
					double first = Math.rint(range.tickUnit) == range.tickUnit ? Math.ceil(range.lowerBound) : range.lowerBound + range.tickUnit;

					for ( double major = first; major < range.upperBound; major += range.tickUnit ) {
						if ( !tickValues.contains(major) ) {
							tickValues.add(major);
						}
					}

				}
			}

			tickValues.add(range.upperBound);

		}

		return tickValues;

	}

	/**
	 * Called to get the current axis range.
	 *
	 * @return A range object that can be passed to {@code setRange(...)} and
	 *         {@code calculateTickValues(...)).
	 */
	@Override
	protected Object getRange() {
		return new Range(getLowerBound(), getUpperBound(), getTickUnit(), getScale(), currentTickFormat.get());
	}

	/**
	 * Get the string label name for a tick mark with the given value.
	 *
	 * @param value The value to format into a tick label string.
	 * @return A formatted string for the given value.
	 */
	@Override
	protected String getTickMarkLabel( Number value ) {

		StringConverter<Number> formatter = getTickLabelFormatter();
		String tickLabel;

		if ( formatter == null ) {
			formatter = defaultFormatter;
		}

		tickLabel = formatter.toString(value);

		return tickLabel;

	}

	/**
	 * Measure the size of the label for given tick mark value. This uses the
	 * font that is set for the tick marks.
	 *
	 * @param value    Tick mark value.
	 * @param rangeObj Range to use during calculations.
	 * @return Size of tick mark label for given value.
	 */
	@Override
	protected Dimension2D measureTickMarkSize( Number value, Object rangeObj ) {

		Range range = (Range) rangeObj;

		return measureTickMarkSize(value, getTickLabelRotation(), range.tickFormat);

	}

	/**
	 * Called to set the current axis range to the given range. If
	 * {@code isAnimating()} is {@code true} then this method should animate the
	 * range to the new range.
	 *
	 * @param rangeObj A range object returned from {@code autoRange()}.
	 * @param animate  If {@code true} animate the change in range.
	 */
	@Override
	protected void setRange( Object rangeObj, boolean animate ) {

		Range range = (Range) rangeObj;

		currentTickFormat.set(range.tickFormat);

		final double oldLowerBound = getLowerBound();

		setLowerBound(range.lowerBound);
		setUpperBound(range.upperBound);
		setTickUnit(range.tickUnit);

		if ( animate ) {
			animator.stop();
			animator.getKeyFrames().setAll(
				new KeyFrame(Duration.ZERO,
					new KeyValue(currentLowerBound, oldLowerBound),
					new KeyValue(scaleBinding, getScale())
				),
				new KeyFrame(Duration.millis(700),
					new KeyValue(currentLowerBound, range.lowerBound),
					new KeyValue(scaleBinding, range.scale)
				)
			);
			animator.play();
		} else {
			currentLowerBound.set(range.lowerBound);
			setScale(range.scale);
		}

	}

	private void bindToBounds() {

		ChangeListener<Number> rangeUpdater = ( ob, o, n ) -> {
			if ( !isAutoRanging() ) {
				setRange(computeRange(), false);
			}
		};

		lowerBoundProperty().addListener(rangeUpdater);
		upperBoundProperty().addListener(rangeUpdater);

	}

	private Object computeRange() {

		double length = getSide().isVertical() ? getHeight() : getWidth();
		double labelSize = getTickLabelFont().getSize() * 2;

		return computeRange(getLowerBound(), getUpperBound(), length, labelSize);

	}

	private Range computeRange( double min, double max, double length, double labelSize ) {

		int numOfTickMarks = Math.max((int) Math.floor(length / labelSize), 2);
		double tkUnit = ( max - min ) / numOfTickMarks;
		double tickUnitRounded = 0;
		double minRounded = 0;
		double maxRounded = 0;
		int count = 0;
		double reqLength = Double.MAX_VALUE;
		String formatter = "0.00000000";

		//	Loop till we find a set of ticks that fit length and result in a
		//	total of less than 20 tick marks.
		while ( reqLength > length || count > MAX_TICK_COUNT ) {
			if ( isAutoRanging() ) {

				int exp = (int) Math.floor(Math.log10(tkUnit));
				final double mant = tkUnit / Math.pow(10, exp);
				double ratio = mant;

				if ( exp > 1 ) {
					formatter = "#,##0";
				} else if ( exp == 1 ) {
					formatter = "0";
				} else {

					final boolean ratioHasFrac = Math.rint(ratio) != ratio;
					final StringBuilder formatterB = new StringBuilder("0");
					int n = ratioHasFrac ? Math.abs(exp) + 1 : Math.abs(exp);

					if ( n > 0 ) {
						formatterB.append(".");
					}

					for ( int i = 0; i < n; ++i ) {
						formatterB.append("0");
					}

					formatter = formatterB.toString();

				}

				tickUnitRounded = ratio * Math.pow(10, exp);
				minRounded = Math.floor(min / tickUnitRounded) * tickUnitRounded;
				maxRounded = Math.ceil(max / tickUnitRounded) * tickUnitRounded;

				//	Calculate the required length to display the chosen tick
				//	marks for real, this will handle if there are huge numbers
				//	involved etc or special formatting of the tick mark label
				//	text.
				double maxReqTickGap = 0;
				double last = 0;
				count = 0;

				for ( double major = minRounded; major <= maxRounded; major += tickUnitRounded, count++ ) {

					double size = getSide().isVertical()
								? measureTickMarkSize(major, getTickLabelRotation(), formatter).getHeight()
								: measureTickMarkSize(major, getTickLabelRotation(), formatter).getWidth();

					if ( major == minRounded ) { // first
						last = size / 2;
					} else {
						maxReqTickGap = Math.max(maxReqTickGap, last + 6 + ( size / 2 ));
					}

				}

				reqLength = ( count - 1 ) * maxReqTickGap;
				tkUnit = tickUnitRounded;

				//	Fix for RT-35600 where a massive tick unit was being
				//	selected unnecessarily. There is probably a better solution,
				//	but this works well enough for now.
				if ( numOfTickMarks == 2 && reqLength > length ) {
					break;
				}

				if ( reqLength > length || count > 20 ) {
					tkUnit *= 2; // This is just for the while loop, if there are still too many ticks
				}

			} else {
				
				//	When Autoscale is off there should be no rouding of the limits.
				int exp = (int) Math.floor(Math.log10(tkUnit));
				final double mant = tkUnit / Math.pow(10, exp);

				if ( exp > 1 ) {
					formatter = "#,##0";
				} else if ( exp == 1 ) {
					formatter = "0";
				} else {

					final boolean ratioHasFrac = Math.rint(mant) != mant;
					final StringBuilder formatterB = new StringBuilder("0");
					int n = ratioHasFrac ? Math.abs(exp) + 1 : Math.abs(exp);

					if ( n > 0 ) {
						formatterB.append(".");
					}

					for ( int i = 0; i < n; ++i ) {
						formatterB.append("0");
					}

					formatter = formatterB.toString();

				}

				tickUnitRounded = Math.ceil(mant) * Math.pow(10, exp);
				minRounded = min;
				maxRounded = max;
				
				//	Calculate the required length to display the chosen tick
				//	marks for real, this will handle if there are huge numbers
				//	involved etc or special formatting of the tick mark label
				//	text.
				double maxReqTickGap = 0;
				double last = 0;
				count = 0;

				for ( double major = minRounded; major <= maxRounded; major += tickUnitRounded, count++ ) {

					double size = getSide().isVertical()
								? measureTickMarkSize(major, getTickLabelRotation(), formatter).getHeight()
								: measureTickMarkSize(major, getTickLabelRotation(), formatter).getWidth();

					if ( major == minRounded ) { // first
						last = size / 2;
					} else {
						maxReqTickGap = Math.max(maxReqTickGap, last + 6 + ( size / 2 ));
					}

				}

				reqLength = ( count - 1 ) * maxReqTickGap;
				tkUnit = tickUnitRounded;

				//	Fix for RT-35600 where a massive tick unit was being
				//	selected unnecessarily. There is probably a better solution,
				//	but this works well enough for now.
				if ( numOfTickMarks == 2 && reqLength > length ) {
					break;
				}

				if ( reqLength > length || count > 20 ) {
					tkUnit *= 2; // This is just for the while loop, if there are still too many ticks
				}
				
			}
		}

		// calculate new scale
		final double newScale = calculateNewScale(length, minRounded, maxRounded);

		return new Range(minRounded, maxRounded, tickUnitRounded, newScale, formatter);

	}

	/**
	 * Measure the size of the label for given tick mark value. This uses the 
	 * font that is set for the tick marks.
	 *
	 * @param value        Tick mark value.
	 * @param rotation     The text rotation.
	 * @param numFormatter The number formatter.
	 * @return Size of tick mark label for given value.
	 */
	@SuppressWarnings( "null" )
	private Dimension2D measureTickMarkSize( Number value, double rotation, String numFormatter ) {

		String labelText;
		StringConverter<Number> formatter = getTickLabelFormatter();

		if ( formatter == null ) {
			formatter = defaultFormatter;
		}

		if ( formatter instanceof DefaultFormatter ) {
			labelText = ( (DefaultFormatter) formatter ).toString(value, numFormatter);
		} else {
			labelText = formatter.toString(value);
		}
		
		return measureTickMarkLabelSize(labelText, rotation);

	}

	/**
	 * Default number formatter for NumberAxis, this stays in sync with 
	 * auto-ranging and formats values appropriately.You can wrap this formatter
	 * to add prefixes or suffixes;
	 */
	@SuppressWarnings( "PublicInnerClass" )
	public static class DefaultFormatter extends StringConverter<Number> {

		private DecimalFormat formatter;
		private String prefix = null;
		private String suffix = null;

		/**
		 * Construct a DefaultFormatter for the given NumberAxis.
		 *
		 * @param axis The axis to format tick marks for.
		 */
		@SuppressWarnings( "AccessingNonPublicFieldOfAnotherObject" )
		public DefaultFormatter( final NumberAxis axis ) {

			formatter = axis.isAutoRanging()
					  ? new DecimalFormat(axis.currentTickFormat.get())
					  : new DecimalFormat();

			axis.currentTickFormat.addListener(( observable, oldValue, newValue ) -> {
				formatter = new DecimalFormat(axis.currentTickFormat.get());
			});

		}

		/**
		 * Construct a DefaultFormatter for the given NumberAxis with a prefix
		 * and/or suffix.
		 *
		 * @param axis   The axis to format tick marks for.
		 * @param prefix The prefix to append to the start of formatted number,
		 *               can be null if not needed.
		 * @param suffix The suffix to append to the end of formatted number,
		 *               can be null if not needed.
		 */
		public DefaultFormatter( NumberAxis axis, String prefix, String suffix ) {

			this(axis);

			this.prefix = prefix;
			this.suffix = suffix;

		}

		/**
		 * Converts the string provided into a Number defined by the this
		 * converter.Format of the string and type of the resulting object is
		 * defined by this converter.
		 *
		 * @param string The string containing a number to be converted.
		 * @return A Number representation of the string passed in.
		 * @see StringConverter#toString
		 */
		@Override
		public Number fromString( String string ) {
			try {

				int prefixLength = ( prefix == null ) ? 0 : prefix.length();
				int suffixLength = ( suffix == null ) ? 0 : suffix.length();

				return formatter.parse(string.substring(prefixLength, string.length() - suffixLength));

			} catch ( ParseException e ) {
				return null;
			}
		}

		/**
		 * Converts the object provided into its string foFormat of the
		 * returned string is defined by this converter.rer.
		 *
		 * @param number The number to be converted.
		 * @return A string representation of the object passed in.
		 * @see StringConverter#toString
		 */
		@Override 
		public String toString( Number number ) {
			return toString(number, formatter);
		}

		private String toString( Number object, String numFormatter ) {
			if ( numFormatter == null || numFormatter.isEmpty() ) {
				return toString(object, formatter);
			} else {
				return toString(object, new DecimalFormat(numFormatter));
			}
		}

		private String toString( Number object, DecimalFormat formatter ) {
			if ( prefix != null && suffix != null ) {
				return prefix + formatter.format(object) + suffix;
			} else if ( prefix != null ) {
				return prefix + formatter.format(object);
			} else if ( suffix != null ) {
				return formatter.format(object) + suffix;
			} else {
				return formatter.format(object);
			}
		}

	}

	/**
	 * Sets axis as time axis with major tick marks as (Default)
	 * HOUR:MINUTE:SECOND:MILLISECOND.
	 */
	private static class Range {

		final double lowerBound;
		final double scale;
		final String tickFormat;
		final double tickUnit;
		final double upperBound;

		Range( double lowerBound, double upperBound, double tickUnit, double scale, String tickFormat ) {
			this.lowerBound = lowerBound;
			this.upperBound = upperBound;
			this.tickUnit = tickUnit;
			this.scale = scale;
			this.tickFormat = tickFormat;
		}

	}

	private static class StyleableProperties {

		private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
		private static final CssMetaData<NumberAxis, Number> TICK_UNIT = new CssMetaData<NumberAxis, Number>("-fx-tick-unit", SizeConverter.getInstance(), 5.0) {

			@Override
			public StyleableProperty<Number> getStyleableProperty( NumberAxis n ) {
				return (StyleableProperty<Number>) n.tickUnitProperty();
			}

			@Override
			@SuppressWarnings( "AccessingNonPublicFieldOfAnotherObject" )
			public boolean isSettable( NumberAxis n ) {
				return n.tickUnit == null || !n.tickUnit.isBound();
			}

		};

		static {

			final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<>(ValueAxis.getClassCssMetaData());

			styleables.add(TICK_UNIT);

			STYLEABLES = Collections.unmodifiableList(styleables);

		}

		private StyleableProperties() {
			//	Nothing to do.
		}

	}

}
