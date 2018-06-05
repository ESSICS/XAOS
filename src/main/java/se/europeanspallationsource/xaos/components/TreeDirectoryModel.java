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


import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.reactfx.EventSource;
import org.reactfx.EventStream;
import se.europeanspallationsource.xaos.tools.io.DirectoryModel;
import se.europeanspallationsource.xaos.tools.io.PathElement;

import static se.europeanspallationsource.xaos.application.utilities.CommonIcons.FILE;
import static se.europeanspallationsource.xaos.application.utilities.CommonIcons.FOLDER;


/**
 * A {@link DirectoryModel} that can be used in a {@link TreeView}.
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
	public static final GraphicFactory NO_GRAPHIC_FACTORY = ( path, isDirectory ) -> null;

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
	 *
	 * @param factory The new graphic factory instance. If {@code null},
	 *                {@link #DEFAULT_GRAPHIC_FACTORY} will be used.
	 */
	public void setGraphicFactory( GraphicFactory factory ) {
		graphicFactory = factory != null ? factory : DEFAULT_GRAPHIC_FACTORY;
	}

	void addDirectory( Path path ) {
		addDirectory(path, defaultInitiator);
	}

	void addDirectory( Path path, I initiator ) {
		topLevelAncestorStream(path).forEach(ancestor -> {

			Path relativePath = ancestor.getPath().relativize(path);

			ancestor.addDirectory(relativePath, initiator);

		});
	}

	void addFile( Path path, FileTime lastModified ) {
		addFile(path, lastModified, defaultInitiator);
	}

	void addFile( Path path, FileTime lastModified, I initiator ) {
		topLevelAncestorStream(path).forEach(ancestor -> {

			Path relativePath = ancestor.getPath().relativize(path);

			ancestor.addFile(relativePath, lastModified, initiator);

		});
	}

	void addTopLevelDirectory( Path directory ) {
		root.getChildren().add(new TreeDirectoryItems.TopLevelDirectoryItem<>(injector.apply(directory), graphicFactory, projector, injector, reporter));
	}

	void delete( Path path ) {
		delete(path, defaultInitiator);
	}

	void delete( Path path, I initiator ) {
		getTopLevelAncestors(path, true).forEach(( ancestor ) -> {

			Path relativePath = ancestor.getPath().relativize(path);

			ancestor.remove(relativePath, initiator);

		});
	}

	void sync( PathElement tree ) {
		sync(tree, defaultInitiator);
	}

	void sync( PathElement tree, I initiator ) {

		Path path = tree.getPath();

		topLevelAncestorStream(path).forEach(
			ancestor -> ancestor.sync(tree, initiator));

	}

	void updateModificationTime( Path path, FileTime lastModified ) {
		updateModificationTime(path, lastModified, defaultInitiator);
	}

	void updateModificationTime( Path path, FileTime lastModified, I initiator ) {
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
	private static class DefaultGraphicFactory implements GraphicFactory {

		@Override
		public Node createGraphic( Path path, boolean isDirectory ) {
			return isDirectory ? FOLDER.getIcon() : FILE.getIcon();
		}

	}

	/**
	 * Factory to create graphics for {@link TreeItem}s in a
	 * {@link DirectoryModel}.
	 */
	@FunctionalInterface
	@SuppressWarnings( "PublicInnerClass" )
	public interface GraphicFactory extends BiFunction<Path, Boolean, Node> {

		Node createGraphic( Path path, boolean isDirectory );

		@Override
		default Node apply( Path path, Boolean isDirectory ) {
			return createGraphic(path, isDirectory);
		}

	}

}
