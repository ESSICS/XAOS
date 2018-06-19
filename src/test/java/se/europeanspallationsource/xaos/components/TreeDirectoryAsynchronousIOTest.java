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
import java.text.MessageFormat;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import se.europeanspallationsource.xaos.util.io.DeleteFileVisitor;
import se.europeanspallationsource.xaos.util.io.DirectoryWatcher;

import static org.junit.Assert.assertTrue;
import static se.europeanspallationsource.xaos.util.io.DirectoryWatcher.create;


/**
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( { "ClassWithoutLogger", "UseOfSystemOutOrSystemErr" } )
public class TreeDirectoryAsynchronousIOTest {

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
		root = Files.createTempDirectory("TDAIO_");
			dir_a = Files.createTempDirectory(root, "TDAIO_a_");
				file_a = Files.createTempFile(dir_a, "TDAIO_a_", ".test");
				dir_a_c = Files.createTempDirectory(dir_a, "TDAIO_a_c_");
					file_a_c = Files.createTempFile(dir_a_c, "TDAIO_a_c_", ".test");
			dir_b = Files.createTempDirectory(root, "TDAIO_b_");
				file_b1 = Files.createTempFile(dir_b, "TDAIO_b1_", ".test");
				file_b2 = Files.createTempFile(dir_b, "TDAIO_b2_", ".test");

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
	 * Test of createDirectories method, of class TreeDirectoryAsynchronousIO.
	 */
	@Test
	public void testCreateDirectories() {
	}

	/**
	 * Test of createDirectory method, of class TreeDirectoryAsynchronousIO.
	 *
	 * @throws java.io.IOException
	 * @throws java.lang.InterruptedException
	 * @throws java.util.concurrent.ExecutionException
	 */
	@Test
	public void testCreateDirectory() throws IOException, InterruptedException, ExecutionException {

		System.out.println(MessageFormat.format("  Testing ''createDirectory'' [on {0}]...", root));

		DirectoryWatcher watcher = create(executor);
		TreeDirectoryModel<TreeDirectoryAsynchronousIOTest, String> model = new TreeDirectoryModel<>(
			this,
			s -> Paths.get(s),
			p -> p != null ? p.toString() : null
		);
		TreeDirectoryAsynchronousIO<TreeDirectoryAsynchronousIOTest, String> treeDAIO = new TreeDirectoryAsynchronousIO<>(
			watcher,
			model,
			executor
		);
		Path toBeCreated = FileSystems.getDefault().getPath(dir_a.toString(), "dir_a_z");

		model.addTopLevelDirectory(dir_a);

		CompletionStage<Void> stage = treeDAIO.createDirectory(toBeCreated, this);

//	TODO:CR add 1 minute timeout.
		stage.toCompletableFuture().get();

		assertTrue(Files.exists(toBeCreated));
		assertTrue(Files.isDirectory(toBeCreated));

//	TODO:CR verify the model has the correct entry.

	}

	/**
	 * Test of createFile method, of class TreeDirectoryAsynchronousIO.
	 */
	@Test
	public void testCreateFile() {
	}

	/**
	 * Test of delete method, of class TreeDirectoryAsynchronousIO.
	 */
	@Test
	public void testDelete() {
	}

	/**
	 * Test of deleteTree method, of class TreeDirectoryAsynchronousIO.
	 */
	@Test
	public void testDeleteTree() {
	}

	/**
	 * Test of readBinaryFile method, of class TreeDirectoryAsynchronousIO.
	 */
	@Test
	public void testReadBinaryFile() {
	}

	/**
	 * Test of readTextFile method, of class TreeDirectoryAsynchronousIO.
	 */
	@Test
	public void testReadTextFile() {
	}

	/**
	 * Test of writeBinaryFile method, of class TreeDirectoryAsynchronousIO.
	 */
	@Test
	public void testWriteBinaryFile() {
	}

	/**
	 * Test of writeTextFile method, of class TreeDirectoryAsynchronousIO.
	 */
	@Test
	public void testWriteTextFile() {
	}

}
