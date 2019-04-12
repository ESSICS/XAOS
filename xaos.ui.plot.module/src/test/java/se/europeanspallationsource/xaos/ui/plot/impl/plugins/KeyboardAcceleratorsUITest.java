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
package se.europeanspallationsource.xaos.ui.plot.impl.plugins;


import chart.LineChartFX;
import chart.Plugin;
import chart.data.DataReducingSeries;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeoutException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.assertj.core.data.Offset;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import se.europeanspallationsource.xaos.ui.plot.plugins.Plugins;

import static javafx.scene.input.MouseButton.PRIMARY;
import static org.assertj.core.api.Assertions.assertThat;
import static se.europeanspallationsource.xaos.ui.control.NavigatorController.PAN_DOWN_ACCELERATOR;
import static se.europeanspallationsource.xaos.ui.control.NavigatorController.PAN_LEFT_ACCELERATOR;
import static se.europeanspallationsource.xaos.ui.control.NavigatorController.PAN_RIGHT_ACCELERATOR;
import static se.europeanspallationsource.xaos.ui.control.NavigatorController.PAN_UP_ACCELERATOR;
import static se.europeanspallationsource.xaos.ui.control.NavigatorController.REDO_ACCELERATOR;
import static se.europeanspallationsource.xaos.ui.control.NavigatorController.UNDO_ACCELERATOR;
import static se.europeanspallationsource.xaos.ui.control.NavigatorController.ZOOM_IN_ACCELERATOR;
import static se.europeanspallationsource.xaos.ui.control.NavigatorController.ZOOM_OUT_ACCELERATOR;
import static se.europeanspallationsource.xaos.ui.control.NavigatorController.ZOOM_TO_ONE_ACCELERATOR;


