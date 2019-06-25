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
package se.europeanspallationsource.xaos.ui.plot.plugins.impl;


import java.text.MessageFormat;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.Chart;
import javafx.scene.chart.XYChart;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.apache.commons.lang3.Validate;
import se.europeanspallationsource.xaos.ui.plot.DensityChartFX;
import se.europeanspallationsource.xaos.ui.plot.plugins.AbstractNamedPlugin;
import se.europeanspallationsource.xaos.ui.plot.plugins.AxisConstrained;
import se.europeanspallationsource.xaos.ui.plot.util.impl.ChartUndoManager;

import static se.europeanspallationsource.xaos.ui.plot.plugins.impl.ZoomHelper.DEFAULT_ANIMATION_DURATION;
import static se.europeanspallationsource.xaos.ui.plot.util.Assertions.assertValueAxis;


/**
 * A plugin that allows zooming-by-dragging operation along the specified axis.
 * <p>
 * The plugin is activated by pressing SHORTCUT (COMMAND on macOS, CTRL on
 * Windows and Linux) and then dragging the rectangle to zoom in, or
 * double-clicking to auto-scale.</p>
 * <p>
 * Pressing also SHIFT will constrain zooming on a single axis according to
 * the longest side of the drawn rectangle.</p>
 *
 * @author claudio.rosati@esss.se
 * @css.class {@code chart-zoomer}
 */
@SuppressWarnings( "ClassWithoutLogger" )
public final class Zoomer extends AbstractNamedPlugin implements AxisConstrained {

	private static final String NAME = "Zoomer";
	private static final Cursor ZOOM_CURSOR = Cursor.HAND;
	private static final int ZOOM_RECT_MIN_SIZE = 10;

	private final EventHandler<MouseEvent> dragDetectedHandler = this::dragDetected;
	private final EventHandler<MouseEvent> draggedHandler = this::dragged;
	private final EventHandler<MouseEvent> mouseClickedHandler = this::mouseClicked;
	private final EventHandler<MouseEvent> mouseEnteredHandler = this::mouseEntered;
	private final EventHandler<MouseEvent> mouseExitedHandler = this::mouseExited;
	private final EventHandler<MouseEvent> mouseReleasedHandler = this::mouseReleased;
	private double oldMouseXLocation;
	private double oldMouseYLocation;
	private Cursor originalCursor = Cursor.DEFAULT;
	private AxisConstraints overriddenConstraints;
	private boolean shiftWasDown = false;
	private final EventHandler<ZoomEvent> zoomFinishedHandler = this::zoomFinished;
	private final EventHandler<ZoomEvent> zoomHandler = this::zoom;
	private final ZoomHelper zoomHelper = new ZoomHelper(this);
	private Point2D zoomEndPoint = null;
	private final Rectangle zoomRectangle = new Rectangle();
	private Point2D zoomStartPoint = null;
	private final EventHandler<ZoomEvent> zoomStartedHandler = this::zoomStarted;
	private boolean zooming = false;
	
	/* *********************************************************************** *
	 * START OF JAVAFX PROPERTIES                                              *
	 * *********************************************************************** */

	/*
	 * ---- animated -----------------------------------------------------------
	 */
	private final BooleanProperty animated = new SimpleBooleanProperty(Zoomer.this, "animated", false);

	public final BooleanProperty animatedProperty() {
		return animated;
	}

	public final boolean isAnimated() {
		return animatedProperty().get();
	}

	public final void setAnimated( boolean value ) {
		animatedProperty().set(value);
	}

	/*
	 * ---- animationDuration --------------------------------------------------
	 */
	private final ObjectProperty<Duration> animationDuration = new SimpleObjectProperty<>(this, "zoomDuration", DEFAULT_ANIMATION_DURATION);

	/**
	 * @return The animation duration property. Duration of the animated zoom
	 * will be in milliseconds, by default initialized to 500ms.
	 */
	public final ObjectProperty<Duration> animationDurationProperty() {
		return animationDuration;
	}

	public final Duration getAnimationDuration() {
		return animationDurationProperty().get();
	}

	public final void setAnimationDuration( Duration value ) {
		animationDurationProperty().set(value);
	}

	/*
	 * ---- constraints --------------------------------------------------------
	 */
	private final ObjectProperty<AxisConstraints> constraints = new SimpleObjectProperty<>( Zoomer.this, "constraints", AxisConstraints.X_AND_Y) {
		@Override protected void invalidated() {
			Validate.notNull(get(), "Null '%1$s' property.", getName());
		}
	};

	@Override
	public final ObjectProperty<AxisConstraints> constraintsProperty() {
		return constraints;
	}

	/* *********************************************************************** *
	 * END OF JAVAFX PROPERTIES                                                *
	 * *********************************************************************** */

