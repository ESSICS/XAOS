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


import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.attribute.FileTime;
import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import javafx.application.Platform;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import se.europeanspallationsource.xaos.core.util.io.DirectoryWatcher;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;


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
 *         view.setShowRoot(false);
 *         view.setCellFactory(TreeItems.defaultTreePathCellFactory());
 * 
 *         // Stop DirectoryWatcher's thread.
 *         primaryStage.setOnCloseRequest(val -&gt; dirmon.dispose());
 *
 *       } catch (IOException e) {
 *         e.printStackTrace();
 *       }
 *
 *       primaryStage.setOnCloseRequest(event -&gt; dirmon.dispose());
 *       primaryStage.setScene(new Scene(view, 500, 500));
 *       primaryStage.show();
 *
 *     }
 *
 *   }
 * </pre>
 * <p>
 * <b>Note:</b> {@link #dispose()} should be called when the model is no more
 * used (typically when the viewer using it is disposed).</p>
 *
 * @param <I> Type of the <i>external initiator</i> of the I/O operation.
 * @param <T> Type of the object returned by {@link TreeItem#getValue()}.
 * @author claudio.rosati@esss.se
 * @see <a href="https://github.com/ESSICS/LiveDirsFX">LiveDirsFX:org.fxmisc.livedirs.LiveDirsIO</a>
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class TreeDirectoryMonitor<I, T> implements Disposable {

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
        return new TreeDirectoryMonitor<>(
			externalInitiator,
			projector,
			injector,
			clientThreadExecutor
		);
    }

    private final DirectoryWatcher directoryWatcher;
	private final Disposable directoryWatcherEventsSubscription;
	private boolean disposed = false;
	private final Observable<Throwable> errors;
	private final I externalInitiator;
    private final TreeDirectoryAsynchronousIO<I, T> io;
	private final Subject<Throwable> localErrors;
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
		this.directoryWatcher = DirectoryWatcher.build(clientThreadExecutor);
		this.io = new TreeDirectoryAsynchronousIO<>(directoryWatcher, model, clientThreadExecutor);

		Subject<Throwable> localErrorsSubject = PublishSubject.create();

		this.localErrors = localErrorsSubject.toSerialized();
		this.errors = Observable.merge(directoryWatcher.errors(), model.errors(), localErrors);
		this.directoryWatcherEventsSubscription = this.directoryWatcher.events().subscribe(this::processDirectoryEvent);

    }

	/**
	 * Adds a directory to watch. The directory will be added to the directory
	 * model and watched for changes.
	 *
	 * @param dir The directory to be watched and viewed.
	 */
	public void addTopLevelDirectory( Path dir) {
		addTopLevelDirectory(dir, null, null);
	}

	/**
	 * Adds a directory to watch. The directory will be added to the directory
	 * model and watched for changes.
	 *
	 * @param dir        The directory to be watched and viewed.
	 * @param onCollapse A {@link Consumer} to be invoked when this item is
	 *                   collapsed. Can be {@code null}.
	 * @param onExpand   A {@link Consumer} to be invoked when this item is
	 *                   expanded. Can be {@code null}.
	 */
	public void addTopLevelDirectory(
		Path dir,
		final Consumer<? super TreeDirectoryItems.DirectoryItem<T>> onCollapse,
		final Consumer<? super TreeDirectoryItems.DirectoryItem<T>> onExpand
	) {

		if ( !dir.isAbsolute() ) {
			throw new IllegalArgumentException(MessageFormat.format(
				"`{0}` is not absolute. Only absolute paths may be added as top-level directories.",
				dir
			));
		}

		try {
			model.addTopLevelDirectory(
				dir,
				onCollapse,
				dirItem -> {

					watchDirectory(dirItem.getPath());

					if ( onExpand != null ) {
						onExpand.accept(dirItem);
					}

				}
			);
			model.sync(dir);
		} catch ( Exception e ) {
			localErrors.onNext(e);
		}
		
	}

	/**
	 * Releases resources used by this {@link TreeDirectoryMonitor} instance.
	 * In particular, stops the I/O thread (used for I/O operations as well as
	 * directory watching).
	 */
	@Override
	@SuppressWarnings( "ConvertToTryWithResources" )
	public void dispose() {
		directoryWatcherEventsSubscription.dispose();
		localErrors.onComplete();
		directoryWatcher.close();
	}

	/**
	 * @return The {@link Observable} of asynchronously thrown errors.
	 */
	public Observable<Throwable> errors() {
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

	@Override
	public boolean isDisposed() {
		return disposed;
	}

    /**
     * @return The observable tree directory model.
     */
	public TreeDirectoryModel<I, T> model() {
		return model;
	}

	@SuppressWarnings( "unchecked" )
    private void processDirectoryEvent ( DirectoryWatcher.DirectoryEvent event ) {

		Path dir = event.getWatchedPath();
        
		if( model.containsPrefixOf(dir) ) {

			List<WatchEvent<?>> events = event.getEvents();
        
			if ( events.stream().anyMatch(evt -> evt.kind() == OVERFLOW) ) {
				model.sync(dir);
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
				localErrors.onNext(ex);
			}
		} else if ( kind == ENTRY_CREATE ) {
			if ( Files.isDirectory(child) ) {
				if ( model.containsPrefixOf(child) ) {
					model.addDirectory(child, externalInitiator);
					model.sync(child);
				}
			} else {
				try {

					FileTime timestamp = Files.getLastModifiedTime(child);

					model.addFile(child, timestamp, externalInitiator);

				} catch ( IOException e ) {
					localErrors.onNext(e);
				}
			}
		} else if ( kind == ENTRY_DELETE ) {
			model.delete(child, externalInitiator);
		} else {
			throw new AssertionError("Unreachable code.");
		}

	}

	private void watchDirectory( Path path ) {
		if ( Files.isDirectory(path) ) {
			if ( !directoryWatcher.isWatched(path) ) {
				directoryWatcher.watchOrStreamError(path);
			}
		} else {
			localErrors.onNext(new NotDirectoryException(path.toString()));
		}
	}

}
