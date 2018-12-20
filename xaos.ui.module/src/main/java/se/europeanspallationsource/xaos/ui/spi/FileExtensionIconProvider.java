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
package se.europeanspallationsource.xaos.ui.spi;


import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import javafx.scene.Node;


/**
 * Provides icons (i.e. square {@link Node}s) for a given file extension.
 *
 * @author claudio.rosati@esss.se
 */
public interface FileExtensionIconProvider {

	/**
	 * Return an icon (i.e. a square {@link Node} for the given file extension.
	 * A file extension is string following the last '.' in the file's pathname.
	 *
	 * @param extension File extension for which a graphical representation is
	 *                  needed. Implementation should discard the first character
	 *                  if equal to '.'. Can be {@code null}.
	 * @param size      The size of the square {@code Node} to be returned.
	 * @return An icon as a {@link Node} instance, or {@code null} if
	 *         {@code extension} is {@code null} or empty, {@code size <= 0}, or no
	 *         suitable representation can be found.
	 */
	public Node iconFor( String extension, int size );

	/**
	 * @param path The {@link Path} for which the extension must be returned.
	 * @return The extension for the given {@link Path}, or {@code null} if it
	 *         is {@code null}, is not a file or it has no extension.
	 */
	static public String extensionFor( Path path ) {

		if ( path != null && !Files.isDirectory(path) && !Files.isSymbolicLink(path) ) {

			String name = path.getFileName().toString();
			int lastDotIndex = name.lastIndexOf('.');

			if ( lastDotIndex != -1 && lastDotIndex < name.length() - 1 ) {
				return name.substring(lastDotIndex + 1);
			}

		}

		return null;

	}

	/**
	 * @param file The {@link File} for which the extension must be returned.
	 * @return {@link #extensionFor(java.nio.file.Path)} calling
	 *         {@link File#toPath()} on the given parameter.
	 */
	static public String extensionFor( File file ) {

		if ( file != null ) {
			return extensionFor(file.toPath());
		}

		return null;

	}

}
