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
package se.europeanspallationsource.xaos.tools.svg;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLStreamException;


/**
 * SVGLoader is a class for loading SVG file.
 *
 * <pre>
 * URL url = ...;
 * SVGContent content SVGLoader.load(url);
 *
 * container.getChildren.add(content);</pre>
 *
 * @author claudio.rosati@esss.se
 * @see <a href="https://github.com/skrb/SVGLoader">SVGLoader</a>
 */
public class SVGLoader {

	private static final Logger LOGGER = Logger.getLogger(SVGLoader.class.getName());

	/**
	 * Load SVG file and convert it to JavaFX.
	 *
	 * @param url The location of the SVG file.
	 * @return An {@link SVGContent} object.
	 * @throws MalformedURLException If {@code url} cannot be resolved to a
	 *                               valid {@link URL}.
	 * @throws IOException           If the SVG file specified by {@code url}
	 *                               cannot be read.
	 * @throws XMLStreamException    If the resource specified by {@code url} is
	 *                               not a proper SVG file.
	 * @throws NullPointerException  If {@code url} is null.
	 */
	public static SVGContent load( String url ) throws MalformedURLException, IOException, XMLStreamException {
		return load(new URL(url));
	}

	/**
	 * Load SVG file and convert it to JavaFX.
	 *
	 * @param url The location of the SVG file.
	 * @return An {@link SVGContent} object.
	 * @throws IOException          If the SVG file specified by {@code url}
	 *                              cannot be read.
	 * @throws XMLStreamException   If the resource specified by {@code url} is
	 *                              not a proper SVG file.
	 * @throws NullPointerException If {@code url} is null.
	 */
	public static SVGContent load( URL url ) throws IOException, XMLStreamException {

		if ( url == null ) {
			throw new NullPointerException("url");
		}

		SVGContent root = null;
		SVGContentBuilder builder = new SVGContentBuilder(url);

		try {
			root = builder.build();
		} catch ( IOException | XMLStreamException ex ) {
			LOGGER.log(Level.SEVERE, MessageFormat.format("Unable to load SVG from URL: {0}", url.toExternalForm()), ex);
		}

		return root;

	}

	private SVGLoader() {
		//	Nothing to do.
	}

}
