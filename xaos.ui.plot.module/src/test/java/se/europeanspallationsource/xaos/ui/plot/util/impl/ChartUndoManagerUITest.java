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
package se.europeanspallationsource.xaos.ui.plot.util.impl;


import se.europeanspallationsource.xaos.ui.plot.LineChartFX;
import se.europeanspallationsource.xaos.ui.plot.Plugin;
import se.europeanspallationsource.xaos.ui.plot.data.DataReducingSeries;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeoutException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.geometry.VerticalDirection;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import se.europeanspallationsource.xaos.core.util.ThreadUtils;
import se.europeanspallationsource.xaos.ui.plot.plugins.Plugins;

import static javafx.geometry.Pos.CENTER;
import static javafx.geometry.VerticalDirection.DOWN;
import static javafx.geometry.VerticalDirection.UP;
import static javafx.scene.input.KeyCode.ALT;
import static javafx.scene.input.MouseButton.PRIMARY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.testfx.robot.Motion.DEFAULT;
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
public class ChartUndoManagerUITest extends ApplicationTest {

	private static final Point2D PAN_DOWN_POINT = new Point2D(0, 25);
	private static final Point2D PAN_LEFT_POINT = new Point2D(-25, 0);
	private static final Point2D PAN_RIGHT_POINT = new Point2D(25, 0);
	private static final Point2D PAN_UP_POINT = new Point2D(0, -25);
	private static final int POINTS_COUNT = 20;
	private static final Random RANDOM = new Random(System.currentTimeMillis());
	private static final Point2D REDO_POINT = new Point2D(-40, 40);
	private static final Point2D UNDO_POINT = new Point2D(-40, -40);
	private static final Point2D ZOOM_IN_POINT = new Point2D(40, -40);
	private static final Point2D ZOOM_OUT_POINT = new Point2D(40, 40);
	private static final Point2D ZOOM_TO_ONE_POINT = new Point2D(0, 0);

	@BeforeClass
	public static void setUpClass() {
		System.out.println("---- ChartUndoManagerUITest ------------------------------------");
	}

	private LineChartFX<Number, Number> chart;
	private boolean chartXAutoRange;
	private double chartXLowerBound;
	private double chartXUpperBound;
	private boolean chartYAutoRange;
	private double chartYLowerBound;
	private double chartYUpperBound;
	private Plugin keyboardAccelerators;
	private Plugin navigator;
	private Plugin panner;
	private Plugin zoomer;

