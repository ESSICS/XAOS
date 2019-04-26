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


import chart.Plugin;
import java.text.Format;
import javafx.collections.ObservableList;
import javafx.scene.chart.Chart;
import se.europeanspallationsource.xaos.ui.control.NavigatorPopup;
import se.europeanspallationsource.xaos.ui.plot.impl.plugins.AbscissaCursorDisplay;
import se.europeanspallationsource.xaos.ui.plot.impl.plugins.AreaValueCursorDisplay;
import se.europeanspallationsource.xaos.ui.plot.impl.plugins.CursorDisplay.Position;
import se.europeanspallationsource.xaos.ui.plot.impl.plugins.CursorLines;
import se.europeanspallationsource.xaos.ui.plot.impl.plugins.DataPointCursorDisplay;
import se.europeanspallationsource.xaos.ui.plot.impl.plugins.KeyboardAccelerators;
import se.europeanspallationsource.xaos.ui.plot.impl.plugins.Navigator;
import se.europeanspallationsource.xaos.ui.plot.impl.plugins.OrdinateCursorDisplay;
import se.europeanspallationsource.xaos.ui.plot.impl.plugins.Panner;
import se.europeanspallationsource.xaos.ui.plot.impl.plugins.Zoomer;
import se.europeanspallationsource.xaos.ui.plot.plugins.AxisConstrained.AxisConstraints;


