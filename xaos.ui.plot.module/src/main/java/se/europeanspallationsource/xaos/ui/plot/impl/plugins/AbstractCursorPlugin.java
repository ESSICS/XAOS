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


import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.chart.Chart;
import javafx.scene.input.MouseEvent;


/**
 * Abstract class base of all cursor plugins.
 *
 * @author claudio.rosati@esss.se
 */
public abstract class AbstractCursorPlugin extends AbstractBoundedPlugin {

	//	TODO:CR Listen to change in the chart's area and update the displayed
	//			value at the cursor position.
	//	TODO:CR Define the following handlers. Give default implementation.
	//			mouseMove by default shoult record the cursor's position.

	private final EventHandler<MouseEvent> dragDetectedHandler = this::dragDetected;
	private final EventHandler<MouseEvent> mouseEnteredHandler = this::mouseEntered;
	private final EventHandler<MouseEvent> mouseExitedHandler = this::mouseExited;
	private final EventHandler<MouseEvent> mouseMoveHandler = this::mouseMove;
	private Point2D sceneMouseLocation = null;

	@Override
	protected void chartConnected( Chart newChart ) {
		super.chartConnected(newChart);
	}

	@Override
	protected void chartDisconnected( Chart oldChart ) {
		super.chartDisconnected(oldChart);
	}

	/**
	 * Called when a drag gesture is detected.
	 *
	 * @param e The {@link MouseEvent} associated with the gesture.
	 */
	protected abstract void dragDetected( MouseEvent e );

	/**
	 * @return The current mouse location in scene coordinate system or
	 *         {@code null} if the mouse cursor is not inside the chart.
	 */
	protected Point2D getSceneMouseLocation() {
		return sceneMouseLocation;
	}

	/**
	 * Called when mouse enters the chart. Current implementation adds the
	 * {@link #mouseMoveHandler} to the chart.
	 *
	 * @param e The {@link MouseEvent} when mouse entered the chart.
	 */
	protected void mouseEntered( MouseEvent e ) {

		sceneMouseLocation = new Point2D(e.getSceneX(), e.getSceneY());

		getChart().addEventHandler(MouseEvent.MOUSE_MOVED, mouseMoveHandler);

	}

	/**
	 * Called when mouse exits the chart. Current implementation removes the
	 * {@link #mouseMoveHandler} from the chart.
	 *
	 * @param e The {@link MouseEvent} when mouse exited the chart.
	 */
	protected void mouseExited( MouseEvent e ) {

		getChart().removeEventHandler(MouseEvent.MOUSE_MOVED, mouseMoveHandler);

		sceneMouseLocation = null;

	}

	/**
	 * Called when mouse moves inside the chart. Current implementation stores
	 * into {@link #sceneMouseLocation} the current mouse location in scene
	 * coordinate system.
	 *
	 * @param e The {@link MouseEvent} when mouse moved inside the chart.
	 */
	protected void mouseMove( MouseEvent e ) {

		sceneMouseLocation = new Point2D(e.getSceneX(), e.getSceneY());

	}

}
