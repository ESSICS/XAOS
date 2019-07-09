/*
 * Copyright 2018 European Spallation Source ERIC.
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
package se.europeanspallationsource.xaos.tests.tools;


import java.util.Arrays;
import java.util.Collections;
import java.util.ServiceLoader;
import org.junit.BeforeClass;
import org.junit.Test;
import se.europeanspallationsource.xaos.tests.tools.services.BasicUsageImplementation;
import se.europeanspallationsource.xaos.tests.tools.services.BasicUsageInterface;
import se.europeanspallationsource.xaos.tests.tools.services.MultipleRegistrationsImpl;
import se.europeanspallationsource.xaos.tests.tools.services.MultipleRegistrationsInterface1;
import se.europeanspallationsource.xaos.tests.tools.services.MultipleRegistrationsInterface2;
import se.europeanspallationsource.xaos.tests.tools.services.OrderedImpl1;
import se.europeanspallationsource.xaos.tests.tools.services.OrderedImpl2;
import se.europeanspallationsource.xaos.tests.tools.services.OrderedImpl3;
import se.europeanspallationsource.xaos.tests.tools.services.OrderedImpl4;
import se.europeanspallationsource.xaos.tests.tools.services.OrderedInterface;
import se.europeanspallationsource.xaos.tools.annotation.ServiceLoaderUtilities;

import static org.junit.Assert.assertEquals;


/**
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( { "ClassWithoutLogger", "UseOfSystemOutOrSystemErr" } )
public class ServiceProviderProcessorTest {

	@BeforeClass
	public static void setUpClass() {
		System.out.println("---- ServiceProviderProcessorTest ------------------------------");
	}

	@Test
	public void testBasicUsage() {

		System.out.println("  Basic Usage...");

		assertEquals(
			Collections.singletonList(BasicUsageImplementation.class),
			ServiceLoaderUtilities.classesOf(ServiceLoader.load(BasicUsageInterface.class))
		);

	}

	@Test
    public void testMultipleRegistrations() throws Exception {

		System.out.println("  Multiple Registrations Usage...");

        assertEquals(
			Collections.singletonList(MultipleRegistrationsImpl.class),
			ServiceLoaderUtilities.classesOf(ServiceLoader.load(MultipleRegistrationsInterface1.class))
		);
        assertEquals(
			Collections.singletonList(MultipleRegistrationsImpl.class),
			ServiceLoaderUtilities.classesOf(ServiceLoader.load(MultipleRegistrationsInterface2.class))
		);

	}

	@Test
	@SuppressWarnings("NestedAssignment")
	public void testOrder() throws Exception {

		System.out.println("  Order Usage...");

		assertEquals(
			Arrays.<Class<?>>asList(OrderedImpl3.class, OrderedImpl2.class, OrderedImpl1.class, OrderedImpl4.class),
			ServiceLoaderUtilities.classesOf(ServiceLoader.load(OrderedInterface.class))
		);
        assertEquals(
			OrderedImpl3.class,
			ServiceLoaderUtilities.findFirst(ServiceLoader.load(OrderedInterface.class)).orElseThrow().getClass()
		);

	}

}
