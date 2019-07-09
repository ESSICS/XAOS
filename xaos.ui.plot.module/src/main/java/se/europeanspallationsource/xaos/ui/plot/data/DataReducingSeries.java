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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import se.europeanspallationsource.xaos.core.util.ThreadUtils;
import se.europeanspallationsource.xaos.ui.plot.util.AbscissaDataComparator;

import static se.europeanspallationsource.xaos.ui.plot.data.DataReducer.DEFAULT_POINTS_COUNT;
import static se.europeanspallationsource.xaos.ui.plot.data.DataReducer.MIN_TARGET_POINTS_COUNT;


/**
 * A data series capable of reducing data.
 *
 * @param <X> Type of X values.
 * @param <Y> Type of Y values.
 * @author Grzegorz Kruk (original author).
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( "ClassWithoutLogger" )
public final class DataReducingSeries<X extends Number, Y extends Number> {

	private static final double DEFAULT_CHART_WIDTH_TO_DATA_POINTS_RATIO = 2;
	private final BooleanProperty autoPointsCount = new SimpleBooleanProperty(this, "autoPointsCount", true);
	private final ChangeListener<Number> axisRangeChangeListener = new ChangeListener<Number>() {

		private AtomicBoolean dataReductionNeeded = new AtomicBoolean(false);
		private ExecutorService executor = Executors.newSingleThreadExecutor();

		@Override
		public void changed( ObservableValue<? extends Number> observable, Number oldValue, Number newValue ) {
			dataReductionNeeded.set(true);
			executor.submit(() -> {
				//	Temporary hack to avoid reducing data twice (lowerBound and upperBound update).
				ThreadUtils.sleep(5);
				if ( dataReductionNeeded.compareAndSet(true, false) ) {
					Platform.runLater(DataReducingSeries.this::reduceData);
				}
			});
		}

	};
	private final ChangeListener<Number> axisWidthChangeListener = ( ob, o, n ) -> {
		if ( isAutoPointsCount() ) {
			Platform.runLater(DataReducingSeries.this::reduceData);
		}
	};
	private final DoubleProperty chartWidthToDataPointsRatio = new SimpleDoubleProperty(this, "chartWidthToDataPointsRatio", DEFAULT_CHART_WIDTH_TO_DATA_POINTS_RATIO);
	private final ObjectProperty<ObservableList<Data<X, Y>>> data = new SimpleObjectProperty<>(this, "data") {

		private ObservableList<Data<X, Y>> old;

		@Override
		protected void invalidated() {

			ObservableList<Data<X, Y>> current = get();

			if ( old != null ) {
				old.removeListener(dataChangeListener);
			}

			if ( current != null ) {
				current.addListener(dataChangeListener);
			}

			dataChangeListener.onChanged(null);

			old = current;

		}

	};
	private final ListChangeListener<Data<X, Y>> dataChangeListener = e -> reduceData();
	private final ObjectProperty<DataReducer<X, Y>> dataReducer = new SimpleObjectProperty<>(DataReducingSeries.this, "dataReducer", new RamerDouglasPeuckerDataReducer<>());
	private final IntegerProperty fixedPointsCount = new SimpleIntegerProperty(this, "fixedPointsCount", DEFAULT_POINTS_COUNT);
	private final Series<X, Y> target = new Series<>();

	/**
	 * Construct a empty series.
	 */
	public DataReducingSeries() {
		this(FXCollections.<Data<X, Y>>observableArrayList());
	}

	/**
	 * Constructs a series and populates it with the given
	 * {@link ObservableList} data.
	 *
	 * @param data {@link ObservableList} of {@link Data} points.
	 */
	public DataReducingSeries( ObservableList<Data<X, Y>> data ) {
		this(null, data);
	}

	/**
	 * Constructs a named series and populates it with the given
	 * {@link ObservableList} data.
	 *
	 * @param name The name for the series.
	 * @param data {@link ObservableList} of {@link Data} points.
	 */
	public DataReducingSeries( String name, ObservableList<Data<X, Y>> data ) {
		setName(name);
		setData(data);
		bindListeners();
	}

	public BooleanProperty autoPointsCountProperty() {
		return autoPointsCount;
	}

	public DoubleProperty chartWidthToDataPointsRatioProperty() {
		return chartWidthToDataPointsRatio;
	}

	public ObjectProperty<ObservableList<Data<X, Y>>> dataProperty() {
		return data;
	}

	public ObjectProperty<DataReducer<X, Y>> dataReducerProperty() {
		return dataReducer;
	}

	public IntegerProperty fixedPointsCountProperty() {
		return fixedPointsCount;
	}

	public double getChartWidthToDataPointsRatio() {
		return chartWidthToDataPointsRatio.get();
	}

	public ObservableList<Data<X, Y>> getData() {
		return data.getValue();
	}

	public DataReducer<X, Y> getDataReducer() {
		return dataReducer.get();
	}

	public int getFixedPointsCount() {
		return fixedPointsCount.get();
	}

	public String getName() {
		return target.getName();
	}

	public Series<X, Y> getSeries() {
		return this.target;
	}

	public boolean isAutoPointsCount() {
		return autoPointsCount.get();
	}

	public StringProperty nameProperty() {
		return target.nameProperty();
	}

	public void setAutoPointsCount( boolean value ) {
		autoPointsCount.set(value);
	}

	public void setChartWidthToDataPointsRatio( double value ) {

		if ( !Double.isFinite(value) || value <= 0 ) {
			throw new IllegalArgumentException(MessageFormat.format(
				"The ratio value must be a finite positive number [{0}].",
				value
			));
		}

		chartWidthToDataPointsRatio.set(value);

	}

	public void setData( ObservableList<Data<X, Y>> value ) {
		data.setValue(value);
	}

	public void setDataReducer( DataReducer<X, Y> value ) {
		dataReducer.set(value);
	}

	public void setFixedPointsCount( int value ) {

		if ( value < MIN_TARGET_POINTS_COUNT ) {
			throw new IllegalArgumentException(MessageFormat.format(
				"Target number of points expected to be greater or equal to {0}.",
				MIN_TARGET_POINTS_COUNT
			));
		}

		fixedPointsCount.set(value);

	}

	public void setName( String value ) {
		target.setName(value);
	}

	private void bindAxis( XYChart<X, Y> chart ) {

		ValueAxis<X> xAxis = getXAxis(chart);

		xAxis.lowerBoundProperty().addListener(axisRangeChangeListener);
		xAxis.upperBoundProperty().addListener(axisRangeChangeListener);
		xAxis.widthProperty().addListener(axisWidthChangeListener);

	}

	private void bindListeners() {
		target.chartProperty().addListener(( ob, o, n ) -> chartChanged(o, n));
		autoPointsCountProperty().addListener(e -> reduceData());
		chartWidthToDataPointsRatioProperty().addListener(( ob, o, n ) -> {
			if ( isAutoPointsCount() ) {
				reduceData();
			}
		});
		fixedPointsCountProperty().addListener(( ob, o, n ) -> {
			if ( !isAutoPointsCount() ) {
				reduceData();
			}
		});
	}

	private void chartChanged( XYChart<X, Y> oldChart, XYChart<X, Y> newChart ) {

		if ( oldChart != null ) {
			unbindAxis(oldChart);
		}

		if ( newChart != null ) {

			if ( !( newChart.getXAxis() instanceof ValueAxis<?> ) ) {
				throw new IllegalArgumentException("Only instances of ValueAxis are supported as X axis.");
			}

			bindAxis(newChart);

		}

	}

	private int getPointsCount() {

		if ( isAutoPointsCount() ) {

			ValueAxis<X> xAxis = getXAxis(getSeries().getChart());

			return ( xAxis == null )
				? getFixedPointsCount()
				: (int) ( xAxis.getWidth() / getChartWidthToDataPointsRatio() );

		} else {
			return getFixedPointsCount();
		}

	}

	private ValueAxis<X> getXAxis( XYChart<X, Y> chart ) {
		return ( chart == null ) ? null : (ValueAxis<X>) chart.getXAxis();
	}

	private List<Data<X, Y>> getXRangeData() {

		ValueAxis<X> xAxis = getXAxis(getSeries().getChart());
		List<Data<X, Y>> originalData = getData();

		return ( xAxis == null )
			? originalData
			: subList(originalData, xAxis.getLowerBound(), xAxis.getUpperBound());

	}

	private boolean isAllDataInRange( List<Data<X, Y>> input, double lowerBound, double upperBound ) {

		if ( input.isEmpty() ) {
			return true;
		}

		double firstX = input.get(0).getXValue().doubleValue();
		double lastX = input.get(input.size() - 1).getXValue().doubleValue();

		return firstX >= lowerBound && lastX <= upperBound;

	}

	private List<Data<X, Y>> recreateData( List<Data<X, Y>> reduced ) {
		return reduced.stream()
			.map(d -> {

				Data<X, Y> newData = new Data<>(d.getXValue(), d.getYValue(), d.getExtraValue());

				newData.setNode(d.getNode());

				return newData;

			})
			.collect(Collectors.toList());
	}

	private void reduceData() {

		DataReducer<X, Y> reducer = getDataReducer();

		if ( reducer == null ) {
			target.getData().setAll(recreateData(getXRangeData()));
		} else {

			List<Data<X, Y>> reduced = reducer.reduce(getXRangeData(), getPointsCount());

			target.getData().setAll(recreateData(reduced));

		}

	}

	@SuppressWarnings( "unchecked" )
	private List<Data<X, Y>> subList( List<Data<X, Y>> input, double lowerBound, double upperBound ) {

		if ( isAllDataInRange(input, lowerBound, upperBound) ) {
			return input;
		}

		ValueAxis<X> xAxis = getXAxis(getSeries().getChart());
		AbscissaDataComparator comparator = new AbscissaDataComparator(xAxis);
		int fromIndex = Collections.binarySearch(input, AbscissaDataComparator.key(lowerBound), comparator);

		if ( fromIndex < 0 ) {

			fromIndex = -fromIndex - 1;

			if ( fromIndex > 0 ) {
				fromIndex--;
			}

		}

		int toIndex = Collections.binarySearch(input, AbscissaDataComparator.key(upperBound), comparator);

		if ( toIndex < 0 ) {
			toIndex = -toIndex - 1;
		}

		if ( toIndex < input.size() - 1 ) {
			toIndex++;
		}

		return input.subList(fromIndex, toIndex);

	}

	private void unbindAxis( XYChart<X, Y> chart ) {

		ValueAxis<X> xAxis = getXAxis(chart);

		xAxis.lowerBoundProperty().removeListener(axisRangeChangeListener);
		xAxis.upperBoundProperty().removeListener(axisRangeChangeListener);
		xAxis.widthProperty().removeListener(axisWidthChangeListener);

	}

}
