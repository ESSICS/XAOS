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


import java.text.MessageFormat;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.Chart;
import javafx.scene.chart.XYChart;
import javafx.scene.input.MouseEvent;
import se.europeanspallationsource.xaos.ui.plot.DensityChartFX;

import static se.europeanspallationsource.xaos.ui.plot.util.Assertions.assertValueAxis;


/**
 * Abstract class base of all cursor plugins.
 *
 * @author claudio.rosati@esss.se
 */
public abstract class AbstractCursorPlugin extends AbstractBoundedPlugin {

	private final EventHandler<MouseEvent> dragDetectedHandler = this::dragDetected;
	private final EventHandler<MouseEvent> mouseEnteredHandler = this::mouseEntered;
	private final EventHandler<MouseEvent> mouseExitedHandler = this::mouseExited;
	private final EventHandler<MouseEvent> mouseMoveHandler = this::mouseMove;
	private Point2D sceneMouseLocation = null;

	/**
	 * @param name The display name of this plugin.
	 */
	public AbstractCursorPlugin( String name ) {
		super(name);
	}

	@Override
	@SuppressWarnings( "null" )
	protected void chartConnected( Chart chart ) {

		if ( chart instanceof BarChart ) {
			throw new UnsupportedOperationException(MessageFormat.format(
				"{0} non supported.",
				chart.getClass().getSimpleName()
			));
		} else if ( chart instanceof XYChart<?, ?> ) {
			assertValueAxis(( (XYChart<?, ?>) chart ).getXAxis(), "X");
			assertValueAxis(( (XYChart<?, ?>) chart ).getYAxis(), "Y");
		} else if ( chart instanceof DensityChartFX<?, ?> ) {
			assertValueAxis(( (DensityChartFX<?, ?>) chart ).getXAxis(), "X");
			assertValueAxis(( (DensityChartFX<?, ?>) chart ).getYAxis(), "Y");
		}

		chart.addEventHandler(MouseEvent.DRAG_DETECTED, dragDetectedHandler);
		chart.addEventHandler(MouseEvent.MOUSE_ENTERED, mouseEnteredHandler);
		chart.addEventHandler(MouseEvent.MOUSE_EXITED, mouseExitedHandler);

		super.chartConnected(chart);

	}

	@Override
	protected void chartDisconnected( Chart chart ) {

		super.chartDisconnected(chart);

		chart.removeEventHandler(MouseEvent.MOUSE_EXITED, mouseExitedHandler);
		chart.removeEventHandler(MouseEvent.MOUSE_ENTERED, mouseEnteredHandler);
		chart.removeEventHandler(MouseEvent.DRAG_DETECTED, dragDetectedHandler);

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
