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
package se.europeanspallationsource.xaos.core.util.io;


import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.Subject;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.text.MessageFormat;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static se.europeanspallationsource.xaos.core.util.io.DirectoryWatcher.build;


/**
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( { "ClassWithoutLogger", "UseOfSystemOutOrSystemErr" } )
public class DirectoryWatcherTest {

	@BeforeClass
	public static void setUpClass() {
		System.out.println("---- DirectoryWatcherTest --------------------------------------");
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
		root = Files.createTempDirectory("DW_");
			dir_a = Files.createTempDirectory(root, "DW_a_");
				file_a = Files.createTempFile(dir_a, "DW_a_", ".test");
				dir_a_c = Files.createTempDirectory(dir_a, "DW_a_c_");
					file_a_c = Files.createTempFile(dir_a_c, "DW_a_c_", ".test");
			dir_b = Files.createTempDirectory(root, "DW_b_");
				file_b1 = Files.createTempFile(dir_b, "DW_b1_", ".test");
				file_b2 = Files.createTempFile(dir_b, "DW_b2_", ".test");
	}

	@After
	public void tearDown() throws IOException {
		Files.walkFileTree(root, new DeleteFileVisitor());
		executor.shutdown();
	}

	/**
	 * Test of close method, of class DirectoryWatcher.
	 *
	 * @throws java.io.IOException
	 */
	@Test(expected = RejectedExecutionException.class)
	public void testClose() throws IOException, RejectedExecutionException {

		System.out.println("  Testing 'close'...");

		DirectoryWatcher watcher = build(executor);

		assertFalse(watcher.isClosed());

		watcher.delete(
			file_b2,
			t -> {
				assertTrue(t);
			},
			e -> {
				fail(MessageFormat.format("File not deleted: {0}", file_b2));
			}
		);

		watcher.close();

		assertTrue(watcher.isClosed());

		watcher.delete(
			file_b1,
			t -> {
				fail("Operation has not been rejected.");
			},
			e -> {
				fail("Operation has not been rejected.");
			}
		);

	}

	/**
	 * Test of build method, of class DirectoryWatcher.
	 *
	 * @throws java.io.IOException
	 */
	@Test
	public void testCreate() throws IOException {

		System.out.println("  Testing 'create'...");

		try ( DirectoryWatcher watcher = build(executor) ) {
			assertNotNull(watcher);
			assertFalse(watcher.isClosed());
		}

	}

	/**
	 * Test of createDirectories method, of class DirectoryWatcher.
	 *
	 * @throws java.io.IOException
	 * @throws java.lang.InterruptedException
	 */
	@Test
	public void testCreateDirectories() throws IOException, InterruptedException {

		System.out.println(MessageFormat.format("  Testing ''createDirectories'' [on {0}]...", root));

		try ( DirectoryWatcher watcher = build(executor) ) {

			CountDownLatch latch = new CountDownLatch(2);
			Path toBeCreated = FileSystems.getDefault().getPath(dir_a.toString(), "dir_a_x", "dir_a_y", "dir_a_z");

			watcher.createDirectories(
				toBeCreated,
				p -> {
					assertNotNull(p);
					assertTrue(Files.exists(p));
					assertTrue(Files.isDirectory(p));
					assertEquals(toBeCreated, p);
					latch.countDown();
				},
				e -> {
					fail(MessageFormat.format("Directory not created: {0}", toBeCreated));
					latch.countDown();
				}
			);

			Path toFail = file_a;

			watcher.createDirectories(
				toFail,
				p -> {
					fail(MessageFormat.format("Directory was created: {0}", toFail));
					latch.countDown();
				},
				e -> {
					assertNotNull(e);
					assertTrue(e instanceof FileAlreadyExistsException);
					latch.countDown();
				}
			);

			if ( !latch.await(1, TimeUnit.MINUTES) ) {
				fail("Directories creation not completed in 1 minute.");
			}

		}

	}

	/**
	 * Test of createDirectory method, of class DirectoryWatcher.
	 *
	 * @throws java.io.IOException
	 * @throws java.lang.InterruptedException
	 */
	@Test
	public void testCreateDirectory() throws IOException, InterruptedException {

		System.out.println(MessageFormat.format("  Testing ''createDirectory'' [on {0}]...", root));

		try ( DirectoryWatcher watcher = build(executor) ) {

			CountDownLatch latch = new CountDownLatch(3);
			Path toBeCreated = FileSystems.getDefault().getPath(dir_a.toString(), "dir_a_z");

			watcher.createDirectory(
				toBeCreated,
				p -> {
					assertNotNull(p);
					assertTrue(Files.exists(p));
					assertTrue(Files.isDirectory(p));
					assertEquals(toBeCreated, p);
					latch.countDown();
				},
				e -> {
					fail(MessageFormat.format("Directory not created: {0}", toBeCreated));
					latch.countDown();
				}
			);

			Path toFail1 = dir_a_c;

			watcher.createDirectory(
				toFail1,
				p -> {
					fail(MessageFormat.format("Directory was created: {0}", toFail1));
					latch.countDown();
				},
				e -> {
					assertNotNull(e);
					assertTrue(e instanceof FileAlreadyExistsException);
					latch.countDown();
				}
			);

			Path toFail2 = FileSystems.getDefault().getPath(dir_a.toString(), "dir_a_x", "dir_a_y", "dir_a_z");

			watcher.createDirectory(
				toFail2,
				p -> {
					fail(MessageFormat.format("Directory was created: {0}", toFail2));
					latch.countDown();
				},
				e -> {
					assertNotNull(e);
					assertTrue(e instanceof IOException);
					latch.countDown();
				}
			);

			if ( !latch.await(1, TimeUnit.MINUTES) ) {
				fail("Directory creation not completed in 1 minute.");
			}

		}

	}

	/**
	 * Test of createFile method, of class DirectoryWatcher.
	 *
	 * @throws java.io.IOException
	 * @throws java.lang.InterruptedException
	 */
	@Test
	public void testCreateFile() throws IOException, InterruptedException {

		System.out.println(MessageFormat.format("  Testing ''createFile'' [on {0}]...", root));

		try ( DirectoryWatcher watcher = build(executor) ) {

			CountDownLatch latch = new CountDownLatch(2);
			Path toBeCreated = FileSystems.getDefault().getPath(dir_a.toString(), "created_file.txt");

			watcher.createFile(
				toBeCreated,
				t -> {
					assertNotNull(t);
					latch.countDown();
				},
				e -> {
					fail(MessageFormat.format("File not created: {0}", toBeCreated));
					latch.countDown();
				}
			);

			Path toFail = FileSystems.getDefault().getPath(dir_a.toString(), "non-exitent", "created_file.txt");

			watcher.createFile(
				toFail,
				t -> {
					fail(MessageFormat.format("File was created: {0}", toFail));
					latch.countDown();
				},
				e -> {
					assertNotNull(e);
					assertTrue(e instanceof IOException);
					latch.countDown();
				}
			);

			if ( !latch.await(1, TimeUnit.MINUTES) ) {
				fail("File creation not completed in 1 minute.");
			}

		}

	}

	/**
	 * Test of delete method, of class DirectoryWatcher.
	 *
	 * @throws java.io.IOException
	 * @throws java.lang.InterruptedException
	 */
	@Test
	public void testDelete() throws IOException, InterruptedException {

		System.out.println(MessageFormat.format("  Testing ''delete'' [on {0}]...", root));

		try ( DirectoryWatcher watcher = build(executor) ) {

			CountDownLatch latch = new CountDownLatch(5);

			watcher.delete(
				file_b2,
				t -> {
					assertTrue(t);
					latch.countDown();
				},
				e -> {
					fail(MessageFormat.format("File not deleted: {0}", file_b2));
					latch.countDown();
				}
			);
			watcher.delete(
				file_b2,
				t -> {
					assertFalse(t);
					latch.countDown();
				},
				e -> {
					fail(MessageFormat.format("File not deleted: {0}", file_b2));
					latch.countDown();
				}
			);
			watcher.delete(
				dir_b,
				t -> {
					fail(MessageFormat.format("Non-empty directory was deleted: {0}", file_b2));
					latch.countDown();
				},
				e -> {
					assertNotNull(e);
					assertTrue(e instanceof IOException);
					latch.countDown();
				}
			);
			watcher.delete(
				file_b1,
				t -> {
					assertTrue(t);
					latch.countDown();
				},
				e -> {
					fail(MessageFormat.format("File not deleted: {0}", file_b2));
					latch.countDown();
				}
			);
			watcher.delete(
				dir_b,
				t -> {
					assertTrue(t);
					latch.countDown();
				},
				e -> {
					fail(MessageFormat.format("Directory not deleted: {0}", file_b2));
					latch.countDown();
				}
			);

			if ( !latch.await(1, TimeUnit.MINUTES) ) {
				fail("File deletion not completed in 1 minute.");
			}

			assertFalse(Files.exists(dir_b));
			assertFalse(Files.exists(file_b1));
			assertFalse(Files.exists(file_b2));

		}

	}

	/**
	 * Test of deleteTree method, of class DirectoryWatcher.
	 *
	 * @throws java.io.IOException
	 * @throws java.lang.InterruptedException
	 */
	@Test
	public void testDeleteTree() throws IOException, InterruptedException {

		System.out.println(MessageFormat.format("  Testing ''deleteTree'' [on {0}]...", root));

		try ( DirectoryWatcher watcher = build(executor) ) {

			CountDownLatch latch = new CountDownLatch(2);

			watcher.deleteTree(
				dir_a,
				t -> {
					latch.countDown();
				},
				e -> {
					fail(MessageFormat.format("Tree not deleted: {0}", file_b2));
					latch.countDown();
				}
			);
			watcher.deleteTree(
				Paths.get("a", "b", "c", "d", "e", "1", "2", "3", "4", "5"),
				t -> {
					latch.countDown();
				},
				e -> {
					fail(MessageFormat.format("Tree not deleted: {0}", file_b2));
					latch.countDown();
				}
			);

			if ( !latch.await(1, TimeUnit.MINUTES) ) {
				fail("File deletion not completed in 1 minute.");
			}

			assertFalse(Files.exists(file_a_c));
			assertFalse(Files.exists(dir_a_c));
			assertFalse(Files.exists(file_a));
			assertFalse(Files.exists(dir_a));

		}

	}

	/**
	 * Test of errors method, of class DirectoryWatcher.
	 *
	 * @throws java.io.IOException
	 */
	@Test
	public void testErrors() throws IOException {

		System.out.println("  Testing 'errors'...");

		try ( DirectoryWatcher watcher = build(executor) ) {

			Subject<Throwable> errorsStream = watcher.errors();

			assertNotNull(errorsStream);

		}

	}

	/**
	 * Test of events method, of class DirectoryWatcher.
	 *
	 * @throws java.io.IOException
	 */
	@Test
	public void testEvents() throws IOException {

		System.out.println("  Testing 'events'...");

		try ( DirectoryWatcher watcher = build(executor) ) {

			Subject<DirectoryWatcher.DirectoryEvent> event = watcher.events();

			assertNotNull(event);

		}

	}

	/**
	 * Test of isClosed method, of class DirectoryWatcher.
	 *
	 * @throws java.io.IOException
	 */
	@Test
	public void testIsClosed() throws IOException {

		System.out.println("  Testing 'isClosed'...");

		DirectoryWatcher watcher = build(executor);

		assertFalse(watcher.isClosed());

		watcher.close();

		assertTrue(watcher.isClosed());

	}

	/**
	 * Test of isCloseComplete method, of class DirectoryWatcher.
	 *
	 * @throws java.io.IOException
	 */
	@Test
	public void testIsCloseComplete() throws IOException {

		System.out.println("  Testing 'isCloseComplete'...");

		DirectoryWatcher watcher = build(executor);

		assertFalse(watcher.isClosed());
		assertFalse(watcher.isCloseComplete());

		watcher.close();

		assertFalse(watcher.isCloseComplete());
		assertTrue(watcher.isClosed());

		long startTime = System.currentTimeMillis();
		long currentTime = startTime;

		while ( !watcher.isCloseComplete() && startTime + 60000L > currentTime ) {
			currentTime = System.currentTimeMillis();
		}

		assertTrue(watcher.isCloseComplete());

	}

	/**
	 * Test of isWatched method, of class DirectoryWatcher.
	 *
	 * @throws java.io.IOException
	 * @throws java.lang.InterruptedException
	 */
	@Test
	@SuppressWarnings( "CallToThreadYield" )
	public void testIsWatched() throws IOException, InterruptedException {

		System.out.println(MessageFormat.format("  Testing ''isWatched'' [on {0}]...", root));

		DirectoryWatcher watcher = build(executor);

		watcher.watch(dir_a);
		assertTrue(watcher.isWatched(dir_a));

		watcher.watch(dir_a_c);
		assertTrue(watcher.isWatched(dir_a_c));

		watcher.watch(dir_b);
		assertTrue(watcher.isWatched(dir_b));

		CountDownLatch latch = new CountDownLatch(1);

		Disposable subscription = watcher.events().subscribe(event -> {
			event.getEvents().stream().forEach(e -> {
				if ( StandardWatchEventKinds.ENTRY_DELETE.equals(e.kind()) ) {
					System.out.println("    Path deleted: " + e.context());
					if ( dir_a_c.equals(event.getWatchedPath().resolve((Path) e.context())) ) {
						latch.countDown();
					}
				}
			});
		});

		Files.walkFileTree(dir_a_c, new DeleteFileVisitor());

		if ( !latch.await(1, TimeUnit.MINUTES) ) {
			fail("File deletion not signalled in 1 minute.");
		}

		assertFalse(watcher.isWatched(dir_a_c));

		watcher.delete(
			dir_a,
			success -> assertFalse(watcher.isWatched(dir_a)),
			null
		);

		subscription.dispose();
		watcher.close();

		while ( !watcher.isCloseComplete() ) {
			Thread.yield();
		}

		assertFalse(watcher.isWatched(dir_b));

	}

