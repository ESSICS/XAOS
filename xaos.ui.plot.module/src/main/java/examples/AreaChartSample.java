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

import chart.AreaChartFX;
import chart.NumberAxis;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import plugins.CoordinatesLines;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import chart.data.DataReducingSeries;
import plugins.CoordinatesLabel;
import se.europeanspallationsource.xaos.ui.plot.plugins.Navigator;
import se.europeanspallationsource.xaos.ui.plot.plugins.KeyboardAccelerators;
import plugins.DataPointTooltip;
import plugins.Pan;
import plugins.Zoom;
import plugins.ErrorBars;
import plugins.AreaValueTooltip;
import plugins.PropertyMenu;
import se.europeanspallationsource.xaos.ui.plot.util.ErrorSeries;

public class AreaChartSample extends Application {
    private static final Random RANDOM = new Random(System.currentTimeMillis());

    private static final int NB_OF_POINTS = 20;
    private static final int PERIOD_MS = 500;

    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private DataReducingSeries<Number, Number> series0;
    private DataReducingSeries<Number, Number> series1;
    private DataReducingSeries<Number, Number> series2;

    @Override
    public void start(Stage stage) {
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Platform.exit();
                System.exit(0);
            }
        });

        stage.setTitle("Area Chart Sample");
        final NumberAxis xAxis = new NumberAxis();
        xAxis.setAnimated(false);
        final NumberAxis yAxis = new NumberAxis();
        yAxis.setAnimated(false);
        final AreaChartFX<Number,Number> chart = new AreaChartFX<Number, Number>(xAxis, yAxis);
        chart.setTitle("Test data");
        chart.setAnimated(false);
//        chart.setAxisSortingPolicy(SortingPolicy.NONE);
//        chart.setCreateSymbols(false);
        chart.getChartPlugins().addAll(new Navigator(),
			new KeyboardAccelerators(),
			new CoordinatesLines(),
            new Zoom(), 
			new Pan(),
			new CoordinatesLabel(),
			new DataPointTooltip(),
			new AreaValueTooltip(),
			new PropertyMenu()
		);
               
        series0 = new DataReducingSeries<>();
        series0.setName("Generated test data-horizontal");
        series0.setData(generateData(NB_OF_POINTS));
        chart.getData().add(series0.getSeries());
        
        series1 = new DataReducingSeries<>();
        series1.setName("Generated test data-vertical");
        series1.setData(generateData(NB_OF_POINTS));
        chart.getData().add(series1.getSeries());
        
        series2 = new DataReducingSeries<>();
        series2.setName("Generated test data-longitudinal");
        series2.setData(generateData(NB_OF_POINTS));
        chart.getData().add(series2.getSeries());               

        ObservableList<ErrorSeries.ErrorData<Number,Number>> error0 = FXCollections.observableArrayList();
        ObservableList<ErrorSeries.ErrorData<Number,Number>> error1 = FXCollections.observableArrayList();       
        ObservableList<ErrorSeries.ErrorData<Number,Number>> error2 = FXCollections.observableArrayList();
       
      //DataReducingObservableList.Data<Number,Number> error0 = FXCollections.observableArrayList();
       
        for (int ind = 0; ind < NB_OF_POINTS; ind++) {
            error0.add(new ErrorSeries.ErrorData<Number,Number>(series0.getData().get(ind),0.2,0.2));
            error1.add(new ErrorSeries.ErrorData<Number, Number>(series1.getData().get(ind),0.15,0.0));
            error2.add(new ErrorSeries.ErrorData<Number, Number>(series2.getData().get(ind),0.05,0.1));          
        }        

        //Series 0
        chart.setSeriesAsHorizontal(0);//red
        chart.getChartPlugins().add(new ErrorBars(new ErrorSeries(error0),0));


        //Series 1
        chart.setSeriesAsVertical(1);//blue
        chart.getChartPlugins().add(new ErrorBars(new ErrorSeries(error1),1));

        //Series 2
        chart.setSeriesAsLongitudinal(2);//horrible green
        chart.getChartPlugins().add(new ErrorBars(new ErrorSeries(error2),2));
       
        
        Label infoLabel = new Label();
        infoLabel.setText("Zoom-in: drag with left-mouse, Zoom-out: right-click, Zoom-origin: right-click + CTRL, Pan: drag with left-mouse + CTRL or keyboard arrows");

        BorderPane borderPane = new BorderPane(chart);
        
        //chart.getChartPlugins().add(new ErrorBars(chart));

        //stage.widthProperty().addListener((obs, oldVal, newVal) -> {
        //   System.out.println(chart.getXAxis().getChildrenUnmodifiable());
        //   System.out.println(chart.getYAxis());
        //  }); 
        
        
        
        
        
        chart.setFocusTraversable(true);
        
        
        borderPane.setBottom(infoLabel);
        Scene scene = new Scene(borderPane, 800, 600);

//         executor.scheduleAtFixedRate(new ChartUpdater(), 2000, PERIOD_MS, TimeUnit.MILLISECONDS);

        stage.setScene(scene);
        stage.show();
    }

//    private static ObservableList<XYChart.Data<Number, Number>> generateData(int nbOfPoints) {
//        List<XYChart.Data<Number, Number>> data = new ArrayList<>(nbOfPoints);
//        data.add(new XYChart.Data<>(0,0));
//        data.add(new XYChart.Data<>(3,1));
//        data.add(new XYChart.Data<>(2,1));
//        data.add(new XYChart.Data<>(4,2));
//        data.add(new XYChart.Data<>(2,1.5));
//        return FXCollections.observableArrayList(data);
//    }

    private static ObservableList<XYChart.Data<Number, Number>> generateData(int nbOfPoints) {
        int[] yValues = generateIntArray(0, 5, nbOfPoints);
        List<XYChart.Data<Number, Number>> data = new ArrayList<>(nbOfPoints);
        for (int i = 0; i < yValues.length; i++) {
            data.add(new XYChart.Data<Number, Number>(i, yValues[i]));
        }
        return FXCollections.observableArrayList(data);
    }
    public static int[] generateIntArray(int firstValue, int variance, int size) {
        int[] data = new int[size];
        data[0] = firstValue;
        for (int i = 1; i < data.length; i++) {
            int sign = RANDOM.nextBoolean() ? 1 : -1;
            data[i] = data[i - 1] + (int) (variance * RANDOM.nextDouble()) * sign;
        }
        return data;
    }

    private class ChartUpdater implements Runnable {
        @Override
        public void run() {
            try {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        long s1 = System.currentTimeMillis();
                        series1.getData().setAll(generateData(NB_OF_POINTS));
                        series2.getData().setAll(generateData(NB_OF_POINTS));
                        long s2 = System.currentTimeMillis();
                        System.out.println("SetData: " + (s2 - s1));
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
