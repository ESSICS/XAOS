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

package Demo;

import chart.BarChartFX;
import chart.NumberAxis;
import chart.LogAxis;
import java.util.Random;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import se.europeanspallationsource.xaos.ui.plot.Plugins;

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
        chart.getPlugins().addAll(Plugins.all());
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
