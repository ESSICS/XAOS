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
package se.europeanspallationsource.xaos.components;


import java.nio.file.Path;
import java.util.function.BiFunction;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


/**
 * Observable model of multiple directory trees.
 *
 * @param <I> Type of the initiator of changes to the model.
 * @param <T> Type of the object returned by {@link TreeItem#getValue()}.
 * @author claudio.rosati@esss.se
 * @see <a href="https://github.com/ESSICS/LiveDirsFX">LiveDirsFX</a>
 */
public interface DirectoryModel<I, T> {

	/**
	 * Graphic factory that always returns {@code null}.
	 */
	final GraphicFactory NO_GRAPHIC_FACTORY = ( path, isDirectory ) -> null;

	/**
	 * Graphic factory that returns a folder icon for a directory and
	 * a document icon for a regular file.
	 */
	final GraphicFactory DEFAULT_GRAPHIC_FACTORY = new DefaultGraphicFactory();

	/**
	 * Types of updates to the director model.
	 */
	@SuppressWarnings( "PublicInnerClass" )
	enum UpdateType {

		/**
		 * Indicates a new file/directory entry.
		 */
		CREATION,

		/**
		 * Indicates removal of a file/directory entry.
		 */
		DELETION,

		/**
		 * Indicates file modification.
		 */
		MODIFICATION,

	}

	/**
	 * Default graphic factory returning a folder icon for a directory and
     * a document icon for a regular file.
	 */
	@SuppressWarnings( "PublicInnerClass" )
	class DefaultGraphicFactory implements GraphicFactory {

//	TODO:CR Usare FontawesomeFX
		private static final Image FOLDER_IMAGE = new Image(DefaultGraphicFactory.class.getResource("folder-16.png").toString());
		private static final Image FILE_IMAGE = new Image(DefaultGraphicFactory.class.getResource("file-16.png").toString());

		@Override
		public Node createGraphic( Path path, boolean isDirectory ) {
			return isDirectory ? new ImageView(FOLDER_IMAGE) : new ImageView(FILE_IMAGE);
		}
	}

	/**
	 * Factory to create graphics for {@link TreeItem}s in a
	 * {@link DirectoryModel}.
	 */
	@FunctionalInterface
	@SuppressWarnings( "PublicInnerClass" )
	interface GraphicFactory extends BiFunction<Path, Boolean, Node> {

		Node createGraphic( Path path, boolean isDirectory );

		@Override
		default Node apply( Path path, Boolean isDirectory ) {
			return createGraphic(path, isDirectory);
		}

	}

	/**
	 * Represents an update to the directory model.
	 *
	 * @param <I> Type of the initiator of changes to the model.
	 */
	@SuppressWarnings( "PublicInnerClass" )
	class Update<I> {

		/**
		 * Utility method to create an {@link Update} instance whose type is
		 * {@link UpdateType#CREATION}.
		 *
		 * @param <I>          Type of the initiator of changes to the model.
		 * @param baseDir      Base directory of the update.
		 * @param relativePath Path relative to {@code baseDir} of the created
		 *                     element.
		 * @param initiator    The initiator of changes to the model.
		 * @return A newly created {@link Update} instance.
		 */
		public static <I> Update<I> creation( Path baseDir, Path relativePath, I initiator ) {
			return new Update<>(baseDir, relativePath, initiator, UpdateType.CREATION);
		}

		/**
		 * Utility method to create an {@link Update} instance whose type is
		 * {@link UpdateType#DELETION}.
		 *
		 * @param <I>          Type of the initiator of changes to the model.
		 * @param baseDir      Base directory of the update.
		 * @param relativePath Path relative to {@code baseDir} of the deleted
		 *                     element.
		 * @param initiator    The initiator of changes to the model.
		 * @return A newly created {@link Update} instance.
		 */
		public static <I> Update<I> deletion( Path baseDir, Path relativePath, I initiator ) {
			return new Update<>(baseDir, relativePath, initiator, UpdateType.DELETION);
		}

		/**
		 * Utility method to create an {@link Update} instance whose type is
		 * {@link UpdateType#MODIFICATION}.
		 *
		 * @param <I>          Type of the initiator of changes to the model.
		 * @param baseDir      Base directory of the update.
		 * @param relativePath Path relative to {@code baseDir} of the modified
		 *                     element.
		 * @param initiator    The initiator of changes to the model.
		 * @return A newly created {@link Update} instance.
		 */
		public static <I> Update<I> modification( Path baseDir, Path relativePath, I initiator ) {
			return new Update<>(baseDir, relativePath, initiator, UpdateType.MODIFICATION);
		}

		private final Path baseDir;
		private final I initiator;
		private final Path relativePath;
		private final UpdateType type;

		private Update( Path baseDir, Path relativePath, I initiator, UpdateType type ) {
			this.baseDir = baseDir;
			this.relativePath = relativePath;
			this.initiator = initiator;
			this.type = type;
		}

		/**
		 * @return The base directory of the update.
		 */
		public Path getBaseDir() {
			return baseDir;
		}

		/**
		 * @return The initiator of changes to the model.
		 */
		public I getInitiator() {
			return initiator;
		}

		/**
		 * @return The resolved path of the updated element. This is equivalent
		 *         of calling {@code getBaseDir().resolve(getRelativePath())}.
		 */
		public Path getPath() {
			return baseDir.resolve(relativePath);
		}

		/**
		 * @return The path relative to {@link #getBaseDir()} of the updated
		 *         element.
		 */
		public Path getRelativePath() {
			return relativePath;
		}

		/**
		 * @return The type of the update.
		 */
		public UpdateType getType() {
			return type;
		}

	}

}
