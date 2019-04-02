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


import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.event.WeakEventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;
import org.kordamp.ikonli.javafx.FontIcon;

import static java.util.logging.Level.SEVERE;
import static javafx.scene.input.MouseButton.PRIMARY;


/**
 * A JavaFX controller with 9 buttons to navigate a graphical area. Zoom, Pan
 * and backward/forward buttons are provided and can be bound to the application
 * code through the provided {@link EventHandler} methods.
 *
 * @author claudio.rosati@esss.se
 */
public class NavigatorController extends AnchorPane {

	/**
	 * Called when the navigator's {@code backward} button is pressed.
	 */
	public static final EventType<Event> ON_BACKWARD = new EventType<Event>(Event.ANY, "NAVIGATOR_ON_BACKWARD");

	/**
	 * Called when the navigator's {@code forward} button is pressed.
	 */
	public static final EventType<Event> ON_FORWARD = new EventType<Event>(Event.ANY, "NAVIGATOR_ON_FORWARD");

	/**
	 * Called when the navigator's {@code panDown} button is pressed.
	 */
	public static final EventType<Event> ON_PAN_DOWN = new EventType<Event>(Event.ANY, "NAVIGATOR_ON_PAN_DOWN");

	/**
	 * Called when the navigator's {@code panLeft} button is pressed.
	 */
	public static final EventType<Event> ON_PAN_LEFT = new EventType<Event>(Event.ANY, "NAVIGATOR_ON_PAN_LEFT");

	/**
	 * Called when the navigator's {@code panRight} button is pressed.
	 */
	public static final EventType<Event> ON_PAN_RIGHT = new EventType<Event>(Event.ANY, "NAVIGATOR_ON_PAN_RIGHT");

	/**
	 * Called when the navigator's {@code panUp} button is pressed.
	 */
	public static final EventType<Event> ON_PAN_UP = new EventType<Event>(Event.ANY, "NAVIGATOR_ON_PAN_UP");

	/**
	 * Called when the navigator's {@code zoomIn} button is pressed.
	 */
	public static final EventType<Event> ON_ZOOM_IN = new EventType<Event>(Event.ANY, "NAVIGATOR_ON_ZOOM_IN");

	/**
	 * Called when the navigator's {@code zoomOut} button is pressed.
	 */
	public static final EventType<Event> ON_ZOOM_OUT = new EventType<Event>(Event.ANY, "NAVIGATOR_ON_ZOOM_OUT");

	/**
	 * Called when the navigator's {@code zoomToOne} button is pressed.
	 */
	public static final EventType<Event> ON_ZOOM_TO_ONE = new EventType<Event>(Event.ANY, "NAVIGATOR_ON_ZOOM_TO_ONE");

	private static final String FOCUS_CHANGE_LISTENER = "FOCUS_CHANGE_LISTENER";
	private static final String KEY_PRESSED_HANDLER = "KEY_PRESSED_HANDLER";
	private static final String KEY_RELEASED_HANDLER = "KEY_RELEASED_HANDLER";

	private static final Logger LOGGER = Logger.getLogger(NavigatorController.class.getName());

	@FXML private Path backward;
	@FXML private FontIcon backwardIcon;
	private String enteredStyle;
	@FXML private Path forward;
	@FXML private FontIcon forwardIcon;
	@FXML private Path panDown;
	@FXML private FontIcon panDownIcon;
	@FXML private Path panLeft;
	@FXML private FontIcon panLeftIcon;
	@FXML private Path panRight;
	@FXML private FontIcon panRightIcon;
	@FXML private Path panUp;
	@FXML private FontIcon panUpIcon;
	@FXML private Path zoomIn;
	@FXML private FontIcon zoomInIcon;
	@FXML private Path zoomOut;
	@FXML private FontIcon zoomOutIcon;
	@FXML private Circle zoomToOne;
	@FXML private Label zoomToOneLabel;

	public NavigatorController() {
		init();
		initListeners();
	}

	/***************************************************************************
	 * START OF JAVAFX PROPERTIES                                              *
	 ***************************************************************************/

	/*
	 * ---- onBackward ---------------------------------------------------------
	 */
	private ObjectProperty<EventHandler<Event>> onBackward = new ObjectPropertyBase<EventHandler<Event>>() {

		@Override
		public Object getBean() {
			return NavigatorController.this;
		}

		@Override
		public String getName() {
			return "onBackward";
		}

		@Override protected void invalidated() {
			setEventHandler(ON_BACKWARD, get());
		}

	};

	/**
	 * Called when the {@code backward} button is pressed.
	 *
	 * @return The "on backward" property.
	 */
	public final ObjectProperty<EventHandler<Event>> onBackwardProperty() {
		return onBackward;
	}

