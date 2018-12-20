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
	 * The default icon size to used in menus and toolbars
	 */
	public static final int DEFAULT_SIZE = 17;

	/**
	 * Calls {@link #iconForClass(String, int)} for the provided {@link Class}.
	 *
	 * @param clazz The {@link Class} for which a graphical representation is
	 *              needed.
	 * @param size  The size of the square {@code Node} to be returned.
	 * @return An iconic {@link Node} representing the given {@link Class} object,
	 *         or {@code null}.
	 */
	public static Node iconFor( Class<?> clazz, int size ) {

		if ( clazz != null ) {
			return iconForClass(clazz.getName(), size);
		}

		return null;

	}

	/**
	 * Calls {@link #iconForClass(String, int, Node)} for the provided {@link Class}
	 * and default {@link Node}..
	 *
	 * @param clazz       The {@link Class} for which a graphical representation
	 *                    is needed.
	 * @param size        The size of the square {@code Node} to be returned.
	 * @param defaultIcon The value to be returned if no provider was able to
	 *                    return a valid alternative.
	 * @return An iconic {@link Node} representing the given {@link Class} object,
	 *         or {@code null}.
	 */
	public static Node iconFor( Class<?> clazz, int size, Node defaultIcon ) {

		if ( clazz != null ) {
			return iconForClass(clazz.getName(), size, defaultIcon);
		}

		return null;

	}

	/**
	 * Calls {@link #iconForFileExtension(String, int)} for the provided {@link Path}.
	 *
	 * @param file The {@link File} for which a graphical representation is
	 *             needed.
	 * @param size The size of the square {@code Node} to be returned.
	 * @return An iconic {@link Node} representing the given {@link Path} object,
	 *         or {@code null}. {@code null} is also returned if {@code file} is
	 *         {@code null}, or {@code size <= 0}.
	 */
	public static Node iconFor( File file, int size ) {

		if ( file != null ) {
			return iconForFileExtension(FileExtensionIconProvider.extensionFor(file), size);
		}

		return null;

	}

	/**
	 * Calls {@link #iconForFileExtension(String, int, Node)} for the provided
	 * {@link File} and default {@link Node}..
	 *
	 * @param file        The {@link File} for which a graphical representation
	 *                    is needed.
	 * @param size        The size of the square {@code Node} to be returned.
	 * @param defaultIcon The value to be returned if no provider was able to
	 *                    return a valid alternative.
	 * @return An iconic {@link Node} representing the given {@link Path} object,
	 *         or {@code null}.
	 */
	public static Node iconFor( File file, int size, Node defaultIcon ) {

		if ( file != null ) {
			return iconForFileExtension(FileExtensionIconProvider.extensionFor(file), size, defaultIcon);
		}

		return null;

	}

	/**
	 * @param object The object for which a graphical representation is needed.
	 * @param size   The size of the square {@code Node} to be returned.
	 * @return An iconic {@link Node} representing the given {@code object} at the
	 *         given {@code size}, or {@code null}. {@code null} is also returned
	 *         if {@code object} is {@code null}, or {@code size <= 0}.
	 */
	public static Node iconFor( Object object, int size ) {

		if ( Objects.nonNull(object) && size > 0 ) {

			Iterator<IconProvider> iterator = ServiceLoader.load(IconProvider.class).iterator();

			while ( iterator.hasNext() ) {

				IconProvider provider = iterator.next();
				Node icon = provider.iconFor(object, size);

				if ( icon != null ) {
					return icon;
				}

			}

		}

		return null;

	}

	/**
	 * @param object      The object for which a graphical representation is needed.
	 * @param size        The size of the square {@code Node} to be returned.
	 * @param defaultIcon The value to be returned if no provider was able to
	 *                    return a valid alternative.
	 * @return An iconic {@link Node} representing the given {@code object} at the
	 *         given {@code size}.
	 */
	public static Node iconFor( Object object, int size, Node defaultIcon ) {

		Node icon = iconFor(object, size);

		return ( icon != null ) ? icon : defaultIcon;

	}

	/**
	 * Calls {@link #iconForFileExtension(String, int)} for the provided {@link Path}.
	 *
	 * @param path The {@link Path} for which a graphical representation is
	 *             needed.
	 * @param size The size of the square {@code Node} to be returned.
	 * @return An iconic {@link Node} representing the given {@link Path} object,
	 *         or {@code null}. {@code null} is also returned if {@code path} is
	 *         {@code null}, or {@code size <= 0}.
	 */
	public static Node iconFor( Path path, int size ) {

		if ( path != null ) {
			return iconForFileExtension(FileExtensionIconProvider.extensionFor(path), size);
		}

		return null;

	}

	/**
	 * Calls {@link #iconForFileExtension(String, int, Node)} for the provided
	 * {@link Path} and default {@link Node}..
	 *
	 * @param path        The {@link Path} for which a graphical representation
	 *                    is needed.
	 * @param size        The size of the square {@code Node} to be returned.
	 * @param defaultIcon The value to be returned if no provider was able to
	 *                    return a valid alternative.
	 * @return An iconic {@link Node} representing the given {@link Path} object,
	 *         or {@code null}.
	 */
	public static Node iconFor( Path path, int size, Node defaultIcon ) {

		if ( path != null ) {
			return iconForFileExtension(FileExtensionIconProvider.extensionFor(path), size, defaultIcon);
		}

		return null;

	}

	/**
	 * @param clazz The full class name (the one returned by {@link Class#getName()})
	 *              for which a graphical representation is needed.
	 * @param size  The size of the square {@code Node} to be returned.
	 * @return An iconic {@link Node} representing {@link Class} with the given
	 *         {@code name}, or {@code null}. {@code null} is also returned if
	 *         {@code clazz} is {@code null}, or {@code size <= 0}.
	 */
	public static Node iconForClass( String clazz, int size ) {

		if ( StringUtils.isNotBlank(clazz) && size > 0 ) {

			Iterator<ClassIconProvider> iterator = ServiceLoader.load(ClassIconProvider.class).iterator();

			while ( iterator.hasNext() ) {

				ClassIconProvider provider = iterator.next();
				Node icon = provider.iconFor(clazz, size);

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
	 * @param size        The size of the square {@code Node} to be returned.
	 * @param defaultIcon The value to be returned if no provider was able to
	 *                    return a valid alternative.
	 * @return An iconic {@link Node} representing {@link Class} with the given
	 *         {@code name}, or {@code null}.
	 */
	public static Node iconForClass( String clazz, int size, Node defaultIcon ) {

		Node icon = iconForClass(clazz, size);

		return ( icon != null ) ? icon : defaultIcon;

	}

	/**
	 * @param extension The file extension (without the preceding '.').
	 * @param size      The size of the square {@code Node} to be returned.
	 * @return An iconic {@link Node} representing a file with the given
	 *         {@code extension} at the given {@code size}, or {@code null}.
	 *         {@code null} is also returned if {@code extension} is {@code null}
	 *         or empty, or {@code size <= 0}.
	 */
	public static Node iconForFileExtension( String extension, int size ) {

		if ( StringUtils.isNotBlank(extension) && size > 0 ) {

			Iterator<FileExtensionIconProvider> iterator = ServiceLoader.load(FileExtensionIconProvider.class).iterator();

			while ( iterator.hasNext() ) {

				FileExtensionIconProvider provider = iterator.next();
				Node icon = provider.iconFor(extension, size);

				if ( icon != null ) {
					return icon;
				}

			}

		}

		return null;

	}

	/**
	 * @param extension   The file extension (without the preceding '.').
	 * @param size        The size of the square {@code Node} to be returned.
	 * @param defaultIcon The value to be returned if no provider was able to
	 *                    return a valid alternative.
	 * @return An iconic {@link Node} representing a file with the given
	 *         {@code extension}, or {@code null}.
	 */
	public static Node iconForFileExtension( String extension, int size, Node defaultIcon ) {

		Node icon = iconForFileExtension(extension, size);

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
