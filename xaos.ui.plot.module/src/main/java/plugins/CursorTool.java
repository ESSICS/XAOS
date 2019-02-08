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
import chart.XYChartPlugin;
import java.util.ArrayDeque;
import java.util.Deque;
import util.iconParser;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.input.KeyEvent;
import javafx.scene.chart.Chart;


/**
 * Creates an Object when Alt key is pressed within the chart. 
 * @author reubenlindroos
 * 
 * 
 */
public final class CursorTool extends XYChartPlugin{
    
    private boolean altPressed = false;
    private final double toolSize = 125.0;
    private Circle smallCircle = new Circle();
    private Circle bigCircle = new Circle();
    private Rectangle outerRectangle = new Rectangle();
    private Point2D toolCenter = null;
    private iconParser tool = new iconParser();
    private static double plotWidth;
    private static double plotHeight;
    private final Deque<Rectangle2D> zoomStack = new ArrayDeque<>();

    
    
    public CursorTool() {        
        smallCircle.setRadius(toolSize/6);
        bigCircle.setRadius(toolSize/2*4/5);
        outerRectangle.setWidth(toolSize);
        outerRectangle.setHeight(toolSize);
    }
    
    @Override
    protected void chartDisconnected(Chart oldChart) {
        oldChart.removeEventHandler(MouseEvent.MOUSE_PRESSED, mousePressHandler); 
        oldChart.removeEventHandler(MouseEvent.MOUSE_MOVED, mouseMoveHandler);
        oldChart.removeEventHandler(KeyEvent.KEY_PRESSED, keyPressHandler);
        oldChart.removeEventHandler(KeyEvent.KEY_RELEASED,keyReleaseHandler);
    }

    @Override
    protected void chartConnected(Chart newChart) {
        newChart.addEventHandler(MouseEvent.MOUSE_PRESSED, mousePressHandler); 
        newChart.addEventHandler(MouseEvent.MOUSE_MOVED, mouseMoveHandler);
        newChart.addEventHandler(KeyEvent.KEY_PRESSED, keyPressHandler);
        newChart.addEventHandler(KeyEvent.KEY_RELEASED,keyReleaseHandler);
    
    }
    // Makes the object appear when the key is pressed by adding the object
    // to the plugin list
    
    private final EventHandler<KeyEvent> keyPressHandler = (KeyEvent ke) ->{        
        switch(ke.getCode()){
            case ALT:
                tool = new iconParser();
                bigCircle.setCenterX(toolCenter.getX());
                bigCircle.setCenterY(toolCenter.getY());
                smallCircle.setCenterX(toolCenter.getX());
                smallCircle.setCenterY(toolCenter.getY());
                outerRectangle.setX(toolCenter.getX()-toolSize/2);
                outerRectangle.setY(toolCenter.getY()-toolSize/2);
                tool.setLocation(toolCenter);
                altPressed=true;
                getPlotChildren().addAll(tool.getImages());
        }
        
    };
   
   // Makes the object dissappear when key is released by removing the object
   // from the plugin list
    
   private final EventHandler<KeyEvent> keyReleaseHandler = (KeyEvent ke) ->{
        altPressed =false;
        getPlotChildren().removeAll(tool.getImages());
   };
    
  
    private final EventHandler<MouseEvent> mouseMoveHandler = (MouseEvent event) -> {
        Point2D mouseLocation = getLocationInPlotArea(event);
        
        if(getChart() instanceof DensityChartFX<?, ?>){ 
            mouseLocation =mouseLocation.add(new Point2D(getXValueAxis().getLayoutX(),0.0));
        } 
        
        Bounds plotAreaBounds = getPlotAreaBounds();
                
        toolCenter = mouseLocation;
                      
        if (altPressed && getPlotAreaBounds().contains(mouseLocation)) { 
            getPlotChildren().removeAll(tool.getImages());        
            updateCursorTool(event);            
            getPlotChildren().addAll(tool.getImages());
        } else {
            getPlotChildren().removeAll(tool.getImages());
            updateCursorTool(event); 
        }
    };
    
