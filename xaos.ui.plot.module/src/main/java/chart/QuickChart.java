/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chart;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
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
