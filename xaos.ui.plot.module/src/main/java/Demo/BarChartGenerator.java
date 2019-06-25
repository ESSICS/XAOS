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


import se.europeanspallationsource.xaos.ui.plot.LogAxis;
import se.europeanspallationsource.xaos.ui.plot.NumberAxis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import se.europeanspallationsource.xaos.ui.plot.BarChartFX;
import se.europeanspallationsource.xaos.ui.plot.plugins.Plugins;


/**
 * @author reubenlindroos
 * @author claudio.rosati@esss.se
 */
public class BarChartGenerator implements ChartGenerator {

	private static final String BUGS = "Bugs";
	private static final String CODE = "Useful Code";

	private XYChart.Series<String, Number> series1;
	private XYChart.Series<String, Number> series2;
	private XYChart.Series<String, Number> series3;

	@Override
	@SuppressWarnings( "unchecked" )
	public XYChart<String, Number> getNewChart( int numberOfPoints, boolean logXAxis, boolean logYAxis ) {

		CategoryAxis      xAxis = new CategoryAxis();
		ValueAxis<Number> yAxis = logYAxis ? new LogAxis() : new NumberAxis();

		xAxis.setAnimated(false);
		yAxis.setAnimated(false);

		BarChartFX<String, Number> chart = new BarChartFX<String, Number>(xAxis, yAxis);

		chart.setTitle("Bar Test Data – Employee Quarterly Summary");
		chart.setAnimated(false);
		chart.setOnMouseClicked(event -> chart.requestFocus());

//	TODO:CR add the proper plugins for BarChart.
		chart.getPlugins().addAll(Plugins.all());

		if ( series1 == null ) {

			series1 = new XYChart.Series<>();
			series1.setName("Natalia");
			series1.getData().add(new XYChart.Data<>(CODE, 256.34));
			series1.getData().add(new XYChart.Data<>(BUGS, 20));

			series2 = new XYChart.Series<>();
			series2.setName("Reuben");
			series2.getData().add(new XYChart.Data<>(CODE, 84.85));
			series2.getData().add(new XYChart.Data<>(BUGS, 419));

			series3 = new XYChart.Series<>();
			series3.setName("Any Windows Dev");
			series3.getData().add(new XYChart.Data<>(CODE, 10));
			series3.getData().add(new XYChart.Data<>(BUGS, 1009));

		}

		chart.getData().addAll(series1, series2, series3);

		return chart;

	}

}
