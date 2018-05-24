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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import se.europeanspallationsource.xaos.tools.io.DirectoryModel;
import se.europeanspallationsource.xaos.tools.io.PathElement;


/**
 * Containing the classes used by {@link TreeDirectoryModel}.
 *
 * @author claudio.rosati@esss.se
 * @see <a href="https://github.com/ESSICS/LiveDirsFX">LiveDirsFX:org.fxmisc.livedirs.PathItem</a>
 */
@SuppressWarnings( "ClassWithoutLogger" )
class TreeItems {

	/**
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
	static <T> DirectoryItem<T> createDirectoryItem( T path, TreeDirectoryModel.GraphicFactory graphicFactory, Function<T, Path> projector, Function<Path, T> injector ) {
		return new DirectoryItem<>(path, graphicFactory.createGraphic(projector.apply(path), true), projector, injector);
	}

	/**
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
	static <T> FileItem<T> createFileItem( T path, FileTime lastModified, TreeDirectoryModel.GraphicFactory graphicFactory, Function<T, Path> projector ) {
		return new FileItem<>(path, lastModified, graphicFactory.createGraphic(projector.apply(path), false), projector);
	}

	/**
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
	static <I, T> TopLevelDirectoryItem<I, T> createTopLevelDirectoryItem( T path, TreeDirectoryModel.GraphicFactory graphicFactory, Function<T, Path> projector, Function<Path, T> injector, DirectoryModel.Reporter<I> reporter ) {
		return new TopLevelDirectoryItem<>(path, graphicFactory, projector, injector, reporter);
	}

	private TreeItems() {
	}

	@SuppressWarnings( "PackageVisibleInnerClass" )
	static class DirectoryItem<T> extends PathItem<T> {

		private final Function<Path, T> injector;

		protected DirectoryItem( T path, Node graphic, Function<T, Path> projector, Function<Path, T> injector ) {

			super(path, graphic, projector);

			this.injector = injector;

		}

		DirectoryItem<T> addChildDirectory( Path dirName, TreeDirectoryModel.GraphicFactory graphicFactory ) {

			assert dirName.getNameCount() == 1;

			int i = getDirectoryInsertionIndex(dirName.toString());
			DirectoryItem<T> child = createDirectoryItem(inject(getPath().resolve(dirName)), graphicFactory, getProjector(), getInjector());

			getChildren().add(i, child);

			return child;

		}

		FileItem<T> addChildFile( Path fileName, FileTime lastModified, TreeDirectoryModel.GraphicFactory graphicFactory ) {

			assert fileName.getNameCount() == 1;

			int i = getFileInsertionIndex(fileName.toString());
			FileItem<T> child = createFileItem(inject(getPath().resolve(fileName)), lastModified, graphicFactory, getProjector());

			getChildren().add(i, child);

			return child;

		}

		final T inject( Path path ) {
			return injector.apply(path);
		}

		@Override
		final boolean isDirectory() {
			return true;
		}

		protected final Function<Path, T> getInjector() {
			return injector;
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

	@SuppressWarnings( "PackageVisibleInnerClass" )
	static class FileItem<T> extends PathItem<T> {

		private FileTime lastModified;

		protected FileItem( T path, FileTime lastModified, Node graphic, Function<T, Path> projector ) {

			super(path, graphic, projector);

			this.lastModified = lastModified;
		}

		@Override
		final boolean isDirectory() {
			return false;
		}

		boolean updateModificationTime( FileTime lastModified ) {

			if ( lastModified.compareTo(this.lastModified) > 0 ) {

				this.lastModified = lastModified;

				return true;

			} else {
				return false;
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

		PathItem<T> getChild() {
			return child;
		}

		DirectoryItem<T> getParent() {
			return parent;
		}

	}

	@SuppressWarnings( "PackageVisibleInnerClass" )
	static abstract class PathItem<T> extends TreeItem<T> {

		private final Function<T, Path> projector;

		protected PathItem( T path, Node graphic, Function<T, Path> projector ) {

			super(path, graphic);

			this.projector = projector;

		}

		@Override
		public final boolean isLeaf() {
			return !isDirectory();
		}

		DirectoryItem<T> asDirectoryItem() {
			return (DirectoryItem<T>) this;
		}

		FileItem<T> asFileItem() {
			return (FileItem<T>) this;
		}

		final Path getPath() {
			return projector.apply(getValue());
		}

		PathItem<T> getRelativeChild( Path relativePath ) {

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

		abstract boolean isDirectory();

		protected final Function<T, Path> getProjector() {
			return projector;
		}

		protected PathItem<T> resolve( Path relativePath ) {

			int len = relativePath.getNameCount();

			if ( len == 0 ) {
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

	@SuppressWarnings( "PackageVisibleInnerClass" )
	static class TopLevelDirectoryItem<I, T> extends DirectoryItem<T> {

		private final TreeDirectoryModel.GraphicFactory graphicFactory;
		private final DirectoryModel.Reporter<I> reporter;

		protected TopLevelDirectoryItem( T path, TreeDirectoryModel.GraphicFactory graphicFactory, Function<T, Path> projector, Function<Path, T> injector, DirectoryModel.Reporter<I> reporter ) {
			super(path, graphicFactory.createGraphic(projector.apply(path), true), projector, injector);
			this.graphicFactory = graphicFactory;
			this.reporter = reporter;
		}

		public void addDirectory( Path relativePath, I initiator ) {

			PathItem<T> item = resolve(relativePath);

			if ( item == null || !item.isDirectory() ) {
				sync(PathElement.directory(getPath().resolve(relativePath), Collections.emptyList()), initiator);
			}

		}

		void addFile( Path relativePath, FileTime lastModified, I initiator ) {
			updateFile(relativePath, lastModified, initiator);
		}

		boolean contains( Path relativePath ) {
			return resolve(relativePath) != null;
		}

		void remove( Path relativePath, I initiator ) {

			PathItem<T> item = resolve(relativePath);

			if ( item != null ) {
				removeNode(item, initiator);
			}

		}

		void sync( PathElement tree, I initiator ) {

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

		void updateModificationTime( Path relativePath, FileTime lastModified, I initiator ) {
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
					if ( getPath().resolve(relativePath).equals(getValue()) ) {
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
			node.getChildren().forEach(child ->  signalDeletionRecursively(child, initiator));
			reporter.reportDeletion(getPath(), getPath().relativize(getProjector().apply(node.getValue())), initiator);
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

			if ( item == null || item.isDirectory() ) {
				sync(PathElement.file(getPath().resolve(relativePath), lastModified), initiator);
			}

		}

	}

}
