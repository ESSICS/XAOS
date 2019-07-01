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


import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import se.europeanspallationsource.xaos.ui.plot.LineChartFX;
import se.europeanspallationsource.xaos.ui.plot.LogAxis;
import se.europeanspallationsource.xaos.ui.plot.NumberAxis;
import se.europeanspallationsource.xaos.ui.plot.data.DataReducingSeries;
import se.europeanspallationsource.xaos.ui.plot.plugins.Plugins;

import static Demo.ChartGenerator.generateData;


/**
 * @author reubenlindroos
 * @author claudio.rosati@esss.se
 */
public class LineChartGenerator implements ChartGenerator {

	private DataReducingSeries<Number, Number> series0;
	private DataReducingSeries<Number, Number> series1;
	private DataReducingSeries<Number, Number> series2;
	private DataReducingSeries<Number, Number> series3;
	private DataReducingSeries<Number, Number> series4;
	private DataReducingSeries<Number, Number> series5;
	private DataReducingSeries<Number, Number> series6;
	private DataReducingSeries<Number, Number> series7;

	@Override
	public XYChart<Number, Number> getNewChart( int numberOfPoints, boolean logXAxis, boolean logYAxis ) {

		ValueAxis<Number> xAxis = logXAxis ? new LogAxis() : new NumberAxis();
		ValueAxis<Number> yAxis = logYAxis ? new LogAxis() : new NumberAxis();

		xAxis.setAnimated(false);
		yAxis.setAnimated(false);

		LineChartFX<Number, Number> chart = new LineChartFX<>(xAxis, yAxis);

		chart.setTitle("Line Test Data");
		chart.setAnimated(false);
		chart.setOnMouseClicked(event -> chart.requestFocus());
		chart.getPlugins().addAll(Plugins.all());

		if ( series0 == null ) {

			series0 = new DataReducingSeries<>();
			series0.setName("Generated test data-horizontal");
			series0.setData(generateData(numberOfPoints));

			series1 = new DataReducingSeries<>();
			series1.setName("Generated test data-vertical");
			series1.setData(generateData(numberOfPoints));

			series2 = new DataReducingSeries<>();
			series2.setName("Generated test data-longitudinal");
			series2.setData(generateData(numberOfPoints));

			series3 = new DataReducingSeries<>();
			series3.setName("Generated test data 1");
			series3.setData(generateData(numberOfPoints));

			series4 = new DataReducingSeries<>();
			series4.setName("Generated test data 2");
			series4.setData(generateData(numberOfPoints));

			series5 = new DataReducingSeries<>();
			series5.setName("Generated test data 3");
			series5.setData(generateData(numberOfPoints));

			series6 = new DataReducingSeries<>();
			series6.setName("Generated test data 4");
			series6.setData(generateData(numberOfPoints));

			series7 = new DataReducingSeries<>();
			series7.setName("Generated test data 5");
			series7.setData(generateData(numberOfPoints));

		}

		chart.getData().add(series0.getSeries());
		chart.getData().add(series1.getSeries());
		chart.getData().add(series2.getSeries());
		chart.getData().add(series3.getSeries());
		chart.getData().add(series4.getSeries());
		chart.getData().add(series5.getSeries());
		chart.getData().add(series6.getSeries());
		chart.getData().add(series7.getSeries());

		chart.setHVLSeries(0, 1, 2);

		return chart;

	}

}
