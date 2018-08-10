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
package se.europeanspallationsource.xaos.ui;


import java.io.File;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.ServiceLoader;
import javafx.scene.Node;
import org.apache.commons.lang3.StringUtils;
import se.europeanspallationsource.xaos.ui.spi.FileExtensionIconProvider;


/**
 * Provides methods to obtain specific icons.
 *
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class Icons {

	/**
	 * Calls {@link #iconForFileExtension(String)} for the provided {@link Path}.
	 *
	 * @param path The {@link Path} for which a graphical representation is
	 *             needed.
	 * @return An iconic {@link Node} representing the given {@link Path} object.
	 */
	public static Node iconFor( File path ) {

		if ( path != null ) {
			return iconForFileExtension(FileExtensionIconProvider.extensionFor(path));
		}

		return null;

	}

	/**
	 * Calls {@link #iconForFileExtension(String, Node)} for the provided
	 * {@link File} and default {@link Node}..
	 *
	 * @param file        The {@link File} for which a graphical representation
	 *                    is needed.
	 * @param defaultIcon The value to be returned if no provider was able to
	 *                    return a valid alternative.
	 * @return An iconic {@link Node} representing the given {@link Path} object.
	 */
	public static Node iconFor( File file, Node defaultIcon ) {

		if ( file != null ) {
			return iconForFileExtension(FileExtensionIconProvider.extensionFor(file), defaultIcon);
		}

		return null;

	}

	/**
	 * Calls {@link #iconForFileExtension(String)} for the provided {@link Path}.
	 *
	 * @param path The {@link Path} for which a graphical representation is
	 *             needed.
	 * @return An iconic {@link Node} representing the given {@link Path} object.
	 */
	public static Node iconFor( Path path ) {

		if ( path != null ) {
			return iconForFileExtension(FileExtensionIconProvider.extensionFor(path));
		}

		return null;

	}

	/**
	 * Calls {@link #iconForFileExtension(String, Node)} for the provided
	 * {@link Path} and default {@link Node}..
	 *
	 * @param path        The {@link Path} for which a graphical representation
	 *                    is needed.
	 * @param defaultIcon The value to be returned if no provider was able to
	 *                    return a valid alternative.
	 * @return An iconic {@link Node} representing the given {@link Path} object.
	 */
	public static Node iconFor( Path path, Node defaultIcon ) {

		if ( path != null ) {
			return iconForFileExtension(FileExtensionIconProvider.extensionFor(path), defaultIcon);
		}

		return null;

	}

	/**
	 * @param extension The file extension (without the preceding '.').
	 * @return An iconic {@link Node} representing a file with the given
	 *         {@code extension}.
	 */
	public static Node iconForFileExtension( String extension ) {

		if ( StringUtils.isNotBlank(extension) ) {

			Iterator<FileExtensionIconProvider> iterator = ServiceLoader.load(FileExtensionIconProvider.class).iterator();

			while ( iterator.hasNext() ) {

				FileExtensionIconProvider provider = iterator.next();
				Node icon = provider.iconFor(extension);

				if ( icon != null ) {
					return icon;
				}

			}

		}

		return null;

	}

	/**
	 * @param extension   The file extension (without the preceding '.').
	 * @param defaultIcon The value to be returned if no provider was able to
	 *                    return a valid alternative.
	 * @return An iconic {@link Node} representing a file with the given
	 *         {@code extension}.
	 */
	public static Node iconForFileExtension( String extension, Node defaultIcon ) {

		Node icon = iconForFileExtension(extension);

		return ( icon != null ) ? icon : defaultIcon;

	}

	private Icons() {
	}

}
