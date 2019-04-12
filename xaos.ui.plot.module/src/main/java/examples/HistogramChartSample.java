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

package examples;

import chart.HistogramChartFX;
import chart.NumberAxis;
import java.util.Random;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import plugins.ErrorBars;
import se.europeanspallationsource.xaos.ui.plot.plugins.Plugins;
import se.europeanspallationsource.xaos.ui.plot.util.ErrorSeries;

public class HistogramChartSample extends Application{
    private static final Random RANDOM = new Random(System.currentTimeMillis());
    private static final int NB_OF_POINTS = 10000;
    private ObservableList<Double> dataForPlot = FXCollections.observableArrayList();
    
    
    @Override
    public void start(Stage stage) throws Exception {
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setForceZeroInRange(false);
        Double[] data = generateDoubleArray(0.0,15,100.0,NB_OF_POINTS);
        dataForPlot.addAll(data);
        HistogramChartFX<Number,Number> chart = new HistogramChartFX<Number,Number>(xAxis,yAxis,dataForPlot, 20);
        
        chart.getData().get(0).setName("Gauss Histogram");
        chart.getPlugins().addAll(Plugins.all());
                                      
        chart.setBarGap(1);
        chart.setLegendVisible(true);       
        
        ObservableList<ErrorSeries.ErrorData<Number,Number>> error0 = FXCollections.observableArrayList();
        ObservableList<ErrorSeries.ErrorData<Number,Number>> error1 = FXCollections.observableArrayList();       
        ObservableList<ErrorSeries.ErrorData<Number,Number>> error2 = FXCollections.observableArrayList();
       
      //DataReducingObservableList.Data<Number,Number> error0 = FXCollections.observableArrayList();
       
        for (int ind = 0; ind < chart.getData().get(0).getData().size(); ind++) {
            error0.add(new ErrorSeries.ErrorData<Number,Number>(chart.getData().get(0).getData().get(ind),0,0.2));
            //error1.add(new ErrorSeries.ErrorData<Number, Number>(series1.getData().get(ind),0.15,0.0));
            //error2.add(new ErrorSeries.ErrorData<Number, Number>(series2.getData().get(ind),0.05,0.1));          
        }        

        //Series 0
        //chart.setSeriesAsVertical(0);//red
        chart.getPlugins().add(new ErrorBars(new ErrorSeries(error0),0));      
        
        
        Label infoLabel = new Label();
        infoLabel.setText("Zoom-in: drag with left-mouse, Zoom-out: right-click, Zoom-origin: right-click + CTRL, Pan: drag with left-mouse + CTRL or keyboard arrows");

        BorderPane borderPane = new BorderPane(chart);
        chart.setFocusTraversable(true);        
        
        borderPane.setBottom(infoLabel);
        Scene scene = new Scene(borderPane, 800, 600);


        stage.setScene(scene);
        stage.show();        
                  
    }
    public static Double[] generateDoubleArray(Double firstValue, int variance, Double mean, int size) {
        Double[] data = new Double[size];
        data[0] = variance * RANDOM.nextGaussian()+mean;
        for (int i = 1; i < data.length; i++) {
            int sign = RANDOM.nextBoolean() ? 1 : -1;
            data[i] = variance * RANDOM.nextGaussian()+mean;//*sign;
        }
        return data;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

