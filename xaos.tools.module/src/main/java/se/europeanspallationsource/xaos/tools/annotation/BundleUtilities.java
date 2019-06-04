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
package se.europeanspallationsource.xaos.tools.annotation;


import java.text.MessageFormat;

import static se.europeanspallationsource.xaos.tools.annotation.impl.BundleProcessor.DEFAULT_BUNDLE_NAME;


/**
 * Utility method to be used with {@link Bundle}, {@link BundleItem}, and
 * {@link BundleItems} annotations.
 *
 * @author claudio.rosati@esss.se
 */
public class BundleUtilities {

	/**
	 * Used to retrieve a message from a resource bundle populated through the
	 * {@link Bundle}, {@link BundleItem}, and {@link BundleItems} annotations.
	 *
	 * @param clazz      The class containing the {@link Bundle},
	 *                   {@link BundleItem}, and {@link BundleItems} annotations.
	 *                   The {@link Bundle} annotation is searched for the name
	 *                   of the resource bundle to be used.
	 * @param key        The key string in the resource bundle to retrieve the
	 *                   message.
	 * @param parameters If the message string contains {@code {0}, {1}, ... {n}}
	 *                   parameters, they will be orderly substituted with the
	 *                   ones provided here.
	 *                   {@link MessageFormat#format(java.lang.String, java.lang.Object...)}
	 *                   will be used to format the message.
	 * @return The found message string formatted with the given {@code parameters}.
	 */
	public static String getMessage( Class<?> clazz, String key, Object... parameters ) {

		String packageName = clazz.getPackageName();
		String bundleName = DEFAULT_BUNDLE_NAME;
		Bundle annotation = clazz.getAnnotation(Bundle.class);

		if ( annotation != null ) {
			bundleName = annotation.name();
		}

//	TODO:CR use cache2k

	}

	private BundleUtilities() {
		//	Nothing to do.
	}

}
