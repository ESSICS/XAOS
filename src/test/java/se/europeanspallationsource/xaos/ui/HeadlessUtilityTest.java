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
package se.europeanspallationsource.xaos.ui;


import java.text.MessageFormat;
import java.util.Comparator;
import java.util.Map;
import java.util.Properties;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static se.europeanspallationsource.xaos.ui.HeadlessUtility.conditionallyHeadless;
import static se.europeanspallationsource.xaos.ui.HeadlessUtility.headless;


/**
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( { "ClassWithoutLogger", "UseOfSystemOutOrSystemErr" } )
public class HeadlessUtilityTest {

	private static final Properties originalProperties = new Properties();

	@BeforeClass
	public static void setUpClass() {

		System.out.println("---- HeadlessUtilityTest ---------------------------------------");
		System.out.println("  Saving original properties...");

		originalProperties.putAll(System.getProperties());
//		originalProperties
//			.entrySet()
//			.stream()
//			.sorted(( e1, e2 ) -> String.CASE_INSENSITIVE_ORDER.compare(e1.getKey().toString(), e2.getKey().toString()))
//			.forEach(e -> System.out.println(MessageFormat.format("    {0} = {1}", e.getKey(), e.getValue())));

	}

	@AfterClass
	public static void tearDownClass() {

		System.out.println("  Restoring original properties...");

		System.setProperties(originalProperties);
//		System.getProperties()
//			.entrySet()
//			.stream()
//			.sorted(( e1, e2 ) -> String.CASE_INSENSITIVE_ORDER.compare(e1.getKey().toString(), e2.getKey().toString()))
//			.forEach(e -> System.out.println(MessageFormat.format("    {0} = {1}", e.getKey(), e.getValue())));

	}

	@Before
	public void setUp() {

		System.setProperty("xaos.headless", "");

		System.setProperty("glass.platform", "");
		System.setProperty("monocle.platform", "");
		System.setProperty("prism.order", "");
		System.setProperty("prism.text", "");
		System.setProperty("java.awt.headless", "");
		System.setProperty("testfx.robot", "");
		System.setProperty("testfx.headless", "");

	}

	/**
	 * Test of conditionallyHeadless method, of class HeadlessUtility.
	 */
	@Test
	public void testConditionallyHeadless() {

		System.out.println("  Testing ''conditionallyHeadless''...");

		assertThat(System.getProperty("xaos.headless")).isEmpty();

		assertThat(System.getProperty("glass.platform")).isEmpty();
		assertThat(System.getProperty("monocle.platform")).isEmpty();
		assertThat(System.getProperty("prism.order")).isEmpty();
		assertThat(System.getProperty("prism.text")).isEmpty();
		assertThat(System.getProperty("java.awt.headless")).isEmpty();
		assertThat(System.getProperty("testfx.robot")).isEmpty();
		assertThat(System.getProperty("testfx.headless")).isEmpty();

		conditionallyHeadless();

		assertThat(System.getProperty("xaos.headless")).isEmpty();

		assertThat(System.getProperty("glass.platform")).isEmpty();
		assertThat(System.getProperty("monocle.platform")).isEmpty();
		assertThat(System.getProperty("prism.order")).isEmpty();
		assertThat(System.getProperty("prism.text")).isEmpty();
		assertThat(System.getProperty("java.awt.headless")).isEmpty();
		assertThat(System.getProperty("testfx.robot")).isEmpty();
		assertThat(System.getProperty("testfx.headless")).isEmpty();

		System.setProperty("xaos.headless", "true");

		assertThat(System.getProperty("xaos.headless")).isEqualTo("true");

		conditionallyHeadless();

		assertThat(System.getProperty("glass.platform")).isEqualTo("Monocle");
		assertThat(System.getProperty("monocle.platform")).isEqualTo("Headless");
		assertThat(System.getProperty("prism.order")).isEqualTo("sw");
		assertThat(System.getProperty("prism.text")).isEqualTo("t2k");
		assertThat(System.getProperty("java.awt.headless")).isEqualTo("true");
		assertThat(System.getProperty("testfx.robot")).isEqualTo("glass");
		assertThat(System.getProperty("testfx.headless")).isEqualTo("true");

	}

	/**
	 * Test of headless method, of class HeadlessUtility.
	 */
	@Test
	public void testHeadless() {

		System.out.println("  Testing ''headless''...");

		assertThat(System.getProperty("xaos.headless")).isEmpty();

		assertThat(System.getProperty("glass.platform")).isEmpty();
		assertThat(System.getProperty("monocle.platform")).isEmpty();
		assertThat(System.getProperty("prism.order")).isEmpty();
		assertThat(System.getProperty("prism.text")).isEmpty();
		assertThat(System.getProperty("java.awt.headless")).isEmpty();
		assertThat(System.getProperty("testfx.robot")).isEmpty();
		assertThat(System.getProperty("testfx.headless")).isEmpty();

		headless();

		assertThat(System.getProperty("xaos.headless")).isEmpty();

		assertThat(System.getProperty("glass.platform")).isEqualTo("Monocle");
		assertThat(System.getProperty("monocle.platform")).isEqualTo("Headless");
		assertThat(System.getProperty("prism.order")).isEqualTo("sw");
		assertThat(System.getProperty("prism.text")).isEqualTo("t2k");
		assertThat(System.getProperty("java.awt.headless")).isEqualTo("true");
		assertThat(System.getProperty("testfx.robot")).isEqualTo("glass");
		assertThat(System.getProperty("testfx.headless")).isEqualTo("true");

	}

}
