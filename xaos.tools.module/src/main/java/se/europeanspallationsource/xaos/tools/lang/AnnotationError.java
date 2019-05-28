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


import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;


/**
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class AnnotationError extends Error {

	private static final long serialVersionUID = 1L;

	private Element element;
	private AnnotationMirror mirror;
	private AnnotationValue value;

	public AnnotationError( String message ) {
		super(message);
	}

	public AnnotationError( Element element, String message ) {
		this(message);
		this.element = element;
	}

	public <T> AnnotationError( Element element, Class<T> annotation, String message ) {
		this(element, ModelUtils.annotationMirror(element, annotation), message);
	}

	public <T> AnnotationError( ProcessingEnvironment environment, Element element, Class<T> annotation, String method, String message ) {
		this(
			element,
			ModelUtils.annotationMirror(element, annotation),
			ModelUtils.rawAnnotationValue(
				environment,
				element,
				ModelUtils.annotationMirror(element, annotation),
				method
			),
			message
		);
	}

	public AnnotationError( Element element, AnnotationMirror mirror, String message ) {
		this(element, message);
		this.mirror = mirror;
	}

	public AnnotationError( Element element, AnnotationMirror mirror, AnnotationValue value, String message ) {
		this(element, mirror, message);
		this.value = value;
	}

	public void report( ProcessingEnvironment environment ) {
		printMessage(environment, Diagnostic.Kind.ERROR);
	}

	public void warn( ProcessingEnvironment environment ) {
		printMessage(environment, Diagnostic.Kind.WARNING);
	}

	private void printMessage( ProcessingEnvironment environment, Diagnostic.Kind kind ) {
		if ( element == null ) {
			environment.getMessager().printMessage(kind, getMessage());
		} else if ( mirror == null ) {
			environment.getMessager().printMessage(kind, getMessage(), element);
		} else if ( value == null ) {
			environment.getMessager().printMessage(kind, getMessage(), element, mirror);
		} else {
			environment.getMessager().printMessage(kind, getMessage(), element, mirror, value);
		}
	}

}
