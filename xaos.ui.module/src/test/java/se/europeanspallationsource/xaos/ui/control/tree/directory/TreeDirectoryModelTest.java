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


import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.text.MessageFormat;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javafx.scene.control.TreeItem;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import se.europeanspallationsource.xaos.core.util.io.DeleteFileVisitor;
import se.europeanspallationsource.xaos.ui.control.tree.TreeItemWalker;
import se.europeanspallationsource.xaos.ui.control.tree.TreeItems;

import static java.nio.file.attribute.FileTime.from;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;


/**
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( { "ClassWithoutLogger", "UseOfSystemOutOrSystemErr" } )
public class TreeDirectoryModelTest {

	/**
	 * Enable/disable the {@link #printTree(TreeDirectoryModel, String)}. For
	 * more verbose output set it to {@code true}.
	 */
	private static final boolean VERBOSE = Boolean.getBoolean("xaos.test.verbose");

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

		model.addTopLevelDirectory(root);
		model.addDirectory(dir_a);
		model.addDirectory(dir_a_c);
		printTree(model, "After adding directories:");

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

		model.addTopLevelDirectory(root);
		model.addDirectory(dir_a);
		model.addDirectory(dir_a_c);
		model.addFile(file_a_c, Files.getLastModifiedTime(file_a_c));
		printTree(model, "After adding directories and file:");

		assertThat(model.contains(dir_a)).isTrue();
		assertThat(model.contains(dir_a_c)).isTrue();
		assertThat(model.contains(file_a_c)).isTrue();

	}

	/**
	 * Test of addTopLevelDirectory method, of class TreeDirectoryModel.
	 */
	@Test
	public void testAddTopLevelDirectoryNoCallback() {

		System.out.println("  Testing 'addTopLevelDirectory (no callback)'...");

		TreeDirectoryModel<TreeDirectoryModelTest, String> model = new TreeDirectoryModel<>(
			this,
			s -> Paths.get(s),
			p -> p != null ? p.toString() : null
		);

		assertThat(model.contains(root)).isFalse();

		model.addTopLevelDirectory(root);
		printTree(model, "After adding top directory:");

		assertThat(model.contains(root)).isTrue();

	}

	/**
	 * Test of addTopLevelDirectory method, of class TreeDirectoryModel.
	 * 
	 * @throws java.lang.InterruptedException
	 */
	@Test
	public void testAddTopLevelDirectoryWithCallback() throws InterruptedException {

		System.out.println("  Testing 'addTopLevelDirectory (with callbacks)'...");

		CountDownLatch expandedLatch = new CountDownLatch(4);
		CountDownLatch collapsedLatch = new CountDownLatch(4);
		TreeDirectoryModel<TreeDirectoryModelTest, String> model = new TreeDirectoryModel<>(
			this,
			s -> Paths.get(s),
			p -> p != null ? p.toString() : null
		);

		assertThat(model.contains(root)).isFalse();

		model.addTopLevelDirectory(
			root,
			di -> collapsedLatch.countDown(),
			di -> expandedLatch.countDown()
		);
		printTree(model, "After adding top directory:");

		assertThat(model.contains(root)).isTrue();

		TreeItems.expandAll(model.getRoot(), true);
		printTree(model, "After tree expansion:");

		assertThat(model.contains(dir_a)).isFalse();
		assertThat(model.contains(dir_b)).isFalse();

		model.sync(root, this);
		printTree(model, "After synch:");

		TreeItems.expandAll(model.getRoot(), true);
		printTree(model, "After tree expansion:");

		if ( !expandedLatch.await(15, TimeUnit.SECONDS) ) {
			fail("Directory expansion not completed in 15 seconds.");
		}

		TreeItems.expandAll(model.getRoot(), false);

		if ( !collapsedLatch.await(15, TimeUnit.SECONDS) ) {
			fail("Directory collapse not completed in 15 seconds.");
		}

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
		printTree(model, "After adding top directory:");

		//	Roots not yet synchronized.
		assertThat(model.contains(file_a_c)).isFalse();
		assertThat(model.contains(file_b1)).isFalse();

		model.sync(dir_a_c);
		printTree(model, "After sync on a wrong directory:");

		//	Roots not yet synchronized, so dir_a_c does not exist in the model.
		assertThat(model.contains(dir_a_c)).isFalse();
		assertThat(model.contains(file_a_c)).isFalse();

		model.sync(root);
		printTree(model, "After sync on root directory:");

		//	Roots synchronized, but not yet expanded.
		assertThat(model.contains(dir_a_c)).isFalse();
		assertThat(model.contains(file_a_c)).isFalse();

		TreeItems.expandAll(model.getRoot(), true);
		printTree(model, "After tree expansion:");

		//	Roots synchronized and expanded.
		//	Now both dir_a_c and file_a_c are in the model.
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
		model.sync(root);
		printTree(model, "After adding top directory and sync:");

		//	Roots synchronized, now both dir_a_c and file_a_c are in the model.
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
		model.addTopLevelDirectory(root);
		model.sync(root);
		
		TreeItems.expandAll(model.getRoot(), true);
		printTree(model, "After adding top directory, sync and tree expansion:");

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

		model.addTopLevelDirectory(dir_a);
		model.addDirectory(dir_a_c);
		model.addFile(file_a_c, Files.getLastModifiedTime(file_a_c));
		printTree(model, "After adding directories and file:");

		assertThat(model.contains(dir_a)).isTrue();
		assertThat(model.contains(dir_a_c)).isTrue();
		assertThat(model.contains(file_a_c)).isTrue();

		model.delete(file_a_c);
		printTree(model, "After file removal:");

		assertThat(model.contains(dir_a)).isTrue();
		assertThat(model.contains(dir_a_c)).isTrue();
		assertThat(model.contains(file_a_c)).isFalse();

		model.delete(file_a_c);
		printTree(model, "After removal ot he same file again:");

		assertThat(model.contains(dir_a)).isTrue();
		assertThat(model.contains(dir_a_c)).isTrue();
		assertThat(model.contains(file_a_c)).isFalse();

		model.addFile(file_a_c, Files.getLastModifiedTime(file_a_c));
		printTree(model, "After adding back the removed file:");

		assertThat(model.contains(dir_a)).isTrue();
		assertThat(model.contains(dir_a_c)).isTrue();
		assertThat(model.contains(file_a_c)).isTrue();

		model.delete(dir_a_c);
		printTree(model, "After directory removal:");

		assertThat(model.contains(dir_a)).isTrue();
		assertThat(model.contains(dir_a_c)).isFalse();
		assertThat(model.contains(file_a_c)).isFalse();

		//	file_b2 is outside of the tree rooted at dir_a
		//	  => java.lang.AssertionError is thrown
		model.delete(file_b2);

		//	The following code should never be executed
		System.out.println("ERROR: The following code should never be executed!");
		printTree(model, "After removal of a file not in the model:");

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
		model.addTopLevelDirectory(root);
		model.sync(root);

		TreeItems.expandAll(model.getRoot(), true);
		printTree(model, "After adding top directory, sync and tree expansion:");

		Files.walkFileTree(dir_a, new DeleteFileVisitor());

		model.sync(root);
		printTree(model, "After directory removal and sync:");

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

		model.errors().subscribe(t -> {
			latch.countDown();
			if ( VERBOSE ) {
				System.out.println(MessageFormat.format(
					"    ERROR: {0} [{1}].",
					t.getMessage(),
					t.getClass().getName()
				));
			}
		});
		model.addTopLevelDirectory(root);
		model.sync(dir_a_c);
		printTree(model, "After adding top directory and sync:");

		model = new TreeDirectoryModel<>(
			this,
			s -> Paths.get(s),
			p -> p != null ? p.toString() : null
		);

		model.errors().subscribe(t -> {
			latch.countDown();
			if ( VERBOSE ) {
				System.out.println(MessageFormat.format(
					"    ERROR: {0} [{1}].",
					t.getMessage(),
					t.getClass().getName()
				));
			}
		});
		model.addTopLevelDirectory(file_a);
		model.sync(file_a);
		printTree(model, "After adding top directory and sync:");

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

		model.addTopLevelDirectory(dir_a);
		model.addTopLevelDirectory(dir_b);
		printTree(model, "After adding top directories:");

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
		model.addTopLevelDirectory(root);
		model.sync(root);

		TreeItems.expandAll(model.getRoot(), true);
		printTree(model, "After adding top directory, sync and tree expansion:");

		//	Give some time before the actual modification.
		Thread.sleep(2222L);

		FileTime timeBeforeWrite = Files.getLastModifiedTime(file_b1);
		TreeDirectoryItems.PathItem<String> fileItem = (TreeDirectoryItems.PathItem<String>) TreeItemWalker
			.build(model.getRoot())
			.stream()
			.filter(ti -> {
				if ( ti instanceof TreeDirectoryItems.PathItem ) {
					return ((TreeDirectoryItems.PathItem<String>) ti).getPath().equals(file_b1);
				} else {
					return false;
				}
			})
			.findFirst().get();

		assertThat(timeBeforeWrite).isEqualTo(fileItem.asFileItem().getLastModified());

		Files.write(file_b1, "Some text to be written.\n".getBytes());

		FileTime timeAfterWrite = Files.getLastModifiedTime(file_b1);

		assertThat(timeAfterWrite).isNotEqualTo(timeBeforeWrite);
		assertThat(timeAfterWrite).isNotEqualTo(fileItem.asFileItem().getLastModified());

		model.sync(root);

		assertThat(timeAfterWrite).isEqualTo(fileItem.asFileItem().getLastModified());

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

		model.addTopLevelDirectory(dir_a);
		model.sync(dir_a);

		TreeItems.expandAll(model.getRoot(), true);
		printTree(model, "After adding top directory, sync and tree expansion:");

		assertThat(model.contains(dir_a)).isTrue();
		assertThat(model.contains(dir_a_c)).isTrue();
		assertThat(model.contains(file_a_c)).isTrue();
		assertThat(model.contains(file_a)).isTrue();

		model.delete(file_a_c);
		printTree(model, "After file removal from model:");

		assertThat(model.contains(dir_a)).isTrue();
		assertThat(model.contains(dir_a_c)).isTrue();
		assertThat(model.contains(file_a_c)).isFalse();
		assertThat(model.contains(file_a)).isTrue();

		model.sync(dir_a);
		printTree(model, "After sync:");

		assertThat(model.contains(dir_a)).isTrue();
		assertThat(model.contains(dir_a_c)).isTrue();
		assertThat(model.contains(file_a_c)).isTrue();
		assertThat(model.contains(file_a)).isTrue();

		model.delete(dir_a_c);
		printTree(model, "After directory removal from model:");

		assertThat(model.contains(dir_a)).isTrue();
		assertThat(model.contains(dir_a_c)).isFalse();
		assertThat(model.contains(file_a_c)).isFalse();
		assertThat(model.contains(file_a)).isTrue();

		model.sync(dir_a);
		printTree(model, "After sync:");

		assertThat(model.contains(dir_a)).isTrue();
		assertThat(model.contains(dir_a_c)).isTrue();
		assertThat(model.contains(file_a_c)).isFalse();
		assertThat(model.contains(file_a)).isTrue();

		TreeItems.expandAll(model.getRoot(), true);
		printTree(model, "After tree expansion:");

		assertThat(model.contains(dir_a)).isTrue();
		assertThat(model.contains(dir_a_c)).isTrue();
		assertThat(model.contains(file_a_c)).isTrue();
		assertThat(model.contains(file_a)).isTrue();

		//	Something outside the current roots.
		model.sync(dir_b);
		printTree(model, "After sync:");

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

		model.addTopLevelDirectory(root);
		model.addDirectory(dir_a);
		model.addDirectory(dir_a_c);

		FileTime lastModifiedTime1 = Files.getLastModifiedTime(file_a_c);

		model.addFile(file_a_c, lastModifiedTime1);
		printTree(model, "After adding directories and file:");

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
			.isInstanceOf(TreeDirectoryItems.FileItem.class)
			.extracting("lastModified").element(0).isEqualTo(lastModifiedTime1);

		FileTime lastModifiedTime2 = from(lastModifiedTime1.toInstant().plusSeconds(123L));

		assertThat(lastModifiedTime2).isNotEqualTo(lastModifiedTime1);

		model.updateModificationTime(file_a_c, lastModifiedTime2);

		assertThat(item)
			.isInstanceOf(TreeDirectoryItems.FileItem.class)
			.extracting("lastModified").element(0).isEqualTo(lastModifiedTime2);

	}

	private void printTree( TreeDirectoryModel<TreeDirectoryModelTest, String> model ) {
		printTree(model, null);
	}

	private void printTree( TreeDirectoryModel<TreeDirectoryModelTest, String> model, String comment ) {

		if ( !VERBOSE ) {
			return;
		}

		final boolean noComment = StringUtils.isBlank(comment);

		if ( !noComment ) {
			System.out.println("    " + comment);
		}

		TreeItemWalker.visitValue(model.getRoot(), ( v, d ) -> {

			StringBuilder builder = new StringBuilder(noComment ? "  " : "    ");

			for ( int i = 0; i < d; i++ ) {
				builder.append("  ");
			}

			String item = "<root>";

			if ( v != null ) {
				System.out.println(builder.append(v.substring(1 + v.lastIndexOf(File.separatorChar))).toString());
			}

		});
	}

}
