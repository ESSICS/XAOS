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
package se.europeanspallationsource.xaos.ui.control.tree;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import se.europeanspallationsource.xaos.ui.control.tree.directory.TreeDirectoryAsynchronousIOTest;
import se.europeanspallationsource.xaos.ui.control.tree.directory.TreeDirectoryItemsTest;
import se.europeanspallationsource.xaos.ui.control.tree.directory.TreeDirectoryModelTest;
import se.europeanspallationsource.xaos.ui.control.tree.directory.TreeDirectoryMonitorTest;


/**
 * @author claudio.rosati@esss.se
 */
@RunWith( Suite.class )
@Suite.SuiteClasses( {
	//	tree
	FilterableTreeItemTest.class,
	TreeItemsTest.class,
	//	tree directory
	TreeDirectoryAsynchronousIOTest.class,
	TreeDirectoryItemsTest.class,
	TreeDirectoryModelTest.class,
	TreeDirectoryMonitorTest.class,
} )
@SuppressWarnings( { "ClassMayBeInterface", "ClassWithoutLogger", "UtilityClassWithoutPrivateConstructor" } )
public class TreeSuite {
}
