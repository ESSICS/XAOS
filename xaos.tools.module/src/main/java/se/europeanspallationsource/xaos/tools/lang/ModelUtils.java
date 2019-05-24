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
package se.europeanspallationsource.xaos.tools.lang;


import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;


/**
 * Utility to navigate Java language model (useful in annotation processing).
 *
 * @author claudio.rosati@esss.se
 */
public class ModelUtils {

	/**
	 * Return the {@link TypeElement} representing the super-class or
	 * super-interface of the element represented by the given parameter.
	 *
	 * @param clazz The {@link TypeElement} of the element whose super-class
	 *              must be returned.
	 * @return The existing super-class {@link TypeElement} or {@code null}.
	 */
	public static TypeElement getSuper( TypeElement clazz ) {

		TypeMirror superMirror = clazz.getSuperclass();

		if ( DeclaredType.class.isAssignableFrom(superMirror.getClass()) ) {
			return (TypeElement) ( (DeclaredType) superMirror ).asElement();
		} else {
			return null;
		}

	}

	/**
	 * Returns the first enclosing parent of the given {@code type}.
	 *
	 * @param <T>     The type of the parent to be found.
	 * @param element The element whose parent must be found.
	 * @param type    The type of the parent to be found.
	 * @return The found parent instance of the given {@code type} or
	 *         {@code null} if no parent can be found.
	 */
	@SuppressWarnings( { "AssignmentToMethodParameter", "unchecked" } )
	public static <T> T parent( Element element, Class<T> type ) {

		do {
			element = element.getEnclosingElement();
		} while ( element != null && !type.isInstance(element) );

		return (T) element;

	}

}
