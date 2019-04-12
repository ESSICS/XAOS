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
import java.text.MessageFormat;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.Chart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.input.MouseEvent;
import org.apache.commons.lang3.Validate;
import se.europeanspallationsource.xaos.ui.plot.impl.util.ChartUndoManager;
import se.europeanspallationsource.xaos.ui.plot.plugins.AxisConstrained;

import static se.europeanspallationsource.xaos.ui.plot.util.Assertions.assertValueAxis;


/**
 * Allows dragging the visible plot area along X_ONLY and/or Y_ONLY axis,
 * changing the visible axis range.
 *
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( "ClassWithoutLogger" )
public final class Pan extends Plugin implements AxisConstrained {

	private static final Cursor PAN_CURSOR = Cursor.OPEN_HAND;
	
	private Cursor originalCursor = Cursor.DEFAULT;
	private double plotHeight;
	private double plotWidth;
	private Data<Number, Number> startingDataPoint = null;

	/* *********************************************************************** *
	 * START OF JAVAFX PROPERTIES                                              *
	 * *********************************************************************** */

	/*
	 * ---- constraints --------------------------------------------------------
	 */
	private final ObjectProperty<AxisConstraints> constraints = new SimpleObjectProperty<AxisConstraints>( Pan.this, "constraints", AxisConstraints.X_AND_Y) {
		@Override protected void invalidated() {
			Validate.notNull(get(), "Null '%1$s' property.", getName());
		}
	};

	@Override
	public ObjectProperty<AxisConstraints> constraintsProperty() {
		return constraints;
	}

	/* *********************************************************************** *
	 * END OF JAVAFX PROPERTIES                                                *
	 * *********************************************************************** */

	/**
	 * Creates a new instance of this plugin with
	 * {@link AxisConstraints#X_AND_Y X_AND_Y} constraints.
	 *
	 * @see #constraintsProperty()
	 */
	public Pan() {
		this(AxisConstraints.X_AND_Y);
	}

	/**
	 * Creates a new instance of this plugin with the given {@code constraints}.
	 *
	 * @param constraints Initial value for the
	 *                    {@link #constraintsProperty() constraints} property.
	 */
	public Pan( AxisConstraints constraints ) {
		setConstraints(constraints);
	}

	private final EventHandler<MouseEvent> dragDetectedHandler = this::dragDetected;
	private final EventHandler<MouseEvent> draggedHandler = this::dragged;
	private final EventHandler<MouseEvent> mouseEnteredHandler  = this::mouseEntered;
	private final EventHandler<MouseEvent> mouseExitedHandler   = this::mouseExited;
	private final EventHandler<MouseEvent> mouseReleasedHandler = this::mouseReleased;

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
		chart.addEventHandler(MouseEvent.MOUSE_RELEASED, mouseReleasedHandler);

	}

	@Override
	protected void chartDisconnected( Chart chart ) {
		chart.removeEventHandler(MouseEvent.MOUSE_RELEASED, mouseReleasedHandler);
		chart.removeEventHandler(MouseEvent.MOUSE_EXITED, mouseExitedHandler);
		chart.removeEventHandler(MouseEvent.MOUSE_ENTERED, mouseEnteredHandler);
		chart.removeEventHandler(MouseEvent.DRAG_DETECTED, dragDetectedHandler);
	}

	private void dragDetected( MouseEvent event ) {

		if ( event.isPrimaryButtonDown()
		  && !event.isAltDown()
		  && !event.isControlDown()
		  && !event.isMetaDown()
		  && !event.isShiftDown() ) {

			//	Capture undo status...
			Chart chart = getChart();

			ChartUndoManager.get(chart).captureUndoable(this);

			//	Disable auto range...
			getXValueAxis().setAutoRanging(false);
			getYValueAxis().setAutoRanging(false);

			//	Capture initial pan location.
			Point2D mouseLocation = getLocationInPlotArea(event);

			startingDataPoint = new Data<>(
				getXValueForDisplayAsDouble(mouseLocation.getX()),
				getYValueForDisplayAsDouble(mouseLocation.getY())
			);
			plotHeight = getYValueAxis().getUpperBound() - getYValueAxis().getLowerBound();
			plotWidth = getXValueAxis().getUpperBound() - getXValueAxis().getLowerBound();

			//	Set the PAN cursor...
			originalCursor = chart.getScene().getCursor();

			chart.getScene().setCursor(PAN_CURSOR);

			//	Add drag listener...
			chart.addEventHandler(MouseEvent.MOUSE_DRAGGED, draggedHandler);

			//	Job done, consume the event...
			event.consume();

		}

	}

	private void dragged( MouseEvent event ) {

		if ( isPanOngoing() ) {
			//	Drag started...

			Point2D mouseLocation = getLocationInPlotArea(event);
			double xOffset = startingDataPoint.getXValue().doubleValue() - getXValueForDisplayAsDouble(mouseLocation.getX());
			double yOffset = startingDataPoint.getYValue().doubleValue() - getYValueForDisplayAsDouble(mouseLocation.getY());

			if ( getConstraints() == AxisConstraints.X_ONLY || getConstraints() == AxisConstraints.X_AND_Y ) {
				getXValueAxis().setLowerBound(getXValueAxis().getLowerBound() + xOffset);
				getXValueAxis().setUpperBound(getXValueAxis().getLowerBound() + plotWidth);
			}

			if ( getConstraints() == AxisConstraints.Y_ONLY || getConstraints() == AxisConstraints.X_AND_Y ) {
				getYValueAxis().setLowerBound(getYValueAxis().getLowerBound() + yOffset);
				getYValueAxis().setUpperBound(getYValueAxis().getLowerBound() + plotHeight);
			}

			//	Job done, consume the event...
			event.consume();

		}

	}

	private boolean isPanOngoing() {
		return ( startingDataPoint != null );
	}

	private void mouseEntered( MouseEvent event ) {

		if ( isPanOngoing() ) {

			Chart chart = getChart();

			//	Add drag listener...
			chart.addEventHandler(MouseEvent.MOUSE_DRAGGED, draggedHandler);

			//	Set the PAN cursor...
			chart.getScene().setCursor(PAN_CURSOR);

		}

	}

	private void mouseExited( MouseEvent event ) {

		if ( isPanOngoing() ) {

			Chart chart = getChart();

			//	Remove drag listener...
			chart.removeEventHandler(MouseEvent.MOUSE_DRAGGED, draggedHandler);

			//	Restore original cursor...
			chart.getScene().setCursor(originalCursor);

		}

	}

	private void mouseReleased( MouseEvent event ) {

		//	Clear starting point...
		startingDataPoint = null;

		//	Remove drag listener...
		Chart chart = getChart();

		chart.removeEventHandler(MouseEvent.MOUSE_DRAGGED, draggedHandler);

		//	Restore original cursor...
		chart.getScene().setCursor(originalCursor);

		//	Job done, consume the event...
		event.consume();

	};

}
