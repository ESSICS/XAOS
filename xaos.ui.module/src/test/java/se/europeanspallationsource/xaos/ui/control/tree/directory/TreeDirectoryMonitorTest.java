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
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import se.europeanspallationsource.xaos.core.util.io.DeleteFileVisitor;
import se.europeanspallationsource.xaos.ui.control.tree.TreeItems;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static se.europeanspallationsource.xaos.ui.control.tree.directory.TreeDirectoryMonitorTest.ChangeSource.EXTERNAL;
import static se.europeanspallationsource.xaos.ui.control.tree.directory.TreeDirectoryMonitorTest.ChangeSource.INTERNAL;


/**
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( { "ClassWithoutLogger", "UseOfSystemOutOrSystemErr" } )
public class TreeDirectoryMonitorTest extends ApplicationTest {

	@BeforeClass
	public static void setUpClass() {
		System.out.println("---- TreeDirectoryMonitorTest ----------------------------------");
	}

	private Path dir_a;
	private Path dir_a_c;
	private Path dir_b;
	private ExecutorService executor;
	private Path file_a;
	private Path file_a_c;
	private Path file_b1;
	private Path file_b2;
	private TreeDirectoryMonitor<ChangeSource, Path> monitor;
	private Path root;
	private TreeItem<Path> rootItem;
	private TreeView<Path> view;

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
	}

	@Override
	public void start( Stage stage ) throws IOException {

		monitor = TreeDirectoryMonitor.build(EXTERNAL);
		rootItem = monitor.model().getRoot();

		rootItem.setExpanded(true);

		view = new TreeView<>(rootItem);

		view.setId("tree");
		view.setShowRoot(false);
		view.setCellFactory(TreeItems.defaultTreePathCellFactory());

		stage.setOnCloseRequest(event -> monitor.dispose());
		stage.setScene(new Scene(view, 800, 500));
		stage.show();

	}

	@After
	public void tearDown() throws TimeoutException, IOException, InterruptedException {
		FxToolkit.cleanupStages();
		Files.walkFileTree(root, new DeleteFileVisitor());
		executor.shutdown();
	}

	/**
	 * Test of addTopLevelDirectory method, of class TreeDirectoryMonitor.
	 *
	 * @throws java.lang.InterruptedException
	 */
	@Test
	public void testAddTopLevelDirectory() throws InterruptedException {

		System.out.println(MessageFormat.format("  Testing ''addTopLevelDirectory'' [on {0}]...", root));

		CountDownLatch latch = new CountDownLatch(1);
		EventHandler<TreeItem.TreeModificationEvent<Path>> eventHandler = event -> {
			latch.countDown();
		};

		rootItem.addEventHandler(TreeItem.childrenModificationEvent(), eventHandler);
		executor.execute(() -> monitor.addTopLevelDirectory(root));

		if ( !latch.await(1, TimeUnit.MINUTES) ) {
			fail("Root node addition not completed in 1 minute.");
		}

	}

	/**
	 * Test of creating directories.
	 *
	 * @throws java.lang.InterruptedException
	 * @throws java.io.IOException
	 */
	@Test
	public void testCreateDirectories() throws InterruptedException, IOException {

		System.out.println(MessageFormat.format("  Testing directories creation [on {0}]...", root));

		Queue<ChangeSource> sources = new ConcurrentLinkedDeque<>();
		Path toBeInternallyCreated1 = FileSystems.getDefault().getPath(dir_a.toString(), "dir_a_y");
		Path toBeInternallyCreated2 = FileSystems.getDefault().getPath(toBeInternallyCreated1.toString(), "dir_a_z");
		CountDownLatch latchInternal = new CountDownLatch(2);
		Path toBeExternallyCreated1 = FileSystems.getDefault().getPath(dir_a.toString(), "dir_a_w");
		Path toBeExternallyCreated2 = FileSystems.getDefault().getPath(toBeExternallyCreated1.toString(), "dir_a_x");
		CountDownLatch latchExternal = new CountDownLatch(2);
		EventHandler<TreeItem.TreeModificationEvent<Path>> eventHandler = event -> {
			Platform.runLater(() -> {
				if ( event.wasAdded() ) {

					if ( toBeInternallyCreated1.equals(event.getAddedChildren().get(0).getValue())
					  || toBeInternallyCreated2.equals(event.getAddedChildren().get(0).getValue()) ) {
						latchInternal.countDown();
					}

					if ( toBeExternallyCreated1.equals(event.getAddedChildren().get(0).getValue())
					  || toBeExternallyCreated2.equals(event.getAddedChildren().get(0).getValue()) ) {
						latchExternal.countDown();
					}

					event.getAddedChildren().forEach(i -> i.setExpanded(true));

				}
			});
		};

		rootItem.addEventHandler(TreeItem.childrenModificationEvent(), eventHandler);
		monitor.addTopLevelDirectory(root);
		monitor.model().creations().subscribe(u -> {
			if ( toBeInternallyCreated1.equals(u.getPath())
			  || toBeInternallyCreated2.equals(u.getPath())
			  || toBeExternallyCreated1.equals(u.getPath())
			  || toBeExternallyCreated2.equals(u.getPath()) ) {
				sources.offer(u.getInitiator());
			}
		});

		expandTreeAndWait(monitor.model().getRoot());

		//	INTERNAL creation.
		executor.execute(() -> {
			try {
				monitor.io().createDirectories(toBeInternallyCreated2, INTERNAL).toCompletableFuture().get();
			} catch ( InterruptedException | ExecutionException ex ) {
				fail(ex.getMessage());
			}
		});

		if ( !latchInternal.await(1, TimeUnit.MINUTES) ) {
			fail("Directory creation not completed in 1 minute.");
		}

		assertTrue(monitor.model().contains(toBeInternallyCreated1));
		assertTrue(monitor.model().contains(toBeInternallyCreated2));
		assertThat(view.getTreeItem(2).getValue()).isEqualTo(toBeInternallyCreated1);
		assertThat(view.getTreeItem(3).getValue()).isEqualTo(toBeInternallyCreated2);
		assertThat(sources.stream().filter(cs -> INTERNAL.equals(cs)).count()).isEqualTo(2);
		assertThat(sources.stream().filter(cs -> EXTERNAL.equals(cs)).count()).isEqualTo(0);

		//	EXTERNAL creation.
		sources.clear();
		Files.createDirectories(toBeExternallyCreated2);

		if ( !latchExternal.await(1, TimeUnit.MINUTES) ) {
			fail("Directory creation not completed in 1 minute.");
		}

		assertTrue(monitor.model().contains(toBeExternallyCreated1));
		assertTrue(monitor.model().contains(toBeExternallyCreated2));
		assertThat(view.getTreeItem(2).getValue()).isEqualTo(toBeExternallyCreated1);
		assertThat(view.getTreeItem(3).getValue()).isEqualTo(toBeExternallyCreated2);
		assertThat(sources.stream().filter(cs -> INTERNAL.equals(cs)).count()).isEqualTo(0);
		assertThat(sources.stream().filter(cs -> EXTERNAL.equals(cs)).count()).isEqualTo(2);

	}

	/**
	 * Test of creating a directory.
	 *
	 * @throws java.lang.InterruptedException
	 * @throws java.io.IOException
	 */
	@Test
	public void testCreateDirectory() throws InterruptedException, IOException {

		System.out.println(MessageFormat.format("  Testing directory creation [on {0}]...", root));

		Queue<ChangeSource> sources = new ConcurrentLinkedDeque<>();
		Path toBeInternallyCreated = FileSystems.getDefault().getPath(dir_a.toString(), "dir_a_z");
		CountDownLatch latchInternal = new CountDownLatch(1);
		Path toBeExternallyCreated = FileSystems.getDefault().getPath(dir_a.toString(), "dir_a_y");
		CountDownLatch latchExternal = new CountDownLatch(1);
		EventHandler<TreeItem.TreeModificationEvent<Path>> eventHandler = event -> {
			Platform.runLater(() -> {
				if ( event.wasAdded() ) {

					if ( toBeInternallyCreated.equals(event.getAddedChildren().get(0).getValue()) ) {
						latchInternal.countDown();
					}

					if ( toBeExternallyCreated.equals(event.getAddedChildren().get(0).getValue()) ) {
						latchExternal.countDown();
					}

					event.getAddedChildren().forEach(i -> i.setExpanded(true));

				}
			});
		};

		rootItem.addEventHandler(TreeItem.childrenModificationEvent(), eventHandler);
		monitor.addTopLevelDirectory(root);
		monitor.model().creations().subscribe(u -> {
			if ( toBeInternallyCreated.equals(u.getPath())
			  || toBeExternallyCreated.equals(u.getPath()) ) {
				sources.offer(u.getInitiator());
			}
		});

		expandTreeAndWait(monitor.model().getRoot());

		//	INTERNAL creation.
		executor.execute(() -> {
			try {
				monitor.io().createDirectory(toBeInternallyCreated, INTERNAL).toCompletableFuture().get();
			} catch ( InterruptedException | ExecutionException ex ) {
				fail(ex.getMessage());
			}
		});

		if ( !latchInternal.await(1, TimeUnit.MINUTES) ) {
			fail("Directory creation not completed in 1 minute.");
		}

		assertTrue(monitor.model().contains(toBeInternallyCreated));
		assertThat(view.getTreeItem(2).getValue()).isEqualTo(toBeInternallyCreated);
		assertThat(sources.stream().filter(cs -> INTERNAL.equals(cs)).count()).isEqualTo(1);
		assertThat(sources.stream().filter(cs -> EXTERNAL.equals(cs)).count()).isEqualTo(0);

		//	EXTERNAL creation.
		sources.clear();
		Files.createDirectory(toBeExternallyCreated);

		if ( !latchExternal.await(1, TimeUnit.MINUTES) ) {
			fail("Directory creation not completed in 1 minute.");
		}

		assertTrue(monitor.model().contains(toBeExternallyCreated));
		assertThat(view.getTreeItem(2).getValue()).isEqualTo(toBeExternallyCreated);
		assertThat(sources.stream().filter(cs -> INTERNAL.equals(cs)).count()).isEqualTo(0);
		assertThat(sources.stream().filter(cs -> EXTERNAL.equals(cs)).count()).isEqualTo(1);

	}

	/**
	 * Test of creating a file.
	 *
	 * @throws java.lang.InterruptedException
	 * @throws java.io.IOException
	 */
	@Test
	public void testCreateFile() throws InterruptedException, IOException {

		System.out.println(MessageFormat.format("  Testing file creation [on {0}]...", root));

		Queue<ChangeSource> sources = new ConcurrentLinkedDeque<>();
		Path toBeInternallyCreated = FileSystems.getDefault().getPath(dir_a.toString(), "created_file_internal.txt");
		CountDownLatch latchInternal = new CountDownLatch(1);
		Path toBeExternallyCreated = FileSystems.getDefault().getPath(dir_a.toString(), "created_file_external.txt");
		CountDownLatch latchExternal = new CountDownLatch(1);
		EventHandler<TreeItem.TreeModificationEvent<Path>> eventHandler = event -> {
			Platform.runLater(() -> {
				if ( event.wasAdded() ) {

					if ( toBeInternallyCreated.equals(event.getAddedChildren().get(0).getValue()) ) {
						latchInternal.countDown();
					}

					if ( toBeExternallyCreated.equals(event.getAddedChildren().get(0).getValue()) ) {
						latchExternal.countDown();
					}

					event.getAddedChildren().forEach(i -> i.setExpanded(true));

				}
			});
		};

		rootItem.addEventHandler(TreeItem.childrenModificationEvent(), eventHandler);
		monitor.addTopLevelDirectory(root);
		monitor.model().creations().subscribe(u -> {
			if ( toBeInternallyCreated.equals(u.getPath())
			  || toBeExternallyCreated.equals(u.getPath()) ) {
				sources.offer(u.getInitiator());
			}
		});

		expandTreeAndWait(monitor.model().getRoot());

		//	INTERNAL creation.
		executor.execute(() -> {
			try {
				monitor.io().createFile(toBeInternallyCreated, INTERNAL).toCompletableFuture().get();
			} catch ( InterruptedException | ExecutionException ex ) {
				fail(ex.getMessage());
			}
		});

		if ( !latchInternal.await(1, TimeUnit.MINUTES) ) {
			fail("File creation not completed in 1 minute.");
		}

		assertTrue(monitor.model().contains(toBeInternallyCreated));
		assertThat(view.getTreeItem(4).getValue()).isEqualTo(toBeInternallyCreated);
		assertThat(sources.stream().filter(cs -> INTERNAL.equals(cs)).count()).isEqualTo(1);
		assertThat(sources.stream().filter(cs -> EXTERNAL.equals(cs)).count()).isEqualTo(0);

		//	EXTERNAL creation.
		sources.clear();
		Files.createFile(toBeExternallyCreated);

		if ( !latchExternal.await(1, TimeUnit.MINUTES) ) {
			fail("File creation not completed in 1 minute.");
		}

		assertTrue(monitor.model().contains(toBeExternallyCreated));
		assertThat(view.getTreeItem(4).getValue()).isEqualTo(toBeExternallyCreated);
		assertThat(sources.stream().filter(cs -> INTERNAL.equals(cs)).count()).isEqualTo(0);
		assertThat(sources.stream().filter(cs -> EXTERNAL.equals(cs)).count()).isEqualTo(1);

	}

	/**
	 * Test of deleting a file and a directory.
	 *
	 * @throws java.lang.InterruptedException
	 * @throws java.io.IOException
	 */
	@Test
	@SuppressWarnings( "CallToThreadYield" )
	public void testDelete() throws InterruptedException, IOException {

		System.out.println(MessageFormat.format("  Testing file and directory deletion [on {0}]...", root));

		Queue<ChangeSource> sources = new ConcurrentLinkedDeque<>();
		CountDownLatch latchInternal = new CountDownLatch(2);
		CountDownLatch latchExternal = new CountDownLatch(2);
		EventHandler<TreeItem.TreeModificationEvent<Path>> eventHandler = event -> {
			Platform.runLater(() -> {
				if ( event.wasRemoved() ) {

					if ( dir_a_c.equals(event.getRemovedChildren().get(0).getValue())
					  || file_a_c.equals(event.getRemovedChildren().get(0).getValue()) ) {
						latchInternal.countDown();
					}

					if ( dir_a.equals(event.getRemovedChildren().get(0).getValue())
					  || file_a.equals(event.getRemovedChildren().get(0).getValue()) ) {
						latchExternal.countDown();
					}

				}
			});
		};

		rootItem.addEventHandler(TreeItem.childrenModificationEvent(), eventHandler);
		monitor.addTopLevelDirectory(root);
		monitor.model().deletions().subscribe(u -> {
			if ( dir_a_c.equals(u.getPath())
			  || file_a_c.equals(u.getPath())
			  || dir_a.equals(u.getPath())
			  || file_a.equals(u.getPath()) ) {
				sources.offer(u.getInitiator());
			}
		});

		expandTreeAndWait(monitor.model().getRoot());

		//	INTERNAL deletion.
		executor.execute(() -> {
			try {
				monitor.io().delete(file_a_c, INTERNAL).toCompletableFuture().get();
				monitor.io().delete(dir_a_c, INTERNAL).toCompletableFuture().get();
			} catch ( InterruptedException | ExecutionException ex ) {
				fail(ex.getMessage());
			}
		});

		if ( !latchInternal.await(1, TimeUnit.MINUTES) ) {
			fail("Directory and file deletion not completed in 1 minute.");
		}

		assertFalse(monitor.model().contains(file_a_c));
		assertFalse(monitor.model().contains(dir_a_c));
		assertThat(view.getTreeItem(3).getValue()).isEqualTo(dir_b);
		assertThat(sources.stream().filter(cs -> INTERNAL.equals(cs)).count()).isEqualTo(2);
		assertThat(sources.stream().filter(cs -> EXTERNAL.equals(cs)).count()).isEqualTo(0);

		//	EXTERNAL deletion.
		sources.clear();
		Files.delete(file_a);

		while ( latchExternal.getCount() > 1 ) {
			Thread.yield();
		}

		Files.delete(dir_a);

		if ( !latchExternal.await(1, TimeUnit.MINUTES) ) {
			fail("Directory and file deletion not completed in 1 minute.");
		}

		assertFalse(monitor.model().contains(file_a));
		assertFalse(monitor.model().contains(dir_a));
		assertThat(view.getTreeItem(1).getValue()).isEqualTo(dir_b);
		assertThat(sources.stream().filter(cs -> INTERNAL.equals(cs)).count()).isEqualTo(0);
		assertThat(sources.stream().filter(cs -> EXTERNAL.equals(cs)).count()).isEqualTo(2);

	}

	/**
	 * Test of deleting a tree.
	 *
	 * @throws java.lang.InterruptedException
	 * @throws java.io.IOException
	 */
	@Test
	public void testDeleteTree() throws InterruptedException, IOException {

		System.out.println(MessageFormat.format("  Testing tree deletion [on {0}]...", root));

		Queue<ChangeSource> sources = new ConcurrentLinkedDeque<>();
		CountDownLatch latchInternal = new CountDownLatch(1);
		CountDownLatch latchExternal = new CountDownLatch(4);
		EventHandler<TreeItem.TreeModificationEvent<Path>> eventHandler = event -> {
			Platform.runLater(() -> {
				if ( event.wasRemoved() ) {

					if ( dir_b.equals(event.getRemovedChildren().get(0).getValue()) ) {
						latchInternal.countDown();
					}

					if ( dir_a.equals(event.getRemovedChildren().get(0).getValue())
					  || file_a.equals(event.getRemovedChildren().get(0).getValue())
					  || dir_a_c.equals(event.getRemovedChildren().get(0).getValue())
					  || file_a_c.equals(event.getRemovedChildren().get(0).getValue()) ) {
						latchExternal.countDown();
					}

				}
			});
		};

		rootItem.addEventHandler(TreeItem.childrenModificationEvent(), eventHandler);
		monitor.addTopLevelDirectory(root);
		monitor.model().deletions().subscribe(u -> {
			if ( dir_b.equals(u.getPath())
			  || dir_a.equals(u.getPath())
			  || file_a.equals(u.getPath())
			  || dir_a_c.equals(u.getPath())
			  || file_a_c.equals(u.getPath()) ) {
				sources.offer(u.getInitiator());
			}
		});

		expandTreeAndWait(monitor.model().getRoot());

		//	INTERNAL deletion.
		executor.execute(() -> {
			try {
				monitor.io().deleteTree(dir_b, INTERNAL).toCompletableFuture().get();
			} catch ( InterruptedException | ExecutionException ex ) {
				fail(ex.getMessage());
			}
		});

		if ( !latchInternal.await(1, TimeUnit.MINUTES) ) {
			fail("Tree deletion not completed in 1 minute.");
		}

		assertFalse(monitor.model().contains(dir_b));
		assertFalse(monitor.model().contains(file_b1));
		assertFalse(monitor.model().contains(file_b2));
		assertThat(view.getExpandedItemCount()).isEqualTo(5);
		assertThat(sources.stream().filter(cs -> INTERNAL.equals(cs)).count()).isEqualTo(1);
		assertThat(sources.stream().filter(cs -> EXTERNAL.equals(cs)).count()).isEqualTo(0);

		//	EXTERNAL deletion.
		sources.clear();
		deleteRecursively(dir_a, latchExternal);

		if ( !latchExternal.await(3, TimeUnit.MINUTES) ) {
			fail("Tree deletion not completed in 3 minutes.");
		}

		assertFalse(monitor.model().contains(dir_a));
		assertFalse(monitor.model().contains(file_a));
		assertFalse(monitor.model().contains(dir_a_c));
		assertFalse(monitor.model().contains(file_a_c));
		assertThat(view.getExpandedItemCount()).isEqualTo(1);
		assertThat(sources.stream().filter(cs -> INTERNAL.equals(cs)).count()).isEqualTo(0);
		assertThat(sources.stream().filter(cs -> EXTERNAL.equals(cs)).count()).isEqualTo(4);

	}

	/**
	 * Test of navigation capabilities.
	 *
	 * @throws java.lang.InterruptedException
	 */
	@Test
	public void testNavigation() throws InterruptedException {

		System.out.println("  Testing browser navigation...");

		monitor.addTopLevelDirectory(root);

		assertThat(rootItem.getChildren().size()).isEqualTo(1);

		//	---- root ----------------------------------------------------------
		TreeItem<Path> rItem = rootItem.getChildren().get(0);

		assertThat(rItem).hasFieldOrPropertyWithValue("value", root);
		assertThat(rItem.getChildren().size()).isEqualTo(0);

		executor.execute(() -> rItem.setExpanded(true));

		Thread.sleep(3000L);

		assertThat(rItem.getChildren().size()).isEqualTo(2);

		//	---- dir_a ---------------------------------------------------------
		TreeItem<Path> aItem = rItem.getChildren().get(0);

		assertThat(aItem).hasFieldOrPropertyWithValue("value", dir_a);
		assertThat(aItem.getChildren().size()).isEqualTo(0);

		executor.execute(() -> aItem.setExpanded(true));

		Thread.sleep(3000L);

		assertThat(aItem.getChildren().size()).isEqualTo(2);

		//	---- dir_a_c ---------------------------------------------------------
		TreeItem<Path> acItem = aItem.getChildren().get(0);

		assertThat(acItem).hasFieldOrPropertyWithValue("value", dir_a_c);
		assertThat(acItem.getChildren().size()).isEqualTo(0);

		executor.execute(() -> acItem.setExpanded(true));

		Thread.sleep(3000L);

		assertThat(acItem.getChildren().size()).isEqualTo(1);

		//	---- file_a_c --------------------------------------------------------
		TreeItem<Path> acfItem = acItem.getChildren().get(0);

		assertThat(acfItem).hasFieldOrPropertyWithValue("value", file_a_c);

		//	---- file_a --------------------------------------------------------
		TreeItem<Path> afItem = aItem.getChildren().get(1);

		assertThat(afItem).hasFieldOrPropertyWithValue("value", file_a);

		//	---- dir_b ---------------------------------------------------------
		TreeItem<Path> bItem = rItem.getChildren().get(1);

		assertThat(bItem).hasFieldOrPropertyWithValue("value", dir_b);
		assertThat(bItem.getChildren().size()).isEqualTo(0);

		executor.execute(() -> bItem.setExpanded(true));

		Thread.sleep(3000L);

		assertThat(bItem.getChildren().size()).isEqualTo(2);

		//	---- file_b1 -------------------------------------------------------
		TreeItem<Path> b1Item = bItem.getChildren().get(0);

		assertThat(b1Item).hasFieldOrPropertyWithValue("value", file_b1);

		//	---- file_b2 -------------------------------------------------------
		TreeItem<Path> b2Item = bItem.getChildren().get(1);

		assertThat(b2Item).hasFieldOrPropertyWithValue("value", file_b2);

	}

	@SuppressWarnings( "CallToThreadYield" )
	private void deleteRecursively( Path root, CountDownLatch latch ) throws IOException {

		if ( Files.exists(root) ) {

			if ( Files.isDirectory(root) ) {
				try ( DirectoryStream<Path> stream = Files.newDirectoryStream(root) ) {
					for ( Path path : stream ) {
						deleteRecursively(path, latch);
					}
				}
			}

			long count = latch.getCount();

			Files.delete(root);

			while ( latch.getCount() == count ) {
				Thread.yield();
			}

		}

	}

	private <T> void expandTreeAndWait( TreeItem<T> item ) {

		CountDownLatch done = new CountDownLatch(1);

		Platform.runLater(() -> {
			TreeItems.expandAll(item, true);
			done.countDown();
		});

		try {
			if ( !done.await(1, TimeUnit.MINUTES) ) {
				fail("Tree not expanded in 1 minute.");
			}
		} catch ( InterruptedException ex ) {
				fail("Tree not expanded in 1 minute.");
		}

	}

	@SuppressWarnings( "PackageVisibleInnerClass" )
	enum ChangeSource {
		INTERNAL,
		EXTERNAL
	}

}
