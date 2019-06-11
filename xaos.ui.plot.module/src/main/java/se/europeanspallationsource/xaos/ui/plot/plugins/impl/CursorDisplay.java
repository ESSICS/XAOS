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


import chart.DensityChartFX;
import chart.Plugin;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.chart.ValueAxis;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import se.europeanspallationsource.xaos.ui.plot.plugins.AbstractCursorPlugin;


/**
 * A {@link Plugin} displaying a {@link Label} next to the mouse cursor, showing
 * some text.
 * <p>
 * The display's relative position to the mouse cursor can be adjusted using the
 * {@link #positionProperty() position} property.</p>
 *
 * @author claudio.rosati@esss.se
 * @css.class {@code chart-cursor-display}
 */
@SuppressWarnings( "ClassWithoutLogger" )
public abstract class CursorDisplay extends AbstractCursorPlugin {

	private static final int OFFSET = 5;

	private final Label label = new Label();

	/* *********************************************************************** *
	 * START OF JAVAFX PROPERTIES                                              *
	 * *********************************************************************** */

	/*
	 * ---- position -----------------------------------------------------------
	 */
	private final ObjectProperty<Position> position = new SimpleObjectProperty<>(this, "position", Position.CENTER);

	/**
	 * @return The {@link Position} of the cursor display.
	 */
	public final ObjectProperty<Position> positionProperty() {
		return position;
	}

	public final Position getPosition() {
		return positionProperty().get();
	}

	public final void setPosition( Position value ) {
		positionProperty().set(value);
	}

	/* *********************************************************************** *
	 * END OF JAVAFX PROPERTIES                                                *
	 * *********************************************************************** */

	/**
	 * Creates a new instance of this class, where the display's position is
	 * set to {@link Position#CENTER}.
	 *
	 * @param name The display name of this plugin.
	 */
	public CursorDisplay( String name ) {
		this(name, Position.CENTER);
	}

	/**
	 * Creates a new instance of this class, where the display's position is
	 * set to the given one.
	 * 
	 * @param name     The display name of this plugin.
	 * @param position The position of the cursor display.
	 */
	public CursorDisplay( String name, Position position ) {

		super(name);

		label.getStyleClass().add("chart-cursor-display");
		label.setManaged(false);

		getPlotChildren().add(label);
		setPosition(position);

	}

	@Override
	protected void boundsChanged() {

		Point2D mouseLocation = getSceneMouseLocation();

		if ( mouseLocation != null ) {
			performMove(mouseLocation);
		}

	}

	@Override
	protected void dragDetected( MouseEvent event ) {
		label.setVisible(false);
	}

	/**
	 * @return The {@link Label} used to display values at the mouse cursor.
	 */
	protected Label getDisplay() {
		return label;
	}

	@Override
	protected void mouseMove( MouseEvent event ) {
		super.mouseMove(event);
		performMove(getSceneMouseLocation());
	}

