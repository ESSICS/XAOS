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

/**
 *
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class MissinBundleException extends RuntimeException {

	private static final long serialVersionUID = 5964658066668127793L;

	/**
	 * Creates a new instance of {@code MissinBundleException} without detail
	 * message.
	 */
	public MissinBundleException() {
		super();
	}

	/**
	 * Constructs an instance of {@code MissinBundleException} with the
	 * specified detail {@code message}.
	 *
	 * @param message The detail message.
	 */
	public MissinBundleException( String message ) {
		super(message);
	}

	/**
	 * Creates a new instance of {@code MissinBundleException} with the specific
	 * {@code cause}, but without detail message.
	 *
	 * @param cause The original exception that caused this one.
	 */
	public MissinBundleException( Throwable cause ) {
		super(cause);
	}

	/**
	 * Constructs an instance of {@code MissinBundleException} with the
	 * specified detail {@code message} and {@code cause}.
	 *
	 * @param message The detail message.
	 * @param cause   The original exception that caused this one.
	 */
	public MissinBundleException( String message, Throwable cause ) {
		super(cause);
	}

}
