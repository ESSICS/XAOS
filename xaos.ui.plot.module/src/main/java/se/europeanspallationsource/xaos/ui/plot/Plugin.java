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


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.chart.Axis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.input.MouseEvent;
import org.apache.commons.lang3.Validate;


/**
 * Base for {@link XYChart} plugins.
 *
 * @author Grzegorz Kruk (original author).
 * @author claudio.rosati@esss.se
 */
public abstract class Plugin {

	/**
	 * Return the X {@link ValueAxis} for the given {@code chart}.
	 *
	 * @param chart The {@link Chart} whose X {@link ValueAxis} must be returned.
	 * @return The X {@link ValueAxis} for the given {@code chart} or {@code null}.
	 */
	public static final ValueAxis<?> getXValueAxis( Chart chart ) {
		if ( chart instanceof XYChart<?, ?> ) {
			if ( ( (XYChart<?, ?>) chart ).getXAxis() instanceof CategoryAxis ) {
				return new NumberAxis();
			} else {
				return (ValueAxis<?>) ( (XYChart<?, ?>) chart ).getXAxis();
			}
		} else if ( chart instanceof DensityChartFX<?, ?> ) {
			return (ValueAxis<?>) ( (DensityChartFX<?, ?>) chart ).getXAxis();
		} else {
			return null;
		}
	}

	/**
	 * Return the Y {@link ValueAxis} for the given {@code chart}.
	 *
	 * @param chart The {@link Chart} whose Y {@link ValueAxis} must be returned.
	 * @return The Y {@link ValueAxis} for the given {@code chart} or {@code null}.
	 */
	public static final ValueAxis<?> getYValueAxis( Chart chart ) {
		if ( chart instanceof XYChart<?, ?> ) {
			if ( ( (XYChart<?, ?>) chart ).getYAxis() instanceof CategoryAxis ) {
				return new NumberAxis();
			} else {
				return (ValueAxis<?>) ( (XYChart<?, ?>) chart ).getYAxis();
			}
		} else if ( chart instanceof DensityChartFX<?, ?> ) {
			return (ValueAxis<?>) ( (DensityChartFX<?, ?>) chart ).getYAxis();
		} else {
			return null;
		}
	}

	private Chart chart;
	private final ObservableList<Node> plotChildren = FXCollections.observableArrayList();

	/**
	 * Returns {@link Chart} associated with this plugin or {@code null} if this
	 * plugin hasn't been added to any chart.
	 *
	 * @return The associated chart or {@code null}.
	 */
	public final Chart getChart() {
		return chart;
	}

	/**
	 * @return The display name of this plugin.
	 */
	public abstract String getName();

	/**
	 * @return The X {@link ValueAxis} for this plugin's chart or {@code null}.
	 */
	public final ValueAxis<?> getXValueAxis() {
		return getXValueAxis(getChart());
	}

	/**
	 * @return The Y {@link ValueAxis} for this plugin's chart or {@code null}.
	 */
	public final ValueAxis<?> getYValueAxis() {
		return getYValueAxis(getChart());
	}

	/**
	 * @param <X>    Type of X values.
	 * @param <Y>    Type of Y values.
	 * @param series The data {@link Series} whose visibility is queried.
	 * @return {@code true} if the series' {@link Node} is visible.
	 * @throws NullPointerException If {@code series} or its {@link Node} are
	 *                              {code null}.
	 */
	public <X, Y> boolean isSeriesVisible( Series<X, Y> series ) {

		Validate.notNull(series, "Null 'series' parameter.");

		Node node = series.getNode();

		if ( node != null ) {
			return node.isVisible();
		} else {
			return series.getData().parallelStream().anyMatch(d -> {

				Node n = d.getNode();

				return ( n != null ) && n.isVisible();

			});
		}

	}

	/**
	 * Called by charts when the visibility of a series changed.
	 *
	 * @param <X>     Type of X values.
	 * @param <Y>     Type of Y values.
	 * @param chart   The chart where the {@code series} visibility change occurred.
	 * @param series  The {@link Series} whose visibility changed.
	 * @param index   Index of (@code series} inside the {@code chart}'s data.
	 * @param visible The current visibility state of the given {@code series}.
	 */
	@SuppressWarnings( "NoopMethodInAbstractClass" )
	public <X, Y> void seriesVisibilityUpdated( Chart chart, Series<X, Y> series, int index, boolean visible ) {
		//	Nothing done in the default implementation.
	}

	/**
	 * Called when the plugin is added to a chart.
	 * <p>
	 * Can be overridden by concrete implementations to register e.g. mouse
	 * listeners or perform any other initializations.</p>
	 *
	 * @param newChart The chart to which the plugin has been added.
	 */
	@SuppressWarnings( "NoopMethodInAbstractClass" )
	protected void chartConnected( Chart newChart ) {
		//	Nothing done in the default implementation.
	}

