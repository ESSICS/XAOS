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

package plugins;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import se.europeanspallationsource.xaos.ui.plot.util.AbscissaDataComparator;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.layout.StackPane;

/**
 * A tool tip label appearing next to the mouse cursor, showing coordinates of the closest data point that is within the
 * {@link #pickingDistanceProperty()} from the mouse cursor.
 * <p>
 * Label relative position to the mouse cursor can be adjusted using {@link #offsetX} and {@link #offsetY} properties.
 * </p><p>
 * CSS style class name: {@code chart-data-tooltip-label}
 * </p>
 * 
 * @author Grzegorz Kruk
 */
public final class DataPointTooltip extends CoordinatesLabel {
    private static final int DEFAULT_PICKING_DISTANCE = 10;
    private static final int DEFAULT_Y_OFFSET = -25;

    /**
     * Distance of the mouse cursor from the data point (expressed in display units) that should trigger showing the
     * tool tip.
     */
    private final DoubleProperty pickingDistance = new DoublePropertyBase(DEFAULT_PICKING_DISTANCE) {
        @Override protected void invalidated() {
			if ( get() <= 0 ) {
				throw new IllegalArgumentException("The picking distance must be a positive value.");
			}
        }

        @Override public Object getBean() {
            return DataPointTooltip.this;
        }

        @Override public String getName() {
            return "pickingDistance";
        }
    };

    public double getPickingDistance() { return pickingDistance.get(); }
    public void setPickingDistance(double value) { pickingDistance.set(value); }
    public DoubleProperty pickingDistanceProperty() { return pickingDistance; }

    private AbscissaDataComparator xValueComparator;

    /**
     * Creates and initalizes a new instance of DataPointTooltip class with {{@link #pickingDistanceProperty() picking
     * distance} initialized to 10.
     */
    public DataPointTooltip() {
        label.getStyleClass().add("chart-data-tooltip-label");
        setOffsetY(DEFAULT_Y_OFFSET);
    }

    /**
     * Creates and initalizes a new instance of DataPointTooltip class.
     * 
     * @param pickingDistance the initial value for the {@link #pickingDistanceProperty() pickingDistance} property
     */
    public DataPointTooltip(double pickingDistance) {
        this();
        setPickingDistance(pickingDistance);
    }

    @Override
    protected void chartConnected(Chart newChart) {        
        if(newChart instanceof XYChart<?, ?>){
            super.chartConnected(newChart);
            xValueComparator = new AbscissaDataComparator(((XYChart<?, ?>)newChart).getXAxis());
        }       
    }

    @Override
    protected Data<?, ?> getDataPoint(Point2D mouseLocation) {
        Object xValue = ((XYChart<?, ?>)getChart()).getXAxis().getValueForDisplay(mouseLocation.getX());
        List<Data<?, ?>> neighborPoints = findNeighborPoints(xValue);
        if (neighborPoints.isEmpty()) {
            return null;
        }
        return pickNearestPointWithinPickingDistance(neighborPoints, mouseLocation);
    }
    
    @Override
    protected void prepareLabel(Point2D mouseLocation, Data<?, ?> dataPoint) {
        super.prepareLabel(mouseLocation, dataPoint);
        Node node = dataPoint.getNode();
        if (node instanceof StackPane) {
            StackPane stackPane = (StackPane) node;
            // YL: This background makes the text hard to read if it is a dark colour, e.g. blue
            label.setBackground(stackPane.getBackground());
            label.setBorder(stackPane.getBorder());
            label.setPadding(new Insets(2, 3, 2, 3));
        }
    }

    private List<Data<?, ?>> findNeighborPoints(Object xValue) {
        if (isDataInXAscendingOrder()) {
            return findNeighborPointsWithBinarySearch(xValue);
        } else {
            List<Data<?, ?>> points = new LinkedList<>();
            for (Series<?, ?> series : ((XYChart<?, ?>)getChart()).getData()) {
                points.addAll(series.getData());
            }
            return points;
        }
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private boolean isDataInXAscendingOrder() {
        Axis xAxis = ((XYChart<?, ?>)getChart()).getXAxis();
        for (Series<?, ?> series : ((XYChart<?, ?>)getChart()).getData()) {
            double prevX = Double.MIN_VALUE;
            for (Data<?, ?> point : series.getData()) {
                double x = xAxis.toNumericValue(point.getXValue());
                if (x < prevX) {
                    return false;
                }
                prevX = x;
            }
        }
        return true;
    }
    
    private List<Data<?, ?>> findNeighborPointsWithBinarySearch(Object xValue) {
        List<Data<?, ?>> points = new LinkedList<>();
        for (Series<?, ?> series : ((XYChart<?, ?>)getChart()).getData()) {
            int index = Collections.binarySearch(series.getData(), AbscissaDataComparator.key(xValue), xValueComparator);
            if (index >= 0) {
                points.add(series.getData().get(index));
            } else {
                index = -index - 1;
                int seriesSize = series.getData().size();
                if (index < seriesSize) {
                    points.add(series.getData().get(index));
                }
                if (index > 0) {
                    points.add(series.getData().get(index - 1));
                }
            }
        }            
        return points;
    }

    private Data<?, ?> pickNearestPointWithinPickingDistance(List<Data<?, ?>> dataPoints, Point2D mouseLocation) {
        Data<?, ?> nearestDataPoint = null;
        Point2D nearestDisplayPoint = new Point2D(Double.MAX_VALUE, Double.MAX_VALUE);
        for (Data<?, ?> dataPoint : dataPoints) {
            Node node = dataPoint.getNode();
            if (node != null && node.isVisible() && node.getBoundsInParent().contains(mouseLocation)) {
                nearestDataPoint = dataPoint;
                break;
            } else {
                Point2D displayPoint = toDisplayPoint(dataPoint);
                if (mouseLocation.distance(displayPoint) <= getPickingDistance()
                        && displayPoint.distance(mouseLocation) < nearestDisplayPoint.distance(mouseLocation)) {
                    nearestDataPoint = dataPoint;
                    nearestDisplayPoint = displayPoint;
                }
            }
        }
        return nearestDataPoint;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Point2D toDisplayPoint(Data<?, ?> dataPoint) {
        Axis xAxis = ((XYChart<?, ?>)getChart()).getXAxis();
        Axis yAxis = ((XYChart<?, ?>)getChart()).getYAxis();
        double displayX = xAxis.getDisplayPosition(dataPoint.getXValue());
        double displayY = yAxis.getDisplayPosition(dataPoint.getYValue());
        return new Point2D(displayX, displayY);
    }
}