	@Override
	@SuppressWarnings( "NestedAssignment" )
	public void start( Stage stage ) throws IOException {

		final NumberAxis xAxis = new NumberAxis();
		final NumberAxis yAxis = new NumberAxis();

		xAxis.setAnimated(false);
		yAxis.setAnimated(false);

		chart = new LineChartFX<>(xAxis, yAxis);

		chart.setTitle("ChartUndoManagerUITest");
		chart.setAnimated(false);
		chart.getPlugins().addAll(
			navigator            = Plugins.navigator(),
			keyboardAccelerators = Plugins.keyboardAccelerators(),
			panner               = Plugins.panner(),
			zoomer               = Plugins.zoomer()
//			new CoordinatesLines(),
//			new CoordinatesLabel(),
//			new DataPointTooltip(),
//			new AreaValueTooltip(),
//			new PropertyMenu()
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

	/**
	 * Test UNDO and REDO on KayboardAccelerators.
	 */
	@Test
	public void testKayboardAccelerators() {

		System.out.println("  Testing ''ChartUndoManager'' on KayboardAccelerators...");

		FxRobot robot = new FxRobot();
		ChartUndoManager undoManager = ChartUndoManager.get(keyboardAccelerators.getChart());

		//	Get chart's reference bounds...
		chartXAutoRange  = keyboardAccelerators.getXValueAxis().isAutoRanging();
		chartXLowerBound = keyboardAccelerators.getXValueAxis().getLowerBound();
		chartXUpperBound = keyboardAccelerators.getXValueAxis().getUpperBound();
		chartYAutoRange  = keyboardAccelerators.getYValueAxis().isAutoRanging();
		chartYLowerBound = keyboardAccelerators.getYValueAxis().getLowerBound();
		chartYUpperBound = keyboardAccelerators.getYValueAxis().getUpperBound();

		//	Activate the tool...
		robot.moveTo(chart);
		robot.clickOn(PRIMARY);

		//	Assert initial conditions...
		assertFalse(undoManager.isRedoable());
		assertFalse(undoManager.isUndoable());

		// Testing PAN DOWN...
		System.out.println("    - Testing PAN DOWN...");
		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) PAN_DOWN_ACCELERATOR);
		assertFalse(undoManager.isRedoable());
		assertTrue(undoManager.isUndoable());
		assertThat(keyboardAccelerators.getXValueAxis().getLowerBound()).isEqualTo(chartXLowerBound);
		assertThat(keyboardAccelerators.getXValueAxis().getUpperBound()).isEqualTo(chartXUpperBound);
		assertThat(keyboardAccelerators.getYValueAxis().getLowerBound()).isNotEqualTo(chartYLowerBound);
		assertThat(keyboardAccelerators.getYValueAxis().getUpperBound()).isNotEqualTo(chartYUpperBound);
		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) UNDO_ACCELERATOR, false);
		assertTrue(undoManager.isRedoable());
		assertFalse(undoManager.isUndoable());
		assertThat(keyboardAccelerators.getXValueAxis().getLowerBound()).isEqualTo(chartXLowerBound);
		assertThat(keyboardAccelerators.getXValueAxis().getUpperBound()).isEqualTo(chartXUpperBound);
		assertThat(keyboardAccelerators.getYValueAxis().getLowerBound()).isEqualTo(chartYLowerBound);
		assertThat(keyboardAccelerators.getYValueAxis().getUpperBound()).isEqualTo(chartYUpperBound);
		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) REDO_ACCELERATOR, false);
		assertFalse(undoManager.isRedoable());
		assertTrue(undoManager.isUndoable());
		assertThat(keyboardAccelerators.getXValueAxis().getLowerBound()).isEqualTo(chartXLowerBound);
		assertThat(keyboardAccelerators.getXValueAxis().getUpperBound()).isEqualTo(chartXUpperBound);
		assertThat(keyboardAccelerators.getYValueAxis().getLowerBound()).isNotEqualTo(chartYLowerBound);
		assertThat(keyboardAccelerators.getYValueAxis().getUpperBound()).isNotEqualTo(chartYUpperBound);
		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) UNDO_ACCELERATOR, false);
		assertTrue(undoManager.isRedoable());
		assertFalse(undoManager.isUndoable());

		// Testing PAN UP...
		System.out.println("    - Testing PAN UP...");
		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) PAN_UP_ACCELERATOR);
		assertFalse(undoManager.isRedoable());
		assertTrue(undoManager.isUndoable());
		assertThat(keyboardAccelerators.getXValueAxis().getLowerBound()).isEqualTo(chartXLowerBound);
		assertThat(keyboardAccelerators.getXValueAxis().getUpperBound()).isEqualTo(chartXUpperBound);
		assertThat(keyboardAccelerators.getYValueAxis().getLowerBound()).isNotEqualTo(chartYLowerBound);
		assertThat(keyboardAccelerators.getYValueAxis().getUpperBound()).isNotEqualTo(chartYUpperBound);
		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) UNDO_ACCELERATOR, false);
		assertTrue(undoManager.isRedoable());
		assertFalse(undoManager.isUndoable());
		assertThat(keyboardAccelerators.getXValueAxis().getLowerBound()).isEqualTo(chartXLowerBound);
		assertThat(keyboardAccelerators.getXValueAxis().getUpperBound()).isEqualTo(chartXUpperBound);
		assertThat(keyboardAccelerators.getYValueAxis().getLowerBound()).isEqualTo(chartYLowerBound);
		assertThat(keyboardAccelerators.getYValueAxis().getUpperBound()).isEqualTo(chartYUpperBound);
		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) REDO_ACCELERATOR, false);
		assertFalse(undoManager.isRedoable());
		assertTrue(undoManager.isUndoable());
		assertThat(keyboardAccelerators.getXValueAxis().getLowerBound()).isEqualTo(chartXLowerBound);
		assertThat(keyboardAccelerators.getXValueAxis().getUpperBound()).isEqualTo(chartXUpperBound);
		assertThat(keyboardAccelerators.getYValueAxis().getLowerBound()).isNotEqualTo(chartYLowerBound);
		assertThat(keyboardAccelerators.getYValueAxis().getUpperBound()).isNotEqualTo(chartYUpperBound);
		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) UNDO_ACCELERATOR, false);
		assertTrue(undoManager.isRedoable());
		assertFalse(undoManager.isUndoable());

		// Testing PAN LEFT...
		System.out.println("    - Testing PAN LEFT...");
		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) PAN_LEFT_ACCELERATOR);
		assertFalse(undoManager.isRedoable());
		assertTrue(undoManager.isUndoable());
		assertThat(keyboardAccelerators.getXValueAxis().getLowerBound()).isNotEqualTo(chartXLowerBound);
		assertThat(keyboardAccelerators.getXValueAxis().getUpperBound()).isNotEqualTo(chartXUpperBound);
		assertThat(keyboardAccelerators.getYValueAxis().getLowerBound()).isEqualTo(chartYLowerBound);
		assertThat(keyboardAccelerators.getYValueAxis().getUpperBound()).isEqualTo(chartYUpperBound);
		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) UNDO_ACCELERATOR, false);
		assertTrue(undoManager.isRedoable());
		assertFalse(undoManager.isUndoable());
		assertThat(keyboardAccelerators.getXValueAxis().getLowerBound()).isEqualTo(chartXLowerBound);
		assertThat(keyboardAccelerators.getXValueAxis().getUpperBound()).isEqualTo(chartXUpperBound);
		assertThat(keyboardAccelerators.getYValueAxis().getLowerBound()).isEqualTo(chartYLowerBound);
		assertThat(keyboardAccelerators.getYValueAxis().getUpperBound()).isEqualTo(chartYUpperBound);
		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) REDO_ACCELERATOR, false);
		assertFalse(undoManager.isRedoable());
		assertTrue(undoManager.isUndoable());
		assertThat(keyboardAccelerators.getXValueAxis().getLowerBound()).isNotEqualTo(chartXLowerBound);
		assertThat(keyboardAccelerators.getXValueAxis().getUpperBound()).isNotEqualTo(chartXUpperBound);
		assertThat(keyboardAccelerators.getYValueAxis().getLowerBound()).isEqualTo(chartYLowerBound);
		assertThat(keyboardAccelerators.getYValueAxis().getUpperBound()).isEqualTo(chartYUpperBound);
		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) UNDO_ACCELERATOR, false);
		assertTrue(undoManager.isRedoable());
		assertFalse(undoManager.isUndoable());

		// Testing PAN RIGHT...
		System.out.println("    - Testing PAN RIGHT...");
		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) PAN_RIGHT_ACCELERATOR);
		assertFalse(undoManager.isRedoable());
		assertTrue(undoManager.isUndoable());
		assertThat(keyboardAccelerators.getXValueAxis().getLowerBound()).isNotEqualTo(chartXLowerBound);
		assertThat(keyboardAccelerators.getXValueAxis().getUpperBound()).isNotEqualTo(chartXUpperBound);
		assertThat(keyboardAccelerators.getYValueAxis().getLowerBound()).isEqualTo(chartYLowerBound);
		assertThat(keyboardAccelerators.getYValueAxis().getUpperBound()).isEqualTo(chartYUpperBound);
		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) UNDO_ACCELERATOR, false);
		assertTrue(undoManager.isRedoable());
		assertFalse(undoManager.isUndoable());
		assertThat(keyboardAccelerators.getXValueAxis().getLowerBound()).isEqualTo(chartXLowerBound);
		assertThat(keyboardAccelerators.getXValueAxis().getUpperBound()).isEqualTo(chartXUpperBound);
		assertThat(keyboardAccelerators.getYValueAxis().getLowerBound()).isEqualTo(chartYLowerBound);
		assertThat(keyboardAccelerators.getYValueAxis().getUpperBound()).isEqualTo(chartYUpperBound);
		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) REDO_ACCELERATOR, false);
		assertFalse(undoManager.isRedoable());
		assertTrue(undoManager.isUndoable());
		assertThat(keyboardAccelerators.getXValueAxis().getLowerBound()).isNotEqualTo(chartXLowerBound);
		assertThat(keyboardAccelerators.getXValueAxis().getUpperBound()).isNotEqualTo(chartXUpperBound);
		assertThat(keyboardAccelerators.getYValueAxis().getLowerBound()).isEqualTo(chartYLowerBound);
		assertThat(keyboardAccelerators.getYValueAxis().getUpperBound()).isEqualTo(chartYUpperBound);
		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) UNDO_ACCELERATOR, false);
		assertTrue(undoManager.isRedoable());
		assertFalse(undoManager.isUndoable());

		// Testing ZOOM IN...
		System.out.println("    - Testing ZOOM IN...");
		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) ZOOM_IN_ACCELERATOR);
		assertFalse(undoManager.isRedoable());
		assertTrue(undoManager.isUndoable());
		assertThat(keyboardAccelerators.getXValueAxis().getLowerBound()).isNotEqualTo(chartXLowerBound);
		assertThat(keyboardAccelerators.getXValueAxis().getUpperBound()).isNotEqualTo(chartXUpperBound);
		assertThat(keyboardAccelerators.getYValueAxis().getLowerBound()).isNotEqualTo(chartYLowerBound);
		assertThat(keyboardAccelerators.getYValueAxis().getUpperBound()).isNotEqualTo(chartYUpperBound);
		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) UNDO_ACCELERATOR, false);
		assertTrue(undoManager.isRedoable());
		assertFalse(undoManager.isUndoable());
		assertThat(keyboardAccelerators.getXValueAxis().getLowerBound()).isEqualTo(chartXLowerBound);
		assertThat(keyboardAccelerators.getXValueAxis().getUpperBound()).isEqualTo(chartXUpperBound);
		assertThat(keyboardAccelerators.getYValueAxis().getLowerBound()).isEqualTo(chartYLowerBound);
		assertThat(keyboardAccelerators.getYValueAxis().getUpperBound()).isEqualTo(chartYUpperBound);
		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) REDO_ACCELERATOR, false);
		assertFalse(undoManager.isRedoable());
		assertTrue(undoManager.isUndoable());
		assertThat(keyboardAccelerators.getXValueAxis().getLowerBound()).isNotEqualTo(chartXLowerBound);
		assertThat(keyboardAccelerators.getXValueAxis().getUpperBound()).isNotEqualTo(chartXUpperBound);
		assertThat(keyboardAccelerators.getYValueAxis().getLowerBound()).isNotEqualTo(chartYLowerBound);
		assertThat(keyboardAccelerators.getYValueAxis().getUpperBound()).isNotEqualTo(chartYUpperBound);
		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) UNDO_ACCELERATOR, false);
		assertTrue(undoManager.isRedoable());
		assertFalse(undoManager.isUndoable());

		// Testing ZOOM OUT...
		System.out.println("    - Testing ZOOM OUT...");
		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) ZOOM_OUT_ACCELERATOR);
		assertFalse(undoManager.isRedoable());
		assertTrue(undoManager.isUndoable());
		assertThat(keyboardAccelerators.getXValueAxis().getLowerBound()).isNotEqualTo(chartXLowerBound);
		assertThat(keyboardAccelerators.getXValueAxis().getUpperBound()).isNotEqualTo(chartXUpperBound);
		assertThat(keyboardAccelerators.getYValueAxis().getLowerBound()).isNotEqualTo(chartYLowerBound);
		assertThat(keyboardAccelerators.getYValueAxis().getUpperBound()).isNotEqualTo(chartYUpperBound);
		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) UNDO_ACCELERATOR, false);
		assertTrue(undoManager.isRedoable());
		assertFalse(undoManager.isUndoable());
		assertThat(keyboardAccelerators.getXValueAxis().getLowerBound()).isEqualTo(chartXLowerBound);
		assertThat(keyboardAccelerators.getXValueAxis().getUpperBound()).isEqualTo(chartXUpperBound);
		assertThat(keyboardAccelerators.getYValueAxis().getLowerBound()).isEqualTo(chartYLowerBound);
		assertThat(keyboardAccelerators.getYValueAxis().getUpperBound()).isEqualTo(chartYUpperBound);
		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) REDO_ACCELERATOR, false);
		assertFalse(undoManager.isRedoable());
		assertTrue(undoManager.isUndoable());
		assertThat(keyboardAccelerators.getXValueAxis().getLowerBound()).isNotEqualTo(chartXLowerBound);
		assertThat(keyboardAccelerators.getXValueAxis().getUpperBound()).isNotEqualTo(chartXUpperBound);
		assertThat(keyboardAccelerators.getYValueAxis().getLowerBound()).isNotEqualTo(chartYLowerBound);
		assertThat(keyboardAccelerators.getYValueAxis().getUpperBound()).isNotEqualTo(chartYUpperBound);
		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) UNDO_ACCELERATOR, false);
		assertTrue(undoManager.isRedoable());
		assertFalse(undoManager.isUndoable());

		// Testing ZOOM TO ONE...
		System.out.println("    - Testing ZOOM TO ONE...");
		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) ZOOM_IN_ACCELERATOR);
		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) ZOOM_TO_ONE_ACCELERATOR, false);
		assertFalse(undoManager.isRedoable());
		assertTrue(undoManager.isUndoable());
		assertThat(keyboardAccelerators.getXValueAxis().getLowerBound()).isEqualTo(chartXLowerBound);
		assertThat(keyboardAccelerators.getXValueAxis().getUpperBound()).isEqualTo(chartXUpperBound);
		assertThat(keyboardAccelerators.getYValueAxis().getLowerBound()).isEqualTo(chartYLowerBound);
		assertThat(keyboardAccelerators.getYValueAxis().getUpperBound()).isEqualTo(chartYUpperBound);
		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) UNDO_ACCELERATOR, false);
		assertTrue(undoManager.isRedoable());
		assertTrue(undoManager.isUndoable());
		assertThat(keyboardAccelerators.getXValueAxis().getLowerBound()).isNotEqualTo(chartXLowerBound);
		assertThat(keyboardAccelerators.getXValueAxis().getUpperBound()).isNotEqualTo(chartXUpperBound);
		assertThat(keyboardAccelerators.getYValueAxis().getLowerBound()).isNotEqualTo(chartYLowerBound);
		assertThat(keyboardAccelerators.getYValueAxis().getUpperBound()).isNotEqualTo(chartYUpperBound);
		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) REDO_ACCELERATOR, false);
		assertFalse(undoManager.isRedoable());
		assertTrue(undoManager.isUndoable());
		assertThat(keyboardAccelerators.getXValueAxis().getLowerBound()).isEqualTo(chartXLowerBound);
		assertThat(keyboardAccelerators.getXValueAxis().getUpperBound()).isEqualTo(chartXUpperBound);
		assertThat(keyboardAccelerators.getYValueAxis().getLowerBound()).isEqualTo(chartYLowerBound);
		assertThat(keyboardAccelerators.getYValueAxis().getUpperBound()).isEqualTo(chartYUpperBound);
		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) UNDO_ACCELERATOR, false);
		assertTrue(undoManager.isRedoable());
		assertTrue(undoManager.isUndoable());
		assertThat(keyboardAccelerators.getXValueAxis().getLowerBound()).isNotEqualTo(chartXLowerBound);
		assertThat(keyboardAccelerators.getXValueAxis().getUpperBound()).isNotEqualTo(chartXUpperBound);
		assertThat(keyboardAccelerators.getYValueAxis().getLowerBound()).isNotEqualTo(chartYLowerBound);
		assertThat(keyboardAccelerators.getYValueAxis().getUpperBound()).isNotEqualTo(chartYUpperBound);
		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) UNDO_ACCELERATOR, false);
		assertTrue(undoManager.isRedoable());
		assertFalse(undoManager.isUndoable());
		assertThat(keyboardAccelerators.getXValueAxis().getLowerBound()).isEqualTo(chartXLowerBound);
		assertThat(keyboardAccelerators.getXValueAxis().getUpperBound()).isEqualTo(chartXUpperBound);
		assertThat(keyboardAccelerators.getYValueAxis().getLowerBound()).isEqualTo(chartYLowerBound);
		assertThat(keyboardAccelerators.getYValueAxis().getUpperBound()).isEqualTo(chartYUpperBound);
		assertTrue(undoManager.isRedoable());
		assertFalse(undoManager.isUndoable());

		//	Testing multiple operations and undo/redo
		System.out.println("    - Testing multiple operation and undo/redo...");
		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) ZOOM_OUT_ACCELERATOR);
		assertThat(undoManager.getAvailableRedoables()).isEqualTo(0);
		assertThat(undoManager.getAvailableUndoables()).isEqualTo(1);
		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) PAN_DOWN_ACCELERATOR, false);
		assertThat(undoManager.getAvailableRedoables()).isEqualTo(0);
		assertThat(undoManager.getAvailableUndoables()).isEqualTo(2);
		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) PAN_DOWN_ACCELERATOR, false);
		assertThat(undoManager.getAvailableRedoables()).isEqualTo(0);
		assertThat(undoManager.getAvailableUndoables()).isEqualTo(3);
		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) PAN_LEFT_ACCELERATOR, false);
		assertThat(undoManager.getAvailableRedoables()).isEqualTo(0);
		assertThat(undoManager.getAvailableUndoables()).isEqualTo(4);
		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) ZOOM_OUT_ACCELERATOR, false);
		assertThat(undoManager.getAvailableRedoables()).isEqualTo(0);
		assertThat(undoManager.getAvailableUndoables()).isEqualTo(5);
		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) PAN_DOWN_ACCELERATOR, false);
		assertThat(undoManager.getAvailableRedoables()).isEqualTo(0);
		assertThat(undoManager.getAvailableUndoables()).isEqualTo(6);
		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) PAN_LEFT_ACCELERATOR, false);
		assertThat(undoManager.getAvailableRedoables()).isEqualTo(0);
		assertThat(undoManager.getAvailableUndoables()).isEqualTo(7);
		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) UNDO_ACCELERATOR, false);
		assertThat(undoManager.getAvailableRedoables()).isEqualTo(1);
		assertThat(undoManager.getAvailableUndoables()).isEqualTo(6);
		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) UNDO_ACCELERATOR, false);
		assertThat(undoManager.getAvailableRedoables()).isEqualTo(2);
		assertThat(undoManager.getAvailableUndoables()).isEqualTo(5);
		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) UNDO_ACCELERATOR, false);
		assertThat(undoManager.getAvailableRedoables()).isEqualTo(3);
		assertThat(undoManager.getAvailableUndoables()).isEqualTo(4);
		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) REDO_ACCELERATOR, false);
		assertThat(undoManager.getAvailableRedoables()).isEqualTo(2);
		assertThat(undoManager.getAvailableUndoables()).isEqualTo(5);
		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) PAN_DOWN_ACCELERATOR, false);
		assertThat(undoManager.getAvailableRedoables()).isEqualTo(0);
		assertThat(undoManager.getAvailableUndoables()).isEqualTo(6);

	}

	/**
	 * Test UNDO and REDO on Navigator.
	 */
	@Test
	public void testNavigator() {

		System.out.println("  Testing ''ChartUndoManager'' on Navigator...");

		FxRobot robot = new FxRobot();
		ChartUndoManager undoManager = ChartUndoManager.get(navigator.getChart());

		//	Get chart's reference bounds...
		chartXAutoRange  = navigator.getXValueAxis().isAutoRanging();
		chartXLowerBound = navigator.getXValueAxis().getLowerBound();
		chartXUpperBound = navigator.getXValueAxis().getUpperBound();
		chartYAutoRange  = navigator.getYValueAxis().isAutoRanging();
		chartYLowerBound = navigator.getYValueAxis().getLowerBound();
		chartYUpperBound = navigator.getYValueAxis().getUpperBound();

		//	Activate the tool...
		robot.moveTo(chart);
		robot.type(ALT);

		//	Assert initial conditions...
		assertFalse(undoManager.isRedoable());
		assertFalse(undoManager.isUndoable());

		// Testing PAN DOWN...
		System.out.println("    - Testing PAN DOWN...");
		navigatorResetChartMoveAndClick(robot, PAN_DOWN_POINT);
		assertFalse(undoManager.isRedoable());
		assertTrue(undoManager.isUndoable());
		assertThat(navigator.getXValueAxis().getLowerBound()).isEqualTo(chartXLowerBound);
		assertThat(navigator.getXValueAxis().getUpperBound()).isEqualTo(chartXUpperBound);
		assertThat(navigator.getYValueAxis().getLowerBound()).isNotEqualTo(chartYLowerBound);
		assertThat(navigator.getYValueAxis().getUpperBound()).isNotEqualTo(chartYUpperBound);
		navigatorResetChartMoveAndClick(robot, UNDO_POINT, false);
		assertTrue(undoManager.isRedoable());
		assertFalse(undoManager.isUndoable());
		assertThat(navigator.getXValueAxis().getLowerBound()).isEqualTo(chartXLowerBound);
		assertThat(navigator.getXValueAxis().getUpperBound()).isEqualTo(chartXUpperBound);
		assertThat(navigator.getYValueAxis().getLowerBound()).isEqualTo(chartYLowerBound);
		assertThat(navigator.getYValueAxis().getUpperBound()).isEqualTo(chartYUpperBound);
		navigatorResetChartMoveAndClick(robot, REDO_POINT, false);
		assertFalse(undoManager.isRedoable());
		assertTrue(undoManager.isUndoable());
		assertThat(navigator.getXValueAxis().getLowerBound()).isEqualTo(chartXLowerBound);
		assertThat(navigator.getXValueAxis().getUpperBound()).isEqualTo(chartXUpperBound);
		assertThat(navigator.getYValueAxis().getLowerBound()).isNotEqualTo(chartYLowerBound);
		assertThat(navigator.getYValueAxis().getUpperBound()).isNotEqualTo(chartYUpperBound);
		navigatorResetChartMoveAndClick(robot, UNDO_POINT, false);
		assertTrue(undoManager.isRedoable());
		assertFalse(undoManager.isUndoable());

		// Testing PAN UP...
		System.out.println("    - Testing PAN UP...");
		navigatorResetChartMoveAndClick(robot, PAN_UP_POINT);
		assertFalse(undoManager.isRedoable());
		assertTrue(undoManager.isUndoable());
		assertThat(navigator.getXValueAxis().getLowerBound()).isEqualTo(chartXLowerBound);
		assertThat(navigator.getXValueAxis().getUpperBound()).isEqualTo(chartXUpperBound);
		assertThat(navigator.getYValueAxis().getLowerBound()).isNotEqualTo(chartYLowerBound);
		assertThat(navigator.getYValueAxis().getUpperBound()).isNotEqualTo(chartYUpperBound);
		navigatorResetChartMoveAndClick(robot, UNDO_POINT, false);
		assertTrue(undoManager.isRedoable());
		assertFalse(undoManager.isUndoable());
		assertThat(navigator.getXValueAxis().getLowerBound()).isEqualTo(chartXLowerBound);
		assertThat(navigator.getXValueAxis().getUpperBound()).isEqualTo(chartXUpperBound);
		assertThat(navigator.getYValueAxis().getLowerBound()).isEqualTo(chartYLowerBound);
		assertThat(navigator.getYValueAxis().getUpperBound()).isEqualTo(chartYUpperBound);
		navigatorResetChartMoveAndClick(robot, REDO_POINT, false);
		assertFalse(undoManager.isRedoable());
		assertTrue(undoManager.isUndoable());
		assertThat(navigator.getXValueAxis().getLowerBound()).isEqualTo(chartXLowerBound);
		assertThat(navigator.getXValueAxis().getUpperBound()).isEqualTo(chartXUpperBound);
		assertThat(navigator.getYValueAxis().getLowerBound()).isNotEqualTo(chartYLowerBound);
		assertThat(navigator.getYValueAxis().getUpperBound()).isNotEqualTo(chartYUpperBound);
		navigatorResetChartMoveAndClick(robot, UNDO_POINT, false);
		assertTrue(undoManager.isRedoable());
		assertFalse(undoManager.isUndoable());

		// Testing PAN LEFT...
		System.out.println("    - Testing PAN LEFT...");
		navigatorResetChartMoveAndClick(robot, PAN_LEFT_POINT);
		assertFalse(undoManager.isRedoable());
		assertTrue(undoManager.isUndoable());
		assertThat(navigator.getXValueAxis().getLowerBound()).isNotEqualTo(chartXLowerBound);
		assertThat(navigator.getXValueAxis().getUpperBound()).isNotEqualTo(chartXUpperBound);
		assertThat(navigator.getYValueAxis().getLowerBound()).isEqualTo(chartYLowerBound);
		assertThat(navigator.getYValueAxis().getUpperBound()).isEqualTo(chartYUpperBound);
		navigatorResetChartMoveAndClick(robot, UNDO_POINT, false);
		assertTrue(undoManager.isRedoable());
		assertFalse(undoManager.isUndoable());
		assertThat(navigator.getXValueAxis().getLowerBound()).isEqualTo(chartXLowerBound);
		assertThat(navigator.getXValueAxis().getUpperBound()).isEqualTo(chartXUpperBound);
		assertThat(navigator.getYValueAxis().getLowerBound()).isEqualTo(chartYLowerBound);
		assertThat(navigator.getYValueAxis().getUpperBound()).isEqualTo(chartYUpperBound);
		navigatorResetChartMoveAndClick(robot, REDO_POINT, false);
		assertFalse(undoManager.isRedoable());
		assertTrue(undoManager.isUndoable());
		assertThat(navigator.getXValueAxis().getLowerBound()).isNotEqualTo(chartXLowerBound);
		assertThat(navigator.getXValueAxis().getUpperBound()).isNotEqualTo(chartXUpperBound);
		assertThat(navigator.getYValueAxis().getLowerBound()).isEqualTo(chartYLowerBound);
		assertThat(navigator.getYValueAxis().getUpperBound()).isEqualTo(chartYUpperBound);
		navigatorResetChartMoveAndClick(robot, UNDO_POINT, false);
		assertTrue(undoManager.isRedoable());
		assertFalse(undoManager.isUndoable());

		// Testing PAN RIGHT...
		System.out.println("    - Testing PAN RIGHT...");
		navigatorResetChartMoveAndClick(robot, PAN_RIGHT_POINT);
		assertFalse(undoManager.isRedoable());
		assertTrue(undoManager.isUndoable());
		assertThat(navigator.getXValueAxis().getLowerBound()).isNotEqualTo(chartXLowerBound);
		assertThat(navigator.getXValueAxis().getUpperBound()).isNotEqualTo(chartXUpperBound);
		assertThat(navigator.getYValueAxis().getLowerBound()).isEqualTo(chartYLowerBound);
		assertThat(navigator.getYValueAxis().getUpperBound()).isEqualTo(chartYUpperBound);
		navigatorResetChartMoveAndClick(robot, UNDO_POINT, false);
		assertTrue(undoManager.isRedoable());
		assertFalse(undoManager.isUndoable());
		assertThat(navigator.getXValueAxis().getLowerBound()).isEqualTo(chartXLowerBound);
		assertThat(navigator.getXValueAxis().getUpperBound()).isEqualTo(chartXUpperBound);
		assertThat(navigator.getYValueAxis().getLowerBound()).isEqualTo(chartYLowerBound);
		assertThat(navigator.getYValueAxis().getUpperBound()).isEqualTo(chartYUpperBound);
		navigatorResetChartMoveAndClick(robot, REDO_POINT, false);
		assertFalse(undoManager.isRedoable());
		assertTrue(undoManager.isUndoable());
		assertThat(navigator.getXValueAxis().getLowerBound()).isNotEqualTo(chartXLowerBound);
		assertThat(navigator.getXValueAxis().getUpperBound()).isNotEqualTo(chartXUpperBound);
		assertThat(navigator.getYValueAxis().getLowerBound()).isEqualTo(chartYLowerBound);
		assertThat(navigator.getYValueAxis().getUpperBound()).isEqualTo(chartYUpperBound);
		navigatorResetChartMoveAndClick(robot, UNDO_POINT, false);
		assertTrue(undoManager.isRedoable());
		assertFalse(undoManager.isUndoable());

		// Testing ZOOM IN...
		System.out.println("    - Testing ZOOM IN...");
		navigatorResetChartMoveAndClick(robot, ZOOM_IN_POINT);
		assertFalse(undoManager.isRedoable());
		assertTrue(undoManager.isUndoable());
		assertThat(navigator.getXValueAxis().getLowerBound()).isNotEqualTo(chartXLowerBound);
		assertThat(navigator.getXValueAxis().getUpperBound()).isNotEqualTo(chartXUpperBound);
		assertThat(navigator.getYValueAxis().getLowerBound()).isNotEqualTo(chartYLowerBound);
		assertThat(navigator.getYValueAxis().getUpperBound()).isNotEqualTo(chartYUpperBound);
		navigatorResetChartMoveAndClick(robot, UNDO_POINT, false);
		assertTrue(undoManager.isRedoable());
		assertFalse(undoManager.isUndoable());
		assertThat(navigator.getXValueAxis().getLowerBound()).isEqualTo(chartXLowerBound);
		assertThat(navigator.getXValueAxis().getUpperBound()).isEqualTo(chartXUpperBound);
		assertThat(navigator.getYValueAxis().getLowerBound()).isEqualTo(chartYLowerBound);
		assertThat(navigator.getYValueAxis().getUpperBound()).isEqualTo(chartYUpperBound);
		navigatorResetChartMoveAndClick(robot, REDO_POINT, false);
		assertFalse(undoManager.isRedoable());
		assertTrue(undoManager.isUndoable());
		assertThat(navigator.getXValueAxis().getLowerBound()).isNotEqualTo(chartXLowerBound);
		assertThat(navigator.getXValueAxis().getUpperBound()).isNotEqualTo(chartXUpperBound);
		assertThat(navigator.getYValueAxis().getLowerBound()).isNotEqualTo(chartYLowerBound);
		assertThat(navigator.getYValueAxis().getUpperBound()).isNotEqualTo(chartYUpperBound);
		navigatorResetChartMoveAndClick(robot, UNDO_POINT, false);
		assertTrue(undoManager.isRedoable());
		assertFalse(undoManager.isUndoable());

		// Testing ZOOM OUT...
		System.out.println("    - Testing ZOOM OUT...");
		navigatorResetChartMoveAndClick(robot, ZOOM_OUT_POINT);
		assertFalse(undoManager.isRedoable());
		assertTrue(undoManager.isUndoable());
		assertThat(navigator.getXValueAxis().getLowerBound()).isNotEqualTo(chartXLowerBound);
		assertThat(navigator.getXValueAxis().getUpperBound()).isNotEqualTo(chartXUpperBound);
		assertThat(navigator.getYValueAxis().getLowerBound()).isNotEqualTo(chartYLowerBound);
		assertThat(navigator.getYValueAxis().getUpperBound()).isNotEqualTo(chartYUpperBound);
		navigatorResetChartMoveAndClick(robot, UNDO_POINT, false);
		assertTrue(undoManager.isRedoable());
		assertFalse(undoManager.isUndoable());
		assertThat(navigator.getXValueAxis().getLowerBound()).isEqualTo(chartXLowerBound);
		assertThat(navigator.getXValueAxis().getUpperBound()).isEqualTo(chartXUpperBound);
		assertThat(navigator.getYValueAxis().getLowerBound()).isEqualTo(chartYLowerBound);
		assertThat(navigator.getYValueAxis().getUpperBound()).isEqualTo(chartYUpperBound);
		navigatorResetChartMoveAndClick(robot, REDO_POINT, false);
		assertFalse(undoManager.isRedoable());
		assertTrue(undoManager.isUndoable());
		assertThat(navigator.getXValueAxis().getLowerBound()).isNotEqualTo(chartXLowerBound);
		assertThat(navigator.getXValueAxis().getUpperBound()).isNotEqualTo(chartXUpperBound);
		assertThat(navigator.getYValueAxis().getLowerBound()).isNotEqualTo(chartYLowerBound);
		assertThat(navigator.getYValueAxis().getUpperBound()).isNotEqualTo(chartYUpperBound);
		navigatorResetChartMoveAndClick(robot, UNDO_POINT, false);
		assertTrue(undoManager.isRedoable());
		assertFalse(undoManager.isUndoable());

		// Testing ZOOM TO ONE...
		System.out.println("    - Testing ZOOM TO ONE...");
		navigatorResetChartMoveAndClick(robot, ZOOM_OUT_POINT);
		navigatorResetChartMoveAndClick(robot, ZOOM_TO_ONE_POINT, false);
		assertFalse(undoManager.isRedoable());
		assertTrue(undoManager.isUndoable());
		assertThat(navigator.getXValueAxis().getLowerBound()).isEqualTo(chartXLowerBound);
		assertThat(navigator.getXValueAxis().getUpperBound()).isEqualTo(chartXUpperBound);
		assertThat(navigator.getYValueAxis().getLowerBound()).isEqualTo(chartYLowerBound);
		assertThat(navigator.getYValueAxis().getUpperBound()).isEqualTo(chartYUpperBound);
		navigatorResetChartMoveAndClick(robot, UNDO_POINT, false);
		assertTrue(undoManager.isRedoable());
		assertTrue(undoManager.isUndoable());
		assertThat(navigator.getXValueAxis().getLowerBound()).isNotEqualTo(chartXLowerBound);
		assertThat(navigator.getXValueAxis().getUpperBound()).isNotEqualTo(chartXUpperBound);
		assertThat(navigator.getYValueAxis().getLowerBound()).isNotEqualTo(chartYLowerBound);
		assertThat(navigator.getYValueAxis().getUpperBound()).isNotEqualTo(chartYUpperBound);
		navigatorResetChartMoveAndClick(robot, REDO_POINT, false);
		assertFalse(undoManager.isRedoable());
		assertTrue(undoManager.isUndoable());
		assertThat(navigator.getXValueAxis().getLowerBound()).isEqualTo(chartXLowerBound);
		assertThat(navigator.getXValueAxis().getUpperBound()).isEqualTo(chartXUpperBound);
		assertThat(navigator.getYValueAxis().getLowerBound()).isEqualTo(chartYLowerBound);
		assertThat(navigator.getYValueAxis().getUpperBound()).isEqualTo(chartYUpperBound);
		navigatorResetChartMoveAndClick(robot, UNDO_POINT, false);
		assertTrue(undoManager.isRedoable());
		assertTrue(undoManager.isUndoable());
		assertThat(navigator.getXValueAxis().getLowerBound()).isNotEqualTo(chartXLowerBound);
		assertThat(navigator.getXValueAxis().getUpperBound()).isNotEqualTo(chartXUpperBound);
		assertThat(navigator.getYValueAxis().getLowerBound()).isNotEqualTo(chartYLowerBound);
		assertThat(navigator.getYValueAxis().getUpperBound()).isNotEqualTo(chartYUpperBound);
		navigatorResetChartMoveAndClick(robot, UNDO_POINT, false);
		assertTrue(undoManager.isRedoable());
		assertFalse(undoManager.isUndoable());
		assertThat(navigator.getXValueAxis().getLowerBound()).isEqualTo(chartXLowerBound);
		assertThat(navigator.getXValueAxis().getUpperBound()).isEqualTo(chartXUpperBound);
		assertThat(navigator.getYValueAxis().getLowerBound()).isEqualTo(chartYLowerBound);
		assertThat(navigator.getYValueAxis().getUpperBound()).isEqualTo(chartYUpperBound);
		assertTrue(undoManager.isRedoable());
		assertFalse(undoManager.isUndoable());

		//	Testing multiple operations and undo/redo
		System.out.println("    - Testing multiple operation and undo/redo...");
		navigatorResetChartMoveAndClick(robot, ZOOM_OUT_POINT);
		assertThat(undoManager.getAvailableRedoables()).isEqualTo(0);
		assertThat(undoManager.getAvailableUndoables()).isEqualTo(1);
		navigatorResetChartMoveAndClick(robot, PAN_DOWN_POINT, false);
		assertThat(undoManager.getAvailableRedoables()).isEqualTo(0);
		assertThat(undoManager.getAvailableUndoables()).isEqualTo(2);
		navigatorResetChartMoveAndClick(robot, PAN_DOWN_POINT, false);
		assertThat(undoManager.getAvailableRedoables()).isEqualTo(0);
		assertThat(undoManager.getAvailableUndoables()).isEqualTo(3);
		navigatorResetChartMoveAndClick(robot, PAN_LEFT_POINT, false);
		assertThat(undoManager.getAvailableRedoables()).isEqualTo(0);
		assertThat(undoManager.getAvailableUndoables()).isEqualTo(4);
		navigatorResetChartMoveAndClick(robot, ZOOM_OUT_POINT, false);
		assertThat(undoManager.getAvailableRedoables()).isEqualTo(0);
		assertThat(undoManager.getAvailableUndoables()).isEqualTo(5);
		navigatorResetChartMoveAndClick(robot, PAN_DOWN_POINT, false);
		assertThat(undoManager.getAvailableRedoables()).isEqualTo(0);
		assertThat(undoManager.getAvailableUndoables()).isEqualTo(6);
		navigatorResetChartMoveAndClick(robot, PAN_LEFT_POINT, false);
		assertThat(undoManager.getAvailableRedoables()).isEqualTo(0);
		assertThat(undoManager.getAvailableUndoables()).isEqualTo(7);
		navigatorResetChartMoveAndClick(robot, UNDO_POINT, false);
		assertThat(undoManager.getAvailableRedoables()).isEqualTo(1);
		assertThat(undoManager.getAvailableUndoables()).isEqualTo(6);
		navigatorResetChartMoveAndClick(robot, UNDO_POINT, false);
		assertThat(undoManager.getAvailableRedoables()).isEqualTo(2);
		assertThat(undoManager.getAvailableUndoables()).isEqualTo(5);
		navigatorResetChartMoveAndClick(robot, UNDO_POINT, false);
		assertThat(undoManager.getAvailableRedoables()).isEqualTo(3);
		assertThat(undoManager.getAvailableUndoables()).isEqualTo(4);
		navigatorResetChartMoveAndClick(robot, REDO_POINT, false);
		assertThat(undoManager.getAvailableRedoables()).isEqualTo(2);
		assertThat(undoManager.getAvailableUndoables()).isEqualTo(5);
		navigatorResetChartMoveAndClick(robot, PAN_DOWN_POINT, false);
		assertThat(undoManager.getAvailableRedoables()).isEqualTo(0);
		assertThat(undoManager.getAvailableUndoables()).isEqualTo(6);

	}

	/**
	 * Test UNDO and REDO on Panner drag operations.
	 */
	@Test
	public void testPannerDrag() {

		System.out.println("  Testing ''ChartUndoManager'' on Panner (drag)...");

		FxRobot robot = new FxRobot();
		ChartUndoManager undoManager = ChartUndoManager.get(panner.getChart());

		//	Get chart's reference bounds...
		chartXAutoRange  = panner.getXValueAxis().isAutoRanging();
		chartXLowerBound = panner.getXValueAxis().getLowerBound();
		chartXUpperBound = panner.getXValueAxis().getUpperBound();
		chartYAutoRange  = panner.getYValueAxis().isAutoRanging();
		chartYLowerBound = panner.getYValueAxis().getLowerBound();
		chartYUpperBound = panner.getYValueAxis().getUpperBound();

		//	Activate the tool...
		robot.moveTo(chart);
		robot.clickOn(PRIMARY);

		//	Assert initial conditions...
		assertFalse(undoManager.isRedoable());
		assertFalse(undoManager.isUndoable());

		// Testing mouse DRAG DOWN...
		System.out.println("    - Testing mouse mouse DRAG DOWN...");
		mouseResetChartAndDrag(robot, new Point2D(0, 100));
		assertFalse(undoManager.isRedoable());
		assertTrue(undoManager.isUndoable());
		assertThat(panner.getYValueAxis().getLowerBound()).isNotEqualTo(chartYLowerBound);
		assertThat(panner.getYValueAxis().getUpperBound()).isNotEqualTo(chartYUpperBound);
		undoManager.undo(panner);
		assertTrue(undoManager.isRedoable());
		assertFalse(undoManager.isUndoable());
		assertThat(panner.getXValueAxis().getLowerBound()).isEqualTo(chartXLowerBound);
		assertThat(panner.getXValueAxis().getUpperBound()).isEqualTo(chartXUpperBound);
		assertThat(panner.getYValueAxis().getLowerBound()).isEqualTo(chartYLowerBound);
		assertThat(panner.getYValueAxis().getUpperBound()).isEqualTo(chartYUpperBound);
		undoManager.redo(panner);
		assertFalse(undoManager.isRedoable());
		assertTrue(undoManager.isUndoable());
		assertThat(panner.getYValueAxis().getLowerBound()).isNotEqualTo(chartYLowerBound);
		assertThat(panner.getYValueAxis().getUpperBound()).isNotEqualTo(chartYUpperBound);
		undoManager.undo(panner);
		assertTrue(undoManager.isRedoable());
		assertFalse(undoManager.isUndoable());

		// Testing mouse DRAG UP...
		System.out.println("    - Testing mouse DRAG UP...");
		mouseResetChartAndDrag(robot, new Point2D(0, -100));
		assertFalse(undoManager.isRedoable());
		assertTrue(undoManager.isUndoable());
		assertThat(panner.getYValueAxis().getLowerBound()).isNotEqualTo(chartYLowerBound);
		assertThat(panner.getYValueAxis().getUpperBound()).isNotEqualTo(chartYUpperBound);
		undoManager.undo(panner);
		assertTrue(undoManager.isRedoable());
		assertFalse(undoManager.isUndoable());
		assertThat(panner.getXValueAxis().getLowerBound()).isEqualTo(chartXLowerBound);
		assertThat(panner.getXValueAxis().getUpperBound()).isEqualTo(chartXUpperBound);
		assertThat(panner.getYValueAxis().getLowerBound()).isEqualTo(chartYLowerBound);
		assertThat(panner.getYValueAxis().getUpperBound()).isEqualTo(chartYUpperBound);
		undoManager.redo(panner);
		assertFalse(undoManager.isRedoable());
		assertTrue(undoManager.isUndoable());
		assertThat(panner.getYValueAxis().getLowerBound()).isNotEqualTo(chartYLowerBound);
		assertThat(panner.getYValueAxis().getUpperBound()).isNotEqualTo(chartYUpperBound);
		undoManager.undo(panner);
		assertTrue(undoManager.isRedoable());
		assertFalse(undoManager.isUndoable());

		// Testing mouse DRAG LEFT...
		System.out.println("    - Testing mouse DRAG LEFT...");
		mouseResetChartAndDrag(robot, new Point2D(-100, 0));
		assertFalse(undoManager.isRedoable());
		assertTrue(undoManager.isUndoable());
		assertThat(panner.getXValueAxis().getLowerBound()).isNotEqualTo(chartXLowerBound);
		assertThat(panner.getXValueAxis().getUpperBound()).isNotEqualTo(chartXUpperBound);
		undoManager.undo(panner);
		assertTrue(undoManager.isRedoable());
		assertFalse(undoManager.isUndoable());
		assertThat(panner.getXValueAxis().getLowerBound()).isEqualTo(chartXLowerBound);
		assertThat(panner.getXValueAxis().getUpperBound()).isEqualTo(chartXUpperBound);
		assertThat(panner.getYValueAxis().getLowerBound()).isEqualTo(chartYLowerBound);
		assertThat(panner.getYValueAxis().getUpperBound()).isEqualTo(chartYUpperBound);
		undoManager.redo(panner);
		assertFalse(undoManager.isRedoable());
		assertTrue(undoManager.isUndoable());
		assertThat(panner.getXValueAxis().getLowerBound()).isNotEqualTo(chartXLowerBound);
		assertThat(panner.getXValueAxis().getUpperBound()).isNotEqualTo(chartXUpperBound);
		undoManager.undo(panner);
		assertTrue(undoManager.isRedoable());
		assertFalse(undoManager.isUndoable());

		// Testing mouse DRAG RIGHT...
		System.out.println("    - Testing mouse PAN RIGHT...");
		mouseResetChartAndDrag(robot, new Point2D(100, 0));
		assertFalse(undoManager.isRedoable());
		assertTrue(undoManager.isUndoable());
		assertThat(panner.getXValueAxis().getLowerBound()).isNotEqualTo(chartXLowerBound);
		assertThat(panner.getXValueAxis().getUpperBound()).isNotEqualTo(chartXUpperBound);
		undoManager.undo(panner);
		assertTrue(undoManager.isRedoable());
		assertFalse(undoManager.isUndoable());
		assertThat(panner.getXValueAxis().getLowerBound()).isEqualTo(chartXLowerBound);
		assertThat(panner.getXValueAxis().getUpperBound()).isEqualTo(chartXUpperBound);
		assertThat(panner.getYValueAxis().getLowerBound()).isEqualTo(chartYLowerBound);
		assertThat(panner.getYValueAxis().getUpperBound()).isEqualTo(chartYUpperBound);
		undoManager.redo(panner);
		assertFalse(undoManager.isRedoable());
		assertTrue(undoManager.isUndoable());
		assertThat(panner.getXValueAxis().getLowerBound()).isNotEqualTo(chartXLowerBound);
		assertThat(panner.getXValueAxis().getUpperBound()).isNotEqualTo(chartXUpperBound);
		undoManager.undo(panner);
		assertTrue(undoManager.isRedoable());
		assertFalse(undoManager.isUndoable());

		// Testing mouse DRAG DOWN-LEFT...
		System.out.println("    - Testing mouse mouse DRAG DOWN-LEFT...");
		mouseResetChartAndDrag(robot, new Point2D(-100, 100));
		assertFalse(undoManager.isRedoable());
		assertTrue(undoManager.isUndoable());
		assertThat(panner.getXValueAxis().getLowerBound()).isNotEqualTo(chartXLowerBound);
		assertThat(panner.getXValueAxis().getUpperBound()).isNotEqualTo(chartXUpperBound);
		assertThat(panner.getYValueAxis().getLowerBound()).isNotEqualTo(chartYLowerBound);
		assertThat(panner.getYValueAxis().getUpperBound()).isNotEqualTo(chartYUpperBound);
		undoManager.undo(panner);
		assertTrue(undoManager.isRedoable());
		assertFalse(undoManager.isUndoable());
		assertThat(panner.getXValueAxis().getLowerBound()).isEqualTo(chartXLowerBound);
		assertThat(panner.getXValueAxis().getUpperBound()).isEqualTo(chartXUpperBound);
		assertThat(panner.getYValueAxis().getLowerBound()).isEqualTo(chartYLowerBound);
		assertThat(panner.getYValueAxis().getUpperBound()).isEqualTo(chartYUpperBound);
		undoManager.redo(panner);
		assertFalse(undoManager.isRedoable());
		assertTrue(undoManager.isUndoable());
		assertThat(panner.getXValueAxis().getLowerBound()).isNotEqualTo(chartXLowerBound);
		assertThat(panner.getXValueAxis().getUpperBound()).isNotEqualTo(chartXUpperBound);
		assertThat(panner.getYValueAxis().getLowerBound()).isNotEqualTo(chartYLowerBound);
		assertThat(panner.getYValueAxis().getUpperBound()).isNotEqualTo(chartYUpperBound);
		undoManager.undo(panner);
		assertTrue(undoManager.isRedoable());
		assertFalse(undoManager.isUndoable());

		// Testing mouse DRAG DOWN-RIGHT...
		System.out.println("    - Testing mouse mouse DRAG DOWN-RIGHT...");
		mouseResetChartAndDrag(robot, new Point2D(100, 100));
		assertFalse(undoManager.isRedoable());
		assertTrue(undoManager.isUndoable());
		assertThat(panner.getXValueAxis().getLowerBound()).isNotEqualTo(chartXLowerBound);
		assertThat(panner.getXValueAxis().getUpperBound()).isNotEqualTo(chartXUpperBound);
		assertThat(panner.getYValueAxis().getLowerBound()).isNotEqualTo(chartYLowerBound);
		assertThat(panner.getYValueAxis().getUpperBound()).isNotEqualTo(chartYUpperBound);
		undoManager.undo(panner);
		assertTrue(undoManager.isRedoable());
		assertFalse(undoManager.isUndoable());
		assertThat(panner.getXValueAxis().getLowerBound()).isEqualTo(chartXLowerBound);
		assertThat(panner.getXValueAxis().getUpperBound()).isEqualTo(chartXUpperBound);
		assertThat(panner.getYValueAxis().getLowerBound()).isEqualTo(chartYLowerBound);
		assertThat(panner.getYValueAxis().getUpperBound()).isEqualTo(chartYUpperBound);
		undoManager.redo(panner);
		assertFalse(undoManager.isRedoable());
		assertTrue(undoManager.isUndoable());
		assertThat(panner.getXValueAxis().getLowerBound()).isNotEqualTo(chartXLowerBound);
		assertThat(panner.getXValueAxis().getUpperBound()).isNotEqualTo(chartXUpperBound);
		assertThat(panner.getYValueAxis().getLowerBound()).isNotEqualTo(chartYLowerBound);
		assertThat(panner.getYValueAxis().getUpperBound()).isNotEqualTo(chartYUpperBound);
		undoManager.undo(panner);
		assertTrue(undoManager.isRedoable());
		assertFalse(undoManager.isUndoable());

		// Testing mouse DRAG UP-LEFT...
		System.out.println("    - Testing mouse DRAG UP-LEFT...");
		mouseResetChartAndDrag(robot, new Point2D(-100, -100));
		assertFalse(undoManager.isRedoable());
		assertTrue(undoManager.isUndoable());
		assertThat(panner.getXValueAxis().getLowerBound()).isNotEqualTo(chartXLowerBound);
		assertThat(panner.getXValueAxis().getUpperBound()).isNotEqualTo(chartXUpperBound);
		assertThat(panner.getYValueAxis().getLowerBound()).isNotEqualTo(chartYLowerBound);
		assertThat(panner.getYValueAxis().getUpperBound()).isNotEqualTo(chartYUpperBound);
		undoManager.undo(panner);
		assertTrue(undoManager.isRedoable());
		assertFalse(undoManager.isUndoable());
		assertThat(panner.getXValueAxis().getLowerBound()).isEqualTo(chartXLowerBound);
		assertThat(panner.getXValueAxis().getUpperBound()).isEqualTo(chartXUpperBound);
		assertThat(panner.getYValueAxis().getLowerBound()).isEqualTo(chartYLowerBound);
		assertThat(panner.getYValueAxis().getUpperBound()).isEqualTo(chartYUpperBound);
		undoManager.redo(panner);
		assertFalse(undoManager.isRedoable());
		assertTrue(undoManager.isUndoable());
		assertThat(panner.getXValueAxis().getLowerBound()).isNotEqualTo(chartXLowerBound);
		assertThat(panner.getXValueAxis().getUpperBound()).isNotEqualTo(chartXUpperBound);
		assertThat(panner.getYValueAxis().getLowerBound()).isNotEqualTo(chartYLowerBound);
		assertThat(panner.getYValueAxis().getUpperBound()).isNotEqualTo(chartYUpperBound);
		undoManager.undo(panner);
		assertTrue(undoManager.isRedoable());
		assertFalse(undoManager.isUndoable());

		// Testing mouse DRAG UP-RIGHT...
		System.out.println("    - Testing mouse DRAG UP-RIGHT...");
		mouseResetChartAndDrag(robot, new Point2D(100, -100));
		assertFalse(undoManager.isRedoable());
		assertTrue(undoManager.isUndoable());
		assertThat(panner.getXValueAxis().getLowerBound()).isNotEqualTo(chartXLowerBound);
		assertThat(panner.getXValueAxis().getUpperBound()).isNotEqualTo(chartXUpperBound);
		assertThat(panner.getYValueAxis().getLowerBound()).isNotEqualTo(chartYLowerBound);
		assertThat(panner.getYValueAxis().getUpperBound()).isNotEqualTo(chartYUpperBound);
		undoManager.undo(panner);
		assertTrue(undoManager.isRedoable());
		assertFalse(undoManager.isUndoable());
		assertThat(panner.getXValueAxis().getLowerBound()).isEqualTo(chartXLowerBound);
		assertThat(panner.getXValueAxis().getUpperBound()).isEqualTo(chartXUpperBound);
		assertThat(panner.getYValueAxis().getLowerBound()).isEqualTo(chartYLowerBound);
		assertThat(panner.getYValueAxis().getUpperBound()).isEqualTo(chartYUpperBound);
		undoManager.redo(panner);
		assertFalse(undoManager.isRedoable());
		assertTrue(undoManager.isUndoable());
		assertThat(panner.getXValueAxis().getLowerBound()).isNotEqualTo(chartXLowerBound);
		assertThat(panner.getXValueAxis().getUpperBound()).isNotEqualTo(chartXUpperBound);
		assertThat(panner.getYValueAxis().getLowerBound()).isNotEqualTo(chartYLowerBound);
		assertThat(panner.getYValueAxis().getUpperBound()).isNotEqualTo(chartYUpperBound);
		undoManager.undo(panner);
		assertTrue(undoManager.isRedoable());
		assertFalse(undoManager.isUndoable());

	}

	/**
	 * Test UNDO and REDO on Panner scroll operations.
	 */
	@Test
	public void testPannerScroll() {

		System.out.println("  Testing ''ChartUndoManager'' on Panner (scroll)...");

		FxRobot robot = new FxRobot();
		ChartUndoManager undoManager = ChartUndoManager.get(panner.getChart());

		//	Get chart's reference bounds...
		chartXAutoRange  = panner.getXValueAxis().isAutoRanging();
		chartXLowerBound = panner.getXValueAxis().getLowerBound();
		chartXUpperBound = panner.getXValueAxis().getUpperBound();
		chartYAutoRange  = panner.getYValueAxis().isAutoRanging();
		chartYLowerBound = panner.getYValueAxis().getLowerBound();
		chartYUpperBound = panner.getYValueAxis().getUpperBound();

		//	Activate the tool...
		robot.moveTo(chart);
		robot.clickOn(PRIMARY);

		//	Assert initial conditions...
		assertFalse(undoManager.isRedoable());
		assertFalse(undoManager.isUndoable());

		// Testing SCROLL DOWN...
		System.out.println("    - Testing SCROLL DOWN...");
		mouseResetChartAndScroll(robot, DOWN, 1);
		assertFalse(undoManager.isRedoable());
		assertTrue(undoManager.isUndoable());
		assertThat(panner.getXValueAxis().getLowerBound()).isEqualTo(chartXLowerBound);
		assertThat(panner.getXValueAxis().getUpperBound()).isEqualTo(chartXUpperBound);
		assertThat(panner.getYValueAxis().getLowerBound()).isNotEqualTo(chartYLowerBound);
		assertThat(panner.getYValueAxis().getUpperBound()).isNotEqualTo(chartYUpperBound);
		undoManager.undo(panner);
		assertTrue(undoManager.isRedoable());
		assertFalse(undoManager.isUndoable());
		assertThat(panner.getXValueAxis().getLowerBound()).isEqualTo(chartXLowerBound);
		assertThat(panner.getXValueAxis().getUpperBound()).isEqualTo(chartXUpperBound);
		assertThat(panner.getYValueAxis().getLowerBound()).isEqualTo(chartYLowerBound);
		assertThat(panner.getYValueAxis().getUpperBound()).isEqualTo(chartYUpperBound);
		undoManager.redo(panner);
		assertFalse(undoManager.isRedoable());
		assertTrue(undoManager.isUndoable());
		assertThat(panner.getXValueAxis().getLowerBound()).isEqualTo(chartXLowerBound);
		assertThat(panner.getXValueAxis().getUpperBound()).isEqualTo(chartXUpperBound);
		assertThat(panner.getYValueAxis().getLowerBound()).isNotEqualTo(chartYLowerBound);
		assertThat(panner.getYValueAxis().getUpperBound()).isNotEqualTo(chartYUpperBound);
		undoManager.undo(panner);
		assertTrue(undoManager.isRedoable());
		assertFalse(undoManager.isUndoable());

		// Testing SCROLL UP...
		System.out.println("    - Testing SCROLL UP...");
		mouseResetChartAndScroll(robot, UP, 1);
		assertFalse(undoManager.isRedoable());
		assertTrue(undoManager.isUndoable());
		assertThat(panner.getXValueAxis().getLowerBound()).isEqualTo(chartXLowerBound);
		assertThat(panner.getXValueAxis().getUpperBound()).isEqualTo(chartXUpperBound);
		assertThat(panner.getYValueAxis().getLowerBound()).isNotEqualTo(chartYLowerBound);
		assertThat(panner.getYValueAxis().getUpperBound()).isNotEqualTo(chartYUpperBound);
		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) UNDO_ACCELERATOR, false);
		assertTrue(undoManager.isRedoable());
		assertFalse(undoManager.isUndoable());
		assertThat(panner.getXValueAxis().getLowerBound()).isEqualTo(chartXLowerBound);
		assertThat(panner.getXValueAxis().getUpperBound()).isEqualTo(chartXUpperBound);
		assertThat(panner.getYValueAxis().getLowerBound()).isEqualTo(chartYLowerBound);
		assertThat(panner.getYValueAxis().getUpperBound()).isEqualTo(chartYUpperBound);
		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) REDO_ACCELERATOR, false);
		assertFalse(undoManager.isRedoable());
		assertTrue(undoManager.isUndoable());
		assertThat(panner.getXValueAxis().getLowerBound()).isEqualTo(chartXLowerBound);
		assertThat(panner.getXValueAxis().getUpperBound()).isEqualTo(chartXUpperBound);
		assertThat(panner.getYValueAxis().getLowerBound()).isNotEqualTo(chartYLowerBound);
		assertThat(panner.getYValueAxis().getUpperBound()).isNotEqualTo(chartYUpperBound);
		acceleratorsResetChartAndPress(robot, (KeyCodeCombination) UNDO_ACCELERATOR, false);
		assertTrue(undoManager.isRedoable());
		assertFalse(undoManager.isUndoable());

	}

	private void acceleratorsResetChartAndPress ( FxRobot robot, KeyCodeCombination combination ) {
		acceleratorsResetChartAndPress(robot, combination, true);
	}

	private void acceleratorsResetChartAndPress ( FxRobot robot, KeyCodeCombination combination, boolean reset ) {

		if ( reset ) {
			keyboardAccelerators.getXValueAxis().setAutoRanging(chartXAutoRange);
			keyboardAccelerators.getYValueAxis().setAutoRanging(chartYAutoRange);
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

	private void mouseResetChartAndDrag ( FxRobot robot, Point2D offset ) {
		mouseResetChartAndDrag(robot, offset, true);
	}

	private void mouseResetChartAndDrag ( FxRobot robot, Point2D offset, boolean reset ) {

		if ( reset ) {
			panner.getXValueAxis().setAutoRanging(chartXAutoRange);
			panner.getYValueAxis().setAutoRanging(chartYAutoRange);
			panner.getXValueAxis().setLowerBound(chartXLowerBound);
			panner.getXValueAxis().setUpperBound(chartXUpperBound);
			panner.getYValueAxis().setLowerBound(chartYLowerBound);
			panner.getYValueAxis().setUpperBound(chartYUpperBound);
		}

		robot.moveTo(chart, CENTER, Point2D.ZERO, DEFAULT);
		robot.press(PRIMARY);
		robot.moveTo(chart, CENTER, offset, DEFAULT);

		//	Necessary to give the robot the time to perform the movement.
		ThreadUtils.sleep(200);
		robot.release(PRIMARY);

	}

	private void mouseResetChartAndScroll ( FxRobot robot, VerticalDirection direction, int amount ) {
		mouseResetChartAndScroll(robot, direction, amount, true);
	}

	private void mouseResetChartAndScroll ( FxRobot robot, VerticalDirection direction, int amount, boolean reset ) {

		if ( reset ) {
			panner.getXValueAxis().setAutoRanging(chartXAutoRange);
			panner.getYValueAxis().setAutoRanging(chartYAutoRange);
			panner.getXValueAxis().setLowerBound(chartXLowerBound);
			panner.getXValueAxis().setUpperBound(chartXUpperBound);
			panner.getYValueAxis().setLowerBound(chartYLowerBound);
			panner.getYValueAxis().setUpperBound(chartYUpperBound);
		}

		robot.moveTo(chart, CENTER, Point2D.ZERO, DEFAULT);
		robot.scroll(amount, direction);

	}

	private void navigatorResetChartMoveAndClick ( FxRobot robot, Point2D offset ) {
		navigatorResetChartMoveAndClick(robot, offset, true);
	}

	private void navigatorResetChartMoveAndClick ( FxRobot robot, Point2D offset, boolean reset ) {

		if ( reset ) {
			navigator.getXValueAxis().setAutoRanging(chartXAutoRange);
			navigator.getYValueAxis().setAutoRanging(chartYAutoRange);
			navigator.getXValueAxis().setLowerBound(chartXLowerBound);
			navigator.getXValueAxis().setUpperBound(chartXUpperBound);
			navigator.getYValueAxis().setLowerBound(chartYLowerBound);
			navigator.getYValueAxis().setUpperBound(chartYUpperBound);
		}

		robot.moveTo(chart, CENTER, offset, DEFAULT);
		robot.clickOn(PRIMARY);

	}

}
