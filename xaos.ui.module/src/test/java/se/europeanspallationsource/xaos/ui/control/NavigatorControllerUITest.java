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
package se.europeanspallationsource.xaos.ui.control;


import java.io.IOException;
import java.text.MessageFormat;
import java.util.concurrent.TimeoutException;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import se.europeanspallationsource.xaos.core.util.ThreadUtils;
import se.europeanspallationsource.xaos.ui.control.NavigatorController.FillStyle;
import se.europeanspallationsource.xaos.ui.control.NavigatorController.StrokeStyle;
import se.europeanspallationsource.xaos.ui.util.FXUtils;

import static javafx.geometry.Pos.BOTTOM_LEFT;
import static javafx.geometry.Pos.BOTTOM_RIGHT;
import static javafx.geometry.Pos.CENTER;
import static javafx.geometry.Pos.TOP_LEFT;
import static javafx.geometry.Pos.TOP_RIGHT;
import static javafx.scene.input.MouseButton.PRIMARY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.testfx.robot.Motion.DEFAULT;


/**
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( { "UseOfSystemOutOrSystemErr", "ClassWithoutLogger" } )
public class NavigatorControllerUITest extends ApplicationTest {

	@BeforeClass
	public static void setUpClass() {
		System.out.println("---- NavigatorControllerUITest ---------------------------------");
	}

	private AnchorPane root;
	private NavigatorController controller;
	private Label label;

	@Override
	public void start( Stage stage ) throws IOException {

		root = new AnchorPane();
		controller = new NavigatorController();
		label = new Label("—");

		controller.setLayoutX(20);
		controller.setLayoutY(20);
		controller.setOnBackward(e -> label.setText(((Node) e.getTarget()).getId()));
		controller.setOnForward(e -> label.setText(((Node) e.getTarget()).getId()));
		controller.setOnPanDown(e -> label.setText(((Node) e.getTarget()).getId()));
		controller.setOnPanLeft(e -> label.setText(((Node) e.getTarget()).getId()));
		controller.setOnPanRight(e -> label.setText(((Node) e.getTarget()).getId()));
		controller.setOnPanUp(e -> label.setText(((Node) e.getTarget()).getId()));
		controller.setOnZoomIn(e -> label.setText(((Node) e.getTarget()).getId()));
		controller.setOnZoomOut(e -> label.setText(((Node) e.getTarget()).getId()));
		controller.setOnZoomToOne(e -> label.setText(((Node) e.getTarget()).getId()));

		label.setLayoutX(20);
		label.setLayoutY(140);
		label.setPrefSize(100, 20);
		label.setAlignment(CENTER);
		label.setTextAlignment(TextAlignment.CENTER);

		root.getChildren().addAll(controller, label);

		Scene scene = new Scene(root, 140, 180);

		scene.getStylesheets().add(getClass().getResource("css/modena.css").toExternalForm());
		stage.setScene(scene);
		stage.show();

	}

	@After
	public void tearDown() throws TimeoutException {
		FxToolkit.cleanupStages();
	}

	@Test
	public void test() throws InterruptedException {

		System.out.println("  Testing ''NavigatorController''...");

		//	Give the UI time to draw itself.
		ThreadUtils.sleep(500);

		FxRobot robot = new FxRobot();

		testSingleButton(robot, "#forward",   TOP_LEFT,     new Point2D( 10,  10));
		testSingleButton(robot, "#backward",  BOTTOM_LEFT,  new Point2D( 10, -10));
		testSingleButton(robot, "#zoomIn",    TOP_RIGHT,    new Point2D(-10,  10));
		testSingleButton(robot, "#zoomOut",   BOTTOM_RIGHT, new Point2D(-10, -10));
		testSingleButton(robot, "#panUp",     CENTER,       new Point2D(  0,   0));
		testSingleButton(robot, "#panRight",  CENTER,       new Point2D(  0,   0));
		testSingleButton(robot, "#panDown",   CENTER,       new Point2D(  0,   0));
		testSingleButton(robot, "#panLeft",   CENTER,       new Point2D(  0,   0));
		testSingleButton(robot, "#zoomToOne", CENTER,       new Point2D(  0,   0));

	}

	private void testSingleButton( FxRobot robot, String cssID, Pos position, Point2D offset ) throws InterruptedException {

		System.out.println(MessageFormat.format("    - Testing ''{0}''...", cssID));

		Node node = robot.lookup(cssID).query();

		//	Check the button is in its normal status.
		assertTrue(node.getStyle().contains(FillStyle.DEFAULT.getStyle()) || node.getStyle().contains(FillStyle.LIGHTER.getStyle()));

		//	Move the mouse cursor over the node and check its status.
		robot.moveTo(node, position, offset, DEFAULT);
		assertTrue(node.getStyle().contains(FillStyle.HOVER.getStyle()));

		//	Press the primary mouse button and check the node status.
		robot.press(PRIMARY);
		assertTrue(node.getStyle().contains(FillStyle.PRESSED.getStyle()));

		//	Release the primary mouse button and check the node status.
		robot.release(PRIMARY);
		assertTrue(node.getStyle().contains(FillStyle.HOVER.getStyle()));

		//	Move the mouse cursor outside the node and check its status.
		robot.moveTo(node, position, new Point2D(100, 100), DEFAULT);
		assertTrue(node.getStyle().contains(FillStyle.DEFAULT.getStyle()) || node.getStyle().contains(FillStyle.LIGHTER.getStyle()));

		//	Mode the cursor over the node, press and keep pressed the primary
		//	mouse button, then move outside the button: it must be in it normal
		//	status.
		robot.moveTo(node, position, offset, DEFAULT);
		assertTrue(node.getStyle().contains(FillStyle.HOVER.getStyle()));
		robot.press(PRIMARY);
		assertTrue(node.getStyle().contains(FillStyle.PRESSED.getStyle()));
		robot.moveTo(node, position, new Point2D(100, 100), DEFAULT);
		assertTrue(node.getStyle().contains(FillStyle.PRESSED.getStyle()));
		robot.release(PRIMARY);
		assertTrue(node.getStyle().contains(FillStyle.DEFAULT.getStyle()) || node.getStyle().contains(FillStyle.LIGHTER.getStyle()));

		//	Clear the label, click on the button and verify that the label's
		//	text corresponds to the given cssID.
		FXUtils.runOnFXThreadAndWait(() -> label.setText("—"));
		robot.moveTo(node, position, offset, DEFAULT);
		robot.clickOn(PRIMARY);
		assertThat("#" + label.getText()).isEqualTo(cssID);

		//	After clicking the button it should have been focused.
		assertTrue(node.getStyle().contains(StrokeStyle.FOCUSED.getStyle()));

		//	Press SPACE to press the button.
		FXUtils.runOnFXThreadAndWait(() -> label.setText("—"));
		robot.type(KeyCode.SPACE);
		assertThat("#" + label.getText()).isEqualTo(cssID);

		//	Press ENTER to press the button.
		FXUtils.runOnFXThreadAndWait(() -> label.setText("—"));
		robot.type(KeyCode.ENTER);
		assertThat("#" + label.getText()).isEqualTo(cssID);

	}

}
