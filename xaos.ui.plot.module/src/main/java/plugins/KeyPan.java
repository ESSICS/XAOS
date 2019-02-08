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

import chart.XYChartPlugin;
import javafx.event.EventHandler;
import javafx.scene.chart.Chart;
import javafx.scene.input.KeyEvent;

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