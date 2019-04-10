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

import java.text.DecimalFormat;

import chart.Plugin;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.chart.Chart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

/**
 * A {@link Label} appearing next to the mouse cursor, showing mouse X and Y position in data coordinates.
 * <p>
 * Label relative position to the mouse cursor can be adjusted using {@link #offsetX} and {@link #offsetY} properties.
 * </p><p>
 * CSS style class name: {@code chart-coordinates-label}
 * </p>
 * 
 * @author Grzegorz Kruk
 */
public class CoordinatesLabel extends Plugin {
    private static final int DEFAULT_X_OFFSET = 15;
    private static final int DEFAULT_Y_OFFSET = 5;

    // Need separate configurable formatter for X and Y or delegating it to the X and Y axis
    private static final DecimalFormat DEFAULT_FORMATTER = new DecimalFormat("0.####");

    /** Offset of the label X position with respect to the mouse cursor location, expressed in display units. */
    private final DoubleProperty offsetX = new SimpleDoubleProperty(this, "offsetX", DEFAULT_X_OFFSET);
    public final double getOffsetX() { return offsetX.get(); }
    public final void setOffsetX(double value) { offsetX.set(value); }
    public final DoubleProperty offsetXProperty() { return offsetX; }
    
    /** Offset of the label Y position with respect to the mouse cursor location, expressed in display units. */
    private final DoubleProperty offsetY = new SimpleDoubleProperty(this, "offsetY", DEFAULT_Y_OFFSET);
    public final double getOffsetY() { return offsetY.get(); }
    public final void setOffsetY(double value) { offsetY.set(value); }
    public final DoubleProperty offsetYProperty() { return offsetY; }
    
    /**
     * The label showing coordinates.
     */
    protected final Label label = new Label();

    /**
     * Creates and initializes a new instance of the CoordinatesLabel class.
     */
    public CoordinatesLabel() {
        label.getStyleClass().add("chart-coordinates-label");
        label.setManaged(false);
        getPlotChildren().add(label);
    }

    @Override
    protected void chartDisconnected(Chart oldChart) {        
        if(getChart() instanceof XYChart<?, ?>){ 
            oldChart.removeEventHandler(MouseEvent.MOUSE_MOVED, mouseMoveHandler);
        }        
    }

    @Override
    protected void chartConnected(Chart newChart) {
        if(getChart() instanceof XYChart<?, ?>){ 
            newChart.addEventHandler(MouseEvent.MOUSE_MOVED, mouseMoveHandler);
        }
    }

    private final EventHandler<MouseEvent> mouseMoveHandler = (MouseEvent event) -> {
        Point2D mouseLocation = getLocationInPlotArea(event);               
        
        if (!getPlotAreaBounds().contains(mouseLocation)) {
            getPlotChildren().remove(label);
            return;
        }
        renderCoordinates(mouseLocation);
    };

    private void renderCoordinates(Point2D mouseLocation) {
        Data<?, ?> dataPoint = getDataPoint(mouseLocation);
        if (dataPoint == null) {
            getPlotChildren().remove(label);
        } else {
            prepareLabel(mouseLocation, dataPoint);
            if (!getPlotChildren().contains(label)) {
                getPlotChildren().add(label);
                label.requestLayout();
            }
        }
    }
    
    /**
     * Prepares the label to display point coordinates.
     * 
     * @param mouseLocation current mouse location within the chart area
     * @param dataPoint data point to be displayed by the label
     */
    protected void prepareLabel(Point2D mouseLocation, Data<?, ?> dataPoint) {
        label.setText(formatData(dataPoint));
        label.setPadding(new Insets(2, 2, 2, 2));
        double mouseX = mouseLocation.getX();
        double mouseY = mouseLocation.getY();
        double width = label.prefWidth(-1);
        double height = label.prefHeight(width);
        label.resizeRelocate(mouseX + getOffsetX(), mouseY + getOffsetY(), width, height);    
    }
    
    /**
     * Converts given display point into the corresponding data point i.e. point expressed in data coordinates.
     * 
     * @param displayPoint display point to be converted
     * @return the corresponding data point or {@code null} if given display point doesn't correspond to any data point
     */
    protected Data<?, ?> getDataPoint(Point2D displayPoint) {
        return toDataPoint(displayPoint);
    }
    
    private String formatData(Data<?, ?> dataPoint) {
        return formatValue(dataPoint.getXValue()) + ", " + formatValue(dataPoint.getYValue());
    }

    private String formatValue(Object value) {
        if (value instanceof Number) {
            return DEFAULT_FORMATTER.format(((Number) value).doubleValue());
        } else {
            return String.valueOf(value);
        }
    }
    
    
}
