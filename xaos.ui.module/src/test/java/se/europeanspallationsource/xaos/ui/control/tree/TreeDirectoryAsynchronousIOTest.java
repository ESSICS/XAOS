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
package se.europeanspallationsource.xaos.ui.control.tree;


import se.europeanspallationsource.xaos.ui.control.tree.TreeDirectoryModel;
import se.europeanspallationsource.xaos.ui.control.tree.TreeDirectoryAsynchronousIO;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import se.europeanspallationsource.xaos.core.util.io.DeleteFileVisitor;
import se.europeanspallationsource.xaos.core.util.io.DirectoryWatcher;

import static java.nio.charset.Charset.defaultCharset;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static se.europeanspallationsource.xaos.core.util.io.DirectoryWatcher.build;
import static se.europeanspallationsource.xaos.core.util.io.PathElement.tree;


/**
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( { "ClassWithoutLogger", "UseOfSystemOutOrSystemErr" } )
public class TreeDirectoryAsynchronousIOTest {

	@BeforeClass
	public static void setUpClass() {
		System.out.println("---- TreeDirectoryAsynchronousIOTest ---------------------------");
	}

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
	 *
	 * @throws java.io.IOException
	 * @throws java.lang.InterruptedException
	 * @throws java.util.concurrent.ExecutionException
	 */
	@Test
	@SuppressWarnings( { "BroadCatchBlock", "TooBroadCatch", "UseSpecificCatch" } )
	public void testCreateDirectories() throws IOException, InterruptedException, ExecutionException {

		System.out.println(MessageFormat.format("  Testing ''createDirectories'' [on {0}]...", root));

		DirectoryWatcher watcher = build(executor);
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

		model.addTopLevelDirectory(dir_a);

		Path toBeCreated = FileSystems.getDefault().getPath(dir_a.toString(), "dir_a_x", "dir_a_y", "dir_a_z");
		CompletionStage<Void> stage = treeDAIO.createDirectories(toBeCreated, this);

		stage.toCompletableFuture().get();

		assertTrue(stage.toCompletableFuture().isDone());
		assertTrue(Files.exists(toBeCreated));
		assertTrue(Files.isDirectory(toBeCreated));
		assertTrue(model.contains(toBeCreated));

		Path toFail = file_a;

		stage = treeDAIO.createDirectory(toFail, this);

		try {
			stage.toCompletableFuture().get();
		} catch ( Exception ex ) {
			assertTrue(ex.getCause() instanceof FileAlreadyExistsException);
		}
		assertTrue(stage.toCompletableFuture().isCompletedExceptionally());

		watcher.shutdown();

	}

	/**
	 * Test of createDirectory method, of class TreeDirectoryAsynchronousIO.
	 *
	 * @throws java.io.IOException
	 * @throws java.lang.InterruptedException
	 * @throws java.util.concurrent.ExecutionException
	 */
	@Test
	@SuppressWarnings( { "BroadCatchBlock", "TooBroadCatch", "UseSpecificCatch" } )
	public void testCreateDirectory() throws IOException, InterruptedException, ExecutionException {

		System.out.println(MessageFormat.format("  Testing ''createDirectory'' [on {0}]...", root));

		DirectoryWatcher watcher = build(executor);
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

		model.addTopLevelDirectory(dir_a);

		Path toBeCreated = FileSystems.getDefault().getPath(dir_a.toString(), "dir_a_z");
		CompletionStage<Void> stage = treeDAIO.createDirectory(toBeCreated, this);

		stage.toCompletableFuture().get();

		assertTrue(stage.toCompletableFuture().isDone());
		assertTrue(Files.exists(toBeCreated));
		assertTrue(Files.isDirectory(toBeCreated));
		assertTrue(model.contains(toBeCreated));

		Path toFail1 = dir_a_c;

		stage = treeDAIO.createDirectory(toFail1, this);

		try {
			stage.toCompletableFuture().get();
		} catch ( Exception ex ) {
			assertTrue(ex.getCause() instanceof FileAlreadyExistsException);
		}
		assertTrue(stage.toCompletableFuture().isCompletedExceptionally());

		Path toFail2 = FileSystems.getDefault().getPath(dir_a.toString(), "dir_a_x", "dir_a_y", "dir_a_z");

		stage = treeDAIO.createDirectory(toFail2, this);

		try {
			stage.toCompletableFuture().get();
		} catch ( Exception ex ) {
			assertTrue(ex.getCause() instanceof NoSuchFileException);
		}
		assertTrue(stage.toCompletableFuture().isCompletedExceptionally());

		watcher.shutdown();

	}

	/**
	 * Test of createFile method, of class TreeDirectoryAsynchronousIO.
	 *
	 * @throws java.io.IOException
	 * @throws java.lang.InterruptedException
	 * @throws java.util.concurrent.ExecutionException
	 */
	@Test
	@SuppressWarnings( { "UseSpecificCatch", "BroadCatchBlock", "TooBroadCatch" } )
	public void testCreateFile() throws IOException, InterruptedException, ExecutionException {

		System.out.println(MessageFormat.format("  Testing ''createFile'' [on {0}]...", root));

		DirectoryWatcher watcher = build(executor);
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

		model.addTopLevelDirectory(dir_a);

		Path toBeCreated = FileSystems.getDefault().getPath(dir_a.toString(), "created_file.txt");
		CompletionStage<Void> stage = treeDAIO.createFile(toBeCreated, this);

		stage.toCompletableFuture().get();

		assertTrue(stage.toCompletableFuture().isDone());
		assertTrue(Files.exists(toBeCreated));
		assertFalse(Files.isDirectory(toBeCreated));
		assertTrue(Files.isRegularFile(toBeCreated));
		assertTrue(model.contains(toBeCreated));

		Path toFail = FileSystems.getDefault().getPath(dir_a.toString(), "non-exitent", "created_file.txt");

		stage = treeDAIO.createFile(toFail, this);

		try {
			stage.toCompletableFuture().get();
		} catch ( Exception ex ) {
			assertTrue(ex.getCause() instanceof NoSuchFileException);
		}
		assertTrue(stage.toCompletableFuture().isCompletedExceptionally());

		watcher.shutdown();

	}

	/**
	 * Test of delete method, of class TreeDirectoryAsynchronousIO.
	 *
	 * @throws java.io.IOException
	 * @throws java.lang.InterruptedException
	 * @throws java.util.concurrent.ExecutionException
	 */
	@Test
	@SuppressWarnings( { "UseSpecificCatch", "BroadCatchBlock", "TooBroadCatch" } )
	public void testDelete() throws IOException, InterruptedException, ExecutionException {

		System.out.println(MessageFormat.format("  Testing ''delete'' [on {0}]...", root));

		DirectoryWatcher watcher = build(executor);
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

		model.addTopLevelDirectory(root);
		model.sync(tree(root));

		assertTrue(model.contains(dir_a));
		assertTrue(model.contains(file_a));
		assertTrue(model.contains(dir_a_c));
		assertTrue(model.contains(file_a_c));

		CompletionStage<Void> stage = treeDAIO.delete(file_a_c, this);

		stage.toCompletableFuture().get();

		assertTrue(stage.toCompletableFuture().isDone());
		assertFalse(Files.exists(file_a_c));

		stage = treeDAIO.delete(dir_a_c, this);

		stage.toCompletableFuture().get();

		assertTrue(stage.toCompletableFuture().isDone());
		assertFalse(Files.exists(dir_a_c));

		stage = treeDAIO.delete(dir_a, this);

		try {
			stage.toCompletableFuture().get();
		} catch ( Exception ex ) {
			assertTrue(ex.getCause() instanceof DirectoryNotEmptyException);
		}
		assertTrue(stage.toCompletableFuture().isCompletedExceptionally());
		assertTrue(model.contains(dir_a));
		assertTrue(model.contains(file_a));

		watcher.shutdown();

	}

	/**
	 * Test of deleteTree method, of class TreeDirectoryAsynchronousIO.
	 *
	 * @throws java.io.IOException
	 * @throws java.lang.InterruptedException
	 * @throws java.util.concurrent.ExecutionException
	 */
	@Test
	@SuppressWarnings( { "UseSpecificCatch", "BroadCatchBlock", "TooBroadCatch" } )
	public void testDeleteTree() throws IOException, InterruptedException, ExecutionException {

		System.out.println(MessageFormat.format("  Testing ''deleteTree'' [on {0}]...", root));

		DirectoryWatcher watcher = build(executor);
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

		model.addTopLevelDirectory(root);
		model.sync(tree(root));

		assertTrue(model.contains(dir_a));
		assertTrue(model.contains(file_a));
		assertTrue(model.contains(dir_a_c));
		assertTrue(model.contains(file_a_c));

		CompletionStage<Void> stage = treeDAIO.deleteTree(dir_a, this);

		stage.toCompletableFuture().get();

		assertTrue(stage.toCompletableFuture().isDone());
		assertFalse(model.contains(dir_a));
		assertFalse(model.contains(file_a));
		assertFalse(model.contains(dir_a_c));
		assertFalse(model.contains(file_a_c));

		watcher.shutdown();

	}

	/**
	 * Test of readBinaryFile method, of class TreeDirectoryAsynchronousIO.
	 *
	 * @throws java.io.IOException
	 * @throws java.lang.InterruptedException
	 * @throws java.util.concurrent.ExecutionException
	 */
	@Test
	@SuppressWarnings( { "UseSpecificCatch", "BroadCatchBlock", "TooBroadCatch" } )
	public void testReadBinaryFile() throws IOException, InterruptedException, ExecutionException {

		System.out.println(MessageFormat.format("  Testing ''readBinaryFile'' [on {0}]...", root));

		DirectoryWatcher watcher = build(executor);
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

		model.addTopLevelDirectory(root);
		model.sync(tree(root));

		byte[] content = { 0x00, 0x01, 0x02, 0x03, 0x04, 0x03, 0x02, 0x01, 0x00 };

		Files.write(file_b1, content);

		CompletionStage<byte[]> stage = treeDAIO.readBinaryFile(file_b1, this);

		byte[] read = stage.toCompletableFuture().get();

		assertTrue(stage.toCompletableFuture().isDone());
		assertArrayEquals(content, read);

		Path toFail = FileSystems.getDefault().getPath(dir_a.toString(), "non-exitent", "created_file.txt");

		stage = treeDAIO.readBinaryFile(toFail, this);

		try {
			stage.toCompletableFuture().get();
		} catch ( Exception ex ) {
			assertTrue(ex.getCause() instanceof NoSuchFileException);
		}
		assertTrue(stage.toCompletableFuture().isCompletedExceptionally());

		watcher.shutdown();

	}

	/**
	 * Test of readTextFile method, of class TreeDirectoryAsynchronousIO.
	 *
	 * @throws java.io.IOException
	 * @throws java.lang.InterruptedException
	 * @throws java.util.concurrent.ExecutionException
	 */
	@Test
	@SuppressWarnings( { "UseSpecificCatch", "BroadCatchBlock", "TooBroadCatch" } )
	public void testReadTextFile() throws IOException, InterruptedException, ExecutionException {

		System.out.println(MessageFormat.format("  Testing ''readTextFile'' [on {0}]...", root));

		DirectoryWatcher watcher = build(executor);
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

		model.addTopLevelDirectory(root);
		model.sync(tree(root));

		String content = "First line of text.\nSecond line of text.";
		Charset charset = defaultCharset();

		Files.write(file_b1, content.getBytes(charset), CREATE, WRITE, TRUNCATE_EXISTING);

		CompletionStage<String> stage = treeDAIO.readTextFile(file_b1, charset, this);

		String read = stage.toCompletableFuture().get();

		assertTrue(stage.toCompletableFuture().isDone());
		assertEquals(content, read);

		Path toFail = FileSystems.getDefault().getPath(dir_a.toString(), "non-exitent", "created_file.txt");

		stage = treeDAIO.readTextFile(toFail, charset, this);

		try {
			stage.toCompletableFuture().get();
		} catch ( Exception ex ) {
			assertTrue(ex.getCause() instanceof NoSuchFileException);
		}
		assertTrue(stage.toCompletableFuture().isCompletedExceptionally());

		watcher.shutdown();

	}

	/**
	 * Test of writeBinaryFile method, of class TreeDirectoryAsynchronousIO.
	 *
	 * @throws java.io.IOException
	 * @throws java.lang.InterruptedException
	 * @throws java.util.concurrent.ExecutionException
	 */
	@Test
	public void testWriteBinaryFile() throws IOException, InterruptedException, ExecutionException {

		System.out.println(MessageFormat.format("  Testing ''writeBinaryFile'' [on {0}]...", root));

		DirectoryWatcher watcher = build(executor);
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

		model.addTopLevelDirectory(root);
		model.sync(tree(root));

		byte[] content = { 0x00, 0x01, 0x02, 0x03, 0x04, 0x03, 0x02, 0x01, 0x00 };
		CompletionStage<Void> stage = treeDAIO.writeBinaryFile(file_b1, content, this);

		stage.toCompletableFuture().get();
		assertTrue(stage.toCompletableFuture().isDone());

		byte[] read = Files.readAllBytes(file_b1);

		assertArrayEquals(content, read);

		watcher.shutdown();

	}

	/**
	 * Test of writeTextFile method, of class TreeDirectoryAsynchronousIO.
	 *
	 * @throws java.io.IOException
	 * @throws java.lang.InterruptedException
	 * @throws java.util.concurrent.ExecutionException
	 */
	@Test
	public void testWriteTextFile() throws IOException, InterruptedException, ExecutionException {

		System.out.println(MessageFormat.format("  Testing ''writeBinaryFile'' [on {0}]...", root));

		DirectoryWatcher watcher = build(executor);
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

		model.addTopLevelDirectory(root);
		model.sync(tree(root));

		String content = "First line of text.\nSecond line of text.";
		Charset charset = defaultCharset();
		CompletionStage<Void> stage = treeDAIO.writeTextFile(file_b1, content, charset, this);

		stage.toCompletableFuture().get();
		assertTrue(stage.toCompletableFuture().isDone());

		byte[] read = Files.readAllBytes(file_b1);

		assertEquals(content, new String(read, charset));

		watcher.shutdown();

	}

}
