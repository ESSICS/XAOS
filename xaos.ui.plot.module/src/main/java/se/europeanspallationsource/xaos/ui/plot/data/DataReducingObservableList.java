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
package se.europeanspallationsource.xaos.ui.plot.data;


import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ModifiableObservableListBase;
import javafx.collections.ObservableList;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart.Data;
import se.europeanspallationsource.xaos.ui.plot.util.AbscissaDataComparator;

import static se.europeanspallationsource.xaos.ui.plot.data.DataReducer.DEFAULT_POINTS_COUNT;
import static se.europeanspallationsource.xaos.ui.plot.data.DataReducer.MIN_TARGET_POINTS_COUNT;


/**
 * An {@link ObservableList} whose data is automatically reduced to the
 * specified {@link #pointsCountProperty() pointsCount}.
 *
 * @param <X> Type of X values.
 * @param <Y> Type of Y values.
 * @author Andreas Schaller (original author).
 * @author Grzegorz Kruk (original author).
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( "ClassWithoutLogger" )
public final class DataReducingObservableList<X extends Number, Y extends Number> extends ModifiableObservableListBase<Data<X, Y>> {

	private final ChangeListener<Number> axisRangeChangeListener = new AxisRangeChangeListener();
	private final ObjectProperty<DataReducer<X, Y>> dataReducer = new SimpleObjectProperty<>(this, "dataReducer", new RamerDouglasPeuckerDataReducer<>());
	private List<Data<X, Y>> reducedData = Collections.emptyList();
	private final ObservableList<Data<X, Y>> sourceData;
	private final ValueAxis<X> xAxis;

	/*
	 * *********************************************************************** *
	 * START OF JAVAFX PROPERTIES *
	 * ***********************************************************************
	 */

	/*
	 * ---- pointsCount --------------------------------------------------------
	 */
	private final IntegerProperty pointsCount = new SimpleIntegerProperty(this, "pointsCount", DEFAULT_POINTS_COUNT) {
		@Override
		protected void invalidated() {
			if ( get() < MIN_TARGET_POINTS_COUNT ) {
				throw new IllegalArgumentException(MessageFormat.format(
					"Target number of points expected to be greater or equal to {0}.",
					MIN_TARGET_POINTS_COUNT
				));
			}
		}
	};

	/**
	 * @return The target number of points for the data reducing algorithm.
	 */
	public final IntegerProperty pointsCountProperty() {
		return pointsCount;
	}

	public int getPointsCount() {
		return pointsCount.get();
	}

	public void setPointsCount( int value ) {
		pointsCount.set(value);
	}

	/*
	 * *********************************************************************** *
	 * END OF JAVAFX PROPERTIES *
	 * ***********************************************************************
	 */

	/**
	 * Creates a new instance of {@link DataReducingObservableList} initializing
	 * the {@link #getSourceData() source data} to an empty list.
	 *
	 * @param xAxis X coordinates axis of the chart displaying the data.
	 * @throws NullPointerException If the X value axis is {@code null}.
	 */
	public DataReducingObservableList( ValueAxis<X> xAxis ) throws NullPointerException {
		this(xAxis, FXCollections.observableArrayList());
	}

	/**
	 * Creates a new instance of {@link DataReducingObservableList}.
	 *
	 * @param xAxis      X coordinates axis of the chart displaying the data.
	 * @param sourceData List containing data to be reduced.
	 * @throws NullPointerException If the X value axis and/or the source data
	 *                              list is {@code null}.
	 */
	public DataReducingObservableList( ValueAxis<X> xAxis, ObservableList<Data<X, Y>> sourceData )
		throws NullPointerException
	{
	
		if ( xAxis == null ) {
			throw new NullPointerException("X ValueAxis cannot be null.");
		} else if ( sourceData == null ) {
			throw new IllegalArgumentException("Source list of data cannot be null.");
		}

		this.xAxis = xAxis;
		this.sourceData = sourceData;

		xAxis.lowerBoundProperty().addListener(axisRangeChangeListener);
		xAxis.upperBoundProperty().addListener(axisRangeChangeListener);

		sourceData.addListener((ListChangeListener<Data<X, Y>>) change -> reduce());

	}

	public ObjectProperty<DataReducer<X, Y>> dataReducerProperty() {
		return dataReducer;
	}

	public IntegerProperty fixedPointsCountProperty() {
		return pointsCount;
	}

	@Override
	public Data<X, Y> get( int index ) {
		return reducedData.get(index);
	}

	public DataReducer<X, Y> getDataReducer() {
		return dataReducer.get();
	}

	@SuppressWarnings( "ReturnOfCollectionOrArrayField" )
	public ObservableList<Data<X, Y>> getSourceData() {
		return sourceData;
	}

	public void setDataReducer( DataReducer<X, Y> value ) {
		dataReducer.set(value);
	}

	@Override
	public int size() {
		return reducedData.size();
	}

	@Override
	protected void doAdd( int index, Data<X, Y> element ) {
		throw new UnsupportedOperationException("Add operation is not allowed. Use getSourceValues().add(...) instead");
	}

	@Override
	protected Data<X, Y> doRemove( int index ) {
		throw new UnsupportedOperationException("Remove operation is not allowed. Use getSourceValues().remove(...) instead");
	}

	@Override
	protected Data<X, Y> doSet( int index, Data<X, Y> element ) {
		throw new UnsupportedOperationException("Set operation is not allowed. Use getSourceValues().set(...) instead");
	}

	private int findFromIndex( AbscissaDataComparator comparator ) {

		@SuppressWarnings( "unchecked" )
		int fromIndex = Collections.binarySearch(sourceData, AbscissaDataComparator.key(xAxis.getLowerBound()), comparator);

		if ( fromIndex < 0 ) {

			fromIndex = -fromIndex - 1;

			if ( fromIndex > 0 ) {
				//	Include previous point to draw a line to it.
				fromIndex--;
			}

		}

		return fromIndex;

	}

	private int findToIndex( AbscissaDataComparator comparator ) {

		@SuppressWarnings( "unchecked" )
		int toIndex = Collections.binarySearch(sourceData, AbscissaDataComparator.key(xAxis.getUpperBound()), comparator);

		if ( toIndex < 0 ) {
			toIndex = -toIndex - 1;
		}

		if ( toIndex < sourceData.size() - 1 ) {
			//	Include next point to draw a line to it.
			toIndex++;
		}

		return toIndex;

	}

	@SuppressWarnings( "ReturnOfCollectionOrArrayField" )
	private List<Data<X, Y>> getXRangeData() {

		if ( isAllDataInXBounds() ) {
			return sourceData;
		}

		AbscissaDataComparator<X> comparator = new AbscissaDataComparator<>(xAxis);

		return sourceData.subList(findFromIndex(comparator), findToIndex(comparator));

	}

	private boolean isAllDataInXBounds() {

		if ( sourceData.isEmpty() ) {
			return true;
		}

		double firstX = sourceData.get(0).getXValue().doubleValue();
		double lastX = sourceData.get(sourceData.size() - 1).getXValue().doubleValue();

		return firstX >= xAxis.getLowerBound() && lastX <= xAxis.getUpperBound();

	}

	private void reduce() {

		beginChange();

		//	Clear previous reduced data list.
		nextRemove(0, reducedData);

		DataReducer<X, Y> reducer = getDataReducer();

		if ( reducer == null ) {
			reducedData = sourceData;
		} else {
			reducedData = reducer.reduce(getXRangeData(), getPointsCount());
		}

		//	Add new reduced data list elements.
		nextAdd(0, reducedData.size());
		endChange();

	}

	/**
	 * Listens to changes in lower and upper bound of the X axis and runs the 
	 * {@link #reduce()} method after {@link #EVENT_DELAY_MILLIS} to schedule
	 * only one reduction in case both lower and upper bound are changed (and
	 * therefore two events are fired).
	 */
	private class AxisRangeChangeListener implements ChangeListener<Number> {

		private static final int EVENT_DELAY_MILLIS = 50;

		private TimerTask task = null;
		private final Timer timer = new Timer();

		@Override
		public void changed( ObservableValue<? extends Number> observable, Number oldValue, Number newValue ) {

			if ( task != null ) {
				task.cancel();
			}

			task = new TimerTask() {
				@Override
				public void run() {
					Platform.runLater(DataReducingObservableList.this::reduce);
				}
			};

			timer.schedule(task, EVENT_DELAY_MILLIS);

		}

	}

}
