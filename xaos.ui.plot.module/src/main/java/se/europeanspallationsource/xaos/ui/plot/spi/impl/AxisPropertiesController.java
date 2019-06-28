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
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SpinnerValueFactory.DoubleSpinnerValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.WindowEvent;
import javafx.util.StringConverter;
import org.apache.commons.lang3.StringUtils;
import se.europeanspallationsource.xaos.core.util.LogUtils;
import se.europeanspallationsource.xaos.ui.plot.DateAxis;
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

	private static final Logger LOGGER = Logger.getLogger(AxisPropertiesController.class.getName());

	@FXML private Button closeButton;
	@FXML private Label maxCaption;
	@FXML private Label minCaption;
		  private final Pluggable pluggable;
	@FXML private CheckBox xGridValue;
	@FXML private Spinner xMaxValue;
		  private ObjectProperty xMaxValueExpression;
	@FXML private Spinner xMinValue;
		  private ObjectProperty xMinValueExpression;
	@FXML private CheckBox xScaleValue;
	@FXML private CheckBox yGridValue;
	@FXML private Spinner yMaxValue;
		  private ObjectProperty yMaxValueExpression;
	@FXML private Spinner yMinValue;
		  private ObjectProperty yMinValueExpression;
	@FXML private CheckBox yScaleValue;

	public AxisPropertiesController( Pluggable pluggable ) {

		this.pluggable = pluggable;

		init();

	}

	@Override
	@SuppressWarnings( "unchecked" )
	public void initialize( URL url, ResourceBundle rb ) {

		@SuppressWarnings( "unchecked" )
		XYChart<?, ?> chart = (XYChart<?, ?>) pluggable.getChart();

		xGridValue.selectedProperty().bindBidirectional(chart.verticalGridLinesVisibleProperty());
		yGridValue.selectedProperty().bindBidirectional(chart.horizontalGridLinesVisibleProperty());

		xScaleValue.selectedProperty().bindBidirectional(chart.getXAxis().autoRangingProperty());
		yScaleValue.selectedProperty().bindBidirectional(chart.getYAxis().autoRangingProperty());

		minCaption.disableProperty().bind(Bindings.and(xScaleValue.selectedProperty(), yScaleValue.selectedProperty()));
		maxCaption.disableProperty().bind(Bindings.and(xScaleValue.selectedProperty(), yScaleValue.selectedProperty()));

		xMinValue.disableProperty().bind(xScaleValue.selectedProperty());
		xMinValue.setValueFactory(minXSpinnerValueFactory(chart));
		
		xMaxValue.disableProperty().bind(xScaleValue.selectedProperty());
		xMaxValue.setValueFactory(maxXSpinnerValueFactory(chart));

		yMinValue.disableProperty().bind(yScaleValue.selectedProperty());
		yMinValue.setValueFactory(minYSpinnerValueFactory(chart));

		yMaxValue.disableProperty().bind(yScaleValue.selectedProperty());
		yMaxValue.setValueFactory(maxYSpinnerValueFactory(chart));
		
		xMinValueExpression = minXProperty(chart);
		xMaxValueExpression = maxXProperty(chart);
		yMinValueExpression = minYProperty(chart);
		yMaxValueExpression = maxYProperty(chart);
		
		xMinValue.getValueFactory().valueProperty().bindBidirectional(xMinValueExpression);
		xMaxValue.getValueFactory().valueProperty().bindBidirectional(xMaxValueExpression);
		yMinValue.getValueFactory().valueProperty().bindBidirectional(yMinValueExpression);
		yMaxValue.getValueFactory().valueProperty().bindBidirectional(yMaxValueExpression);

	}

	@FXML
	void close( ActionEvent event ) {
		getScene().getWindow().fireEvent(new WindowEvent(getScene().getWindow(), WINDOW_CLOSE_REQUEST));
	}

	@SuppressWarnings( "unchecked" )
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

	private DateSpinnerValueFactory dateSpinnerValueFactory( DateAxis dateAxis, Date currentValue ) {
		return new DateSpinnerValueFactory(dateAxis.getDateFormat(), dateAxis.getActualInterval(), currentValue);
	}

	private DoubleSpinnerValueFactory doubleSpinnerValueFactory( double currentValue ) {
		return new DoubleSpinnerValueFactory(-Double.MAX_VALUE, Double.MAX_VALUE, currentValue, 1.0) {{
			setConverter(new DoubleConverter());
		}};
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
			LogUtils.log(LOGGER, SEVERE, ex, "Unable to load ''axis.xml'' resource [{0}].", resource.toExternalForm());
		}

	}

	private ObjectProperty<?> maxXProperty( XYChart<?, ?> chart ) {
		if ( chart.getXAxis() instanceof ValueAxis<?> ) {
			return Plugin.getXValueAxis(chart).upperBoundProperty().asObject();
		} else if ( chart.getXAxis() instanceof DateAxis ) {
			return ((DateAxis) chart.getXAxis()).upperBoundProperty();
		} else {
			return null;
		}
	}

	private SpinnerValueFactory maxXSpinnerValueFactory( XYChart<?, ?> chart ) {
		if ( chart.getXAxis() instanceof ValueAxis<?> ) {
			return doubleSpinnerValueFactory(Plugin.getXValueAxis(chart).getUpperBound());
		} else if ( chart.getXAxis() instanceof DateAxis ) {
			return dateSpinnerValueFactory((DateAxis) chart.getXAxis(), ((DateAxis) chart.getXAxis()).getUpperBound());
		} else {
			return null;
		}
	}

	private ObjectProperty<?> maxYProperty( XYChart<?, ?> chart ) {
		if ( chart.getYAxis() instanceof ValueAxis<?> ) {
			return Plugin.getYValueAxis(chart).upperBoundProperty().asObject();
		} else if ( chart.getYAxis() instanceof DateAxis ) {
			return ((DateAxis) chart.getYAxis()).upperBoundProperty();
		} else {
			return null;
		}
	}

	private SpinnerValueFactory maxYSpinnerValueFactory( XYChart<?, ?> chart ) {
		if ( chart.getYAxis() instanceof ValueAxis<?> ) {
			return doubleSpinnerValueFactory(Plugin.getYValueAxis(chart).getUpperBound());
		} else if ( chart.getYAxis() instanceof DateAxis ) {
			return dateSpinnerValueFactory((DateAxis) chart.getYAxis(), ((DateAxis) chart.getYAxis()).getUpperBound());
		} else {
			return null;
		}
	}

	private ObjectProperty<?> minXProperty( XYChart<?, ?> chart ) {
		if ( chart.getXAxis() instanceof ValueAxis<?> ) {
			return Plugin.getXValueAxis(chart).lowerBoundProperty().asObject();
		} else if ( chart.getXAxis() instanceof DateAxis ) {
			return ((DateAxis) chart.getXAxis()).lowerBoundProperty();
		} else {
			return null;
		}
	}

	private SpinnerValueFactory minXSpinnerValueFactory( XYChart<?, ?> chart ) {
		if ( chart.getXAxis() instanceof ValueAxis<?> ) {
			return doubleSpinnerValueFactory(Plugin.getXValueAxis(chart).getLowerBound());
		} else if ( chart.getXAxis() instanceof DateAxis ) {
			return dateSpinnerValueFactory((DateAxis) chart.getXAxis(), ((DateAxis) chart.getXAxis()).getLowerBound());
		} else {
			return null;
		}
	}

	private ObjectProperty<?> minYProperty( XYChart<?, ?> chart ) {
		if ( chart.getYAxis() instanceof ValueAxis<?> ) {
			return Plugin.getYValueAxis(chart).lowerBoundProperty().asObject();
		} else if ( chart.getYAxis() instanceof DateAxis ) {
			return ((DateAxis) chart.getYAxis()).lowerBoundProperty();
		} else {
			return null;
		}
	}

	private SpinnerValueFactory minYSpinnerValueFactory( XYChart<?, ?> chart ) {
		if ( chart.getYAxis() instanceof ValueAxis<?> ) {
			return doubleSpinnerValueFactory(Plugin.getYValueAxis(chart).getLowerBound());
		} else if ( chart.getYAxis() instanceof DateAxis ) {
			return dateSpinnerValueFactory((DateAxis) chart.getYAxis(), ((DateAxis) chart.getYAxis()).getLowerBound());
		} else {
			return null;
		}
	}

	private static class DateConverter extends StringConverter<Date> {

		private final DateFormat format;

		public DateConverter( DateFormat format) {
			this.format = format;
		}

		@Override
		@SuppressWarnings( "AssignmentToMethodParameter" )
		public Date fromString( String value ) {
			try {

				if ( StringUtils.isBlank(value) ) {
					//	If the specified value is null, zero-length or blank, return null.
					return null;
				}

				value = value.trim();

				//	Perform the requested parsing.
				return format.parse(value);

			} catch ( ParseException ex ) {
				throw new RuntimeException(ex);
			}
		}

		@Override
		public String toString( Date value ) {

			if ( value == null ) {
				//	If the specified value is null, return a zero-length String.
				return "";
			}

			return format.format(value);

		}

	}

	private static class DateSpinnerValueFactory extends SpinnerValueFactory<Date> {

		private final DateFormat format;
		private final int field;

		/**
		 * @param format       {@link DateFormat} used by the {@link DateConverter}.
		 * @param field        The {@link Calendar} field used to properly
		 *                     increment/decrement the spinner.
		 * @param initialValue The initial {@link Date} value.
		 */
		DateSpinnerValueFactory( DateFormat format, int field, Date initialValue ) {

			this.format = format;
			this.field = field;

			setConverter(new DateConverter(format));
			setValue(initialValue);

		}

		@Override
		public void decrement( int i ) {

			Calendar calendar = Calendar.getInstance();

			calendar.setTime(getValue());
			calendar.add(field, - i);

			setValue(calendar.getTime());

		}

		@Override
		public void increment( int i ) {

			Calendar calendar = Calendar.getInstance();

			calendar.setTime(getValue());
			calendar.add(field, i);

			setValue(calendar.getTime());

		}

	}

	private static class DoubleConverter extends StringConverter<Double> {

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

	}

}
