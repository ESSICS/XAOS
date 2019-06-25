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


import se.europeanspallationsource.xaos.ui.plot.LineChartFX;
import se.europeanspallationsource.xaos.ui.plot.NumberAxis;
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

import se.europeanspallationsource.xaos.ui.plot.plugins.impl.ErrorBars;
import se.europeanspallationsource.xaos.ui.plot.TimeAxis;
import se.europeanspallationsource.xaos.ui.plot.Plugin;
import java.util.concurrent.TimeUnit;
import javafx.scene.chart.XYChart.Series;
import se.europeanspallationsource.xaos.ui.plot.plugins.Plugins;
import se.europeanspallationsource.xaos.ui.plot.data.ErrorSeries;


public class DateLineChartSample extends Application {

	private static final Random RANDOM = new Random(System.currentTimeMillis());

	private static final int NB_OF_POINTS = 10;
	private static final int PERIOD_MS = 500;

	private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
	private Series<Number, Number> series0;
	private Series<Number, Number> series1;
	private Series<Number, Number> series2;

	@Override
	public void start( Stage stage ) {
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle( WindowEvent event ) {
				Platform.exit();
				System.exit(0);
			}
		});

		stage.setTitle("Date Line Chart Sample");
		final TimeAxis xAxis = new TimeAxis(TimeUnit.DAYS, TimeUnit.NANOSECONDS);
		xAxis.setTickMarkVisible(true);
		xAxis.setAnimated(false);

		final NumberAxis yAxis = new NumberAxis();
		yAxis.setAnimated(false);
		final LineChartFX<Number, Number> chart = new LineChartFX<Number, Number>(xAxis, yAxis);
		chart.setTitle("Test data");
		chart.setAnimated(false);

		ObservableList<Plugin> pluginList = FXCollections.observableArrayList();

		pluginList.addAll(Plugins.all());

		chart.addChartPlugins(pluginList);

		series0 = new Series<>();
		series0.setName("Generated test data-horizontal");
		series0.setData(generateData(NB_OF_POINTS));
		chart.getData().add(series0);

		series1 = new Series<>();
		series1.setName("Generated test data-vertical");
		series1.setData(generateData(NB_OF_POINTS));
		chart.getData().add(series1);

		series2 = new Series<>();
		series2.setName("Generated test data-longitudinal");
		series2.setData(generateData(NB_OF_POINTS));
		chart.getData().add(series2);

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
		chart.setSeriesAsHorizontal(0);//red
		chart.getPlugins().add(new ErrorBars(error0, 0));
		//Series 1
		chart.setSeriesAsVertical(1);//blue
		chart.getPlugins().add(new ErrorBars(error1, 1));
		//Series 2
		chart.setSeriesAsLongitudinal(2);//horrible green
		chart.getPlugins().add(new ErrorBars(error2, 2));

		Label infoLabel = new Label();
		infoLabel.setText("Zoom-in: drag with left-mouse, Zoom-out: right-click, Zoom-origin: right-click + CTRL, Pan: drag with left-mouse + CTRL or keyboard arrows");

		BorderPane borderPane = new BorderPane(chart);

		borderPane.setBottom(infoLabel);
		Scene scene = new Scene(borderPane, 800, 600);

		stage.setScene(scene);
		stage.show();
	}

	private static ObservableList<XYChart.Data<Number, Number>> generateData( int nbOfPoints ) {
		int[] yValues = generateIntArray(0, 5, nbOfPoints);
		List<XYChart.Data<Number, Number>> data = new ArrayList<>(nbOfPoints);
		for ( int i = 0; i < yValues.length; i++ ) {
			data.add(new XYChart.Data<Number, Number>(i, yValues[i]));
		}
		return FXCollections.observableArrayList(data);
	}

	public static int[] generateIntArray( int firstValue, int variance, int size ) {
		int[] data = new int[size];
		data[0] = firstValue;
		for ( int i = 1; i < data.length; i++ ) {
			int sign = RANDOM.nextBoolean() ? 1 : -1;
			data[i] = data[i - 1] + (int) ( variance * RANDOM.nextDouble() ) * sign;
		}
		return data;
	}

	public static void main( String[] args ) {
		launch(args);
	}
}
