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
package se.europeanspallationsource.xaos.tools.svg;


import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;


/**
 * @author claudio.rosati@esss.se
 */
@RunWith( Suite.class )
@Suite.SuiteClasses( {
	AtomTest.class,
	GasCylinderTest.class,
	SVGFromInputStreamTest.class,
	SVGFromURLTest.class
} )
@SuppressWarnings( { "ClassMayBeInterface", "ClassWithoutLogger", "UtilityClassWithoutPrivateConstructor" } )
public class SvgSuite {

	@BeforeClass
	public static void setUpSuite() {
		if ( Boolean.getBoolean("headless") ) {
			System.setProperty("testfx.robot", "glass");
			System.setProperty("testfx.headless", "true");
			System.setProperty("prism.order", "sw");
			System.setProperty("prism.text", "t2k");
			System.setProperty("monocle.platform", "Headless");
			System.setProperty("java.awt.headless", "true");
		}
	}

}
