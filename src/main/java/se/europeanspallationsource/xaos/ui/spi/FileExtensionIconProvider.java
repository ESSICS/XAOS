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
package se.europeanspallationsource.xaos.ui.spi;


import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import javafx.scene.Node;
import se.europeanspallationsource.xaos.ui.CommonIcons;

import static se.europeanspallationsource.xaos.ui.CommonIcons.FILE;
import static se.europeanspallationsource.xaos.ui.CommonIcons.FOLDER;
import static se.europeanspallationsource.xaos.ui.CommonIcons.LINK;


/**
 * Provides icons (i.e. {@link Node}s) for a given file extension.
 *
 * @author claudio.rosati@esss.se
 */
public interface FileExtensionIconProvider {

	/**
	 * Return an icon (i.e. a {@link Node} for the given file extension. A file
	 * extension is string following the last '.' in the file's pathname.
	 *
	 * @param extension File extension for which a graphical representation is
	 *                  needed. Implementation should discard the first character
	 *                  if equal to '.'.
	 * @return An icon as a {@link Node} instance, or {@code null}.
	 */
	public Node iconFor( String extension );

	/**
	 * @param path The {@link Path} for which a graphical representation is
	 *             needed.
	 * @return {@code null} if the given {@link Path} is {@code null},
	 *         {@link CommonIcons#FOLDER} if the given path is a folder,
	 *         {@link CommonIcons#LINK} if it is a symbolic link,
	 *         {@link CommonIcons#FILE} if the given path is a file but it has
	 *         no extension, {@link #iconFor(String)} otherwise.
	 */
	default public Node iconFor( Path path ) {
		if ( path == null ) {
			return null;
		} else if ( Files.isDirectory(path) ) {
			return FOLDER.getIcon();
		} else if ( Files.isSymbolicLink(path) ) {
			return LINK.getIcon();
		} else {

			String name = path.getFileName().toString();
			int lastDotIndex = name.lastIndexOf('.');

			if ( lastDotIndex != -1 && lastDotIndex < name.length() - 1 ) {
				return iconFor(name.substring(lastDotIndex + 1));
			} else {
				return FILE.getIcon();
			}

		}
	}

	/**
	 * @param path The {@link File} for which a graphical representation is
	 *             needed.
	 * @return {@code null} if the given {@link File} is {@code null},
	 *         {@link CommonIcons#FOLDER} if the given path is a folder,
	 *         {@link CommonIcons#LINK} if it is a symbolic link,
	 *         {@link CommonIcons#FILE} if the given path is a file but it has
	 *         no extension, {@link #iconFor(String)} otherwise.
	 */
	default public Node iconFor( File path ) {
		if ( path == null ) {
			return null;
		} else if ( path.isDirectory() ) {
			return FOLDER.getIcon();
		} else if ( Files.isSymbolicLink(path.toPath()) ) {
			return LINK.getIcon();
		} else {

			String name = path.getName();
			int lastDotIndex = name.lastIndexOf('.');

			if ( lastDotIndex != -1 && lastDotIndex < name.length() - 1 ) {
				return iconFor(name.substring(lastDotIndex + 1));
			} else {
				return FILE.getIcon();
			}

		}
	}

}
