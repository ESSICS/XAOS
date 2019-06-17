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
package se.europeanspallationsource.xaos.ui.plot.spi.impl;


import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;


/**
 * FXML Controller class
 *
 * @author claudiorosati
 */
public class FitController implements Initializable {

	@FXML private Button applyButton;
	@FXML private Button clearAllButton;
	@FXML private Button clearButton;
	@FXML private ComboBox<?> dataSetValue;
	@FXML private Label degreeOrOffsetCaption;
	@FXML private Spinner<?> degreeOrOffsetValue;
	@FXML private Spinner<?> discretizationValue;
	@FXML private ComboBox<?> fittingValue;
	@FXML private Spinner<?> maxXValue;
	@FXML private Spinner<?> minXValue;

	/**
	 * Initializes the controller class.
	 */
	@Override
	public void initialize( URL url, ResourceBundle rb ) {
		// TODO
	}

	@FXML
	void apply( ActionEvent event ) {

	}

	@FXML
	void clear( ActionEvent event ) {

	}

	@FXML
	void clearAll( ActionEvent event ) {

	}

	void setChart( XYChart<?, ?> xyChart ) {
	}

}
