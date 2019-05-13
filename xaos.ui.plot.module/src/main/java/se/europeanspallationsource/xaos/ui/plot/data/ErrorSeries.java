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


import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.XYChart;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.VLineTo;


/**
 * Holds the values of the error bars.
 *
 * @param <X> The X type of the series.
 * @param <Y> The Y type of the series.
 * @author natalia.milas@esss.se
 * @author claudio.rosati@esss.se
 * @css.class {@code chart-error-paths}
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class ErrorSeries<X, Y> {

	private ObservableList<ErrorData<X, Y>> displayedErrorData = FXCollections.observableArrayList();
	private Integer seriesRef = null;

	/*
	 * *********************************************************************** *
	 * START OF JAVAFX PROPERTIES *
	 * ***********************************************************************
	 */

	/*
	 * ---- name ---------------------------------------------------------------
	 */
	private final StringProperty name = new SimpleStringProperty(this, "name");

	public final StringProperty nameProperty() {
		return name;
	}

	/**
	 * @return The series' name.
	 */
	public final String getName() {
		return nameProperty().get();
	}

	/**
	 * Sets the series' name.
	 *
	 * @param name The new series' name.
	 */
	public final void setName( String name ) {
		nameProperty().set(name);
	}

	/*
	 * ---- node ---------------------------------------------------------------
	 */
	private final ObjectProperty<Node> node = new SimpleObjectProperty<>(this, "node");

	public final ObjectProperty<Node> nodeProperty() {
		return node;
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
		return nodeProperty().get();
	}

	/**
	 * Sets the node to display for this series.
	 *
	 * @param value The new node to display.
	 * @see #getNode()
	 */
	public final void setNode( Node value ) {
		nodeProperty().set(value);
	}

	/*
	 * *********************************************************************** *
	 * END OF JAVAFX PROPERTIES *
	 * ***********************************************************************
	 */

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
	 * @return The series number to attach error bar.
	 */
	public Integer getSeriesRef() {
		return seriesRef;
	}

	@SuppressWarnings( "AssignmentToCollectionOrArrayFieldFromParameter" )
	public final void setData( ObservableList<ErrorData<X, Y>> data ) {
		displayedErrorData = data;
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

		/**
		 * The size in pixels of the closing segment.
		 * <pre>
		 *   -----
		 *     |
		 *     |
		 *     x
		 *     |
		 *     |
		 *   -----
		 *   &lt;---&gt; CLOSING_SEGMENT_SIZE
		 * </pre>
		 */
		private static final double CLOSING_SEGMENT_SIZE = 5;

		/**
		 * Data point connected to the error bar.
		 */
		private XYChart.Data<X, Y> dataPoint = new XYChart.Data<>();

		/**
		 * X coordinates of the error start and end.
		 */
		private double xError = Double.NaN;
		private final Path xErrorPath = new Path();

		/**
		 * Y coordinates of the error start and end.
		 */
		private double yError = Double.NaN;
		private final Path yErrorPath = new Path();

		/**
		 * Creates an error data element without no X error data.
		 *
		 * @param data   The data point around which the error exists.
		 * @param yError The Y error around the data point:
		 * <pre>
		 * ---
		 *  |
		 *  |
		 *  y
		 *  |
		 *  |
		 * ---</pre>
		 *               {@link Double#NaN} is used to indicate no Y error value.
		 */
		public ErrorData( XYChart.Data<X, Y> data, double yError ) {
			this(data, Double.NaN, yError);
		}

		/**
		 * Creates an error data element.
		 *
		 * @param data   The data point around which the error exists.
		 * @param xError The X error around the data point: {@code |---x---|}.
		 *               {@link Double#NaN} is used to indicate no X error value.
		 * @param yError The Y error around the data point:
		 * <pre>
		 * ---
		 *  |
		 *  |
		 *  y
		 *  |
		 *  |
		 * ---</pre>
		 *               {@link Double#NaN} is used to indicate no Y error value.
		 */
		public ErrorData( XYChart.Data<X, Y> data, double xError, double yError ) {

			this(data);

			this.xError = xError;
			this.yError = yError;

		}

		/**
		 * Initializes the data point and the 2 {@link Path}s.
		 *
		 * @param data The data point.
		 */
		protected ErrorData( XYChart.Data<X, Y> data ) {

			dataPoint = data;

			xErrorPath.getElements().add(new MoveTo(-0.5, -CLOSING_SEGMENT_SIZE / 2));
			xErrorPath.getElements().add(new VLineTo(CLOSING_SEGMENT_SIZE));
			xErrorPath.getElements().add(new MoveTo(-0.5, 0.0));
			xErrorPath.getElements().add(new HLineTo(1.0));
			xErrorPath.getElements().add(new MoveTo(0.5, -CLOSING_SEGMENT_SIZE / 2));
			xErrorPath.getElements().add(new VLineTo(CLOSING_SEGMENT_SIZE));
			xErrorPath.getStyleClass().add("chart-error-paths");

			yErrorPath.getElements().add(new MoveTo(-CLOSING_SEGMENT_SIZE / 2, -0.5));
			yErrorPath.getElements().add(new HLineTo(CLOSING_SEGMENT_SIZE));
			yErrorPath.getElements().add(new MoveTo(0.0, -0.5));
			yErrorPath.getElements().add(new VLineTo(1.0));
			yErrorPath.getElements().add(new MoveTo(-CLOSING_SEGMENT_SIZE / 2, 0.5));
			yErrorPath.getElements().add(new VLineTo(CLOSING_SEGMENT_SIZE));
			yErrorPath.getStyleClass().add("chart-error-paths");

		}

		/**
		 * @return The data point around which the error exists.
		 */
		public XYChart.Data<?, ?> getDataPoint() {
			return dataPoint;
		}

		/**
		 * @return The X error around the data point: {@code |---x---|}.
		 *         {@link Double#NaN} is used to indicate no X error value.
		 */
		public double getXError() {
			return xError;
		}

		/**
		 * @return The {@link Path} used to draw the X error: {@code |---x---|}.
		 */
		public Path getXErrorPath() {
			return xErrorPath;
		}

		/**
		 * @return The Y error around the data point:
		 * <pre>
		 * ---
		 *  |
		 *  |
		 *  y
		 *  |
		 *  |
		 * ---</pre>
		 *         {@link Double#NaN} is used to indicate no Y error value.
		 */
		public double getYError() {
			return yError;
		}

		/**
		 * @return The {@link Path} used to draw the Y error:
		 * <pre>
		 * ---
		 *  |
		 *  |
		 *  y
		 *  |
		 *  |
		 * ---</pre>
		 */
		public Path getYErrorPath() {
			return yErrorPath;
		}

		/**
		 * @return Return {@code true} if the X error around the data point is
		 *         not {@link Double#NaN}.
		 */
		public boolean isXErrorValid() {
			return !Double.isNaN(xError);
		}

		/**
		 * @return Return {@code true} if the Y error around the data point is
		 *         not {@link Double#NaN}.
		 */
		public boolean isYErrorValid() {
			return !Double.isNaN(yError);
		}

		/**
		 * @param data The new data point around which the error exists.
		 */
		public void setDataPoint( XYChart.Data<X, Y> data ) {
			this.dataPoint = data;
		}

		/**
		 * Sets the X and Y errors.
		 *
		 * @param xError The X error around the data point: {@code |---x---|}.
		 *               {@link Double#NaN} is used to indicate no X error value.
		 * @param yError The Y error around the data point:
		 * <pre>
		 * ---
		 *  |
		 *  |
		 *  y
		 *  |
		 *  |
		 * ---</pre>
		 *               {@link Double#NaN} is used to indicate no Y error value.
		 */
		public void setErrors( double xError, double yError ) {
			this.xError = xError;
			this.yError = yError;
		}

		/**
		 * @param xError The X error around the data point: {@code |---x---|}.
		 *               {@link Double#NaN} is used to indicate no X error value.
		 */
		public void setXError( double xError ) {
			this.xError = xError;
		}

		/**
		 * @param yError The Y error around the data point:
		 * <pre>
		 * ---
		 *  |
		 *  |
		 *  y
		 *  |
		 *  |
		 * ---</pre>
		 *               {@link Double#NaN} is used to indicate no Y error value.
		 */
		public void setYErrorBar( double yError ) {
			this.yError = yError;
		}

	}

}
