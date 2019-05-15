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


import chart.AreaChartFX;
import chart.BarChartFX;
import chart.LineChartFX;
import chart.LogAxis;
import chart.Plugin;
import chart.ScatterChartFX;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import se.europeanspallationsource.xaos.ui.plot.data.ErrorSeries;
import se.europeanspallationsource.xaos.ui.plot.impl.plugins.ErrorBars;
import se.europeanspallationsource.xaos.ui.plot.plugins.Pluggable;
import se.europeanspallationsource.xaos.ui.plot.plugins.Plugins;


/**
 * @author reubenlindroos
 * @author claudio.rosati@esss.se
 */
public class FXMLController implements Initializable {

	private static final int NB_OF_POINTS = 100;//10000;
	private static final Random RANDOM = new Random(System.nanoTime());

	@FXML
	private BorderPane borderpane;
	@FXML
	private ComboBox<String> chartchoice;
	@FXML
	private Button errorBarsButton;
	@FXML
	private Button logXButton;
	@FXML
	private Button logYButton;
	@FXML
	private Button resetButton;

	private final ObservableList<String> options = FXCollections.observableArrayList(
		"AreaChartFX",
		"BarChartFX",
		"LineChartFX",
		"ScatterChartFX"
	);

	private AreaChartFX<Number, Number> areaChart;
	private BarChartFX<String, Number> barChart;
	private LineChartFX<Number, Number> lineChart;
	private ScatterChartFX<Number, Number> scatterChart;

	private final AreaChartGenerator areaChartGen = new AreaChartGenerator();
	private final BarChartGenerator barChartGen = new BarChartGenerator();
	private final LineChartGenerator lineChartGen = new LineChartGenerator();
	private final ScatterChartGenerator scatterChartGen = new ScatterChartGenerator();

