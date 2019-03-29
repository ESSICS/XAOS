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
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Path;
import org.kordamp.ikonli.javafx.FontIcon;

import static java.util.logging.Level.SEVERE;


/**
 * The JavaFX controller for the {@link NavigatorPopup}.
 *
 * @author claudio.rosati@esss.se
 */
public class NavigatorController extends AnchorPane {

	private static final Logger LOGGER = Logger.getLogger(NavigatorController.class.getName());

	@FXML
	private Path backward;
	@FXML
	private FontIcon backwardIcon;
	@FXML
	private Path forward;
	@FXML
	private FontIcon forwardIcon;
	@FXML
	private Path panDown;
	@FXML
	private FontIcon panDownIcon;
	@FXML
	private Path panLeft;
	@FXML
	private FontIcon panLeftIcon;
	@FXML
	private Path panRight;
	@FXML
	private FontIcon panRightIcon;
	@FXML
	private Path panUp;
	@FXML
	private FontIcon panUpIcon;
	@FXML
	private FontIcon soomOutIcon;
	@FXML
	private Path zoomIn;
	@FXML
	private FontIcon zoomInIcon;
	@FXML
	private Path zoomOut;
	@FXML
	private Circle zoomToOne;
	@FXML
	private Label zoomToOneLabel;

	public NavigatorController() {
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

	private String enteredStyle;

	@FXML
	private void mouseEntered( MouseEvent event ) {

		enteredStyle = ((Node) event.getSource()).getStyle();

		((Node) event.getSource()).setStyle("-fx-fill: -hover-color");

	}

	@FXML
	void mouseExited( MouseEvent event ) {

		((Node) event.getSource()).setStyle(enteredStyle);

	}

	@FXML
	void mousePressed( MouseEvent event ) {

		((Node) event.getSource()).setStyle("-fx-fill: -pressed-color");

	}

	@FXML
	void mouseReleased( MouseEvent event ) {

		Node source = (Node) event.getSource();

		if ( source.contains(event.getX(), event.getY()) ) {
			source.setStyle("-fx-fill: -hover-color");
		} else {
			source.setStyle(enteredStyle);
		}

	}

}
