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


import chart.Plugin;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory.DoubleSpinnerValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.apache.commons.lang3.StringUtils;
import se.europeanspallationsource.xaos.tools.annotation.Bundle;
import se.europeanspallationsource.xaos.tools.annotation.BundleItem;
import se.europeanspallationsource.xaos.tools.annotation.BundleItems;
import se.europeanspallationsource.xaos.tools.annotation.Bundles;
import se.europeanspallationsource.xaos.ui.plot.Legend.LegendItem;
import se.europeanspallationsource.xaos.ui.plot.plugins.Pluggable;

import static java.util.logging.Level.SEVERE;
import static javafx.stage.WindowEvent.WINDOW_CLOSE_REQUEST;


/**
 * FXML Controller class
 *
 * @author claudiorosati
 */
@Bundle( name = "FitBundle" )
public class FitController extends GridPane implements Initializable {

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
	private static final StringConverter<Double> INTEGER_CONVERTER = new StringConverter<Double>() {

		private final DecimalFormat format = new DecimalFormat("0");

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
				return Math.rint(format.parse(value).doubleValue());

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

			return format.format(Math.rint(value));

		}

	};
	private static final Logger LOGGER = Logger.getLogger(FitController.class.getName());

	@FXML private Button applyButton;
	@FXML private Button clearAllButton;
	@FXML private Button clearButton;
	@FXML private Button closeButton;
	@FXML private ComboBox<LegendItem> dataSetValue;
	@FXML private Label degreeOrOffsetCaption;
	@FXML private Spinner<Double> degreeOrOffsetValue;
	@FXML private Spinner<Double> discretizationValue;
	@FXML private ComboBox<String> fittingValue;
	@FXML private Spinner<Double> maxXValue;
	@FXML private Spinner<Double> minXValue;
		  private final Pluggable pluggable;

	public FitController( Pluggable pluggable ) {

		this.pluggable = pluggable;

		init();

	}

	@BundleItems( {
		@BundleItem( key = "exponential.trend.line", message = "Exponential Trend Line" ),
		@BundleItem( key = "gaussian.trend.line", message = "Gaussian Trend Line" ),
		@BundleItem( key = "logarithmic.trend.line", message = "Logarithmic Trend Line" ),
		@BundleItem( key = "polynomial.trend.line", message = "Polynomial Trend Line" ),
		@BundleItem( key = "power.trend.line", message = "Power Trend Line" ),
		@BundleItem( key = "degree.caption", message = "Polynomial Degree:" ),
		@BundleItem( key = "offset.caption", message = "Offset:" )
	} )
	@Override
	public void initialize( URL url, ResourceBundle rb ) {

		Callback<ListView<LegendItem>, ListCell<LegendItem>> cellFactory = p -> new ListCell<>() {
			@Override
			protected void updateItem( LegendItem item, boolean empty ) {

				super.updateItem(item, empty);

				SnapshotParameters sp = new SnapshotParameters();

				sp.setFill(Color.TRANSPARENT);
				setGraphic(( item == null || empty ) ? null : new ImageView(item.getSymbol().snapshot(sp, null)));
				setText(( item == null || empty ) ? null : item.toString());

			}
		};

		pluggable.getLegendItems().forEach(li -> dataSetValue.getItems().add(li));
		dataSetValue.setValue(dataSetValue.getItems().get(0));
		dataSetValue.setButtonCell(cellFactory.call(null));
		dataSetValue.setCellFactory(cellFactory);

		fittingValue.getItems().addAll(
			getString("exponential.trend.line"),
			getString("gaussian.trend.line"),
			getString("logarithmic.trend.line"),
			getString("polynomial.trend.line"),
			getString("power.trend.line")
		);
		fittingValue.setValue(getString("polynomial.trend.line"));

		degreeOrOffsetCaption.disableProperty().bind(Bindings.createBooleanBinding(
			() -> getString("logarithmic.trend.line").equals(fittingValue.getValue()),
			fittingValue.valueProperty()
		));
		degreeOrOffsetCaption.textProperty().bind(Bindings.createStringBinding(
			() -> getString("polynomial.trend.line").equals(fittingValue.getValue())
				  ? getString("degree.caption")
				  : getString("offset.caption"),
			fittingValue.valueProperty()
		));

		degreeOrOffsetValue.disableProperty().bind(Bindings.createBooleanBinding(
			() -> getString("logarithmic.trend.line").equals(fittingValue.getValue()),
			fittingValue.valueProperty()
		));
		degreeOrOffsetValue.setValueFactory(new DoubleSpinnerValueFactory(1.0, Integer.MAX_VALUE, 1.0, 1.0) {{
			setConverter(INTEGER_CONVERTER);
		}});
		fittingValue.valueProperty().addListener(( ob, o, n) -> {
			if ( getString("polynomial.trend.line").equals(n) ) {
				degreeOrOffsetValue.getValueFactory().setConverter(INTEGER_CONVERTER);
				((DoubleSpinnerValueFactory) degreeOrOffsetValue.getValueFactory()).setMin(1.0);
				((DoubleSpinnerValueFactory) degreeOrOffsetValue.getValueFactory()).setMax(Integer.MAX_VALUE);
			} else {
				degreeOrOffsetValue.getValueFactory().setConverter(DOUBLE_CONVERTER);
				((DoubleSpinnerValueFactory) degreeOrOffsetValue.getValueFactory()).setMin(-Double.MAX_VALUE);
				((DoubleSpinnerValueFactory) degreeOrOffsetValue.getValueFactory()).setMax(Double.MAX_VALUE);
			}
		});
		
		discretizationValue.setValueFactory(new DoubleSpinnerValueFactory(-Double.MAX_VALUE, Double.MAX_VALUE, 100.0, 1.0) {{
			setConverter(DOUBLE_CONVERTER);
		}});

		XYChart<?, ?> chart = (XYChart<?, ?>) pluggable.getChart();

		minXValue.setValueFactory(new DoubleSpinnerValueFactory(-Double.MAX_VALUE, Double.MAX_VALUE, Plugin.getXValueAxis(chart).getLowerBound(), 1.0) {{
			setConverter(DOUBLE_CONVERTER);
		}});

		maxXValue.setValueFactory(new DoubleSpinnerValueFactory(-Double.MAX_VALUE, Double.MAX_VALUE, Plugin.getXValueAxis(chart).getUpperBound(), 1.0) {{
			setConverter(DOUBLE_CONVERTER);
		}});

	}

	@FXML
	void apply( ActionEvent event ) {

	}

	@FXML
	void clear( ActionEvent event ) {

	}

	@FXML
	void close( ActionEvent event ) {
		getScene().getWindow().fireEvent(new WindowEvent(getScene().getWindow(), WINDOW_CLOSE_REQUEST));
	}

	@FXML
	void clearAll( ActionEvent event ) {

	}

	void setChart( XYChart<?, ?> xyChart ) {
	}

	private String getString( String key, Object... parameters ) {
		return Bundles.get(FitController.class, key, parameters);
	}

	private void init() {

		URL resource = FitController.class.getResource("fxml/fit.fxml");

		try {

			FXMLLoader loader = new FXMLLoader(resource);

			loader.setController(this);
			loader.setRoot(this);
			loader.setResources(ResourceBundle.getBundle(FitController.class.getPackageName() + ".FitBundle"));
			loader.load();

		} catch ( IOException ex ) {
			LOGGER.log(
				SEVERE,
				MessageFormat.format(
					"Unable to load ''fit.xml'' resource [{0}].",
					resource.toExternalForm()
				),
				ex
			);
		}

	}

}
