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


import java.lang.annotation.Annotation;
import java.util.Map;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;


/**
 * An {@link AbstractProcessor} with some common utilities.
 *
 * @author claudiorosati
 */
public abstract class AbstractAnnotationProcessor extends AbstractProcessor {

	/**
	 * @param element The {@link TypeElement} whose binary name must be returned.
	 * @return The binary name of the given {@code element}.
	 */
	protected String binaryName( TypeElement element ) {
		return processingEnv.getElementUtils().getBinaryName(element).toString();
	}

	/**
	 * Returns the mirror for the given {@code annotation}.
	 *
	 * @param element    A source element.
	 * @param annotation A type of annotation.
	 * @return The instance of that annotation on the element, or {@code null}
	 *         if not found.
	 */
	protected AnnotationMirror findAnnotationMirror( Element element, Class<? extends Annotation> annotation ) {

		for ( AnnotationMirror ann : element.getAnnotationMirrors() ) {
			if ( processingEnv.getElementUtils().getBinaryName((TypeElement) ann.getAnnotationType().asElement()).contentEquals(annotation.getName()) ) {
				return ann;
			}
		}

		return null;

	}

	/**
	 * Returns the value for the given {@code annotation}.
	 *
	 * @param annotation An annotation instance ({@code null} permitted).
	 * @param name       The name of an attribute of that annotation.
	 * @return The corresponding annotation value if found.
	 */
	protected AnnotationValue findAnnotationValue( AnnotationMirror annotation, String name ) {

		if ( annotation != null ) {
			for ( Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotation.getElementValues().entrySet() ) {
				if ( entry.getKey().getSimpleName().contentEquals(name) ) {
					return entry.getValue();
				}
			}
		}

		return null;

	}

	/**
	 * A clean way of accessing {@link #processingEnv}.
	 *
	 * @return {@link #processingEnv}.
	 */
	protected ProcessingEnvironment getEnvironment() {
		return processingEnv;
	}

	/**
	 * @return The {@link Messager} used to report errors, warnings, and other
	 *         notices.
	 */
	protected Messager getMessager() {
		return processingEnv.getMessager();
	}

}
