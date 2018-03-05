/*
 * Copyright 2018 claudiorosati.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package se.europeanspallationsource.framework.annotation;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import org.apache.commons.collections4.IteratorUtils;


/**
 * Provides few more methods to {@link ServiceLoader}.
 *
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class ServiceLoaderUtilities {

	/**
	 * Returns the {@link List} of classes implementing the given
	 * service provider interface ({@code spi}).
	 *
	 * @param <S> The service provider interface type.
	 * @param spi The meta-class representing the service provider interface.
	 * @return The {@link List} of found service provider classes.
	 */
	public static <S> List<Class<S>> classesOf( Class<S> spi ) {
		return classesOf(ServiceLoader.load(spi).iterator());
	}

	/**
	 * Returns the {@link List} of service providers implementing the given
	 * service provider interface ({@code spi}).
	 *
	 * @param <S> The service provider interface type.
	 * @param spi The meta-class representing the service provider interface.
	 * @return The {@link List} of found service providers.
	 */
	public static <S> List<S> load( Class<S> spi ) {
		return IteratorUtils.toList(ServiceLoader.load(spi).iterator());
	}

	@SuppressWarnings( "unchecked" )
	private static <S> List<Class<S>> classesOf( Iterator<S> iterator ) {

		List<Class<S>> cs = new ArrayList<>(3);

		while ( iterator.hasNext() ) {
			cs.add((Class<S>) iterator.next().getClass());
		}

		return cs;

	}

	private ServiceLoaderUtilities() {
	}

}
