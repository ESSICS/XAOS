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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;
import javafx.application.Platform;
import javafx.geometry.Point2D;
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
import se.europeanspallationsource.xaos.ui.util.FXUtils;

import static javafx.geometry.Pos.CENTER;
import static javafx.scene.input.MouseButton.PRIMARY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.testfx.robot.Motion.DEFAULT;


/**
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( { "UseOfSystemOutOrSystemErr", "ClassWithoutLogger" } )
public class NavigatorPopupUITest extends ApplicationTest {

	private static final Point2D PAN_DOWN_POINT = new Point2D(0, 25);
	private static final Point2D PAN_LEFT_POINT = new Point2D(-25, 0);
	private static final Point2D PAN_RIGHT_POINT = new Point2D(25, 0);
	private static final Point2D PAN_UP_POINT = new Point2D(0, -25);
	private static final Point2D REDO_POINT = new Point2D(-40, 40);
	private static final Point2D UNDO_POINT = new Point2D(-40, -40);
	private static final Point2D ZOOM_IN_POINT = new Point2D(40, -40);
	private static final Point2D ZOOM_OUT_POINT = new Point2D(40, 40);
	private static final Point2D ZOOM_TO_ONE_POINT = new Point2D(0, 0);

	@BeforeClass
	public static void setUpClass() {
		System.out.println("---- NavigatorControllerUITest ---------------------------------");
	}

	private Label label;
	private double mouseScreenX = 0;
	private double mouseScreenY = 0;
	private NavigatorPopup popup;
	private AnchorPane root;

	@Override
	public void start( Stage stage ) throws IOException {

		root = new AnchorPane();
		popup = new NavigatorPopup();
		label = new Label("—");

		//	Next statement is used to visally check the rounded border of the popup.
		//root.setStyle("-fx-background-color: yellow;");
		root.setOnMouseMoved(e -> {
			mouseScreenX = e.getScreenX();
			mouseScreenY = e.getScreenY();
		});

		popup.setOnPanDown(e -> label.setText(((Node) e.getTarget()).getId()));
		popup.setOnPanLeft(e -> label.setText(((Node) e.getTarget()).getId()));
		popup.setOnPanRight(e -> label.setText(((Node) e.getTarget()).getId()));
		popup.setOnPanUp(e -> label.setText(((Node) e.getTarget()).getId()));
		popup.setOnRedo(e -> label.setText(((Node) e.getTarget()).getId()));
		popup.setOnUndo(e -> label.setText(((Node) e.getTarget()).getId()));
		popup.setOnZoomIn(e -> label.setText(((Node) e.getTarget()).getId()));
		popup.setOnZoomOut(e -> label.setText(((Node) e.getTarget()).getId()));
		popup.setOnZoomToOne(e -> label.setText(((Node) e.getTarget()).getId()));

		label.setFocusTraversable(true);
		label.setLayoutX(20);
		label.setLayoutY(20);
		label.setPrefSize(260, 20);
		label.setAlignment(CENTER);
		label.setTextAlignment(TextAlignment.CENTER);

		root.getChildren().addAll(label);

		Scene scene = new Scene(root, 300, 200);

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

		FxRobot robot = new FxRobot();

		//	Open the popup...
		robot.moveTo(root, CENTER, new Point2D(0, 0), DEFAULT);
		FXUtils.runOnFXThreadAndWait(() -> popup.show(root, mouseScreenX, mouseScreenY));
		assertTrue(popup.isShowing());

		//	Dismiss the popup explicitly...
		FXUtils.runOnFXThreadAndWait(() -> popup.hide());
		assertFalse(popup.isShowing());

		//	Open the popup again...
		robot.moveTo(root, CENTER, new Point2D(0, 0), DEFAULT);
		FXUtils.runOnFXThreadAndWait(() -> popup.show(root, mouseScreenX, mouseScreenY));
		assertTrue(popup.isShowing());

		// Test each popup button...
		testSingleButton(robot, "undo",      UNDO_POINT);
		testSingleButton(robot, "redo",      REDO_POINT);
		testSingleButton(robot, "zoomIn",    ZOOM_IN_POINT);
		testSingleButton(robot, "zoomOut",   ZOOM_OUT_POINT);
		testSingleButton(robot, "panUp",     PAN_UP_POINT);
		testSingleButton(robot, "panRight",  PAN_RIGHT_POINT);
		testSingleButton(robot, "panDown",   PAN_DOWN_POINT);
		testSingleButton(robot, "panLeft",   PAN_LEFT_POINT);
		testSingleButton(robot, "zoomToOne", ZOOM_TO_ONE_POINT);

		//	Dismiss the popup...
		robot.type(KeyCode.ESCAPE);
		assertFalse(popup.isShowing());

	}

	private void testSingleButton( FxRobot robot, String buttonID, Point2D offset ) throws InterruptedException {

		System.out.println(MessageFormat.format("    - Testing ''{0}''...", buttonID));

		//	Clear the label, click on the button and verify that the label's
		//	text corresponds to the given cssID.
		CountDownLatch latch = new CountDownLatch(1);

		Platform.runLater(() -> {
			label.setText("—");
			latch.countDown();
		});

		latch.await();

		robot.moveTo(root, CENTER, offset, DEFAULT);
		robot.clickOn(PRIMARY);
		assertThat(label.getText()).isEqualTo(buttonID);

	}

}