    private final EventHandler<MouseEvent> mousePressHandler = (MouseEvent event) ->{
        Point2D mouseLocation = getLocationInPlotArea(event);
        
        if(getChart() instanceof DensityChartFX<?, ?>){ 
            mouseLocation = mouseLocation.add(new Point2D(getXValueAxis().getLayoutX(),0.0));
        } 
        
        if (smallCircle.contains(mouseLocation)){
            autoScale(getChart());
            viewReset = true;
            event.consume();
        }
        else if(bigCircle.contains(mouseLocation)){
            Double yVal = mouseLocation.getY()-bigCircle.getCenterY();
            Double xVal = mouseLocation.getX()-bigCircle.getCenterX();

            if (Math.atan(Math.abs(yVal)/xVal)<Math.PI/4 && Math.atan(Math.abs(yVal)/xVal)>0){
                panRight(getChart());
            }
            else if (Math.atan(Math.abs(yVal)/xVal)>-Math.PI/4 && Math.atan(Math.abs(yVal)/xVal)<0){
                panLeft(getChart());
            }
            else if (yVal<0) {
                panUp(getChart());
            }

            else if (yVal>0) {
                panDown(getChart());
            }    
            event.consume();
        } else if(outerRectangle.contains(mouseLocation)){
            viewReset = true;
            if(mouseLocation.getX()<bigCircle.getCenterX() && mouseLocation.getY()<bigCircle.getCenterY()) {
                
            }
            if(mouseLocation.getX()>bigCircle.getCenterX() && mouseLocation.getY()<bigCircle.getCenterY()) {
                zoomIn(getChart());
            }
            if(mouseLocation.getX()>bigCircle.getCenterX() && mouseLocation.getY()>bigCircle.getCenterY()) {
                zoomOut(getChart());            
            }
            if(mouseLocation.getX()<bigCircle.getCenterX() && mouseLocation.getY()>bigCircle.getCenterY()) {
                
            }
            event.consume();
        }

    };
             
    private void updateCursorTool(MouseEvent event) {
        Point2D mouseLocation = getLocationInPlotArea(event);
        
        if(getChart() instanceof DensityChartFX<?, ?>){ 
            mouseLocation = mouseLocation.add(new Point2D(getXValueAxis().getLayoutX(),0.0));
        } 
     
        if (smallCircle.contains(mouseLocation)){
            tool = new iconParser();
            tool.setLocation(new Point2D(smallCircle.getCenterX(),smallCircle.getCenterY()));
            tool.setChartForIcon(getChart());
            tool.updateIcon(true, false, false, 0,smallCircle.getCenterX(),smallCircle.getCenterY());
            event.consume();
        }
     
        else if(bigCircle.contains(mouseLocation)){
            Double yVal = mouseLocation.getY()-bigCircle.getCenterY();
            Double xVal = mouseLocation.getX()-bigCircle.getCenterX();

            if (Math.atan(Math.abs(yVal)/xVal)<Math.PI/4 && Math.atan(Math.abs(yVal)/xVal)>0){
                tool = new iconParser();
                tool.setLocation(new Point2D(bigCircle.getCenterX(),bigCircle.getCenterY()));
                tool.setChartForIcon(getChart());
                tool.updateIcon(false, true, false, 3,bigCircle.getCenterX(),bigCircle.getCenterY());
            }
            else if (Math.atan(Math.abs(yVal)/xVal)>-Math.PI/4 && Math.atan(Math.abs(yVal)/xVal)<0){
                tool = new iconParser();
                tool.setLocation(new Point2D(bigCircle.getCenterX(),bigCircle.getCenterY()));
                tool.setChartForIcon(getChart());
                tool.updateIcon(false, true, false, 1,bigCircle.getCenterX(),bigCircle.getCenterY());
            }
            else if (yVal<0) {
                tool = new iconParser();
                tool.setLocation(new Point2D(bigCircle.getCenterX(),bigCircle.getCenterY()));
                tool.setChartForIcon(getChart());
                tool.updateIcon(false, true, false, 2,bigCircle.getCenterX(),bigCircle.getCenterY());
            }

            else if (yVal>0) {
                tool = new iconParser();
                tool.setLocation(new Point2D(bigCircle.getCenterX(),bigCircle.getCenterY()));
                tool.setChartForIcon(getChart());
                tool.updateIcon(false, true, false, 4,bigCircle.getCenterX(),bigCircle.getCenterY());
            }
            event.consume();
        }


        else if(outerRectangle.contains(mouseLocation)){
            if(mouseLocation.getX()<bigCircle.getCenterX() && mouseLocation.getY()<bigCircle.getCenterY()) {
                tool = new iconParser();
                tool.setLocation(new Point2D(bigCircle.getCenterX(),bigCircle.getCenterY()));
                tool.updateIcon(false, false, true, 1,bigCircle.getCenterX(),bigCircle.getCenterY());
            }
            if(mouseLocation.getX()>bigCircle.getCenterX() && mouseLocation.getY()<bigCircle.getCenterY()) {
                tool = new iconParser();
                tool.setLocation(new Point2D(bigCircle.getCenterX(),bigCircle.getCenterY()));
                tool.updateIcon(false, false, true, 2,bigCircle.getCenterX(),bigCircle.getCenterY());
            }
            if(mouseLocation.getX()>bigCircle.getCenterX() && mouseLocation.getY()>bigCircle.getCenterY()) {
                tool = new iconParser();
                tool.setLocation(new Point2D(bigCircle.getCenterX(),bigCircle.getCenterY()));
                tool.updateIcon(false, false, true, 3,bigCircle.getCenterX(),bigCircle.getCenterY());
            }
            if(mouseLocation.getX()<bigCircle.getCenterX() && mouseLocation.getY()>bigCircle.getCenterY()) {
                tool = new iconParser();
                tool.setLocation(new Point2D(bigCircle.getCenterX(),bigCircle.getCenterY()));
                tool.updateIcon(false, false, true, 4,bigCircle.getCenterX(),bigCircle.getCenterY());
            }
            event.consume();

        }
        else {
            tool = new iconParser();
            tool.setLocation(new Point2D(bigCircle.getCenterX(),bigCircle.getCenterY()));
        }
         
     
    }
 
