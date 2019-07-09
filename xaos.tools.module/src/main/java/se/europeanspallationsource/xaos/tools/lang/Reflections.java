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


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;


/**
 * Utility methods using the Java Reflection API.
 *
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class Reflections {

	/**
	 * Searches for a field in the given class and all of its super classes. If the
	 * field cannot be found {@link NoSuchFieldException} is thrown.
	 *
	 * @param clazz Class to start the search for the field.
	 * @param name  Name of the field.
	 * @return The found field.
	 * @throws NoSuchFieldException If the field cannot be found in the class
	 *                              hierarchy starting from the given class.
	 */
	public static Field getField( Class<?> clazz, String name ) throws NoSuchFieldException {

		Class<?> searchClass = clazz;
		Field field = null;

		while ( field == null && searchClass != null ) {
			try {
				field = searchClass.getDeclaredField(name);
			} catch ( NoSuchFieldException ex ) {
				searchClass = searchClass.getSuperclass();
			}
		}

		if ( field == null ) {
			throw new NoSuchFieldException(MessageFormat.format("{0}.{1}", clazz.getSimpleName(), name));
		}

		return field;

	}

	/**
	 * Read a field value. If the field is not accessible, it will be set to be
	 * accessible.
	 *
	 * @param field  The {@link Field} description.
	 * @param object Instance in which the field value should be read.
	 * @return The value of the field.
	 * @throws RuntimeException If the field doesn't exist or cannot be read.
	 */
	public static Object getFieldValue( Field field, Object object ) throws RuntimeException {
		try {

			if ( !field.canAccess(object)) {
				field.setAccessible(true);
			}

			return field.get(object);

		} catch ( SecurityException | IllegalArgumentException | IllegalAccessException ex ) {
			throw new RuntimeException(MessageFormat.format(
				"Could not read field value [class: {0}, field: {1}].",
				field.getDeclaringClass().getSimpleName(),
				field.getName()
			), ex);
		}
	}

	/**
	 * Read a field value. If the field is not accessible, it will be set to be
	 * accessible.
	 *
	 * @param object Instance in which the field value should be read.
	 * @param name   Name of the field who's value should be read.
	 * @return The value of the field.
	 * @throws RuntimeException If the field doesn't exist or cannot be read.
	 */
	public static Object getFieldValue( Object object, String name ) throws RuntimeException {
		try {
			return getFieldValue(getField(object.getClass(), name), object);
		} catch ( NoSuchFieldException ex ) {
			throw new RuntimeException(MessageFormat.format(
				"Could not read field value [class: {0}, field: {1}].",
				object.getClass().getSimpleName(),
				name
			), ex);
		}
	}

	/**
	 * Searches for a method in the given class and all of its super classes.If the
	 * method cannot be found {@link NoSuchMethodException} is thrown.
	 *
	 * @param clazz          Class to start the search for the method.
	 * @param name           Name of the method.
	 * @param parameterTypes The parameters type array.
	 * @return The found method.
	 * @throws NoSuchMethodException If the method cannot be found in the class
	 *                               hierarchy starting from the given class.
	 */
	public static Method getMethod( Class<?> clazz, String name, Class<?>... parameterTypes ) throws NoSuchMethodException {

		Class<?> searchClass = clazz;
		Method method = null;

		while ( method == null && searchClass != null ) {
			try {
				method = searchClass.getDeclaredMethod(name, parameterTypes);
			} catch ( NoSuchMethodException ex ) {
				searchClass = searchClass.getSuperclass();
			}
		}

		if ( method == null ) {
			throw new NoSuchMethodException(MessageFormat.format(
				"{0}.{1}({2} parameters)",
				clazz.getSimpleName(),
				name,
				( parameterTypes == null ) ? 0 : parameterTypes.length
			));
		}

		return method;

	}

	/**
	 * Invokes a method on a given object. If the method is not accessible, it will
	 * be set to be accessible.
	 *
	 * @param method     The {@link Method} descriptor.
	 * @param object     Instance whose method must be invoked onto.
	 * @param parameters The parameters array.
	 * @return The returned value from the method invocation.
	 * @throws RuntimeException If the method doesn't exist or cannot be invoked.
	 */
	public static Object invokeMethod( Method method, Object object, Object... parameters ) throws RuntimeException {
		try {

			if ( !method.canAccess(object)) {
				method.setAccessible(true);
			}

			return method.invoke(object, parameters);

		} catch ( IllegalAccessException | IllegalArgumentException | SecurityException | InvocationTargetException ex ) {
			throw new RuntimeException(MessageFormat.format(
				"Could not invoke method [class: {0}, method: {1}, parameters: {2}].",
				method.getDeclaringClass().getSimpleName(),
				method.getName(),
				( parameters == null ) ? 0 : parameters.length
			), ex);
		}
	}

	/**
	 * Invokes a no-parameter method on a given object. If the method is not
	 * accessible, it will be set to be accessible.
	 *
	 * @param object Instance whose method must be invoked onto.
	 * @param name   The name of the method to be invoked.
	 * @return The returned value from the method invocation.
	 * @throws RuntimeException If the method doesn't exist or cannot be invoked.
	 */
	public static Object invokeMethod( Object object, String name ) throws RuntimeException {
		try {
			return invokeMethod(getMethod(object.getClass(), name), object);
		} catch ( NoSuchMethodException ex ) {
			throw new RuntimeException(MessageFormat.format(
				"Could not invoke method [class: {0}, method: {1}, parameters: 0].",
				object.getClass().getSimpleName(),
				name
			), ex);
		}
	}

	/**
	 * Set a field to a given value. If the field is not accessible, it will be set
	 * to be accessible.
	 *
	 * @param object Instance in which the field must be set to the given value.
	 * @param field  The {@link Field} descriptor.
	 * @param value  The value to be set.
	 * @throws RuntimeException If the field doesn't exist or cannot be set.
	 */
	public static void setFieldValue( Field field, Object object, Object value ) throws RuntimeException {
		try {

			if ( !field.canAccess(object)) {
				field.setAccessible(true);
			}

			field.set(object, value);

		} catch ( SecurityException | IllegalArgumentException | IllegalAccessException ex ) {
			throw new RuntimeException(MessageFormat.format(
				"Could not set field value [class: {0}, field: {1}, value: {2}].",
				field.getDeclaringClass().getSimpleName(),
				field.getName(),
				value
			), ex);
		}
	}

	/**
	 * Set a field to a given value. If the field is not accessible, it will be set
	 * to be accessible.
	 *
	 * @param object Instance in which the field must be set to the given value.
	 * @param name   Name of the field who's value should be set.
	 * @param value  The value to be set.
	 * @throws RuntimeException If the field doesn't exist or cannot be set.
	 */
	public static void setFieldValue( Object object, String name, Object value ) throws RuntimeException {
		try {
			setFieldValue(getField(object.getClass(), name), object, value);
		} catch ( NoSuchFieldException ex ) {
			throw new RuntimeException(MessageFormat.format(
				"Could not set field value [class: {0}, field: {1}, value: {2}].",
				object.getClass().getSimpleName(),
				name,
				value
			), ex);
		}
	}

	private Reflections() {
	}

}
