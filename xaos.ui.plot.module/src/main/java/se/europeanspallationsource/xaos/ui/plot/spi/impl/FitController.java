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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.WeakHashMap;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory.DoubleSpinnerValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.apache.commons.lang3.StringUtils;
import se.europeanspallationsource.xaos.core.util.LogUtils;
import se.europeanspallationsource.xaos.tools.annotation.Bundle;
import se.europeanspallationsource.xaos.tools.annotation.BundleItem;
import se.europeanspallationsource.xaos.tools.annotation.BundleItems;
import se.europeanspallationsource.xaos.tools.annotation.Bundles;
import se.europeanspallationsource.xaos.ui.control.Icons;
import se.europeanspallationsource.xaos.ui.plot.Legend.LegendItem;
import se.europeanspallationsource.xaos.ui.plot.Plugin;
import se.europeanspallationsource.xaos.ui.plot.plugins.Pluggable;
import se.europeanspallationsource.xaos.ui.plot.spi.impl.trend.ExponentialTrendLine;
import se.europeanspallationsource.xaos.ui.plot.spi.impl.trend.GaussianTrendLine;
import se.europeanspallationsource.xaos.ui.plot.spi.impl.trend.LogarithmicTrendLine;
import se.europeanspallationsource.xaos.ui.plot.spi.impl.trend.PolynomialTrendLine;
import se.europeanspallationsource.xaos.ui.plot.spi.impl.trend.PowerTrendLine;
import se.europeanspallationsource.xaos.ui.plot.spi.impl.trend.TrendLine;

import static java.util.logging.Level.SEVERE;
import static javafx.stage.WindowEvent.WINDOW_CLOSE_REQUEST;
import static se.europeanspallationsource.xaos.ui.control.CommonIcons.WARNING;


/**
 * FXML Controller class
 *
 * @author claudiorosati
 */
@Bundle( name = "FitBundle" )
public class FitController extends GridPane implements Initializable {

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
	private static final Random RANDOM = new Random(System.nanoTime());
	/**
	 * Map of the lists of series labels added by the fitting function. These
	 * are saved so that can removed even if the fit popup is closed.
	 */
	private static final Map<Pluggable, List<Label>> SERIES_LABEL_MAP = new WeakHashMap<>(1);
	/**
	 * Map of the lists of series added by the fitting function. These are saved
	 * so that can removed even if the fit popup is closed.
	 */
	private static final Map<Pluggable, ObservableList<XYChart.Series<Number, Number>>> SERIES_MAP = new WeakHashMap<>(1);

	@FXML private Button applyButton;
	@FXML private Button clearAllButton;
	@FXML private Button clearButton;
	@FXML private Button closeButton;
	@FXML private ComboBox<LegendItem> dataSetValue;
	@FXML private Label degreeCaption;
	@FXML private Spinner<Double> degreeOrOffsetValue;
	@FXML private Spinner<Double> discretizationValue;
		  private List<Label> fittingLabelsList;
		  private ObservableList<XYChart.Series<Number, Number>> fittingSeriesList;
	@FXML private ComboBox<String> fittingValue;
	@FXML private Spinner<Double> maxXValue;
	@FXML private Spinner<Double> minXValue;
	@FXML private Label offsetCaption;
		  private final Pluggable pluggable;


	public FitController( Pluggable pluggable ) {

		this.pluggable = pluggable;
		this.fittingSeriesList = SERIES_MAP.get(pluggable);

		if ( this.fittingSeriesList == null ) {

			this.fittingSeriesList = FXCollections.observableArrayList();

			SERIES_MAP.put(pluggable, this.fittingSeriesList);

		}

		this.fittingLabelsList = SERIES_LABEL_MAP.get(pluggable);

		if ( this.fittingLabelsList == null ) {

			this.fittingLabelsList = new ArrayList<>(4);

			SERIES_LABEL_MAP.put(pluggable, this.fittingLabelsList);

		}

		init();

	}

