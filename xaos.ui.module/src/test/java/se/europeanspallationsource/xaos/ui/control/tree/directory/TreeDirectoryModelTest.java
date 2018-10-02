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
package se.europeanspallationsource.xaos.ui.control.tree.directory;


import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javafx.scene.control.TreeItem;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import se.europeanspallationsource.xaos.core.util.io.DeleteFileVisitor;
import se.europeanspallationsource.xaos.ui.control.tree.directory.TreeDirectoryItems.FileItem;

import static java.nio.file.attribute.FileTime.from;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static se.europeanspallationsource.xaos.core.util.io.PathElement.tree;


/**
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( { "ClassWithoutLogger", "UseOfSystemOutOrSystemErr" } )
public class TreeDirectoryModelTest {

	@BeforeClass
	public static void setUpClass() {
		System.out.println("---- TreeDirectoryModelTest ------------------------------------");
	}

	private Path dir_a;
	private Path dir_a_c;
	private Path dir_b;
	private Path file_a;
	private Path file_a_c;
	private Path file_b1;
	private Path file_b2;
	private Path root;

	@Before
	public void setUp() throws IOException {

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
	}

	/**
	 * Test of addDirectory method, of class TreeDirectoryModel.
	 */
	@Test
	public void testAddDirectory() {

		System.out.println("  Testing 'addDirectory'...");

		TreeDirectoryModel<TreeDirectoryModelTest, String> model = new TreeDirectoryModel<>(
			this,
			s -> Paths.get(s),
			p -> p != null ? p.toString() : null
		);

		assertThat(model.contains(dir_a)).isFalse();
		assertThat(model.contains(dir_a_c)).isFalse();

		model.addTopLevelDirectory(root, false);
		model.addDirectory(dir_a);
		model.addDirectory(dir_a_c);

		assertThat(model.contains(dir_a)).isTrue();
		assertThat(model.contains(dir_a_c)).isTrue();

	}

	/**
	 * Test of addFile method, of class TreeDirectoryModel.
	 * 
	 * @throws java.io.IOException
	 */
	@Test
	public void testAddFile() throws IOException {

		System.out.println("  Testing 'addFile'...");

		TreeDirectoryModel<TreeDirectoryModelTest, String> model = new TreeDirectoryModel<>(
			this,
			s -> Paths.get(s),
			p -> p != null ? p.toString() : null
		);

		assertThat(model.contains(dir_a)).isFalse();
		assertThat(model.contains(dir_a_c)).isFalse();
		assertThat(model.contains(file_a_c)).isFalse();

		model.addTopLevelDirectory(root, false);
		model.addDirectory(dir_a);
		model.addDirectory(dir_a_c);
		model.addFile(file_a_c, Files.getLastModifiedTime(file_a_c));

		assertThat(model.contains(dir_a)).isTrue();
		assertThat(model.contains(dir_a_c)).isTrue();
		assertThat(model.contains(file_a_c)).isTrue();

	}

	/**
	 * Test of addTopLevelDirectory method, of class TreeDirectoryModel.
	 */
	@Test
	public void testAddTopLevelDirectory() {

		System.out.println("  Testing 'addTopLevelDirectory'...");

		TreeDirectoryModel<TreeDirectoryModelTest, String> model = new TreeDirectoryModel<>(
			this,
			s -> Paths.get(s),
			p -> p != null ? p.toString() : null
		);

		assertThat(model.contains(root)).isFalse();

		model.addTopLevelDirectory(root, false);

		assertThat(model.contains(root)).isTrue();

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

		model.addTopLevelDirectory(root, false);

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

		model.addTopLevelDirectory(root, false);
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
	 * @throws java.lang.InterruptedException
	 */
	@Test
	public void testCreations() throws IOException, InterruptedException {

		System.out.println("  Testing 'creations'...");

		CountDownLatch latch = new CountDownLatch(7);
		TreeDirectoryModel<TreeDirectoryModelTest, String> model = new TreeDirectoryModel<>(
			this,
			s -> Paths.get(s),
			p -> p != null ? p.toString() : null
		);

		model.creations().subscribe(u -> latch.countDown());
		model.addTopLevelDirectory(root, false);
		model.sync(tree(root));

		if ( !latch.await(1, TimeUnit.MINUTES) ) {
			fail("Directory model synchronization not completed in 1 minute.");
		}

	}

	/**
	 * Test of delete method, of class TreeDirectoryModel.
	 * 
	 * @throws java.io.IOException
	 */
	@Test( expected = AssertionError.class )
	public void testDelete() throws IOException {

		System.out.println("  Testing 'delete'...");

		TreeDirectoryModel<TreeDirectoryModelTest, String> model = new TreeDirectoryModel<>(
			this,
			s -> Paths.get(s),
			p -> p != null ? p.toString() : null
		);

		assertThat(model.contains(dir_a)).isFalse();
		assertThat(model.contains(dir_a_c)).isFalse();
		assertThat(model.contains(file_a_c)).isFalse();

		model.addTopLevelDirectory(dir_a, false);
		model.addDirectory(dir_a_c);
		model.addFile(file_a_c, Files.getLastModifiedTime(file_a_c));

		assertThat(model.contains(dir_a)).isTrue();
		assertThat(model.contains(dir_a_c)).isTrue();
		assertThat(model.contains(file_a_c)).isTrue();

		model.delete(file_a_c);

		assertThat(model.contains(dir_a)).isTrue();
		assertThat(model.contains(dir_a_c)).isTrue();
		assertThat(model.contains(file_a_c)).isFalse();

		model.delete(file_a_c);

		assertThat(model.contains(dir_a)).isTrue();
		assertThat(model.contains(dir_a_c)).isTrue();
		assertThat(model.contains(file_a_c)).isFalse();

		model.addFile(file_a_c, Files.getLastModifiedTime(file_a_c));

		assertThat(model.contains(dir_a)).isTrue();
		assertThat(model.contains(dir_a_c)).isTrue();
		assertThat(model.contains(file_a_c)).isTrue();

		model.delete(dir_a_c);

		assertThat(model.contains(dir_a)).isTrue();
		assertThat(model.contains(dir_a_c)).isFalse();
		assertThat(model.contains(file_a_c)).isFalse();

		//	file_b2 is outside of the tree rooted at dir_a
		//	  => java.lang.AssertionError is thrown
		model.delete(file_b2);

		assertThat(model.contains(dir_a)).isTrue();
		assertThat(model.contains(dir_a_c)).isFalse();
		assertThat(model.contains(file_a_c)).isFalse();

	}

	/**
	 * Test of deletions method, of class TreeDirectoryModel.
	 * 
	 * @throws java.io.IOException
	 * @throws java.lang.InterruptedException
	 */
	@Test
	public void testDeletions() throws IOException, InterruptedException {

		System.out.println("  Testing 'deletions'...");

		CountDownLatch latch = new CountDownLatch(4);
		TreeDirectoryModel<TreeDirectoryModelTest, String> model = new TreeDirectoryModel<>(
			this,
			s -> Paths.get(s),
			p -> p != null ? p.toString() : null
		);

		model.deletions().subscribe(u -> latch.countDown());
		model.addTopLevelDirectory(root, false);
		model.sync(tree(root));

		Files.walkFileTree(dir_a, new DeleteFileVisitor());

		model.sync(tree(root));

		if ( !latch.await(1, TimeUnit.MINUTES) ) {
			fail("Directory model synchronization not completed in 1 minute.");
		}

	}

	/**
	 * Test of errors method, of class TreeDirectoryModel.
	 *
	 * @throws java.io.IOException
	 * @throws java.lang.InterruptedException
	 */
	@Test
	public void testErrors() throws IOException, InterruptedException {

		System.out.println("  Testing 'errors'...");

		CountDownLatch latch = new CountDownLatch(2);
		TreeDirectoryModel<TreeDirectoryModelTest, String> model = new TreeDirectoryModel<>(
			this,
			s -> Paths.get(s),
			p -> p != null ? p.toString() : null
		);

		model.errors().subscribe(u -> latch.countDown());
		model.addTopLevelDirectory(root, false);
		model.sync(tree(dir_a_c));

		model = new TreeDirectoryModel<>(
			this,
			s -> Paths.get(s),
			p -> p != null ? p.toString() : null
		);

		model.errors().subscribe(u -> latch.countDown());
		model.addTopLevelDirectory(file_a, false);
		model.sync(tree(file_a));

		if ( !latch.await(1, TimeUnit.MINUTES) ) {
			fail("Directory model synchronization not completed in 1 minute.");
		}

	}

	/**
	 * Test of getRoot method, of class TreeDirectoryModel.
	 */
	@Test
	public void testGetRoot() {

		System.out.println("  Testing 'getRoot'...");

		TreeDirectoryModel<TreeDirectoryModelTest, String> model = new TreeDirectoryModel<>(
			this,
			s -> Paths.get(s),
			p -> p != null ? p.toString() : null
		);

		model.addTopLevelDirectory(dir_a, false);
		model.addTopLevelDirectory(dir_b, false);

		TreeItem<String> modelRoot = model.getRoot();

		assertThat(modelRoot).isNotNull();
		assertThat(modelRoot.getChildren()).size().isEqualTo(2);
		assertThat(modelRoot.getChildren()).element(0)
			.isInstanceOf(TreeDirectoryItems.TopLevelDirectoryItem.class)
			.extracting("path").element(0).isEqualTo(dir_a);
		assertThat(modelRoot.getChildren()).element(1)
			.isInstanceOf(TreeDirectoryItems.TopLevelDirectoryItem.class)
			.extracting("path").element(0).isEqualTo(dir_b);

	}

	/**
	 * Test of modifications method, of class TreeDirectoryModel.
	 *
	 * @throws java.io.IOException
	 * @throws java.lang.InterruptedException
	 */
	@Test
	public void testModifications() throws IOException, InterruptedException {

		System.out.println("  Testing 'modifications'...");

		CountDownLatch latch = new CountDownLatch(1);
		TreeDirectoryModel<TreeDirectoryModelTest, String> model = new TreeDirectoryModel<>(
			this,
			s -> Paths.get(s),
			p -> p != null ? p.toString() : null
		);

		model.modifications().subscribe(u -> latch.countDown());
		model.addTopLevelDirectory(root, false);
		model.sync(tree(root));

		//	Give some time before the actual modification.
		Thread.sleep(2222L);

		Files.write(file_b1, "Some text to be written.\n".getBytes());

		model.sync(tree(root));

		if ( !latch.await(1, TimeUnit.MINUTES) ) {
			fail("Directory model synchronization not completed in 1 minute.");
		}

	}

	/**
	 * Test of sync method, of class TreeDirectoryModel.
	 * 
	 * @throws java.io.IOException
	 */
	@Test
	public void testSync() throws IOException {

		System.out.println("  Testing 'sync'...");

		TreeDirectoryModel<TreeDirectoryModelTest, String> model = new TreeDirectoryModel<>(
			this,
			s -> Paths.get(s),
			p -> p != null ? p.toString() : null
		);

		assertThat(model.contains(dir_a)).isFalse();
		assertThat(model.contains(dir_a_c)).isFalse();
		assertThat(model.contains(file_a_c)).isFalse();

		model.addTopLevelDirectory(dir_a, false);
		model.sync(tree(dir_a));

		assertThat(model.contains(dir_a)).isTrue();
		assertThat(model.contains(dir_a_c)).isTrue();
		assertThat(model.contains(file_a_c)).isTrue();

		model.delete(file_a_c);

		assertThat(model.contains(dir_a)).isTrue();
		assertThat(model.contains(dir_a_c)).isTrue();
		assertThat(model.contains(file_a_c)).isFalse();

		model.sync(tree(dir_a));

		assertThat(model.contains(dir_a)).isTrue();
		assertThat(model.contains(dir_a_c)).isTrue();
		assertThat(model.contains(file_a_c)).isTrue();

		model.delete(file_a_c);

		assertThat(model.contains(dir_a)).isTrue();
		assertThat(model.contains(dir_a_c)).isTrue();
		assertThat(model.contains(file_a_c)).isFalse();

		model.sync(tree(dir_a));

		assertThat(model.contains(dir_a)).isTrue();
		assertThat(model.contains(dir_a_c)).isTrue();
		assertThat(model.contains(file_a_c)).isTrue();

		//	Something outside the current roots.
		model.sync(tree(dir_b));

		assertThat(model.contains(dir_b)).isFalse();
		assertThat(model.contains(file_b1)).isFalse();
		assertThat(model.contains(file_b2)).isFalse();

	}

	/**
	 * Test of updateModificationTime method, of class TreeDirectoryModel.
	 *
	 * @throws java.io.IOException
	 */
	@Test
	public void testUpdateModificationTime() throws IOException {

		System.out.println("  Testing 'updateModificationTime'...");

		TreeDirectoryModel<TreeDirectoryModelTest, String> model = new TreeDirectoryModel<>(
			this,
			s -> Paths.get(s),
			p -> p != null ? p.toString() : null
		);

		assertThat(model.contains(dir_a)).isFalse();
		assertThat(model.contains(dir_a_c)).isFalse();
		assertThat(model.contains(file_a_c)).isFalse();

		model.addTopLevelDirectory(root, false);
		model.addDirectory(dir_a);
		model.addDirectory(dir_a_c);

		FileTime lastModifiedTime1 = Files.getLastModifiedTime(file_a_c);

		model.addFile(file_a_c, lastModifiedTime1);

		assertThat(model.contains(dir_a)).isTrue();
		assertThat(model.contains(dir_a_c)).isTrue();
		assertThat(model.contains(file_a_c)).isTrue();

		TreeItem<String> item = model
			.getRoot()				//	The hidden root.
			.getChildren().get(0)	//	top directory: root
			.getChildren().get(0)	//	    directory: dir_a
			.getChildren().get(0)	//	    directory: dir_a_c
			.getChildren().get(0);	//	         file: file_a_c

		assertThat(item)
			.isInstanceOf(FileItem.class)
			.extracting("lastModified").element(0).isEqualTo(lastModifiedTime1);
		
		FileTime lastModifiedTime2 = from(lastModifiedTime1.toInstant().plusSeconds(123L));
		
		assertThat(lastModifiedTime2).isNotEqualTo(lastModifiedTime1);

		model.updateModificationTime(file_a_c, lastModifiedTime2);

		assertThat(item)
			.isInstanceOf(FileItem.class)
			.extracting("lastModified").element(0).isEqualTo(lastModifiedTime2);

	}

}
