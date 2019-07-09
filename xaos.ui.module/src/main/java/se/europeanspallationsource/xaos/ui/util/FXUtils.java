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
package se.europeanspallationsource.xaos.ui.util;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import se.europeanspallationsource.xaos.core.util.LogUtils;

import static java.util.logging.Level.SEVERE;
import static javafx.scene.control.ContentDisplay.GRAPHIC_ONLY;



/**
 * Various utilities for the FX platform.
 *
 * @author claudiorosati
 */
public class FXUtils {

	private static final Logger LOGGER = Logger.getLogger(FXUtils.class.getName());

	/**
	 * Creates a {@link TitledPane} with the given {@code title} and
	 * {@code content}, and places the further {@link Node}s on the right.
	 *
	 * @param title         The titled pane title string.
	 * @param content       The titled pane content.
	 * @param rightElements The {@link Node}s to be displayed on the right side
	 *                      of the returned titled pane.
	 * @return A configured {@link TitledPane} object.
	 */
	public static TitledPane createTitlePane ( String title, Node content, Node... rightElements ) {

		final TitledPane titledPane = new TitledPane(title, content);
		final Label titleLabel = new Label(title);

		titleLabel.textProperty().bind(titledPane.textProperty());

		final HBox rightBox = new HBox(4);

		if ( rightElements.length > 0 ) {
			rightBox.getChildren().addAll(rightElements);
		}

		final AnchorPane titleGraphics = new AnchorPane();

		AnchorPane.setLeftAnchor(titleLabel, 0.0);
		AnchorPane.setRightAnchor(rightBox, 0.0);

		titleGraphics.getChildren().addAll(titleLabel, rightBox);

		titledPane.setGraphic(titleGraphics);
		titledPane.setContentDisplay(GRAPHIC_ONLY);
		titledPane.setExpanded(false);

		Platform.runLater(() -> {

			final Node titleRegion = titledPane.lookup(".title");
			final Insets padding = ( (Region) titleRegion ).getPadding();
			final Region arrow = (Region) titledPane.lookup(".arrow-button");

			titleGraphics.prefWidthProperty().bind(
				titledPane.widthProperty()
					.subtract(arrow.widthProperty())
					.subtract(padding.getLeft() + padding.getRight() + 2)
			);

		});

		return titledPane;

	}

	/**
	 * Sets the minimum and the preferred size of the given {@code region} to
	 * the given {@code size}.
	 *
	 * @param region The {@code Region} to be squared.
	 * @param size   The new width and height dimension of the given {@code region}.
	 * @return The given {@code region}.
	 */
	public static Region makeSquare ( Region region, double size ) {

		region.setMinSize(size, size);
		region.setPrefSize(size, size);

		return region;

	}

	/**
	 * Execute the given {@code task} on the JavaFX event thread via
	 * {@link Platform#runLater(Runnable)} and await for termination.
	 * <p>
	 * If an exception is thrown while the given {@code task} is executed,
	 * an error is logged.</p>
	 *
	 * @param task The {@link Runnable} to be executed.
	 * @throws InterruptedException If the current thread is interrupted while
	 *                              waiting.
	 */
	public static void runOnFXThreadAndWait( Runnable task ) throws InterruptedException {

		if ( Platform.isFxApplicationThread() ) {
			try {
				task.run();
			} catch ( Exception ex ) {
				LogUtils.log(LOGGER, SEVERE, ex, "Exception while running task in the JavaFX event thread.");
			}
		} else {

			CountDownLatch latch = new CountDownLatch(1);

			Platform.runLater(() -> {
				try {
					task.run();
				} catch ( Exception ex ) {
					LogUtils.log(LOGGER, SEVERE, ex, "Exception while running task in the JavaFX event thread.");
				} finally {
					latch.countDown();
				}
			});

			latch.await();
		}

	}

	/**
	 * Execute the given {@code task} on the JavaFX event thread via
	 * {@link Platform#runLater(Runnable)} and await for termination.
	 * <p>
	 * If an exception is thrown while the given {@code task} is executed,
	 * an error is logged.</p>
	 *
	 * @param task    The {@link Runnable} to be executed.
	 * @param timeout The maximum time to wait.
	 * @param unit    The time unit of the {@code timeout} argument.
	 * @return {@code true} if the given {@code task} was executed, or
	 *         {@code false} if the waiting time elapsed before the {@code task}
	 *         is executed.
	 * @throws InterruptedException If the current thread is interrupted while
	 *                              waiting.
	 */
	public static boolean runOnFXThreadAndWait( Runnable task, long timeout, TimeUnit unit )
		throws InterruptedException
	{

		if ( Platform.isFxApplicationThread() ) {

			try {
				task.run();
			} catch ( Exception ex ) {
				LogUtils.log(LOGGER, SEVERE, ex, "Exception while running task in the JavaFX event thread.");
			}

			return true;

		} else {

			CountDownLatch latch = new CountDownLatch(1);

			Platform.runLater(() -> {
				try {
					task.run();
				} catch ( Exception ex ) {
					LogUtils.log(LOGGER, SEVERE, ex, "Exception while running task in the JavaFX event thread.");
				} finally {
					latch.countDown();
				}
			});

			return latch.await(timeout, unit);
		}

	}

	/**
	 * Execute the given {@code task} on the JavaFX event thread via
	 * {@link Platform#runLater(Runnable)} and await for termination.
	 * <p>
	 * If an exception is thrown while the given {@code task} is executed,
	 * an error is logged.</p>
	 * <p>
	 * If the current thread is interrupted no exception is thrown: an error
	 * message is logged instead.
	 * </p>
	 *
	 * @param task The {@link Runnable} to be executed.
	 */
	public static void runOnFXThreadAndWaitSilently( Runnable task ) {

		if ( Platform.isFxApplicationThread() ) {
			try {
				task.run();
			} catch ( Exception ex ) {
				LogUtils.log(LOGGER, SEVERE, ex, "Exception while running task in the JavaFX event thread.");
			}
		} else {

			CountDownLatch latch = new CountDownLatch(1);

			Platform.runLater(() -> {
				try {
					task.run();
				} catch ( Exception ex ) {
					LogUtils.log(LOGGER, SEVERE, ex, "Exception while running task in the JavaFX event thread.");
				} finally {
					latch.countDown();
				}
			});

			try {
				latch.await();
			} catch ( InterruptedException ex ) {
				LogUtils.log(LOGGER, SEVERE, ex, "Exception while waiting for task being executed in the JavaFX event thread.");
			}

		}

	}

	private FXUtils() {
		//	Nothing to do.
	}

}
