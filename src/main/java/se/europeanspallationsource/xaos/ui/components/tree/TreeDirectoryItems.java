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
package se.europeanspallationsource.xaos.ui.components.tree;


import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import se.europeanspallationsource.xaos.core.util.io.DirectoryModel;
import se.europeanspallationsource.xaos.core.util.io.PathElement;


/**
 * Containing the classes used by {@link TreeDirectoryModel}.
 *
 * @author claudio.rosati@esss.se
 * @see <a href="https://github.com/ESSICS/LiveDirsFX">LiveDirsFX:org.fxmisc.livedirs.PathItem</a>
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class TreeDirectoryItems {

	/**
	 * Creates a new instance of {@link DirectoryItem} for the given parameters.
	 *
	 * @param <T>            Type of the object returned by {@link TreeItem#getValue()}.
	 * @param path           The value of this {@link TreeItem}.
	 * @param graphicFactory The factory class used to get an "icon" representing
	 *                       the created item.
	 * @param projector      A {@link Function} converting the object returned
	 *                       by {@link TreeItem#getValue()}) into the
	 *                       corresponding {@link Path}.
	 * @param injector       A {@link Function} converting a {@link Path} into
	 *                       the object used as value in the corresponding
	 *                       {@link TreeItem}.
	 * @return A new instance of{@link DirectoryItem}.
	 */
	public static <T> DirectoryItem<T> createDirectoryItem( T path, TreeDirectoryModel.GraphicFactory graphicFactory, Function<T, Path> projector, Function<Path, T> injector ) {
		return new DirectoryItem<>(path, graphicFactory.createGraphic(projector.apply(path), true, false), graphicFactory.createGraphic(projector.apply(path), true, true), projector, injector);
	}

	/**
	 * Creates a new instance of {@link FileItem} for the given parameters.
	 *
	 * @param <T>            Type of the object returned by {@link TreeItem#getValue()}.
	 * @param path           The value of this {@link TreeItem}.
	 * @param lastModified   The date of last modification for this item.
	 * @param graphicFactory The factory class used to get an "icon" representing
	 *                       the created item.
	 * @param projector      A {@link Function} converting the object returned
	 *                       by {@link TreeItem#getValue()}) into the
	 *                       corresponding {@link Path}.
	 * @return A new instance of{@link FileItem}.
	 */
	public static <T> FileItem<T> createFileItem( T path, FileTime lastModified, TreeDirectoryModel.GraphicFactory graphicFactory, Function<T, Path> projector ) {
		return new FileItem<>(path, lastModified, graphicFactory.createGraphic(projector.apply(path), false, false), projector);
	}

	/**
	 * Creates a new instance of {@link TopLevelDirectoryItem} for the given parameters.
	 *
	 * @param <I>            Type of the initiator of changes to the model.
	 * @param <T>            Type of the object returned by {@link TreeItem#getValue()}.
	 * @param path           The value of this {@link TreeItem}.
	 * @param graphicFactory The factory class used to get an "icon" representing
	 *                       the created item.
	 * @param projector      A {@link Function} converting the object returned
	 *                       by {@link TreeItem#getValue()}) into the
	 *                       corresponding {@link Path}.
	 * @param injector       A {@link Function} converting a {@link Path} into
	 *                       the object used as value in the corresponding
	 *                       {@link TreeItem}.
	 * @param reporter       The object reporting changes in the model.
	 * @return A new instance of{@link TopLevelDirectoryItem}.
	 */
	public static <I, T> TopLevelDirectoryItem<I, T> createTopLevelDirectoryItem( T path, TreeDirectoryModel.GraphicFactory graphicFactory, Function<T, Path> projector, Function<Path, T> injector, DirectoryModel.Reporter<I> reporter ) {
		return new TopLevelDirectoryItem<>(path, graphicFactory, projector, injector, reporter);
	}

	private TreeDirectoryItems() {
	}

	/**
	 * A {@link TreeItem} representing a directory.
	 *
	 * @param <T> Type of the object returned by {@link TreeItem#getValue()}.
	 */
	@SuppressWarnings( { "PackageVisibleInnerClass", "PublicInnerClass" } )
	public static class DirectoryItem<T> extends PathItem<T> {

		private final Function<Path, T> injector;

		protected DirectoryItem(
			T path,
			final Node closeGraphic,
			final Node openGraphic,
			Function<T, Path> projector,
			Function<Path, T> injector
		) {

			super(path, projector);

			this.injector = injector;

			graphicProperty().bind(Bindings.createObjectBinding(
				() -> isExpanded() ? openGraphic : closeGraphic,
				expandedProperty()
			));

		}

		/**
		 * Adds a new child representing a directory to this item.
		 *
		 * @param dir            The directory {@link Path}.
		 * @param graphicFactory The factory returning a suitable icon.
		 * @return The child {@link DirectoryItem}.
		 */
		public DirectoryItem<T> addChildDirectory( Path dir, TreeDirectoryModel.GraphicFactory graphicFactory ) {

			assert dir.getNameCount() == 1;

			int i = getDirectoryInsertionIndex(dir.toString());
			DirectoryItem<T> child = createDirectoryItem(inject(getPath().resolve(dir)), graphicFactory, getProjector(), getInjector());

			getChildren().add(i, child);

			return child;

		}

		/**
		 * Adds a new child representing a directory to this item.
		 *
		 * @param file           The file {@link Path}.
		 * @param lastModified   The file's last modification time.
		 * @param graphicFactory The factory returning a suitable icon.
		 * @return The child {@link FileItem}.
		 */
		public FileItem<T> addChildFile( Path file, FileTime lastModified, TreeDirectoryModel.GraphicFactory graphicFactory ) {

			assert file.getNameCount() == 1;

			int i = getFileInsertionIndex(file.toString());
			FileItem<T> child = createFileItem(inject(getPath().resolve(file)), lastModified, graphicFactory, getProjector());

			getChildren().add(i, child);

			return child;

		}

		/**
		 * @return A {@link Function} converting a {@link Path} into the object
		 *         used as value in the corresponding{@link TreeItem}.
		 */
		public final Function<Path, T> getInjector() {
			return injector;
		}

		/**
		 * Applies {@link #getInjector()} to the given {@link Path}.
		 *
		 * @param path The {@link Path} to be converted.
		 * @return The value obtained applying {@link #getInjector()} to the
		 *         given {@link Path}.
		 */
		public final T inject( Path path ) {
			return injector.apply(path);
		}

		@Override
		public final boolean isDirectory() {
			return true;
		}

		private int getDirectoryInsertionIndex( String dirName ) {

			ObservableList<TreeItem<T>> children = getChildren();
			int n = children.size();

			for ( int i = 0; i < n; ++i ) {

				PathItem<T> child = (PathItem<T>) children.get(i);

				if ( child.isDirectory() ) {

					String childName = child.getPath().getFileName().toString();

					if ( childName.compareToIgnoreCase(dirName) > 0 ) {
						return i;
					}

				} else {
					return i;
				}

			}

			return n;

		}

		private int getFileInsertionIndex( String fileName ) {

			ObservableList<TreeItem<T>> children = getChildren();
			int n = children.size();

			for ( int i = 0; i < n; ++i ) {

				PathItem<T> child = (PathItem<T>) children.get(i);

				if ( !child.isDirectory() ) {

					String childName = child.getPath().getFileName().toString();

					if ( childName.compareToIgnoreCase(fileName) > 0 ) {
						return i;
					}

				}

			}

			return n;

		}

	}

	/**
	 * A {@link TreeItem} representing a file.
	 *
	 * @param <T> Type of the object returned by {@link TreeItem#getValue()}.
	 */
	@SuppressWarnings( { "PackageVisibleInnerClass", "PublicInnerClass" } )
	public static class FileItem<T> extends PathItem<T> {

		private FileTime lastModified;

		protected FileItem( T path, FileTime lastModified, Node graphic, Function<T, Path> projector ) {

			super(path, graphic, projector);

			this.lastModified = lastModified;
		}

		/**
		 * @return The last modification time for the file associated with this
		 *         item.
		 */
		public final FileTime getLastModified() {
			return lastModified;
		}

		@Override
		public final boolean isDirectory() {
			return false;
		}

		/**
		 * Updates the modification time associated with the file path.
		 *
		 * @param lastModified The new modification time.
		 * @return {@code true} if the given value is greater than the current
		 *         one.
		 */
		public boolean updateModificationTime( FileTime lastModified ) {

			if ( lastModified.compareTo(this.lastModified) > 0 ) {

				this.lastModified = lastModified;

				return true;

			} else {
				return false;
			}

		}

	}

	/**
	 * An abstract class representing a {@link TreeItem} for a given {@link Path}.
	 *
	 * @param <T> Type of the object returned by {@link TreeItem#getValue()}.
	 */
	@SuppressWarnings( { "PackageVisibleInnerClass", "PublicInnerClass" } )
	public static abstract class PathItem<T> extends TreeItem<T> {

		private final Function<T, Path> projector;

		protected PathItem( T path, Function<T, Path> projector ) {

			super(path);

			this.projector = projector;

		}

		protected PathItem( T path, Node graphic, Function<T, Path> projector ) {

			super(path, graphic);

			this.projector = projector;

		}

		/**
		 * @return {@code (DirectoryItem<T>) this}.
		 */
		public DirectoryItem<T> asDirectoryItem() {
			return (DirectoryItem<T>) this;
		}

		/**
		 * @return {@code (FileItem<T>) this}.
		 */
		public FileItem<T> asFileItem() {
			return (FileItem<T>) this;
		}

		/**
		 * @return The {@link Path} obtained projecting the value of this
		 *         {@link TreeItem}.
		 */
		public final Path getPath() {
			return projector.apply(getValue());
		}

		/**
		 * @return The {@link Function} converting the object returned by
		 *         {@link TreeItem#getValue()}) into the corresponding
		 *         {@link Path}.
		 */
		public final Function<T, Path> getProjector() {
			return projector;
		}

		/**
		 * @param relativePath The relative {@link Path} for which the
		 *                     corresponding {@link PathItem} object must be
		 *                     returned.
		 * @return A {@link PathItem} object for the given relative {@link Path}.
		 */
		public PathItem<T> getRelativeChild( Path relativePath ) {

			assert relativePath.getNameCount() == 1;

			Path childValue = getPath().resolve(relativePath);

			for ( TreeItem<T> ch : getChildren() ) {

				PathItem<T> pathCh = (PathItem<T>) ch;

				if ( pathCh.getPath().equals(childValue) ) {
					return pathCh;
				}

			}

			return null;

		}

		/**
		 * @return {@code true} if this item represents a directory.
		 */
		public abstract boolean isDirectory();

//	TODO:CR To be removed?
//		@Override
//		public final boolean isLeaf() {
//			return !isDirectory();
//		}

		protected PathItem<T> resolve( Path relativePath ) {

			int len = relativePath.getNameCount();

			if ( len == 0 || "".equals(relativePath.toString()) ) {
				return this;
			} else {

				PathItem<T> child = getRelativeChild(relativePath.getName(0));

				if ( child == null ) {
					return null;
				} else if ( len == 1 ) {
					return child;
				} else {
					return child.resolve(relativePath.subpath(1, len));
				}
			}

		}

	}

	/**
	 * A {@link TreeItem} representing a top-level directory, i.e. the root
	 * directory for the tree model.
	 *
	 * @param <I> Type of the initiator of changes to the model.
	 * @param <T> Type of the object returned by {@link TreeItem#getValue()}.
	 */
	@SuppressWarnings( { "PackageVisibleInnerClass", "PublicInnerClass" } )
	public static class TopLevelDirectoryItem<I, T> extends DirectoryItem<T> {

		private final TreeDirectoryModel.GraphicFactory graphicFactory;
		private final DirectoryModel.Reporter<I> reporter;

		protected TopLevelDirectoryItem( T path, TreeDirectoryModel.GraphicFactory graphicFactory, Function<T, Path> projector, Function<Path, T> injector, DirectoryModel.Reporter<I> reporter ) {
			super(path, graphicFactory.createGraphic(projector.apply(path), true, false), graphicFactory.createGraphic(projector.apply(path), true, true), projector, injector);
			this.graphicFactory = graphicFactory;
			this.reporter = reporter;
		}

		/**
		 * Adds a directory to the model rooted at this item.
		 *
		 * @param relativePath The directory {@link Path}.
		 * @param initiator    The initiator of changes to the model.
		 */
		public void addDirectory( Path relativePath, I initiator ) {

			PathItem<T> item = resolve(relativePath);

			if ( item == null || !item.isDirectory() ) {
				sync(PathElement.directory(getPath().resolve(relativePath), Collections.emptyList()), initiator);
			}

		}

		/**
		 * Adds a file to the model rooted at this item.
		 *
		 * @param relativePath The file {@link Path}.
		 * @param lastModified The file's last modification time.
		 * @param initiator    The initiator of changes to the model.
		 */
		public void addFile( Path relativePath, FileTime lastModified, I initiator ) {
			updateFile(relativePath, lastModified, initiator);
		}

		/**
		 * @param relativePath The {@link Path} to be checked.
		 * @return {@code true} if the given {@link Path} is contained in the
		 *         model rooted at this item.
		 */
		public boolean contains( Path relativePath ) {
			return resolve(relativePath) != null;
		}

		/**
		 * Removes the given {@link Path} from the model rooted at this item.
		 *
		 * @param relativePath The {@link Path} to be removed from the model
		 *                     rooted at this item.
		 * @param initiator    The initiator of changes to the model.
		 */
		public void remove( Path relativePath, I initiator ) {

			PathItem<T> item = resolve(relativePath);

			if ( item != null ) {
				removeNode(item, initiator);
			}

		}

		/**
		 * Adds to the model rooted at this item all the subdirectories and
		 * files represented by the {@code tree} element.
		 *
 		 * @param tree      The element to be synchronized.
		 * @param initiator The initiator of changes to the model.
		 */
		public void sync( PathElement tree, I initiator ) {

			Path path = tree.getPath();
			Path relativePath = getPath().relativize(path);
			ParentChild<T> pc = resolveInParent(relativePath);
			DirectoryItem<T> parent = pc.getParent();
			PathItem<T> item = pc.getChild();

			if ( parent != null ) {
				syncChild(parent, relativePath.getFileName(), tree, initiator);
			} else if ( item == null ) {
				//	Neither path nor its parent present in model.
				report(new NoSuchElementException(MessageFormat.format("Parent directory for {0} does not exist within {1}.", relativePath, getValue())));
			} else {

				//	Resolved to top-level dir.
				assert item == this;

				if ( tree.isDirectory() ) {
					syncContent(this, tree, initiator);
				} else {
					report(new IllegalArgumentException(MessageFormat.format("Cannot replace top-level directory {0} with a file.", getValue())));
				}

			}

		}

		/**
		 * Updates the modification time for the item associated to the given
		 * {@link Path}.
		 *
		 * @param relativePath The path whose associated item must be updated.
		 * @param lastModified The new modification time.
		 * @param initiator    The initiator of changes to the model.
		 */
		public void updateModificationTime( Path relativePath, FileTime lastModified, I initiator ) {
			updateFile(relativePath, lastModified, initiator);
		}

		private void removeNode( TreeItem<T> node, I initiator ) {
			signalDeletionRecursively(node, initiator);
			node.getParent().getChildren().remove(node);
		}

		private void report( Throwable t ) {
			reporter.reportError(t);
		}

		private ParentChild<T> resolveInParent( Path relativePath ) {

			int len = relativePath.getNameCount();

			switch ( len ) {
				case 0:
					return new ParentChild<>(null, this);
				case 1:

					Path p = getPath();

					if ( p.resolve(relativePath).equals(p) ) {
						return new ParentChild<>(null, this);
					} else {
						return new ParentChild<>(this, getRelativeChild(relativePath.getName(0)));
					}
				default: {

					PathItem<T> parent = resolve(relativePath.subpath(0, len - 1));

					if ( parent == null || !parent.isDirectory() ) {
						return new ParentChild<>(null, null);
					} else {

						PathItem<T> child = parent.getRelativeChild(relativePath.getFileName());

						return new ParentChild<>(parent.asDirectoryItem(), child);

					}

				}
			}

		}

		private void signalDeletionRecursively( TreeItem<T> node, I initiator ) {

			node.getChildren().forEach(child -> signalDeletionRecursively(child, initiator));

			Path p = getPath();

			reporter.reportDeletion(p, p.relativize(getProjector().apply(node.getValue())), initiator);

		}

		private void syncChild( DirectoryItem<T> parent, Path childName, PathElement tree, I initiator ) {

			PathItem<T> child = parent.getRelativeChild(childName);

			if ( child != null && child.isDirectory() != tree.isDirectory() ) {
				removeNode(child, null);
			}

			if ( child == null ) {
				if ( tree.isDirectory() ) {

					DirectoryItem<T> directoryChild = parent.addChildDirectory(childName, graphicFactory);

					reporter.reportCreation(getPath(), getPath().relativize(directoryChild.getPath()), initiator);
					syncContent(directoryChild, tree, initiator);

				} else {

					FileItem<T> fileChild = parent.addChildFile(childName, tree.getLastModified(), graphicFactory);

					reporter.reportCreation(getPath(), getPath().relativize(fileChild.getPath()), initiator);

				}
			} else {
				if ( child.isDirectory() ) {
					syncContent(child.asDirectoryItem(), tree, initiator);
				} else {
					if ( child.asFileItem().updateModificationTime(tree.getLastModified()) ) {
						reporter.reportModification(getPath(), getPath().relativize(child.getPath()), initiator);
					}
				}
			}

		}

		private void syncContent( DirectoryItem<T> dir, PathElement tree, I initiator ) {

			Set<Path> desiredChildren = tree.getChildren().stream().map(element -> element.getPath()).collect(Collectors.toSet());
			ArrayList<TreeItem<T>> actualChildren = new ArrayList<>(dir.getChildren());

			//	Remove undesired children
			actualChildren.stream()
				.filter(child -> !desiredChildren.contains(getProjector().apply(child.getValue())))
				.forEachOrdered(child -> removeNode(child, null));

			//	Synchronize desired children
			tree.getChildren().forEach(child -> sync(child, initiator));

		}

		private void updateFile( Path relativePath, FileTime lastModified, I initiator ) {

			PathItem<T> item = resolve(relativePath);

			if ( item == null || !item.isDirectory() ) {
				sync(PathElement.file(getPath().resolve(relativePath), lastModified), initiator);
			}

		}

	}

	@SuppressWarnings( "PackageVisibleInnerClass" )
	static class ParentChild<T> {

		private final PathItem<T> child;
		private final DirectoryItem<T> parent;

		ParentChild( DirectoryItem<T> parent, PathItem<T> child ) {
			this.parent = parent;
			this.child = child;
		}

		public PathItem<T> getChild() {
			return child;
		}

		public DirectoryItem<T> getParent() {
			return parent;
		}

	}

}
