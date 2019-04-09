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


import chart.XYChartPlugin;
import javafx.beans.binding.Bindings;
import javafx.event.EventHandler;
import javafx.scene.chart.Chart;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import se.europeanspallationsource.xaos.ui.control.NavigatorPopup;
import se.europeanspallationsource.xaos.ui.plot.impl.util.ChartUndoManager;


/**
 * Opens a {@link NavigatorPopup} when ALT key is pressed within the chart.
 * <p>The popup can be opened pressing ALT on a focused chart. If the popup
 * is still opened, pressing ALT again will move it under the current mouse
 * cursor position. The popup can be closed pressing ESC or clicking outside
 * it.</p>
 *
 * @author Reuben Lindroos
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( "ClassWithoutLogger" )
public final class Navigator extends XYChartPlugin {

	private double cursorScreenX;
	private double cursorScreenY;
	private final EventHandler<KeyEvent> keyPressedHandler = this::keyPressed;
	private final EventHandler<MouseEvent> mouseEnteredHandler = this::mouseEntered;
	private final EventHandler<MouseEvent> mouseExitedHandler = this::mouseExited;
	private final EventHandler<MouseEvent> mouseMovedHandler = this::mouseMoved;
	private final EventHandler<MouseEvent> mouseReleasedHandler = this::mouseReleased;
	private final NavigatorPopup popup = new NavigatorPopup();

	public Navigator() {

		popup.setOnPanDown(e -> panDown());
		popup.setOnPanLeft(e -> panLeft());
		popup.setOnPanRight(e -> panRight());
		popup.setOnPanUp(e -> panUp());

		popup.setOnZoomIn(e -> zoomIn());
		popup.setOnZoomOut(e -> zoomOut());
		popup.setOnZoomToOne(e -> autoScale());

		popup.setOnRedo(e -> ChartUndoManager.get(getChart()).redo(this));
		popup.setOnUndo(e -> ChartUndoManager.get(getChart()).undo(this));

	}

	@Override
	protected void chartConnected( Chart newChart ) {

		//	Using event filters instead of event handlers to capture the event
		//	earlier and block other plugins using it.
		newChart.addEventFilter(KeyEvent.KEY_PRESSED, keyPressedHandler);
		newChart.addEventHandler(MouseEvent.MOUSE_ENTERED_TARGET, mouseEnteredHandler);
		newChart.addEventHandler(MouseEvent.MOUSE_EXITED_TARGET, mouseExitedHandler);
		newChart.addEventHandler(MouseEvent.MOUSE_RELEASED, mouseReleasedHandler);

		popup.redoDisabledProperty().bind(Bindings.not(ChartUndoManager.get(newChart).redoableProperty()));
		popup.undoDisabledProperty().bind(Bindings.not(ChartUndoManager.get(newChart).undoableProperty()));

	}

	@Override
	protected void chartDisconnected( Chart oldChart ) {

		popup.undoDisabledProperty().unbind();
		popup.redoDisabledProperty().unbind();

		oldChart.removeEventHandler(MouseEvent.MOUSE_RELEASED, mouseReleasedHandler);
		oldChart.removeEventHandler(MouseEvent.MOUSE_MOVED, mouseMovedHandler);
		oldChart.removeEventHandler(MouseEvent.MOUSE_EXITED_TARGET, mouseExitedHandler);
		oldChart.removeEventHandler(MouseEvent.MOUSE_ENTERED_TARGET, mouseEnteredHandler);
		oldChart.addEventFilter(KeyEvent.KEY_PRESSED, keyPressedHandler);

	}

	private void autoScale() {
		ChartUndoManager.get(getChart()).captureUndoable(this);
		getXValueAxis().setAutoRanging(true);
		getYValueAxis().setAutoRanging(true);
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

	private void panDown() {

		ChartUndoManager.get(getChart()).captureUndoable(this);

		double plotHeight = getYValueAxis().getUpperBound() - getYValueAxis().getLowerBound();

		getXValueAxis().setAutoRanging(false);
		getYValueAxis().setAutoRanging(false);
		getYValueAxis().setLowerBound(getYValueAxis().getLowerBound() - 0.1 * plotHeight);
		getYValueAxis().setUpperBound(getYValueAxis().getUpperBound() - 0.1 * plotHeight);

	}

	private void panLeft() {

		ChartUndoManager.get(getChart()).captureUndoable(this);

		double plotWidth = getXValueAxis().getUpperBound() - getXValueAxis().getLowerBound();

		getXValueAxis().setAutoRanging(false);
		getYValueAxis().setAutoRanging(false);
		getXValueAxis().setLowerBound(getXValueAxis().getLowerBound() - 0.1 * plotWidth);
		getXValueAxis().setUpperBound(getXValueAxis().getUpperBound() - 0.1 * plotWidth);

	}

	private void panRight() {

		ChartUndoManager.get(getChart()).captureUndoable(this);

		double plotWidth = getXValueAxis().getUpperBound() - getXValueAxis().getLowerBound();

		getXValueAxis().setAutoRanging(false);
		getYValueAxis().setAutoRanging(false);
		getXValueAxis().setLowerBound(getXValueAxis().getLowerBound() + 0.1 * plotWidth);
		getXValueAxis().setUpperBound(getXValueAxis().getUpperBound() + 0.1 * plotWidth);

	}

	private void panUp() {

		ChartUndoManager.get(getChart()).captureUndoable(this);

		double plotHeight = getYValueAxis().getUpperBound() - getYValueAxis().getLowerBound();

		getXValueAxis().setAutoRanging(false);
		getYValueAxis().setAutoRanging(false);
		getYValueAxis().setLowerBound(getYValueAxis().getLowerBound() + 0.1 * plotHeight);
		getYValueAxis().setUpperBound(getYValueAxis().getUpperBound() + 0.1 * plotHeight);

	}

	private void zoomIn() {

		ChartUndoManager.get(getChart()).captureUndoable(this);

		double plotHeight = getYValueAxis().getUpperBound() - getYValueAxis().getLowerBound();
		double plotWidth  = getXValueAxis().getUpperBound() - getXValueAxis().getLowerBound();

		getXValueAxis().setAutoRanging(false);
		getYValueAxis().setAutoRanging(false);
		getXValueAxis().setLowerBound(getXValueAxis().getLowerBound() + 0.1 * plotWidth);
		getXValueAxis().setUpperBound(getXValueAxis().getUpperBound() - 0.1 * plotWidth);
		getYValueAxis().setLowerBound(getYValueAxis().getLowerBound() + 0.1 * plotHeight);
		getYValueAxis().setUpperBound(getYValueAxis().getUpperBound() - 0.1 * plotHeight);

	}

	private void zoomOut() {

		ChartUndoManager.get(getChart()).captureUndoable(this);

		double plotHeight = getYValueAxis().getUpperBound() - getYValueAxis().getLowerBound();
		double plotWidth  = getXValueAxis().getUpperBound() - getXValueAxis().getLowerBound();

		getXValueAxis().setAutoRanging(false);
		getYValueAxis().setAutoRanging(false);
		getXValueAxis().setLowerBound(getXValueAxis().getLowerBound() - 0.1 * plotWidth);
		getXValueAxis().setUpperBound(getXValueAxis().getUpperBound() + 0.1 * plotWidth);
		getYValueAxis().setLowerBound(getYValueAxis().getLowerBound() - 0.1 * plotHeight);
		getYValueAxis().setUpperBound(getYValueAxis().getUpperBound() + 0.1 * plotHeight);

	}

}
