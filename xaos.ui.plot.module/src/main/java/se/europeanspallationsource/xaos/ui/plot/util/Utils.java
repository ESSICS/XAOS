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
package se.europeanspallationsource.xaos.ui.plot.util;


/**
 * To be removed.
 *
 * @author Grzegorz Kruk
 */

import java.io.InputStream;

import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;


public class Utils {

	public static void copyToClipboardImage( Label lbl ) {

		WritableImage snapshot = lbl.snapshot(new SnapshotParameters(), null);
		final Clipboard clipboard = Clipboard.getSystemClipboard();
		final ClipboardContent content = new ClipboardContent();

		content.putImage(snapshot);
		clipboard.setContent(content);

	}

	public static void copyToClipboardImageFromFile( String path ) {

		final Clipboard clipboard = Clipboard.getSystemClipboard();
		final ClipboardContent content = new ClipboardContent();

		content.putImage(Utils.getImage(path));
		clipboard.setContent(content);

	}

	public static Image getImage( String path ) {

		InputStream is = Utils.class.getResourceAsStream(path);
		return new Image(is);
	}

//	public static ImageView setIcon( String path ) {
//
//		InputStream is = Utils.class.getResourceAsStream(path);
//		ImageView iv = new ImageView(new Image(is));
//
//		iv.setFitWidth(100);
//		iv.setFitHeight(100);
//		return iv;
//	}

}