    public void panLeft(Chart newChart) {
        this.setChart(newChart);
        if (viewReset){
            resetPlotSize();
            viewReset = false;
        };
        getXValueAxis().setAutoRanging(false);
        getXValueAxis().setLowerBound(getXValueAxis().getLowerBound() - 0.1*plotWidth);
        getXValueAxis().setUpperBound(getXValueAxis().getUpperBound() - 0.1*plotWidth);                  
    }
    
    public void panUp(Chart newChart) {
        this.setChart(newChart);
        if (viewReset){
            resetPlotSize();
            viewReset = false;
        }
        getYValueAxis().setAutoRanging(false);
        getYValueAxis().setUpperBound(getYValueAxis().getUpperBound() + 0.1*plotHeight);
        getYValueAxis().setLowerBound(getYValueAxis().getLowerBound() + 0.1*plotHeight);
   
    } 
    
    public void panRight(Chart newChart) {
        this.setChart(newChart);
        if (viewReset){
            resetPlotSize();
            viewReset = false;
        };
        getXValueAxis().setAutoRanging(false);
        getXValueAxis().setUpperBound(getXValueAxis().getUpperBound() + 0.1*plotWidth);
        getXValueAxis().setLowerBound(getXValueAxis().getLowerBound() + 0.1*plotWidth);
    } 
    
    public void panDown(Chart newChart) {
        this.setChart(newChart);
        if (viewReset){
            resetPlotSize();
            viewReset = false;
        };
        getYValueAxis().setAutoRanging(false);
        getYValueAxis().setLowerBound(getYValueAxis().getLowerBound() - 0.1*plotHeight);
        getYValueAxis().setUpperBound(getYValueAxis().getUpperBound() - 0.1*plotHeight);
    } 
    
    private void resetPlotSize() {
        plotHeight = getYValueAxis().getUpperBound() - getYValueAxis().getLowerBound();
        plotWidth = getXValueAxis().getUpperBound() - getXValueAxis().getLowerBound();
    }
    
    public void autoScale(Chart newChart){
        this.setChart(newChart);
        getXValueAxis().setAutoRanging(true);
        getYValueAxis().setAutoRanging(true);        
    }
    
    public void zoomIn(Chart newChart){
        this.setChart(newChart);
        getXValueAxis().setAutoRanging(false);
        getYValueAxis().setAutoRanging(false);  
        this.setChart(newChart);
        if (viewReset){
            resetPlotSize();
            viewReset = false;
        };
        zoomStack.addFirst(new Rectangle2D(getXValueAxis().getLowerBound(), getYValueAxis().getLowerBound(), getXValueAxis().getUpperBound()
                - getXValueAxis().getLowerBound(), getYValueAxis().getUpperBound() - getYValueAxis().getLowerBound()));
        getXValueAxis().setUpperBound(getXValueAxis().getUpperBound() - 0.1*plotWidth);
        getXValueAxis().setLowerBound(getXValueAxis().getLowerBound() + 0.1*plotWidth);
        getYValueAxis().setUpperBound(getYValueAxis().getUpperBound()-0.1*plotHeight);
        getYValueAxis().setLowerBound(getYValueAxis().getLowerBound()+0.1*plotHeight);
    }
    
    public void zoomOut(Chart newChart){
        this.setChart(newChart);
        getXValueAxis().setAutoRanging(false);
        getYValueAxis().setAutoRanging(false); 
        this.setChart(newChart);
        getXValueAxis().setAutoRanging(false);
        getYValueAxis().setAutoRanging(false);  
        this.setChart(newChart);
        if (viewReset){
            resetPlotSize();
            viewReset = false;
        };
        zoomStack.addFirst(new Rectangle2D(getXValueAxis().getLowerBound(), getYValueAxis().getLowerBound(), getXValueAxis().getUpperBound()
                - getXValueAxis().getLowerBound(), getYValueAxis().getUpperBound() - getYValueAxis().getLowerBound()));
        getXValueAxis().setUpperBound(getXValueAxis().getUpperBound() + 0.1*plotWidth);
        getXValueAxis().setLowerBound(getXValueAxis().getLowerBound() - 0.1*plotWidth);
        getYValueAxis().setUpperBound(getYValueAxis().getUpperBound() + 0.1*plotHeight);
        getYValueAxis().setLowerBound(getYValueAxis().getLowerBound() - 0.1*plotHeight);
    }
    
   
}


