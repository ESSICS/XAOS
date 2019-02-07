/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package examples;
import chart.BarChartFX;

import chart.NumberAxis;
import chart.XYChartPlugin;
import plugins.ErrorBars;
import plugins.DataPointTooltip;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import plugins.AreaValueTooltip;
import plugins.CoordinatesLabel;
import plugins.CoordinatesLines;
import plugins.CursorTool;
import plugins.KeyPan;
import plugins.Pan;
import plugins.PropertyMenu;
import plugins.Zoom;
import util.ErrorSeries;

public class HorBarChartSample extends Application {
    final static String AUSTRIA = "Austria";
    final static String BRAZIL = "Brazil";
    final static String FRANCE = "France";
    final static String ITALY = "Italy";
    final static String USA = "USA";
    final static Integer NB_OF_COUNTRIES = 5;
 
    @Override public void start(Stage stage) {
        stage.setTitle("Bar Chart Sample");
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        
        final BarChartFX<Number,String> bc = new BarChartFX<>(yAxis,xAxis);
        
        bc.setTitle("Country Summary");
       ObservableList<XYChartPlugin> pluginList = FXCollections.observableArrayList();
         
       pluginList.addAll(new CursorTool(), new KeyPan(), new CoordinatesLines(), 
            new Zoom(), new Pan(), new CoordinatesLabel(), new DataPointTooltip(), new AreaValueTooltip(), new PropertyMenu());
         
        bc.addChartPlugins(pluginList);

        xAxis.setLabel("Country");       
        yAxis.setLabel("Value");
 
        XYChart.Series series1 = new XYChart.Series();
        series1.setName("2003");       
        series1.getData().add(new XYChart.Data( 25601.34, AUSTRIA));
        series1.getData().add(new XYChart.Data(20148.82,BRAZIL ));
        series1.getData().add(new XYChart.Data( 10000,FRANCE));
        series1.getData().add(new XYChart.Data( 35407.15,ITALY));
        series1.getData().add(new XYChart.Data( 12000, USA ));      
        
        XYChart.Series series2 = new XYChart.Series();
        series2.setName("2004");
        series2.getData().add(new XYChart.Data( 25501.34, AUSTRIA));
        series2.getData().add(new XYChart.Data(20128.82,BRAZIL ));
        series2.getData().add(new XYChart.Data( 20000,FRANCE));
        series2.getData().add(new XYChart.Data( 33407.15,ITALY));
        series2.getData().add(new XYChart.Data( 12400, USA )); 
        XYChart.Series series3 = new XYChart.Series();
        series3.setName("2005");
        series3.getData().add(new XYChart.Data( 25201.34, AUSTRIA));
        series3.getData().add(new XYChart.Data(20158.82,BRAZIL ));
        series3.getData().add(new XYChart.Data( 10200,FRANCE));
        series3.getData().add(new XYChart.Data( 35207.15,ITALY));
        series3.getData().add(new XYChart.Data( 12300, USA ));   
        
        
        Scene scene  = new Scene(bc,800,600);
        bc.getData().addAll(series1, series2, series3);
       
                          
        
        ObservableList<ErrorSeries.ErrorData<Number, String>> error0 = FXCollections.observableArrayList();
        ObservableList<ErrorSeries.ErrorData<Number, String>> error1 = FXCollections.observableArrayList();
        ObservableList<ErrorSeries.ErrorData<Number, String>> error2 = FXCollections.observableArrayList();

        for (int ind = 0; ind < NB_OF_COUNTRIES; ind++) {

         error0.add(new ErrorSeries.ErrorData<>(bc.getData().get(0).getData().get(ind),0.2));
         error1.add(new ErrorSeries.ErrorData<>(bc.getData().get(1).getData().get(ind),0.1));
         error2.add(new ErrorSeries.ErrorData<>(bc.getData().get(2).getData().get(ind),0.05));
        }
       
       
       
       
       ObservableList<String> categories = FXCollections.observableArrayList(AUSTRIA, BRAZIL, FRANCE, ITALY, USA
    );
       CategoryAxis cAxis = new CategoryAxis(categories);
       
       
       bc.getChartPlugins().addAll(new ErrorBars(new ErrorSeries<>(error0),0),new ErrorBars(new ErrorSeries<>(error1),1), new ErrorBars(new ErrorSeries<>(error2),2));
        stage.setScene(scene);
        
        stage.show();
    }
 
    public static void main(String[] args) {
        launch(args);
    }
}