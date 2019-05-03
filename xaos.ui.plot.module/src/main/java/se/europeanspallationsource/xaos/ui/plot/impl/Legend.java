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
package se.europeanspallationsource.xaos.ui.plot.impl;


import java.util.function.Consumer;
import java.util.stream.Collectors;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.layout.Region;
import javafx.scene.layout.TilePane;

import static javafx.geometry.Orientation.HORIZONTAL;
import static javafx.geometry.Orientation.VERTICAL;


/**
 * A chart legend that displays a list of items with symbols in a {@link TilePane}.
 *
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class Legend extends TilePane {

	private static final int GAP = 6;

	private ListChangeListener<LegendItem> itemsListener = c -> {

		getChildren().setAll(getItems().stream().map(i -> i.checkBox).collect(Collectors.toList()));

		if ( isVisible() ) {
			requestLayout();
		}

	};

	/*
	 * *********************************************************************** *
	 * START OF JAVAFX PROPERTIES *
	 * ***********************************************************************
	 */

	/*
	 * ---- items --------------------------------------------------------------
	 */
	private final ObjectProperty<ObservableList<LegendItem>> items = new SimpleObjectProperty<>(this, "items") {

		ObservableList<LegendItem> oldItems = null;

		@Override protected void invalidated() {

			if ( oldItems != null ) {
				oldItems.removeListener(itemsListener);
			}

			getChildren().clear();

			ObservableList<LegendItem> newItems = get();

			if ( newItems != null ) {
				newItems.addListener(itemsListener);
				getChildren().addAll(newItems.stream().map(i -> i.checkBox).collect(Collectors.toList()));
			}

			oldItems = newItems;

			requestLayout();

		}

	};

	/**
	 * @return The legend items to display in this legend.
	 */
	public final ObjectProperty<ObservableList<LegendItem>> itemsProperty() {
		return items;
	}

	public final void setItems( ObservableList<LegendItem> value ) {
		itemsProperty().set(value);
	}

	public final ObservableList<LegendItem> getItems() {
		return itemsProperty().get();
	}

	/*
	 * ---- vertical -----------------------------------------------------------
	 */
	private final BooleanProperty vertical = new SimpleBooleanProperty(this, "vertical", false) {
		@Override protected void invalidated() {
			setOrientation(get() ? VERTICAL : HORIZONTAL);
		}
	};

    /**
	 * @return Whether legend items should be laid out vertically in columns
	 *         rather than horizontally in rows.
	 */
	public final BooleanProperty verticalProperty() {
		return vertical;
	}

	public final boolean isVertical() {
		return verticalProperty().get();
	}

	public final void setVertical( boolean value ) {
		verticalProperty().set(value);
	}

	/*
	 * *********************************************************************** *
	 * END OF JAVAFX PROPERTIES *
	 * ***********************************************************************
	 */

	public Legend() {

		super(GAP, GAP);

		getStyleClass().setAll("chart-legend");
		setId("legend");
		setTileAlignment(Pos.CENTER_LEFT);
		setItems(FXCollections.observableArrayList());

	}

	@Override
	protected double computePrefHeight( double forWidth ) {
		//	Legend prefHeight is zero if there are no legend items.
		return ( getItems().size() > 0 ) ? super.computePrefHeight(forWidth) : 0;
	}

	@Override
	protected double computePrefWidth( double forHeight ) {
		//	Legend prefWidth is zero if there are no legend items.
		return ( getItems().size() > 0 ) ? super.computePrefWidth(forHeight) : 0;
	}

	/**
	 * A item to be displayed on a Legend.
	 * <p>
	 * It is realized by a {@link CheckBox} whose text is the series name, and
	 * its color is the series one.</p>
	 *
	 * @css.class {@code chart-legend-item} for the legend text.
	 * @css.class {@code chart-legend-item-symbol} It should be defined if a
	 *            special default symbol is wanted. If defined it will take
	 *            precedence of the others.
	 * @css.class {@code chart-symbol}, {@code default-color1.chart-symbol},
	 *            {@code default-color2.chart-symbol}, {@code default-color3.chart-symbol},
	 *            {@code default-color4.chart-symbol}, {@code default-color5.chart-symbol},
	 *            {@code default-color6.chart-symbol} and {@code default-color6.chart-symbol}
	 *            for scatter charts.
	 * @css.class {@code chart-line-symbol} for line charts.
	 * @css.class {@code chart-area-symbol} and {@code area-legend-symbol} for
	 *            area charts.
	 * @css.class {@code bubble-legend-symbol} for bubble charts.
	 * @css.class {@code bar-legend-symbol} for bar charts.
	 */
	@SuppressWarnings( "PublicInnerClass" )
	public static class LegendItem {

		/**
		 * CheckBox used to represent the legend item.
		 */
		@SuppressWarnings( "PackageVisibleField" )
		final CheckBox checkBox = new CheckBox();

		/*
		 * ******************************************************************* *
		 * START OF JAVAFX PROPERTIES *
		 * *******************************************************************
		 */

		/*
		 * ---- symbol ---------------------------------------------------------
		 */
		private ObjectProperty<Node> symbol = new SimpleObjectProperty<>(LegendItem.this, "symbol", new Region()) {
			@Override
			protected void invalidated() {

				Node symbol = get();

				if ( symbol != null ) {
					symbol.getStyleClass().setAll("chart-legend-item-symbol");
				}

				checkBox.setGraphic(symbol);

			}
		};

		/**
		 * @return The symbol to use next to the item text, set to {@code null}
		 *         for no symbol. The default is a simple circle.
		 */
		public final ObjectProperty<Node> symbolProperty() {
			return symbol;
		}

		public final Node getSymbol() {
			return symbol.getValue();
		}

		public final void setSymbol( Node value ) {
			symbol.setValue(value);
		}

		/*
		 * ---- text -----------------------------------------------------------
		 */
		public final StringProperty textProperty() {
			return checkBox.textProperty();
		}

		public final String getText() {
			return checkBox.getText();
		}

		public final void setText( String value ) {
			checkBox.setText(value);
		}

		/*
		 * ******************************************************************* *
		 * END OF JAVAFX PROPERTIES *
		 * *******************************************************************
		 */

		public LegendItem( String text, Consumer<Boolean> checkBoxSelectionHandler ) {

			checkBox.getStyleClass().add("chart-legend-item");
			checkBox.setAlignment(Pos.CENTER_LEFT);
			checkBox.setContentDisplay(ContentDisplay.LEFT);
			checkBox.setGraphic(getSymbol());
			checkBox.setId("legend-check-box: " + text);
			checkBox.setSelected(true);
			checkBox.selectedProperty().addListener(( ob, ov, nv ) -> {
				if ( checkBoxSelectionHandler != null ) {
					checkBoxSelectionHandler.accept(nv);
				}
			});

			getSymbol().getStyleClass().setAll("chart-legend-item-symbol");
			setText(text);

		}

		public LegendItem( String text, Node symbol, Consumer<Boolean> checkBoxSelectionHandler ) {
			this(text, checkBoxSelectionHandler);
			setSymbol(symbol);
		}

	}

}
