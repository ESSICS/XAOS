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
public class CursorToolUITest extends ApplicationTest {

	private static final int POINTS_COUNT = 20;
	private static final Random RANDOM = new Random(System.currentTimeMillis());

	@BeforeClass
	public static void setUpClass() {
		System.out.println("---- CursorToolUITest ------------------------------------------");
	}

	private LineChartFX<Number, Number> chart;
	private double height;
	private CursorTool tool;
	private double width;
	private double xLowerBound;
	private double xUpperBound;
	private double yLowerBound;
	private double yUpperBound;

	@Override
	public void start( Stage stage ) throws IOException {

		final NumberAxis xAxis = new NumberAxis();
		final NumberAxis yAxis = new NumberAxis();

		xAxis.setAnimated(false);
		yAxis.setAnimated(false);

		tool = new CursorTool();
		chart = new LineChartFX<Number, Number>(xAxis, yAxis);

		chart.setTitle("CursorToolUITest");
		chart.setAnimated(false);
		chart.getChartPlugins().addAll(tool);

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
	public void test() throws InterruptedException {

		System.out.println("  Testing ''CursorTool''...");

		FxRobot robot = new FxRobot();

		//	Get chart's reference bounds...
		xLowerBound = tool.getXValueAxis().getLowerBound();
		xUpperBound = tool.getXValueAxis().getUpperBound();
		yLowerBound = tool.getYValueAxis().getLowerBound();
		yUpperBound = tool.getYValueAxis().getUpperBound();
		width  = xUpperBound - xLowerBound;
		height = yUpperBound - yLowerBound;

		//	Activate the tool...
		robot.moveTo(chart);
		robot.type(ALT);

		//	--------------------------------------------------------------------
		//	Test each popup button...
		//	--------------------------------------------------------------------

		// Testing PAN DOWN...
		System.out.println("    - Testing PAN DOWN...");
		resetChartMoveAndClick(robot, new Point2D(0, 25));
		assertThat(tool.getYValueAxis().getLowerBound()).isLessThan(yLowerBound);
		assertThat(tool.getYValueAxis().getUpperBound()).isLessThan(yUpperBound);
		assertThat(tool.getYValueAxis().getUpperBound() - tool.getYValueAxis().getLowerBound()).isEqualTo(height, Offset.offset(0.01));

		// Testing PAN UP...
		System.out.println("    - Testing PAN UP...");
		resetChartMoveAndClick(robot, new Point2D(0, -25));
		assertThat(tool.getYValueAxis().getLowerBound()).isGreaterThan(yLowerBound);
		assertThat(tool.getYValueAxis().getUpperBound()).isGreaterThan(yUpperBound);
		assertThat(tool.getYValueAxis().getUpperBound() - tool.getYValueAxis().getLowerBound()).isEqualTo(height, Offset.offset(0.01));

		// Testing PAN LEFT...
		System.out.println("    - Testing PAN LEFT...");
		resetChartMoveAndClick(robot, new Point2D(-25, 0));
		assertThat(tool.getXValueAxis().getLowerBound()).isLessThan(xLowerBound);
		assertThat(tool.getXValueAxis().getUpperBound()).isLessThan(xUpperBound);
		assertThat(tool.getXValueAxis().getUpperBound() - tool.getXValueAxis().getLowerBound()).isEqualTo(width, Offset.offset(0.01));

		// Testing PAN RIGHT...
		System.out.println("    - Testing PAN RIGHT...");
		resetChartMoveAndClick(robot, new Point2D(25, 0));
		assertThat(tool.getXValueAxis().getLowerBound()).isGreaterThan(xLowerBound);
		assertThat(tool.getXValueAxis().getUpperBound()).isGreaterThan(xUpperBound);
		assertThat(tool.getXValueAxis().getUpperBound() - tool.getXValueAxis().getLowerBound()).isEqualTo(width, Offset.offset(0.01));

		// Testing ZOOM IN...
		System.out.println("    - Testing ZOOM IN...");
		resetChartMoveAndClick(robot, new Point2D(40, -40));
		assertThat(tool.getXValueAxis().getLowerBound()).isGreaterThan(xLowerBound);
		assertThat(tool.getXValueAxis().getUpperBound()).isLessThan(xUpperBound);
		assertThat(tool.getXValueAxis().getUpperBound() - tool.getXValueAxis().getLowerBound()).isLessThan(width);
		assertThat(tool.getYValueAxis().getLowerBound()).isGreaterThan(yLowerBound);
		assertThat(tool.getYValueAxis().getUpperBound()).isLessThan(yUpperBound);
		assertThat(tool.getYValueAxis().getUpperBound() - tool.getYValueAxis().getLowerBound()).isLessThan(height);

		// Testing ZOOM OUT...
		System.out.println("    - Testing ZOOM OUT...");
		resetChartMoveAndClick(robot, new Point2D(40, 40));
		assertThat(tool.getXValueAxis().getLowerBound()).isLessThan(xLowerBound);
		assertThat(tool.getXValueAxis().getUpperBound()).isGreaterThan(xUpperBound);
		assertThat(tool.getXValueAxis().getUpperBound() - tool.getXValueAxis().getLowerBound()).isGreaterThan(width);
		assertThat(tool.getYValueAxis().getLowerBound()).isLessThan(yLowerBound);
		assertThat(tool.getYValueAxis().getUpperBound()).isGreaterThan(yUpperBound);
		assertThat(tool.getYValueAxis().getUpperBound() - tool.getYValueAxis().getLowerBound()).isGreaterThan(height);

		// Testing ZOOM TO ONE...
		System.out.println("    - Testing ZOOM TO ONE...");
		resetChartMoveAndClick(robot, new Point2D(40, -40));	//	Zoom In
		resetChartMoveAndClick(robot, new Point2D(0, 0), false);
		assertThat(tool.getXValueAxis().getLowerBound()).isEqualTo(xLowerBound, Offset.offset(0.01));
		assertThat(tool.getXValueAxis().getUpperBound()).isEqualTo(xUpperBound, Offset.offset(0.01));
		assertThat(tool.getYValueAxis().getLowerBound()).isEqualTo(yLowerBound, Offset.offset(0.01));
		assertThat(tool.getYValueAxis().getUpperBound()).isEqualTo(yUpperBound, Offset.offset(0.01));

		// Testing UNDO...
		System.out.println("    - Testing UNDO...");
		resetChartMoveAndClick(robot, new Point2D(40, -40));	//	Zoom In
		resetChartMoveAndClick(robot, new Point2D(-40, -40), false);
		assertThat(tool.getXValueAxis().getLowerBound()).isEqualTo(xLowerBound, Offset.offset(0.01));
		assertThat(tool.getXValueAxis().getUpperBound()).isEqualTo(xUpperBound, Offset.offset(0.01));
		assertThat(tool.getYValueAxis().getLowerBound()).isEqualTo(yLowerBound, Offset.offset(0.01));
		assertThat(tool.getYValueAxis().getUpperBound()).isEqualTo(yUpperBound, Offset.offset(0.01));

		// Testing REDO...
		System.out.println("    - Testing REDO...");
		resetChartMoveAndClick(robot, new Point2D(-40, 40), false);
		//	Now it should be after Zoom In done
		assertThat(tool.getXValueAxis().getLowerBound()).isGreaterThan(xLowerBound);
		assertThat(tool.getXValueAxis().getUpperBound()).isLessThan(xUpperBound);
		assertThat(tool.getXValueAxis().getUpperBound() - tool.getXValueAxis().getLowerBound()).isLessThan(width);
		assertThat(tool.getYValueAxis().getLowerBound()).isGreaterThan(yLowerBound);
		assertThat(tool.getYValueAxis().getUpperBound()).isLessThan(yUpperBound);
		assertThat(tool.getYValueAxis().getUpperBound() - tool.getYValueAxis().getLowerBound()).isLessThan(height);

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

	private void resetChartMoveAndClick ( FxRobot robot, Point2D offset ) {
		resetChartMoveAndClick(robot, offset, true);
	}

	private void resetChartMoveAndClick ( FxRobot robot, Point2D offset, boolean reset ) {

		if ( reset ) {
			tool.getXValueAxis().setLowerBound(xLowerBound);
			tool.getXValueAxis().setUpperBound(xUpperBound);
			tool.getYValueAxis().setLowerBound(yLowerBound);
			tool.getYValueAxis().setUpperBound(yUpperBound);
		}

		robot.moveTo(chart, CENTER, offset, DEFAULT);
		robot.clickOn(PRIMARY);

	}

}
