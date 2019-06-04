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
package se.europeanspallationsource.xaos.tests.tools;


import java.util.Collections;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.assertj.core.api.Fail;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;


/**
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( { "ClassWithoutLogger", "UseOfSystemOutOrSystemErr" } )
public class BundleProcessorTest {

	@BeforeClass
	public static void setUpClass() {
		System.out.println("---- BundleProcessorTest ---------------------------------------");
	}

	@Test
	public void testBundlesCreation() {

		System.out.println("  Bundles Creation");

		String resource = "se.europeanspallationsource.xaos.tests.tools.bundles.Bundle";
		ResourceBundle bundle = checkBundle(resource, 6);

		checkItem(bundle, "ClassA.fieldA1.default", "Some initial A1 value.");
		checkItem(bundle, "ClassA.methodAa.1", "First message of method Aa.");
		checkItem(bundle, "ClassA.methodAa.2", "fieldA1: {0}, fieldA2: {1}");
		checkItem(bundle, "ClassA.methodAAa", "First message of method AAa.");
		checkItem(bundle, "ClassA.methodAAb", "First message of method AAb.");
		checkItem(bundle, "ClassC.staticMethodCa.message", "Some Ca message.");

		resource = "se.europeanspallationsource.xaos.tests.tools.bundles.Messages";
		bundle = checkBundle(resource, 2);

		checkItem(bundle, "ClassB.staticFieldB1.default", "Some initial B1 value.");
		checkItem(bundle, "ClassB.methodBAb", "First message of method BAb [{0}].");

		resource = "se.europeanspallationsource.xaos.tests.tools.bundles.p1.LocalizedMessages";
		bundle = checkBundle(resource, 0);

		resource = "se.europeanspallationsource.xaos.tests.tools.bundles.p1.Messages";
		bundle = checkBundle(resource, 2);

		checkItem(bundle, "ClassB.staticFieldB1.default", "Some initial static B1 value.");
		checkItem(bundle, "ClassC.methodCb.message", "Some Cb message [{0}, {1}, {2}].");

	}

	private ResourceBundle checkBundle ( String resource, int messages ) {

		ResourceBundle bundle = null;

		try {

			bundle = ResourceBundle.getBundle(resource);

			assertThat(Collections.list(bundle.getKeys())).size().isEqualTo(messages);


		} catch ( MissingResourceException mrex ) {
			Fail.fail("Unable to ger resource: " + resource, mrex);
		}

		return bundle;

	}

	private void checkItem ( ResourceBundle bundle, String key, String message ) {
		assertTrue(bundle.containsKey(key));
		assertThat(bundle.getString(key)).isEqualTo(message);
	}

}