//	/**
//	 * Test of readBinaryFile method, of class DirectoryWatcher.
//	 *
//	 * @throws java.io.IOException
//	 * @throws java.lang.InterruptedException
//	 */
//	@Test
//	public void testReadBinaryFile() throws IOException, InterruptedException {
//
//		System.out.println(MessageFormat.format("  Testing ''readBinaryFile'' [on {0}]...", root));
//
//		byte[] content = { 0x00, 0x01, 0x02, 0x03, 0x04, 0x03, 0x02, 0x01, 0x00 };
//		CountDownLatch latch = new CountDownLatch(2);
//		DirectoryWatcher watcher = build(executor);
//
//		Files.write(file_b1, content);
//
//		watcher.readBinaryFile(
//			file_b1,
//			t -> {
//				assertArrayEquals(content, t);
//				latch.countDown();
//			},
//			e -> {
//				fail(MessageFormat.format("File not read: {0}", file_b1));
//				latch.countDown();
//			}
//		);
//
//		Path toFail = FileSystems.getDefault().getPath(dir_a.toString(), "non-exitent", "created_file.txt");
//
//		watcher.readBinaryFile(
//			toFail,
//			t -> {
//				fail(MessageFormat.format("File was read: {0}", toFail));
//				latch.countDown();
//			},
//			e -> {
//				assertNotNull(e);
//				assertTrue(e instanceof IOException);
//				latch.countDown();
//			}
//		);
//
//		if ( !latch.await(1, TimeUnit.MINUTES) ) {
//			fail("File creation not completed in 1 minute.");
//		}
//
//		watcher.shutdown();
//
//	}
//
//	/**
//	 * Test of readTextFile method, of class DirectoryWatcher.
//	 *
//	 * @throws java.io.IOException
//	 * @throws java.lang.InterruptedException
//	 */
//	@Test
//	public void testReadTextFile() throws IOException, InterruptedException {
//
//		System.out.println(MessageFormat.format("  Testing ''readTextFile'' [on {0}]...", root));
//
//		String content = "First line of text.\nSecond line of text.";
//		Charset charset = defaultCharset();
//		CountDownLatch latch = new CountDownLatch(2);
//		DirectoryWatcher watcher = build(executor);
//
//		Files.write(file_b1, content.getBytes(charset), CREATE, WRITE, TRUNCATE_EXISTING);
//
//		watcher.readTextFile(
//			file_b1,
//			charset,
//			t -> {
//				assertEquals(content, t);
//				latch.countDown();
//			},
//			e -> {
//				fail(MessageFormat.format("File not read: {0}", file_b1));
//				latch.countDown();
//			}
//		);
//
//		Path toFail = FileSystems.getDefault().getPath(dir_a.toString(), "non-exitent", "created_file.txt");
//
//		watcher.readTextFile(
//			toFail,
//			charset,
//			t -> {
//				fail(MessageFormat.format("File was read: {0}", toFail));
//				latch.countDown();
//			},
//			e -> {
//				assertNotNull(e);
//				assertTrue(e instanceof IOException);
//				latch.countDown();
//			}
//		);
//
//		if ( !latch.await(1, TimeUnit.MINUTES) ) {
//			fail("File creation not completed in 1 minute.");
//		}
//
//		watcher.shutdown();
//
//	}
//
//	/**
//	 * Test of writeBinaryFile and readBinaryFile method, of class DirectoryWatcher.
//	 *
//	 * @throws java.io.IOException
//	 * @throws java.lang.InterruptedException
//	 */
//	@Test
//	public void testReadWriteBinaryFile() throws IOException, InterruptedException {
//
//		System.out.println(MessageFormat.format("  Testing ''writeBinaryFile'' and ''readBinaryFile'' [on {0}]...", root));
//
//		byte[] content = { 0x00, 0x01, 0x02, 0x03, 0x04, 0x03, 0x02, 0x01, 0x00 };
//		CountDownLatch latch = new CountDownLatch(2);
//		DirectoryWatcher watcher = build(executor);
//		Path readWriteFile = FileSystems.getDefault().getPath(dir_a.toString(), "created_file.txt");
//
//		watcher.writeBinaryFile(
//			readWriteFile,
//			content,
//			t -> {
//				assertNotNull(t);
//				latch.countDown();
//			},
//			e -> {
//				fail(MessageFormat.format("File not written: {0}", readWriteFile));
//				latch.countDown();
//			}
//		);
//		watcher.readBinaryFile(
//			readWriteFile,
//			t -> {
//				assertArrayEquals(content, t);
//				latch.countDown();
//			},
//			e -> {
//				fail(MessageFormat.format("File not read: {0}", file_b1));
//				latch.countDown();
//			}
//		);
//
//		if ( !latch.await(1, TimeUnit.MINUTES) ) {
//			fail("File creation not completed in 1 minute.");
//		}
//
//		watcher.shutdown();
//
//	}
//
//	/**
//	 * Test of writeTextFile and readTextFile methods, of class DirectoryWatcher.
//	 *
//	 * @throws java.io.IOException
//	 * @throws java.lang.InterruptedException
//	 */
//	@Test
//	public void testReadWriteTextFile() throws IOException, InterruptedException {
//
//		System.out.println(MessageFormat.format("  Testing ''writeTextFile'' and ''readTextFile'' [on {0}]...", root));
//
//		String content = "First line of text.\nSecond line of text.";
//		Charset charset = defaultCharset();
//		CountDownLatch latch = new CountDownLatch(2);
//		DirectoryWatcher watcher = build(executor);
//		Path readWriteFile = FileSystems.getDefault().getPath(dir_a.toString(), "created_file.txt");
//
//		watcher.writeTextFile(
//			readWriteFile,
//			content,
//			charset,
//			t -> {
//				assertNotNull(t);
//				latch.countDown();
//			},
//			e -> {
//				fail(MessageFormat.format("File not written: {0}", readWriteFile));
//				latch.countDown();
//			}
//		);
//		watcher.readTextFile(
//			readWriteFile,
//			charset,
//			t -> {
//				assertEquals(content, t);
//				latch.countDown();
//			},
//			e -> {
//				fail(MessageFormat.format("File not read: {0}", file_b1));
//				latch.countDown();
//			}
//		);
//
//		if ( !latch.await(1, TimeUnit.MINUTES) ) {
//			fail("File creation not completed in 1 minute.");
//		}
//
//		watcher.shutdown();
//
//	}
//
//	/**
//	 * Test of unwatch method, of class DirectoryWatcher.
//	 *
//	 * @throws java.io.IOException
//	 */
//	@Test
//	public void testUnwatch() throws IOException {
//
//		System.out.println(MessageFormat.format("  Testing ''unwatch'' [on {0}]...", root));
//
//		DirectoryWatcher watcher = build(executor);
//
//		watcher.watch(dir_a);
//		assertTrue(watcher.isWatched(dir_a));
//
//		watcher.watch(dir_a_c);
//		assertTrue(watcher.isWatched(dir_a_c));
//
//		watcher.watch(dir_b);
//		assertTrue(watcher.isWatched(dir_b));
//
//		watcher.unwatch(dir_a);
//		assertFalse(watcher.isWatched(dir_a));
//
//		watcher.unwatch(dir_a_c);
//		assertFalse(watcher.isWatched(dir_a_c));
//
//		watcher.unwatch(dir_b);
//		assertFalse(watcher.isWatched(dir_b));
//
//		watcher.shutdown();
//
//	}
//
//	/**
//	 * Test of watch method, of class DirectoryWatcher.
//	 *
//	 * @throws java.io.IOException
//	 * @throws java.lang.InterruptedException
//	 */
//	@Test
//	public void testWatch() throws IOException, InterruptedException {
//
//		System.out.println(MessageFormat.format("  Testing ''watch'' [on {0}]...", root));
//
//		CountDownLatch createLatch = new CountDownLatch(1);
//		CountDownLatch deleteLatch = new CountDownLatch(1);
//		CountDownLatch modifyLatch = new CountDownLatch(1);
//		DirectoryWatcher watcher = build(executor);
//
//		watcher.events().subscribe(event -> {
//			event.getEvents().stream().forEach(e -> {
//				if ( StandardWatchEventKinds.ENTRY_CREATE.equals(e.kind()) ) {
//					System.out.println("    File created: " + e.context());
//					createLatch.countDown();
//				} else if ( StandardWatchEventKinds.ENTRY_DELETE.equals(e.kind()) ) {
//					System.out.println("    File deleted: " + e.context());
//					deleteLatch.countDown();
//				} else if ( StandardWatchEventKinds.ENTRY_MODIFY.equals(e.kind()) ) {
//					System.out.println("    File modified: " + e.context());
//					modifyLatch.countDown();
//				}
//			});
//		});
//
//		watcher.watch(root);
//
//		Path tmpFile = Files.createTempFile(root, "DW_", ".test");
//
//		if ( !createLatch.await(1, TimeUnit.MINUTES) ) {
//			fail("File creation not signalled in 1 minute.");
//		}
//
//		Files.write(tmpFile, "Some text content".getBytes(), APPEND);
//
//		if ( !modifyLatch.await(1, TimeUnit.MINUTES) ) {
//			fail("File modification not signalled in 1 minute.");
//		}
//
//		Files.delete(tmpFile);
//
//		if ( !deleteLatch.await(1, TimeUnit.MINUTES) ) {
//			fail("File deletion not signalled in 1 minute.");
//		}
//
//		watcher.shutdown();
//
//	}
//
//	/**
//	 * Test of watchOrStreamError method, of class DirectoryWatcher.
//	 *
//	 * @throws java.io.IOException
//	 * @throws java.lang.InterruptedException
//	 */
//	@Test
//	public void testWatchOrStreamError() throws IOException, InterruptedException {
//
//		System.out.println(MessageFormat.format("  Testing ''watchOrStreamError'' [on {0}]...", root));
//
//		CountDownLatch errorLatch = new CountDownLatch(1);
//		DirectoryWatcher watcher = build(executor);
//
//		watcher.errors().subscribe(throwable -> {
//			if ( throwable instanceof NotDirectoryException ) {
//				errorLatch.countDown();
//			}
//		});
//
//		watcher.watchOrStreamError(file_a);
//
//		if ( !errorLatch.await(1, TimeUnit.MINUTES) ) {
//			fail("File deletion not signalled in 1 minute.");
//		}
//
//		watcher.shutdown();
//
//	}
//
//	/**
//	 * Test of watchUp and unwatchUp methods, of class DirectoryWatcher.
//	 *
//	 * @throws java.io.IOException
//	 */
//	@Test
//	public void testWatchUpAndUnwatchUp() throws IOException {
//
//		System.out.println(MessageFormat.format("  Testing ''watchUp'' and ''unwatchUp'' [on {0}]...", root));
//
//		DirectoryWatcher watcher = build(executor);
//
//		watcher.watchUp(dir_a_c, root);
//		assertTrue(watcher.isWatched(dir_a));
//		assertTrue(watcher.isWatched(dir_a_c));
//		assertFalse(watcher.isWatched(root));
//
//		watcher.watch(root);
//		assertTrue(watcher.isWatched(root));
//
//		watcher.unwatchUp(dir_a_c, root);
//		assertFalse(watcher.isWatched(dir_a));
//		assertFalse(watcher.isWatched(dir_a_c));
//		assertTrue(watcher.isWatched(root));
//
//		watcher.shutdown();
//
//	}
//
//	/**
//	 * Test of writeBinaryFile method, of class DirectoryWatcher.
//	 *
//	 * @throws java.io.IOException
//	 * @throws java.lang.InterruptedException
//	 */
//	@Test
//	public void testWriteBinaryFile() throws IOException, InterruptedException {
//
//		System.out.println(MessageFormat.format("  Testing ''writeBinaryFile'' [on {0}]...", root));
//
//		byte[] content = { 0x00, 0x01, 0x02, 0x03, 0x04, 0x03, 0x02, 0x01, 0x00 };
//		CountDownLatch latch = new CountDownLatch(2);
//		DirectoryWatcher watcher = build(executor);
//		Path toBeCreated = FileSystems.getDefault().getPath(dir_a.toString(), "created_file.txt");
//
//		watcher.writeBinaryFile(
//			toBeCreated,
//			content,
//			t -> {
//				assertNotNull(t);
//				latch.countDown();
//			},
//			e -> {
//				fail(MessageFormat.format("File not written: {0}", toBeCreated));
//				latch.countDown();
//			}
//		);
//
//		Path toFail = FileSystems.getDefault().getPath(dir_a.toString(), "non-exitent", "created_file.txt");
//
//		watcher.writeBinaryFile(
//			toFail,
//			content,
//			t -> {
//				fail(MessageFormat.format("File was written: {0}", toFail));
//				latch.countDown();
//			},
//			e -> {
//				assertNotNull(e);
//				assertTrue(e instanceof IOException);
//				latch.countDown();
//			}
//		);
//
//		if ( !latch.await(1, TimeUnit.MINUTES) ) {
//			fail("File creation not completed in 1 minute.");
//		}
//
//		assertArrayEquals(content, Files.readAllBytes(toBeCreated));
//
//		watcher.shutdown();
//
//	}
//
//	/**
//	 * Test of writeTextFile method, of class DirectoryWatcher.
//	 *
//	 * @throws java.io.IOException
//	 * @throws java.lang.InterruptedException
//	 */
//	@Test
//	public void testWriteTextFile() throws IOException, InterruptedException {
//
//		System.out.println(MessageFormat.format("  Testing ''writeTextFile'' [on {0}]...", root));
//
//		String content = "First line of text.\nSecond line of text.";
//		Charset charset = defaultCharset();
//		CountDownLatch latch = new CountDownLatch(2);
//		DirectoryWatcher watcher = build(executor);
//		Path toBeCreated = FileSystems.getDefault().getPath(dir_a.toString(), "created_file.txt");
//
//		watcher.writeTextFile(
//			toBeCreated,
//			content,
//			charset,
//			t -> {
//				assertNotNull(t);
//				latch.countDown();
//			},
//			e -> {
//				fail(MessageFormat.format("File not written: {0}", toBeCreated));
//				latch.countDown();
//			}
//		);
//
//		Path toFail = FileSystems.getDefault().getPath(dir_a.toString(), "non-exitent", "created_file.txt");
//
//		watcher.writeTextFile(
//			toFail,
//			content,
//			charset,
//			t -> {
//				fail(MessageFormat.format("File was written: {0}", toFail));
//				latch.countDown();
//			},
//			e -> {
//				assertNotNull(e);
//				assertTrue(e instanceof IOException);
//				latch.countDown();
//			}
//		);
//
//		if ( !latch.await(1, TimeUnit.MINUTES) ) {
//			fail("File creation not completed in 1 minute.");
//		}
//
//		assertEquals(content, new String(Files.readAllBytes(toBeCreated), charset));
//
//		watcher.shutdown();
//
//	}

}
