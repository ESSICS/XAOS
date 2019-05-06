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

package chart;
import com.sun.javafx.charts.Legend;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.input.MouseEvent;


/**
 * Base for {@link XYChart} plugins.
 *
 * @author claudio.rosati@esss.se
 */
public abstract class Plugin {

    private Chart chart;    
    private final ObservableList<Node> plotChildren = FXCollections.observableArrayList();

    /**
     * Returns boolean true if methods need to update their internal record of chart.
     * 
     * true if zoomInStarted(), zoomOut() or zoomOrigin()
     */
    public static boolean viewReset = true;

    /**
     * Returns chart associated with the plugin or {@code null} if plugin hasn't been added to any chart.
     * 
     * @return associated chart or {@code null}
     */
    public final Chart getChart() {
        return chart;
    }

    public final ValueAxis<?> getXValueAxis() {
        if(getChart() instanceof XYChart<?, ?>){
            if (((XYChart<?, ?>) getChart()).getXAxis() instanceof CategoryAxis) {
                return new NumberAxis();
            } else {
            return (ValueAxis<?>) ((XYChart<?, ?>) getChart()).getXAxis();}
        } else if(getChart() instanceof DensityChartFX<?, ?>){ 
            return (ValueAxis<?>) ((DensityChartFX<?, ?>) getChart()).getXAxis();
        } else {
            return null;
        }
    }
    

    public final ValueAxis<?> getYValueAxis() {
        if(getChart() instanceof XYChart<?, ?>){
            if (((XYChart<?, ?>) getChart()).getYAxis() instanceof CategoryAxis) {
                return new NumberAxis();}
            else {
            return (ValueAxis<?>) ((XYChart<?, ?>) getChart()).getYAxis();}
        } else if(getChart() instanceof DensityChartFX<?, ?>){ 
            return (ValueAxis<?>) ((DensityChartFX<?, ?>) getChart()).getYAxis();
        } else {
            return null;
        }
    }
    
    protected final double getWindowWidth() {
        return getChart().getMaxWidth();
    }
    /**
     * Returns a list containing nodes that should be added to the list of child nodes of the associated XYChart's
     * plot area children. 
     * <p>
     * The method should be used by concrete implementations to add any graphical components that should be rendered
     * on the plot area.
     * </p>
     * @return non-null list of nodes to be added to the chart's plot area
     */
    protected final ObservableList<Node> getPlotChildren() {
        return plotChildren; // NOSONAR
    }

    protected void setChart(Chart newChart) {
        Chart oldChart = chart;
        this.chart = newChart;

        if (oldChart != null) {
            chartDisconnected(oldChart);
        }
        if (newChart != null) {
            chartConnected(newChart);
        }
    }

    /**
     * Called when the plugin has been removed from the chart.
     * <p>
     * Can be overridden by concrete implementations to unbind listeners and perform any other cleanup operations.
     * </p>
     * 
     * @param oldChart the chart from which the plugin has been removed
     */
    protected void chartDisconnected(Chart oldChart) {
        //
    }

    /**
     * Called when the plugin is added to a chart.
     * <p>
     * Can be overridden by concrete implementations to register e.g. mouse listeners or perform any other
     * initializations.
     * </p>
     * 
     * @param newChart the chart to which the plugin has been added
     */
    protected void chartConnected(Chart newChart) {
        //
    }

    /**
     * Bounds of the plot area.
     * 
     * @return bounds of the plot area or {@code null} if the plugin hasn't been added to any chart
     * @throws NullPointerException if the plugin hasn't been added to any chart
     */
    protected final Bounds getPlotAreaBounds() {
        if(getChart() instanceof XYChart<?, ?>){
            XYChart<?, ?> xyChart = (XYChart<?, ?>) getChart();
            return new BoundingBox(0, 0, xyChart.getXAxis().getWidth(), xyChart.getYAxis().getHeight());
        } else if(getChart() instanceof DensityChartFX<?, ?>){ 
            DensityChartFX<?, ?> xyChart = (DensityChartFX<?, ?>) getChart();
            return new BoundingBox(0, 0, xyChart.getXAxis().getWidth(), xyChart.getYAxis().getHeight());
        } else {
            return null;
        }
        
    }
    
    /**
     * Bounds of the plot area.
     * 
     * @return bounds of the plot area or {@code null} if the plugin hasn't been added to any chart
     * @throws NullPointerException if the plugin hasn't been added to any chart
     */
    protected Legend getLegendChart(){
        final Parent aParent = getChart();
        final String aClassname = Legend.class.getName();
        if (null != aParent) {
            final ObservableList<Node> children = aParent.getChildrenUnmodifiable();
            if (null != children) {
                for (final Node child : children) {
                    String className = child.getClass().getName();
                    if (className.contains("$")) {
                        className = className.substring(0, className.indexOf("$"));
                    }
                    if (0 == aClassname.compareToIgnoreCase(className)) {
                        return (Legend) child;
                    }   
                }
            }
        }
        return null;
    }
    