/**
 * This class consists exclusively of static methods that return {@link Chart}'s
 * plugins.
 *
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class Plugins {

	/**
	 * Returns a plugin displaying at the {@link Position#BOTTOM} the abscissa
	 * value at mouse cursor position.
	 *
	 * @return An abscissa cursor display.
	 */
	public static Plugin abscissaCursorDisplay() {
		return new AbscissaCursorDisplay();
	}

	/**
	 * Returns a plugin displaying at the given {@code position} the abscissa
	 * value at mouse cursor position.
	 *
	 * @param position Where the abscissa value must be displayed.
	 * @return An abscissa cursor display.
	 */
	public static Plugin abscissaCursorDisplay( Position position ) {
		return new AbscissaCursorDisplay(position);
	}

	/**
	 * Returns a plugin displaying at the given {@code position} the abscissa
	 * value at mouse cursor position, using the given {@code formatter} to
	 * format the abscissa value.
	 *
	 * @param position  Where the abscissa value must be displayed.
	 * @param formatter The formatter used to format the abscissa value.
	 * @return An abscissa cursor display.
	 */
	public static Plugin abscissaCursorDisplay( Position position, Format formatter ) {
		return new AbscissaCursorDisplay(position, formatter);
	}

	/**
	 * Returns a plugin displaying at the {@link Position#TOP} the chart area
	 * value under mouse cursor position.
	 *
	 * @return An abscissa cursor display.
	 */
	public static Plugin areaValueCursorDisplay() {
		return new AreaValueCursorDisplay();
	}

	/**
	 * Returns a plugin displaying at the given {@code position} the chart area
	 * value under mouse cursor position.
	 *
	 * @param position Where the chart area value must be displayed.
	 * @return An abscissa cursor display.
	 */
	public static Plugin areaValueCursorDisplay( Position position ) {
		return new AreaValueCursorDisplay(position);
	}

	/**
	 * Returns a plugin displaying at the given {@code position} the chart area
	 * value under mouse cursor position, using the given {@code formatter} to
	 * format the area value.
	 *
	 * @param position  Where the chart area value must be displayed.
	 * @param formatter The formatter used to format the abscissa and ordinate
	 *                  values of the picked chart data point.
	 * @return An abscissa cursor display.
	 */
	public static Plugin areaValueCursorDisplay( Position position, Format formatter ) {
		return new AreaValueCursorDisplay(position, formatter);
	}

	/**
	 * Returns all available {@link Plugin}s, in the right order. It is
	 * equivalent to:
	 * <pre>
	 *   Plugin plugins = new Plugin[] {
	 *     navigator(),
	 *     keyboardAccelerators(),
	 *     panner(),
	 *     zoomer(),
	 *     cursorLines(),
	 *     abscissaCursorDisplay(),
	 *     ordinateCursorDisplay(),
	 *     dataPointCursorDisplay(),
	 *     areaValueCursorDisplay()
	 *   };
	 * </pre>
	 *
	 * @return All available {@link Plugin}s, in the right order, to be
	 *         used as parameter for the {@link ObservableList#addAll(java.lang.Object...)}
	 *         method of the lister returned by {@link Pluggable#getPlugins()}.
	 * @see #keyboardAccelerators
	 * @see #navigator()
	 * @see #panner()
	 */
	public static Plugin[] all() {
		return new Plugin[] {
			navigator(),
			keyboardAccelerators(),
			panner(),
			zoomer(),
			cursorLines(),
			abscissaCursorDisplay(),
			ordinateCursorDisplay(),
			dataPointCursorDisplay(),
			areaValueCursorDisplay()
//			new PropertyMenu()
//			new ErrorBars()
		};
	}

	/**
	 * Returns a plugin that shows horizontal and vertical lines drawn on the
	 * plot area, crossing at the mouse cursor location.
	 *
	 * @return A cursor lines plugin.
	 */
	public static Plugin cursorLines() {
		return new CursorLines();
	}

	/**
	 * Returns a plugin displaying at the {@link Position#CENTER} the
	 * coordinates of the closest chart data point at mouse cursor position.
	 *
	 * @return An abscissa cursor display.
	 */
	public static Plugin dataPointCursorDisplay() {
		return new DataPointCursorDisplay();
	}

	/**
	 * Returns a plugin displaying at the given {@code position} the
	 * coordinates of the closest chart data point at mouse cursor position.
	 *
	 * @param position Where the chart data point must be displayed.
	 * @return An abscissa cursor display.
	 */
	public static Plugin dataPointCursorDisplay( Position position ) {
		return new DataPointCursorDisplay(position);
	}

	/**
	 * Returns a plugin displaying at the given {@code position} the
	 * coordinates of the closest chart data point at mouse cursor position,
	 * using the given {@code formatter} to format the abscissa and ordinate
	 * values of the picked chart data point.
	 *
	 * @param position  Where the chart data point must be displayed.
	 * @param formatter The formatter used to format the abscissa and ordinate
	 *                  values of the picked chart data point.
	 * @return An abscissa cursor display.
	 */
	public static Plugin dataPointCursorDisplay( Position position, Format formatter ) {
		return new DataPointCursorDisplay(position, formatter);
	}

	/**
	 * Returns a plugin that allow panner and zoom operations to be performed
	 * using keyboards accelerators (Shortcut stands for Ctrl on Windows or
	 * Linux, and Command on macOS):
	 * <table>
	 * <caption>&nbsp;</caption>
	 * <tr><td>Panner Down</td><td>Shortcut+DOWN</td></tr>
	 * <tr><td>Panner Left</td><td>Shortcut+LEFT</td></tr>
	 * <tr><td>Panner Right</td><td>Shortcut+RIGHT</td></tr>
	 * <tr><td>Panner Up</td><td>Shortcut+UP</td></tr>
	 * <tr><td>Redo</td><td>Shift+Shortcut+Z</td></tr>
	 * <tr><td>Undo</td><td>Shortcut+Z</td></tr>
	 * <tr><td>Zoom In</td><td>Shift+Shortcut+UP</td></tr>
	 * <tr><td>Zoom Out</td><td>Shift+Shortcut+DOWN</td></tr>
	 * <tr><td>Zoom To One</td><td>Shortcut+EQUALS</td></tr>
	 * </table>
	 *
	 * @return A navigator plugin.
	 */
	public static Plugin keyboardAccelerators() {
		return new KeyboardAccelerators();
	}

	/**
	 * Returns a plugin that opens a {@link NavigatorPopup} when ALT key is
	 * pressed within the chart.
	 * <p>
	 * The popup can be opened pressing ALT on a focused chart. If the popup
	 * is still opened, pressing ALT again will move it under the current mouse
	 * cursor position. The popup can be closed pressing ESC or clicking outside
	 * it.</p>
	 * <p>
	 * Once the popup is open, buttons can be navigated with TAB, and pressing
	 * SPACE/ENTER will press the button. Button can be activated pressing keyboard
	 * accelerators (Shortcut stands for Ctrl on Windows or Linux, and Command on
	 * macOS):</p>
	 * <table>
	 * <caption>&nbsp;</caption>
	 * <tr><td>Panner Down</td><td>Shortcut+DOWN</td></tr>
	 * <tr><td>Panner Left</td><td>Shortcut+LEFT</td></tr>
	 * <tr><td>Panner Right</td><td>Shortcut+RIGHT</td></tr>
	 * <tr><td>Panner Up</td><td>Shortcut+UP</td></tr>
	 * <tr><td>Redo</td><td>Shift+Shortcut+Z</td></tr>
	 * <tr><td>Undo</td><td>Shortcut+Z</td></tr>
	 * <tr><td>Zoom In</td><td>Shift+Shortcut+UP</td></tr>
	 * <tr><td>Zoom Out</td><td>Shift+Shortcut+DOWN</td></tr>
	 * <tr><td>Zoom To One</td><td>Shortcut+EQUALS</td></tr>
	 * </table>
	 *
	 * @return A navigator plugin.
	 */
	public static Plugin navigator() {
		return new Navigator();
	}

	/**
	 * Returns a plugin displaying at the {@link Position#BOTTOM} the ordinate
	 * value at mouse cursor position.
	 *
	 * @return An ordinate cursor display.
	 */
	public static Plugin ordinateCursorDisplay() {
		return new OrdinateCursorDisplay();
	}

	/**
	 * Returns a plugin displaying at the given {@code position} the ordinate
	 * value at mouse cursor position.
	 *
	 * @param position Where the ordinate value must be displayed.
	 * @return An ordinate cursor display.
	 */
	public static Plugin ordinateCursorDisplay( Position position ) {
		return new OrdinateCursorDisplay(position);
	}

	/**
	 * Returns a plugin displaying at the given {@code position} the ordinate
	 * value at mouse cursor position, using the given {@code formatter} to
	 * format the ordinate value.
	 *
	 * @param position  Where the ordinate value must be displayed.
	 * @param formatter The formatter used to format the ordinate value.
	 * @return An ordinate cursor display.
	 */
	public static Plugin ordinateCursorDisplay( Position position, Format formatter ) {
		return new OrdinateCursorDisplay(position, formatter);
	}

	/**
	 * Return a plugin that allows panning-by-dragging operation on both axis.
	 * <p>
	 * Pressing SHIFT when panning will restrict the movements to
	 * {@link AxisConstraints#X_ONLY X_ONLY} or {@link AxisConstraints#Y_ONLY Y_ONLY}
	 * according to the direction of the initial movement of the gesture.</p>
	 * <p>
	 * Scroll can be performed with a track-pad or a mouse wheel. Shift will lock
	 * the scroll on the prominent axis, while Shortcut (Command on macOS, Ctrl
	 * on Windows and Linux) will swap the axis (so with a mouse wheel will be
	 * possible to scroll on both axis).</p>
	 *
	 * @return A panner plugin.
	 */
	public static Plugin panner() {
		return new Panner();
	}

	/**
	 * Return a plugin that allows panning-by-dragging operation along the
	 * specified axis.
	 * <p>
	 * If {@code constraints} parameter is set to
	 * {@link AxisConstraints#X_AND_Y X_AND_Y}, then pressing SHIFT when
	 * panning will restrict the movement to {@link AxisConstraints#X_ONLY X_ONLY}
	 * or {@link AxisConstraints#Y_ONLY Y_ONLY} according to the direction of the
	 * initial movement of the gesture.</p>
	 * <p>
	 * Scroll can be performed with a track-pad or a mouse wheel. Shift will lock
	 * the scroll on the prominent axis, while Shortcut (Command on macOS, Ctrl
	 * on Windows and Linux) will swap the axis (so with a mouse wheel will be
	 * possible to scroll on both axis).</p>
	 *
	 * @param constraints Initial value for the panner plugin's
	 *                    {@link Panner#constraintsProperty() constraints}
	 *                    property.
	 * @return A panner plugin.
	 */
	public static Plugin panner( AxisConstraints constraints ) {
		return new Panner(constraints);
	}

	/**
	 * Return a plugin that allows zooming-by-dragging operation along the
	 * specified axis.
	 * <p>
	 * The plugin is activated by pressing SHORTCUT (COMMAND on macOS, CTRL on
	 * Windows and Linux) and then dragging the rectangle to zoom in, or
	 * double-clicking to auto-scale.</p>
	 * <p>
	 * Pressing also SHIFT will constrain zooming on a single axis according to
	 * the longest side of the drawn rectangle.</p>
	 *
	 * @return A zoomer plugin.
	 */
	public static Plugin zoomer() {
		return new Zoomer();
	}

	/**
	 * Return a plugin that allows zooming-by-dragging operation along the
	 * specified axis.
	 * <p>
	 * The plugin is activated by pressing SHORTCUT (COMMAND on macOS, CTRL on
	 * Windows and Linux) and then dragging the rectangle to zoom in, or
	 * double-clicking to auto-scale.</p>
	 * <p>
	 * Pressing also SHIFT will constrain zooming on a single axis according to
	 * the longest side of the drawn rectangle.</p>
	 *
	 * @param constraints Initial value for the
	 *                    {@link Zoomer#constraintsProperty() constraints}
	 *                    property.
	 * @return A zoomer plugin.
	 */
	public static Plugin zoomer( AxisConstraints constraints ) {
		return new Zoomer(constraints);
	}

	/**
	 * Return a plugin that allows zooming-by-dragging operation along the
	 * specified axis.
	 * <p>
	 * The plugin is activated by pressing SHORTCUT (COMMAND on macOS, CTRL on
	 * Windows and Linux) and then dragging the rectangle to zoom in, or
	 * double-clicking to auto-scale.</p>
	 * <p>
	 * Pressing also SHIFT will constrain zooming on a single axis according to
	 * the longest side of the drawn rectangle.</p>
	 *
	 * @param constraints Initial value for the
	 *                    {@link Zoomer#constraintsProperty() constraints}
	 *                    property.
	 * @param animated    {@code true} if the zoom must be animated,
	 *                    {@code false} otherwise.
	 * @return A zoomer plugin.
	 */
	public static Plugin zoomer( AxisConstraints constraints, boolean animated ) {
		return new Zoomer(constraints, animated);
	}

	private Plugins() {
		//	Nothing to do.
	}

}
