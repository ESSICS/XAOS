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

import chart.DensityChartFX;
import static util.Assert.assertArgNotNull;
import static util.Assert.assertValueAxis;
import static javafx.scene.input.MouseButton.PRIMARY;


import java.util.function.Predicate;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.input.MouseEvent;
import chart.XYChartPlugin;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.Chart;

/**
 * Allows dragging the visible plot area along X and/or Y axis, changing the visible axis range.
 * <p>
 * Reacts on {@link MouseEvent#DRAG_DETECTED} event accepted by {@link #getMouseFilter() mouse filter}.
 * </p>
 * 
 * @author Grzegorz Kruk
 */
public final class Pan extends XYChartPlugin {
    /**
     * Default pan mouse filter passing on left mouse button with {@link MouseEvent#isControlDown() control key down}.
     */
    public static final Predicate<MouseEvent> DEFAULT_MOUSE_FILTER = new Predicate<MouseEvent>() {
        @Override public boolean test(MouseEvent event) {
            return event.getButton() == PRIMARY && !event.isMiddleButtonDown() && !event.isSecondaryButtonDown()
                    && event.isControlDown() && !event.isAltDown() && !event.isMetaDown() && !event.isShiftDown();
        }
    };

    /** Panning mode along X, Y or both axis */
    private final ObjectProperty<AxisMode> panMode = new ObjectPropertyBase<AxisMode>(AxisMode.XY) {
        @Override protected void invalidated() {
            assertArgNotNull(get(), getName());
        }

        @Override public Object getBean() {
            return Pan.this;
        }

        @Override public String getName() {
            return "panMode";
        }
    };
    public void setPanMode(AxisMode value) { panMode.set(value); }
    public AxisMode getPanMode() { return panMode.get(); }
    public ObjectProperty<AxisMode> panModeProperty() { return panMode; }

    /** Mouse cursor used during panning. */
    private final ObjectProperty<Cursor> panCursor = new SimpleObjectProperty<Cursor>(this, "panCursor", Cursor.CLOSED_HAND);
    public void setPanCursor(Cursor value) { panCursor.set(value); }
    public Cursor getPanCursor() { return panCursor.get(); }
    public ObjectProperty<Cursor> panCursorProperty() { return panCursor; }

    private Predicate<MouseEvent> mouseFilter = DEFAULT_MOUSE_FILTER;
    private Cursor sceneCursor;

    /**
     * Returns MouseEvent filter triggering pan operation.
     * 
     * @return filter used to test whether given MouseEvent should start panning operation
     * @see #setMouseFilter(Predicate)
     */
    public Predicate<MouseEvent> getMouseFilter() {
        return mouseFilter;
    }

    /**
     * Sets the filter determining whether given MouseEvent triggered on {@link MouseEvent#DRAG_DETECTED event type}
     * should start panning operation.
     * <p>
     * By default it is initialized to {@link #DEFAULT_MOUSE_FILTER}.
     * </p>
     * 
     * @param mouseFilter the mouse filter to be used. Can be set to {@code null} to start panning on any
     *            {@link MouseEvent#DRAG_DETECTED DRAG_DETECTED} event.
     */
    public void setMouseFilter(Predicate<MouseEvent> mouseFilter) {
        this.mouseFilter = mouseFilter;
    }

    private Data<Number, Number> prevDataPoint = null;
    private double plotWidth;
    private double plotHeight;
    
    
    /**
     * Creates a new instance of Pan class with {@link AxisMode#XY XY} {@link #panModeProperty() panMode}.
     */
    public Pan() {
        this(AxisMode.XY);
    }

    /**
     * Creates a new instance of Pan class.
     * 
     * @param panMode initial value for the {@link #panModeProperty() panMode} property
     */
    public Pan(AxisMode panMode) {
        setPanMode(panMode);
    }

    @Override
    protected void chartDisconnected(Chart oldChart) {
        unbindListeners(oldChart);
    }

