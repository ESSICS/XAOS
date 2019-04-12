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
import javafx.scene.input.ScrollEvent;
import org.apache.commons.lang3.Validate;
import se.europeanspallationsource.xaos.ui.plot.impl.util.ChartUndoManager;
import se.europeanspallationsource.xaos.ui.plot.plugins.AxisConstrained;

import static se.europeanspallationsource.xaos.ui.plot.util.Assertions.assertValueAxis;


/**
 * Allows dragging the visible plot area along {@link AxisConstraints#X_ONLY X_ONLY},
 * {@link AxisConstraints#Y_ONLY Y_ONLY} or {@link AxisConstraints#X_AND_Y X_AND_Y}
 * axis, changing the visible axis range.
 * <p>
 * If {@link #constraintsProperty() constraints} property is set to
 * {@link AxisConstraints#X_AND_Y X_AND_Y}, then pressing SHIFT when starting
 * panning will restrict the movement to {@link AxisConstraints#X_ONLY X_ONLY}
 * or {@link AxisConstraints#Y_ONLY Y_ONLY} according to the direction of the
 * initial movement of the gesture.</p>
 * <p>
 * Scroll can be performed with a track-pad or a mouse wheel. Shift will lock
 * the scroll on the prominent axis, while Shortcut (Command on macOS, Ctrl
 * on Windows and Linux) will swap the axis (so with a mouse wheel will be
 * possible to scroll on both axis).</p>
 *
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( "ClassWithoutLogger" )
public final class Panner extends Plugin implements AxisConstrained {

	private static final Cursor PAN_CURSOR = Cursor.OPEN_HAND;
	
	private final EventHandler<MouseEvent> dragDetectedHandler = this::dragDetected;
	private final EventHandler<MouseEvent> draggedHandler = this::dragged;
	private final EventHandler<MouseEvent> mouseEnteredHandler = this::mouseEntered;
	private final EventHandler<MouseEvent> mouseExitedHandler = this::mouseExited;
	private final EventHandler<MouseEvent> mouseReleasedHandler = this::mouseReleased;
	private Cursor originalCursor = Cursor.DEFAULT;
	private AxisConstraints overriddenConstraints;
	private final PanHelper panHelper = new PanHelper(this);
	private final EventHandler<ScrollEvent> scrollHandler = this::scroll;
	private boolean shiftWasDown = false;
	private Data<Number, Number> startingDataPoint = null;

	/* *********************************************************************** *
	 * START OF JAVAFX PROPERTIES                                              *
	 * *********************************************************************** */

	/*
	 * ---- constraints --------------------------------------------------------
	 */
	private final ObjectProperty<AxisConstraints> constraints = new SimpleObjectProperty<AxisConstraints>( Panner.this, "constraints", AxisConstraints.X_AND_Y) {
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
	 * <p>
	 * Pressing SHIFT when starting
	 * {@link AxisConstraints#X_ONLY X_ONLY} or {@link AxisConstraints#Y_ONLY Y_ONLY}
	 * according to the direction of the initial movement of the gesture.</p>
	 * <p>
	 * Scroll can be performed with a track-pad or a mouse wheel. Shift will lock
	 * the scroll on the prominent axis, while Shortcut (Command on macOS, Ctrl
	 * on Windows and Linux) will swap the axis (so with a mouse wheel will be
	 * possible to scroll on both axis).</p>
	 *
	 * @see #constraintsProperty()
	 */
	public Panner() {
		this(AxisConstraints.X_AND_Y);
	}

	/**
	 * Creates a new instance of this plugin with the given {@code constraints}.
	 * <p>
	 * If {@link #constraintsProperty() constraints} property is set to
	 * {@link AxisConstraints#X_AND_Y X_AND_Y}, then pressing SHIFT when starting
	 * panning will restrict the movement to {@link AxisConstraints#X_ONLY X_ONLY}
	 * or {@link AxisConstraints#Y_ONLY Y_ONLY} according to the direction of the
	 * initial movement of the gesture.</p>
	 * <p>
	 * Scroll can be performed with a track-pad or a mouse wheel. Shift will lock
	 * the scroll on the prominent axis, while Shortcut (Command on macOS, Ctrl
	 * on Windows and Linux) will swap the axis (so with a mouse wheel will be
	 * possible to scroll on both axis).</p>
	 *
	 * @param constraints Initial value for the
	 *                    {@link #constraintsProperty() constraints} property.
	 */
	public Panner( AxisConstraints constraints ) {

		setConstraints(constraints);

		overriddenConstraints = constraints;

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
		chart.addEventHandler(MouseEvent.MOUSE_RELEASED, mouseReleasedHandler);
		chart.addEventHandler(ScrollEvent.SCROLL, scrollHandler);

	}

	@Override
	protected void chartDisconnected( Chart chart ) {
		chart.addEventHandler(ScrollEvent.SCROLL, scrollHandler);
		chart.removeEventHandler(MouseEvent.MOUSE_RELEASED, mouseReleasedHandler);
		chart.removeEventHandler(MouseEvent.MOUSE_EXITED, mouseExitedHandler);
		chart.removeEventHandler(MouseEvent.MOUSE_ENTERED, mouseEnteredHandler);
		chart.removeEventHandler(MouseEvent.DRAG_DETECTED, dragDetectedHandler);
	}

	private void dragDetected( MouseEvent event ) {

		if ( event.isPrimaryButtonDown()
		  && !event.isAltDown()
		  && !event.isControlDown()
		  && !event.isMetaDown() ) {

			if ( isInsidePlotArea(event) ) {

				//	Capture undo status...
				Chart chart = getChart();

				ChartUndoManager.get(chart).captureUndoable(this);

				//	Capture initial pan location.
				Point2D mouseLocation = getLocationInPlotArea(event);

				startingDataPoint = new Data<>(
					getXValueForDisplayAsDouble(mouseLocation.getX()),
					getYValueForDisplayAsDouble(mouseLocation.getY())
				);

				//	Set the PAN cursor...
				originalCursor = chart.getScene().getCursor();

				chart.getScene().setCursor(PAN_CURSOR);

				//	Add drag listener...
				chart.addEventHandler(MouseEvent.MOUSE_DRAGGED, draggedHandler);

				//	Drag just started: prepare to detect largest movement...
				shiftWasDown = false;
				overriddenConstraints = getConstraints();

			}

			//	Job done, consume the event...
			event.consume();

		}

	}

	private void dragged( MouseEvent event ) {

		if ( isPanOngoing() && isInsidePlotArea(event) ) {

			Point2D mouseLocation = getLocationInPlotArea(event);
			double xOffset = startingDataPoint.getXValue().doubleValue() - getXValueForDisplayAsDouble(mouseLocation.getX());
			double yOffset = startingDataPoint.getYValue().doubleValue() - getYValueForDisplayAsDouble(mouseLocation.getY());

			if ( !shiftWasDown && event.isShiftDown() ) {
				
				shiftWasDown = true;
				overriddenConstraints = getConstraints();

				if ( overriddenConstraints == AxisConstraints.X_AND_Y ) {
					if ( Math.abs(xOffset) > Math.abs(yOffset) ) {
						overriddenConstraints = AxisConstraints.X_ONLY;
					} else {
						overriddenConstraints = AxisConstraints.Y_ONLY;
					}
				}

			} else if ( shiftWasDown && !event.isShiftDown() ) {
				shiftWasDown = false;
				overriddenConstraints = getConstraints();
			}

			if ( overriddenConstraints == AxisConstraints.X_ONLY || overriddenConstraints == AxisConstraints.X_AND_Y ) {
				panHelper.moveHorizontally(xOffset);
			}

			if ( overriddenConstraints == AxisConstraints.Y_ONLY || overriddenConstraints == AxisConstraints.X_AND_Y ) {
				panHelper.moveVertically(yOffset);
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
		shiftWasDown = false;
		overriddenConstraints = getConstraints();

		//	Remove drag listener...
		Chart chart = getChart();

		chart.removeEventHandler(MouseEvent.MOUSE_DRAGGED, draggedHandler);

		//	Restore original cursor...
		chart.getScene().setCursor(originalCursor);

		//	Job done, consume the event...
		event.consume();

	};

	private void scroll ( ScrollEvent event ) {

		if ( !isPanOngoing() && !event.isAltDown() ) {

			//	Capture undo status...
			ChartUndoManager.get(getChart()).captureUndoable(this);

			//	Perform scroll...
			double xOffset = event.isShortcutDown() ? event.getDeltaY() : event.getDeltaX();
			double yOffset = event.isShortcutDown() ? event.getDeltaX() : event.getDeltaY();

			if ( !shiftWasDown && event.isShiftDown() ) {

				shiftWasDown = true;
				overriddenConstraints = getConstraints();

				if ( overriddenConstraints == AxisConstraints.X_AND_Y ) {
					if ( Math.abs(xOffset) > Math.abs(yOffset) ) {
						overriddenConstraints = AxisConstraints.X_ONLY;
					} else {
						overriddenConstraints = AxisConstraints.Y_ONLY;
					}
				}

			} else if ( shiftWasDown && !event.isShiftDown() ) {
				shiftWasDown = false;
				overriddenConstraints = getConstraints();
			}

			if ( overriddenConstraints == AxisConstraints.X_ONLY || overriddenConstraints == AxisConstraints.X_AND_Y ) {
				panHelper.scrollHorizontally(- xOffset);
			}

			if ( overriddenConstraints == AxisConstraints.Y_ONLY || overriddenConstraints == AxisConstraints.X_AND_Y ) {
				panHelper.scrollVertically(yOffset);
			}

			//	Job done, consume the event...
			event.consume();

		}

	}

}
