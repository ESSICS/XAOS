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


import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileTime;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.reactfx.EventSource;
import org.reactfx.EventStream;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;


/**
 * Watches for changes in files and directories, and allows for standard
 * operations on both files and directories.
 * <p>
 * Usage:</p>
 * <pre>
 *   ExecutorService executor = Executors.newSingleThreadExecutor();
 *   DirectoryWatcher watcher = build(executor);
 *
 *   watcher.events().subscribe(event -&gt; {
 *     event.getEvents().stream().forEach(e -&gt; {
 *       if ( StandardWatchEventKinds.ENTRY_CREATE.equals(e.kind()) ) {
 *         ...
 *       } else if ( StandardWatchEventKinds.ENTRY_DELETE.equals(e.kind()) ) {
 *         ...
 *       } else if ( StandardWatchEventKinds.ENTRY_MODIFY.equals(e.kind()) ) {
 *         ...
 *       }
 *     });
 *   });
 *
 *   Path root = ...
 *
 *   watcher.watch(root);</pre>
 *
 * @author claudio.rosati@esss.se
 * @see <a href="https://github.com/ESSICS/LiveDirsFX">LiveDirsFX:org.fxmisc.livedirs.DirWatcher</a>
 */
public class DirectoryWatcher {

	private static final Logger LOGGER = Logger.getLogger(DirectoryWatcher.class.getName());

	/**
	 * Creates a {@link DirectoryWatcher} instance.
	 *
	 * @param eventThreadExecutor The {@link Executor} used to queue I/O events.
	 * @return A newly created {@link DirectoryWatcher} instance.
	 * @throws IOException If an I/O error occurs.
	 */
	public static DirectoryWatcher build( Executor eventThreadExecutor ) throws IOException {
		return new DirectoryWatcher(eventThreadExecutor);
	}

	private final EventSource<Throwable> errors = new EventSource<>();
	private final Executor eventThreadExecutor;
	private final EventSource<DirectoryEvent> events = new EventSource<>();
	private final LinkedBlockingQueue<Runnable> executorQueue = new LinkedBlockingQueue<>();
	private boolean interrupted = false;
	private final Thread ioThread;
	private boolean mayInterrupt = false;
	private volatile boolean shutdown = false;
	private final WatchService watcher;
	private final Map<WatchKey, WeakReference<Path>> watcherKeys = Collections.synchronizedMap(new WeakHashMap<>());

	protected DirectoryWatcher( Executor eventThreadExecutor ) throws IOException {

		this.watcher = FileSystems.getDefault().newWatchService();
		this.ioThread = new Thread(this::ioLoop, "DirectoryWatcherIO");
		this.eventThreadExecutor = eventThreadExecutor;

		startIOThread();

	}

	/**
	 * Create a new directory by creating all nonexistent parent directories
	 * first. One of the two given {@link Consumer}s will be called on success
	 * or on failure.
	 *
	 * @param dir       The pathname of the file to be created.
	 * @param onSuccess The {@link Consumer} called on success, where the passed
	 *                  parameter is the new directory {@link Path}.
	 * @param onError   The {@link Consumer} called on failure.
	 * @param attrs     An optional list of file attributes to set atomically
	 *                  when creating the directory.
	 */
	public void createDirectories(
		Path dir,
		Consumer<Path> onSuccess,
		Consumer<Throwable> onError,
		FileAttribute<?>... attrs
	) {
		executeIOOperation(() -> Files.createDirectories(dir, attrs), onSuccess, onError);
	}

	/**
	 * Create a new directory using the given path. One of the two given
	 * {@link Consumer}s will be called on success or on failure.
	 * <p>
	 * The {@link #createDirectories createDirectories} method should be used
	 * where it is required to create all nonexistent parent directories first.
	 * </p>
	 *
	 * @param dir       The pathname of the file to be created.
	 * @param onSuccess The {@link Consumer} called on success, where the passed
	 *                  parameter is the new directory {@link Path}.
	 * @param onError   The {@link Consumer} called on failure.
	 * @param attrs     An optional list of file attributes to set atomically
	 *                  when creating the directory.
	 */
	public void createDirectory(
		Path dir,
		Consumer<Path> onSuccess,
		Consumer<Throwable> onError,
		FileAttribute<?>... attrs
	) {
		executeIOOperation(() -> Files.createDirectory(dir, attrs), onSuccess, onError);
	}