	public final EventHandler<Event> getOnBackward() {
		return onBackwardProperty().get();
	}

	public final void setOnBackward( EventHandler<Event> value ) {
		onBackwardProperty().set(value);
	}

	/*
	 * ---- onForward ----------------------------------------------------------
	 */
	private ObjectProperty<EventHandler<Event>> onForward = new ObjectPropertyBase<EventHandler<Event>>() {

		@Override
		public Object getBean() {
			return NavigatorController.this;
		}

		@Override
		public String getName() {
			return "onForward";
		}

		@Override protected void invalidated() {
			setEventHandler(ON_FORWARD, get());
		}

	};

	/**
	 * Called when the {@code forward} button is pressed.
	 *
	 * @return The "on forward" property.
	 */
	public final ObjectProperty<EventHandler<Event>> onForwardProperty() {
		return onForward;
	}

	public final EventHandler<Event> getOnForward() {
		return onForwardProperty().get();
	}

	public final void setOnForward( EventHandler<Event> value ) {
		onForwardProperty().set(value);
	}

	/*
	 * ---- onPanDown ----------------------------------------------------------
	 */
	private ObjectProperty<EventHandler<Event>> onPanDown = new ObjectPropertyBase<EventHandler<Event>>() {

		@Override
		public Object getBean() {
			return NavigatorController.this;
		}

		@Override
		public String getName() {
			return "onPanDown";
		}

		@Override protected void invalidated() {
			setEventHandler(ON_PAN_DOWN, get());
		}

	};

	/**
	 * Called when the {@code panDown} button is pressed.
	 *
	 * @return The "on pan down" property.
	 */
	public final ObjectProperty<EventHandler<Event>> onPanDownProperty() {
		return onPanDown;
	}

	public final EventHandler<Event> getOnPanDown() {
		return onPanDownProperty().get();
	}

	public final void setOnPanDown( EventHandler<Event> value ) {
		onPanDownProperty().set(value);
	}

	/*
	 * ---- onPanLeft ----------------------------------------------------------
	 */
	private ObjectProperty<EventHandler<Event>> onPanLeft = new ObjectPropertyBase<EventHandler<Event>>() {

		@Override
		public Object getBean() {
			return NavigatorController.this;
		}

		@Override
		public String getName() {
			return "onPanLeft";
		}

		@Override protected void invalidated() {
			setEventHandler(ON_PAN_LEFT, get());
		}

	};

	/**
	 * Called when the {@code panLeft} button is pressed.
	 *
	 * @return The "on pan left" property.
	 */
	public final ObjectProperty<EventHandler<Event>> onPanLeftProperty() {
		return onPanLeft;
	}

	public final EventHandler<Event> getOnPanLeft() {
		return onPanLeftProperty().get();
	}

	public final void setOnPanLeft( EventHandler<Event> value ) {
		onPanLeftProperty().set(value);
	}

	/*
	 * ---- onPanRight ---------------------------------------------------------
	 */
	private ObjectProperty<EventHandler<Event>> onPanRight = new ObjectPropertyBase<EventHandler<Event>>() {

		@Override
		public Object getBean() {
			return NavigatorController.this;
		}

		@Override
		public String getName() {
			return "onPanRight";
		}

		@Override protected void invalidated() {
			setEventHandler(ON_PAN_RIGHT, get());
		}

	};

	/**
	 * Called when the {@code panRight} button is pressed.
	 *
	 * @return The "on pan right" property.
	 */
	public final ObjectProperty<EventHandler<Event>> onPanRightProperty() {
		return onPanRight;
	}

	public final EventHandler<Event> getOnPanRight() {
		return onPanRightProperty().get();
	}

	public final void setOnPanRight( EventHandler<Event> value ) {
		onPanRightProperty().set(value);
	}

	/*
	 * ---- onPanUp ------------------------------------------------------------
	 */
	private ObjectProperty<EventHandler<Event>> onPanUp = new ObjectPropertyBase<EventHandler<Event>>() {

		@Override
		public Object getBean() {
			return NavigatorController.this;
		}

		@Override
		public String getName() {
			return "onPanUp";
		}

		@Override protected void invalidated() {
			setEventHandler(ON_PAN_UP, get());
		}

	};

	/**
	 * Called when the {@code panUp} button is pressed.
	 *
	 * @return The "on pan up" property.
	 */
	public final ObjectProperty<EventHandler<Event>> onPanUpProperty() {
		return onPanUp;
	}

	public final EventHandler<Event> getOnPanUp() {
		return onPanUpProperty().get();
	}

