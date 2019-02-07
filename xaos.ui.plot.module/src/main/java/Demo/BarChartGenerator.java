/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Demo;

import chart.BarChartFX;
import chart.LineChartFX;
import chart.NumberAxis;
import chart.data.DataReducingSeries;
import chart.LogAxis;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import plugins.CoordinatesLabel;
import plugins.CoordinatesLines;
import plugins.DataPointTooltip;
import plugins.Pan;
import plugins.Zoom;
import plugins.CursorTool;
import plugins.KeyPan;

/**
 *
 * @author reubenlindroos
 */
public class BarChartGenerator {
    private static String usefulCode = "Useful Code";
    private static String bugs = "Bugs";
    private BarChartFX<String,Number> chart ;
    
    private CategoryAxis xAxis = new CategoryAxis();
    private ValueAxis yAxis = new NumberAxis();
    
     private static final Random RANDOM = new Random(System.currentTimeMillis());
    
     
  public BarChartFX getChart() {
        generateChart();
        return chart;
 
       
  }

    public void generateChart() {
        chart = new BarChartFX<String,Number>(xAxis,yAxis);
        chart.setTitle("Employee quarterly Summary");
        chart.getChartPlugins().addAll(new DataPointTooltip());
        XYChart.Series series1 = new XYChart.Series();
        series1.setName("Natalia");       
        series1.getData().add(new XYChart.Data(usefulCode, 256.34));
        series1.getData().add(new XYChart.Data(bugs, 20));
              
        
        XYChart.Series series2 = new XYChart.Series();
        series2.setName("Reuben");
        series2.getData().add(new XYChart.Data(usefulCode, 84.85));
        series2.getData().add(new XYChart.Data(bugs, 419));
       
        XYChart.Series series3 = new XYChart.Series();
        series3.setName("Any Windows Dev");
        series3.getData().add(new XYChart.Data(usefulCode, 10));
        series3.getData().add(new XYChart.Data(bugs, 1009));
        chart.getData().addAll(series1,series2,series3);
    }

    public BarChartFX setYLogAxis(Integer nb_of_points) {
        yAxis = new LogAxis();
        generateChart();
        return chart;
    }

}
