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
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.XYChart;
import javafx.scene.shape.Line;


/**
 * Holds the values of the error bars.
 *
 * @author natalia.milas@esss.se
 * @param <X> The X type of the series.
 * @param <Y> The Y type of the series.
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class ErrorSeries<X, Y> {

	private ObservableList<ErrorData<X, Y>> displayedErrorData = FXCollections.observableArrayList();
	private StringProperty name;
	private ObjectProperty<Node> node = new SimpleObjectProperty<>(this, "node");
	private Integer seriesRef = null;

	/**
	 * Construct a empty series.
	 */
	public ErrorSeries() {
		//	Nothing to do.
	}

	/**
	 * Constructs the series and populates it with the given
	 * {@link ObservableList} data.
	 *
	 * @param data {@link ObservableList} of {@link ErrorData}.
	 */
	public ErrorSeries( ObservableList<ErrorData<X, Y>> data ) {
		setData(data);
		data.forEach(item -> item.setErrorSeries(this));
	}

	/**
	 * Constructs a named series and populates it with the given
	 * {@link ObservableList} data.
	 *
	 * @param name The name of the series.
	 * @param data {@link ObservableList} of {@link ErrorData}.
	 */
	public ErrorSeries( String name, ObservableList<ErrorData<X, Y>> data ) {
		this(data);
		setName(name);
	}

	/**
	 * Adds the given value to the series.
	 *
	 * @param value The value to be added to the series.
	 */
	public void addErrorData( ErrorData<X, Y> value ) {
		displayedErrorData.add(value);
	}

	/**
	 * @return The displayed error data.
	 */
	@SuppressWarnings( "ReturnOfCollectionOrArrayField" )
	public final ObservableList<ErrorData<X, Y>> getData() {
		return displayedErrorData;
	}

	/**
	 * @return The series' name.
	 */
	public final String getName() {
		return name.get();
	}

	/**
	 * Returns the node to display for this series. This is created by the chart
	 * if it uses nodes to represent the whole series. For example line chart
	 * uses this for the line but scatter chart does not use it. This node will
	 * be set as soon as the series is added to the chart. You can then get it
	 * to add mouse listeners etc.
	 *
	 * @return The node to display for this series.
	 */
	public final Node getNode() {
		return node.get();
	}

	/**
	 * @return The series number to attach error bar.
	 */
	public Integer getSeriesRef() {
		return seriesRef;
	}

	public final StringProperty nameProperty() {
		return name;
	}

	public final ObjectProperty<Node> nodeProperty() {
		return node;
	}

	@SuppressWarnings( "AssignmentToCollectionOrArrayFieldFromParameter" )
	public final void setData( ObservableList<ErrorData<X, Y>> data ) {
		displayedErrorData = data;
	}

	/**
	 * Sets the series' name.
	 *
	 * @param name The new series' name.
	 */
	public final void setName( String name ) {
		this.name.set(name);
	}

	/**
	 * Sets the node to display for this series.
	 *
	 * @param value The new node to display.
	 * @see #getNode()
	 */
	public final void setNode( Node value ) {
		node.set(value);
	}

	/**
	 * Sets the series number to attach error bar.
	 *
	 * @param seriesRef The new series number.
	 */
	public void setSeriesRef( Integer seriesRef ) {
		this.seriesRef = seriesRef;
	}

	/**
	 * Returns a string representation of this {@code Series} object.
	 *
	 * @return a string representation of this {@code Series} object.
	 */
	@Override
	public String toString() {
		return "Series [" + getName() + "]";
	}

	int getDataSize() {
		return displayedErrorData.size();
	}

	ErrorData<X, Y> getItem( int i ) {
		return displayedErrorData.get(i);
	}

	int getItemIndex( XYChart.Data<X, Y> item ) {
		return displayedErrorData.indexOf(item);
	}

	@SuppressWarnings( "PublicInnerClass" )
	public static final class ErrorData<X, Y> {

		/** Data point connected to the error bar. */
		private XYChart.Data<X, Y> errorData = new XYChart.Data<>();
		/** The series this data belongs to. */
		private ErrorSeries<X, Y> errorSeries;
		/** X coordinates of the error start and end. */
		private List<X> xErrorBar = new ArrayList<>(2);
		private final Line xErrorLine = new Line();
		/** Y coordinates of the error start and end. */
		private List<Y> yErrorBar = new ArrayList<>(2);
		private final Line yErrorLine = new Line();

		public ErrorData() {
			//	Nothing to do.
		}

		public ErrorData( XYChart.Data<X, Y> data ) {
			errorData = data;
		}

		public ErrorData( XYChart.Data<X, Y> data, double yError ) {
			this(data);
			List catList = new ArrayList<>();
			List<Number> numberList = new ArrayList<>();

			if ( ( !( data.getXValue() instanceof Number ) || ( data.getXValue() instanceof BigDecimal ) || ( data.getXValue() instanceof Long ) ) && ( data.getYValue() instanceof Number ) ) {
				numberList.add(0, Double.parseDouble(errorData.getYValue().toString()) * ( 1 - yError ));
				numberList.add(1, Double.parseDouble(errorData.getYValue().toString()) * ( 1 + yError ));
				catList.add(0, errorData.getXValue());
				catList.add(1, errorData.getXValue());
				setErrors(catList, numberList);
			} else if ( ( !( data.getYValue() instanceof Number ) ) && ( data.getXValue() instanceof Number ) ) {
				numberList.add(0, Double.parseDouble(errorData.getXValue().toString()) * ( 1 - yError ));
				numberList.add(1, Double.parseDouble(errorData.getXValue().toString()) * ( 1 + yError ));
				catList.add(0, errorData.getYValue());
				catList.add(1, errorData.getYValue());
				setErrors(numberList, catList);
			} else {
				throw new IllegalArgumentException("Axis type incorrect, at least one of the Axes should be a Number axis. If both are the error val should be specified for both x and y. ");
			};
		}

		;


        public ErrorData( XYChart.Data<X, Y> data, double[] xError, double[] yError ) {
			this(data);
			List<Number> xErrorList = new ArrayList<>();
			List<Number> yErrorList = new ArrayList<>();
			xErrorList.add(0, Double.parseDouble(errorData.getXValue().toString()) + xError[0]);
			xErrorList.add(1, Double.parseDouble(errorData.getXValue().toString()) - xError[1]);
			yErrorList.add(0, Double.parseDouble(errorData.getYValue().toString()) + yError[0]);
			yErrorList.add(1, Double.parseDouble(errorData.getYValue().toString()) - yError[1]);
			setErrors(xErrorList, yErrorList);
		}

		public ErrorData( XYChart.Data<X, Y> data, double xError, double yError ) {
			this(data);
			List<Number> xErrorList = new ArrayList<>();
			List<Number> yErrorList = new ArrayList<>();
			xErrorList.add(0, Double.parseDouble(errorData.getXValue().toString()) * ( 1 - xError ));
			xErrorList.add(1, Double.parseDouble(errorData.getXValue().toString()) * ( 1 + xError ));
			yErrorList.add(0, Double.parseDouble(errorData.getYValue().toString()) * ( 1 - yError ));
			yErrorList.add(1, Double.parseDouble(errorData.getYValue().toString()) * ( 1 + yError ));
			setErrors(xErrorList, yErrorList);
		}


		public XYChart.Data<?, ?> getDataPoint() {
			return errorData;
		}


		@SuppressWarnings( { "unchecked", "rawtypes" } )
		public List<?> getXErrorBar() {
			return xErrorBar;
		}

		public Line getXErrorLine() {
			return xErrorLine;
		}


		public List<?> getYErrorBar() {
			return yErrorBar;
		}

		public Line getYErrorLine() {
			return yErrorLine;
		}


		public Boolean isData( XYChart.Data<?, ?> data ) {
			if ( errorData.getXValue().equals(data.getXValue()) && errorData.getYValue().equals(data.getYValue()) ) {
				return true;
			} else {
				return false;
			}
		}

		public void resetLine() {
			xErrorLine.setStartX(0);
			xErrorLine.setEndX(0);
			xErrorLine.setStartY(0);
			xErrorLine.setEndY(0);

			yErrorLine.setStartX(0);
			yErrorLine.setEndX(0);
			yErrorLine.setStartY(0);
			yErrorLine.setEndY(0);
		}
		public void setDataPoint( XYChart.Data<X, Y> data ) {
			this.errorData = data;
		}
		public void setErrors( List<?> xError, List<Number> yError ) {
			xErrorBar.add(0, (X) xError.get(0));
			xErrorBar.add(1, (X) xError.get(1));
			yErrorBar.add(0, (Y) yError.get(0));
			yErrorBar.add(1, (Y) yError.get(1));
		}
		public void setXErrorBar( List<X> xErrorBar ) {
			this.xErrorBar = xErrorBar;
		}
		public void setYErrorBar( List<Y> yErrorBar ) {
			this.yErrorBar = yErrorBar;
		}
		void setErrorSeries( ErrorSeries<X, Y> series ) {
			this.errorSeries = series;
		}
	}

}
