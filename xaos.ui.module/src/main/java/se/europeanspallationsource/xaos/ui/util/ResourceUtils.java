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


import java.text.MessageFormat;
import java.util.logging.Logger;
import javafx.scene.image.Image;

import static java.util.logging.Level.SEVERE;


/**
 * Utilities to load resources from the module/class path.
 *
 * @author claudio.rosati@esss.se
 */
public class ResourceUtils {

	private static final Logger LOGGER = Logger.getLogger(ResourceUtils.class.getName());

	/**
	 * @param resource The string representing the URL to use in fetching the
	 *                 pixel data. In case of a resource file a simple
	 *                 "/path/to/resource.ext" string is sufficient. The modules
	 *                 whose code uses this method must <b>open</b> the
	 *                 corresponding package to the {@code xaos.ui} module.
	 * @return The {@link Image} loaded from the given {@code resource} path or
	 *         {@code null} if the resource doesn't exist or cannot be loaded,
	 *         in which case a severe message is logged.
	 */
	public static Image getImage( String resource ) {

		try {
			return new Image(resource);
		} catch ( NullPointerException npex) {
            LOGGER.log(SEVERE, "Null 'resource' parameter.");
		} catch ( IllegalArgumentException iaex) {
            LOGGER.log(SEVERE, MessageFormat.format("Unable to load \"{0}\" image file.", resource), iaex);
		}

		return null;

	}

	private ResourceUtils() {
		//	Nothing to do.
	}

}