	/**
	 * Called when the plugin has been removed from the chart.
	 * <p>
	 * Can be overridden by concrete implementations to unbind listeners and
	 * perform any other cleanup operations.</p>
	 *
	 * @param oldChart The chart from which the plugin has been removed.
	 */
	@SuppressWarnings( "NoopMethodInAbstractClass" )
	protected void chartDisconnected( Chart oldChart ) {
		//	Nothing done in the default implementation.
	}

	/**
	 * @return The {@link Legend} of the chart or {@code null} if no legend
	 *         exists or it is not an instance of {@link Legend}.
	 */
	protected Legend getChartLegend() {

		final Parent parent = getChart();

		if ( parent != null ) {

			final ObservableList<Node> children = parent.getChildrenUnmodifiable();

			if ( children != null ) {
				for ( Node child : children ) {
					if ( Legend.class.isAssignableFrom(child.getClass()) ) {
						return (Legend) children;
					}
				}
			}

		}

		return null;

	}

	/**
	 * Converts mouse location within the scene to the location relative to the
	 * plot area.
	 *
	 * @param event The mouse event.
	 * @return The location within the plot area.
	 * @throws NullPointerException If the plugin hasn't been added to any chart.
	 */
	protected final Point2D getLocationInPlotArea( MouseEvent event ) {
		return getLocationInPlotArea(new Point2D(event.getSceneX(), event.getSceneY()));
	}

	/**
	 * Converts mouse location within the scene to the location relative to the
	 * plot area.
	 *
	 * @param mouseLocationInScene The mouse location in scene coordinates system.
	 * @return The location within the plot area.
	 * @throws NullPointerException If the plugin hasn't been added to any chart.
	 */
	protected final Point2D getLocationInPlotArea( Point2D mouseLocationInScene ) {

		double xInAxis = 0.0;
		double yInAxis = 0.0;

		if ( getChart() instanceof XYChart<?, ?> ) {

			XYChart<?, ?> xyChart = (XYChart<?, ?>) getChart();

			xInAxis = xyChart.getXAxis().sceneToLocal(mouseLocationInScene).getX();
			yInAxis = xyChart.getYAxis().sceneToLocal(mouseLocationInScene).getY();

		} else if ( getChart() instanceof DensityChartFX<?, ?> ) {

			DensityChartFX<?, ?> dChart = (DensityChartFX<?, ?>) getChart();

			xInAxis = dChart.getXAxis().sceneToLocal(mouseLocationInScene).getX();
			yInAxis = dChart.getYAxis().sceneToLocal(mouseLocationInScene).getY();

		}

		return new Point2D(xInAxis, yInAxis);

	}

	/**
	 * Gets the bounds of the plot area.
	 *
	 * @return the bounds of the plot area or {@code null} if the plugin hasn't
	 *         been added to any chart
	 * @throws NullPointerException If the plugin hasn't been added to any chart.
	 */
	protected final Bounds getPlotAreaBounds() {

		if ( getChart() instanceof XYChart<?, ?> ) {

			XYChart<?, ?> xyChart = (XYChart<?, ?>) getChart();

			return new BoundingBox(0, 0, xyChart.getXAxis().getWidth(), xyChart.getYAxis().getHeight());

		} else if ( getChart() instanceof DensityChartFX<?, ?> ) {

			DensityChartFX<?, ?> dChart = (DensityChartFX<?, ?>) getChart();

			return new BoundingBox(0, 0, dChart.getXAxis().getWidth(), dChart.getYAxis().getHeight());

		} else {
			return null;
		}

	}

	/**
	 * Returns a list containing nodes that should be added to the list of child
	 * nodes of the associated XYChart's plot area children.
	 * <p>
	 * The method should be used by concrete implementations to add any
	 * graphical components that should be rendered on the plot area.</p>
	 *
	 * @return Non-{@code null} list of nodes to be added to the chart's plot
	 *         area.
	 */
	@SuppressWarnings( "ReturnOfCollectionOrArrayField" )
	protected final ObservableList<Node> getPlotChildren() {
		return plotChildren;
	}

	/**
	 * @param xDisplayValue The display position on X axis.
	 * @return The data value for the given display position on the X axis or
	 *         {@code null}.
	 */
	protected final Object getXValueForDisplay( double xDisplayValue ) {
		if ( getChart() instanceof XYChart<?, ?> ) {
			return ( (XYChart<?, ?>) getChart() ).getXAxis().getValueForDisplay(xDisplayValue);
		} else if ( getChart() instanceof DensityChartFX<?, ?> ) {
			return ( (DensityChartFX<?, ?>) getChart() ).getXAxis().getValueForDisplay(xDisplayValue);
		} else {
			return null;
		}
	}

