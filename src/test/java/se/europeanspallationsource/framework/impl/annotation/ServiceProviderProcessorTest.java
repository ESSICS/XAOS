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
package se.europeanspallationsource.framework.impl.annotation;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import se.europeanspallationsource.framework.annotation.ServiceProvider;


/**
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class ServiceProviderProcessorTest {

	@SuppressWarnings( "unchecked" )
	private static <S> List<Class<S>> classesOf( Iterator<S> iterator ) {

		List<Class<S>> cs = new ArrayList<>(3);

		while ( iterator.hasNext() ) {
			cs.add((Class<S>) iterator.next().getClass());
		}

		return cs;

	}

	private static <S> List<Class<S>> classesOfLookup( Class<S> spi ) {
		return classesOf(ServiceLoader.load(spi).iterator());
	}

	private static List<Class<?>> sortClassList( List<Class<?>> classes ) {

		List<Class<?>> sorted = new ArrayList<>(classes);

		Collections.sort(sorted, ( c1, c2 ) -> c1.getName().compareTo(c2.getName()));

		return sorted;

	}

//	@Test
//	@SuppressWarnings( "UseOfSystemOutOrSystemErr" )
//	public void testBasicUsage() {
//
//		System.out.println("Basic Usage");
//
//        assertEquals(
//			Collections.singletonList(Implementation.class),
//			classesOfLookup(Interface.class)
//		);
//
//	}

	@ServiceProvider( service = Interface.class )
	@SuppressWarnings( { "PublicInnerClass", "ClassMayBeInterface" } )
	public static class Implementation implements Interface {
	}

	@SuppressWarnings( { "MarkerInterface", "PublicInnerClass" } )
	public interface Interface {
	}

}
