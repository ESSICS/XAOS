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


import Demo.AreaChartGenerator;
import chart.AreaChartFX;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import chart.LineChartFX;
import chart.BarChartFX;
import chart.ScatterChartFX;
import javafx.scene.chart.XYChart;

import plugins.ErrorBars;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import plugins.PropertyMenu;
import se.europeanspallationsource.xaos.ui.plot.util.ErrorSeries;


public class FXMLController implements Initializable {

	@FXML
	private ComboBox chartchoice;
	@FXML
	private BorderPane borderpane;
	@FXML
	private Button errorBars;

	ObservableList<String> options
		= FXCollections.observableArrayList(
			"LineChartFX",
			"ScatterChartFX",
			"BarChartFX",
			"AreaChartFX"
		);
	private LineChartFX<Number, Number> lineChart;
	private ScatterChartFX<Number, Number> scatterChart;
	private AreaChartFX<Number, Number> areaChart;
	private BarChartFX<String, Number> barChart;

	private LineChartGenerator lineChartGen = new LineChartGenerator();
	private ScatterChartGenerator scatterChartGen = new ScatterChartGenerator();
	private AreaChartGenerator areaChartGen = new AreaChartGenerator();
	private BarChartGenerator barChartGen = new BarChartGenerator();

	private Integer NB_OF_POINTS = 10000;
	private ObservableList< ErrorBars> errorBarsToInclude = FXCollections.observableArrayList();

	@Override
	public void initialize( URL url, ResourceBundle rb ) {

		chartchoice.setItems(options);
		chartchoice.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed( ObservableValue ov, String t, String t1 ) {
				if ( "LineChartFX".equals(t1) ) {
					lineChart = lineChartGen.getChart(NB_OF_POINTS);
					borderpane.setCenter(lineChart);
					lineChart.getPlugins().add(new PropertyMenu());

				}
				if ( "ScatterChartFX".equals(t1) ) {
					scatterChart = scatterChartGen.getChart(NB_OF_POINTS);
					borderpane.setCenter(scatterChart);
					scatterChart.getPlugins().add(new PropertyMenu());

				}

				if ( "AreaChartFX".equals(t1) ) {
					areaChart = areaChartGen.getChart(NB_OF_POINTS);
					borderpane.setCenter(areaChart);
					areaChart.getPlugins().add(new PropertyMenu());

				}
				if ( "BarChartFX".equals(t1) ) {
					barChart = barChartGen.getChart();
					borderpane.setCenter(barChart);
					barChart.getPlugins().add(new PropertyMenu());

				}
				requestFocusOnClick();
			}
		});

		errorBars.prefWidthProperty().bind(chartchoice.widthProperty());

	}

	@FXML
	private void handleErrorButton( ActionEvent event ) {
		if ( "LineChartFX".equals(chartchoice.getValue().toString()) ) {
			if ( errorBarsToInclude.isEmpty() ) {
				errorGenerator(lineChart);
			}
			if ( !lineChart.getPlugins().containsAll(errorBarsToInclude) ) {
				lineChart.getPlugins().addAll(errorBarsToInclude);
			} else {
				lineChart.getPlugins().removeAll(errorBarsToInclude);
				errorBarsToInclude.clear();
			}
		}

		if ( "ScatterChartFX".equals(chartchoice.getValue().toString()) ) {
			if ( errorBarsToInclude.isEmpty() ) {
				errorGenerator(scatterChart);
			}
			if ( !scatterChart.getPlugins().containsAll(errorBarsToInclude) ) {
				scatterChart.getPlugins().addAll(errorBarsToInclude);
			} else {
				scatterChart.getPlugins().removeAll(errorBarsToInclude);
				errorBarsToInclude.clear();
			}
		}

		if ( "AreaChartFX".equals(chartchoice.getValue().toString()) ) {
			if ( errorBarsToInclude.isEmpty() ) {
				errorGenerator(areaChart);
			}
			if ( !areaChart.getPlugins().containsAll(errorBarsToInclude) ) {
				areaChart.getPlugins().addAll(errorBarsToInclude);
			} else {
				areaChart.getPlugins().removeAll(errorBarsToInclude);
				errorBarsToInclude.clear();
			}
		}

		if ( "BarChartFX".equals(chartchoice.getValue().toString()) ) {
			if ( errorBarsToInclude.isEmpty() ) {
				errorGenerator(barChart);
			}
			if ( !barChart.getPlugins().containsAll(errorBarsToInclude) ) {
				barChart.getPlugins().addAll(errorBarsToInclude);
			} else {
				barChart.getPlugins().removeAll(errorBarsToInclude);
				errorBarsToInclude.clear();
			}
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

	private void errorGenerator( XYChart<Number, Number> chart ) {
		ErrorSeries<Number, Number> error0 = new ErrorSeries();
		ErrorSeries<Number, Number> error1 = new ErrorSeries();
		ErrorSeries<Number, Number> error2 = new ErrorSeries();

		for ( int ind = 0; ind < chart.getData().get(0).getData().size(); ind++ ) {
			error0.getData().add(new ErrorSeries.ErrorData<Number, Number>(chart.getData().get(0).getData().get(ind), 0.02, 0.02));
			error1.getData().add(new ErrorSeries.ErrorData<Number, Number>(chart.getData().get(1).getData().get(ind), 0.015, 0.02));
			error2.getData().add(new ErrorSeries.ErrorData<Number, Number>(chart.getData().get(2).getData().get(ind), 0.05, 0.01));
		}
		errorBarsToInclude = FXCollections.observableArrayList(
			new ErrorBars(error0, 0),
			new ErrorBars(error1, 1),
			new ErrorBars(error2, 2));

	}

	private void errorGenerator( BarChartFX<String, Number> chart ) {
		ErrorSeries<String, Number> error0 = new ErrorSeries();
		ErrorSeries<String, Number> error1 = new ErrorSeries();
		ErrorSeries<String, Number> error2 = new ErrorSeries();

		//DataReducingObservableList.Data<Number,Number> error0 = FXCollections.observableArrayList();
		for ( int ind = 0; ind < chart.getData().get(0).getData().size(); ind++ ) {
			error0.getData().add(new ErrorSeries.ErrorData<String, Number>(chart.getData().get(0).getData().get(ind), 0.2));
			error1.getData().add(new ErrorSeries.ErrorData<String, Number>(chart.getData().get(1).getData().get(ind), 0.15));
			error2.getData().add(new ErrorSeries.ErrorData<String, Number>(chart.getData().get(2).getData().get(ind), 0.05));
		}

		errorBarsToInclude = FXCollections.observableArrayList(
			new ErrorBars(error0, 0),
			new ErrorBars(error1, 1),
			new ErrorBars(error2, 2));

	}

	private void requestFocusOnClick( XYChart<Number, Number> chart ) {
		chart.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle( MouseEvent event ) {
				chart.requestFocus();
			}
		});
	}

	private void requestFocusOnClick() {
		if ( "LineChartFX".equals(chartchoice.getValue().toString()) ) {
			requestFocusOnClick(lineChart);
		}

		if ( "ScatterChartFX".equals(chartchoice.getValue().toString()) ) {
			requestFocusOnClick(scatterChart);
		}

		if ( "AreaChartFX".equals(chartchoice.getValue().toString()) ) {
			requestFocusOnClick(areaChart);

		}
	}

}
