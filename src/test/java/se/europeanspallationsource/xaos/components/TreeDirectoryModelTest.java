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
package se.europeanspallationsource.xaos.components;


import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import se.europeanspallationsource.xaos.tools.io.DeleteFileVisitor;

import static org.assertj.core.api.Assertions.assertThat;
import static se.europeanspallationsource.xaos.tools.io.PathElement.tree;


/**
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( { "ClassWithoutLogger", "UseOfSystemOutOrSystemErr" } )
public class TreeDirectoryModelTest {

	private Path dir_a;
	private Path dir_a_c;
	private Path dir_b;
	private ExecutorService executor;
	private Path file_a;
	private Path file_a_c;
	private Path file_b1;
	private Path file_b2;
	private Path root;

	@Before
	public void setUp() throws IOException {

		executor = Executors.newSingleThreadExecutor();
		root = Files.createTempDirectory("TDM_");
			dir_a = Files.createTempDirectory(root, "TDM_a_");
				file_a = Files.createTempFile(dir_a, "TDM_a_", ".test");
				dir_a_c = Files.createTempDirectory(dir_a, "TDM_a_c_");
					file_a_c = Files.createTempFile(dir_a_c, "TDM_a_c_", ".test");
			dir_b = Files.createTempDirectory(root, "TDM_b_");
				file_b1 = Files.createTempFile(dir_b, "TDM_b1_", ".test");
				file_b2 = Files.createTempFile(dir_b, "TDM_b2_", ".test");

//		System.out.println(MessageFormat.format(
//			"  Testing 'DirectoryWatcher'\n"
//			+ "    created directories:\n"
//			+ "      {0}\n"
//			+ "      {1}\n"
//			+ "      {2}\n"
//			+ "      {3}\n"
//			+ "    created files:\n"
//			+ "      {4}\n"
//			+ "      {5}\n"
//			+ "      {6}\n"
//			+ "      {7}",
//			root,
//			dir_a,
//			dir_a_c,
//			dir_b,
//			file_a,
//			file_a_c,
//			file_b1,
//			file_b2
//		));
//
	}

	@After
	public void tearDown() throws IOException {
		Files.walkFileTree(root, new DeleteFileVisitor());
		executor.shutdown();
	}

	/**
	 * Test of addDirectory method, of class TreeDirectoryModel.
	 */
	@Test
	public void testAddDirectory_Path() {
	}

	/**
	 * Test of addDirectory method, of class TreeDirectoryModel.
	 */
	@Test
	public void testAddDirectory_Path_GenericType() {
	}

	/**
	 * Test of addFile method, of class TreeDirectoryModel.
	 */
	@Test
	public void testAddFile_3args() {
	}

	/**
	 * Test of addFile method, of class TreeDirectoryModel.
	 */
	@Test
	public void testAddFile_Path_FileTime() {
	}

	/**
	 * Test of addTopLevelDirectory method, of class TreeDirectoryModel.
	 */
	@Test
	public void testAddTopLevelDirectory() {
	}

	/**
	 * Test of contains method, of class TreeDirectoryModel.
	 *
	 * @throws java.io.IOException
	 */
	@Test
	@SuppressWarnings( "null" )
	public void testContains() throws IOException {

		System.out.println("  Testing 'contains'...");

		TreeDirectoryModel<TreeDirectoryModelTest, String> model = new TreeDirectoryModel<>(
			this,
			s -> Paths.get(s),
			p -> p != null ? p.toString() : null
		);

		//	No roots in the model.
		assertThat(model.contains(file_a_c)).isFalse();

		model.addTopLevelDirectory(root);

		//	Roots not yet synchronized.
		assertThat(model.contains(file_a_c)).isFalse();

		model.sync(tree(dir_a_c));

		//	Roots not yet synchronized, so dir_a_c does not exist in the model.
		assertThat(model.contains(dir_a_c)).isFalse();
		assertThat(model.contains(file_a_c)).isFalse();

		model.sync(tree(root));

		//	Roots synchronized, not both dir_a_c and file_a_c are in the model.
		assertThat(model.contains(dir_a_c)).isTrue();
		assertThat(model.contains(file_a_c)).isTrue();

	}

	/**
	 * Test of containsPrefixOf method, of class TreeDirectoryModel.
	 *
	 * @throws java.io.IOException
	 */
	@Test
	public void testContainsPrefixOf() throws IOException {

		System.out.println("  Testing 'containsPrefixOf'...");

		TreeDirectoryModel<TreeDirectoryModelTest, String> model = new TreeDirectoryModel<>(
			this,
			s -> Paths.get(s),
			p -> p != null ? p.toString() : null
		);

		//	No roots in the model.
		assertThat(model.contains(file_a_c)).isFalse();

		model.addTopLevelDirectory(root);
		model.sync(tree(root));

		//	Roots synchronized, not both dir_a_c and file_a_c are in the model.
		assertThat(model.containsPrefixOf(dir_a_c)).isTrue();
		assertThat(model.containsPrefixOf(file_a_c)).isTrue();
		assertThat(model.containsPrefixOf(FileSystems.getDefault().getPath(System.getProperty("user.dir")))).isFalse();

	}

	/**
	 * Test of creations method, of class TreeDirectoryModel.
	 *
	 * @throws java.io.IOException
	 */
	@Test
	public void testCreations() throws IOException {

		System.out.println("  Testing 'creations'...");

		TreeDirectoryModel<TreeDirectoryModelTest, String> model = new TreeDirectoryModel<>(
			this,
			s -> Paths.get(s),
			p -> p != null ? p.toString() : null
		);

		model.creations().subscribe(u -> {
			System.out.println("**** " + u.getType().name());
		});

		model.addTopLevelDirectory(root);
		model.sync(tree(root));

	}

	/**
	 * Test of delete method, of class TreeDirectoryModel.
	 */
	@Test
	public void testDelete_Path() {
	}

	/**
	 * Test of delete method, of class TreeDirectoryModel.
	 */
	@Test
	public void testDelete_Path_GenericType() {
	}

	/**
	 * Test of deletions method, of class TreeDirectoryModel.
	 */
	@Test
	public void testDeletions() {
	}

	/**
	 * Test of errors method, of class TreeDirectoryModel.
	 */
	@Test
	public void testErrors() {
	}

	/**
	 * Test of getRoot method, of class TreeDirectoryModel.
	 */
	@Test
	public void testGetRoot() {
	}

	/**
	 * Test of modifications method, of class TreeDirectoryModel.
	 */
	@Test
	public void testModifications() {
	}

	/**
	 * Test of setGraphicFactory method, of class TreeDirectoryModel.
	 */
	@Test
	public void testSetGraphicFactory() {
	}

	/**
	 * Test of sync method, of class TreeDirectoryModel.
	 */
	@Test
	public void testSync_PathElement() {
	}

	/**
	 * Test of sync method, of class TreeDirectoryModel.
	 */
	@Test
	public void testSync_PathElement_GenericType() {
	}

	/**
	 * Test of updateModificationTime method, of class TreeDirectoryModel.
	 */
	@Test
	public void testUpdateModificationTime_3args() {
	}

	/**
	 * Test of updateModificationTime method, of class TreeDirectoryModel.
	 */
	@Test
	public void testUpdateModificationTime_Path_FileTime() {
	}

}
