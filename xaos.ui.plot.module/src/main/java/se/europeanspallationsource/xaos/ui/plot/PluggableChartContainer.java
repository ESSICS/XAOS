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


import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.text.TextAlignment;
import javafx.scene.web.WebView;
import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.HiddenSidesPane;
import org.controlsfx.control.PopOver;
import se.europeanspallationsource.xaos.tools.annotation.BundleItem;
import se.europeanspallationsource.xaos.tools.annotation.BundleItems;
import se.europeanspallationsource.xaos.tools.annotation.Bundles;
import se.europeanspallationsource.xaos.ui.control.Icons;
import se.europeanspallationsource.xaos.ui.plot.plugins.Pluggable;

import static javafx.geometry.Side.TOP;
import static org.controlsfx.control.PopOver.ArrowLocation.TOP_RIGHT;
import static se.europeanspallationsource.xaos.ui.control.CommonIcons.INFO;
import static se.europeanspallationsource.xaos.ui.control.CommonIcons.PIN;
import static se.europeanspallationsource.xaos.ui.util.FXUtils.makeSquare;


/**
 * A container for {@link XYChart}s where tool-bar is displayed when the mouse
 * cursor is close to the top border. Once the tool-bar is displayed it can be
 * pinned.
 * <p>
 * <b>Note:</b> The {@link #setContent(javafx.scene.Node)} should never be used,
 * preferring {@link #setPluggable(Pluggable)} instead.</p>
 *
 * @author claudio.rosati@esss.se
 * @css.class {@code chart-container-toolbar}
 */
public class PluggableChartContainer extends HiddenSidesPane {

	private final ToolBar toolbar = new ToolBar();

	/* *********************************************************************** *
	 * START OF JAVAFX PROPERTIES                                              *
	 * *********************************************************************** */

	/*
	 * ---- pluggable ----------------------------------------------------------
	 */
    private final ObjectProperty<Pluggable> pluggable = new SimpleObjectProperty<>(PluggableChartContainer.this, "pluggable") {
        @Override
		protected void invalidated() {
			setContent(get().getChart());
        }
    };

	public final ObjectProperty<Pluggable> pluggableProperty() {
		return pluggable;
	}

	public final Pluggable getPluggable() {
		return pluggableProperty().get();
	}

	public final void setPluggable( Pluggable value ) {
		pluggableProperty().set(value);
	}

	/* *********************************************************************** *
	 * END OF JAVAFX PROPERTIES                                                *
	 * *********************************************************************** */

	/**
	 * Creates a new instance of this container.
	 */
	@BundleItems( {
		@BundleItem( key = "infoButton.tooltip", message = "Open/close the plugins info dialog." ),
		@BundleItem( key = "pinButton.tooltip", message = "Pin/unpin toolbar." )
	} )
	public PluggableChartContainer() {

		//	Horizontal filler...
		Pane filler = new Pane();

		HBox.setHgrow(filler, Priority.ALWAYS);

		//	Buttons created all together in order to be passed to handling
		//	callbacks.
		ToggleButton infoButton = new ToggleButton(null, Icons.iconFor(INFO, 14));
		ToggleButton pinButton = new ToggleButton(null, Icons.iconFor(PIN, 14));

		//	Info/Help button...
		infoButton.setOnAction(e -> handleInfoButton(infoButton, pinButton));
		infoButton.setTooltip(new Tooltip(Bundles.get(PluggableChartContainer.class, "infoButton.tooltip")));
		infoButton.disableProperty().bind(Bindings.or(
			Bindings.isNull(pluggableProperty()),
			Bindings.selectBoolean(pluggableProperty(), "plugins", "empty")
		));


		//	Pin button...
		pinButton.setOnAction(e -> setPinnedSide(pinButton.isSelected() ? TOP : null));
		pinButton.setTooltip(new Tooltip(Bundles.get(PluggableChartContainer.class, "pinButton.tooltip")));

		//	Setup the toolbar
		toolbar.setOpacity(0.66);

		ObservableList<Node> tItems = toolbar.getItems();

//		tItems.add(new Separator());
		tItems.add(filler);
		tItems.add(makeSquare(infoButton, 22));
		tItems.add(makeSquare(pinButton, 22));

		setTop(toolbar);

	}

	/**
	 * Creates a new instance of this container with the given {@code chart} in
	 * its center and a toolbar on the top side, sliding down when the mouse
	 * cursor is close to it.
	 *
	 * @param pluggable The {@link Pluggable} chart to be contained.
	 */
	public PluggableChartContainer( Pluggable pluggable ) {
		this();
		setPluggable(pluggable);
	}

	@BundleItem( key = "infoPopOver.noPluginsLabel", message = "No plugins.")
	private Node createInfoContent() {

		Pluggable plggbl = getPluggable();

		if ( plggbl == null || plggbl.getPlugins().isEmpty() ) {

			//	In theory this block of code should never be executed because of
			//	the bindings of the infoButton... but just in case something
			//	goes wrong...
			Label noPluginsLabel = new Label(Bundles.get(PluggableChartContainer.class, "infoPopOver.noPluginsLabel"));

			noPluginsLabel.setAlignment(Pos.CENTER);
			noPluginsLabel.setTextAlignment(TextAlignment.CENTER);
			noPluginsLabel.setMinSize(300, 200);

			return noPluginsLabel;

		} else {

			Accordion accordion = new Accordion();

			accordion.setMinSize(300, 200);

			plggbl.getPlugins().stream()
				.sorted(( p1, p2 ) -> p1.getName().compareToIgnoreCase(p2.getName()))
				.forEach(p -> {

					String descriptionPage = p.getHTMLDescription();

					if ( StringUtils.isNotBlank(descriptionPage) ) {

						WebView view = new WebView();

						view.getEngine().loadContent(descriptionPage);
						view.setPrefSize(500, 300);
						accordion.getPanes().add(new TitledPane(p.getName(), view));

					}

				});

			return accordion;

		}

	}

	@BundleItem( key = "infoPopOver.title", message = "Plugins Info")
	private void handleInfoButton( ToggleButton infoButton, ToggleButton pinButton ) {

		infoButton.setCursor(Cursor.WAIT);

		if ( !pinButton.isSelected() ) {
			pinButton.fire();
		}

		PopOver popOver = new PopOver(createInfoContent());

		popOver.setAnimated(true);
		popOver.setCloseButtonEnabled(true);
		popOver.setDetachable(true);
		popOver.setHeaderAlwaysVisible(true);
		popOver.setArrowLocation(TOP_RIGHT);
		popOver.setOnShown(e -> { 
			popOver.getContentNode().requestFocus();
			infoButton.setCursor(Cursor.DEFAULT);
		});
		popOver.setOnHidden(e -> infoButton.setSelected(false));
		popOver.setTitle(Bundles.get(PluggableChartContainer.class, "infoPopOver.title"));


		popOver.show(infoButton);

	}

}
