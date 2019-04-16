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


import chart.DensityChartFX;
import chart.Plugin;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.chart.Chart;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;


/**
 * Horizontal and vertical {@link Line}s drawn on the plot area, crossing at the
 * mouse cursor location.
 * <p>
 * CSS style class name: {@code chart-coordinates-line}</p>
 *
 * @author Grzegorz Kruk
 */
public final class CursorLines extends Plugin {

	private final Line horizontalLine = new Line();
	private final EventHandler<MouseEvent> dragDetectedHandler = this::dragDetected;
	private final EventHandler<MouseEvent> mouseEnteredHandler = this::mouseEntered;
	private final EventHandler<MouseEvent> mouseExitedHandler = this::mouseExited;
	private final EventHandler<MouseEvent> mouseMoveHandler = this::mouseMove;
	private final Line verticalLine = new Line();

	/**
	 * Creates a new instance of this plugin.
	 */
	public CursorLines() {

		verticalLine.getStyleClass().add("chart-coordinates-line");
		verticalLine.setManaged(false);
		verticalLine.setVisible(false);
		horizontalLine.getStyleClass().add("chart-coordinates-line");
		horizontalLine.setManaged(false);
		horizontalLine.setVisible(false);

		getPlotChildren().addAll(verticalLine, horizontalLine);

	}

	@Override
	protected void chartConnected( Chart chart ) {
		chart.addEventHandler(MouseEvent.DRAG_DETECTED, dragDetectedHandler);
		chart.addEventHandler(MouseEvent.MOUSE_ENTERED, mouseEnteredHandler);
		chart.addEventHandler(MouseEvent.MOUSE_EXITED, mouseExitedHandler);
	}

	@Override
	protected void chartDisconnected( Chart chart ) {
		chart.removeEventHandler(MouseEvent.MOUSE_EXITED, mouseExitedHandler);
		chart.removeEventHandler(MouseEvent.MOUSE_ENTERED, mouseEnteredHandler);
		chart.removeEventHandler(MouseEvent.DRAG_DETECTED, dragDetectedHandler);
	}

	private void dragDetected( MouseEvent event ) {
		horizontalLine.setVisible(false);
		verticalLine.setVisible(false);
	}

	private void mouseEntered( MouseEvent event ) {
		getChart().addEventHandler(MouseEvent.MOUSE_MOVED, mouseMoveHandler);
	}

	private void mouseExited( MouseEvent event ) {
		getChart().removeEventHandler(MouseEvent.MOUSE_MOVED, mouseMoveHandler);
	}

	private void mouseMove( MouseEvent event ) {

		if ( isInsidePlotArea(event) ) {

			Point2D mouseLocation = getLocationInPlotArea(event);
			Bounds plotAreaBounds = getPlotAreaBounds();

			if ( getChart() instanceof DensityChartFX<?, ?> ) {

				mouseLocation = mouseLocation.add(new Point2D(getXValueAxis().getLayoutX(), 0.0));

				horizontalLine.setStartX(getXValueAxis().getLayoutX());
				horizontalLine.setEndX(getXValueAxis().getWidth() + getXValueAxis().getLayoutX());
				horizontalLine.setStartY(mouseLocation.getY());
				horizontalLine.setEndY(mouseLocation.getY());

				verticalLine.setStartY(getYValueAxis().getLayoutY());
				verticalLine.setStartX(mouseLocation.getX());
				verticalLine.setEndX(mouseLocation.getX());
				verticalLine.setEndY(getYValueAxis().getHeight() + getYValueAxis().getLayoutY());

			} else {

				horizontalLine.setStartX(0);
				horizontalLine.setStartY(mouseLocation.getY());
				horizontalLine.setEndX(plotAreaBounds.getWidth());
				horizontalLine.setEndY(mouseLocation.getY());

				verticalLine.setStartX(mouseLocation.getX());
				verticalLine.setStartY(0);
				verticalLine.setEndX(mouseLocation.getX());
				verticalLine.setEndY(plotAreaBounds.getHeight());

			}

			if ( !horizontalLine.isVisible() ) {
				horizontalLine.setVisible(true);
				verticalLine.setVisible(true);
			}

		} else {
			horizontalLine.setVisible(false);
			verticalLine.setVisible(false);
		}

	}

}
