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
package se.europeanspallationsource.xaos.ui.util;


import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;


/**
 * Various utilities for clipboards.
 *
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class ClipboardUtils {

	/**
	 * Clear the system clipboard.
	 */
	public static void clearSystemClipboard() {
		Clipboard.getSystemClipboard().clear();
	}

	/**
	 * Copy the given string into the system clipboard. The clipboard is cleared
	 * before adding the new content.
	 *
	 * @param text The string to be copied into the system clipboard.
	 */
	public static void copyToSystemClipboard( String text ) {

		final ClipboardContent content = new ClipboardContent();

		content.putString(text);
		Clipboard.getSystemClipboard().setContent(content);

	}

	private ClipboardUtils() {
		//	Nothing to do.
	}

}