	@Override
	@SuppressWarnings( "unchecked" )
	public void initialize( URL url, ResourceBundle rb ) {

		chartchoice.setItems(options);
		chartchoice.valueProperty().addListener(( ob, ov, nv ) -> {
			if ( nv != null ) {
				switch ( nv ) {
					case "LineChartFX":
						lineChart = (LineChartFX<Number, Number>) initializeChart(lineChartGen, NB_OF_POINTS);
						break;
					case "ScatterChartFX":
						scatterChart = (ScatterChartFX<Number, Number>) initializeChart(scatterChartGen, NB_OF_POINTS);
						break;
					case "AreaChartFX":
						areaChart = (AreaChartFX<Number, Number>) initializeChart(areaChartGen, NB_OF_POINTS);
						break;
					case "BarChartFX":
						barChart = (BarChartFX<String, Number>) initializeChart(barChartGen, NB_OF_POINTS);
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
					return logXButton.isDisabled() || logYButton.isDisabled();
				}
			},
			logXButton.disableProperty(),
			logYButton.disableProperty()
		));

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

	private XYChart<?, Number> initializeChart ( ChartGenerator generator, int numberOfPoints ) {

		@SuppressWarnings( "unchecked" )
		XYChart<?, Number> chart = generator.getNewChart(numberOfPoints, false, false);

		((Pluggable) chart).getPlugins().add(new PropertyMenu());
		borderpane.setCenter(chart);

		updateButtons(chart);

		return chart;

	}

	private XYChart<?, Number> initializeChart ( XYChart<?, Number> oldChart, ChartGenerator generator, int numberOfPoints ) {

		@SuppressWarnings( "unchecked" )
		XYChart<?, Number> chart = generator.getNewChart(numberOfPoints, false, false);

		((Pluggable) chart).getPlugins().add(new PropertyMenu());
		borderpane.setCenter(chart);

		return chart;

	}

	private void updateButtons( XYChart<?, Number> chart ) {

		logXButton.setDisable(!( chart.getXAxis() instanceof LogAxis ));
		logYButton.setDisable(!( chart.getYAxis() instanceof LogAxis ));

		if ( chart instanceof Pluggable ) {

			Pluggable pchart = (Pluggable) chart;

			errorBarsButton.setDisable(
				pchart.getPlugins().stream()
					.filter(p -> p instanceof ErrorBars)
					.count() > 0
			);

		}

	}


















	@FXML
	private void handleErrorButton( ActionEvent event ) {
		if ( chartchoice.getValue() != null ) {
			switch ( chartchoice.getValue() ) {
				case "AreaChartFX":
					if ( errorBarsToInclude.isEmpty() ) {
						errorGenerator(areaChart);
					}
					if ( !areaChart.getPlugins().containsAll(errorBarsToInclude) ) {
						areaChart.getPlugins().addAll(errorBarsToInclude);
					} else {
						areaChart.getPlugins().removeAll(errorBarsToInclude);
						errorBarsToInclude.clear();
					}
					break;
				case "BarChartFX":
					if ( errorBarsToInclude.isEmpty() ) {
						errorGenerator(barChart);
					}
					if ( !barChart.getPlugins().containsAll(errorBarsToInclude) ) {
						barChart.getPlugins().addAll(errorBarsToInclude);
					} else {
						barChart.getPlugins().removeAll(errorBarsToInclude);
						errorBarsToInclude.clear();
					}
					break;
				case "LineChartFX":
					if ( errorBarsToInclude.isEmpty() ) {
						errorGenerator(lineChart);
					}
					if ( !lineChart.getPlugins().containsAll(errorBarsToInclude) ) {
						lineChart.getPlugins().addAll(errorBarsToInclude);
					} else {
						lineChart.getPlugins().removeAll(errorBarsToInclude);
						errorBarsToInclude.clear();
					}
					break;
				case "ScatterChartFX":
					if ( errorBarsToInclude.isEmpty() ) {
						errorGenerator(scatterChart);
					}
					if ( !scatterChart.getPlugins().containsAll(errorBarsToInclude) ) {
						scatterChart.getPlugins().addAll(errorBarsToInclude);
					} else {
						scatterChart.getPlugins().removeAll(errorBarsToInclude);
						errorBarsToInclude.clear();
					}
					break;
				default:
					break;
			}
		}

		requestFocusOnClick();
	}

	@FXML
	private void handleResetButton( ActionEvent event ) {
		borderpane.getChildren().clear();
		if ( "LineChartFX".equals(chartchoice.getValue().toString()) ) {
			lineChart = lineChartGen.resetAxes(NB_OF_POINTS);
			borderpane.setCenter(lineChart);
		}

		if ( "ScatterChartFX".equals(chartchoice.getValue().toString()) ) {
			scatterChart = scatterChartGen.resetAxes(NB_OF_POINTS);
			borderpane.setCenter(scatterChart);
		}

		if ( "AreaChartFX".equals(chartchoice.getValue().toString()) ) {
			areaChart = areaChartGen.resetAxes(NB_OF_POINTS);
			borderpane.setCenter(areaChart);
		}
		requestFocusOnClick();
	}

	@FXML
	private void handleXLogButton( ActionEvent event ) {
		borderpane.getChildren().clear();
		if ( "LineChartFX".equals(chartchoice.getValue().toString()) ) {
			lineChart = lineChartGen.setXLogAxis(NB_OF_POINTS);
			borderpane.setCenter(lineChart);
		}

		if ( "ScatterChartFX".equals(chartchoice.getValue().toString()) ) {
			scatterChart = scatterChartGen.setXLogAxis(NB_OF_POINTS);
			borderpane.setCenter(scatterChart);
		}

		if ( "AreaChartFX".equals(chartchoice.getValue().toString()) ) {
			areaChart = areaChartGen.setXLogAxis(NB_OF_POINTS);
			borderpane.setCenter(areaChart);
		}

		requestFocusOnClick();
	}

	@FXML
	private void handleYLogButton( ActionEvent event ) {
		borderpane.getChildren().clear();
		if ( "ScatterChartFX".equals(chartchoice.getValue().toString()) ) {
			scatterChart = scatterChartGen.setYLogAxis(NB_OF_POINTS);
			borderpane.setCenter(scatterChart);
		}
		if ( "LineChartFX".equals(chartchoice.getValue().toString()) ) {
			lineChart = lineChartGen.setYLogAxis(NB_OF_POINTS);
			borderpane.setCenter(lineChart);
		}
		if ( "AreaChartFX".equals(chartchoice.getValue().toString()) ) {
			areaChart = areaChartGen.setYLogAxis(NB_OF_POINTS);
			borderpane.setCenter(areaChart);
		}

		requestFocusOnClick();
	}

}
