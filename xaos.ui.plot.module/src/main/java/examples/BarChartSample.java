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


import se.europeanspallationsource.xaos.ui.plot.NumberAxis;
import se.europeanspallationsource.xaos.ui.plot.Plugin;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import se.europeanspallationsource.xaos.ui.plot.BarChartFX;
import se.europeanspallationsource.xaos.ui.plot.plugins.impl.ErrorBars;
import se.europeanspallationsource.xaos.ui.plot.plugins.Plugins;
import se.europeanspallationsource.xaos.ui.plot.data.ErrorSeries;


public class BarChartSample extends Application {

	final static String AUSTRIA = "Austria";
	final static String BRAZIL = "Brazil";
	final static String FRANCE = "France";
	final static String ITALY = "Italy";
	final static String USA = "USA";

	@Override public void start( Stage stage ) {
		stage.setTitle("Bar Chart Sample");
		final CategoryAxis xAxis = new CategoryAxis();
		final NumberAxis yAxis = new NumberAxis();
		final BarChartFX<String, Number> bc
			= new BarChartFX<>(xAxis, yAxis);
		bc.setTitle("Country Summary");

		ObservableList<Plugin> pluginList = FXCollections.observableArrayList();

		pluginList.addAll(Plugins.all());

		bc.addChartPlugins(pluginList);
		xAxis.setLabel("Country");
		yAxis.setLabel("Value");

		XYChart.Series series1 = new XYChart.Series();
		series1.setName("2003");
		series1.getData().add(new XYChart.Data(AUSTRIA, 25601.34));
		series1.getData().add(new XYChart.Data(BRAZIL, 20148.82));
		series1.getData().add(new XYChart.Data(FRANCE, 10000));
		series1.getData().add(new XYChart.Data(ITALY, 35407.15));
		series1.getData().add(new XYChart.Data(USA, 12000));

		XYChart.Series series2 = new XYChart.Series();
		series2.setName("2004");
		series2.getData().add(new XYChart.Data(AUSTRIA, 57401.85));
		series2.getData().add(new XYChart.Data(BRAZIL, 41941.19));
		series2.getData().add(new XYChart.Data(FRANCE, 45263.37));
		series2.getData().add(new XYChart.Data(ITALY, 117320.16));
		series2.getData().add(new XYChart.Data(USA, 14845.27));

		XYChart.Series series3 = new XYChart.Series();
		series3.setName("2005");
		series3.getData().add(new XYChart.Data(AUSTRIA, 45000.65));
		series3.getData().add(new XYChart.Data(BRAZIL, 44835.76));
		series3.getData().add(new XYChart.Data(FRANCE, 18722.18));
		series3.getData().add(new XYChart.Data(ITALY, 17557.31));
		series3.getData().add(new XYChart.Data(USA, 92633.68));

		Scene scene = new Scene(bc, 800, 600);
		bc.getData().addAll(series1, series2, series3);

		ObservableList<ErrorSeries.ErrorData<String, Number>> error0 = FXCollections.observableArrayList();
		ObservableList<ErrorSeries.ErrorData<String, Number>> error1 = FXCollections.observableArrayList();
		ObservableList<ErrorSeries.ErrorData<String, Number>> error2 = FXCollections.observableArrayList();

		for ( int ind = 0; ind < 5; ind++ ) {
			error0.add(new ErrorSeries.ErrorData<>(bc.getData().get(0).getData().get(ind), 0.1));
			error1.add(new ErrorSeries.ErrorData<>(bc.getData().get(1).getData().get(ind), 0.1));
			error2.add(new ErrorSeries.ErrorData<>(bc.getData().get(2).getData().get(ind), 0.05));
		}

		bc.getPlugins().addAll(new ErrorBars(new ErrorSeries<>(error0), 0), new ErrorBars(new ErrorSeries<>(error1), 1), new ErrorBars(new ErrorSeries<>(error2), 2));

		stage.setScene(scene);
		stage.show();
	}

	public static void main( String[] args ) {
		launch(args);
	}
}
