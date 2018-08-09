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
package se.europeanspallationsource.xaos.ui.components;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchService;
import java.nio.file.attribute.FileTime;
import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.Function;
import javafx.application.Platform;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.reactfx.EventSource;
import org.reactfx.EventStream;
import org.reactfx.EventStreams;
import se.europeanspallationsource.xaos.ui.components.tree.TreeDirectoryAsynchronousIO;
import se.europeanspallationsource.xaos.ui.components.tree.TreeDirectoryModel;
import se.europeanspallationsource.xaos.util.io.DirectoryWatcher;
import se.europeanspallationsource.xaos.util.io.PathElement;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;
import static se.europeanspallationsource.xaos.util.DefaultExecutorCompletionStage.wrap;


/**
 * TreeDirectoryMonitor combines a {@link DirectoryWatcher}, a 
 * {@link TreeDirectoryModel}, and a {@link TreeDirectoryAsynchronousIO}.
 * The added value of this combination is:
 * <ul>
 *   <li>The tree model is updated automatically to reflect the current state of
 *     the file-system;</li>
 *   <li>The application can distinguish file-system changes made via the
 *     I/O facility ({@link TreeDirectoryAsynchronousIO}) from external ones.</li>
 * </ul>
 * <p>The directory model can be used directly as a model for {@link TreeView}s.</p>
 * <p><b>Usage:</b></p>
 * <pre>
 *   public class UsageExample extends Application {
 *
 *     enum ChangeSource {
 *       INTERNAL,
 *       EXTERNAL
 *     };
 *
 *     public static void main( String[] args ) {
 *       launch(args);
 *     }
 *
 *     &#64;Override
 *     public void start(Stage primaryStage) {
 *
 *       TreeView&lt;Path&gt; view = new TreeView&lt;&gt;();
 *
 *       view.setShowRoot(false);
 *
 *       try {
 *
 *         // Create a TreeDirectoryMonitor instance for use
 *         // on the JavaFX Application Thread.
 *         TreeDirectoryMonitor&lt;ChangeSource, Path&gt; dirmon = TreeDirectoryMonitor.build(ChangeSource.EXTERNAL);
 * 
 *         // Set directory to watch.
 *         dirmon.addTopLevelDirectory(Paths.get(System.getProperty("user.home"), "Documents").toAbsolutePath());
 *         view.setRoot(dirmon.model().getRoot());
 * 
 *         // Stop DirectoryWatcher's thread.
 *         primaryStage.setOnCloseRequest(val -&gt; dirmon.dispose());
 *       } catch (IOException e) {
 *         e.printStackTrace();
 *       }
 *
 *       primaryStage.setScene(new Scene(view, 500, 500));
 *       primaryStage.show();
 *
 *     }
 *
 *   }
 * </pre>
 *
 * @param <I> Type of the <i>external initiator</i> of the I/O operation.
 * @param <T> Type of the object returned by {@link TreeItem#getValue()}.
 * @author claudio.rosati@esss.se
 * @see <a href="https://github.com/ESSICS/LiveDirsFX">LiveDirsFX:org.fxmisc.livedirs.LiveDirsIO</a>
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class TreeDirectoryMonitor<I, T> {

    /**
     * Creates a {@link TreeDirectoryMonitor} instance to be used from the
	 * JavaFX application thread.
     *
	 * @param <I>               Type of the <i>external initiator</i> of the
	 *                          I/O operation.
	 * @param externalInitiator Object to represent an initiator of an
	 *                          external file-system change.
	 * @return A newly created {@link TreeDirectoryMonitor}'s instance.
	 * @throws IOException If an I/O error occurs.
	 */
	public static <I> TreeDirectoryMonitor<I, Path> build( I externalInitiator ) throws IOException {
		return build(externalInitiator, Platform::runLater);
	}

    /**
     * Creates a {@link TreeDirectoryMonitor} instance to be used from a
	 * designated thread.
     *
	 * @param <I>                  Type of the <i>external initiator</i> of the
	 *                             I/O operation.
	 * @param externalInitiator    Object to represent an initiator of an
	 *                             external file-system change.
	 * @param clientThreadExecutor Executor used to execute actions on the caller
	 *                             thread. Used to publish updates and errors on
	 *                             the caller thread.
	 * @return A newly created {@link TreeDirectoryMonitor}'s instance.
	 * @throws IOException If an I/O error occurs.
	 */
	public static <I> TreeDirectoryMonitor<I, Path> build(
		I externalInitiator,
		Executor clientThreadExecutor
	) throws IOException {
        return build(externalInitiator, Function.identity(), Function.identity(), clientThreadExecutor);
    }

	/**
	 * Creates a {@link TreeDirectoryMonitor} instance to be used from a
	 * designated thread.
	 *
	 * @param <I>                  Type of the <i>external initiator</i> of the
	 *                             I/O operation.
	 * @param <T>                  Type of the object returned by
	 *                             {@link TreeItem#getValue()}.
	 * @param externalInitiator    Object to represent an initiator of an
	 *                             external file-system change.
	 * @param projector            Converts the ({@code T}) {@link TreeItem#getValue()}
	 *                             into a {@link Path} object.
	 * @param injector             Converts a given {@link Path} object into
	 *                             {@code T}. The reverse of {@code projector}.
	 * @param clientThreadExecutor Executor used to execute actions on the caller
	 *                             thread. Used to publish updates and errors on
	 *                             the caller thread.
	 * @return A newly created {@link TreeDirectoryMonitor}'s instance.
	 * @throws IOException If an I/O error occurs.
	 */
	public static <I, T> TreeDirectoryMonitor<I, T> build(
		I externalInitiator,
		Function<T, Path> projector,
		Function<Path, T> injector,
		Executor clientThreadExecutor
	) throws IOException {
        return new TreeDirectoryMonitor<I, T>(
			externalInitiator,
			projector,
			injector,
			clientThreadExecutor
		);
    }

    private final Executor clientThreadExecutor;
    private final DirectoryWatcher directoryWatcher;
	private final EventStream<Throwable> errors;
	private final I externalInitiator;
    private final TreeDirectoryAsynchronousIO<I, T> io;
	private final EventSource<Throwable> localErrors = new EventSource<>();
	private final TreeDirectoryModel<I, T> model;

	/**
	 * Creates a {@link TreeDirectoryMonitor} instance to be used from a
	 * designated thread.
	 *
	 * @param externalInitiator    Object to represent an initiator of an
	 *                             external file-system change.
	 * @param projector            Converts the ({@code T}) {@link TreeItem#getValue()}
	 *                             into a {@link Path} object.
	 * @param injector             Converts a given {@link Path} object into
	 *                             {@code T}. The reverse of {@code projector}.
	 * @param clientThreadExecutor Executor used to execute actions on the caller
	 *                             thread. Used to publish updates and errors on
	 *                             the caller thread.
	 * @throws IOException If an I/O error occurs.
	 */
	private TreeDirectoryMonitor(
		I externalInitiator,
		Function<T, Path> projector,
		Function<Path, T> injector,
		Executor clientThreadExecutor
	) throws IOException {

		this.externalInitiator = externalInitiator;
        this.model = new TreeDirectoryModel<>(externalInitiator, projector, injector);
        this.clientThreadExecutor = clientThreadExecutor;
        this.directoryWatcher = DirectoryWatcher.build(clientThreadExecutor);
        this.io = new TreeDirectoryAsynchronousIO<>(directoryWatcher, model, clientThreadExecutor);

        this.directoryWatcher.events().subscribe(this::processDirectoryEvent);
        this.errors = EventStreams.merge(directoryWatcher.errors(), model.errors(), localErrors);

    }

	/**
	 * Adds a directory to watch. The directory will be added to the directory
	 * model and watched for changes.
	 *
	 * @param dir The directory to be watched and viewed.
	 */
	public void addTopLevelDirectory( Path dir ) {

		if ( !dir.isAbsolute() ) {
			throw new IllegalArgumentException(MessageFormat.format(
				"`{0}` is not absolute. Only absolute paths may be added as top-level directories.",
				dir
			));
		}

		try {

//	Not needed: already in refresh(dir).
//			if ( !directoryWatcher.isWatched(dir) ) {
//				directoryWatcher.watch(dir);
//			}

			model.addTopLevelDirectory(dir);
			refresh(dir);
			
//		} catch ( IOException e ) {
		} catch ( Exception e ) {
			localErrors.push(e);
		}
		
	}

	/**
	 * Releases resources used by this {@link TreeDirectoryMonitor} instance.
	 * In particular, stops the I/O thread (used for I/O operations as well as
	 * directory watching).
	 */
	public void dispose() {
		directoryWatcher.shutdown();
	}

	/**
	 * @return The {@link EventStream} of asynchronously thrown errors.
	 */
	public EventStream<Throwable> errors() {
		return errors;
	}

	/**
	 * @return The asynchronous I/O facility. All I/O operations performed by
	 *         this facility are performed on a single thread. It is the same
	 *         thread that is used to watch the file-system for changes.
	 */
	public TreeDirectoryAsynchronousIO<I, T> io() {
		return io;
	}

    /**
     * @return The observable tree directory model.
     */
	public TreeDirectoryModel<I, T> model() {
		return model;
	}

	/**
	 * Used to manually refresh the given subtree of the directory model.
	 * <p>
	 * Guarantees given by {@link WatchService} are weak and the behavior
	 * may vary on different operating systems. It is possible that the
	 * automatic synchronization is not 100% reliable. This method provides a
	 * way to request synchronization in case any inconsistencies are
	 * observed.</p>
	 *
	 * @param path The {@link Path} to be refreshed.
	 * @return A {@link CompletionStage} completed exceptionally if an I/o
	 *         error occurred.
	 */
	public CompletionStage<Void> refresh( Path path ) {
		return wrap(directoryWatcher.tree(path), clientThreadExecutor)
			.thenAcceptAsync(
				tree -> {
					model.sync(tree);
					watchDirectory(tree);
				},
				clientThreadExecutor
			);
	}

	@SuppressWarnings( "unchecked" )
    private void processDirectoryEvent ( DirectoryWatcher.DirectoryEvent event ) {

		Path dir = event.getWatchedPath();
        
		if( model.containsPrefixOf(dir) ) {

			List<WatchEvent<?>> events = event.getEvents();
        
			if ( events.stream().anyMatch(evt -> evt.kind() == OVERFLOW) ) {
				refreshOrStreamError(dir);
			} else {
				events.forEach(evt -> processEvent(dir, (WatchEvent<Path>) evt));
			}

			if ( !event.wasReset() ) {
				model.delete(dir, externalInitiator);
			}
		}

    }

	private void processEvent( Path dir, WatchEvent<Path> event ) {

		//	Context for directory entry event is the file name of entry.
        Path relChild = event.context();
        Path child = dir.resolve(relChild);
        Kind<Path> kind = event.kind();

		if ( kind == ENTRY_MODIFY ) {
			try {

				FileTime timestamp = Files.getLastModifiedTime(child);

				model.updateModificationTime(child, timestamp, externalInitiator);

			} catch ( IOException ex ) {
				localErrors.push(ex);
			}
		} else if ( kind == ENTRY_CREATE ) {
			if ( Files.isDirectory(child) ) {

				if ( model.containsPrefixOf(child) ) {
					model.addDirectory(child, externalInitiator);
					directoryWatcher.watchOrStreamError(child);
				}

				refreshOrStreamError(child);

			} else {
				try {

					FileTime timestamp = Files.getLastModifiedTime(child);

					model.addFile(child, timestamp, externalInitiator);

				} catch ( IOException e ) {
					localErrors.push(e);
				}
			}
		} else if ( kind == ENTRY_DELETE ) {
			model.delete(child, externalInitiator);
		} else {
			throw new AssertionError("Unreachable code.");
		}

	}

	private void refreshOrStreamError( Path path ) {
		refresh(path).whenComplete(( nothing, ex ) -> {
			if ( ex != null ) {
				localErrors.push(ex);
			}
		});
	}

	private void watchDirectory( PathElement tree ) {

		if ( tree.isDirectory() ) {

			Path path = tree.getPath();
			
			if ( !directoryWatcher.isWatched(path) ) {
				directoryWatcher.watchOrStreamError(path);
			}

			tree.getChildren().forEach(child -> watchDirectory(child));

		}

	}

}