    @Override
    protected void chartConnected(Chart newChart) {
        if (newChart instanceof BarChart){
            super.chartDisconnected(newChart);
        } else { 
        if(newChart instanceof XYChart<?, ?>){
            assertValueAxis(((XYChart<?, ?>) newChart).getXAxis(), "X");
            assertValueAxis(((XYChart<?, ?>) newChart).getYAxis(), "Y");
        } else if(newChart instanceof DensityChartFX<?, ?>){
            assertValueAxis(((DensityChartFX<?, ?>) newChart).getXAxis(), "X");
            assertValueAxis(((DensityChartFX<?, ?>) newChart).getYAxis(), "Y");
        }                    
        bindListeners(newChart);
    }}

    private void unbindListeners(Chart chart) {
        chart.removeEventHandler(MouseEvent.DRAG_DETECTED, panStartHandler);
        chart.removeEventHandler(MouseEvent.MOUSE_DRAGGED, panDragHandler);
        chart.removeEventHandler(MouseEvent.MOUSE_RELEASED, panEndHandler);
    }

    private void bindListeners(Chart chart) {
        chart.addEventHandler(MouseEvent.DRAG_DETECTED, panStartHandler);
        chart.addEventHandler(MouseEvent.MOUSE_DRAGGED, panDragHandler);
        chart.addEventHandler(MouseEvent.MOUSE_RELEASED, panEndHandler);
    }

    private final EventHandler<MouseEvent> panStartHandler = (MouseEvent event) -> {
        if (mouseFilter == null || mouseFilter.test(event)) {
            panStarted(event);
            event.consume();
        }
    };

    private final EventHandler<MouseEvent> panDragHandler = (MouseEvent event) -> {
        if (panOngoing()) {
            panDragged(event);
            event.consume();
        }
    };

    private final EventHandler<MouseEvent> panEndHandler = (MouseEvent event) -> {
        if (panOngoing()) {
            panEnded();
            event.consume();
        }
    };

    private boolean panOngoing() {
        return prevDataPoint != null;
    }

    private void panStarted(MouseEvent event) {
        getXValueAxis().setAutoRanging(false);
        getYValueAxis().setAutoRanging(false);
        Point2D mouseLocation = getLocationInPlotArea(event);
        double dataX = getXValueForDisplayAsDouble(mouseLocation.getX());
        double dataY = getYValueForDisplayAsDouble(mouseLocation.getY());
        prevDataPoint = new Data<>(dataX, dataY);
        if (viewReset) { resetPlotSize(); viewReset = false;
        }
        sceneCursor = getChart().getScene().getCursor();
        if (getPanCursor() != null) {
            getChart().getScene().setCursor(getPanCursor());
        }
    }

    private void panDragged(MouseEvent event) {
        Point2D mouseLocation = getLocationInPlotArea(event);
        double dataX = getXValueForDisplayAsDouble(mouseLocation.getX());
        double dataY = getYValueForDisplayAsDouble(mouseLocation.getY());

        double xOffset = prevDataPoint.getXValue().doubleValue() - dataX;
        double yOffset = prevDataPoint.getYValue().doubleValue() - dataY;
        //System.out.println("prevDataPoint " + prevDataPoint + " datax " + dataX + " xOffset " + xOffset);
        if (getPanMode() == AxisMode.X || getPanMode() == AxisMode.XY) {
            getXValueAxis().setLowerBound(getXValueAxis().getLowerBound() + xOffset);
            getXValueAxis().setUpperBound(getXValueAxis().getLowerBound() + plotWidth);
        }
        if (getPanMode() == AxisMode.Y || getPanMode() == AxisMode.XY) {
            getYValueAxis().setLowerBound(getYValueAxis().getLowerBound() + yOffset);
            getYValueAxis().setUpperBound(getYValueAxis().getLowerBound() + plotHeight);
        }
    }

    private void panEnded() {
        prevDataPoint = null;
        viewReset = true;
        getChart().getScene().setCursor(sceneCursor);
    }
    private void resetPlotSize() {
      plotHeight = getYValueAxis().getUpperBound() - getYValueAxis().getLowerBound();
      plotWidth = getXValueAxis().getUpperBound() - getXValueAxis().getLowerBound();
  }
    
}
