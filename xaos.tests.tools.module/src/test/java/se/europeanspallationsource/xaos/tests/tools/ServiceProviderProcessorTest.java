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


import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.stream.Stream;
import org.junit.BeforeClass;
import org.junit.Test;
import se.europeanspallationsource.xaos.tools.annotation.ServiceLoaderUtilities;



/**
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( { "ClassWithoutLogger", "UseOfSystemOutOrSystemErr" } )
public class ServiceProviderProcessorTest {

	@Test
	public void test() {
	}

	@BeforeClass
	public static void setUpClass() {
		System.out.println("---- ServiceProviderProcessorTest ------------------------------");
	}

	@Test
	public void testBasicUsage() {

		System.out.println("  Basic Usage");

		Stream<Provider<BasicUsageInterface>> stream = ServiceLoaderUtilities.stream(ServiceLoader.load(BasicUsageInterface.class));
		
//		assertEquals(
//			Collections.singletonList(BasicUsageImplementation.class),
//			ServiceLoaderUtilities.classesOf(BasicUsageInterface.class)
//		);

	}

//	@Test
//    public void testMultipleRegistrations() throws Exception {
//
//		System.out.println("  Multiple Registrations Usage");
//
//        assertEquals(
//			Collections.singletonList(MultipleRegistrationsImpl.class),
//			ServiceLoaderUtilities.classesOf(MultipleRegistrationsInterface1.class)
//		);
//        assertEquals(
//			Collections.singletonList(MultipleRegistrationsImpl.class),
//			ServiceLoaderUtilities.classesOf(MultipleRegistrationsInterface2.class)
//		);
//
//	}
//
//	@Test
//	@SuppressWarnings("NestedAssignment")
//	public void testOrder() throws Exception {
//
//		System.out.println("  Order Usage");
//
//		assertEquals(
//			Arrays.<Class<?>>asList(OrderedImpl3.class, OrderedImpl2.class, OrderedImpl1.class),
//			ServiceLoaderUtilities.classesOf(OrderedInterface.class)
//		);
//
//		// Order in file should also be fixed, for benefit of ServiceLoader.
//		BufferedReader r = new BufferedReader(
//			new InputStreamReader(
//				ServiceProviderProcessorTest.class.getResourceAsStream("/META-INF/services/" + OrderedInterface.class.getName())
//			)
//		);
//		List<String> lines = new ArrayList<>(3);
//		String line;
//
//		while ( ( line = r.readLine() ) != null ) {
//			lines.add(line);
//		}
//
//		assertEquals(Arrays.asList(
//			OrderedImpl3.class.getName(),
//			ServiceLoaderLine.ORDER + "100",
//			OrderedImpl2.class.getName(),
//			ServiceLoaderLine.ORDER + "200",
//			OrderedImpl1.class.getName()
//		),
//			lines
//		);
//
//	}

}