	/**
	 * Create a new file using the given path. One of the two given
	 * {@link Consumer}s will be called on success or on failure.
	 * <p>
	 * {@link Files#createFile(java.nio.file.Path, java.nio.file.attribute.FileAttribute...)}
	 * will be called to actually create the file.
	 * </p><p>
	 * <b>Note:</b> the operation is executed by the {@link Executor} passed
	 * to the {@link #build(java.util.concurrent.Executor)} method, i.e. in a
	 * different thread from the caller's one.
	 * </p>
	 *
	 * @param file      The pathname of the file to be created.
	 * @param onSuccess The {@link Consumer} called on success, where the passed
	 *                  parameter is the new file timestamp.
	 * @param onError   The {@link Consumer} called on failure.
	 * @param attrs     An optional list of file attributes to set atomically
	 *                  when creating the file.
	 */
	public void createFile(
		Path file,
		Consumer<FileTime> onSuccess,
		Consumer<Throwable> onError,
		FileAttribute<?>... attrs
	) {
		executeIOOperation(
			() -> {

				Files.createFile(file, attrs);

				return Files.getLastModifiedTime(file);

			},
			onSuccess,
			onError
		);
	}

	/**
	 * Deletes a file or an empty directory. One of the two given
	 * {@link Consumer}s will be called on success or on failure.
	 * <p>
	 * {@link Files#delete(java.nio.file.Path)} will be called to actually
	 * delete the file or empty directory.
	 * </p><p>
	 * <b>Note:</b> the operation is executed by the {@link Executor} passed
	 * to the {@link #build(java.util.concurrent.Executor)} method, i.e. in a
	 * different thread from the caller's one.
	 * </p>
	 *
	 * @param path      Pathname of the file or directory to be deleted.
	 * @param onSuccess The {@link Consumer} called on success, where the passed
	 *                  parameter is {@link Boolean#TRUE} if the file was deleted
	 *                  by this method; {@link Boolean#FALSE} if the file could
	 *                  not be deleted because it did not exist.
	 * @param onError   The {@link Consumer} called on failure.
	 */
	public void delete( Path path, Consumer<Boolean> onSuccess, Consumer<Throwable> onError ) {
		executeIOOperation(
			() -> {

				boolean deleted = Files.deleteIfExists(path);

				if ( deleted ) {
					removeWatcherKey(path);
				}

				return deleted;

			},
			onSuccess,
			onError
		);
	}

	/**
	 * Deletes a file tree rooted at the given path. One of the two given
	 * {@link Consumer}s will be called on success or on failure.
	 *
	 * @param root      The path to the file tree root to be deleted.
	 * @param onSuccess The {@link Consumer} called on success.
	 * @param onError   The {@link Consumer} called on failure.
	 */
	public void deleteTree( Path root, Consumer<Void> onSuccess, Consumer<Throwable> onError ) {
		executeIOOperation(
			() -> {
				deleteRecursively(root);
				return null;
			},
			onSuccess,
			onError
		);
	}

	/**
	 * @return The {@link EventStream} of thrown errors.
	 */
	public EventStream<Throwable> errors() {
		return errors;
	}

	/**
	 * @return The {@link EventStream} of signalled {@link DirectoryEvent}s.
	 */
	public EventStream<DirectoryEvent> events() {
		return events;
	}

	/**
	 * Returns {@code true} if this watcher was shutdown, meaning that no elements
	 * will be posted to the errors and events streams.
	 *
	 * @return {@code true} if this watcher was shutdown.
	 */
	public final boolean isShutdown() {
		return shutdown;
	}

	/**
	 * Returns {@code true} if this watcher was shutdown,  and the shutdown
	 * process is completed (i.e. the I/O thread is terminated).
	 *
	 * @return {@code true} if this watcher's shutdown completed.
	 */
	public final boolean isShutdownComplete() {
		return !ioThread.isAlive();
	}