	private void performMove( Point2D sceneMouseLocation ) {

		if ( isInsidePlotArea(sceneMouseLocation) ) {

			boolean hideLabel = false;
			Point2D mouseLocation = getLocationInPlotArea(sceneMouseLocation);
			String text = textAtPosition(mouseLocation);

			if ( text == null ) {
				hideLabel = true;
			} else {

				//	Update label content...
				label.setText(text);

				double labelWidth = label.prefWidth(-1);
				double labelHeight = label.prefHeight(labelWidth);

				//	Position the label...
				Bounds  plotAreaBounds = getPlotAreaBounds();
				double  left           = 0;
				double  right          = plotAreaBounds.getWidth();
				double  top            = 0;
				double  bottom         = plotAreaBounds.getHeight();

				if ( getChart() instanceof DensityChartFX<?, ?> ) {

					ValueAxis<?> xAxis = getXValueAxis();
					ValueAxis<?> yAxis = getYValueAxis();

					mouseLocation = mouseLocation.add(new Point2D(xAxis.getLayoutX(), yAxis.getLayoutY()));
					left          = xAxis.getLayoutX();
					right         = xAxis.getWidth() + xAxis.getLayoutX();
					top           = yAxis.getLayoutY();
					bottom        = yAxis.getHeight() + yAxis.getLayoutY();

				}

				double mouseX  = mouseLocation.getX();
				double mouseY  = mouseLocation.getY();
				double centerX = ( left + right ) / 2.0;
				double centerY = ( top + bottom ) / 2.0;

				switch ( getPosition() ) {
					case BOTTOM:
						label.resizeRelocate(
							( mouseX > centerX ) ? mouseX - OFFSET - labelWidth : mouseX + OFFSET,
							bottom - OFFSET - labelHeight,
							labelWidth,
							labelHeight
						);
						break;
					case LEFT:
						label.resizeRelocate(
							left + OFFSET,
							( mouseY > centerY ) ? mouseY - OFFSET - labelHeight : mouseY + OFFSET,
							labelWidth,
							labelHeight
						);
						break;
					case RIGHT:
						label.resizeRelocate(
							right - OFFSET - labelWidth,
							( mouseY > centerY ) ? mouseY - OFFSET - labelHeight : mouseY + OFFSET,
							labelWidth,
							labelHeight
						);
						break;
					case TOP:
						label.resizeRelocate(
							( mouseX > centerX ) ? mouseX - OFFSET - labelWidth : mouseX + OFFSET,
							top + OFFSET,
							labelWidth,
							labelHeight
						);
						break;
					case CENTER:
					default:
						label.resizeRelocate(
							( mouseX > centerX ) ? mouseX - OFFSET - labelWidth  : mouseX + OFFSET,
							( mouseY > centerY ) ? mouseY - OFFSET - labelHeight : mouseY + OFFSET,
							labelWidth,
							labelHeight
						);
						break;
				}

				label.requestLayout();

			}

			if ( !label.isVisible() && !hideLabel ) {
				label.setVisible(true);
			} else if ( label.isVisible() && hideLabel ) {
				label.setVisible(false);
			}

		} else {
			label.setVisible(false);
		}

	}

	/**
	 * Returns the text to be displayed at the given mouse cursor location.
	 *
	 * @param mouseLocation The current mouse cursor location where some text
	 *                      must be displayed.
	 * @return A text {@link String} or {@code null}.
	 */
	protected abstract String textAtPosition( Point2D mouseLocation );

	/**
	 * Defines the possible positions of the cursor display relative to the
	 * current mouse cursor position and chart area.
	 * <p>
	 * The display at the {@link #TOP},{@link #CENTER}, and {@link #BOTTOM} 
	 * will be placed at the left/right side of the mouse cursor depending on
	 * its position relative to the center of the chart area.</p>
	 * <pre>
	 *
	 *                       │ TOP
	 *                       │
	 *  LEFT                 │ CENTER         RIGHT
	 * ──────────────────────┼──────────────────────
	 *                       │
	 *                       │
	 *                       │ BOTTOM
	 * </pre>
	 */
	@SuppressWarnings( "PublicInnerClass" )
	public static enum Position {

		/**
		 * Cursor display be positioned at the left/right side of the mouse
		 * cursor position and at the bottom side of the chart area.
		 * <pre>
		 *
		 *                       │
		 *                       │
		 *                       │
		 * ──────────────────────┼──────────────────────
		 *                       │
		 *                       │
		 *                       │ BOTTOM
		 * </pre>
		 */
		BOTTOM,

		/**
		 * Cursor display be positioned just above and at the left/right
		 * side of the mouse cursor position.
		 * <pre>
		 *
		 *                       │
		 *                       │
		 *                       │ CENTER
		 * ──────────────────────┼──────────────────────
		 *                       │
		 *                       │
		 *                       │
		 * </pre>
		 */
		CENTER,

		/**
		 * Cursor display be positioned just above the mouse cursor
		 * position and at the left side of the chart area.
		 * <pre>
		 *
		 *                       │
		 *                       │
		 *  LEFT                 │
		 * ──────────────────────┼──────────────────────
		 *                       │
		 *                       │
		 *                       │
		 * </pre>
		 */
		LEFT,

		/**
		 * Cursor display be positioned just above the mouse cursor
		 * position and at the right side of the chart area.
		 * <pre>
		 *
		 *                       │
		 *                       │
		 *                       │                RIGHT
		 * ──────────────────────┼──────────────────────
		 *                       │
		 *                       │
		 *                       │
		 * </pre>
		 */
		RIGHT,

		/**
		 * Cursor display be positioned at the left/right side of the mouse
		 * cursor position and at the top side of the chart area.
		 * <pre>
		 *
		 *                       │ TOP
		 *                       │
		 *                       │
		 * ──────────────────────┼──────────────────────
		 *                       │
		 *                       │
		 *                       │
		 * </pre>
		 */
		TOP

	}

}
