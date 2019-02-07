/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plugins;

import static util.Assert.assertValueAxis;
import chart.XYChartPlugin;
import chart.NumberAxis.*;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.chart.Chart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author reubenlindroos
 */
public class KeyPan extends XYChartPlugin{
    
    /**
     * constant distance between getUpperBound() and getLowerBound() for axes.
     * Updating these values whilst panning will cause errors, therefore only one of them is updated
     * and the constants are used in order to decide the location of the other. 
     */
    private static double plotWidth;
    private static double plotHeight;
    @Override
    protected void chartDisconnected(Chart oldChart) {
        
        oldChart.removeEventHandler(KeyEvent.KEY_PRESSED, keyPressHandler);
    }

    @Override
    protected void chartConnected(Chart newChart) {
        
        newChart.addEventHandler(KeyEvent.KEY_PRESSED, keyPressHandler);
    
    }
    /**
     * keyPressHandler (EventHander<KeyEvent>)
     *       if (Zoom() has been performed) 
     *              plotWidth, plotHeight = current plot width, current plot height (UpperBound - LowerBound)
     *          pan:
     *              leading edge += 1/plotWidth
     *              trailing edge = leading edge - plotWidth (or plotHeight)      
     */
    private final EventHandler<KeyEvent> keyPressHandler = new EventHandler<KeyEvent>() {
       
        @Override
        public void handle(KeyEvent ke) {
            switch(ke.getCode()){
                case UP:
                    if (viewReset){
                        resetPlotSize();
                        viewReset = false;
                    }
                    getYValueAxis().setAutoRanging(false);
                    getYValueAxis().setUpperBound(getYValueAxis().getUpperBound() + 0.1*plotHeight);
                    getYValueAxis().setLowerBound(getYValueAxis().getLowerBound() + 0.1*plotHeight);
                    
                    break;
                    
               case DOWN:
                    if (viewReset){
                        resetPlotSize();
                        viewReset = false;
                    };
                    getYValueAxis().setAutoRanging(false);
                    getYValueAxis().setLowerBound(getYValueAxis().getLowerBound() - 0.1*plotHeight);
                    getYValueAxis().setUpperBound(getYValueAxis().getUpperBound() - 0.1*plotHeight);
                    break;
                    
                case RIGHT:
                    if (viewReset){
                        resetPlotSize();
                        viewReset = false;
                    };
                    getXValueAxis().setAutoRanging(false);
                    getXValueAxis().setUpperBound(getXValueAxis().getUpperBound() + 0.1*plotWidth);
                    getXValueAxis().setLowerBound(getXValueAxis().getLowerBound() + 0.1*plotWidth);
                    break;
                    
                case LEFT:
                    if (viewReset){
                        resetPlotSize();
                        viewReset = false;
                    };
                    getXValueAxis().setAutoRanging(false);
                    getXValueAxis().setLowerBound(getXValueAxis().getLowerBound() - 0.1*plotWidth);
                    getXValueAxis().setUpperBound(getXValueAxis().getUpperBound() - 0.1*plotWidth);        
                    break;
                    
            }
            viewReset=true;
            ke.consume();
        }
    };
  
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
}