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
package se.europeanspallationsource.xaos.ui.plot.demo;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import se.europeanspallationsource.xaos.ui.plot.HistogramChartFX;
import se.europeanspallationsource.xaos.ui.plot.NumberAxis;
import se.europeanspallationsource.xaos.ui.plot.plugins.Plugins;



/**
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class HistogramChartGenerator implements ChartGenerator<Number, Number> {

	private static Double[] generateDoubleArray( double variance, double mean, int size ) {

		Double[] data = new Double[size];

		for ( int i = 0; i < data.length; i++ ) {
			data[i] = variance * RANDOM.nextGaussian() + mean;
		}

		return data;

	}

	@Override
	public XYChart<Number, Number> getNewChart( int numberOfPoints, boolean logXAxis, boolean logYAxis ) {

		ObservableList<Double> data = FXCollections.observableArrayList(generateDoubleArray(15.0, 100.0, numberOfPoints));
		HistogramChartFX<Number, Number> chart = HistogramChartFX.of(data, 40, "Histogram Data");

		chart.getXAxis().setAnimated(false);
		((NumberAxis) chart.getXAxis()).setForceZeroInRange(false);
		chart.getYAxis().setAnimated(false);

		chart.setTitle("Histogram Test Data");
		chart.setAnimated(false);
		chart.setOnMouseClicked(event -> chart.requestFocus());
		chart.getPlugins().addAll(Plugins.all());
        chart.setBarGap(1);

		return chart;

	}

}
