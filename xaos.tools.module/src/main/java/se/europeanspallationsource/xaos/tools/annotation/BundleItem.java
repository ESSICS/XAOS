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


import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * This annotation define a single entry in the resource bundle file.
 * <p>
 * By default a resource bundle named "Bundle.properties" will be created inside
 * the package of the class or interface annotated by this annotation. If the
 * file already exists, the entry line for this annotation will be appended.</p>
 * <p>
 * The {@link Bundle} annotation can be used to provide a different name to the
 * resource bundle file.</p>
 *
 * @author claudio.rosati@esss.se
 */
@Documented
@Repeatable( BundleItems.class )
@Retention( RUNTIME )
@Target( { FIELD, METHOD, TYPE } )
public @interface BundleItem {

	/**
	 * An optional comment to be inserted before the item in the resource bundle.
	 *
	 * @return A comment to the resource item.
	 */
	String comment() default "";

	/**
	 * The item key in the resource bundle. It is mandatory and must be unique
	 * in the resource bundle.
	 *
	 * @return The item key.
	 */
	String key();

	/**
	 * The item message in the resource bundle. If the resource bundle will be
	 * localized, the returned value will be considered being the "default"
	 * message.
	 *
	 * @return The item message.
	 */
	String message();

}
