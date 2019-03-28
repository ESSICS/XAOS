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
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Path;

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
	private Path forward;
	@FXML
	private Path panDown;
	@FXML
	private Path panLeft;
	@FXML
	private Path panRight;
	@FXML
	private Path panUp;
	@FXML
	private Path zoomIn;
	@FXML
	private Path zoomOut;
	@FXML
	private Circle zoomToOne;

	public NavigatorController() {
		try {

			FXMLLoader loader = new FXMLLoader(getClass().getResource("fxmls/Navigator.fxml"));

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

}
