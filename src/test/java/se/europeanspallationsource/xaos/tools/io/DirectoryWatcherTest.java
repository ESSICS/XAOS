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
package se.europeanspallationsource.xaos.tools.io;


import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchKey;
import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.reactfx.EventStream;

import static java.nio.file.StandardOpenOption.APPEND;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static se.europeanspallationsource.xaos.tools.io.DirectoryWatcher.create;


/**
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( { "ClassWithoutLogger", "UseOfSystemOutOrSystemErr" } )
public class DirectoryWatcherTest {

	private Path dir_a;
	private Path dir_a_c;
	private Path dir_b;
	private ExecutorService executor;
	private Path file_a;
	private Path file_a_c;
	private Path file_b1;
	private Path file_b2;
	private Path root;

	public DirectoryWatcherTest() {
	}

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
	 * Test of create method, of class DirectoryWatcher.
	 *
	 * @throws java.io.IOException
	 */
	@Test
	public void testCreate() throws IOException {

		System.out.println("  Testing 'create'...");

		DirectoryWatcher watcher = create(executor);

		assertNotNull(watcher);
		assertFalse(watcher.isShutdown());

		watcher.shutdown();

	}

	/**
	 * Test of createDirectory method, of class DirectoryWatcher.
	 */
	@Test
	public void testCreateDirectory() {
	}

	/**
	 * Test of createFile method, of class DirectoryWatcher.
	 *
	 * @throws java.io.IOException
	 */
	@Test
	public void testCreateFile() throws IOException {

		System.out.println(MessageFormat.format("  Testing ''createFile'' [on {0}]...", root));

		DirectoryWatcher watcher = create(executor);
		Path toBeCreated = FileSystems.getDefault().getPath(dir_a.toString(), "created_file.txt");

		watcher.createFile(
			toBeCreated,
			t -> assertNotNull(t),
			e -> fail(MessageFormat.format("File not created: {0}", toBeCreated))
		);

		Path toFail = FileSystems.getDefault().getPath(dir_a.toString(), "non-exitent", "created_file.txt");

		watcher.createFile(
			toFail,
			t -> fail(MessageFormat.format("File was created: {0}", toFail)),
			e -> { 
				assertNotNull(e);
				assertTrue(e instanceof IOException);
			}
		);

		watcher.shutdown();

	}

	/**
	 * Test of delete method, of class DirectoryWatcher.
	 */
	@Test
	public void testDelete() {
	}

	/**
	 * Test of deleteTree method, of class DirectoryWatcher.
	 */
	@Test
	public void testDeleteTree() {
	}

	/**
	 * Test of getErrorsStream method, of class DirectoryWatcher.
	 *
	 * @throws java.io.IOException
	 */
	@Test
	public void testGetErrorsStream() throws IOException {

		System.out.println("  Testing 'getErrorsStream'...");

		DirectoryWatcher watcher = create(executor);
		EventStream<Throwable> errorsStream = watcher.getErrorsStream();

		assertNotNull(errorsStream);

		watcher.shutdown();

	}

	/**
	 * Test of getSignalledKeysStream method, of class DirectoryWatcher.
	 *
	 * @throws java.io.IOException
	 */
	@Test
	public void testGetSignalledKeysStream() throws IOException {

		System.out.println("  Testing 'getSignalledKeysStream'...");

		DirectoryWatcher watcher = create(executor);
		EventStream<WatchKey> signalledKeysStream = watcher.getSignalledKeysStream();

		assertNotNull(signalledKeysStream);

		watcher.shutdown();

	}

	/**
	 * Test of isShutdown method, of class DirectoryWatcher.
	 *
	 * @throws java.io.IOException
	 */
	@Test
	public void testIsShutdown() throws IOException {

		System.out.println("  Testing 'isShutdown'...");

		DirectoryWatcher watcher = create(executor);

		assertFalse(watcher.isShutdown());

		watcher.shutdown();

		assertTrue(watcher.isShutdown());

	}

	/**
	 * Test of readBinaryFile method, of class DirectoryWatcher.
	 */
	@Test
	public void testReadBinaryFile() {
	}

	/**
	 * Test of readTextFile method, of class DirectoryWatcher.
	 */
	@Test
	public void testReadTextFile() {
	}

	/**
	 * Test of shutdown method, of class DirectoryWatcher.
	 *
	 * @throws java.io.IOException
	 */
	@Test
	public void testShutdown() throws IOException {

		System.out.println("  Testing 'shutdown'...");

		DirectoryWatcher watcher = create(executor);

		assertFalse(watcher.isShutdown());

//	TODO:CR	Add an operation: it should proceed succesfully.
		watcher.shutdown();

		assertTrue(watcher.isShutdown());

//	TODO:CR	Add an operation: it should throw RejectedExecutionException.
//
	}

	/**
	 * Test of tree method, of class DirectoryWatcher.
	 *
	 * @throws java.io.IOException
	 */
	@Test
	public void testTree() throws IOException {

		System.out.println(MessageFormat.format("  Testing ''tree'' [on {0}]...", root));

		DirectoryWatcher watcher = create(executor);
		CompletableFuture<PathElement> future = watcher.tree(root);

		assertNotNull(future);

		PathElement rootElement = null;

		try {
			rootElement = future.get(1, TimeUnit.MINUTES);
		} catch ( InterruptedException | ExecutionException | TimeoutException ex ) {
			fail(MessageFormat.format("Unable to get tree: {0} [{1}].", ex.getClass().getName(), ex.getMessage()));
		}

		assertNotNull(rootElement);

		assertEquals(root, rootElement.getPath());
		assertTrue(rootElement.isDirectory());

		List<PathElement> rootChildren = rootElement.getChildren();

		assertNotNull(rootChildren);
		assertEquals(2, rootChildren.size());

			PathElement dirAElement = rootChildren.get(0);

			assertNotNull(dirAElement);
			assertEquals(dir_a, dirAElement.getPath());
			assertTrue(dirAElement.isDirectory());

			List<PathElement> dirAChildren = dirAElement.getChildren();

			assertNotNull(dirAChildren);
			assertEquals(2, dirAChildren.size());

				PathElement dirACElement = dirAChildren.get(0);
				
				assertNotNull(dirACElement);
				assertEquals(dir_a_c, dirACElement.getPath());
				assertTrue(dirACElement.isDirectory());
				
					List<PathElement> dirACChildren = dirACElement.getChildren();
					
					assertNotNull(dirACChildren);
					assertEquals(1, dirACChildren.size());
					
					PathElement fileACElement = dirACChildren.get(0);
					
					assertNotNull(fileACElement);
					assertEquals(file_a_c, fileACElement.getPath());
					assertFalse(fileACElement.isDirectory());

				PathElement fileAElement = dirAChildren.get(1);

				assertNotNull(fileAElement);
				assertEquals(file_a, fileAElement.getPath());
				assertFalse(fileAElement.isDirectory());

			PathElement dirBElement = rootChildren.get(1);

			assertNotNull(dirBElement);
			assertEquals(dir_b, dirBElement.getPath());
			assertTrue(dirBElement.isDirectory());

			List<PathElement> dirBChildren = dirBElement.getChildren();

			assertNotNull(dirBChildren);
			assertEquals(2, dirBChildren.size());

				PathElement fileB1Element = dirBChildren.get(0);

				assertNotNull(fileB1Element);
				assertEquals(file_b1, fileB1Element.getPath());
				assertFalse(fileB1Element.isDirectory());

				PathElement fileB2Element = dirBChildren.get(1);

				assertNotNull(fileB2Element);
				assertEquals(file_b2, fileB2Element.getPath());
				assertFalse(fileB2Element.isDirectory());

		watcher.shutdown();


	}

	/**
	 * Test of watch method, of class DirectoryWatcher.
	 *
	 * @throws java.io.IOException
	 * @throws java.lang.InterruptedException
	 */
	@Test