	/**
	 * Creates a new instance of this plugin with animation disabled and
	 * {@link AxisConstraints#X_AND_Y X_AND_Y} constraints.
	 */
	public Zoomer() {
		this(AxisConstraints.X_AND_Y);
	}

	/**
	 * Creates a new instance of this plugin with animation disabled and the
	 * given {@code constraints}.
	 *
	 * @param constraints Initial value for the
	 *                    {@link #constraintsProperty() constraints} property.
	 */
	public Zoomer( AxisConstraints constraints ) {
		this(constraints, false);
	}

	/**
	 * Creates a new instance of this plugin.
	 *
	 * @param constraints Initial value for the
	 *                    {@link #constraintsProperty() constraints} property.
	 * @param animated    Initial value of {@link #animatedProperty() animated}
	 *                    property.
	 */
	public Zoomer( AxisConstraints constraints, boolean animated ) {

		super(NAME);

		setConstraints(constraints);
		setAnimated(animated);

		overriddenConstraints = constraints;

		zoomRectangle.setManaged(false);
		zoomRectangle.setVisible(false);
		zoomRectangle.getStyleClass().add("chart-zoomer");

		getPlotChildren().add(zoomRectangle);

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
		chart.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseClickedHandler);
		chart.addEventHandler(MouseEvent.MOUSE_ENTERED, mouseEnteredHandler);
		chart.addEventHandler(MouseEvent.MOUSE_EXITED, mouseExitedHandler);
		chart.addEventHandler(MouseEvent.MOUSE_RELEASED, mouseReleasedHandler);

	}

	@Override
	protected void chartDisconnected( Chart chart ) {
		chart.removeEventHandler(MouseEvent.MOUSE_RELEASED, mouseReleasedHandler);
		chart.removeEventHandler(MouseEvent.MOUSE_EXITED, mouseExitedHandler);
		chart.removeEventHandler(MouseEvent.MOUSE_ENTERED, mouseEnteredHandler);
		chart.removeEventHandler(MouseEvent.MOUSE_CLICKED, mouseClickedHandler);
		chart.removeEventHandler(MouseEvent.DRAG_DETECTED, dragDetectedHandler);
	}

	private void dragDetected( MouseEvent event ) {

		if ( event.isPrimaryButtonDown()
		  && event.isShortcutDown()
		  && !event.isAltDown() ) {

			if ( isInsidePlotArea(event) ) {

				//	Set the ZOOM cursor...
				Chart chart = getChart();

				originalCursor = chart.getScene().getCursor();

				chart.getScene().setCursor(ZOOM_CURSOR);

				//	Capture initial zoom location.
				zoomStartPoint = getLocationInPlotArea(event);

//	TODO:CR Not clear if useful. If yes, not clear if must be applied to Panner too.
//				if ( getChart() instanceof DensityChartFX<?, ?> ) {
//					zoomStartPoint = zoomStartPoint.add(new Point2D(getXValueAxis().getLayoutX(), 0.0));
//				}

				oldMouseXLocation = zoomStartPoint.getX();
				oldMouseYLocation = zoomStartPoint.getY();

				//	Initialize the zoom rectangle and show it...
				zoomRectangle.setX(oldMouseXLocation);
				zoomRectangle.setY(oldMouseYLocation);
				zoomRectangle.setWidth(0);
				zoomRectangle.setHeight(0);
				zoomRectangle.setVisible(true);

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

		if ( isZoomOngoing() && isInsidePlotArea(event) ) {

			zoomEndPoint = getLocationInPlotArea(event);

//	TODO:CR Not clear if useful. If yes, not clear if must be applied to Panner too.
//			if ( getChart() instanceof DensityChartFX<?, ?> ) {
//				zoomEndPoint = zoomEndPoint.add(new Point2D(getXValueAxis().getLayoutX(), 0.0));
//			}

			double mouseXLocation = zoomEndPoint.getX();
			double mouseYLocation = zoomEndPoint.getY();

			if ( !shiftWasDown && event.isShiftDown() ) {

				shiftWasDown = true;
				overriddenConstraints = getConstraints();

				if ( overriddenConstraints == AxisConstraints.X_AND_Y ) {
					if ( Math.abs(mouseXLocation - oldMouseXLocation) > Math.abs(mouseYLocation - oldMouseYLocation) ) {
						overriddenConstraints = AxisConstraints.X_ONLY;
					} else {
						overriddenConstraints = AxisConstraints.Y_ONLY;
					}
				}

			} else if ( shiftWasDown && !event.isShiftDown() ) {
				shiftWasDown = false;
				overriddenConstraints = getConstraints();
			}

			Bounds plotAreaBounds = getPlotAreaBounds();
			double zoomRectX = plotAreaBounds.getMinX();
			double zoomRectY = plotAreaBounds.getMinY();
			double zoomRectWidth = plotAreaBounds.getWidth();
			double zoomRectHeight = plotAreaBounds.getHeight();

			if ( overriddenConstraints == AxisConstraints.X_ONLY || overriddenConstraints == AxisConstraints.X_AND_Y ) {
				zoomRectX = Math.min(zoomStartPoint.getX(), zoomEndPoint.getX());
				zoomRectWidth = Math.abs(zoomEndPoint.getX() - zoomStartPoint.getX());
			}

			if ( overriddenConstraints == AxisConstraints.Y_ONLY || overriddenConstraints == AxisConstraints.X_AND_Y ) {
				zoomRectY = Math.min(zoomStartPoint.getY(), zoomEndPoint.getY());
				zoomRectHeight = Math.abs(zoomEndPoint.getY() - zoomStartPoint.getY());
			}

			zoomRectangle.setX(zoomRectX);
			zoomRectangle.setY(zoomRectY);
			zoomRectangle.setWidth(Math.max(zoomRectWidth, ZOOM_RECT_MIN_SIZE));
			zoomRectangle.setHeight(Math.max(zoomRectHeight, ZOOM_RECT_MIN_SIZE));

			oldMouseXLocation = mouseXLocation;
			oldMouseYLocation = mouseYLocation;

			//	Job done, consume the event...
			event.consume();

		}

	}

	private boolean isZoomOngoing() {
		return ( zoomStartPoint != null );
	}

	private void mouseClicked( MouseEvent event ) {

		if ( event.getButton() == MouseButton.PRIMARY
		  && event.getClickCount() == 2
		  && event.isShortcutDown()
		  && !event.isAltDown()
		  && !event.isShiftDown() ) {

			if ( isInsidePlotArea(event) ) {
				zoomHelper.autoScale();
			}

			//	Job done, consume the event...
			event.consume();

		}

	}

	private void mouseEntered( MouseEvent event ) {

		if ( isZoomOngoing() ) {

			Chart chart = getChart();

			//	Add drag listener...
			chart.addEventHandler(MouseEvent.MOUSE_DRAGGED, draggedHandler);

			//	Set the PAN cursor...
			chart.getScene().setCursor(ZOOM_CURSOR);

		}

	}

	private void mouseExited( MouseEvent event ) {

		if ( isZoomOngoing() ) {

			Chart chart = getChart();

			//	Remove drag listener...
			chart.removeEventHandler(MouseEvent.MOUSE_DRAGGED, draggedHandler);

			//	Restore original cursor...
			chart.getScene().setCursor(originalCursor);

		}

	}

	private void mouseReleased( MouseEvent event ) {

		if ( isZoomOngoing() ) {

			zoomRectangle.setVisible(false);

			if ( zoomRectangle.getWidth() >= ZOOM_RECT_MIN_SIZE && zoomRectangle.getHeight() >= ZOOM_RECT_MIN_SIZE ) {
				zoomHelper.zoom(
					getXValueForDisplayAsDouble(zoomRectangle.getX()),
					getXValueForDisplayAsDouble(zoomRectangle.getX() + zoomRectangle.getWidth()),
					getYValueForDisplayAsDouble(zoomRectangle.getY() + zoomRectangle.getHeight()),
					getYValueForDisplayAsDouble(zoomRectangle.getY()),
					isAnimated(),
					getAnimationDuration()
				);
			}

			//	Clear starting point...
			zoomStartPoint = null;
			zoomEndPoint = null;
			shiftWasDown = false;
			overriddenConstraints = getConstraints();

			//	Remove drag listener...
			Chart chart = getChart();

			chart.removeEventHandler(MouseEvent.MOUSE_DRAGGED, draggedHandler);

			//	Restore original cursor...
			chart.getScene().setCursor(originalCursor);

			//	Job done, consume the event...
			event.consume();

		}

	}

	private void zoom ( ZoomEvent event ) {

		if ( !isZoomOngoing() && !event.isAltDown() ) {
			if ( event.getZoomFactor() > 1 ) {
				zoomHelper.zoomIn(false);
			} else {
				zoomHelper.zoomOut(true);
			}

			//	Job done, consume the event...
			event.consume();

		}

	}

	private void zoomFinished ( ZoomEvent event ) {

		if ( zooming ) {

			zooming = false;

			//	Job done, consume the event...
			event.consume();

		}

	}

	private void zoomStarted ( ZoomEvent event ) {

		if ( !isZoomOngoing() && !event.isAltDown() ) {

			zooming = true;

			//	Capture undo status...
			Chart chart = getChart();

			ChartUndoManager.get(chart).captureUndoable(this);

			//	Job done, consume the event...
			event.consume();

		}

	}

}
