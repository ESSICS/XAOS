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
package se.europeanspallationsource.xaos.ui.plot;


import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.Axis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import se.europeanspallationsource.xaos.ui.plot.data.ErrorSeries;
import se.europeanspallationsource.xaos.ui.plot.demo.AreaChartGenerator;
import se.europeanspallationsource.xaos.ui.plot.demo.BarChartGenerator;
import se.europeanspallationsource.xaos.ui.plot.demo.ChartGenerator;
import se.europeanspallationsource.xaos.ui.plot.demo.DateAreaChartGenerator;
import se.europeanspallationsource.xaos.ui.plot.demo.DensityChartGenerator;
import se.europeanspallationsource.xaos.ui.plot.demo.HistogramChartGenerator;
import se.europeanspallationsource.xaos.ui.plot.demo.LineChartGenerator;
import se.europeanspallationsource.xaos.ui.plot.demo.ScatterChartGenerator;
import se.europeanspallationsource.xaos.ui.plot.demo.TimeAreaChartGenerator;
import se.europeanspallationsource.xaos.ui.plot.plugins.Pluggable;
import se.europeanspallationsource.xaos.ui.plot.plugins.Plugins;
import se.europeanspallationsource.xaos.ui.plot.plugins.impl.ErrorBars;


/**
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class FXMLController implements Initializable {

	private static final int NB_OF_POINTS = 100;//10000;
	private static final Random RANDOM = new Random(System.nanoTime());

	@FXML private BorderPane borderpane;
	@FXML private ComboBox<String> chartchoice;
	@FXML private Button errorBarsButton;
	@FXML private Button logXButton;
	@FXML private Button logYButton;
	@FXML private Button resetButton;

	private final ObservableList<String> options = FXCollections.observableArrayList(
		"AreaChartFX",
		"BarChartFX",
		"DateAreaChartFX",
		"DensityChartFX",
		"HistogramChartFX",
		"LineChartFX",
		"ScatterChartFX",
		"TimeAreaChartFX"
	);

	private AreaChartFX<Number, Number> areaChart;
	private BarChartFX<String, Number> barChart;
	private AreaChartFX<Date, Number> dateAreaChart;
	private DensityChartFX<Number, Number> densityChart;
	private HistogramChartFX<Number, Number> histogramChart;
	private LineChartFX<Number, Number> lineChart;
	private ScatterChartFX<Number, Number> scatterChart;
	private AreaChartFX<Number, Number> timeAreaChart;

	private final AreaChartGenerator areaChartGen = new AreaChartGenerator();
	private final BarChartGenerator barChartGen = new BarChartGenerator();
	private final DateAreaChartGenerator dateAreaChartGen = new DateAreaChartGenerator();
	private final DensityChartGenerator densityChartGen = new DensityChartGenerator();
	private final HistogramChartGenerator histogramChartGen = new HistogramChartGenerator();
	private final LineChartGenerator lineChartGen = new LineChartGenerator();
	private final ScatterChartGenerator scatterChartGen = new ScatterChartGenerator();
	private final TimeAreaChartGenerator timeAreaChartGen = new TimeAreaChartGenerator();

	private final PluggableChartContainer chartContainer = new PluggableChartContainer();

	@Override
	@SuppressWarnings( "unchecked" )
	public void initialize( URL url, ResourceBundle rb ) {

		chartchoice.setItems(options);
		chartchoice.valueProperty().addListener(( ob, ov, nv ) -> {
			if ( nv != null ) {
				switch ( nv ) {
					case "AreaChartFX":
						areaChart = (AreaChartFX<Number, Number>) initializeChart(
							areaChart,
							areaChartGen,
							NB_OF_POINTS,
							( areaChart == null ) ? false : areaChart.getXAxis() instanceof LogAxis,
							( areaChart == null ) ? false : areaChart.getYAxis() instanceof LogAxis
						);
						break;
					case "BarChartFX":
						barChart = (BarChartFX<String, Number>) initializeChart(
							barChart,
							barChartGen,
							NB_OF_POINTS,
							false,
							( barChart == null ) ? false : barChart.getYAxis() instanceof LogAxis
						);
						break;
					case "DateAreaChartFX":
						dateAreaChart = (AreaChartFX<Date, Number>) initializeChart(
							dateAreaChart,
							dateAreaChartGen,
							NB_OF_POINTS,
							false,
							( dateAreaChart == null ) ? false : dateAreaChart.getYAxis() instanceof LogAxis
						);
						break;
					case "DensityChartFX":
						densityChart = (DensityChartFX<Number, Number>) initializeChart(
							densityChart,
							densityChartGen,
							NB_OF_POINTS,
							false,
							false
						);
						break;
					case "HistogramChartFX":
						histogramChart = (HistogramChartFX<Number, Number>) initializeChart(
							histogramChart,
							histogramChartGen,
							NB_OF_POINTS,
							false,
							false
						);
						break;
					case "LineChartFX":
						lineChart = (LineChartFX<Number, Number>) initializeChart(
							lineChart,
							lineChartGen,
							NB_OF_POINTS,
							( lineChart == null ) ? false : lineChart.getXAxis() instanceof LogAxis,
							( lineChart == null ) ? false : lineChart.getYAxis() instanceof LogAxis
						);
						break;
					case "ScatterChartFX":
						scatterChart = (ScatterChartFX<Number, Number>) initializeChart(
							scatterChart,
							scatterChartGen,
							NB_OF_POINTS,
							( scatterChart == null ) ? false : scatterChart.getXAxis() instanceof LogAxis,
							( scatterChart == null ) ? false : scatterChart.getYAxis() instanceof LogAxis
						);
					break;
					case "TimeAreaChartFX":
						timeAreaChart = (AreaChartFX<Number, Number>) initializeChart(
							timeAreaChart,
							timeAreaChartGen,
							NB_OF_POINTS,
							false,
							( timeAreaChart == null ) ? false : timeAreaChart.getYAxis() instanceof LogAxis
						);
						break;
					default:
						break;
				}
			}
		});

		errorBarsButton.prefWidthProperty().bind(chartchoice.widthProperty());
		resetButton.disableProperty().bind(Bindings.createBooleanBinding(
			() -> {
				if ( chartchoice.getValue() == null ) {
					return true;
				} else {
					return !( logXButton.isDisabled() || logYButton.isDisabled() );
				}
			},
			logXButton.disableProperty(),
			logYButton.disableProperty(),
			chartchoice.valueProperty()
		));

		borderpane.setCenter(chartContainer);

	}

	private ObservableList<Plugin> errorGenerator( XYChart<Number, Number> chart ) {

		ErrorSeries<Number, Number> error0 = new ErrorSeries<>();
		ErrorSeries<Number, Number> error1 = new ErrorSeries<>();
		ErrorSeries<Number, Number> error2 = new ErrorSeries<>();

		for ( int ind = 0; ind < chart.getData().get(0).getData().size(); ind++ ) {
			error0.getData().add(new ErrorSeries.ErrorData<>(chart.getData().get(0).getData().get(ind), RANDOM.nextDouble(), RANDOM.nextDouble()));
			error1.getData().add(new ErrorSeries.ErrorData<>(chart.getData().get(1).getData().get(ind), RANDOM.nextDouble(), RANDOM.nextDouble()));
			error2.getData().add(new ErrorSeries.ErrorData<>(chart.getData().get(2).getData().get(ind), RANDOM.nextDouble(), RANDOM.nextDouble()));
		}

		return FXCollections.observableArrayList(
			Plugins.errorBars(error0, 0),
			Plugins.errorBars(error1, 1),
			Plugins.errorBars(error2, 2)
		);

	}

	private ObservableList<Plugin> errorGenerator( BarChartFX<String, Number> chart ) {

		ErrorSeries<String, Number> error0 = new ErrorSeries<>();
		ErrorSeries<String, Number> error1 = new ErrorSeries<>();
		ErrorSeries<String, Number> error2 = new ErrorSeries<>();

		for ( int ind = 0; ind < chart.getData().get(0).getData().size(); ind++ ) {
			error0.getData().add(new ErrorSeries.ErrorData<>(chart.getData().get(0).getData().get(ind), RANDOM.nextDouble()));
			error1.getData().add(new ErrorSeries.ErrorData<>(chart.getData().get(1).getData().get(ind), RANDOM.nextDouble()));
			error2.getData().add(new ErrorSeries.ErrorData<>(chart.getData().get(2).getData().get(ind), RANDOM.nextDouble()));
		}

		return FXCollections.observableArrayList(
			Plugins.errorBars(error0, 0),
			Plugins.errorBars(error1, 1),
			Plugins.errorBars(error2, 2)
		);

	}

	private Chart getSelectedChart() {
		switch ( chartchoice.getValue() ) {
			case "AreaChartFX":
				return areaChart;
			case "BarChartFX":
				return barChart;
			case "DateAreaChartFX":
				return dateAreaChart;
			case "DensityChartFX":
				return densityChart;
			case "HistogramChartFX":
				return histogramChart;
			case "LineChartFX":
				return lineChart;
			case "ScatterChartFX":
				return scatterChart;
			case "TimeAreaChartFX":
				return timeAreaChart;
			default:
				return null;
		}
	}

	@FXML
	@SuppressWarnings( "unchecked" )
	private void handleErrorButton( ActionEvent event ) {
		if ( chartchoice.getValue() != null ) {

			Chart chart = getSelectedChart();
			ObservableList<Plugin> plugins = ((Pluggable) chart).getPlugins();

			if ( plugins.stream().filter(p -> p instanceof ErrorBars).count() == 0 ) {
				plugins.addAll(
					isBarChartSelected()
				  ? errorGenerator((BarChartFX<String, Number>) chart)
				  : errorGenerator((XYChart<Number, Number> ) chart)
				);
			}

			updateButtons(chart);

		}
	}

	@FXML
	@SuppressWarnings( "unchecked" )
	private void handleResetButton( ActionEvent event ) {
		switch ( chartchoice.getValue() ) {
			case "AreaChartFX":
				areaChart = (AreaChartFX<Number, Number>) initializeChart(areaChart, areaChartGen, NB_OF_POINTS);
				break;
			case "BarChartFX":
				barChart = (BarChartFX<String, Number>) initializeChart(barChart, barChartGen, NB_OF_POINTS);
				break;
			case "DateAreaChartFX":
				dateAreaChart = (AreaChartFX<Date, Number>) initializeChart(dateAreaChart, dateAreaChartGen, NB_OF_POINTS);
				break;
			case "DensityChartFX":
				densityChart = (DensityChartFX<Number, Number>) initializeChart(densityChart, densityChartGen, NB_OF_POINTS);
				break;
			case "HistogramChartFX":
				histogramChart = (HistogramChartFX<Number, Number>) initializeChart(histogramChart, histogramChartGen, NB_OF_POINTS);
				break;
			case "LineChartFX":
				lineChart = (LineChartFX<Number, Number>) initializeChart(lineChart, lineChartGen, NB_OF_POINTS);
				break;
			case "ScatterChartFX":
				scatterChart = (ScatterChartFX<Number, Number>) initializeChart(scatterChart, scatterChartGen, NB_OF_POINTS);
				break;
			case "TimeAreaChartFX":
				timeAreaChart = (AreaChartFX<Number, Number>) initializeChart(timeAreaChart, timeAreaChartGen, NB_OF_POINTS);
				break;
			default:
				break;
		}
	}

	@FXML
	@SuppressWarnings( "unchecked" )
	private void handleXLogButton( ActionEvent event ) {
		switch ( chartchoice.getValue() ) {
			case "AreaChartFX":
				areaChart = (AreaChartFX<Number, Number>) initializeChart(
					areaChart,
					areaChartGen,
					NB_OF_POINTS,
					true,
					areaChart.getYAxis() instanceof LogAxis
				);
				break;
			case "LineChartFX":
				lineChart = (LineChartFX<Number, Number>) initializeChart(
					lineChart,
					lineChartGen,
					NB_OF_POINTS,
					true,
					lineChart.getYAxis() instanceof LogAxis
				);
				break;
			case "ScatterChartFX":
				scatterChart = (ScatterChartFX<Number, Number>) initializeChart(
					scatterChart,
					scatterChartGen,
					NB_OF_POINTS,
					true,
					scatterChart.getYAxis() instanceof LogAxis
				);
				break;
			default:
				break;
		}
	}

	@FXML
	@SuppressWarnings( "unchecked" )
	private void handleYLogButton( ActionEvent event ) {
		switch ( chartchoice.getValue() ) {
			case "AreaChartFX":
				areaChart = (AreaChartFX<Number, Number>) initializeChart(
					areaChart,
					areaChartGen,
					NB_OF_POINTS,
					areaChart.getXAxis() instanceof LogAxis,
					true
				);
				break;
			case "BarChartFX":
				barChart = (BarChartFX<String, Number>) initializeChart(
					barChart,
					barChartGen,
					NB_OF_POINTS,
					false,
					true
				);
				break;
			case "DateAreaChartFX":
				dateAreaChart = (AreaChartFX<Date, Number>) initializeChart(
					dateAreaChart,
					dateAreaChartGen,
					NB_OF_POINTS,
					false,
					true
				);
				break;
			case "LineChartFX":
				lineChart = (LineChartFX<Number, Number>) initializeChart(
					lineChart,
					lineChartGen,
					NB_OF_POINTS,
					lineChart.getXAxis() instanceof LogAxis,
					true
				);
				break;
			case "ScatterChartFX":
				scatterChart = (ScatterChartFX<Number, Number>) initializeChart(
					scatterChart,
					scatterChartGen,
					NB_OF_POINTS,
					scatterChart.getXAxis() instanceof LogAxis,
					true
				);
				break;
			case "TimeAreaChartFX":
				timeAreaChart = (AreaChartFX<Number, Number>) initializeChart(
					timeAreaChart,
					timeAreaChartGen,
					NB_OF_POINTS,
					false,
					true
				);
				break;
			default:
				break;
		}
	}

	private Chart initializeChart ( ChartGenerator generator, int numberOfPoints, boolean logXAxis, boolean logYAxis ) {

		@SuppressWarnings( "unchecked" )
		Chart chart = generator.getNewChart(numberOfPoints, logXAxis, logYAxis);

		chartContainer.setPluggable((Pluggable) chart);
		updateButtons(chart);

		return chart;

	}

	private Chart initializeChart ( Chart oldChart, ChartGenerator generator, int numberOfPoints ) {
		return initializeChart(oldChart, generator, numberOfPoints, false, false);
	}

	private Chart initializeChart ( Chart oldChart, ChartGenerator generator, int numberOfPoints, boolean logXAxis, boolean logYAxis ) {

		if ( oldChart != null ) {

			List<Plugin> plugins = ((Pluggable) oldChart).getPlugins()
				.stream()
				.filter(p -> p instanceof ErrorBars)
				.collect(Collectors.toList());

			Chart chart = initializeChart(generator, numberOfPoints, logXAxis, logYAxis);

			if ( !plugins.isEmpty() ) {
				((Pluggable) chart).getPlugins().addAll(plugins);
				updateButtons(chart);
			}

			return chart;

		} else {
			return initializeChart(generator, numberOfPoints, logXAxis, logYAxis);
		}

	}

	private boolean isBarChartSelected() {
		return "BarChartFX".equals(chartchoice.getValue());
	}

	private boolean isDateAreaChartSelected() {
		return "DateAreaChartFX".equals(chartchoice.getValue());
	}

	private boolean isDensityChartSelected() {
		return "DensityChartFX".equals(chartchoice.getValue());
	}

	private boolean isHistogramChartSelected() {
		return "HistogramChartFX".equals(chartchoice.getValue());
	}

	@SuppressWarnings( "null" )
	private void updateButtons( Chart chart ) {

		Axis<?> xAxis = ( chart instanceof DensityChartFX )
					  ? ((DensityChartFX<?, ?>) chart).getXAxis()
					  : ((XYChart<?, ?>) chart).getXAxis();
		Axis<?> yAxis = ( chart instanceof DensityChartFX )
					  ? ((DensityChartFX<?, ?>) chart).getYAxis()
					  : ((XYChart<?, ?>) chart).getYAxis();

		logXButton.setDisable(( xAxis instanceof LogAxis ) || isBarChartSelected() || isDateAreaChartSelected() || isHistogramChartSelected() );
		logYButton.setDisable(( yAxis instanceof LogAxis ) || isHistogramChartSelected() );

		if ( chart instanceof Pluggable ) {

			Pluggable pchart = (Pluggable) chart;

			errorBarsButton.setDisable(
				pchart.getPlugins().stream()
					.filter(p -> p instanceof ErrorBars)
					.count() > 0
				|| isDateAreaChartSelected()
				|| isDensityChartSelected()
				|| isHistogramChartSelected()
			);

		}

	}

}
