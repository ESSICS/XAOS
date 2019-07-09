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


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeoutException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import se.europeanspallationsource.xaos.ui.plot.Legend.LegendItem;
import se.europeanspallationsource.xaos.ui.plot.data.DataReducingSeries;
import se.europeanspallationsource.xaos.ui.plot.plugins.Plugins;

import static javafx.scene.input.MouseButton.PRIMARY;
import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( { "UseOfSystemOutOrSystemErr", "ClassWithoutLogger" } )
public class LegendUITest extends ApplicationTest {

	private static final int POINTS_COUNT = 200;
	private static final Random RANDOM = new Random(System.currentTimeMillis());

	@BeforeClass
	public static void setUpClass() {
		System.out.println("---- LegendUITest ----------------------------------------------");
	}

	private AreaChartFX<Number, Number> chart;
	private DataReducingSeries<Number, Number> series0;
	private DataReducingSeries<Number, Number> series1;
	private DataReducingSeries<Number, Number> series2;
	private DataReducingSeries<Number, Number> series3;
	private DataReducingSeries<Number, Number> series4;
	private DataReducingSeries<Number, Number> series5;
	private DataReducingSeries<Number, Number> series6;
	private DataReducingSeries<Number, Number> series7;
	private NumberAxis xAxis;
	private NumberAxis yAxis;

	public void generateChart() {

		xAxis = new NumberAxis();
		yAxis = new NumberAxis();

		xAxis.setAnimated(false);
		yAxis.setAnimated(false);

		chart = new AreaChartFX<>(xAxis, yAxis);
		chart.setTitle("LegendUITest");
		chart.setAnimated(false);
		chart.getPlugins().addAll(Plugins.all());
		chart.setFocusTraversable(true);

		if ( series0 == null ) {

			series0 = new DataReducingSeries<>();
			series0.setName("Generated test data-horizontal");
			series0.setData(generateData(POINTS_COUNT));

			series1 = new DataReducingSeries<>();
			series1.setName("Generated test data-vertical");
			series1.setData(generateData(POINTS_COUNT));

			series2 = new DataReducingSeries<>();
			series2.setName("Generated test data-longitudinal");
			series2.setData(generateData(POINTS_COUNT));

			series3 = new DataReducingSeries<>();
			series3.setName("Generated test data 1");
			series3.setData(generateData(POINTS_COUNT));

			series4 = new DataReducingSeries<>();
			series4.setName("Generated test data 2");
			series4.setData(generateData(POINTS_COUNT));

			series5 = new DataReducingSeries<>();
			series5.setName("Generated test data 3");
			series5.setData(generateData(POINTS_COUNT));

			series6 = new DataReducingSeries<>();
			series6.setName("Generated test data 4");
			series6.setData(generateData(POINTS_COUNT));

			series7 = new DataReducingSeries<>();
			series7.setName("Generated test data 5");
			series7.setData(generateData(POINTS_COUNT));

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

	}

	@Override
	@SuppressWarnings( "NestedAssignment" )
	public void start( Stage stage ) throws IOException {

		generateChart();

		Scene scene = new Scene(new BorderPane(chart), 1200, 900);

		scene.getStylesheets().add(getClass().getResource("/se/europeanspallationsource/xaos/ui/plot/css/modena.css").toExternalForm());
		stage.setScene(scene);
		stage.show();

	}

	@After
	public void tearDown() throws TimeoutException {
		FxToolkit.cleanupStages();
	}

	@Test
	public void test() {

		System.out.println("  Testing ''Legend''...");

		FxRobot robot = new FxRobot();
		Legend legend = robot.lookup("#legend").query();

		assertThat(legend).isNotNull();

		ObservableList<LegendItem> items = legend.getItems();

		assertThat(items).size().isEqualTo(8);

		items.forEach(item -> {

			assertThat(item.checkBox)
				.hasFieldOrPropertyWithValue("visible", true)
				.hasFieldOrPropertyWithValue("selected", true);

			Series<Number, Number> series = chart.getData().stream()
				.filter(s -> s.getName().equals(item.getText()))
				.findFirst()
				.get();

			assertThat(series).isNotNull();

			Node node = series.getNode();

			assertThat(node)
				.isNotNull()
				.hasFieldOrPropertyWithValue("visible", true);
			series.getData().forEach(data -> {
				assertThat(data.getNode())
					.isNotNull()
					.hasFieldOrPropertyWithValue("visible", true);
			});


			robot.clickOn(item.checkBox, PRIMARY);

			assertThat(item.checkBox)
				.hasFieldOrPropertyWithValue("visible", true)
				.hasFieldOrPropertyWithValue("selected", false);
			assertThat(node)
				.hasFieldOrPropertyWithValue("visible", false);
			series.getData().forEach(data -> {
				assertThat(data.getNode())
					.isNotNull()
					.hasFieldOrPropertyWithValue("visible", false);
			});

		});

	}

	private ObservableList<XYChart.Data<Number, Number>> generateData( int pointsCount ) {

		int[] yValues = generateIntArray(0, 5, pointsCount);
		List<XYChart.Data<Number, Number>> data = new ArrayList<>(pointsCount);

		for ( int i = 0; i < yValues.length; i++ ) {
			data.add(new XYChart.Data<>(i, yValues[i]));
		}

		return FXCollections.observableArrayList(data);

	}

	private int[] generateIntArray( int firstValue, int variance, int size ) {

		int[] data = new int[size];

		data[0] = firstValue;

		for ( int i = 1; i < data.length; i++ ) {

			int sign = RANDOM.nextBoolean() ? 1 : -1;

			data[i] = data[i - 1] + (int) ( variance * RANDOM.nextDouble() ) * sign;

		}

		return data;

	}

}
