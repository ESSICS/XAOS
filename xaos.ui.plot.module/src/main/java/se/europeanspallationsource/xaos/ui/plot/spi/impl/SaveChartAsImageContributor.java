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


import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.beans.binding.Bindings;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.Chart;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Tooltip;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import org.apache.commons.lang3.StringUtils;
import se.europeanspallationsource.xaos.tools.annotation.BundleItem;
import se.europeanspallationsource.xaos.tools.annotation.BundleItems;
import se.europeanspallationsource.xaos.tools.annotation.Bundles;
import se.europeanspallationsource.xaos.tools.annotation.ServiceProvider;
import se.europeanspallationsource.xaos.ui.control.Icons;
import se.europeanspallationsource.xaos.ui.plot.PluggableChartContainer;
import se.europeanspallationsource.xaos.ui.plot.spi.ToolbarContributor;

import static java.util.logging.Level.SEVERE;
import static se.europeanspallationsource.xaos.ui.control.CommonIcons.FILE_IMAGE;


/**
 * A {@link ToolbarContributor} that allows to save a snapshot of the current
 * chart into an image file.
 *
 * @author claudio.rosati@esss.se
 * @srvc.order 100
 */
@ServiceProvider( service = ToolbarContributor.class, order = 100 )
public class SaveChartAsImageContributor implements ToolbarContributor {

	private static final Logger LOGGER = Logger.getLogger(SaveChartAsImageContributor.class.getName());

	private static File initialDirectory = new File(System.getProperty("user.dir"));

	@Override
	@BundleItem( key = "button.tooltip", message = "Save a snapshot of the current chart into an image file." )
	public Control provide( PluggableChartContainer chartContainer ) {

		Button button = new Button(null, Icons.iconFor(FILE_IMAGE, 14));

		button.disableProperty().bind(Bindings.isNull(chartContainer.pluggableProperty()));
		button.setTooltip(new Tooltip(getString("button.tooltip")));
		button.setOnAction(e -> saveChartAsImage(chartContainer.getPluggable().getChart()));

		return button;

	}

	private String getString ( String key, Object... parameters ) {
		return Bundles.get(SaveChartAsImageContributor.class, key, parameters);
	}

	@BundleItems( {
		@BundleItem( key = "chooser.filter", message = "Image Files" ),
		@BundleItem( key = "chooser.title", message = "Save Chart as Image" )
	} )
	private void saveChartAsImage( Chart chart ) {

		List<String> starExtensions = Stream.of(ImageIO.getWriterFileSuffixes())
			.sorted()
			.map(e -> "*." + e)
			.collect(Collectors.toList());
		StringBuilder builder = new StringBuilder(getString("chooser.filter"));

		builder.append(" (");

		starExtensions.forEach(e -> builder.append(e).append(", "));

		builder.deleteCharAt(builder.length() - 1);
		builder.deleteCharAt(builder.length() - 1);
		builder.append(")");

		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter(builder.toString(), starExtensions);

		fileChooser.getExtensionFilters().add(extensionFilter);
		fileChooser.setInitialFileName(StringUtils.isNotBlank(chart.getTitle()) ? chart.getTitle(): "snapshot");
		fileChooser.setInitialDirectory(initialDirectory);
		fileChooser.setTitle(getString("chooser.title"));

		File file = fileChooser.showSaveDialog(chart.getScene().getWindow());

		if ( file != null ) {

			initialDirectory = file.getParentFile();

			WritableImage image = chart.snapshot(new SnapshotParameters(), null);

			try {
				ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
			} catch ( IOException ex ) {
				LOGGER.log(
					SEVERE,
					MessageFormat.format("Unable to save chart as image file [{0}].", file),
					ex
				);
			}

		}

	}

}
