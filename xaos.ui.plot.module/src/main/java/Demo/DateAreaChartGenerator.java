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


import java.util.Date;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import se.europeanspallationsource.xaos.ui.plot.AreaChartFX;
import se.europeanspallationsource.xaos.ui.plot.DateAxis;
import se.europeanspallationsource.xaos.ui.plot.LogAxis;
import se.europeanspallationsource.xaos.ui.plot.NumberAxis;
import se.europeanspallationsource.xaos.ui.plot.plugins.Plugins;

import static Demo.ChartGenerator.generateDateData;


/**
 * @author reubenlindroos
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class DateAreaChartGenerator implements ChartGenerator<Date, Number> {

	private Series<Date, Number> series0;
	private Series<Date, Number> series1;
	private Series<Date, Number> series2;

	@Override
	@SuppressWarnings( "AssignmentToMethodParameter" )
	public XYChart<Date, Number> getNewChart( int numberOfPoints, boolean logXAxis, boolean logYAxis ) {

		//	The following is necessary because the series is not automatically reduced.
		if ( numberOfPoints > 100 ) {
			numberOfPoints = 100;
		}

		DateAxis xAxis = new DateAxis();
		ValueAxis<Number> yAxis = logYAxis ? new LogAxis() : new NumberAxis();

		xAxis.setAnimated(false);
		yAxis.setAnimated(false);

		AreaChartFX<Date, Number> chart = new AreaChartFX<>(xAxis, yAxis);

		chart.setTitle("Date Area Test Data");
		chart.setAnimated(false);
		chart.setOnMouseClicked(event -> chart.requestFocus());
		chart.getPlugins().addAll(Plugins.all());
		
		if ( series0 == null ) {

			series0 = new Series<>();
			series0.setName("Generated test data-horizontal");
			series0.setData(generateDateData(numberOfPoints));

			series1 = new Series<>();
			series1.setName("Generated test data-vertical");
			series1.setData(generateDateData(numberOfPoints));

			series2 = new Series<>();
			series2.setName("Generated test data-longitudinal");
			series2.setData(generateDateData(numberOfPoints));

		}

		chart.getData().add(series0);
		chart.getData().add(series1);
		chart.getData().add(series2);

		chart.setHVLSeries(0, 1, 2);

		return chart;

	}

}
