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


import chart.DensityChartFX;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.logging.Logger;
import javafx.scene.chart.Chart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Tooltip;
import javafx.stage.FileChooser;
import org.apache.commons.lang3.StringUtils;
import se.europeanspallationsource.xaos.tools.annotation.BundleItem;
import se.europeanspallationsource.xaos.tools.annotation.BundleItems;
import se.europeanspallationsource.xaos.tools.annotation.Bundles;
import se.europeanspallationsource.xaos.tools.annotation.ServiceProvider;
import se.europeanspallationsource.xaos.ui.control.Icons;
import se.europeanspallationsource.xaos.ui.plot.PluggableChartContainer;
import se.europeanspallationsource.xaos.ui.plot.spi.ToolbarContributor;

import static java.util.logging.Level.SEVERE;
import static se.europeanspallationsource.xaos.ui.control.CommonIcons.TABLE;


/**
 * A {@link ToolbarContributor} that allows to save data of the current chart
 * into a {@code .csv} file.
 *
 * @author claudio.rosati@esss.se
 */
@BundleItems( {
	@BundleItem( key = "button.tooltip", message = "Save current chart data into a ''.csv'' file." ),
	@BundleItem( key = "chooser.filter", message = "Data Files" ),
	@BundleItem( key = "chooser.title", message = "Save Chart Data" )
} )
@ServiceProvider( service = ToolbarContributor.class, order = 200 )
public class SaveChartDataContributor implements ToolbarContributor {

	private static final Logger LOGGER = Logger.getLogger(SaveChartDataContributor.class.getName());

	private static File initialDirectory = new File(System.getProperty("user.dir"));

	@Override
	public Control provide( PluggableChartContainer chartContainer ) {

		Button button = new Button(null, Icons.iconFor(TABLE, 14));

		button.setTooltip(new Tooltip(getString("button.tooltip")));
		button.setOnAction(e -> saveChartData(chartContainer.getPluggable().getChart()));

		return button;

	}

	private String getString ( String key ) {
		return Bundles.get(SaveChartDataContributor.class, key);
	}

	private void saveChartData( Chart chart ) {

		if ( !( chart instanceof XYChart ) && !( chart instanceof DensityChartFX ) ) {
			LOGGER.warning(MessageFormat.format(
				"Given chart ({0}) is not a XYChart nor a DensityChartFX [{1}].",
				chart.getTitle(),
				chart.getClass().getName()
			));
			return;
		}

		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter(getString("chooser.filter") + " (*.csv)", "*.csv");

		fileChooser.getExtensionFilters().add(extensionFilter);
		fileChooser.setInitialFileName(StringUtils.isNotBlank(chart.getTitle()) ? chart.getTitle(): "data");
		fileChooser.setInitialDirectory(initialDirectory);
		fileChooser.setTitle(getString("chooser.title"));

		File file = fileChooser.showSaveDialog(chart.getScene().getWindow());

		if ( file != null ) {

			initialDirectory = file.getParentFile();

			if ( chart instanceof XYChart ) {
				saveChartData(chart, file);
			} else {
				saveDensityChartData(chart, file);
			}

		}

	}

	private void saveChartData( Chart chart, File file ) {

		( (XYChart<?, ?>) chart ).getData().forEach(series -> {

			String fileName = file.getPath().substring(0, file.getPath().lastIndexOf('.'))
							+ "_" + series.getName() + ".csv";

			try ( BufferedWriter writer = new BufferedWriter(new FileWriter(fileName)) ) {
				for ( Data<?, ?> data : series.getData() ) {
					writer.write(data.getXValue().toString() + ", " + data.getYValue().toString() + "\n");
				}
			} catch ( IOException ex ) {
				LOGGER.log(
					SEVERE,
					MessageFormat.format("Unable to save chart data [{0}].", fileName),
					ex
				);
			}

		});

	}

	private void saveDensityChartData( Chart chart, File file ) {
		
		DensityChartFX<?, ?> dChart = (DensityChartFX<?, ?>) chart;

		try ( BufferedWriter writer = new BufferedWriter(new FileWriter(file)) ) {
			for ( int yIndex = 0; yIndex < dChart.getData().getYSize(); yIndex++ ) {
				for ( int xIndex = 0; xIndex < dChart.getData().getXSize(); xIndex++ ) {
					writer.write(
						dChart.getData().getXValue(xIndex) + ", "
					  + dChart.getData().getYValue(yIndex) + ", "
					  + dChart.getData().getZValue(xIndex, yIndex) + "\n"
					);
				}
			}
		} catch ( IOException ex ) {
			LOGGER.log(
				SEVERE,
				MessageFormat.format("Unable to save chart data [{0}].", file),
				ex
			);
		}

	}

}
