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
import javafx.scene.layout.TilePane;


/**
 * A chart legend that displays a list of items with symbols in a {@link TilePane}.
 *
 * @author claudio.rosati@esss.se
 */
public class Legend extends TilePane {

    private static final int GAP = 5;

	/* *********************************************************************** *
	 * START OF JAVAFX PROPERTIES                                              *
	 * *********************************************************************** */

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

	/* *********************************************************************** *
	 * END OF JAVAFX PROPERTIES                                                *
	 * *********************************************************************** */

	/**
	 * A item to be displayed on a Legend.
	 * <p>
	 * It is realized by a {@link CheckBox} whose text is the series name, and
	 * its colour is the series one.</p>
	 */
//	public static class LegendItem {
//
//		/**
//		 * CheckBox used to represent the legend item.
//		 */
//		private CheckBox checkBox = new CheckBox();
//
//		/* ******************************************************************* *
//		 * START OF JAVAFX PROPERTIES                                          *
//		 * ******************************************************************* */
//
//		/*
//		 * ---- symbol ---------------------------------------------------------
//		 */
//		/*
//		 * ---- text -----------------------------------------------------------
//		 */
//		private StringProperty text = new SimpleStringProperty(LegendItem.this, "text", checkBox.getText()) {
//			@Override protected void invalidated() {
//				checkBox.setText(get());
//			}
//		};
//
//		/**
//		 * @return The item text.
//		 */
//		public final StringProperty textProperty() {
//			return text;
//		}
//
//		public final String getText() {
//			return text.getValue();
//		}
//
//		public final void setText( String value ) {
//			text.setValue(value);
//		}
//
//		/* ******************************************************************* *
//		 * END OF JAVAFX PROPERTIES                                            *
//		 * ******************************************************************* */
//
//		/**
//		 * The symbol to use next to the item text, set to null for no symbol. The default is a simple square of symbolFill
//		 */
//		//new Rectangle(8,8,null)
//		private ObjectProperty<Node> symbol = new ObjectPropertyBase<Node>(new Region()) {
//			@Override protected void invalidated() {
//				Node symbol = get();
//				if ( symbol != null ) {
//					symbol.getStyleClass().setAll("chart-legend-item-symbol");
//				}
//				checkBox.setGraphic(symbol);
//			}
//
//			@Override
//			public Object getBean() {
//				return LegendItem.this;
//			}
//
//			@Override
//			public String getName() {
//				return "symbol";
//			}
//		};
//
//		public final Node getSymbol() {
//			return symbol.getValue();
//		}
//
//		public final void setSymbol( Node value ) {
//			symbol.setValue(value);
//		}
//
//		public final ObjectProperty<Node> symbolProperty() {
//			return symbol;
//		}
//
//		public LegendItem( String text ) {
//			setText(text);
//			checkBox.getStyleClass().add("chart-legend-item");
//			checkBox.setAlignment(Pos.CENTER_LEFT);
//			checkBox.setContentDisplay(ContentDisplay.LEFT);
//			checkBox.setGraphic(getSymbol());
//			getSymbol().getStyleClass().setAll("chart-legend-item-symbol");
//		}
//
//		public LegendItem( String text, Node symbol ) {
//			this(text);
//			setSymbol(symbol);
//		}
//	}
//
}
