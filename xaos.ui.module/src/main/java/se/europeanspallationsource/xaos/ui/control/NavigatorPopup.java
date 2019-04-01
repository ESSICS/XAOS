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
package se.europeanspallationsource.xaos.ui.control;


import javafx.beans.property.ObjectProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.PopupControl;
import javafx.scene.paint.Color;
import javafx.stage.PopupWindow;
import javafx.stage.Window;


/**
 * A popup window that shows 9 buttons to perform the following operation: 
 * zoom in/out/100%, pan up/right/down/left, forward/backward.
 * <p>
 * The implementation of the actions associated with the popup is left to 
 * the client code, that must add the corresponding callbacks.</p>
 * <p>Typical usage is the following:</P>
 * <pre>
 * NavigatorPopup popup = new NavigatorPopup();
 *
 * popup.setOn...
 * ...
 * popup.setOn...
 * popup.show(ownerNode, cursorScreenX, cursorScreenY);</pre>
 * <p>
 * <b>Note:</b> {@link #anchorLocationProperty()} is set to
 * {@link PopupWindow.AnchorLocation#CONTENT_TOP_LEFT}. Changing this value will
 * result in a wrong popup location.</p>
 *
 * @author claudiorosati
 */
public class NavigatorPopup extends PopupControl {

	private final NavigatorController navigator = new NavigatorController();
	private final double offsetX = navigator.getPrefWidth() / 2.0;
	private final double offsetY = navigator.getPrefHeight() / 2.0;

	/**
	 * Create a new {@link NavigatorPopup} instance.
	 */
	public NavigatorPopup() {
		getScene().setFill(Color.TRANSPARENT);
		getScene().setRoot(navigator);
		super.setAnchorLocation(AnchorLocation.CONTENT_TOP_LEFT);
	}

	/*
	 * **** START OF JAVAFX PROPERTIES *****************************************
	 */

	/*
	 * ---- onBackward ---------------------------------------------------------
	 */
	public final ObjectProperty<EventHandler<Event>> onBackwardProperty() {
		return navigator.onBackwardProperty();
	}

	public final EventHandler<Event> getOnBackward() {
		return navigator.getOnBackward();
	}

	public final void setOnBackward( EventHandler<Event> value ) {
		navigator.setOnBackward(value);
	}

	/*
	 * ---- onForward ----------------------------------------------------------
	 */
	public final ObjectProperty<EventHandler<Event>> onForwardProperty() {
		return navigator.onForwardProperty();
	}

	public final EventHandler<Event> getOnForward() {
		return navigator.getOnForward();
	}

	public final void setOnForward( EventHandler<Event> value ) {
		navigator.setOnForward(value);
	}

	/*
	 * ---- onPanDown ----------------------------------------------------------
	 */
	public final ObjectProperty<EventHandler<Event>> onPanDownProperty() {
		return navigator.onPanDownProperty();
	}

	public final EventHandler<Event> getOnPanDown() {
		return navigator.getOnPanDown();
	}

	public final void setOnPanDown( EventHandler<Event> value ) {
		navigator.setOnPanDown(value);
	}

	/*
	 * ---- onPanLeft ----------------------------------------------------------
	 */
	public final ObjectProperty<EventHandler<Event>> onPanLeftProperty() {
		return navigator.onPanLeftProperty();
	}

	public final EventHandler<Event> getOnPanLeft() {
		return navigator.getOnPanLeft();
	}

	public final void setOnPanLeft( EventHandler<Event> value ) {
		navigator.setOnPanLeft(value);
	}

	/*
	 * ---- onPanRight ---------------------------------------------------------
	 */
	public final ObjectProperty<EventHandler<Event>> onPanRightProperty() {
		return navigator.onPanRightProperty();
	}

	public final EventHandler<Event> getOnPanRight() {
		return navigator.getOnPanRight();
	}

	public final void setOnPanRight( EventHandler<Event> value ) {
		navigator.setOnPanRight(value);
	}

	/*
	 * ---- onPanUp ------------------------------------------------------------
	 */
	public final ObjectProperty<EventHandler<Event>> onPanUpProperty() {
		return navigator.onPanUpProperty();
	}

	public final EventHandler<Event> getOnPanUp() {
		return navigator.getOnPanUp();
	}

	public final void setOnPanUp( EventHandler<Event> value ) {
		navigator.setOnPanUp(value);
	}

	/*
	 * ---- onZoomIn -----------------------------------------------------------
	 */
	public final ObjectProperty<EventHandler<Event>> onZoomInProperty() {
		return navigator.onZoomInProperty();
	}

	public final EventHandler<Event> getOnZoomIn() {
		return navigator.getOnZoomIn();
	}

	public final void setOnZoomIn( EventHandler<Event> value ) {
		navigator.setOnZoomIn(value);
	}

	/*
	 * ---- onZoomOut ----------------------------------------------------------
	 */
	public final ObjectProperty<EventHandler<Event>> onZoomOutProperty() {
		return navigator.onZoomOutProperty();
	}

	public final EventHandler<Event> getOnZoomOut() {
		return navigator.getOnZoomOut();
	}

	public final void setOnZoomOut( EventHandler<Event> value ) {
		navigator.setOnZoomOut(value);
	}

	/*
	 * ---- onZoomToOne --------------------------------------------------------
	 */
	public final ObjectProperty<EventHandler<Event>> onZoomToOneProperty() {
		return navigator.onZoomToOneProperty();
	}

	public final EventHandler<Event> getOnZoomToOne() {
		return navigator.getOnZoomToOne();
	}

	public final void setOnZoomToOne( EventHandler<Event> value ) {
		navigator.setOnZoomToOne(value);
	}

	/*
	 * **** END OF JAVAFX PROPERTIES *******************************************
	 */

	@Override
	public void show( Window ownerWindow, double anchorX, double anchorY ) {
		super.show(ownerWindow, anchorX - offsetX, anchorY - offsetY);
	}

	@Override
	public void show( Node ownerNode, double anchorX, double anchorY ) {
		super.show(ownerNode, anchorX - offsetX, anchorY - offsetY);
	}

}
