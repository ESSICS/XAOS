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
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * This annotation provides the way of changing the resource bundle name from
 * the default "Bundle.properties" to something else.
 * <p>
 * <b>Note:</b> This annotation cannot be applied to inner classes/interface.</p>
 *
 * @author claudio.rosati@esss.se
 */
@Documented
@Retention( RUNTIME )
@Target( TYPE )
public @interface Bundle {

	/**
	 * The name (without extension and path) of the resource bundle for the
	 * class or interface annotated by this annotation. The resource bundle
	 * generated will be in the same packaged of the annotated class/interface
	 * with the standard {@code .properties} extension.
	 *
	 * @return The resource bundle name.
	 */
	String name();

}