/**
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( { "UseOfSystemOutOrSystemErr", "ClassWithoutLogger" } )
public class KeyboardAcceleratorsUITest extends ApplicationTest {

	private static final int POINTS_COUNT = 20;
	private static final Random RANDOM = new Random(System.currentTimeMillis());

	@BeforeClass
	public static void setUpClass() {
		System.out.println("---- KeyboardAcceleratorsUITest --------------------------------");
	}

	private LineChartFX<Number, Number> chart;
	private double chartHeight;
	private double chartWidth;
	private double chartXLowerBound;
	private double chartXUpperBound;
	private double chartYLowerBound;
	private double chartYUpperBound;
	private Plugin keyboardAccelerators;

	@Override
	@SuppressWarnings( "NestedAssignment" )
	public void start( Stage stage ) throws IOException {

		final NumberAxis xAxis = new NumberAxis();
		final NumberAxis yAxis = new NumberAxis();

		xAxis.setAnimated(false);
		yAxis.setAnimated(false);

		chart = new LineChartFX<>(xAxis, yAxis);

		chart.setTitle("KeyboardAcceleratorsUITest");
		chart.setAnimated(false);
		chart.getPlugins().addAll(
			keyboardAccelerators = Plugins.keyboardAccelerators()
		);

		DataReducingSeries<Number, Number> series0 = new DataReducingSeries<>();

		series0.setName("Generated test data-horizontal");
		series0.setData(generateData(POINTS_COUNT));
		chart.getData().add(series0.getSeries());

		DataReducingSeries<Number, Number> series1 = new DataReducingSeries<>();

		series1.setName("Generated test data-vertical");
		series1.setData(generateData(POINTS_COUNT));
		chart.getData().add(series1.getSeries());

		DataReducingSeries<Number, Number> series2 = new DataReducingSeries<>();

		series2.setName("Generated test data-longitudinal");
		series2.setData(generateData(POINTS_COUNT));
		chart.getData().add(series2.getSeries());

        chart.setFocusTraversable(true);

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

		System.out.println("  Testing ''KeyboardAccelerator''...");

		FxRobot robot = new FxRobot();

		//	Get chart's reference bounds...
		chartXLowerBound = keyboardAccelerators.getXValueAxis().getLowerBound();
		chartXUpperBound = keyboardAccelerators.getXValueAxis().getUpperBound();
		chartWidth       = chartXUpperBound - chartXLowerBound;
		chartYLowerBound = keyboardAccelerators.getYValueAxis().getLowerBound();
		chartYUpperBound = keyboardAccelerators.getYValueAxis().getUpperBound();
		chartHeight      = chartYUpperBound - chartYLowerBound;

		//	Activate the tool...
		robot.moveTo(chart);
		robot.clickOn(PRIMARY);

		//	--------------------------------------------------------------------
		//	Test each keyboard accelerator...
		//	--------------------------------------------------------------------

		// Testing PAN DOWN...
		System.out.println("    - Testing PAN DOWN...");
		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) PAN_DOWN_ACCELERATOR);
		assertThat(keyboardAccelerators.getYValueAxis().getLowerBound()).isLessThan(chartYLowerBound);
		assertThat(keyboardAccelerators.getYValueAxis().getUpperBound()).isLessThan(chartYUpperBound);
		assertThat(keyboardAccelerators.getYValueAxis().getUpperBound() - keyboardAccelerators.getYValueAxis().getLowerBound()).isEqualTo(chartHeight, Offset.offset(0.01));

		// Testing PAN UP...
		System.out.println("    - Testing PAN UP...");
		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) PAN_UP_ACCELERATOR);
		assertThat(keyboardAccelerators.getYValueAxis().getLowerBound()).isGreaterThan(chartYLowerBound);
		assertThat(keyboardAccelerators.getYValueAxis().getUpperBound()).isGreaterThan(chartYUpperBound);
		assertThat(keyboardAccelerators.getYValueAxis().getUpperBound() - keyboardAccelerators.getYValueAxis().getLowerBound()).isEqualTo(chartHeight, Offset.offset(0.01));

		// Testing PAN LEFT...
		System.out.println("    - Testing PAN LEFT...");
		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) PAN_LEFT_ACCELERATOR);
		assertThat(keyboardAccelerators.getXValueAxis().getLowerBound()).isLessThan(chartXLowerBound);
		assertThat(keyboardAccelerators.getXValueAxis().getUpperBound()).isLessThan(chartXUpperBound);
		assertThat(keyboardAccelerators.getXValueAxis().getUpperBound() - keyboardAccelerators.getXValueAxis().getLowerBound()).isEqualTo(chartWidth, Offset.offset(0.01));

		// Testing PAN RIGHT...
		System.out.println("    - Testing PAN RIGHT...");
		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) PAN_RIGHT_ACCELERATOR);
		assertThat(keyboardAccelerators.getXValueAxis().getLowerBound()).isGreaterThan(chartXLowerBound);
		assertThat(keyboardAccelerators.getXValueAxis().getUpperBound()).isGreaterThan(chartXUpperBound);
		assertThat(keyboardAccelerators.getXValueAxis().getUpperBound() - keyboardAccelerators.getXValueAxis().getLowerBound()).isEqualTo(chartWidth, Offset.offset(0.01));

		// Testing ZOOM IN...
		System.out.println("    - Testing ZOOM IN...");
		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) ZOOM_IN_ACCELERATOR);
		assertThat(keyboardAccelerators.getXValueAxis().getLowerBound()).isGreaterThan(chartXLowerBound);
		assertThat(keyboardAccelerators.getXValueAxis().getUpperBound()).isLessThan(chartXUpperBound);
		assertThat(keyboardAccelerators.getXValueAxis().getUpperBound() - keyboardAccelerators.getXValueAxis().getLowerBound()).isLessThan(chartWidth);
		assertThat(keyboardAccelerators.getYValueAxis().getLowerBound()).isGreaterThan(chartYLowerBound);
		assertThat(keyboardAccelerators.getYValueAxis().getUpperBound()).isLessThan(chartYUpperBound);
		assertThat(keyboardAccelerators.getYValueAxis().getUpperBound() - keyboardAccelerators.getYValueAxis().getLowerBound()).isLessThan(chartHeight);

		// Testing ZOOM OUT...
		System.out.println("    - Testing ZOOM OUT...");
		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) ZOOM_OUT_ACCELERATOR);
		assertThat(keyboardAccelerators.getXValueAxis().getLowerBound()).isLessThan(chartXLowerBound);
		assertThat(keyboardAccelerators.getXValueAxis().getUpperBound()).isGreaterThan(chartXUpperBound);
		assertThat(keyboardAccelerators.getXValueAxis().getUpperBound() - keyboardAccelerators.getXValueAxis().getLowerBound()).isGreaterThan(chartWidth);
		assertThat(keyboardAccelerators.getYValueAxis().getLowerBound()).isLessThan(chartYLowerBound);
		assertThat(keyboardAccelerators.getYValueAxis().getUpperBound()).isGreaterThan(chartYUpperBound);
		assertThat(keyboardAccelerators.getYValueAxis().getUpperBound() - keyboardAccelerators.getYValueAxis().getLowerBound()).isGreaterThan(chartHeight);

		// Testing ZOOM TO ONE...
		System.out.println("    - Testing ZOOM TO ONE...");
		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) ZOOM_IN_ACCELERATOR);
		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) ZOOM_TO_ONE_ACCELERATOR, false);
		assertThat(keyboardAccelerators.getXValueAxis().getLowerBound()).isEqualTo(chartXLowerBound, Offset.offset(0.01));
		assertThat(keyboardAccelerators.getXValueAxis().getUpperBound()).isEqualTo(chartXUpperBound, Offset.offset(0.01));
		assertThat(keyboardAccelerators.getYValueAxis().getLowerBound()).isEqualTo(chartYLowerBound, Offset.offset(0.01));
		assertThat(keyboardAccelerators.getYValueAxis().getUpperBound()).isEqualTo(chartYUpperBound, Offset.offset(0.01));

		// Testing UNDO...
		System.out.println("    - Testing UNDO...");
		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) ZOOM_IN_ACCELERATOR);
		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) UNDO_ACCELERATOR, false);
		assertThat(keyboardAccelerators.getXValueAxis().getLowerBound()).isEqualTo(chartXLowerBound, Offset.offset(0.01));
		assertThat(keyboardAccelerators.getXValueAxis().getUpperBound()).isEqualTo(chartXUpperBound, Offset.offset(0.01));
		assertThat(keyboardAccelerators.getYValueAxis().getLowerBound()).isEqualTo(chartYLowerBound, Offset.offset(0.01));
		assertThat(keyboardAccelerators.getYValueAxis().getUpperBound()).isEqualTo(chartYUpperBound, Offset.offset(0.01));

		// Testing REDO...
		System.out.println("    - Testing REDO...");
		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) REDO_ACCELERATOR, false);
		//	Now it should be after Zoom In done
		assertThat(keyboardAccelerators.getXValueAxis().getLowerBound()).isGreaterThan(chartXLowerBound);
		assertThat(keyboardAccelerators.getXValueAxis().getUpperBound()).isLessThan(chartXUpperBound);
		assertThat(keyboardAccelerators.getXValueAxis().getUpperBound() - keyboardAccelerators.getXValueAxis().getLowerBound()).isLessThan(chartWidth);
		assertThat(keyboardAccelerators.getYValueAxis().getLowerBound()).isGreaterThan(chartYLowerBound);
		assertThat(keyboardAccelerators.getYValueAxis().getUpperBound()).isLessThan(chartYUpperBound);
		assertThat(keyboardAccelerators.getYValueAxis().getUpperBound() - keyboardAccelerators.getYValueAxis().getLowerBound()).isLessThan(chartHeight);

	}

	private void acceleratorsResetChartAndPress ( FxRobot robot, KeyCodeCombination combination ) {
		acceleratorsResetChartAndPress(robot, combination, true);
	}

	private void acceleratorsResetChartAndPress ( FxRobot robot, KeyCodeCombination combination, boolean reset ) {

		if ( reset ) {
			keyboardAccelerators.getXValueAxis().setLowerBound(chartXLowerBound);
			keyboardAccelerators.getXValueAxis().setUpperBound(chartXUpperBound);
			keyboardAccelerators.getYValueAxis().setLowerBound(chartYLowerBound);
			keyboardAccelerators.getYValueAxis().setUpperBound(chartYUpperBound);
		}

		robot.push(combination);

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