	/**
	 * Return whether the given {@link Path} is watched or not. A path is
	 * watched if {@link #watch(Path)} or {@link #watchOrStreamError(Path)}
	 * was invoked on it.
	 *
	 * @param dir The {@link Path} to be checked.
	 * @return {@code true} if {@link #watch(Path)} or
	 *         {@link #watchOrStreamError(Path)} was invoked on the given
	 *         {@code dir}.
	 */
	public boolean isWatched ( final Path dir ) {
		if ( dir == null ) {
			return false;
		} else {
			return watcherKeys
				.keySet()
				.parallelStream()
				.anyMatch(key -> key.isValid() && dir.equals(watcherKeys.get(key).get()));
		}
	}

	/**
	 * Reads the contents of a binary file. One of the two given
	 * {@link Consumer}s will be called on success or on failure.
	 * <p>
	 * {@link Files#readAllBytes(java.nio.file.Path)} will be called to actually
	 * read the file.
	 * </p><p>
	 * <b>Note:</b> the operation is executed by the {@link Executor} passed
	 * to the {@link #build(java.util.concurrent.Executor)} method, i.e. in a
	 * different thread from the caller's one.
	 * </p>
	 *
	 * @param file      The pathname of the file to be read.
	 * @param onSuccess The {@link Consumer} called on success, where the passed
	 *                  parameter are the read bytes from the file.
	 * @param onError   The {@link Consumer} called on failure.
	 */
	public void readBinaryFile( Path file, Consumer<byte[]> onSuccess, Consumer<Throwable> onError ) {
		executeIOOperation(
			() -> Files.readAllBytes(file),
			onSuccess,
			onError
		);
	}

	/**
	 * Reads the contents of a text file. One of the two given
	 * {@link Consumer}s will be called on success or on failure.
	 * <p>
	 * {@link Files#readAllBytes(java.nio.file.Path)} will be called to actually
	 * read the file.
	 * </p><p>
	 * <b>Note:</b> the operation is executed by the {@link Executor} passed
	 * to the {@link #build(java.util.concurrent.Executor)} method, i.e. in a
	 * different thread from the caller's one.
	 * </p>
	 *
	 * @param file      The pathname of the file to be read.
	 * @param charset   The {@link Charset} to be used in reading the
	 *                  from the {@code file}.
	 * @param onSuccess The {@link Consumer} called on success, where the passed
	 *                  parameter is the read string from the file.
	 * @param onError   The {@link Consumer} called on failure.
	 */
	public void readTextFile(
		Path file,
		Charset charset,
		Consumer<String> onSuccess,
		Consumer<Throwable> onError
	) {
		executeIOOperation(
			() -> {

				byte[] bytes = Files.readAllBytes(file);
				CharBuffer chars = charset.decode(ByteBuffer.wrap(bytes));

				return chars.toString();

			},
			onSuccess,
			onError
		);
	}

	/**
	 * Shutdown this watcher.
	 * <p>
	 * <b>Note:</b> this method will not shutdown the event thread {@link Executor}
	 * used to create this watcher.
	 * </p>
	 */
	public final void shutdown() {

		shutdown = true;

		interrupt();

	}

	/**
	 * Returns a {@link CompletionStage} containing the tree structure of
	 * {@link PathElement} instances representing the content of the given
	 * {@code root} directory. The returned stage is completed exceptionally in
	 * case an I/O error occurs.
	 *
	 * @param root The pathname of the tree's root directory.
	 * @return A {@link CompletionStage} containing the tree structure of
	 *         {@link PathElement} instances rooted at the given {@link Path},
	 *         or completed exceptionally if an I/o error occurred.
	 */
	public CompletableFuture<PathElement> tree( Path root ) {

		CompletableFuture<PathElement> future = new CompletableFuture<>();

		executeOnIOThread(() -> {
			try {
				future.complete(PathElement.tree(root));
			} catch ( IOException e ) {
				future.completeExceptionally(e);
			}
		});

		return future;

	}