	/**
	 * @param xDisplayValue The display position on X axis.
	 * @return The data value for the given display position on the X axis as a
	 *         double value.
	 */
	protected final double getXValueForDisplayAsDouble( double xDisplayValue ) {
		return ( (Number) getXValueForDisplay(xDisplayValue) ).doubleValue();
	}

	/**
	 * @param yDisplayValue The display position on Y axis.
	 * @return The data value for the given display position on the Y axis or
	 *         {@code null}.
	 */
	protected final Object getYValueForDisplay( double yDisplayValue ) {
		if ( getChart() instanceof XYChart<?, ?> ) {
			return ( (XYChart<?, ?>) getChart() ).getYAxis().getValueForDisplay(yDisplayValue);
		} else if ( getChart() instanceof DensityChartFX<?, ?> ) {
			return ( (DensityChartFX<?, ?>) getChart() ).getYAxis().getValueForDisplay(yDisplayValue);
		} else {
			return null;
		}
	}

	/**
	 * @param yDisplayValue The display position on Y axis.
	 * @return The data value for the given display position on the Y axis as a
	 *         double value.
	 */
	protected final double getYValueForDisplayAsDouble( double yDisplayValue ) {
		return ( (Number) getYValueForDisplay(yDisplayValue) ).doubleValue();
	}

	/**
	 * Returns {@code true} if the mouse cursor is inside the plot area.
	 *
	 * @param event The {@link MouseEvent} containing the cursor position.
	 * @return {@code true} if the mouse cursor is inside the plot area,
	 *         {@code false} otherwise.
	 * @throws NullPointerException If the plugin hasn't been added to any chart.
	 */
	protected final boolean isInsidePlotArea( MouseEvent event ) {
		return isInsidePlotArea(new Point2D(event.getSceneX(), event.getSceneY()));
	}

	/**
	 * Returns {@code true} if the mouse cursor is inside the plot area.
	 *
	 * @param mouseLocationInScene The mouse location in scene coordinates system.
	 * @return {@code true} if the mouse cursor is inside the plot area,
	 *         {@code false} otherwise.
	 * @throws NullPointerException If the plugin hasn't been added to any chart.
	 */
	protected final boolean isInsidePlotArea( Point2D mouseLocationInScene ) {

		Point2D mouseLocation = getLocationInPlotArea(mouseLocationInScene);
		double valueX = getXValueForDisplayAsDouble(mouseLocation.getX());
		ValueAxis<?> xValueAxis = getXValueAxis();

		if ( valueX < xValueAxis.getLowerBound() || valueX > xValueAxis.getUpperBound() ) {
			return false;
		}

		double valueY = getYValueForDisplayAsDouble(mouseLocation.getY());
		ValueAxis<?> yValueAxis = getYValueAxis();

		return !( valueY < yValueAxis.getLowerBound() || valueY > yValueAxis.getUpperBound() );

	}

	/**
	 * Sets (or resets) the chart for this plugin.
	 *
	 * @param newChart The new {@link Chart} of this plugin. Can be {@code null}.
	 */
	protected void setChart( Chart newChart ) {

		Chart oldChart = chart;

		this.chart = newChart;

		if ( oldChart != null ) {
			chartDisconnected(oldChart);
		}

		if ( newChart != null ) {
			chartConnected(newChart);
		}

	}

	/**
	 * Converts given point in display coordinates into the corresponding point
	 * in data coordinates.
	 *
	 * @param displayPoint The point in display coordinates to be converted.
	 * @return The corresponding point in data coordinates.
	 * @throws NullPointerException If the plugin hasn't been added to any chart.
	 */
	protected final Data<?, ?> toDataPoint( Point2D displayPoint ) {
		return new Data<>(
			getXValueForDisplay(displayPoint.getX()),
			getYValueForDisplay(displayPoint.getY())
		);
	}

	/**
	 * Converts given point in data coordinates into the corresponding point
	 * in display coordinates.
	 *
	 * @param dataPoint The point in data coordinates to be converted.
	 * @return The corresponding point in display coordinates.
	 * @throws NullPointerException If the plugin hasn't been added to any chart.
	 */
	@SuppressWarnings( { "unchecked", "rawtypes" } )
	protected Point2D toDisplayPoint( Data<?, ?> dataPoint ) {

		Axis xAxis = ( (XYChart<?, ?>) getChart() ).getXAxis();
		Axis yAxis = ( (XYChart<?, ?>) getChart() ).getYAxis();

		return new Point2D(
			xAxis.getDisplayPosition(dataPoint.getXValue()),
			yAxis.getDisplayPosition(dataPoint.getYValue())
		);
		
	}

}
