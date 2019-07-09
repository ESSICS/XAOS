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


import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.chart.ValueAxis;
import javafx.util.Duration;
import se.europeanspallationsource.xaos.core.util.LogUtils;

import static java.util.logging.Level.SEVERE;
import static java.util.logging.Level.WARNING;


/**
 * A logarithmic axis implementation for JavaFX charts.
 *
 * @author claudio.rosati@esss.se
 */
public class LogAxis extends ValueAxis<Number> {

	/**
	 * The time of animation in ms
	 */
	private static final double ANIMATION_TIME = 2000;
	private static final Logger LOGGER = Logger.getLogger(LogAxis.class.getName());

	private double dataMaxValue;
	private double dataMinValue;
	private final DoubleProperty logLowerBound = new SimpleDoubleProperty();
	private final DoubleProperty logUpperBound = new SimpleDoubleProperty();
	private final Timeline lowerRangeTimeline = new Timeline();
	private final Timeline upperRangeTimeline = new Timeline();

	public LogAxis() {

		super();

		bindLogBoundsToDefaultBounds();

	}

	public LogAxis( double lowerBound, double upperBound ) {

		super(lowerBound, upperBound);

		try {
			validateBounds(lowerBound, upperBound);
			bindLogBoundsToDefaultBounds();
		} catch ( IllegalLogarithmicRangeException ex ) {
			LogUtils.log(LOGGER, SEVERE, ex);
		}

	}

	public LogAxis( String axisLabel, double lowerBound, double upperBound ) {

		this(lowerBound, upperBound);

		setLabel(axisLabel);

	}

	@Override
	public double getDisplayPosition( Number value ) {

		double delta = logUpperBound.get() - logLowerBound.get();
		double deltaV = Math.log10(value.doubleValue()) - logLowerBound.get();

		if ( getSide().isVertical() ) {
			return ( 1. - ( ( deltaV ) / delta ) ) * getHeight();
		} else {
			return ( ( deltaV ) / delta ) * getWidth();
		}

	}

	@Override
	public Number getValueForDisplay( double displayPosition ) {

		double delta = logUpperBound.get() - logLowerBound.get();

		if ( getSide().isVertical() ) {
			return Math.pow(10, ( ( ( displayPosition - getHeight() ) / -getHeight() ) * delta ) + logLowerBound.get());
		} else {
			return Math.pow(10, ( ( ( displayPosition / getWidth() ) * delta ) + logLowerBound.get() ));
		}

	}

	/**
	 * Called when data has changed and the range may not be valid any more.
	 * This is only called by the chart if {@code isAutoRanging()} returns
	 * {@code true}. If we are auto ranging it will cause layout to be requested
	 * and auto ranging to happen on next layout pass.
	 *
	 * @param data The current set of all data that needs to be plotted on this
	 *             axis.
	 */
	@Override
	public void invalidateRange( List<Number> data ) {

		if ( data.isEmpty() ) {
			dataMaxValue = getUpperBound();
			dataMinValue = getLowerBound();
		} else {
			dataMinValue = Double.MAX_VALUE;
			dataMaxValue = -Double.MAX_VALUE;
		}

		data.stream()
			.filter(dataValue -> dataValue.doubleValue() > 0 )
			.forEachOrdered(( dataValue ) -> {
				dataMinValue = Math.min(dataMinValue, dataValue.doubleValue());
				dataMaxValue = Math.max(dataMaxValue, dataValue.doubleValue());
			});

		super.invalidateRange(data);

	}

	@Override
	@SuppressWarnings( "AssignmentToMethodParameter" )
	protected Object autoRange( double minValue, double maxValue, double length, double labelSize ) {

		if ( !validateBounds(minValue, maxValue) ) {
			minValue = dataMinValue;
			maxValue = dataMaxValue;
		}

		return new Double[] { minValue, maxValue };

	}

	@Override
	protected List<Number> calculateMinorTickMarks() {

		Number[] range = getRange();
		List<Number> minorTickMarksPositions = new ArrayList<>(50);

		if ( range != null ) {

			Number lowerBound = range[0];
			Number upperBound = range[1];
			double logLowerBoundNow = Math.log10(lowerBound.doubleValue());
			double logUpperBoundNow = Math.log10(upperBound.doubleValue());
			int minorTickMarkCount = getMinorTickCount();

			for ( double i = logLowerBoundNow; i <= logUpperBoundNow; i += 1 ) {
				for ( double j = 0; j <= 9; j += ( 1. / minorTickMarkCount ) ) {
					minorTickMarksPositions.add(j * Math.pow(10, i));
				}
			}

		}

		return minorTickMarksPositions;

	}

