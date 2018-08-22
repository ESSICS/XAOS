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
package se.europeanspallationsource.xaos.ui.control;


import java.io.File;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Objects;
import java.util.ServiceLoader;
import javafx.scene.Node;
import org.apache.commons.lang3.StringUtils;
import se.europeanspallationsource.xaos.ui.spi.ClassIconProvider;
import se.europeanspallationsource.xaos.ui.spi.FileExtensionIconProvider;
import se.europeanspallationsource.xaos.ui.spi.IconProvider;
import se.europeanspallationsource.xaos.ui.spi.MIMETypeIconProvider;


/**
 * Provides methods to obtain specific icons.
 *
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class Icons {

	/**
	 * Calls {@link #iconForClass(String)} for the provided {@link Class}.
	 *
	 * @param clazz The {@link Class} for which a graphical representation is
	 *              needed.
	 * @return An iconic {@link Node} representing the given {@link Class} object,
	 *         or {@code null}.
	 */
	public static Node iconFor( Class<?> clazz ) {

		if ( clazz != null ) {
			return iconForClass(clazz.getName());
		}

		return null;

	}

	/**
	 * Calls {@link #iconForClass(String, Node)} for the provided {@link Class}
	 * and default {@link Node}..
	 *
	 * @param clazz       The {@link Class} for which a graphical representation
	 *                    is needed.
	 * @param defaultIcon The value to be returned if no provider was able to
	 *                    return a valid alternative.
	 * @return An iconic {@link Node} representing the given {@link Class} object,
	 *         or {@code null}.
	 */
	public static Node iconFor( Class<?> clazz, Node defaultIcon ) {

		if ( clazz != null ) {
			return iconForClass(clazz.getName(), defaultIcon);
		}

		return null;

	}

	/**
	 * Calls {@link #iconForFileExtension(String)} for the provided {@link Path}.
	 *
	 * @param path The {@link Path} for which a graphical representation is
	 *             needed.
	 * @return An iconic {@link Node} representing the given {@link Path} object,
	 *         or {@code null}.
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
	 * @return An iconic {@link Node} representing the given {@link Path} object,
	 *         or {@code null}.
	 */
	public static Node iconFor( File file, Node defaultIcon ) {

		if ( file != null ) {
			return iconForFileExtension(FileExtensionIconProvider.extensionFor(file), defaultIcon);
		}

		return null;

	}

	/**
	 * @param object The object for which a graphical representation is needed.
	 * @return An iconic {@link Node} representing given object, or {@code null}.
	 */
	public static Node iconFor( Object object ) {

		if ( Objects.nonNull(object) ) {

			Iterator<IconProvider> iterator = ServiceLoader.load(IconProvider.class).iterator();

			while ( iterator.hasNext() ) {

				IconProvider provider = iterator.next();
				Node icon = provider.iconFor(object);

				if ( icon != null ) {
					return icon;
				}

			}

		}

		return null;

	}

	/**
	 * @param object      The object for which a graphical representation is needed.
	 * @param defaultIcon The value to be returned if no provider was able to
	 *                    return a valid alternative.
	 * @return An iconic {@link Node} representing given object, or {@code null}.
	 */
	public static Node iconFor( Object object, Node defaultIcon ) {

		Node icon = iconFor(object);

		return ( icon != null ) ? icon : defaultIcon;

	}

	/**
	 * Calls {@link #iconForFileExtension(String)} for the provided {@link Path}.
	 *
	 * @param path The {@link Path} for which a graphical representation is
	 *             needed.
	 * @return An iconic {@link Node} representing the given {@link Path} object,
	 *         or {@code null}.
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
	 * @return An iconic {@link Node} representing the given {@link Path} object,
	 *         or {@code null}.
	 */
	public static Node iconFor( Path path, Node defaultIcon ) {

		if ( path != null ) {
			return iconForFileExtension(FileExtensionIconProvider.extensionFor(path), defaultIcon);
		}

		return null;

	}

	/**
	 * @param clazz The full class name (the one returned by {@link Class#getName()})
	 *              for which a graphical representation is needed.
	 * @return An iconic {@link Node} representing {@link Class} with the given
	 *         {@code name}, or {@code null}.
	 */
	public static Node iconForClass( String clazz ) {

		if ( StringUtils.isNotBlank(clazz) ) {

			Iterator<ClassIconProvider> iterator = ServiceLoader.load(ClassIconProvider.class).iterator();

			while ( iterator.hasNext() ) {

				ClassIconProvider provider = iterator.next();
				Node icon = provider.iconFor(clazz);

				if ( icon != null ) {
					return icon;
				}

			}

		}

		return null;

	}

	/**
	 * @param clazz       The full class name (the one returned by {@link Class#getName()})
	 *                    for which a graphical representation is needed.
	 * @param defaultIcon The value to be returned if no provider was able to
	 *                    return a valid alternative.
	 * @return An iconic {@link Node} representing {@link Class} with the given
	 *         {@code name}, or {@code null}.
	 */
	public static Node iconForClass( String clazz, Node defaultIcon ) {

		Node icon = iconForClass(clazz);

		return ( icon != null ) ? icon : defaultIcon;

	}

	/**
	 * @param extension The file extension (without the preceding '.').
	 * @return An iconic {@link Node} representing a file with the given
	 *         {@code extension}, or {@code null}.
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
	 *         {@code extension}, or {@code null}.
	 */
	public static Node iconForFileExtension( String extension, Node defaultIcon ) {

		Node icon = iconForFileExtension(extension);

		return ( icon != null ) ? icon : defaultIcon;

	}

	/**
	 * @param mime The MIME type for which a graphical representation is needed.
	 * @return An iconic {@link Node} representing the given {@code mime} type,
	 *         or {@code null}.
	 */
	public static Node iconForMIMEType( String mime ) {

		if ( StringUtils.isNotBlank(mime) ) {

			Iterator<MIMETypeIconProvider> iterator = ServiceLoader.load(MIMETypeIconProvider.class).iterator();

			while ( iterator.hasNext() ) {

				MIMETypeIconProvider provider = iterator.next();
				Node icon = provider.iconFor(mime);

				if ( icon != null ) {
					return icon;
				}

			}

		}

		return null;

	}

	/**
	 * @param mime        The MIME type for which a graphical representation is
	 *                    needed.
	 * @param defaultIcon The value to be returned if no provider was able to
	 *                    return a valid alternative.
	 * @return An iconic {@link Node} representing the given {@code mime} type,
	 *         or {@code null}.
	 */
	public static Node iconForMIMEType( String mime, Node defaultIcon ) {

		Node icon = iconForMIMEType(mime);

		return ( icon != null ) ? icon : defaultIcon;

	}

	private Icons() {
	}

}
