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
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
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

import static javafx.geometry.Pos.CENTER;
import static javafx.scene.input.MouseButton.PRIMARY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testfx.robot.Motion.DEFAULT;


/**
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( { "UseOfSystemOutOrSystemErr", "ClassWithoutLogger" } )
public class PannerUITest extends ApplicationTest {

	private static final int POINTS_COUNT = 20;
	private static final Random RANDOM = new Random(System.currentTimeMillis());

	@BeforeClass
	public static void setUpClass() {
		System.out.println("---- PannerUITest ----------------------------------------------");
	}

	private LineChartFX<Number, Number> chart;
	private double chartHeight;
	private double chartWidth;
	private double chartXLowerBound;
	private double chartXUpperBound;
	private double chartYLowerBound;
	private double chartYUpperBound;
	private Plugin panner;

	@Override
	@SuppressWarnings( "NestedAssignment" )
	public void start( Stage stage ) throws IOException {

		final NumberAxis xAxis = new NumberAxis();
		final NumberAxis yAxis = new NumberAxis();

		xAxis.setAnimated(false);
		yAxis.setAnimated(false);

		chart = new LineChartFX<>(xAxis, yAxis);

		chart.setTitle("PannerUITest");
		chart.setAnimated(false);
		chart.getPlugins().addAll(
			panner = Plugins.panner()
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

		System.out.println("  Testing ''Panner''...");

		FxRobot robot = new FxRobot();

		//	Get chart's reference bounds...
		chartXLowerBound = panner.getXValueAxis().getLowerBound();
		chartXUpperBound = panner.getXValueAxis().getUpperBound();
		chartWidth       = chartXUpperBound - chartXLowerBound;
		chartYLowerBound = panner.getYValueAxis().getLowerBound();
		chartYUpperBound = panner.getYValueAxis().getUpperBound();
		chartHeight      = chartYUpperBound - chartYLowerBound;

		//	Activate the tool...
		robot.moveTo(chart);
		robot.clickOn(PRIMARY);

		//	--------------------------------------------------------------------
		//	Test each mouse and scroll-wheel positions...
		//	--------------------------------------------------------------------

		// Testing mouse PAN DOWN...
		System.out.println("    - Testing mouse mouse PAN DOWN...");
		mouseResetChartAndDrag(robot, new Point2D(0, 100), true);
		assertThat(panner.getYValueAxis().getLowerBound()).isLessThan(chartYLowerBound);
		assertThat(panner.getYValueAxis().getUpperBound()).isLessThan(chartYUpperBound);
		assertThat(panner.getYValueAxis().getUpperBound() - panner.getYValueAxis().getLowerBound()).isEqualTo(chartHeight, Offset.offset(0.01));

//		// Testing mouse PAN UP...
//		System.out.println("    - Testing mouse PAN UP...");
//		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) PAN_UP_ACCELERATOR);
//		assertThat(keyboardAccelerators.getYValueAxis().getLowerBound()).isGreaterThan(chartYLowerBound);
//		assertThat(keyboardAccelerators.getYValueAxis().getUpperBound()).isGreaterThan(chartYUpperBound);
//		assertThat(keyboardAccelerators.getYValueAxis().getUpperBound() - keyboardAccelerators.getYValueAxis().getLowerBound()).isEqualTo(chartHeight, Offset.offset(0.01));
//
//		// Testing mouse PAN LEFT...
//		System.out.println("    - Testing mouse PAN LEFT...");
//		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) PAN_LEFT_ACCELERATOR);
//		assertThat(keyboardAccelerators.getXValueAxis().getLowerBound()).isLessThan(chartXLowerBound);
//		assertThat(keyboardAccelerators.getXValueAxis().getUpperBound()).isLessThan(chartXUpperBound);
//		assertThat(keyboardAccelerators.getXValueAxis().getUpperBound() - keyboardAccelerators.getXValueAxis().getLowerBound()).isEqualTo(chartWidth, Offset.offset(0.01));
//
//		// Testing mouse PAN RIGHT...
//		System.out.println("    - Testing mouse PAN RIGHT...");
//		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) PAN_RIGHT_ACCELERATOR);
//		assertThat(keyboardAccelerators.getXValueAxis().getLowerBound()).isGreaterThan(chartXLowerBound);
//		assertThat(keyboardAccelerators.getXValueAxis().getUpperBound()).isGreaterThan(chartXUpperBound);
//		assertThat(keyboardAccelerators.getXValueAxis().getUpperBound() - keyboardAccelerators.getXValueAxis().getLowerBound()).isEqualTo(chartWidth, Offset.offset(0.01));
//
//		// Testing mouse ZOOM IN...
//		System.out.println("    - Testing mouse ZOOM IN...");
//		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) ZOOM_IN_ACCELERATOR);
//		assertThat(keyboardAccelerators.getXValueAxis().getLowerBound()).isGreaterThan(chartXLowerBound);
//		assertThat(keyboardAccelerators.getXValueAxis().getUpperBound()).isLessThan(chartXUpperBound);
//		assertThat(keyboardAccelerators.getXValueAxis().getUpperBound() - keyboardAccelerators.getXValueAxis().getLowerBound()).isLessThan(chartWidth);
//		assertThat(keyboardAccelerators.getYValueAxis().getLowerBound()).isGreaterThan(chartYLowerBound);
//		assertThat(keyboardAccelerators.getYValueAxis().getUpperBound()).isLessThan(chartYUpperBound);
//		assertThat(keyboardAccelerators.getYValueAxis().getUpperBound() - keyboardAccelerators.getYValueAxis().getLowerBound()).isLessThan(chartHeight);
//
//		// Testing mouse ZOOM OUT...
//		System.out.println("    - Testing mouse ZOOM OUT...");
//		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) ZOOM_OUT_ACCELERATOR);
//		assertThat(keyboardAccelerators.getXValueAxis().getLowerBound()).isLessThan(chartXLowerBound);
//		assertThat(keyboardAccelerators.getXValueAxis().getUpperBound()).isGreaterThan(chartXUpperBound);
//		assertThat(keyboardAccelerators.getXValueAxis().getUpperBound() - keyboardAccelerators.getXValueAxis().getLowerBound()).isGreaterThan(chartWidth);
//		assertThat(keyboardAccelerators.getYValueAxis().getLowerBound()).isLessThan(chartYLowerBound);
//		assertThat(keyboardAccelerators.getYValueAxis().getUpperBound()).isGreaterThan(chartYUpperBound);
//		assertThat(keyboardAccelerators.getYValueAxis().getUpperBound() - keyboardAccelerators.getYValueAxis().getLowerBound()).isGreaterThan(chartHeight);
//
//		// Testing mouse ZOOM TO ONE...
//		System.out.println("    - Testing mouse ZOOM TO ONE...");
//		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) ZOOM_IN_ACCELERATOR);
//		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) ZOOM_TO_ONE_ACCELERATOR, false);
//		assertThat(keyboardAccelerators.getXValueAxis().getLowerBound()).isEqualTo(chartXLowerBound, Offset.offset(0.01));
//		assertThat(keyboardAccelerators.getXValueAxis().getUpperBound()).isEqualTo(chartXUpperBound, Offset.offset(0.01));
//		assertThat(keyboardAccelerators.getYValueAxis().getLowerBound()).isEqualTo(chartYLowerBound, Offset.offset(0.01));
//		assertThat(keyboardAccelerators.getYValueAxis().getUpperBound()).isEqualTo(chartYUpperBound, Offset.offset(0.01));
//
//		// Testing mouse UNDO...
//		System.out.println("    - Testing mouse UNDO...");
//		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) ZOOM_IN_ACCELERATOR);
//		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) UNDO_ACCELERATOR, false);
//		assertThat(keyboardAccelerators.getXValueAxis().getLowerBound()).isEqualTo(chartXLowerBound, Offset.offset(0.01));
//		assertThat(keyboardAccelerators.getXValueAxis().getUpperBound()).isEqualTo(chartXUpperBound, Offset.offset(0.01));
//		assertThat(keyboardAccelerators.getYValueAxis().getLowerBound()).isEqualTo(chartYLowerBound, Offset.offset(0.01));
//		assertThat(keyboardAccelerators.getYValueAxis().getUpperBound()).isEqualTo(chartYUpperBound, Offset.offset(0.01));
//
//		// Testing mouse REDO...
//		System.out.println("    - Testing mouse REDO...");
//		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) REDO_ACCELERATOR, false);
//		//	Now it should be after Zoom In done
//		assertThat(keyboardAccelerators.getXValueAxis().getLowerBound()).isGreaterThan(chartXLowerBound);
//		assertThat(keyboardAccelerators.getXValueAxis().getUpperBound()).isLessThan(chartXUpperBound);
//		assertThat(keyboardAccelerators.getXValueAxis().getUpperBound() - keyboardAccelerators.getXValueAxis().getLowerBound()).isLessThan(chartWidth);
//		assertThat(keyboardAccelerators.getYValueAxis().getLowerBound()).isGreaterThan(chartYLowerBound);
//		assertThat(keyboardAccelerators.getYValueAxis().getUpperBound()).isLessThan(chartYUpperBound);
//		assertThat(keyboardAccelerators.getYValueAxis().getUpperBound() - keyboardAccelerators.getYValueAxis().getLowerBound()).isLessThan(chartHeight);

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

	private void mouseResetChartAndDrag ( FxRobot robot, Point2D offset, boolean release ) {
		mouseResetChartAndDrag(robot, offset, release, true);
	}

	private void mouseResetChartAndDrag ( FxRobot robot, Point2D offset, boolean release, boolean reset ) {

		if ( reset ) {
			panner.getXValueAxis().setLowerBound(chartXLowerBound);
			panner.getXValueAxis().setUpperBound(chartXUpperBound);
			panner.getYValueAxis().setLowerBound(chartYLowerBound);
			panner.getYValueAxis().setUpperBound(chartYUpperBound);
		}

		robot.moveTo(chart, CENTER, Point2D.ZERO, DEFAULT);
		robot.drag(PRIMARY);
//		robot.press(PRIMARY);
		robot.moveBy(offset.getX(), offset.getY());

		if ( release ) {
			robot.drop();
		}

	}

}