	public final void setOnPanUp( EventHandler<Event> value ) {
		onPanUpProperty().set(value);
	}

	/*
	 * ---- onZoomIn -----------------------------------------------------------
	 */
	private ObjectProperty<EventHandler<Event>> onZoomIn = new ObjectPropertyBase<EventHandler<Event>>() {

		@Override
		public Object getBean() {
			return NavigatorController.this;
		}

		@Override
		public String getName() {
			return "onZoomIn";
		}

		@Override protected void invalidated() {
			setEventHandler(ON_ZOOM_IN, get());
		}

	};

	/**
	 * Called when the {@code zoomIn} button is pressed.
	 *
	 * @return The "on zoom in" property.
	 */
	public final ObjectProperty<EventHandler<Event>> onZoomInProperty() {
		return onZoomIn;
	}

	public final EventHandler<Event> getOnZoomIn() {
		return onZoomInProperty().get();
	}

	public final void setOnZoomIn( EventHandler<Event> value ) {
		onZoomInProperty().set(value);
	}

	/*
	 * ---- onZoomOut ----------------------------------------------------------
	 */
	private ObjectProperty<EventHandler<Event>> onZoomOut = new ObjectPropertyBase<EventHandler<Event>>() {

		@Override
		public Object getBean() {
			return NavigatorController.this;
		}

		@Override
		public String getName() {
			return "onZoomOut";
		}

		@Override protected void invalidated() {
			setEventHandler(ON_ZOOM_OUT, get());
		}

	};

	/**
	 * Called when the {@code zoomOut} button is pressed.
	 *
	 * @return The "on zoom out" property.
	 */
	public final ObjectProperty<EventHandler<Event>> onZoomOutProperty() {
		return onZoomOut;
	}

	public final EventHandler<Event> getOnZoomOut() {
		return onZoomOutProperty().get();
	}

	public final void setOnZoomOut( EventHandler<Event> value ) {
		onZoomOutProperty().set(value);
	}

	/*
	 * ---- onZoomToOne --------------------------------------------------------
	 */
	private ObjectProperty<EventHandler<Event>> onZoomToOne = new ObjectPropertyBase<EventHandler<Event>>() {

		@Override
		public Object getBean() {
			return NavigatorController.this;
		}

		@Override
		public String getName() {
			return "onZoomToOne";
		}

		@Override protected void invalidated() {
			setEventHandler(ON_ZOOM_TO_ONE, get());
		}

	};

	/**
	 * Called when the {@code zoomToOne} button is pressed.
	 *
	 * @return The "on zoom to one" property.
	 */
	public final ObjectProperty<EventHandler<Event>> onZoomToOneProperty() {
		return onZoomToOne;
	}

	public final EventHandler<Event> getOnZoomToOne() {
		return onZoomToOneProperty().get();
	}

	public final void setOnZoomToOne( EventHandler<Event> value ) {
		onZoomToOneProperty().set(value);
	}

	/***************************************************************************
	 * END OF JAVAFX PROPERTIES                                                *
	 ***************************************************************************/

	private void fireNavigationEvent ( Node source ) {

		String id = source.getId();

		if ( "backward".equals(id) ) {
			Event.fireEvent(source, new Event(this, source, ON_BACKWARD));
		} else if ( "forward".equals(id) ) {
			Event.fireEvent(source, new Event(this, source, ON_FORWARD));
		} else if ( "panDown".equals(id) ) {
			Event.fireEvent(source, new Event(this, source, ON_PAN_DOWN));
		} else if ( "panLeft".equals(id) ) {
			Event.fireEvent(source, new Event(this, source, ON_PAN_LEFT));
		} else if ( "panRight".equals(id) ) {
			Event.fireEvent(source, new Event(this, source, ON_PAN_RIGHT));
		} else if ( "panUp".equals(id) ) {
			Event.fireEvent(source, new Event(this, source, ON_PAN_UP));
		} else if ( "zoomIn".equals(id) ) {
			Event.fireEvent(source, new Event(this, source, ON_ZOOM_IN));
		} else if ( "zoomOut".equals(id) ) {
			Event.fireEvent(source, new Event(this, source, ON_ZOOM_OUT));
		} else if ( "zoomToOne".equals(id) ) {
			Event.fireEvent(source, new Event(this, source, ON_ZOOM_TO_ONE));
		} else {
			LOGGER.warning(MessageFormat.format("Unexpected identifier [{0}].", id));
		}

	}

	private void init() {
		try {

			FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/navigator.fxml"));

			loader.setController(this);
			loader.setRoot(this);
			loader.load();

		} catch ( IOException ex ) {
			LOGGER.log(
				SEVERE,
				MessageFormat.format(
					"Unable to load 'Navigator.xml' resource [{0}].",
					getClass().getResource("Navigator.xml")
				),
				ex
			);
		}
	}

