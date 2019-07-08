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


import javafx.geometry.Side;
import javafx.scene.chart.Chart;
import se.europeanspallationsource.xaos.ui.plot.DensityChartFX;
import se.europeanspallationsource.xaos.ui.plot.DensityChartFX.Data;
import se.europeanspallationsource.xaos.ui.plot.DensityChartFX.DefaultData;
import se.europeanspallationsource.xaos.ui.plot.NumberAxis;
import se.europeanspallationsource.xaos.ui.plot.plugins.Plugins;


/**
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class DensityChartGenerator implements ChartGenerator<Number, Number> {

	private static final int NB_POINTS = 101;

	private static Number[] toNumbers( double[] array ) {

		Number[] result = new Number[array.length];

		for ( int i = 0; i < result.length; i++ ) {
			result[i] = array[i];
		}

		return result;

	}

	public Data<Number, Number> createImage( double meanx, double rmsx, double meany, double rmsy ) {

		double[] xValues = new double[NB_POINTS];
		double[] yValues = new double[NB_POINTS];
		double[][] zValues = new double[xValues.length][yValues.length];

		for ( int xIndex = 0; xIndex < xValues.length; xIndex++ ) {
			xValues[xIndex] = ( meanx - 10.0 * rmsx ) + xIndex / ( xValues.length - 1.0 ) * 20.0 * rmsx;
			yValues[xIndex] = ( meany - 10.0 * rmsy ) + xIndex / ( yValues.length - 1.0 ) * 20.0 * rmsy;
		}

		double minVal = 10.0;

		for ( int yIndex = 0; yIndex < yValues.length; yIndex++ ) {
			for ( int xIndex = 0; xIndex < xValues.length; xIndex++ ) {
				zValues[xIndex][yIndex] = 100 * Math.exp(-( xValues[xIndex] - meanx ) * ( xValues[xIndex] - meanx ) / ( 2 * rmsx * rmsx )) * Math.exp(-( yValues[yIndex] - meany ) * ( yValues[yIndex] - meany ) / ( 2 * rmsy * rmsy )) + 0.05 * RANDOM.nextGaussian();
				minVal = Math.min(zValues[xIndex][yIndex], minVal);
			}
		}

		return new DefaultData<>(toNumbers(xValues), toNumbers(yValues), zValues);

	}

	@Override
	public Chart getNewChart( int numberOfPoints, boolean logXAxis, boolean logYAxis ) {

		NumberAxis xAxis = new NumberAxis();
		NumberAxis yAxis = new NumberAxis();

		xAxis.setAnimated(false);
		xAxis.setAutoRangePadding(0.0);

		yAxis.setAnimated(false);
		yAxis.setAutoRangePadding(0.0);

		DensityChartFX<Number, Number> chart = new DensityChartFX<>(xAxis, yAxis);

		chart.setTitle("Density Test Data");
		chart.setAnimated(false);
		chart.setSmooth(true);
		chart.setProjectionLinesVisible(true);
		chart.setOnMouseClicked(event -> chart.requestFocus());
		chart.getPlugins().addAll(Plugins.all());

		chart.setData(createImage(100.0, 10.55, 5, 3.27));

		chart.setLegendVisible(true);
		chart.setLegendSide(Side.RIGHT);
		chart.setProjectionLinesVisible(true);

		return chart;

	}

}
