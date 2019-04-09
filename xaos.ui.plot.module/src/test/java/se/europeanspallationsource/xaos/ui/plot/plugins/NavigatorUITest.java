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
package se.europeanspallationsource.xaos.ui.plot.plugins;


import chart.LineChartFX;
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

import static javafx.geometry.Pos.CENTER;
import static javafx.scene.input.KeyCode.ALT;
import static javafx.scene.input.MouseButton.PRIMARY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testfx.robot.Motion.DEFAULT;



/**
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( { "UseOfSystemOutOrSystemErr", "ClassWithoutLogger" } )
public class NavigatorUITest extends ApplicationTest {

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
		System.out.println("---- NavigatorUITest -------------------------------------------");
	}

	private LineChartFX<Number, Number> chart;
	private double chartHeight;
	private double chartWidth;
	private double chartXLowerBound;
	private double chartXUpperBound;
	private double chartYLowerBound;
	private double chartYUpperBound;
	private Navigator navigatorTool;

	@Override
	@SuppressWarnings( "NestedAssignment" )
	public void start( Stage stage ) throws IOException {

		final NumberAxis xAxis = new NumberAxis();
		final NumberAxis yAxis = new NumberAxis();

		xAxis.setAnimated(false);
		yAxis.setAnimated(false);

		chart = new LineChartFX<>(xAxis, yAxis);

		chart.setTitle("NavigatorUITest");
		chart.setAnimated(false);
		chart.getChartPlugins().addAll(
			navigatorTool = new Navigator()
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

		System.out.println("  Testing ''Navigator''...");

		FxRobot robot = new FxRobot();

		//	Get chart's reference bounds...
		chartXLowerBound = navigatorTool.getXValueAxis().getLowerBound();
		chartXUpperBound = navigatorTool.getXValueAxis().getUpperBound();
		chartWidth       = chartXUpperBound - chartXLowerBound;
		chartYLowerBound = navigatorTool.getYValueAxis().getLowerBound();
		chartYUpperBound = navigatorTool.getYValueAxis().getUpperBound();
		chartHeight      = chartYUpperBound - chartYLowerBound;

		//	Activate the tool...
		robot.moveTo(chart);
		robot.type(ALT);

		//	--------------------------------------------------------------------
		//	Test each popup button...
		//	--------------------------------------------------------------------

		// Testing PAN DOWN...
		System.out.println("    - Testing PAN DOWN...");
		navigatorResetChartMoveAndClick(robot, PAN_DOWN_POINT);
		assertThat(navigatorTool.getYValueAxis().getLowerBound()).isLessThan(chartYLowerBound);
		assertThat(navigatorTool.getYValueAxis().getUpperBound()).isLessThan(chartYUpperBound);
		assertThat(navigatorTool.getYValueAxis().getUpperBound() - navigatorTool.getYValueAxis().getLowerBound()).isEqualTo(chartHeight, Offset.offset(0.01));

		// Testing PAN UP...
		System.out.println("    - Testing PAN UP...");
		navigatorResetChartMoveAndClick(robot, PAN_UP_POINT);
		assertThat(navigatorTool.getYValueAxis().getLowerBound()).isGreaterThan(chartYLowerBound);
		assertThat(navigatorTool.getYValueAxis().getUpperBound()).isGreaterThan(chartYUpperBound);
		assertThat(navigatorTool.getYValueAxis().getUpperBound() - navigatorTool.getYValueAxis().getLowerBound()).isEqualTo(chartHeight, Offset.offset(0.01));

		// Testing PAN LEFT...
		System.out.println("    - Testing PAN LEFT...");
		navigatorResetChartMoveAndClick(robot, PAN_LEFT_POINT);
		assertThat(navigatorTool.getXValueAxis().getLowerBound()).isLessThan(chartXLowerBound);
		assertThat(navigatorTool.getXValueAxis().getUpperBound()).isLessThan(chartXUpperBound);
		assertThat(navigatorTool.getXValueAxis().getUpperBound() - navigatorTool.getXValueAxis().getLowerBound()).isEqualTo(chartWidth, Offset.offset(0.01));

		// Testing PAN RIGHT...
		System.out.println("    - Testing PAN RIGHT...");
		navigatorResetChartMoveAndClick(robot, PAN_RIGHT_POINT);
		assertThat(navigatorTool.getXValueAxis().getLowerBound()).isGreaterThan(chartXLowerBound);
		assertThat(navigatorTool.getXValueAxis().getUpperBound()).isGreaterThan(chartXUpperBound);
		assertThat(navigatorTool.getXValueAxis().getUpperBound() - navigatorTool.getXValueAxis().getLowerBound()).isEqualTo(chartWidth, Offset.offset(0.01));

		// Testing ZOOM IN...
		System.out.println("    - Testing ZOOM IN...");
		navigatorResetChartMoveAndClick(robot, ZOOM_IN_POINT);
		assertThat(navigatorTool.getXValueAxis().getLowerBound()).isGreaterThan(chartXLowerBound);
		assertThat(navigatorTool.getXValueAxis().getUpperBound()).isLessThan(chartXUpperBound);
		assertThat(navigatorTool.getXValueAxis().getUpperBound() - navigatorTool.getXValueAxis().getLowerBound()).isLessThan(chartWidth);
		assertThat(navigatorTool.getYValueAxis().getLowerBound()).isGreaterThan(chartYLowerBound);
		assertThat(navigatorTool.getYValueAxis().getUpperBound()).isLessThan(chartYUpperBound);
		assertThat(navigatorTool.getYValueAxis().getUpperBound() - navigatorTool.getYValueAxis().getLowerBound()).isLessThan(chartHeight);

		// Testing ZOOM OUT...
		System.out.println("    - Testing ZOOM OUT...");
		navigatorResetChartMoveAndClick(robot, ZOOM_OUT_POINT);
		assertThat(navigatorTool.getXValueAxis().getLowerBound()).isLessThan(chartXLowerBound);
		assertThat(navigatorTool.getXValueAxis().getUpperBound()).isGreaterThan(chartXUpperBound);
		assertThat(navigatorTool.getXValueAxis().getUpperBound() - navigatorTool.getXValueAxis().getLowerBound()).isGreaterThan(chartWidth);
		assertThat(navigatorTool.getYValueAxis().getLowerBound()).isLessThan(chartYLowerBound);
		assertThat(navigatorTool.getYValueAxis().getUpperBound()).isGreaterThan(chartYUpperBound);
		assertThat(navigatorTool.getYValueAxis().getUpperBound() - navigatorTool.getYValueAxis().getLowerBound()).isGreaterThan(chartHeight);

		// Testing ZOOM TO ONE...
		System.out.println("    - Testing ZOOM TO ONE...");
		navigatorResetChartMoveAndClick(robot, ZOOM_IN_POINT);
		navigatorResetChartMoveAndClick(robot, ZOOM_TO_ONE_POINT, false);
		assertThat(navigatorTool.getXValueAxis().getLowerBound()).isEqualTo(chartXLowerBound, Offset.offset(0.01));
		assertThat(navigatorTool.getXValueAxis().getUpperBound()).isEqualTo(chartXUpperBound, Offset.offset(0.01));
		assertThat(navigatorTool.getYValueAxis().getLowerBound()).isEqualTo(chartYLowerBound, Offset.offset(0.01));
		assertThat(navigatorTool.getYValueAxis().getUpperBound()).isEqualTo(chartYUpperBound, Offset.offset(0.01));

		// Testing UNDO...
		System.out.println("    - Testing UNDO...");
		navigatorResetChartMoveAndClick(robot, ZOOM_IN_POINT);
		navigatorResetChartMoveAndClick(robot, UNDO_POINT, false);
		assertThat(navigatorTool.getXValueAxis().getLowerBound()).isEqualTo(chartXLowerBound, Offset.offset(0.01));
		assertThat(navigatorTool.getXValueAxis().getUpperBound()).isEqualTo(chartXUpperBound, Offset.offset(0.01));
		assertThat(navigatorTool.getYValueAxis().getLowerBound()).isEqualTo(chartYLowerBound, Offset.offset(0.01));
		assertThat(navigatorTool.getYValueAxis().getUpperBound()).isEqualTo(chartYUpperBound, Offset.offset(0.01));

		// Testing REDO...
		System.out.println("    - Testing REDO...");
		navigatorResetChartMoveAndClick(robot, REDO_POINT, false);
		//	Now it should be after Zoom In done
		assertThat(navigatorTool.getXValueAxis().getLowerBound()).isGreaterThan(chartXLowerBound);
		assertThat(navigatorTool.getXValueAxis().getUpperBound()).isLessThan(chartXUpperBound);
		assertThat(navigatorTool.getXValueAxis().getUpperBound() - navigatorTool.getXValueAxis().getLowerBound()).isLessThan(chartWidth);
		assertThat(navigatorTool.getYValueAxis().getLowerBound()).isGreaterThan(chartYLowerBound);
		assertThat(navigatorTool.getYValueAxis().getUpperBound()).isLessThan(chartYUpperBound);
		assertThat(navigatorTool.getYValueAxis().getUpperBound() - navigatorTool.getYValueAxis().getLowerBound()).isLessThan(chartHeight);

	}

	private void navigatorResetChartMoveAndClick ( FxRobot robot, Point2D offset ) {
		navigatorResetChartMoveAndClick(robot, offset, true);
	}

	private void navigatorResetChartMoveAndClick ( FxRobot robot, Point2D offset, boolean reset ) {

		if ( reset ) {
			navigatorTool.getXValueAxis().setLowerBound(chartXLowerBound);
			navigatorTool.getXValueAxis().setUpperBound(chartXUpperBound);
			navigatorTool.getYValueAxis().setLowerBound(chartYLowerBound);
			navigatorTool.getYValueAxis().setUpperBound(chartYUpperBound);
		}

		robot.moveTo(chart, CENTER, offset, DEFAULT);
		robot.clickOn(PRIMARY);

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
