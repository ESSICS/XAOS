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
package se.europeanspallationsource.xaos.impl.annotation;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import se.europeanspallationsource.xaos.annotation.ServiceLoaderUtilities;
import se.europeanspallationsource.xaos.annotation.ServiceProvider;
import se.europeanspallationsource.xaos.annotation.ServiceProviders;

import static org.junit.Assert.assertEquals;


/**
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class ServiceProviderProcessorTest {

	@Test
	@SuppressWarnings( "UseOfSystemOutOrSystemErr" )
	public void testBasicUsage() {

		System.out.println("Basic Usage");

		assertEquals(
			Collections.singletonList(BasicUsageImplementation.class),
			ServiceLoaderUtilities.classesOf(BasicUsageInterface.class)
		);

	}

	@Test
	@SuppressWarnings( "UseOfSystemOutOrSystemErr" )
    public void testMultipleRegistrations() throws Exception {

		System.out.println("Multiple Registrations Usage");

        assertEquals(
			Collections.singletonList(MultipleRegistrationsImpl.class),
			ServiceLoaderUtilities.classesOf(MultipleRegistrationsInterface1.class)
		);
        assertEquals(
			Collections.singletonList(MultipleRegistrationsImpl.class),
			ServiceLoaderUtilities.classesOf(MultipleRegistrationsInterface2.class)
		);

	}

	@Test
	@SuppressWarnings( { "UseOfSystemOutOrSystemErr", "NestedAssignment" } )
	public void testOrder() throws Exception {

		System.out.println("Order Usage");

		assertEquals(
			Arrays.<Class<?>>asList(OrderedImpl3.class, OrderedImpl2.class, OrderedImpl1.class),
			ServiceLoaderUtilities.classesOf(OrderedInterface.class)
		);

		// Order in file should also be fixed, for benefit of ServiceLoader.
		BufferedReader r = new BufferedReader(
			new InputStreamReader(
				ServiceProviderProcessorTest.class.getResourceAsStream("/META-INF/services/" + OrderedInterface.class.getName())
			)
		);
		List<String> lines = new ArrayList<>(3);
		String line;

		while ( ( line = r.readLine() ) != null ) {
			lines.add(line);
		}

		assertEquals(Arrays.asList(
			OrderedImpl3.class.getName(),
			ServiceLoaderLine.ORDER + "100",
			OrderedImpl2.class.getName(),
			ServiceLoaderLine.ORDER + "200",
			OrderedImpl1.class.getName()
		),
			lines
		);

	}

	@ServiceProvider( service = BasicUsageInterface.class )
	@SuppressWarnings( { "PublicInnerClass", "ClassMayBeInterface" } )
	public static class BasicUsageImplementation implements BasicUsageInterface {
	}

	@ServiceProviders( {
		@ServiceProvider( service = MultipleRegistrationsInterface1.class, order = 200 ),
		@ServiceProvider( service = MultipleRegistrationsInterface2.class )
	} )
	@SuppressWarnings( { "PublicInnerClass", "ClassMayBeInterface" } )
	public static class MultipleRegistrationsImpl implements MultipleRegistrationsInterface1, MultipleRegistrationsInterface2 {
	}

	@ServiceProvider( service = OrderedInterface.class )
	@SuppressWarnings( { "PublicInnerClass", "ClassMayBeInterface" } )
	public static class OrderedImpl1 implements OrderedInterface {
	}

	@ServiceProvider( service = OrderedInterface.class, order = 200 )
	@SuppressWarnings( { "PublicInnerClass", "ClassMayBeInterface" } )
	public static class OrderedImpl2 implements OrderedInterface {
	}

	@ServiceProvider( service = OrderedInterface.class, order = 100 )
	@SuppressWarnings( { "PublicInnerClass", "ClassMayBeInterface" } )
	public static class OrderedImpl3 implements OrderedInterface {
	}

	@SuppressWarnings( { "MarkerInterface", "PublicInnerClass" } )
	public interface BasicUsageInterface {
	}

	@SuppressWarnings( { "MarkerInterface", "PublicInnerClass" } )
	public interface MultipleRegistrationsInterface1 {
	}

	@SuppressWarnings( { "MarkerInterface", "PublicInnerClass" } )
	public interface MultipleRegistrationsInterface2 {
	}

	@SuppressWarnings( { "MarkerInterface", "PublicInnerClass" } )
	public interface OrderedInterface {
	}

}