//	@Ignore
	public void testWatch() throws IOException, InterruptedException {

		System.out.println(MessageFormat.format("  Testing ''watch'' [on {0}]...", root));

		CountDownLatch createLatch = new CountDownLatch(1);
		CountDownLatch deleteLatch = new CountDownLatch(1);
		CountDownLatch modifyLatch = new CountDownLatch(1);
		DirectoryWatcher watcher = create(executor);

		watcher.getSignalledKeysStream().subscribe(key -> {
			key.pollEvents().stream().forEach(e -> {
				if ( StandardWatchEventKinds.ENTRY_CREATE.equals(e.kind()) ) {
					createLatch.countDown();
				} else if ( StandardWatchEventKinds.ENTRY_DELETE.equals(e.kind()) ) {
					deleteLatch.countDown();
				} else if ( StandardWatchEventKinds.ENTRY_MODIFY.equals(e.kind()) ) {
					modifyLatch.countDown();
				}
			});
			key.reset();
		});

		watcher.watch(root);

		Path tmpFile = Files.createTempFile(root, "DW_", ".test");

		if ( !createLatch.await(1, TimeUnit.MINUTES) ) {
			fail("File creation not signalled in 1 minute.");
		}

		Files.write(tmpFile, "Some text content".getBytes(), APPEND);

		if ( !modifyLatch.await(1, TimeUnit.MINUTES) ) {
			fail("File modification not signalled in 1 minute.");
		}

		Files.delete(tmpFile);

		if ( !deleteLatch.await(1, TimeUnit.MINUTES) ) {
			fail("File deletion not signalled in 1 minute.");
		}

		watcher.shutdown();

	}

	/**
	 * Test of watchOrStreamError method, of class DirectoryWatcher.
	 *
	 * @throws java.io.IOException
	 * @throws java.lang.InterruptedException
	 */
	@Test
	public void testWatchOrStreamError() throws IOException, InterruptedException {

		System.out.println(MessageFormat.format("  Testing ''watchOrStreamError'' [on {0}]...", root));

		CountDownLatch errorLatch = new CountDownLatch(1);
		DirectoryWatcher watcher = create(executor);

		watcher.getErrorsStream().subscribe(throwable -> {
			if ( throwable instanceof NotDirectoryException ) {
				errorLatch.countDown();
			}
		});

		watcher.watchOrStreamError(file_a);

		if ( !errorLatch.await(1, TimeUnit.MINUTES) ) {
			fail("File deletion not signalled in 1 minute.");
		}

		watcher.shutdown();

	}

	/**
	 * Test of writeBinaryFile method, of class DirectoryWatcher.
	 */
	@Test
	public void testWriteBinaryFile() {
	}

	/**
	 * Test of writeTextFile method, of class DirectoryWatcher.
	 */
	@Test
	public void testWriteTextFile() {
	}

}