    /**
     * Converts mouse location within the scene to the location relative to the plot area.
     * 
     * @param event mouse event
     * @return location within the plot area
     * @throws NullPointerException if the plugin hasn't been added to any chart
     */
    protected final Point2D getLocationInPlotArea(MouseEvent event) {
		return getLocationInPlotArea(new Point2D(event.getSceneX(), event.getSceneY()));
    }

    /**
     * Converts mouse location within the scene to the location relative to the plot area.
     *
     * @param mouseLocationInScene mouse location in scene coordinates system
     * @return location within the plot area
     * @throws NullPointerException if the plugin hasn't been added to any chart
     */
    protected final Point2D getLocationInPlotArea(Point2D mouseLocationInScene) {
        double xInAxis = 0.0;
        double yInAxis = 0.0;
        if(getChart() instanceof XYChart<?, ?>){
            xInAxis =((XYChart<?, ?>) getChart()).getXAxis().sceneToLocal(mouseLocationInScene).getX();
            yInAxis = ((XYChart<?, ?>) getChart()).getYAxis().sceneToLocal(mouseLocationInScene).getY();
        } else if(getChart() instanceof DensityChartFX<?, ?>){
            xInAxis =((DensityChartFX<?, ?>) getChart()).getXAxis().sceneToLocal(mouseLocationInScene).getX();
            yInAxis = ((DensityChartFX<?, ?>) getChart()).getYAxis().sceneToLocal(mouseLocationInScene).getY();
        }

        return new Point2D(xInAxis, yInAxis);
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
    * @param mouseLocationInScene mouse location in scene coordinates system
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

		double valueY =	getYValueForDisplayAsDouble(mouseLocation.getY());
		ValueAxis<?> yValueAxis = getYValueAxis();

		if ( valueY < yValueAxis.getLowerBound() || valueY > yValueAxis.getUpperBound() ) {
			return false;
		}

		return true;

	}

    /**
     * Converts given point in display coordinates into the corresponding point within the underlying data coordinates.
     * 
     * @param displayPoint point to be converted
     * @return corresponding point in data coordinates
     * @throws NullPointerException if the plugin hasn't been added to any chart
     */
    protected final Data<?, ?> toDataPoint(Point2D displayPoint) {
        return new Data<Object, Object>(getXValueForDisplay(displayPoint.getX()),
                getYValueForDisplay(displayPoint.getY()));
    }

    protected final Object getXValueForDisplay(double xDisplayValue) {
        if(getChart() instanceof XYChart<?, ?>){
            return ((XYChart<?, ?>) getChart()).getXAxis().getValueForDisplay(xDisplayValue);
        } else if(getChart() instanceof DensityChartFX<?, ?>){ 
            return ((DensityChartFX<?, ?>) getChart()).getXAxis().getValueForDisplay(xDisplayValue);
        } else {
            return null;
        }                        
    }

    protected final Object getYValueForDisplay(double yDisplayValue) {
        if(getChart() instanceof XYChart<?, ?>){
            return ((XYChart<?, ?>) getChart()).getYAxis().getValueForDisplay(yDisplayValue);
        } else if(getChart() instanceof DensityChartFX<?, ?>){ 
            return ((DensityChartFX<?, ?>) getChart()).getYAxis().getValueForDisplay(yDisplayValue);
        } else {
            return null;
        }    
    }

    protected final double getXValueForDisplayAsDouble(double xDisplayValue) {
        return ((Number) getXValueForDisplay(xDisplayValue)).doubleValue();
    }

    protected final double getYValueForDisplayAsDouble(double yDisplayValue) {
        return ((Number) getYValueForDisplay(yDisplayValue)).doubleValue();
    }

	/**
	 * Called by charts when the visibility of a series changed.
	 *
	 * @param <X>     Type of X values.
	 * @param <Y>     Type of Y values.
	 * @param chart   The chart where the {@code series} visibility change occurred.
	 * @param series  The {@link Series} whose visibility changed.
	 * @param visible The current visibility state of the given {@code series}.
	 */
	@SuppressWarnings( "NoopMethodInAbstractClass" )
	public <X, Y> void seriesVisibilityUpdated ( Chart chart, Series<X, Y> series, boolean visible ) {
		//	Nothing done in the default implementation.
	}
    
   
}
