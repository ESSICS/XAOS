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
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.text.MessageFormat;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.reactfx.EventSource;
import org.reactfx.EventStream;
import se.europeanspallationsource.xaos.core.util.TriFunction;
import se.europeanspallationsource.xaos.core.util.io.DirectoryModel;
import se.europeanspallationsource.xaos.core.util.io.PathElement;
import se.europeanspallationsource.xaos.ui.control.Icons;

import static se.europeanspallationsource.xaos.ui.control.CommonIcons.FILE;
import static se.europeanspallationsource.xaos.ui.control.CommonIcons.FILE_EXECUTABLE;
import static se.europeanspallationsource.xaos.ui.control.CommonIcons.FILE_HIDDEN;
import static se.europeanspallationsource.xaos.ui.control.CommonIcons.FILE_LINK;
import static se.europeanspallationsource.xaos.ui.control.CommonIcons.FOLDER_COLLAPSED;
import static se.europeanspallationsource.xaos.ui.control.CommonIcons.FOLDER_EXPANDED;
import static se.europeanspallationsource.xaos.ui.control.Icons.DEFAULT_SIZE;


/**
 * A {@link DirectoryModel} that can be used in a {@link TreeView}.
 * <p>
 * This model uses the {@link #DEFAULT_GRAPHIC_FACTORY} to provide graphics
 * to the tree nodes. That can be changed invoking {@link #setGraphicFactory(TreeDirectoryModel.GraphicFactory)}
 * after this model is built.</p>
 *
 * @param <I> Type of the initiator of changes to the model.
 * @param <T> Type of the object returned by {@link TreeItem#getValue()}.
 * @author claudio.rosati@esss.se
 * @see <a href="https://github.com/ESSICS/LiveDirsFX">LiveDirsFX:org.fxmisc.livedirs.LiveDirsModel</a>
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class TreeDirectoryModel<I, T> implements DirectoryModel<I, T> {

	/**
	 * Graphic factory that returns a folder icon for a directory and
	 * a document icon for a regular file.
	 */
	public static final GraphicFactory DEFAULT_GRAPHIC_FACTORY = new DefaultGraphicFactory();

	/**
	 * Graphic factory that always returns {@code null}.
	 */
	public static final GraphicFactory NO_GRAPHIC_FACTORY = ( p, d, e ) -> null;

	private final EventSource<Update<I>> creations = new EventSource<>();
	private final I defaultInitiator;
	private final EventSource<Update<I>> deletions = new EventSource<>();
	private final EventSource<Throwable> errors = new EventSource<>();
	private GraphicFactory graphicFactory = DEFAULT_GRAPHIC_FACTORY;
	private final Function<Path, T> injector;
	private final EventSource<Update<I>> modifications = new EventSource<>();
	private final Function<T, Path> projector;
	private final Reporter<I> reporter;
	private final TreeItem<T> root = new TreeItem<>();

	/**
	 * Create a new instance of this model.
	 *
	 * @param defaultInitiator The initiator used when not explicitly provided
	 *                         as parameter in methods.
	 * @param projector        A {@link Function} converting the object returned
	 *                         by {@link TreeItem#getValue()}) into the
	 *                         corresponding {@link Path}.
	 * @param injector         A {@link Function} converting a {@link Path} into
	 *                         the object used as value in the corresponding
	 *                         {@link TreeItem}.
	 */
	public TreeDirectoryModel( I defaultInitiator, Function<T, Path> projector, Function<Path, T> injector ) {
		this.defaultInitiator = defaultInitiator;
		this.injector = injector;
		this.projector = projector;
		this.reporter = new Reporter<I>() {

			@Override
			public void reportCreation( Path baseDir, Path relativePath, I initiator ) {
				creations.push(Update.creation(baseDir, relativePath, initiator));
			}

			@Override
			public void reportDeletion( Path baseDir, Path relativePath, I initiator ) {
				deletions.push(Update.deletion(baseDir, relativePath, initiator));
			}

			@Override
			public void reportError( Throwable error ) {
				errors.push(error);
			}

			@Override
			public void reportModification( Path baseDir, Path relativePath, I initiator ) {
				modifications.push(Update.modification(baseDir, relativePath, initiator));
			}

		};
	}

	/**
	 * Add a no-top-lever directory to the model.
	 *
	 * @param directory The {@link Path} to be added as a directory.
	 */
	public void addDirectory( Path directory ) {
		addDirectory(directory, defaultInitiator);
	}

	/**
	 * Add a no-top-level directory to the model.
	 *
	 * @param directory The {@link Path} to be added as a directory.
	 * @param initiator The initiator of changes to the model.
	 */
	public void addDirectory( Path directory, I initiator ) {
		topLevelAncestorStream(directory).forEach(ancestor -> {

			Path relativePath = ancestor.getPath().relativize(directory);

			Path progressivePath = null;

			for ( int i = 0; i < relativePath.getNameCount(); i++ ) {

				if ( progressivePath == null ) {
					progressivePath = relativePath.getName(i);
				} else {
					progressivePath = Paths.get(
						progressivePath.toString(),
						relativePath.getName(i).toString()
					);
				}

				ancestor.addDirectory(progressivePath, initiator);

			}

		});
	}

	/**
	 * Add a file to the model.
	 *
	 * @param file         The {@link Path} to be added as a file.
	 * @param lastModified The timestamp of the last modification to the file.
	 */
	public void addFile( Path file, FileTime lastModified ) {
		addFile(file, lastModified, defaultInitiator);
	}

	/**
	 * Add a file to the model.
	 *
	 * @param file         The {@link Path} to be added as a file.
	 * @param lastModified The timestamp of the last modification to the file.
	 * @param initiator    The initiator of changes to the model.
	 */
	public void addFile( Path file, FileTime lastModified, I initiator ) {
		topLevelAncestorStream(file).forEach(ancestor -> {

			Path relativePath = ancestor.getPath().relativize(file);

			ancestor.addFile(relativePath, lastModified, initiator);

		});
	}

	/**
	 * Add a top-lever directory to the model.
	 * <p>
	 * <b>Note:</b> The model is not synchronized automatically with the
	 * given {@code directory}. An explicit call to
	 * {@link #sync(se.europeanspallationsource.xaos.core.util.io.PathElement)},
	 * or {@link #sync(se.europeanspallationsource.xaos.core.util.io.PathElement, java.lang.Object)}
	 * has to be performed if synchronization is required.</p>
	 *
	 * @param directory     The {@link Path} to be added as a top-level directory.
	 * @param synchOnExpand If (@code true}, then folder synchronization is
	 *                      performed only when the tree item is expanded.
	 */
	public void addTopLevelDirectory( Path directory, boolean synchOnExpand ) {
		root.getChildren().add(TreeDirectoryItems.createTopLevelDirectoryItem(
			injector.apply(directory),
			graphicFactory,
			projector,
			injector,
			reporter,
			synchOnExpand,
			null,
			null
		));
	}

	/**
	 * Add a top-lever directory to the model.
	 * <p>
	 * <b>Note:</b> The model is not synchronized automatically with the
	 * given {@code directory}. An explicit call to
	 * {@link #sync(se.europeanspallationsource.xaos.core.util.io.PathElement)},
	 * or {@link #sync(se.europeanspallationsource.xaos.core.util.io.PathElement, java.lang.Object)}
	 * has to be performed if synchronization is required.</p>
	 * <p>
	 * <b>Note:</b> {@link #addDirectory(Path)} and {@link #addDirectory(Path, Object)}
	 * methods will pass the given {@code onCollapse} and {@code onExpand} parameters
	 * to the newly created {@link DirectoryItem}.</p>
	 *
	 * @param directory     The {@link Path} to be added as a top-level directory.
	 * @param synchOnExpand If (@code true}, then folder synchronization is
	 *                      performed only when the tree item is expanded.
	 * @param onCollapse    A {@link Consumer} to be invoked when this item
	 *                      is collapsed. Can be {@code null}.
	 * @param onExpand      A {@link Consumer} to be invoked when this item
	 *                      is expanded. Can be {@code null}.
	 */
	public void addTopLevelDirectory(
		Path directory,
		boolean synchOnExpand,
		Consumer<? super TreeDirectoryItems.DirectoryItem<T>> onCollapse,
		Consumer<? super TreeDirectoryItems.DirectoryItem<T>> onExpand
	) {
		root.getChildren().add(TreeDirectoryItems.createTopLevelDirectoryItem(
			injector.apply(directory),
			graphicFactory,
			projector,
			injector,
			reporter,
			synchOnExpand,
			onCollapse,
			onExpand
		));
	}

	@Override
	public boolean contains( Path path ) {
		return topLevelAncestorStream(path).anyMatch(
			ancestor -> ancestor.contains(ancestor.getPath().relativize(path))
		);
	}

	/**
	 * Indicates whether this directory model contains roots whose name is
	 * a prefix in the given {@code path} name.
	 *
	 * @param path The {@link Path} to be verified.
	 * @return {@code true} if this directory model contains roots whose name is
	 *         a prefix in the given {@code path} name.
	 */
	public boolean containsPrefixOf( Path path ) {
		return root.getChildren().stream().anyMatch(
			item -> path.startsWith(projector.apply(item.getValue()))
		);
	}

	@Override
	public EventStream<Update<I>> creations() {
		return creations;
	}

	/**
	 * Delete the given path from the model.
	 *
	 * @param path The {@link Path} to be removed.
	 */
	public void delete( Path path ) {
		delete(path, defaultInitiator);
	}

	/**
	 * Delete the given path from the model.
	 *
	 * @param path      The {@link Path} to be removed.
	 * @param initiator The initiator of changes to the model.
	 */
	public void delete( Path path, I initiator ) {
		getTopLevelAncestors(path, true).forEach(( ancestor ) -> {

			Path relativePath = ancestor.getPath().relativize(path);

			ancestor.remove(relativePath, initiator);

		});
	}

	@Override
	public EventStream<Update<I>> deletions() {
		return deletions;
	}

	@Override
	public EventStream<Throwable> errors() {
		return errors;
	}

	@Override
	public TreeItem<T> getRoot() {
		return root;
	}

	@Override
	public EventStream<Update<I>> modifications() {
		return modifications;
	}

	/**
	 * Sets graphic factory used to create graphics of {@link TreeItem}s
	 * in this directory model.
	 * <p>
	 * {@link #DEFAULT_GRAPHIC_FACTORY} and {@link #NO_GRAPHIC_FACTORY} are
	 * two factories already available.
	 * </p>
	 * <p>
	 * {@link TreeItem}s created before this method is called will be displayed
	 * using the previous graphic factory.
	 * </p>
	 *
	 * @param factory The new graphic factory instance. If {@code null},
	 *                {@link #DEFAULT_GRAPHIC_FACTORY} will be used.
	 */
	public void setGraphicFactory( GraphicFactory factory ) {
		graphicFactory = factory != null ? factory : DEFAULT_GRAPHIC_FACTORY;
	}

	/**
	 * Synchronize the model with the given {@code tree} element. Missing items
	 * will be added to the model. Items will be removed from the model if no
	 * more existing. Files timestamps will be updated too.
	 *
	 * @param tree The {@link PathElement} used to synchronize the model.
	 */
	public void sync( PathElement tree ) {
		sync(tree, defaultInitiator);
	}

	public void sync( PathElement tree, I initiator ) {

		Path path = tree.getPath();

		topLevelAncestorStream(path).forEach(ancestor -> ancestor.sync(tree, initiator));

	}

	public void updateModificationTime( Path path, FileTime lastModified ) {
		updateModificationTime(path, lastModified, defaultInitiator);
	}

	public void updateModificationTime( Path path, FileTime lastModified, I initiator ) {
		getTopLevelAncestors(path, true).forEach(ancestor -> {

			Path relativePath = ancestor.getPath().relativize(path);

			ancestor.updateModificationTime(relativePath, lastModified, initiator);

		});
	}

	private List<TreeDirectoryItems.TopLevelDirectoryItem<I, T>> getTopLevelAncestors( Path path, boolean verifyNonEmpty ) {

		List<TreeDirectoryItems.TopLevelDirectoryItem<I, T>> roots = topLevelAncestorStream(path).collect(Collectors.toList());

		if ( verifyNonEmpty ) {
			assert !roots.isEmpty() : "'path' resolved against a dir that was reported to be in the model, but does not have a top-level ancestor in the model.";
		}

		return roots;

	}

	@SuppressWarnings( "unchecked" )
	private Stream<TreeDirectoryItems.TopLevelDirectoryItem<I, T>> topLevelAncestorStream( Path path ) {
		return root.getChildren().stream()
			.filter(item -> path.startsWith(projector.apply(item.getValue())))
			.map(item -> (TreeDirectoryItems.TopLevelDirectoryItem<I, T>) item);
	}

	/**
	 * Default graphic factory returning a folder icon for a directory and
	 * a document icon for a regular file.
	 */
	@SuppressWarnings( "PublicInnerClass" )
	public static class DefaultGraphicFactory implements GraphicFactory {

		private static final Logger LOGGER = Logger.getLogger(DefaultGraphicFactory.class.getName());

		@Override
		public Node createGraphic( Path path, boolean isDirectory, boolean isExpanded ) {
			if ( isDirectory ) {
				return isExpanded ? Icons.iconFor(FOLDER_EXPANDED, DEFAULT_SIZE) : Icons.iconFor(FOLDER_COLLAPSED, DEFAULT_SIZE);
			} else if ( Files.isSymbolicLink(path) ) {
				return Icons.iconFor(FILE_LINK, DEFAULT_SIZE);
			} else if ( Files.isExecutable(path) ) {
				return Icons.iconFor(FILE_EXECUTABLE, DEFAULT_SIZE);
			} else {
				
				boolean hidden = false;
				
				try {
					hidden = Files.isHidden(path);
				} catch ( IOException ex ) {
					LOGGER.log(
						Level.WARNING, 
						MessageFormat.format("Unable to check if the given path is hidden [{0}].", path.toString()), 
						ex
					);
				}
				
				return hidden
					   ?	 Icons.iconFor(FILE_HIDDEN, DEFAULT_SIZE)
					   : Icons.iconFor(path, DEFAULT_SIZE, Icons.iconFor(FILE, DEFAULT_SIZE));
				
			}
		}

	}

	/**
	 * Factory to create graphics for {@link TreeItem}s in a
	 * {@link DirectoryModel}.
	 */
	@FunctionalInterface
	@SuppressWarnings( "PublicInnerClass" )
	public interface GraphicFactory extends TriFunction<Path, Boolean, Boolean, Node> {

		/**
		 * Creates a graphics {@link Node} for the given {@link Path}.
		 *
		 * @param path        The {@link Path} needing a graphical representation.
		 * @param isDirectory The returned graphics must represent a node.
		 * @param isExpanded  If {@code isDirectory} is {@code true}, then this
		 *                    parameter tells if the directory must be represented
		 *                    as expanded or collapsed.
		 * @return The graphical representation for the given {@link Path}.
		 */
		Node createGraphic( Path path, boolean isDirectory, boolean isExpanded );

		@Override
		default Node apply( Path path, Boolean isDirectory, Boolean isExpanded ) {
			return createGraphic(path, isDirectory, isExpanded);
		}

	}

}
