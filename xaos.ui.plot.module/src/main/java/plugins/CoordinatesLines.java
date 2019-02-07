/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package plugins;

import chart.DensityChartFX;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;
import chart.XYChartPlugin;
import javafx.scene.chart.Chart;

/**
 * Horizontal and vertical {@link Line} drawn on the plot area, crossing at the mouse cursor location.
 * <p>
 * CSS style class name: {@code chart-coordinates-line}
 * </p>
 * 
 * @author Grzegorz Kruk 
 */
public final class CoordinatesLines extends XYChartPlugin {

    private final Line verticalLine = new Line();
    private final Line horizontalLine = new Line();

    /**
     * Creates and initalizes a new instance of CoordinatesLines class.
     */
    public CoordinatesLines() {
        verticalLine.getStyleClass().add("chart-coordinates-line");
        verticalLine.setManaged(false);
        horizontalLine.getStyleClass().add("chart-coordinates-line");
        horizontalLine.setManaged(false);
        getPlotChildren().addAll(verticalLine, horizontalLine);
    }

    @Override
    protected void chartDisconnected(Chart oldChart) {
        oldChart.removeEventHandler(MouseEvent.MOUSE_MOVED, mouseMoveHandler);
        
    }

    @Override
    protected void chartConnected(Chart newChart) {
        newChart.addEventHandler(MouseEvent.MOUSE_MOVED, mouseMoveHandler);
        
    }

    private final EventHandler<MouseEvent> mouseMoveHandler = (MouseEvent event) -> {
        Point2D mouseLocation = getLocationInPlotArea(event);                
        
        if (!getPlotAreaBounds().contains(mouseLocation)) {
            getPlotChildren().removeAll(horizontalLine, verticalLine);
            return;
        }
        
        Bounds plotAreaBounds = getPlotAreaBounds();
        
        if(getChart() instanceof DensityChartFX<?, ?>){ 
            mouseLocation =mouseLocation.add(new Point2D(getXValueAxis().getLayoutX(),0.0));
            horizontalLine.setStartX(getXValueAxis().getLayoutX());
            horizontalLine.setEndX(getXValueAxis().getWidth()+getXValueAxis().getLayoutX());
            horizontalLine.setStartY(mouseLocation.getY());
            horizontalLine.setEndY(mouseLocation.getY());
            
            verticalLine.setStartY(getYValueAxis().getLayoutY());
            verticalLine.setStartX(mouseLocation.getX());
            verticalLine.setEndX(mouseLocation.getX());
            verticalLine.setEndY(getYValueAxis().getHeight()+getYValueAxis().getLayoutY());
        } else {
            horizontalLine.setStartX(0);
            horizontalLine.setEndX(plotAreaBounds.getWidth());
            horizontalLine.setStartY(mouseLocation.getY());
            horizontalLine.setEndY(mouseLocation.getY());
            
            verticalLine.setStartY(0);
            verticalLine.setStartX(mouseLocation.getX());
            verticalLine.setEndX(mouseLocation.getX());
            verticalLine.setEndY(plotAreaBounds.getHeight());
        }                        
        
        if (!getPlotChildren().contains(horizontalLine)) {
            getPlotChildren().addAll(horizontalLine, verticalLine);
        }
    };
}
