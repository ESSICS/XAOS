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
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.scene.control.TreeItem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import se.europeanspallationsource.xaos.tools.io.DeleteFileVisitor;
import se.europeanspallationsource.xaos.tools.io.DirectoryModel;
import se.europeanspallationsource.xaos.tools.io.PathElement;

import static org.assertj.core.api.Assertions.assertThat;
import static se.europeanspallationsource.xaos.components.TreeDirectoryItems.createDirectoryItem;
import static se.europeanspallationsource.xaos.components.TreeDirectoryItems.createFileItem;
import static se.europeanspallationsource.xaos.components.TreeDirectoryItems.createTopLevelDirectoryItem;
import static se.europeanspallationsource.xaos.components.TreeDirectoryModel.DEFAULT_GRAPHIC_FACTORY;
import static se.europeanspallationsource.xaos.tools.io.PathElement.file;
import static se.europeanspallationsource.xaos.tools.io.PathElement.tree;


/**
 *
 * @author claudiorosati
 */
@SuppressWarnings( { "ClassWithoutLogger", "UseOfSystemOutOrSystemErr" } )
public class TreeDirectoryItemsTest {

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
		root = Files.createTempDirectory("DW_");
			dir_a = Files.createTempDirectory(root, "DW_a_");
				file_a = Files.createTempFile(dir_a, "DW_a_", ".test");
				dir_a_c = Files.createTempDirectory(dir_a, "DW_a_c_");
					file_a_c = Files.createTempFile(dir_a_c, "DW_a_c_", ".test");
			dir_b = Files.createTempDirectory(root, "DW_b_");
				file_b1 = Files.createTempFile(dir_b, "DW_b1_", ".test");
				file_b2 = Files.createTempFile(dir_b, "DW_b2_", ".test");

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
	 * Test of createDirectoryItem method, of class TreeDirectoryItems.
	 *
	 * @throws java.io.IOException
	 */
	@Test
	public void testCreateDirectoryItem() throws IOException {

		System.out.println("  Testing 'createDirectoryItem'...");

		PathElement element = tree(dir_a);
		TreeDirectoryItems.PathItem<PathElement> dItem = createDirectoryItem(
			element,
			DEFAULT_GRAPHIC_FACTORY,
			PathElement::getPath,
			path -> {
				try {
					return Files.isDirectory(path)
						   ? tree(path)
						   : file(path, Files.getLastModifiedTime(path));
				} catch ( IOException ex ) {
					ex.printStackTrace(System.err);
					return null;
				}
			}
		);

		assertThat(dItem)
			.isInstanceOf(TreeDirectoryItems.DirectoryItem.class)
			.hasFieldOrPropertyWithValue("directory", true)
			.hasFieldOrPropertyWithValue("leaf", true)
			.hasFieldOrPropertyWithValue("path", dir_a);

		dItem.asDirectoryItem().addChildFile(
			file_a.getFileName(),
			Files.getLastModifiedTime(file_a),
			DEFAULT_GRAPHIC_FACTORY
		);

		assertThat(dItem)
			.isInstanceOf(TreeDirectoryItems.DirectoryItem.class)
			.hasFieldOrPropertyWithValue("directory", true)
			.hasFieldOrPropertyWithValue("leaf", false)
			.hasFieldOrPropertyWithValue("path", dir_a);

		TreeItem<PathElement> fItem = dItem.getChildren().get(0);

		assertThat(fItem)
			.isInstanceOf(TreeDirectoryItems.FileItem.class)
			.hasFieldOrPropertyWithValue("directory", false)
			.hasFieldOrPropertyWithValue("leaf", true)
			.hasFieldOrPropertyWithValue("path", file_a);

	}

	/**
	 * Test of createFileItem method, of class TreeDirectoryItems.
	 *
	 * @throws java.io.IOException
	 */
	@Test
	public void testCreateFileItem() throws IOException {

		System.out.println("  Testing 'createFileItem'...");

		PathElement element = file(file_a_c, Files.getLastModifiedTime(file_a_c));
		TreeItem<PathElement> item = createFileItem(
			element,
			element.getLastModified(),
			DEFAULT_GRAPHIC_FACTORY,
			PathElement::getPath
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
	 */
	@Test
	public void testCreateTopLevelDirectoryItem() throws IOException {

		System.out.println("  Testing 'testCreateTopLevelDirectoryItem'...");

		PathElement element = tree(root);
		TreeDirectoryItems.PathItem<PathElement> dItem = createTopLevelDirectoryItem(
			element,
			DEFAULT_GRAPHIC_FACTORY,
			PathElement::getPath,
			path -> {
				try {
					return Files.isDirectory(path)
						   ? tree(path)
						   : file(path, Files.getLastModifiedTime(path));
				} catch ( IOException ex ) {
					ex.printStackTrace(System.err);
					return null;
				}
			},
			new DirectoryModel.Reporter<TreeDirectoryItemsTest>() {

				@Override
				public void reportCreation( Path baseDir, Path relativePath, TreeDirectoryItemsTest initiator ) {
					System.out.println(MessageFormat.format(
						"\tCREATION [baseDir = {0}, relativePath = {1}].",
						baseDir,
						relativePath
					));
				}

				@Override
				public void reportDeletion( Path baseDir, Path relativePath, TreeDirectoryItemsTest initiator ) {
					System.out.println(MessageFormat.format(
						"\tDELETION [baseDir = {0}, relativePath = {1}].",
						baseDir,
						relativePath
					));
				}

				@Override
				public void reportError( Throwable error ) {
					System.out.println(MessageFormat.format(
						"\tERROR [error = {0}, message = {1}].",
						error.getClass().getName(),
						error.getMessage()
					));
				}

				@Override
				public void reportModification( Path baseDir, Path relativePath, TreeDirectoryItemsTest initiator ) {
					System.out.println(MessageFormat.format(
						"\tMODIFICATION [baseDir = {0}, relativePath = {1}].",
						baseDir,
						relativePath
					));
				}

			}
		);

		assertThat(dItem)
			.isInstanceOf(TreeDirectoryItems.TopLevelDirectoryItem.class)
			.hasFieldOrPropertyWithValue("directory", true)
			.hasFieldOrPropertyWithValue("leaf", true)
			.hasFieldOrPropertyWithValue("path", root);

		//	TODO:CR Call synch method and assert the results.

	}

}
