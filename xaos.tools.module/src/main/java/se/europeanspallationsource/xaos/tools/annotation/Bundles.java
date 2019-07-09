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


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import static se.europeanspallationsource.xaos.tools.annotation.impl.BundleProcessor.DEFAULT_BUNDLE_NAME;


/**
 * Utility method to be used with {@link Bundle}, {@link BundleItem}, and
 * {@link BundleItems} annotations.
 * <p>
 * Here and example of ho to use the annotations and this utility class:</p>
 * <pre>
 *   &#64;Bundle( name = "Messages" )
 *   public class SomeClass {
 *
 *     &#64;BundleItem(
 *       key = "exception.message",
 *       comment = "Message used in the thrown illegal state exception.\n"
 *               + "  {0} Message from the original exception.",
 *       message = "Operation not permitted [original message: {0}]."
 *     )
 *     public void doSomething() {
 *       try {
 *         ...
 *       } catch ( Exception ex ) {
 *         throw new IllegalStateException(
 *           Bundles.get(getClass(), "exception.message", ex.getMessage()),
 *           ex
 *         );
 *       }
 *     }
 *
 *   }</pre>
 * <p>
 * <b>Note:</b> when using {@link Bundles} the passed {@link Class}'s
 * package must must be opened to the {@code xaos.tools} module, i.e. it must be
 * included into an <b>opens</b> statement in the {@code module-info} class.</p>
 *
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class Bundles {

	private static final Cache<String, ResourceBundle> CACHE = Caffeine.newBuilder()
		.expireAfterAccess(30, TimeUnit.MINUTES)
		.maximumSize(32)
		.softValues()
		.build();

	/**
	 * Used to retrieve a message from a resource bundle populated through the
	 * {@link Bundle}, {@link BundleItem}, and {@link BundleItems} annotations.
	 *
	 * @param clazz      The class containing the {@link Bundle},
	 *                   {@link BundleItem}, and {@link BundleItems} annotations.
	 *                   The {@link Bundle} annotation is searched for the name
	 *                   of the resource bundle to be used.
	 * @param key        The key string in the resource bundle to retrieve the
	 *                   message. It mus be specified in the same was as done in
	 *                   the corresponding {@link BundleItem#key()} annotation.
	 * @param parameters If the message string contains {@code {0}, {1}, ... {n}}
	 *                   parameters, they will be orderly substituted with the
	 *                   ones provided here.
	 *                   {@link MessageFormat#format(java.lang.String, java.lang.Object...)}
	 *                   will be used to format the message.
	 * @return The found message string formatted with the given
	 *         {@code parameters}.
	 * @throws MissinBundleException    If a proper resource bundle cannot be
	 *                                  loaded.
	 * @throws MissingResourceException If a message cannot be found for the
	 *                                  given {@code key}.
	 */
	public static String get( Class<?> clazz, String key, Object... parameters )
		throws MissinBundleException, MissingResourceException
	{

		String packageName = clazz.getPackageName();
		String bundleName = DEFAULT_BUNDLE_NAME;
		Bundle annotation = clazz.getAnnotation(Bundle.class);

		if ( annotation != null ) {
			bundleName = annotation.name();
		}

		String bundleQualifiedName = packageName + "." + bundleName;
		ResourceBundle bundle;

		try {
			bundle = CACHE.get(
				bundleQualifiedName,
				resource -> ResourceBundle.getBundle(resource, clazz.getModule())
			);
		} catch ( Exception ex ) {
			throw new MissinBundleException(
				MessageFormat.format("Bundle not found or not loadable [{0}].", bundleQualifiedName),
				ex
			);
		}

		return MessageFormat.format(bundle.getString(clazz.getSimpleName() + "." + key), parameters);

	}

	private Bundles() {
		//	Nothing to do.
	}

}
