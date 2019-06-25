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


import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory.DoubleSpinnerValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.WindowEvent;
import javafx.util.StringConverter;
import org.apache.commons.lang3.StringUtils;
import se.europeanspallationsource.xaos.ui.plot.Plugin;
import se.europeanspallationsource.xaos.ui.plot.plugins.Pluggable;

import static java.util.logging.Level.SEVERE;
import static javafx.stage.WindowEvent.WINDOW_CLOSE_REQUEST;


/**
 * FXML Controller class
 *
 * @author claudiorosati
 */
public class AxisPropertiesController extends GridPane implements Initializable {

	//	TODO:CR The fitting algorithms should be pluggable.
	private static final StringConverter<Double> DOUBLE_CONVERTER = new StringConverter<Double>() {

		private final DecimalFormat format = new DecimalFormat("0.00##");

		@Override
		@SuppressWarnings( "AssignmentToMethodParameter" )
		public Double fromString( String value ) {
			try {

				if ( StringUtils.isBlank(value) ) {
					//	If the specified value is null, zero-length or blank, return null.
					return null;
				}

				value = value.trim();

				//	Perform the requested parsing.
				return format.parse(value).doubleValue();

			} catch ( ParseException ex ) {
				throw new RuntimeException(ex);
			}
		}

		@Override
		public String toString( Double value ) {

			if ( value == null ) {
				//	If the specified value is null, return a zero-length String.
				return "";
			}

			return format.format(value);

		}

	};
	private static final Logger LOGGER = Logger.getLogger(AxisPropertiesController.class.getName());

	@FXML private Button closeButton;
	@FXML private Label maxCaption;
	@FXML private Label minCaption;
		  private final Pluggable pluggable;
	@FXML private CheckBox xGridValue;
	@FXML private Spinner<Double> xMaxValue;
		  private ObjectProperty<Double> xMaxValueExpression;
	@FXML private Spinner<Double> xMinValue;
		  private ObjectProperty<Double> xMinValueExpression;
	@FXML private CheckBox xScaleValue;
	@FXML private CheckBox yGridValue;
	@FXML private Spinner<Double> yMaxValue;
		  private ObjectProperty<Double> yMaxValueExpression;
	@FXML private Spinner<Double> yMinValue;
		  private ObjectProperty<Double> yMinValueExpression;
	@FXML private CheckBox yScaleValue;

	public AxisPropertiesController( Pluggable pluggable ) {

		this.pluggable = pluggable;

		init();

	}

	@Override
	public void initialize( URL url, ResourceBundle rb ) {

		@SuppressWarnings( "unchecked" )
		XYChart<Number, Number> chart = (XYChart<Number, Number>) pluggable.getChart();

		xGridValue.selectedProperty().bindBidirectional(chart.verticalGridLinesVisibleProperty());
		yGridValue.selectedProperty().bindBidirectional(chart.horizontalGridLinesVisibleProperty());

		xScaleValue.selectedProperty().bindBidirectional(chart.getXAxis().autoRangingProperty());
		yScaleValue.selectedProperty().bindBidirectional(chart.getYAxis().autoRangingProperty());

		minCaption.disableProperty().bind(Bindings.and(xScaleValue.selectedProperty(), yScaleValue.selectedProperty()));
		maxCaption.disableProperty().bind(Bindings.and(xScaleValue.selectedProperty(), yScaleValue.selectedProperty()));

		xMinValue.disableProperty().bind(xScaleValue.selectedProperty());
		xMinValue.setValueFactory(new DoubleSpinnerValueFactory(-Double.MAX_VALUE, Double.MAX_VALUE, Plugin.getXValueAxis(chart).getLowerBound(), 1.0) {{
			setConverter(DOUBLE_CONVERTER);
		}});
		
		xMaxValue.disableProperty().bind(xScaleValue.selectedProperty());
		xMaxValue.setValueFactory(new DoubleSpinnerValueFactory(-Double.MAX_VALUE, Double.MAX_VALUE, Plugin.getXValueAxis(chart).getUpperBound(), 1.0) {{
			setConverter(DOUBLE_CONVERTER);
		}});

		yMinValue.disableProperty().bind(yScaleValue.selectedProperty());
		yMinValue.setValueFactory(new DoubleSpinnerValueFactory(-Double.MAX_VALUE, Double.MAX_VALUE, Plugin.getYValueAxis(chart).getLowerBound(), 1.0) {{
			setConverter(DOUBLE_CONVERTER);
		}});

		yMaxValue.disableProperty().bind(yScaleValue.selectedProperty());
		yMaxValue.setValueFactory(new DoubleSpinnerValueFactory(-Double.MAX_VALUE, Double.MAX_VALUE, Plugin.getYValueAxis(chart).getUpperBound(), 1.0) {{
			setConverter(DOUBLE_CONVERTER);
		}});
		
		xMinValueExpression = Plugin.getXValueAxis(chart).lowerBoundProperty().asObject();
		xMaxValueExpression = Plugin.getXValueAxis(chart).upperBoundProperty().asObject();
		yMinValueExpression = Plugin.getYValueAxis(chart).lowerBoundProperty().asObject();
		yMaxValueExpression = Plugin.getYValueAxis(chart).upperBoundProperty().asObject();
		
		xMinValue.getValueFactory().valueProperty().bindBidirectional(xMinValueExpression);
		xMaxValue.getValueFactory().valueProperty().bindBidirectional(xMaxValueExpression);
		yMinValue.getValueFactory().valueProperty().bindBidirectional(yMinValueExpression);
		yMaxValue.getValueFactory().valueProperty().bindBidirectional(yMaxValueExpression);

	}

	@FXML
	void close( ActionEvent event ) {
		getScene().getWindow().fireEvent(new WindowEvent(getScene().getWindow(), WINDOW_CLOSE_REQUEST));
	}

	void dispose() {

		@SuppressWarnings( "unchecked" )
		XYChart<Number, Number> chart = (XYChart<Number, Number>) pluggable.getChart();

		xGridValue.selectedProperty().unbindBidirectional(chart.verticalGridLinesVisibleProperty());
		yGridValue.selectedProperty().unbindBidirectional(chart.horizontalGridLinesVisibleProperty());

		xScaleValue.selectedProperty().unbindBidirectional(chart.getXAxis().autoRangingProperty());
		yScaleValue.selectedProperty().unbindBidirectional(chart.getYAxis().autoRangingProperty());

		xMinValue.getValueFactory().valueProperty().unbindBidirectional(xMinValueExpression);
		xMaxValue.getValueFactory().valueProperty().unbindBidirectional(xMaxValueExpression);
		yMinValue.getValueFactory().valueProperty().unbindBidirectional(yMinValueExpression);
		yMaxValue.getValueFactory().valueProperty().unbindBidirectional(yMaxValueExpression);

		xMinValueExpression = null;
		xMaxValueExpression = null;
		yMinValueExpression = null;
		yMaxValueExpression = null;

	}

	private void init() {

		URL resource = AxisPropertiesController.class.getResource("fxml/axis.fxml");

		try {

			FXMLLoader loader = new FXMLLoader(resource);

			loader.setController(this);
			loader.setRoot(this);
			loader.setResources(ResourceBundle.getBundle(AxisPropertiesController.class.getPackageName() + ".AxisBundle"));
			loader.load();

		} catch ( IOException ex ) {
			LOGGER.log(
				SEVERE,
				MessageFormat.format(
					"Unable to load ''axis.xml'' resource [{0}].",
					resource.toExternalForm()
				),
				ex
			);
		}

	}

}
