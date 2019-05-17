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


import chart.Plugin;
import javafx.beans.binding.Bindings;
import javafx.event.EventHandler;
import javafx.scene.chart.Chart;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import se.europeanspallationsource.xaos.ui.control.NavigatorPopup;
import se.europeanspallationsource.xaos.ui.plot.util.impl.ChartUndoManager;


/**
 * Opens a {@link NavigatorPopup} when ALT key is pressed within the chart.
 * <p>The popup can be opened pressing ALT on a focused chart. If the popup
 * is still opened, pressing ALT again will move it under the current mouse
 * cursor position. The popup can be closed pressing ESC or clicking outside
 * it.</p>
 * <p>
 * Once the popup is open, buttons can be navigated with TAB, and pressing
 * SPACE/ENTER will press the button. Button can be activated pressing keyboard
 * accelerators (Shortcut stands for Ctrl on Windows or Linux, and Command on
 * macOS):</p>
 * <table>
 *   <caption>&nbsp;</caption>
 *   <tr><td>Pan Down</td><td>Shortcut+DOWN</td></tr>
 *   <tr><td>Pan Left</td><td>Shortcut+LEFT</td></tr>
 *   <tr><td>Pan Right</td><td>Shortcut+RIGHT</td></tr>
 *   <tr><td>Pan Up</td><td>Shortcut+UP</td></tr>
 *   <tr><td>Redo</td><td>Shift+Shortcut+Z</td></tr>
 *   <tr><td>Undo</td><td>Shortcut+Z</td></tr>
 *   <tr><td>Zoom In</td><td>Shift+Shortcut+UP</td></tr>
 *   <tr><td>Zoom Out</td><td>Shift+Shortcut+DOWN</td></tr>
 *   <tr><td>Zoom To One</td><td>Shortcut+EQUALS</td></tr>
 * </table>
 *
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( "ClassWithoutLogger" )
public final class Navigator extends Plugin {

	private double cursorScreenX;
	private double cursorScreenY;
	private final EventHandler<KeyEvent>   keyPressedHandler    = this::keyPressed;
	private final EventHandler<MouseEvent> mouseEnteredHandler  = this::mouseEntered;
	private final EventHandler<MouseEvent> mouseExitedHandler   = this::mouseExited;
	private final EventHandler<MouseEvent> mouseMovedHandler    = this::mouseMoved;
	private final EventHandler<MouseEvent> mouseReleasedHandler = this::mouseReleased;
	private final PanHelper panHelper = new PanHelper(this);
	private final NavigatorPopup popup = new NavigatorPopup();
	private final ZoomHelper zoomHelper = new ZoomHelper(this);

	public Navigator() {

		popup.setOnPanDown(e -> panHelper.panDown());
		popup.setOnPanLeft(e -> panHelper.panLeft());
		popup.setOnPanRight(e -> panHelper.panRight());
		popup.setOnPanUp(e -> panHelper.panUp());

		popup.setOnZoomIn(e -> zoomHelper.zoomIn(true));
		popup.setOnZoomOut(e -> zoomHelper.zoomOut(true));
		popup.setOnZoomToOne(e -> zoomHelper.autoScale());

		popup.setOnRedo(e -> ChartUndoManager.get(getChart()).redo(this));
		popup.setOnUndo(e -> ChartUndoManager.get(getChart()).undo(this));

	}

	@Override
	protected void chartConnected( Chart chart ) {

		//	Using event filters instead of event handlers to capture the event
		//	earlier and block other plugins using it.
		chart.addEventFilter(KeyEvent.KEY_PRESSED, keyPressedHandler);
		chart.addEventHandler(MouseEvent.MOUSE_ENTERED, mouseEnteredHandler);
		chart.addEventHandler(MouseEvent.MOUSE_EXITED, mouseExitedHandler);
		chart.addEventHandler(MouseEvent.MOUSE_RELEASED, mouseReleasedHandler);

		popup.redoDisabledProperty().bind(Bindings.not(ChartUndoManager.get(chart).redoableProperty()));
		popup.undoDisabledProperty().bind(Bindings.not(ChartUndoManager.get(chart).undoableProperty()));

	}

	@Override
	protected void chartDisconnected( Chart chart ) {

		popup.undoDisabledProperty().unbind();
		popup.redoDisabledProperty().unbind();

		chart.removeEventHandler(MouseEvent.MOUSE_RELEASED, mouseReleasedHandler);
		chart.removeEventHandler(MouseEvent.MOUSE_MOVED, mouseMovedHandler);
		chart.removeEventHandler(MouseEvent.MOUSE_EXITED, mouseExitedHandler);
		chart.removeEventHandler(MouseEvent.MOUSE_ENTERED, mouseEnteredHandler);
		chart.removeEventFilter(KeyEvent.KEY_PRESSED, keyPressedHandler);

	}

	private void keyPressed ( KeyEvent event ) {
		switch ( event.getCode() ) {
			case ALT:
				//	If the popup is hidden then show it, otherwise move it at
				//	the current mouse cursor position.
				popup.show(getChart(), cursorScreenX, cursorScreenY);
				event.consume();
				break;
			case DOWN:
			case LEFT:
			case RIGHT:
			case UP:
				//	Blobk all other plugins using those keys. when the popup is
				//	visible.
				if ( popup.isShowing() ) {
					event.consume();
				}
				break;
		}
	}

	private void mouseEntered( MouseEvent event ) {
		getChart().addEventHandler(MouseEvent.MOUSE_MOVED, mouseMovedHandler);
	}

	private void mouseExited( MouseEvent event ) {
		getChart().removeEventHandler(MouseEvent.MOUSE_MOVED, mouseMovedHandler);
	}

	private void mouseMoved( MouseEvent event ) {
		cursorScreenX = event.getScreenX();
		cursorScreenY = event.getScreenY();
	}

	private void mouseReleased( MouseEvent event ) {
		if ( popup.isShowing() ) {
			popup.hide();
		}
	}

}
