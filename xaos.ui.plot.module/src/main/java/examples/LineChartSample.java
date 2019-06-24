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
package examples;


import chart.LineChartFX;
import chart.NumberAxis;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import se.europeanspallationsource.xaos.ui.plot.data.DataReducingSeries;
import se.europeanspallationsource.xaos.ui.plot.plugins.Plugins;
import se.europeanspallationsource.xaos.ui.plot.data.ErrorSeries;


public class LineChartSample extends Application {

	private static Long currentTime = System.currentTimeMillis();
	private static final Random RANDOM = new Random(currentTime);

	private static final int NB_OF_POINTS = 100;
	private static final int PERIOD_MS = 500;

	private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
	private DataReducingSeries<Number, Number> series0;
	private DataReducingSeries<Number, Number> series1;
	private DataReducingSeries<Number, Number> series2;
	private DataReducingSeries<Number, Number> series3;
	private DataReducingSeries<Number, Number> series4;
	private DataReducingSeries<Number, Number> series5;
	private DataReducingSeries<Number, Number> series6;
	private DataReducingSeries<Number, Number> series7;
	private DataReducingSeries<Number, Number> series8;
	private DataReducingSeries<Number, Number> series9;

	@Override
	public void start( Stage stage ) {
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle( WindowEvent event ) {
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
        chart.getPlugins().addAll(Plugins.all());

		series0 = new DataReducingSeries<>();
		series0.setName("Generated test1");
		series0.setData(generateGauss(NB_OF_POINTS));
		chart.getData().add(series0.getSeries());

		series1 = new DataReducingSeries<>();
		series1.setName("Generated test2");
		series1.setData(generateLog(NB_OF_POINTS));
		chart.getData().add(series1.getSeries());

		series2 = new DataReducingSeries<>();
		series2.setName("Generated test3");
		series2.setData(generateLog(NB_OF_POINTS));
		chart.getData().add(series2.getSeries());

		series3 = new DataReducingSeries<>();
		series3.setName("Generated test4");
		series3.setData(generateGauss(NB_OF_POINTS));
		//chart.getData().add(series3.getSeries());

		series4 = new DataReducingSeries<>();
		series4.setName("Generated test5");
		series4.setData(generateLog(NB_OF_POINTS));
		// chart.getData().add(series4.getSeries());

		series5 = new DataReducingSeries<>();
		series5.setName("Generated test6");
		series5.setData(generateLog(NB_OF_POINTS));
		// chart.getData().add(series5.getSeries());

		series6 = new DataReducingSeries<>();
		series6.setName("Generated test7");
		series6.setData(generatePower(NB_OF_POINTS));
		//chart.getData().add(series6.getSeries());

		series7 = new DataReducingSeries<>();
		series7.setName("Generated test8");
		series7.setData(generateGauss(NB_OF_POINTS));
		// chart.getData().add(series7.getSeries());

		series8 = new DataReducingSeries<>();
		series8.setName("Generated test9");
		series8.setData(generatePower(NB_OF_POINTS));
		//chart.getData().add(series8.getSeries());

		series9 = new DataReducingSeries<>();
		series9.setName("Generated test10");
		series9.setData(generatePower(NB_OF_POINTS));
		//chart.getData().add(series9.getSeries());

		ErrorSeries<Number, Number> error0 = new ErrorSeries();
		ErrorSeries<Number, Number> error1 = new ErrorSeries();
		ErrorSeries<Number, Number> error2 = new ErrorSeries();

		//DataReducingObservableList.Data<Number,Number> error0 = FXCollections.observableArrayList();
		for ( int ind = 0; ind < NB_OF_POINTS; ind++ ) {
			error0.getData().add(new ErrorSeries.ErrorData<Number, Number>(series0.getData().get(ind), 0.2, 0.2));
			error1.getData().add(new ErrorSeries.ErrorData<Number, Number>(series1.getData().get(ind), 0.15, 0.0));
			error2.getData().add(new ErrorSeries.ErrorData<Number, Number>(series2.getData().get(ind), 0.05, 0.1));
		}

		//Series 0
		//chart.setSeriesAsHorizontal(3);//red
		//chart.getPlugins().add(new ErrorBars(error0,0));
		//Series 1
		//chart.setSeriesAsVertical(4);//blue
		//chart.getPlugins().add(new ErrorBars(error1,1));
		//Series 2
		//chart.setSeriesAsLongitudinal(5);//horrible green
		//chart.getPlugins().add(new ErrorBars(error2,2));
		Label infoLabel = new Label();
		infoLabel.setText("Zoom-in: drag with left-mouse, Zoom-out: right-click, Zoom-origin: right-click + CTRL, Pan: drag with left-mouse + CTRL or keyboard arrows");

		BorderPane borderPane = new BorderPane(chart);

		chart.setFocusTraversable(true);

		borderPane.setBottom(infoLabel);
		Scene scene = new Scene(borderPane, 800, 600);

		stage.setScene(scene);
		stage.show();

	}

	private static ObservableList<XYChart.Data<Number, Number>> generateData( int nbOfPoints ) {
		int[] yValues = generateIntArray(11, 5, nbOfPoints);
		List<XYChart.Data<Number, Number>> data = new ArrayList<>(nbOfPoints);
		for ( int i = 0; i < yValues.length; i++ ) {
			data.add(new XYChart.Data<Number, Number>(i, yValues[i]));
		}
		return FXCollections.observableArrayList(data);
	}

	private static ObservableList<XYChart.Data<Number, Number>> generateGauss( int nbOfPoints ) {
		double[] yValues = new double[nbOfPoints];
		double mean = 10 + RANDOM.nextDouble() * 40;
		double rms = 10 + RANDOM.nextDouble() * 40;
		double A = 10 + RANDOM.nextDouble() * 100;
		double offset = RANDOM.nextDouble() * 20;
		List<XYChart.Data<Number, Number>> data = new ArrayList<>(nbOfPoints);
		for ( int i = 0; i < yValues.length; i++ ) {
			yValues[i] = offset + A * Math.exp(-( i - mean ) * ( i - mean ) / rms) + RANDOM.nextDouble();
			data.add(new XYChart.Data<Number, Number>(i, yValues[i]));
		}
		System.out.print("Gaussian: mean = " + mean + ", rms = " + rms + " and amplitude = " + A + "\n");
		return FXCollections.observableArrayList(data);
	}

	private static ObservableList<XYChart.Data<Number, Number>> generateExp( int nbOfPoints ) {
		double[] yValues = new double[nbOfPoints];
		double a = 10 + RANDOM.nextDouble() * 40;
		double b = RANDOM.nextDouble() / 10;
		double offset = RANDOM.nextDouble() * 20;
		List<XYChart.Data<Number, Number>> data = new ArrayList<>(nbOfPoints);
		for ( int i = 0; i < yValues.length; i++ ) {
			yValues[i] = offset + a * Math.exp(b * i) + RANDOM.nextDouble();
			data.add(new XYChart.Data<Number, Number>(i, yValues[i]));
		}
		System.out.print("Exponencial : ampl = " + a + " and b = " + b + "\n");
		return FXCollections.observableArrayList(data);
	}

	private static ObservableList<XYChart.Data<Number, Number>> generatePower( int nbOfPoints ) {
		double[] yValues = new double[nbOfPoints];
		double a = 10 + RANDOM.nextDouble() * 40;
		double b = 1 + RANDOM.nextDouble() * 5;
		double offset = RANDOM.nextDouble() * 20;
		List<XYChart.Data<Number, Number>> data = new ArrayList<>(nbOfPoints);
		for ( int i = 0; i < yValues.length; i++ ) {
			yValues[i] = offset + a * Math.pow(i, b) + RANDOM.nextDouble();
			data.add(new XYChart.Data<Number, Number>(i, yValues[i]));
		}
		System.out.print("Power : ampl = " + a + " and power = " + b + "\n");
		return FXCollections.observableArrayList(data);
	}

	private static ObservableList<XYChart.Data<Number, Number>> generateLog( int nbOfPoints ) {
		double[] yValues = new double[nbOfPoints];
		double a = RANDOM.nextDouble() * 20;
		double b = 10 + RANDOM.nextDouble() * 40;
		List<XYChart.Data<Number, Number>> data = new ArrayList<>(nbOfPoints);
		for ( int i = 0; i < yValues.length; i++ ) {
			yValues[i] = a + b * Math.log(i + 1) + RANDOM.nextDouble();
			data.add(new XYChart.Data<Number, Number>(i + 1, yValues[i]));
		}
		System.out.print("Log : a = " + a + " and b = " + b + "\n");
		return FXCollections.observableArrayList(data);
	}

	public static int[] generateIntArray( int firstValue, int variance, int size ) {
		int[] data = new int[size];
		double minval = 100.0;
		data[0] = firstValue;
		for ( int i = 1; i < data.length; i++ ) {
			int sign = RANDOM.nextBoolean() ? 1 : -1;
			data[i] = data[i - 1] + (int) ( variance * RANDOM.nextDouble() ) * sign;
			minval = Math.min(minval, data[i]);
		}

		//System.out.print("min val : "+minval+"\n");
		return data;
	}

	public static Double[] generateDoubleArray( int variance, Double mean, int size ) {
		Double[] data = new Double[size];
		data[0] = variance * RANDOM.nextGaussian() + mean;
		for ( int i = 1; i < data.length; i++ ) {
			int sign = RANDOM.nextBoolean() ? 1 : -1;
			data[i] = variance * RANDOM.nextGaussian() + mean;//*sign;
		}
		return data;
	}

	public static void main( String[] args ) {
		launch(args);
	}
}
