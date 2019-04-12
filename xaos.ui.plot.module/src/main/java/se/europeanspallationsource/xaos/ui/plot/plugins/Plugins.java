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
import javafx.collections.ObservableList;
import javafx.scene.chart.Chart;
import se.europeanspallationsource.xaos.ui.control.NavigatorPopup;
import se.europeanspallationsource.xaos.ui.plot.impl.plugins.KeyboardAccelerators;
import se.europeanspallationsource.xaos.ui.plot.impl.plugins.Navigator;
import se.europeanspallationsource.xaos.ui.plot.impl.plugins.Pan;
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
	 * @return All available {@link Plugin}s, in the right order, to be
	 *         used as parameter for the {@link ObservableList#addAll(java.lang.Object...)}
	 *         method of the lister returned by {@link Pluggable#getPlugins()}.
	 */
	public static Plugin[] all() {
		return new Plugin[] {
			navigator(),
			keyboardAccelerators(),
			pan()
//			new Zoom(),
//			new CoordinatesLines(),
//			new CoordinatesLabel(),
//			new DataPointTooltip(),
//			new AreaValueTooltip(),
//			new PropertyMenu()
		};
	}

	/**
	 * Returns a plugin that allow pan and zoom operations to be performed using
	 * keyboards accelerators (Shortcut stands for Ctrl on Windows or Linux, and
	 * Command on macOS):
	 * <table>
	 * <caption>&nbsp;</caption>
	 * <tr><td>Pan Down</td><td>Shortcut+DOWN</td></tr>
	 * <tr><td>Pan Left</td><td>Shortcut+LEFT</td></tr>
	 * <tr><td>Pan Right</td><td>Shortcut+RIGHT</td></tr>
	 * <tr><td>Pan Up</td><td>Shortcut+UP</td></tr>
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
	 * <tr><td>Pan Down</td><td>Shortcut+DOWN</td></tr>
	 * <tr><td>Pan Left</td><td>Shortcut+LEFT</td></tr>
	 * <tr><td>Pan Right</td><td>Shortcut+RIGHT</td></tr>
	 * <tr><td>Pan Up</td><td>Shortcut+UP</td></tr>
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
	 * Return a plugin that allows panning-by-dragging operation on both axis.
	 *
	 * @return A pan plugin.
	 */
	public static Plugin pan() {
		return new Pan();
	}

	/**
	 * Return a plugin that allows panning-by-dragging operation along the
	 * specified axis.
	 *
	 * @param constraints Initial value for the pan plugin's
	 *                    {@link Pan#constraintsProperty() constraints} property.
	 * @return A pan plugin.
	 */
	public static Plugin pan( AxisConstraints constraints ) {
		return new Pan();
	}

	private Plugins() {
		//	Nothing to do.
	}

}