	/**
	 * Unwatch the given directory {@link Path}.
	 *
	 * @param dir The directory to be unwatched.
	 */
	public void unwatch ( Path dir ) {
		if ( dir != null ) {
			removeWatcherKey(dir);
		}
	}

	/**
	 * Unwatch the given directory and all its parents.
	 *
	 * @param dir The directory to be watched.
	 */
	public void unwatchUp ( Path dir ) {
		unwatchUp(dir, null);
	}

	/**
	 * Unwatch the given directory and all its parents up to the given ancestor.
	 *
	 * @param dir The directory to be watched.
	 * @param to  The {@code dir}'s ancestor {@link Path} (exclusive) where
	 *            unwatching will be stopped. Can be {@code null}.
	 */
	public void unwatchUp ( Path dir, Path to ) {

		if ( dir != null ) {

			if ( to != null && !dir.startsWith(to) ) {
				throw new IllegalArgumentException(
					MessageFormat.format(
						"'to' is not ancestor of 'dir' [to: {0}, dir: {1}].",
						to.toString(),
						dir.toString()
					)
				);
			}

			unwatch(dir);

			Path parent = dir.getParent();

			while ( parent != null && ( to == null || !to.equals(parent) ) ) {

				unwatch(parent);

				parent = parent.getParent();

			}

		}

	}

