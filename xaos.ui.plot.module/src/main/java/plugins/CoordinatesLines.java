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
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;
import chart.Plugin;
import javafx.scene.chart.Chart;

/**
 * Horizontal and vertical {@link Line} drawn on the plot area, crossing at the mouse cursor location.
 * <p>
 * CSS style class name: {@code chart-coordinates-line}
 * </p>
 * 
 * @author Grzegorz Kruk 
 */
public final class CoordinatesLines extends Plugin {

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
