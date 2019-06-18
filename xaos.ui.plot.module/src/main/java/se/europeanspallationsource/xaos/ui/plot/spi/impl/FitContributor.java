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


import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Control;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import org.controlsfx.control.PopOver;
import se.europeanspallationsource.xaos.tools.annotation.BundleItem;
import se.europeanspallationsource.xaos.tools.annotation.BundleItems;
import se.europeanspallationsource.xaos.tools.annotation.Bundles;
import se.europeanspallationsource.xaos.tools.annotation.ServiceProvider;
import se.europeanspallationsource.xaos.ui.control.Icons;
import se.europeanspallationsource.xaos.ui.plot.PluggableChartContainer;
import se.europeanspallationsource.xaos.ui.plot.spi.ToolbarContributor;

import static org.controlsfx.control.PopOver.ArrowLocation.TOP_CENTER;
import static se.europeanspallationsource.xaos.ui.control.CommonIcons.BLUR_OFF;


/**
 * A {@link ToolbarContributor} that allows to save a snapshot of the current
 * chart into an image file.
 *
 * @author claudio.rosati@esss.se
 * @srvc.order 300
 */
@ServiceProvider( service = ToolbarContributor.class, order = 300 )
public class FitContributor implements ToolbarContributor {

	private static final Logger LOGGER = Logger.getLogger(FitContributor.class.getName());

	@Override
	public boolean isPrecededBySeparator() {
		return true;
	}

	@Override
	@BundleItem( key = "button.tooltip", message = "Fit Data" )
	public Control provide( PluggableChartContainer chartContainer ) {

		Node icon = Icons.iconFor(BLUR_OFF, 14);

		icon.setRotate(90);

		ToggleButton button = new ToggleButton(null, icon);

		button.setTooltip(new Tooltip(getString("button.tooltip")));
		button.setOnAction(e -> handleButton(chartContainer, button));
		button.disableProperty().bind(Bindings.createBooleanBinding(() -> {
				return chartContainer.getPluggable() == null
					|| !( chartContainer.getPluggable().getChart() instanceof XYChart )
					|| button.isSelected();
			},
			chartContainer.pluggableProperty(),
			button.selectedProperty()
		));

		return button;

	}

	private String getString( String key, Object... parameters ) {
		return Bundles.get(FitContributor.class, key, parameters);
	}

	@BundleItems({
		@BundleItem( key = "popOver.title", message = "Fit Data")
	})
	private void handleButton( PluggableChartContainer chartContainer, ToggleButton button ) {
		
		ToggleButton pinButton = chartContainer.getPinButton();

		if ( !pinButton.isSelected() ) {
			pinButton.fire();
		}

		FitController controller = new FitController(chartContainer.getPluggable());
		PopOver popOver = new PopOver(controller);

		popOver.setAnimated(false);
		popOver.setCloseButtonEnabled(true);
		popOver.setDetachable(true);
		popOver.setHeaderAlwaysVisible(true);
		popOver.setHideOnEscape(true);
		popOver.setArrowLocation(TOP_CENTER);
		popOver.setOnHidden(e -> button.setSelected(false));
		popOver.setTitle(getString("popOver.title"));

		popOver.show(button);

	}

}
