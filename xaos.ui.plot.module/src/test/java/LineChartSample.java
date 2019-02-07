/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */



import chart.LineChartFX;
import chart.NumberAxis;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import chart.data.DataReducingSeries;
import plugins.CoordinatesLabel;
import plugins.CoordinatesLines;
import plugins.DataPointTooltip;
import plugins.Pan;
import plugins.Zoom;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart.SortingPolicy;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import chart.data.DataReducingSeries;
import plugins.AxisMode;
import plugins.CoordinatesLabel;
import plugins.CursorTool;
import plugins.KeyPan;
import plugins.DataPointTooltip;
import plugins.Pan;
import plugins.Zoom;

public class LineChartSample extends Application {
    private static final Random RANDOM = new Random(System.currentTimeMillis());

    private static final int NB_OF_POINTS = 10;
    private static final int PERIOD_MS = 500;

    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
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

        stage.setTitle("Line Chart Sample");
        final NumberAxis xAxis = new NumberAxis();
        xAxis.setAnimated(false);
        final NumberAxis yAxis = new NumberAxis();
        yAxis.setAnimated(false);
        final LineChartFX<Number, Number> chart = new LineChartFX<Number, Number>(xAxis, yAxis);
        chart.setTitle("Test data");
        chart.setAnimated(false);
//        chart.setAxisSortingPolicy(SortingPolicy.NONE);
//        chart.setCreateSymbols(false);
        chart.getChartPlugins().addAll(new CursorTool(), new KeyPan(), //new CoordinatesLines(), 
               new Zoom(), new Pan(), new CoordinatesLabel(), new DataPointTooltip() );
               
        series1 = new DataReducingSeries<>();
        series1.setName("Generated test data-horizontal");
        series1.setData(generateData(NB_OF_POINTS));
        chart.getData().add(series1.getSeries());
        
        series2 = new DataReducingSeries<>();
        series2.setName("Generated test data-vertical");
        series2.setData(generateData(NB_OF_POINTS));
        chart.getData().add(series2.getSeries());
        
        chart.setSeriesAsHorizontal(0); //red
        chart.setSeriesAsVertical(1); //blue
        
        Label infoLabel = new Label();
        infoLabel.setText("Zoom-in: drag with left-mouse, Zoom-out: right-click, Zoom-origin: right-click + CTRL, Pan: drag with left-mouse + CTRL");

        BorderPane borderPane = new BorderPane(chart);
        
        // THIS IS THE FIX (BY JOHAN RAVNBORG) => needs to be included in order for layout to recognise key bindings.
        // NOTE: Should be somehow implemented from a non-main class (LineFX/ plugin manager/ xyChartPlugin)
        
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