	/**
	 * Watch the given directory for entry create, delete, and modify events.
	 *
	 * @param dir The directory to be watched.
	 * @throws IOException If an I/O error occurs.
	 */
	public void watch( Path dir ) throws IOException {

		if ( dir != null ) {

			WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);

			watcherKeys.put(key, new WeakReference<>(dir));

		}

	}

	/**
	 * Watch the given directory for entry create, delete, and modify events.
	 * If an I/O error occurs, the exception is logged (emitted).
	 *
	 * @param dir The directory to be watched.
	 */
	public void watchOrStreamError( Path dir ) {
		try {
			watch(dir);
		} catch ( IOException e ) {
			emitError(e);
		}
	}

	/**
	 * Watch the given directory and all its parents for entry create, delete,
	 * and modify events.
	 * <p>
	 * This method is equivalent to calling {@link #watchUp(Path, Path)} passing
	 * {@code null} to its second parameter.</p>
	 *
	 * @param dir The directory to be watched.
	 * @throws IOException If an I/O error occurs.
	 */
	public void watchUp( Path dir ) throws IOException {
		watchUp(dir, null);
	}

	/**
	 * Watch the given directory and all its parents up to the given ancestor,
	 * for entry create, delete, and modify events.
	 *
	 * @param dir The directory to be watched.
	 * @param to  The {@code dir}'s ancestor {@link Path} (exclusive) where
	 *            watching will be stopped. Can be {@code null}.
	 * @throws IOException              If an I/O error occurs.
	 * @throws IllegalArgumentException If {@code to} is not an ancestor of
	 *                                  {@code dir}.
	 */
	public void watchUp( Path dir, Path to ) throws IOException, IllegalArgumentException {

		if ( dir != null ) {

			if ( to != null && !dir.startsWith(to) ) {
				throw new IllegalArgumentException(
					MessageFormat.format(
						"'to' is not ancestor of 'dir' [to: {0}, dir: {1}].",
						to.toString(),
						dir.toString()
					)
				);
			}

			watch(dir);

			Path parent = dir.getParent();

			while ( parent != null && ( to == null || !to.equals(parent) ) ) {

				watch(parent);

				parent = parent.getParent();

			}

		}

	}

	/**
	 * Watch the given directory and all its parents for entry create, delete,
	 * and modify events. If an I/O error occurs, the exception is logged (emitted).
	 * <p>
	 * This method is equivalent to calling {@link #watchUpOrStreamError(Path, Path)}
	 * passing {@code null} to its second parameter.</p>
	 *
	 * @param dir The directory to be watched.
	 */
	public void watchUpOrStreamError( Path dir ) {
		watchUpOrStreamError(dir, null);
	}

	/**
	 * Watch the given directory and all its parents up to the given ancestor,
	 * for entry create, delete, and modify events. If an I/O error occurs, the
	 * exception is logged (emitted).
	 *
	 * @param dir The directory to be watched.
	 * @param to  The {@code dir}'s ancestor {@link Path} (exclusive) where
	 *            watching will be stopped. Can be {@code null}.
	 */
	public void watchUpOrStreamError( Path dir, Path to ) {
		try {
			watchUp(dir, to);
		} catch ( IOException | IllegalArgumentException e ) {
			emitError(e);
		}
	}

	/**
	 * Writes a binary file filling it with the given {@code content}. One of
	 * the two given {@link Consumer}s will be called on success or on failure.
	 * <p>
	 * {@link Files#write(java.nio.file.Path, java.lang.Iterable, java.nio.file.OpenOption...)}
	 * will be called to actually write the file.
	 * </p><p>
	 * <b>Note:</b> the operation is executed by the {@link Executor} passed
	 * to the {@link #build(java.util.concurrent.Executor)} method, i.e. in a
	 * different thread from the caller's one.
	 * </p>
	 *
	 * @param file      The pathname of the file to be written.
	 * @param content   The bytes to be written.
	 * @param onSuccess The {@link Consumer} called on success, where the passed
	 *                  parameter is the written file timestamp.
	 * @param onError   The {@link Consumer} called on failure.
	 */
	public void writeBinaryFile(
		Path file,
		byte[] content,
		Consumer<FileTime> onSuccess,
		Consumer<Throwable> onError
	) {
		executeIOOperation(
			() -> {

				Files.write(file, content, CREATE, WRITE, TRUNCATE_EXISTING);

				return Files.getLastModifiedTime(file);

			},
			onSuccess,
			onError
		);
	}

	/**
	 * Writes a text file filling it with the given {@code content}. One of the
	 * two given {@link Consumer}s will be called on success or on failure.
	 * <p>
	 * {@link Files#write(java.nio.file.Path, java.lang.Iterable, java.nio.file.OpenOption...)}
	 * will be called to actually write the file.
	 * </p><p>
	 * <b>Note:</b> the operation is executed by the {@link Executor} passed
	 * to the {@link #build(java.util.concurrent.Executor)} method, i.e. in a
	 * different thread from the caller's one.
	 * </p>
	 *
	 * @param file      The pathname of the file to be written.
	 * @param content   The {@link String} content to be written.
	 * @param charset   The {@link Charset} to be used in writing the
	 *                  {@code content} into the {@code  file}.
	 * @param onSuccess The {@link Consumer} called on success, where the passed
	 *                  parameter is the written file timestamp.
	 * @param onError   The {@link Consumer} called on failure.
	 */
	public void writeTextFile(
		Path file,
		String content,
		Charset charset,
		Consumer<FileTime> onSuccess,
		Consumer<Throwable> onError
	) {
		executeIOOperation(
			() -> {

				Files.write(file, content.getBytes(charset), CREATE, WRITE, TRUNCATE_EXISTING);

				return Files.getLastModifiedTime(file);

			},
			onSuccess,
			onError
		);
	}

	private void deleteRecursively( Path root ) throws IOException {

		if ( Files.exists(root) ) {

			if ( Files.isDirectory(root) ) {
				try ( DirectoryStream<Path> stream = Files.newDirectoryStream(root) ) {
					for ( Path path : stream ) {
						deleteRecursively(path);
					}
				}
			}

			Files.delete(root);
			removeWatcherKey(root);

		}

	}

	private void emitError( Throwable e ) {
		executeOnEventThread(() -> errors.push(e));
	}

	private void emitEvent( DirectoryEvent event ) {
		executeOnEventThread(() -> events.push(event));
	}

	@SuppressWarnings( { "UseSpecificCatch", "BroadCatchBlock", "TooBroadCatch" } )
	private <T> void executeIOOperation( Callable<T> operation, Consumer<T> onSuccess, Consumer<Throwable> onError ) {
		executeOnIOThread(() -> {
			try {

				T result = operation.call();

				if ( onSuccess != null ) {
					executeOnEventThread(() -> onSuccess.accept(result));
				}

			} catch ( Throwable t ) {
				if ( onError != null ) {
					executeOnEventThread(() -> onError.accept(t));
				} else {
					LOGGER.throwing(DirectoryWatcher.class.getName(), "executeIOOperation", t);
				}
			}
		});
	}

	private void executeOnEventThread( Runnable task ) {
		eventThreadExecutor.execute(task);
	}

	private void executeOnIOThread( Runnable action ) throws RejectedExecutionException {
		if ( !isShutdown() ) {
			executorQueue.add(action);
			interrupt();
		} else {
			throw new RejectedExecutionException("Directory watcher is shutdown.");
		}
	}

	private synchronized void interrupt() {
		if ( mayInterrupt ) {
			ioThread.interrupt();
		} else {
			interrupted = true;
		}
	}

	private void ioLoop() {
		while ( true ) {

			WatchKey key = take();

			if ( key != null ) {

				Path watchedPath = (Path) key.watchable();
				List<WatchEvent<?>> polledEvents = key.pollEvents();
				boolean reset = key.reset();
				DirectoryEvent event = new DirectoryEvent(watchedPath, polledEvents, reset);

				event.getEvents().stream().forEach(e -> {
					if ( StandardWatchEventKinds.ENTRY_DELETE.equals(e.kind()) ) {

						Path child = watchedPath.resolve((Path) e.context());

						if ( child != null && !watchedPath.equals(child) ) {
							removeWatcherKey(child);
						}

					}
				});

				emitEvent(event);

			} else if ( isShutdown() ) {
				try {
					watcher.close();
					break;
				} catch ( IOException e ) {
					emitError(e);
				} finally {
					watcherKeys.clear();
				}
				break;
			} else {
				processIOQueue();
			}

		}
	}

	@SuppressWarnings( "NestedAssignment" )
	private void processIOQueue() {

		Runnable operation;

		while ( ( operation = executorQueue.poll() ) != null ) {
			try {
				operation.run();
			} catch ( Throwable t ) {
				errors.push(t);
			}
		}

	}

	private void removeWatcherKey( final Path path ) {

		Set<WatchKey> keys = watcherKeys
			.keySet()
			.parallelStream()
			.filter(key -> key.isValid() && path.equals(watcherKeys.get(key).get()))
			.collect(Collectors.toSet());

		if ( keys != null && !keys.isEmpty() ) {
			keys.forEach(key -> {
				key.cancel();
				watcherKeys.remove(key);
			});
		}

	}

	private void startIOThread() {
		ioThread.setPriority(Thread.NORM_PRIORITY - 2);
		ioThread.start();
	}

	private WatchKey take() {
		try {

			synchronized ( this ) {
				if ( interrupted ) {

					interrupted = false;

					throw new InterruptedException();

				} else {
					mayInterrupt = true;
				}
			}

			try {
				return watcher.poll();
			} finally {
				synchronized ( this ) {
					mayInterrupt = false;
				}
			}

		} catch ( InterruptedException e ) {
			return null;
		}
	}

	/**
	 * Contains the information about entry create, delete or modify occurred
	 * to a watched directory.
	 *
	 * @author claudio.rosati@esss.se
	 */
	@SuppressWarnings( "PublicInnerClass" )
	public static class DirectoryEvent {

		private final List<WatchEvent<?>> events;
		private final boolean reset;
		private final Path watchedPath;

		private DirectoryEvent( Path watchedPath, List<WatchEvent<?>> events, boolean reset ) {
			this.watchedPath = watchedPath;
			this.events = Collections.unmodifiableList(new ArrayList<>(events));
			this.reset = reset;
		}

		/**
		 * @return A {@link List} of the {@link WatchEvent}s occurred to the
		 *         watched path.
		 */
		@SuppressWarnings( "ReturnOfCollectionOrArrayField" )
		public List<WatchEvent<?>> getEvents() {
			return events;
		}

		/**
		 * @return The watched {@link Path}. It corresponds to what returned by
		 * the {@link WatchKey#watchable()} method.
		 */
		public Path getWatchedPath() {
			return watchedPath;
		}

		/**
		 * @return {@code true} if the {@link WatchKey} that generated this event
		 *         was successfully reset.
		 */
		public boolean wasReset() {
			return reset;
		}

	}

}
