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

import se.europeanspallationsource.xaos.ui.plot.LineChartFX;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

/**
 *
 * @author reubenlindroos
 */
public class QuickChart{
    private final LineChartFX<Number,Number> chart = new LineChartFX<Number,Number>(new NumberAxis(),new NumberAxis());
    private Scene scene = new Scene(chart, 800, 600);
    private Stage newWindow = new Stage();
    public QuickChart(ObservableList<Double> data){
        
        XYChart.Series<Number,Number> dataSet = new XYChart.Series<Number,Number>();
        
        
        for (Integer step = 0; step<data.size();step++ ) {
            XYChart.Data dataPoint = new XYChart.Data(step,data.get(step));
            dataSet.getData().add(dataPoint);
        }
       addData(dataSet);
    }
    
    public QuickChart(ObservableList<Double> xVals, ObservableList<Double> data) {
        if (xVals.size()!= data.size()) {
            throw new IllegalArgumentException("arrays for x and y axis must be of the same size");
        }
        
        else {
            XYChart.Series<Number,Number> dataSet = new XYChart.Series<Number,Number>();
            for (Integer step = 0; step<data.size();step++ ) {
                XYChart.Data dataPoint = new XYChart.Data(xVals.get(step),data.get(step));
                
            }
            
            addData(dataSet);
            
        }
    }
    private void addData(XYChart.Series<Number,Number> series){
        chart.getData().add(series);
        newWindow.setTitle("quickChart");
        newWindow.setScene(scene);
        newWindow.show();
    }
    
    public XYChart getChart() {
        return chart;
    }
    

}
