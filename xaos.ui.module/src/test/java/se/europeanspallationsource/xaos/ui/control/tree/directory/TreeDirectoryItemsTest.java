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
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import se.europeanspallationsource.xaos.core.util.io.DeleteFileVisitor;
import se.europeanspallationsource.xaos.ui.control.tree.DirectoryModel;
import se.europeanspallationsource.xaos.ui.control.tree.TreeItems;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static se.europeanspallationsource.xaos.ui.control.tree.directory.TreeDirectoryItems.DEFAULT_INJECTOR;
import static se.europeanspallationsource.xaos.ui.control.tree.directory.TreeDirectoryItems.DEFAULT_PROJECTOR;
import static se.europeanspallationsource.xaos.ui.control.tree.directory.TreeDirectoryItems.createDirectoryItem;
import static se.europeanspallationsource.xaos.ui.control.tree.directory.TreeDirectoryItems.createFileItem;
import static se.europeanspallationsource.xaos.ui.control.tree.directory.TreeDirectoryItems.createTopLevelDirectoryItem;
import static se.europeanspallationsource.xaos.ui.control.tree.directory.TreeDirectoryModel.DEFAULT_GRAPHIC_FACTORY;


/**
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( { "ClassWithoutLogger", "UseOfSystemOutOrSystemErr" } )
public class TreeDirectoryItemsTest {

	@BeforeClass
	public static void setUpClass() {
		System.out.println("---- TreeDirectoryItemsTest ------------------------------------");
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
		root = Files.createTempDirectory("TDI_");
			dir_a = Files.createTempDirectory(root, "TDI_a_");
				file_a = Files.createTempFile(dir_a, "TDI_a_", ".test");
				dir_a_c = Files.createTempDirectory(dir_a, "TDI_a_c_");
					file_a_c = Files.createTempFile(dir_a_c, "TDI_a_c_", ".test");
			dir_b = Files.createTempDirectory(root, "TDI_b_");
				file_b1 = Files.createTempFile(dir_b, "TDI_b1_", ".test");
				file_b2 = Files.createTempFile(dir_b, "TDI_b2_", ".test");
	}

	@After
	public void tearDown() throws IOException {
		Files.walkFileTree(root, new DeleteFileVisitor());
	}

	/**
	 * Test of createDirectoryItem method, of class TreeDirectoryItems.
	 *
	 * @throws java.io.IOException
	 * @throws java.lang.InterruptedException
	 */
	@Test
	public void testCreateDirectoryItem() throws IOException, InterruptedException {

		System.out.println("  Testing 'createDirectoryItem'...");

		CountDownLatch expandedLatch = new CountDownLatch(2);
		CountDownLatch collapsedLatch = new CountDownLatch(2);
		TreeDirectoryItems.PathItem<Path> dItem = createDirectoryItem(
			dir_a,
			DEFAULT_GRAPHIC_FACTORY,
			DEFAULT_PROJECTOR,
			DEFAULT_INJECTOR,
			di -> collapsedLatch.countDown(),
			di -> expandedLatch.countDown()
		);

		assertThat(dItem)
			.isInstanceOf(TreeDirectoryItems.DirectoryItem.class)
			.hasFieldOrPropertyWithValue("directory", true)
			.hasFieldOrPropertyWithValue("leaf", false)
			.hasFieldOrPropertyWithValue("path", dir_a);

		dItem.asDirectoryItem().addChildFile(
			file_a.getFileName(),
			Files.getLastModifiedTime(file_a),
			DEFAULT_GRAPHIC_FACTORY
		);
		dItem.asDirectoryItem().addChildDirectory(
			dir_a_c.getFileName(),
			DEFAULT_GRAPHIC_FACTORY
		);

		assertThat(dItem)
			.isInstanceOf(TreeDirectoryItems.DirectoryItem.class)
			.hasFieldOrPropertyWithValue("directory", true)
			.hasFieldOrPropertyWithValue("leaf", false)
			.hasFieldOrPropertyWithValue("path", dir_a);

		//	Directories first.
		TreeItem<Path> fItem = dItem.getChildren().get(1);

		assertThat(fItem)
			.isInstanceOf(TreeDirectoryItems.FileItem.class)
			.hasFieldOrPropertyWithValue("directory", false)
			.hasFieldOrPropertyWithValue("leaf", true)
			.hasFieldOrPropertyWithValue("path", file_a);

		//	Directories first.
		TreeItem<Path> sdItem = dItem.getChildren().get(0);

		assertThat(sdItem)
			.isInstanceOf(TreeDirectoryItems.DirectoryItem.class)
			.hasFieldOrPropertyWithValue("directory", true)
			.hasFieldOrPropertyWithValue("leaf", false)
			.hasFieldOrPropertyWithValue("path", dir_a_c);

		dItem.setExpanded(true);
		sdItem.setExpanded(true);

		if ( !expandedLatch.await(15, TimeUnit.SECONDS) ) {
			fail("Directory expansion not completed in 15 seconds.");
		}

		dItem.setExpanded(false);
		sdItem.setExpanded(false);

		if ( !collapsedLatch.await(15, TimeUnit.SECONDS) ) {
			fail("Directory collapse not completed in 15 seconds.");
		}

	}

	/**
	 * Test of createFileItem method, of class TreeDirectoryItems.
	 *
	 * @throws java.io.IOException
	 */
	@Test
	public void testCreateFileItem() throws IOException {

		System.out.println("  Testing 'createFileItem'...");

		TreeItem<Path> item = createFileItem(
			file_a_c,
			Files.getLastModifiedTime(file_a_c),
			DEFAULT_GRAPHIC_FACTORY,
			DEFAULT_PROJECTOR
		);

		assertThat(item)
			.isInstanceOf(TreeDirectoryItems.FileItem.class)
			.hasFieldOrPropertyWithValue("directory", false)
			.hasFieldOrPropertyWithValue("leaf", true)
			.hasFieldOrPropertyWithValue("path", file_a_c);

	}

	/**
	 * Test of createTopLevelDirectoryItem method, of class TreeDirectoryItems.
	 *
	 * @throws java.io.IOException
	 * @throws java.lang.InterruptedException
	 */
	@Test
	public void testCreateTopLevelDirectoryItem() throws IOException, InterruptedException {

		System.out.println("  Testing 'createTopLevelDirectoryItem'...");

		CountDownLatch expandedLatch = new CountDownLatch(4);
		CountDownLatch collapsedLatch = new CountDownLatch(4);
		CountDownLatch creationLatch = new CountDownLatch(7);
		TreeDirectoryItems.TopLevelDirectoryItem<TreeDirectoryItemsTest, Path> rootItem = createTopLevelDirectoryItem(
			root,
			DEFAULT_GRAPHIC_FACTORY,
			DEFAULT_PROJECTOR,
			DEFAULT_INJECTOR,
			new DirectoryModel.Reporter<TreeDirectoryItemsTest>() {

				@Override
				public void reportCreation( Path baseDir, Path relativePath, TreeDirectoryItemsTest initiator ) {
					System.out.println(MessageFormat.format(
						"    CREATION [baseDir = {0}, relativePath = {1}].",
						baseDir,
						relativePath
					));
					creationLatch.countDown();
				}

				@Override
				public void reportDeletion( Path baseDir, Path relativePath, TreeDirectoryItemsTest initiator ) {
					System.out.println(MessageFormat.format(
						"    DELETION [baseDir = {0}, relativePath = {1}].",
						baseDir,
						relativePath
					));
				}

				@Override
				public void reportError( Throwable error ) {
					System.out.println(MessageFormat.format(
						"    ERROR [error = {0}, message = {1}].",
						error.getClass().getName(),
						error.getMessage()
					));
				}

				@Override
				public void reportModification( Path baseDir, Path relativePath, TreeDirectoryItemsTest initiator ) {
					System.out.println(MessageFormat.format(
						"    MODIFICATION [baseDir = {0}, relativePath = {1}].",
						baseDir,
						relativePath
					));
				}

			},
			di -> collapsedLatch.countDown(),
			di -> expandedLatch.countDown()
		);

		assertThat(rootItem)
			.isInstanceOf(TreeDirectoryItems.TopLevelDirectoryItem.class)
			.hasFieldOrPropertyWithValue("directory", true)
			.hasFieldOrPropertyWithValue("leaf", false)
			.hasFieldOrPropertyWithValue("path", root);

		rootItem.sync(root, this);

		assertThat(rootItem.getChildren().size()).isEqualTo(0);

		TreeItems.expandAll(rootItem, true);

		if ( !creationLatch.await(1, TimeUnit.MINUTES) ) {
			fail("Directory model synchronization not completed in 1 minute.");
		}

		assertThat(rootItem.getChildren().size()).isNotEqualTo(0);

		ObservableList<TreeItem<Path>> children_root = rootItem.getChildren();

		assertThat(children_root).size().isEqualTo(2);

			TreeItem<Path> child_dir_a = children_root.get(0);

			assertThat(child_dir_a)
				.isInstanceOf(TreeDirectoryItems.DirectoryItem.class)
				.hasFieldOrPropertyWithValue("directory", true)
				.hasFieldOrPropertyWithValue("leaf", false)
				.hasFieldOrPropertyWithValue("path", dir_a);

			ObservableList<TreeItem<Path>> children_dir_a = child_dir_a.getChildren();

			assertThat(children_dir_a).size().isEqualTo(2);

				TreeItem<Path> child_dir_a_c = children_dir_a.get(0);

				assertThat(child_dir_a_c)
					.isInstanceOf(TreeDirectoryItems.DirectoryItem.class)
					.hasFieldOrPropertyWithValue("directory", true)
					.hasFieldOrPropertyWithValue("leaf", false)
					.hasFieldOrPropertyWithValue("path", dir_a_c);

				ObservableList<TreeItem<Path>> children_dir_a_c = child_dir_a_c.getChildren();

				assertThat(children_dir_a_c).size().isEqualTo(1);

					TreeItem<Path> child_file_a_c = children_dir_a_c.get(0);

					assertThat(child_file_a_c)
						.isInstanceOf(TreeDirectoryItems.FileItem.class)
						.hasFieldOrPropertyWithValue("directory", false)
						.hasFieldOrPropertyWithValue("leaf", true)
						.hasFieldOrPropertyWithValue("path", file_a_c);

				TreeItem<Path> child_file_a = children_dir_a.get(1);

				assertThat(child_file_a)
					.isInstanceOf(TreeDirectoryItems.FileItem.class)
					.hasFieldOrPropertyWithValue("directory", false)
					.hasFieldOrPropertyWithValue("leaf", true)
					.hasFieldOrPropertyWithValue("path", file_a);

			TreeItem<Path> child_dir_b = children_root.get(1);

			assertThat(child_dir_b)
				.isInstanceOf(TreeDirectoryItems.DirectoryItem.class)
				.hasFieldOrPropertyWithValue("directory", true)
				.hasFieldOrPropertyWithValue("leaf", false)
				.hasFieldOrPropertyWithValue("path", dir_b);

			ObservableList<TreeItem<Path>> children_dir_b = child_dir_b.getChildren();

			assertThat(children_dir_b).size().isEqualTo(2);

				TreeItem<Path> child_file_b1 = children_dir_b.get(0);

				assertThat(child_file_b1)
					.isInstanceOf(TreeDirectoryItems.FileItem.class)
					.hasFieldOrPropertyWithValue("directory", false)
					.hasFieldOrPropertyWithValue("leaf", true)
					.hasFieldOrPropertyWithValue("path", file_b1);

				TreeItem<Path> child_file_b2 = children_dir_b.get(1);

				assertThat(child_file_b2)
					.isInstanceOf(TreeDirectoryItems.FileItem.class)
					.hasFieldOrPropertyWithValue("directory", false)
					.hasFieldOrPropertyWithValue("leaf", true)
					.hasFieldOrPropertyWithValue("path", file_b2);

		if ( !expandedLatch.await(15, TimeUnit.SECONDS) ) {
			fail("Directory expansion not completed in 15 seconds.");
		}

		TreeItems.expandAll(rootItem, false);

		if ( !collapsedLatch.await(15, TimeUnit.SECONDS) ) {
			fail("Directory collapse not completed in 15 seconds.");
		}

	}

}