	@BundleItems( {
		@BundleItem( key = "exponential.trend.line", message = "Exponential Trend Line" ),
		@BundleItem( key = "gaussian.trend.line", message = "Gaussian Trend Line" ),
		@BundleItem( key = "logarithmic.trend.line", message = "Logarithmic Trend Line" ),
		@BundleItem( key = "polynomial.trend.line", message = "Polynomial Trend Line" ),
		@BundleItem( key = "power.trend.line", message = "Power Trend Line" )
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

		degreeCaption.disableProperty().bind(Bindings.createBooleanBinding(
			() -> getString("logarithmic.trend.line").equals(fittingValue.getValue()),
			fittingValue.valueProperty()
		));
		degreeCaption.visibleProperty().bind(Bindings.equal(
			fittingValue.valueProperty(), 
			getString("polynomial.trend.line")
		));

		offsetCaption.disableProperty().bind(Bindings.createBooleanBinding(
			() -> getString("logarithmic.trend.line").equals(fittingValue.getValue()),
			fittingValue.valueProperty()
		));
		offsetCaption.visibleProperty().bind(Bindings.notEqual(
			fittingValue.valueProperty(),
			getString("polynomial.trend.line")
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

		clearAllButton.disableProperty().bind(Bindings.isEmpty(fittingSeriesList));
		clearButton.disableProperty().bind(Bindings.isEmpty(fittingSeriesList));

	}

	@BundleItems( {
		@BundleItem( key = "exponential.series.name", message = "Exponential Trend Line [{0}]" ),
		@BundleItem( key = "gaussian.series.name", message = "Gaussian Trend Line [{0}]" ),
		@BundleItem( key = "logarithmic.series.name", message = "Logarithmic Trend Line [{0}]" ),
		@BundleItem( key = "polynomial.series.name", message = "Polynomial Trend Line [{0}]" ),
		@BundleItem( key = "power.series.name", message = "Power Trend Line [{0}]" )
	} )
	@FXML
	@SuppressWarnings( "null" )
	void apply( ActionEvent event ) {

		@SuppressWarnings( "unchecked" )
		XYChart<Number, Number> chart = (XYChart<Number, Number>) pluggable.getChart();
		LegendItem selection = dataSetValue.getValue();
		@SuppressWarnings( "unchecked" )
		Series<Number, Number> series = chart.getData().stream()
			.filter(s -> s.getName().equals(selection.getText()))
			.findFirst()
			.get();
		List<Double> x = new ArrayList<>(series.getData().size());
		List<Double> y = new ArrayList<>(series.getData().size());
		boolean polyOrLog = getString("polynomial.trend.line").equals(fittingValue.getValue())
						 || getString("logarithmic.trend.line").equals(fittingValue.getValue());
		double maxXVal = maxXValue.getValue();
		double minXVal = minXValue.getValue();
		double degreeOrOffset = degreeOrOffsetValue.getValue();

		series.getData().forEach(data -> {
			if ( data.getXValue().doubleValue() >= minXVal && data.getXValue().doubleValue() <= maxXVal ) {
				x.add(data.getXValue().doubleValue());
				y.add(data.getYValue().doubleValue() - ( polyOrLog ? 0 : degreeOrOffset ));
			}
		});

		Double disc = discretizationValue.getValue();
		double maxX = x.stream().mapToDouble(v -> v).max().getAsDouble();
		double minX = x.stream().mapToDouble(v -> v).min().getAsDouble();
		double range = maxX - minX;
		XYChart.Series<Number, Number> interpolatedVals = new Series<>();
		TrendLine t = null;

		if ( getString("polynomial.trend.line").equals(fittingValue.getValue()) ) {

			t = new PolynomialTrendLine((int) degreeOrOffset);

			t.setValues(y.stream().mapToDouble(d -> d).toArray(), x.stream().mapToDouble(d -> d).toArray());

			for ( int i = 0; i <= disc; i++ ) {
				interpolatedVals.getData().add(new Data<>(minX + range / disc * i, t.predict(minX + range / disc * i)));
			}

			interpolatedVals.setName(getString("polynomial.series.name", selection.getText()));

		} else if ( getString("logarithmic.trend.line").equals(fittingValue.getValue()) ) {
			
			t = new LogarithmicTrendLine();

			t.setValues(y.stream().mapToDouble(d -> d).toArray(), x.stream().mapToDouble(d -> d).toArray());

			for ( int i = 0; i <= disc; i++ ) {
				interpolatedVals.getData().add(new Data<>(minX + range / disc * i, t.predict(minX + range / disc * i)));
			}

			interpolatedVals.setName(getString("logarithmic.series.name", selection.getText()));

		} else if ( getString("power.trend.line").equals(fittingValue.getValue()) ) {

			t = new PowerTrendLine(degreeOrOffset);

			t.setValues(y.stream().mapToDouble(d -> d).toArray(), x.stream().mapToDouble(d -> d).toArray());

			for ( int i = 0; i <= disc; i++ ) {
				interpolatedVals.getData().add(new Data<>(minX + range / disc * i, t.predict(minX + range / disc * i) + t.getOffset()));
			}

			interpolatedVals.setName(getString("power.series.name", selection.getText()));

		} else if ( getString("exponential.trend.line").equals(fittingValue.getValue()) ) {

			t = new ExponentialTrendLine(degreeOrOffset);

			t.setValues(y.stream().mapToDouble(d -> d).toArray(), x.stream().mapToDouble(d -> d).toArray());

			for ( int i = 0; i <= disc; i++ ) {
				interpolatedVals.getData().add(new Data<>(minX + range / disc * i, t.predict(minX + range / disc * i) + t.getOffset()));
			}

			interpolatedVals.setName(getString("exponential.series.name", selection.getText()));

		} else if ( getString("gaussian.trend.line").equals(fittingValue.getValue()) ) {

			t = new GaussianTrendLine();

			t.setValues(y.stream().mapToDouble(d -> d).toArray(), x.stream().mapToDouble(d -> d).toArray());

			for ( int i = 0; i <= disc; i++ ) {
				interpolatedVals.getData().add(new Data<>(minX + range / disc * i, t.predict(minX + range / disc * i) + degreeOrOffset));
			}

			interpolatedVals.setName(getString("gaussian.series.name", selection.getText()));

		}

		chart.getData().add(interpolatedVals);
		pluggable.setNotShownInLegend(interpolatedVals.getName());

		interpolatedVals.getData().forEach(data -> data.getNode().setVisible(false));

		String[] styles = createStyles();
		Node interpolatedValsNode = interpolatedVals.getNode();

		if ( interpolatedValsNode != null ) {
			if ( interpolatedValsNode instanceof Group ) {

				Group group = (Group) interpolatedValsNode;

				for ( int i = 0; i < group.getChildren().size() - 1; i++ ) {
					group.getChildren().get(i).setStyle("-fx-fill: null;");
				}

				group.getChildren().get(group.getChildren().size() - 1).setStyle(styles[0]);


			} else {
				interpolatedValsNode.setStyle(styles[0]);
			}
		}

		fittingSeriesList.add(interpolatedVals);

		Label fittingLabel = new Label(t.nameFor(interpolatedVals.getName()));

		fittingLabel.getStyleClass().add("chart-fitting-label");
		fittingLabel.setStyle(styles[1]);
		fittingLabel.setManaged(false);;
		fittingLabel.resizeRelocate(
			chart.getLayoutX() + 10,
			chart.getLayoutY() + fittingLabelsList.size() * 60 + 10,
			getLabelWidth(fittingLabel.getText()),
			50
		);

		if ( t.isErrorOccurred() ) {

			Text icon = (Text) Icons.iconFor(WARNING, 16);

			icon.setFill(Color.RED);

			fittingLabel.setGraphic(icon);
			fittingLabel.setContentDisplay(ContentDisplay.RIGHT);
			fittingLabel.setGraphicTextGap(6);
			fittingLabel.resize(getLabelWidth(fittingLabel.getText()) + 10, 50);

		}

		fittingLabelsList.add(fittingLabel);
		pluggable.getPlotChildren().add(fittingLabel);

	}

	@FXML
	void clear( ActionEvent event ) {

		@SuppressWarnings( "unchecked" )
		XYChart<Number, Number> chart = (XYChart<Number, Number>) pluggable.getChart();
		int index = fittingSeriesList.size() - 1;

		chart.getData().remove(fittingSeriesList.remove(index));
		pluggable.getPlotChildren().remove(fittingLabelsList.remove(index));

	}

	@FXML
	void close( ActionEvent event ) {
		getScene().getWindow().fireEvent(new WindowEvent(getScene().getWindow(), WINDOW_CLOSE_REQUEST));
	}

	@FXML
	void clearAll( ActionEvent event ) {
		while ( !fittingSeriesList.isEmpty() ) {
			clear(event);
		}
	}

	void dispose() {
		clearAllButton.disableProperty().unbind();
		clearButton.disableProperty().unbind();
	}

	/**
	 * @return The style for the fitting line at index 0, and the style for the
	 *         corresponding label border at index 1.
	 */
	private String[] createStyles() {

		int phases = 1 + RANDOM.nextInt(3);
		String[] styles = new String[] { "", "" };

		for ( int i = 0; i < phases; i++ ) {

			int empty = 3 + RANDOM.nextInt(8);
			int full = 3 + RANDOM.nextInt(10);

			styles[0] = styles[0] + " "  + empty + "px "  + full + "px";
			styles[1] = styles[1] + ", " + empty + "px, " + full + "px";

		}

		styles[0] = styles[0].substring(1);
		styles[1] = styles[1].substring(2);

		styles[0] = "-fx-stroke: black; -fx-stroke-dash-array: " + styles[0] + "; -fx-stroke-width: 2.2;";
		styles[1] = "-fx-border-style: segments(" + styles[1] + ") centered;";

		return styles;

	}

	@SuppressWarnings( "ResultOfObjectAllocationIgnored" )
	private double getLabelWidth ( String string ) {

		Text text = new Text(string);

		new Scene(new Group(text));
		text.applyCss();

		return 25 + text.getLayoutBounds().getWidth();

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
			LogUtils.log(LOGGER, SEVERE, ex, "Unable to load ''fit.xml'' resource [{0}].", resource.toExternalForm());
		}

	}

}
