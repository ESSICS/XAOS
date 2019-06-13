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


import javafx.scene.chart.Chart;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Tooltip;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import se.europeanspallationsource.xaos.tools.annotation.BundleItem;
import se.europeanspallationsource.xaos.tools.annotation.Bundles;
import se.europeanspallationsource.xaos.tools.annotation.ServiceProvider;
import se.europeanspallationsource.xaos.ui.control.Icons;
import se.europeanspallationsource.xaos.ui.plot.PluggableChartContainer;
import se.europeanspallationsource.xaos.ui.plot.spi.ToolbarContributor;

import static se.europeanspallationsource.xaos.ui.control.CommonIcons.FILE_IMAGE;


/**
 * A {@link ToolbarContributor} that allows to save a snapshot of the current
 * chart into an image file.
 *
 * @author claudio.rosati@esss.se
 */
@BundleItem( key = "button.tooltip", message = "Save a snapshot of the current chart into an image file.")
@ServiceProvider( service = ToolbarContributor.class, order = 100 )
public class SaveChartAsImageContributor implements ToolbarContributor {

	@Override
	public Control provide( PluggableChartContainer chartContainer ) {

		Button button = new Button(null, Icons.iconFor(FILE_IMAGE, 14));

		button.setTooltip(new Tooltip(Bundles.get(SaveChartAsImageContributor.class, "button.tooltip")));
		button.setOnAction(e -> saveChartAsImage(chartContainer.getPluggable().getChart()));

		return button;

	}

	private void saveChartAsImage( Chart chart ) {

		String[] extensions = ImageIO.getWriterFileSuffixes();
		String[] starExtensions = new String[extensions.length];

		for ( int i = 0; i < extensions.length; i++ ) {
			starExtensions[i] = "*." + extensions[i];
		}

		StringBuilder builder = new StringBuilder("Image Files (");

		for ( String e : starExtensions ) {
			builder.append("*.").append(e).append(", ");
		}

		builder.deleteCharAt(builder.length() - 1);
		builder.deleteCharAt(builder.length() - 1);
		builder.append(")");

        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter(builder.toString(), starExtensions);

        fileChooser.getExtensionFilters().add(extensionFilter);









//        //Set extension filter
//
//        //Show save file dialog
//        File file = fileChooser.showSaveDialog(null);
//
//        if(file != null){
//            WritableImage image = getChart().snapshot(new SnapshotParameters(), null);
//            try {
//                ImageIO.write(SwingFXUtils.fromFXImage(image, null),"png",file);
//            } catch (IOException ex) {
//                Logger.getLogger(PropertyMenu.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }







	}

}
