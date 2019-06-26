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


import java.net.URL;
import java.util.ServiceLoader;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Accordion;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Control;
import javafx.scene.control.Separator;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.web.WebView;
import org.controlsfx.control.HiddenSidesPane;
import org.controlsfx.control.PopOver;
import se.europeanspallationsource.xaos.core.util.LogUtils;
import se.europeanspallationsource.xaos.tools.annotation.BundleItem;
import se.europeanspallationsource.xaos.tools.annotation.BundleItems;
import se.europeanspallationsource.xaos.tools.annotation.Bundles;
import se.europeanspallationsource.xaos.tools.annotation.ServiceLoaderUtilities;
import se.europeanspallationsource.xaos.ui.control.Icons;
import se.europeanspallationsource.xaos.ui.plot.plugins.Pluggable;
import se.europeanspallationsource.xaos.ui.plot.spi.ToolbarContributor;

import static java.util.logging.Level.WARNING;
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

	private static final Logger LOGGER = Logger.getLogger(PluggableChartContainer.class.getName());
	private final ToggleButton pinButton = new ToggleButton(null, Icons.iconFor(PIN, 14));
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

	public ToggleButton getPinButton() {
		return pinButton;
	}

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

		//	Info/Help button...
		infoButton.setOnAction(e -> handleInfoButton(infoButton));
		infoButton.setTooltip(new Tooltip(getString("infoButton.tooltip")));
		infoButton.disableProperty().bind(Bindings.or(
			Bindings.isNull(pluggableProperty()),
			Bindings.or(
				Bindings.selectBoolean(pluggableProperty(), "plugins", "empty"),
				infoButton.selectedProperty()
			)
		));

		//	Pin button...
		pinButton.setOnAction(e -> setPinnedSide(pinButton.isSelected() ? TOP : null));
		pinButton.setTooltip(new Tooltip(getString("pinButton.tooltip")));

		//	Setup the toolbar
		toolbar.setOpacity(0.66);

		ObservableList<Node> tItems = toolbar.getItems();

		ServiceLoaderUtilities.of(ServiceLoader.load(ToolbarContributor.class)).forEach(tc -> {

			if ( tc.isPrecededBySeparator() ) {
				tItems.add(new Separator());
			}

			Control element = tc.provide(this);

			if ( element != null ) {
				if ( element instanceof ButtonBase ) {
					tItems.add(makeSquare(element, 22));
				} else {
					tItems.add(element);
				}
			} else {
				LogUtils.log(LOGGER, WARNING, "Null component provided by ''{0}''.", tc.getClass());
			}

		});

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

	private String getString ( String key ) {
		return Bundles.get(PluggableChartContainer.class, key);
	}

	@BundleItems({
		@BundleItem(
			key = "html.language.variation",
			comment = "The extension will be added to class name, before the '.html' extension.\n"
					+ "It could be something like '_it', or '_fr'.",
			message = ""
		),
		@BundleItem( key = "infoPopOver.title", message = "Plugins Info")
	})
	private void handleInfoButton( ToggleButton infoButton ) {

		if ( !pinButton.isSelected() ) {
			pinButton.fire();
		}

		Accordion accordion = new Accordion();

		getPluggable().getPlugins().stream()
			.sorted(( p1, p2 ) -> p1.getName().compareToIgnoreCase(p2.getName()))
			.forEach(p -> {

				//	Looking at the "htmls" package for an HTML file named
				//	"class-name.html", where "class-name" is the actual plugin
				//	simple class name. That HTML page should contain a
				//	descritpion for the user about what the plugin does.
				//	The HTML file name can have locale extensions. See the
				//	above BundleItem entry.
				String htmlResourceName = "/htmls/"
										+ p.getClass().getSimpleName()
										+ getString("html.language.variation")
										+ ".html";
				URL htmlResourceURL = getClass().getResource(htmlResourceName);

				if ( htmlResourceURL != null ) {

					WebView view = new WebView();

					view.getEngine().load(htmlResourceURL.toExternalForm());
					view.setPrefSize(500, 250);
					accordion.getPanes().add(new TitledPane(p.getName(), view));

				}

			});

		accordion.setExpandedPane(accordion.getPanes().get(0));

		PopOver popOver = new PopOver(accordion);

		popOver.setAnimated(false);
		popOver.setCloseButtonEnabled(true);
		popOver.setDetachable(true);
		popOver.setHeaderAlwaysVisible(true);
		popOver.setHideOnEscape(true);
		popOver.setArrowLocation(TOP_RIGHT);
		popOver.setOnHidden(e -> infoButton.setSelected(false));
		popOver.setTitle(getString("infoPopOver.title"));

		popOver.show(infoButton);


	}

}
