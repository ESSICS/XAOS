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


import java.text.DecimalFormat;
import java.text.Format;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.layout.Region;
import javafx.scene.layout.TilePane;


/**
 * A chart legend that displays a list of items with symbols in a {@link TilePane}.
 *
 * @author claudio.rosati@esss.se
 */
public class Legend extends TilePane {

	private static final int GAP = 5;

	/*
	 * *********************************************************************** *
	 * START OF JAVAFX PROPERTIES *
	 * ***********************************************************************
	 */

	/*
	 * ---- formatter ----------------------------------------------------------
	 */
	private final ObjectProperty<Format> formatter = new SimpleObjectProperty<>(this, "formatter", new DecimalFormat("0.000"));

	/**
	 * @return The {@link Format} of the cursor display.
	 */
	public final ObjectProperty<Format> formatterProperty() {
		return formatter;
	}

	public final Format getFormatter() {
		return formatterProperty().get();
	}

	public final void setFormatter( Format value ) {
		formatterProperty().set(value);
	}

	/*
	 * *********************************************************************** *
	 * END OF JAVAFX PROPERTIES *
	 * ***********************************************************************
	 */

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
		private CheckBox checkBox = new CheckBox();

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

			@Override
			public Object getBean() {
				return LegendItem.this;
			}

			@Override
			public String getName() {
				return "symbol";
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
		private StringProperty text = new SimpleStringProperty(LegendItem.this, "text", checkBox.getText()) {
			@Override protected void invalidated() {
				checkBox.setText(get());
			}
		};

		/**
		 * @return The item text.
		 */
		public final StringProperty textProperty() {
			return text;
		}

		public final String getText() {
			return text.getValue();
		}

		public final void setText( String value ) {
			text.setValue(value);
		}

		/*
		 * ******************************************************************* *
		 * END OF JAVAFX PROPERTIES *
		 * *******************************************************************
		 */

		public LegendItem( String text ) {

			checkBox.getStyleClass().add("chart-legend-item");
			checkBox.setAlignment(Pos.CENTER_LEFT);
			checkBox.setContentDisplay(ContentDisplay.LEFT);
			checkBox.setGraphic(getSymbol());

			getSymbol().getStyleClass().setAll("chart-legend-item-symbol");
			setText(text);

		}

		public LegendItem( String text, Node symbol ) {
			this(text);
			setSymbol(symbol);
		}
		
	}

}
