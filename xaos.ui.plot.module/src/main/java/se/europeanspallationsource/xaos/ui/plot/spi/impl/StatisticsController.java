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
import java.text.ParseException;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.apache.commons.lang3.StringUtils;
import se.europeanspallationsource.xaos.core.util.LogUtils;
import se.europeanspallationsource.xaos.ui.plot.Legend.LegendItem;
import se.europeanspallationsource.xaos.ui.plot.plugins.Pluggable;

import static java.util.logging.Level.SEVERE;
import static java.util.logging.Level.WARNING;
import static javafx.stage.WindowEvent.WINDOW_CLOSE_REQUEST;


/**
 * FXML Controller class
 *
 * @author claudiorosati
 */
public class StatisticsController extends GridPane implements Initializable {

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
	private static final Logger LOGGER = Logger.getLogger(StatisticsController.class.getName());

	@FXML private Button closeButton;
	@FXML private ComboBox<LegendItem> dataSetValue;
	@FXML private TextField intAbsFxValue;
	@FXML private TextField intFxValue;
	@FXML private TextField meanXValue;
	@FXML private TextField meanYValue;
		  private final Pluggable pluggable;
	@FXML private TextField stdevXValue;
	@FXML private TextField stdevYValue;

	public StatisticsController( Pluggable pluggable ) {

		this.pluggable = pluggable;

		init();

	}

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
		dataSetValue.setButtonCell(cellFactory.call(null));
		dataSetValue.setCellFactory(cellFactory);

		intAbsFxValue.setTextFormatter(new TextFormatter<>(DOUBLE_CONVERTER));
		intFxValue.setTextFormatter(new TextFormatter<>(DOUBLE_CONVERTER));
		meanXValue.setTextFormatter(new TextFormatter<>(DOUBLE_CONVERTER));
		meanYValue.setTextFormatter(new TextFormatter<>(DOUBLE_CONVERTER));
		stdevXValue.setTextFormatter(new TextFormatter<>(DOUBLE_CONVERTER));
		stdevYValue.setTextFormatter(new TextFormatter<>(DOUBLE_CONVERTER));

	}

	@FXML
	void close( ActionEvent event ) {
		getScene().getWindow().fireEvent(new WindowEvent(getScene().getWindow(), WINDOW_CLOSE_REQUEST));
	}

    @FXML
	@SuppressWarnings( "unchecked" )
    void dataSetSelected(ActionEvent event) {

		@SuppressWarnings( "unchecked" )
		XYChart<Number, Number> chart = (XYChart<Number, Number>) pluggable.getChart();
		LegendItem selection = dataSetValue.getValue();
		@SuppressWarnings( "unchecked" )
		Series<Number, Number> series = chart.getData().stream()
			.filter(s -> s.getName().equals(selection.getText()))
			.findFirst()
			.get();

		//	Calculate mean values.
		double meanX = 0.0;
		double meanY = 0.0;

		for ( Data<Number, Number> data : series.getData() ) {

			if ( ( data.getXValue() instanceof Number ) ) {
				meanX += data.getXValue().doubleValue();
			}

			if ( ( data.getYValue() instanceof Number ) ) {
				meanY += data.getYValue().doubleValue();
			}

		}

		meanX /= series.getData().size();
		meanY /= series.getData().size();

		((TextFormatter<Double>) meanXValue.getTextFormatter()).setValue(meanX);
		((TextFormatter<Double>) meanYValue.getTextFormatter()).setValue(meanY);

		//	Calculate rms values.
		double rmsX = 0.0;
		double rmsY = 0.0;

		for ( Data<Number, Number> data : series.getData() ) {

			if ( ( data.getXValue() instanceof Number ) ) {
				rmsX += ( data.getXValue().doubleValue() - meanX ) * ( data.getXValue().doubleValue() - meanX );
			}

			if ( ( data.getYValue() instanceof Number ) ) {
				rmsY += ( data.getYValue().doubleValue() - meanY ) * ( data.getYValue().doubleValue() - meanY );
			}

		}

		rmsX /= ( series.getData().size() - 1 );
		rmsY /= ( series.getData().size() - 1 );

		((TextFormatter<Double>) stdevXValue.getTextFormatter()).setValue(rmsX);
		((TextFormatter<Double>) stdevYValue.getTextFormatter()).setValue(rmsY);

		try {
			((TextFormatter<Double>) intFxValue.getTextFormatter()).setValue(trapezoidalRule(chart, series));
			((TextFormatter<Double>) intAbsFxValue.getTextFormatter()).setValue(absTrapezoidalRule(chart, series));
		} catch ( RuntimeException rex ) {
			((TextFormatter<Double>) intFxValue.getTextFormatter()).setValue(null);
			((TextFormatter<Double>) intAbsFxValue.getTextFormatter()).setValue(null);
			LogUtils.log(LOGGER, WARNING, rex.getMessage());
		}

    }
	
	void dispose() {
	}

	@SuppressWarnings( { "unchecked", "rawtypes" } )
	private double absTrapezoidalRule( XYChart<Number, Number> chart, Series<Number, Number> series ) {

		if ( !( ( chart instanceof AreaChart ) || ( chart instanceof LineChart ) ) ) {
			return 0;
		} else {

			ObservableList<Data<Number, Number>> data = series.getData();
			double area = 0.0;

			for ( int index = 1; index < data.size(); index++ ) {

				double b = data.get(index).getXValue().doubleValue();
				double a = data.get(index - 1).getXValue().doubleValue();
				double fb = data.get(index).getYValue().doubleValue();
				double fa = data.get(index - 1).getYValue().doubleValue();

				area += Math.abs(( b - a ) / 2 * ( fa + fb ));

			}

			return area;

		}

	}

	private void init() {

		URL resource = StatisticsController.class.getResource("fxml/statistics.fxml");

		try {

			FXMLLoader loader = new FXMLLoader(resource);

			loader.setController(this);
			loader.setRoot(this);
			loader.setResources(ResourceBundle.getBundle(StatisticsController.class.getPackageName() + ".StatisticsBundle"));
			loader.load();

		} catch ( IOException ex ) {
			LogUtils.log(LOGGER, SEVERE, ex, "Unable to load ''statistics.xml'' resource [{0}].", resource.toExternalForm());
		}

	}

	@SuppressWarnings( { "unchecked", "rawtypes" } )
	private double trapezoidalRule( XYChart<Number, Number> chart, Series<Number, Number> series ) {

		if ( !( ( chart instanceof AreaChart ) || ( chart instanceof LineChart ) ) ) {
			return 0;
		} else {

			ObservableList<Data<Number, Number>> data = series.getData();
			double area = 0.0;

			for ( int index = 1; index < data.size(); index++ ) {

				double b = data.get(index).getXValue().doubleValue();
				double a = data.get(index - 1).getXValue().doubleValue();
				double fb = data.get(index).getYValue().doubleValue();
				double fa = data.get(index - 1).getYValue().doubleValue();

				area += ( b - a ) / 2 * ( fa + fb );

			}

			return area;

		}

	}

}