	private void initListeners() {
		initListeners(backward);
		initListeners(forward);
		initListeners(panDown);
		initListeners(panLeft);
		initListeners(panRight);
		initListeners(panUp);
		initListeners(zoomIn);
		initListeners(zoomOut);
		initListeners(zoomToOne);
	}

	private void initListeners( final Shape shape ) {

		Map<String, Object> listenersMap = new HashMap<>(4);
		EventHandler<KeyEvent> keyPressedHandler = new EventHandler<KeyEvent>() {
			@Override
			public void handle( KeyEvent event ) {
				if ( shape.isFocused() ) {
					switch ( event.getCode() ) {
						case ENTER:
						case SPACE:
							updateStyle(shape, FillStyle.PRESSED, StrokeStyle.FOCUSED);
							event.consume();
							break;
					}
				}
			}
		};
		EventHandler<KeyEvent> keyReleasedHandler = new EventHandler<KeyEvent>() {
			@Override
			public void handle( KeyEvent event ) {
				if ( shape.isFocused() ) {
					switch ( event.getCode() ) {
						case ENTER:
						case SPACE:
							fireNavigationEvent(shape);
							updateStyle(shape);
							event.consume();
							break;
					}
				}
			}
		};
		ChangeListener<Boolean> focusChangeListener = ( observable, hadFocus, hasFocus ) -> {
			updateStyle(shape);
		};

		listenersMap.put(FOCUS_CHANGE_LISTENER, focusChangeListener);
		listenersMap.put(KEY_PRESSED_HANDLER, keyPressedHandler);
		listenersMap.put(KEY_RELEASED_HANDLER, keyReleasedHandler);

		shape.setUserData(listenersMap);
		shape.focusedProperty().addListener(new WeakChangeListener<>(focusChangeListener));
		shape.addEventHandler(KeyEvent.KEY_PRESSED, new WeakEventHandler<>(keyPressedHandler));
		shape.addEventHandler(KeyEvent.KEY_RELEASED, new WeakEventHandler<>(keyReleasedHandler));

	}

	private boolean isPanButton( Node node ) {
		return ( node == panDown || node == panLeft || node == panRight || node == panUp );
	}

	@FXML
	@SuppressWarnings( "ConvertToStringSwitch" )
	private void mouseClicked( MouseEvent event ) {

		Node source = (Node) event.getSource();

		source.requestFocus();

		if ( source != null && PRIMARY == event.getButton() ) {
			fireNavigationEvent(source);
		}

		event.consume();

	}

	@FXML
	private void mouseEntered( MouseEvent event ) {
		updateStyle((Node) event.getSource());
		event.consume();
	}

	@FXML
	private void mouseExited( MouseEvent event ) {
		updateStyle((Node) event.getSource());
		event.consume();
	}

	@FXML
	private void mousePressed( MouseEvent event ) {
		updateStyle((Node) event.getSource());
		event.consume();
	}

	@FXML
	private void mouseReleased( MouseEvent event ) {
		updateStyle((Node) event.getSource());
		event.consume();
	}

	private void updateStyle( Node node ) {
		updateStyle(
			node,
			node.isPressed() ? FillStyle.PRESSED : ( node.isHover() ? FillStyle.HOVER : ( isPanButton(node) ? FillStyle.LIGHTER : FillStyle.DEFAULT ) ),
			node.isFocused() ? StrokeStyle.FOCUSED : StrokeStyle.DEFAULT
		);
	}

	private void updateStyle( Node node, FillStyle fill, StrokeStyle stroke ) {
		node.setStyle(MessageFormat.format("{0}; {1};", fill.getStyle(), stroke.getStyle()));
	}

	@SuppressWarnings( "PackageVisibleInnerClass" )
	enum FillStyle {

		DEFAULT("-fx-fill: -normal-color"),
		LIGHTER("-fx-fill: -normal-color-lighter"),
		HOVER("-fx-fill: -hover-color"),
		PRESSED("-fx-fill: -pressed-color");

		private final String style;

		FillStyle( String style ) {
			this.style = style;
		}

		String getStyle() {
			return style;
		}

	}

	@SuppressWarnings( "PackageVisibleInnerClass" )
	enum StrokeStyle {

		DEFAULT("-fx-stroke: -fx-outer-border"),
		FOCUSED("-fx-stroke: -fx-focus-color");

		private final String style;

		StrokeStyle( String style ) {
			this.style = style;
		}

		String getStyle() {
			return style;
		}

	}

}
