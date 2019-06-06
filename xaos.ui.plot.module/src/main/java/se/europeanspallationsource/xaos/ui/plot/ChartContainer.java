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
package se.europeanspallationsource.xaos.ui.plot;


import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import org.controlsfx.control.HiddenSidesPane;
import se.europeanspallationsource.xaos.tools.annotation.BundleItem;
import se.europeanspallationsource.xaos.tools.annotation.BundleItems;
import se.europeanspallationsource.xaos.ui.control.Icons;

import static se.europeanspallationsource.xaos.ui.control.CommonIcons.INFO;
import static se.europeanspallationsource.xaos.ui.control.CommonIcons.PIN;
import static se.europeanspallationsource.xaos.ui.util.FXUtils.makeSquare;


/**
 * A container for {@link XYChart}s where tool-bar is displayed when the mouse
 * cursor is close to the top border. Once the tool-bar is displayed it can be
 * pinned.
 * <p>
 * <b>Note:</b> The {@link #setContent(javafx.scene.Node)} should never be used,
 * preferring {@link #setChart(javafx.scene.chart.XYChart)} instead.</p>
 *
 * @author claudio.rosati@esss.se
 * @css.class {@code chart-container-toolbar}
 */
public class ChartContainer extends HiddenSidesPane {

	private final ToolBar toolbar = new ToolBar();

	/* *********************************************************************** *
	 * START OF JAVAFX PROPERTIES                                              *
	 * *********************************************************************** */

	/*
	 * ---- chart --------------------------------------------------------------
	 */
    private final ObjectProperty<XYChart<?, ?>> chart = new SimpleObjectProperty<>(ChartContainer.this, "chart") {
        @Override
		protected void invalidated() {
			setContent(get());
        }
    };

	public final ObjectProperty<XYChart<?, ?>> chartProperty() {
		return chart;
	}

	public final XYChart<?, ?> getChart() {
		return chartProperty().get();
	}

	public final void setChart( XYChart<?, ?> value ) {
		chartProperty().set(value);
	}

	/* *********************************************************************** *
	 * END OF JAVAFX PROPERTIES                                                *
	 * *********************************************************************** */

	/**
	 * Creates a new instance of this container.
	 */
	@BundleItems({
		@BundleItem(key = "infoButton.tooltip", message = "Open/close the plugins info dialog."),
		@BundleItem(key = "pinButton.tooltip", message = "Pin/unpin toolbar.")
	})
	public ChartContainer() {

		//	Horizontal filler...
		Pane filler = new Pane();

		HBox.setHgrow(filler, Priority.ALWAYS);

		//	Info/Help button...
		ToggleButton infoButton = new ToggleButton(null, Icons.iconFor(INFO, 14));
//		infoButton.setOnAction(e -> handleInfoButton(infoButton));
//		infoButton.setTooltip(new Tooltip(Bundles.getLocalizedString("TooledUpChartContainer.infoButton.tooltip")));
//		infoButton.disableProperty().bind(Bindings.isNull(chartProperty()));

		//	Pin button...
		ToggleButton pinButton = new ToggleButton(null, Icons.iconFor(PIN, 14));

//		pinButton.setOnAction(e -> setPinnedSide(pinButton.isSelected() ? TOP : null));
//		pinButton.setTooltip(new Tooltip(Bundles.getLocalizedString("TooledUpChartContainer.pinButton.tooltip")));

		//	Setup the toolbar
//		toolbar.setOpacity(0.66);

		ObservableList<Node> tItems = toolbar.getItems();

		tItems.add(filler);
		tItems.add(makeSquare(infoButton, 22));
//		tItems.add(new Separator());
		tItems.add(makeSquare(pinButton, 22));

		setTop(toolbar);

	}

	/**
	 * Creates a new instance of this container with the given {@code chart} in
	 * its center and a toolbar on the top side, sliding down when the mouse
	 * cursor is close to it.
	 *
	 * @param chart The {@link XYChart} to be contained.
	 */
	public ChartContainer( XYChart<?, ?> chart ) {
		this();
		setChart(chart);
	}

	private void handleInfoButton( ToggleButton infoButton ) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

}