	@Override
	protected List<Number> calculateTickValues( double length, Object range ) {
		
		List<Number> tickPositions = new ArrayList<>(50);

		if ( range != null ) {

			Number lowerBound = ( (Number[]) range )[0];
			Number upperBound = ( (Number[]) range )[1];
			double logLowerBoundNow = Math.log10(lowerBound.doubleValue());
			double logUpperBoundNow = Math.log10(upperBound.doubleValue());

			for ( double i = logLowerBoundNow; i <= logUpperBoundNow; i += 1 ) {
				for ( double j = 1; j <= 9; j++ ) {
					tickPositions.add(j * Math.pow(10, i));
				}
			}

		}

		return tickPositions;

	}

	@Override
	protected Number[] getRange() {
		return new Number[] { lowerBoundProperty().get(), upperBoundProperty().get() };
	}

	@Override
	protected String getTickMarkLabel( Number value ) {

		NumberFormat formatter = NumberFormat.getInstance();

		formatter.setMaximumIntegerDigits(6);
		formatter.setMinimumIntegerDigits(1);

		return formatter.format(value);

	}

	@Override
	protected void setRange( Object range, boolean animate ) {

		if ( range != null ) {

			Number lowerBound = ( (Number[]) range )[0];
			Number upperBound = ( (Number[]) range )[1];

			if ( animate ) {
				try {

					lowerRangeTimeline.getKeyFrames().clear();
					upperRangeTimeline.getKeyFrames().clear();

					lowerRangeTimeline.getKeyFrames().addAll(
						new KeyFrame(Duration.ZERO, new KeyValue(lowerBoundProperty(), lowerBoundProperty().get())),
						new KeyFrame(new Duration(ANIMATION_TIME), new KeyValue(lowerBoundProperty(), lowerBound.doubleValue()))
					);
					upperRangeTimeline.getKeyFrames().addAll(
						new KeyFrame(Duration.ZERO, new KeyValue(upperBoundProperty(), upperBoundProperty().get())),
						new KeyFrame(new Duration(ANIMATION_TIME), new KeyValue(upperBoundProperty(), upperBound.doubleValue()))
					);

					lowerRangeTimeline.play();
					upperRangeTimeline.play();

				} catch ( Exception e ) {
					lowerBoundProperty().set(lowerBound.doubleValue());
					upperBoundProperty().set(upperBound.doubleValue());
				}
			}

			lowerBoundProperty().set(lowerBound.doubleValue());
			upperBoundProperty().set(upperBound.doubleValue());

		}

	}

	/**
	 * Bind our logarithmic bounds with the super class bounds, consider the
	 * base 10 logarithmic scale.
	 */
	private void bindLogBoundsToDefaultBounds() {

		logLowerBound.bind(new DoubleBinding() {

			{	//	Instance initializer.
				super.bind(lowerBoundProperty());
			}

			@Override
			protected double computeValue() {

				if ( lowerBoundProperty().get() <= 0 ) {
					setAutoRanging(false);
					lowerBoundProperty().set(1);
				}

				return Math.log10(lowerBoundProperty().get());

			}

		});

		logUpperBound.bind(new DoubleBinding() {

			{	//	Instance initializer.
				super.bind(upperBoundProperty());
			}

			@Override
			protected double computeValue() {

				if ( upperBoundProperty().get() <= 0 || upperBoundProperty().get() <= lowerBoundProperty().get() ) {
					setAutoRanging(false);
					upperBoundProperty().set(lowerBoundProperty().get() + 10);
				}

				return Math.log10(upperBoundProperty().get());
			}

		});

	}

	/**
	 * Validate the bounds by throwing an exception if the values are not 
	 * conform to the mathematics log interval: [0,Double.MAX_VALUE].
	 *
	 * @param lowerBound
	 * @param upperBound
	 */
	private Boolean validateBounds( double lowerBound, double upperBound ) {

		if ( lowerBound <= 0 || upperBound <= 0 || lowerBound >= upperBound ) {

			LogUtils.log(
				LOGGER, 
				WARNING, 
				"WARNING: The logarithmic data range should be within (0,Double.MAX_VALUE] and the lowerBound should be less than the upperBound."
			);

			return false;

		} else {
			return true;
		}

	}

	/**
	 * Exception to be thrown when a bound value isn't supported by the
	 * logarithmic axis.
	 *
	 * @author Kevin Senechal mailto: kevin.senechal@dooapp.com
	 *
	 */
	@SuppressWarnings( { "serial", "PublicInnerClass" } )
	public class IllegalLogarithmicRangeException extends RuntimeException {
		public IllegalLogarithmicRangeException( String message ) {
			super(message);
		}
	}

}
