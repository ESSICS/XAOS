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
package se.europeanspallationsource.xaos.ui.control;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import se.europeanspallationsource.xaos.ui.control.tree.TreeDirectoryAsynchronousIOTest;
import se.europeanspallationsource.xaos.ui.control.tree.TreeDirectoryItemsTest;
import se.europeanspallationsource.xaos.ui.control.tree.TreeDirectoryModelTest;


/**
 * @author claudio.rosati@esss.se
 */
@RunWith( Suite.class )
@Suite.SuiteClasses( {
	//	TreeDirectory
	TreeDirectoryAsynchronousIOTest.class,
	TreeDirectoryItemsTest.class,
	TreeDirectoryModelTest.class,
	TreeDirectoryMonitorTest.class,
	//	SVG
	AtomTest.class,
	GasCylinderTest.class,
	SVGFromInputStreamTest.class,
	SVGFromURLTest.class
} )
@SuppressWarnings( { "ClassMayBeInterface", "ClassWithoutLogger", "UtilityClassWithoutPrivateConstructor" } )
public class ControlSuite {
}
