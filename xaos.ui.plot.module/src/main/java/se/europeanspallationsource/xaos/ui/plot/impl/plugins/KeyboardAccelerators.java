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


import chart.XYChartPlugin;
import javafx.event.EventHandler;
import javafx.scene.chart.Chart;
import javafx.scene.input.KeyEvent;
import se.europeanspallationsource.xaos.ui.plot.impl.util.ChartUndoManager;

import static se.europeanspallationsource.xaos.ui.control.NavigatorController.PAN_DOWN_ACCELERATOR;
import static se.europeanspallationsource.xaos.ui.control.NavigatorController.PAN_LEFT_ACCELERATOR;
import static se.europeanspallationsource.xaos.ui.control.NavigatorController.PAN_RIGHT_ACCELERATOR;
import static se.europeanspallationsource.xaos.ui.control.NavigatorController.PAN_UP_ACCELERATOR;
import static se.europeanspallationsource.xaos.ui.control.NavigatorController.REDO_ACCELERATOR;
import static se.europeanspallationsource.xaos.ui.control.NavigatorController.UNDO_ACCELERATOR;
import static se.europeanspallationsource.xaos.ui.control.NavigatorController.ZOOM_IN_ACCELERATOR;
import static se.europeanspallationsource.xaos.ui.control.NavigatorController.ZOOM_OUT_ACCELERATOR;
import static se.europeanspallationsource.xaos.ui.control.NavigatorController.ZOOM_TO_ONE_ACCELERATOR;


/**
 * Allow pan and zoom operations to be performed using keyboards accelerators
 * (Shortcut stands for Ctrl on Windows or Linux, and Command on macOS):
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
 * @author Reuben Lindroos
 * @author claudio.rosati@esss.se
 */
public class KeyboardAccelerators extends XYChartPlugin {

	private final EventHandler<KeyEvent> keyPresseddHandler = this::keyPressed;
	private final PanHelper panHelper = new PanHelper(this);
	private final ZoomHelper zoomHelper = new ZoomHelper(this);

	@Override
	protected void chartConnected( Chart newChart ) {
		newChart.addEventHandler(KeyEvent.KEY_PRESSED, keyPresseddHandler);
	}

	@Override
	protected void chartDisconnected( Chart oldChart ) {
		oldChart.removeEventHandler(KeyEvent.KEY_PRESSED, keyPresseddHandler);
	}

	private void keyPressed ( KeyEvent event ) {

		Chart chart = getChart();

		if ( !chart.isDisabled() && chart.isFocused() ) {

			if ( PAN_DOWN_ACCELERATOR.match(event) ) {
				panHelper.panDown();
			} else if ( PAN_LEFT_ACCELERATOR.match(event) ) {
				panHelper.panLeft();
			} else if ( PAN_RIGHT_ACCELERATOR.match(event) ) {
				panHelper.panRight();
			} else if ( PAN_UP_ACCELERATOR.match(event) ) {
				panHelper.panUp();
			} else if ( REDO_ACCELERATOR.match(event) ) {
				ChartUndoManager.get(chart).redo(this);
			} else if ( UNDO_ACCELERATOR.match(event) ) {
				ChartUndoManager.get(chart).undo(this);
			} else if ( ZOOM_IN_ACCELERATOR.match(event) ) {
				zoomHelper.zoomIn();
			} else if ( ZOOM_OUT_ACCELERATOR.match(event) ) {
				zoomHelper.zoomOut();
			} else if ( ZOOM_TO_ONE_ACCELERATOR.match(event) ) {
				zoomHelper.autoScale();
			} else {
				//	Event must not be consumed.
				return;
			}

			event.consume();

		}

	}

}
