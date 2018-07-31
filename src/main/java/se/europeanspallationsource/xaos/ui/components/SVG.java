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
package se.europeanspallationsource.xaos.ui.components;


import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import javax.xml.stream.XMLStreamException;


/**
 * SVGContent represents SVG content.
 *
 * <p>
 * {@code SVGContent} is a {@link Group}, therefore is able to be added to any
 * scene graph:
 * </p>
 *
 * <pre>
 * URL url = ...;
 * SVG svg = SVG.load(url);
 *
 * container.getChildren.add(svg);</pre>
 *
 * <p>
 * The {@link #getNode(String)} method returns the {@link Node} object represented
 * by the given identifier. When loading the following SVG file, a {@link Rectangle}
 * object is returned by {@link #getNode(String)} method for the "rect" identifier.
 * </p>
 *
 * <pre>
 * &lt;?xml version="1.0" encoding="iso-8859-1"?&gt;
 * &lt;!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd"&gt;
 * &lt;svg version="1.1" id="layer_1"
 *                    xmlns="http://www.w3.org/2000/svg"
 *                    xmlns:xlink="http://www.w3.org/1999/xlink"
 *                    x="0px" y="0px" width="300px" height="200px"
 *                    viewBox="0 0 300 200"
 *                    style="enable-background:new 0 0 300 200;"
 *                    xml:space="preserve"&gt;
 *   &lt;rect id="rect"
 *         x="100" y="50"
 *         width="100" height="80"
 *         style="fill:#FFFFFF; stroke:#000000;"/&gt;
 * &lt;/svg&gt;</pre>
 *
 * <p>
 * The {@link #getGroup(String)} method returns the {@link Group} object
 * represented by the given identifier. When loading the following SVG file, a
 * {@link Group} object is returned by {@link #getGroup(String)} method for the
 * "group" identifier.
 * </p>
 *
 * <pre>
 * &lt;?xml version="1.0" encoding="iso-8859-1"?&gt;
 * &lt;!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd"&gt;
 * &lt;svg version="1.1" xmlns="http://www.w3.org/2000/svg"
 *                    xmlns:xlink="http://www.w3.org/1999/xlink"
 *                    x="0px" y="0px" width="200px" height="200px" viewBox="0 0 200 200"
 *                    style="enable-background:new 0 0 200 200;" xml:space="preserve"&gt;
 *   &lt;g id="group"&gt;
 *     &lt;circle style="fill:#FF0000;stroke:#000000;" cx="100" cy="100" r="50"/&gt;
 *   &lt;/g&gt;
 * &lt;/svg&gt;</pre>
 *
 * <p>
 * <b>Note:</b> There are many SVG elements not yet (fully) supported.
 * </p>
 *
 * @author claudio.rosati@esss.se
 * @see <a href="https://github.com/skrb/SVGLoader">SVGLoader</a>
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class SVG extends Group {

	/**
	 * Load SVG file and convert it to JavaFX.
	 *
	 * @param url The location of the SVG file.
	 * @return An {@link SVG} object.
	 * @throws MalformedURLException If {@code url} cannot be resolved to a
	 *                               valid {@link URL}.
	 * @throws IOException           If the SVG file specified by {@code url}
	 *                               cannot be read.
	 * @throws XMLStreamException    If the resource specified by {@code url} is
	 *                               not a proper SVG file.
	 * @throws NullPointerException  If {@code url} is null.
	 */
	public static SVG load( String url ) throws MalformedURLException, IOException, XMLStreamException {
		return load(new URL(url));
	}

	/**
	 * Load SVG file and convert it to JavaFX.
	 *
	 * @param url The location of the SVG file.
	 * @return An {@link SVG} object.
	 * @throws IOException          If the SVG file specified by {@code url}
	 *                              cannot be read.
	 * @throws XMLStreamException   If the resource specified by {@code url} is
	 *                              not a proper SVG file.
	 * @throws NullPointerException If {@code url} is null.
	 */
	public static SVG load( URL url ) throws IOException, XMLStreamException {

		if ( url == null ) {
			throw new NullPointerException("url");
		}

		SVGContentBuilder builder = new SVGContentBuilder(url);

		return builder.build();

	}

	/**
	 * Load SVG file and convert it to JavaFX.
	 *
	 * @param stream The {@link InputStream} corresponding to the SVG file.
	 * @return An {@link SVG} object.
	 * @throws IOException          If the SVG file specified by {@code url}
	 *                              cannot be read.
	 * @throws XMLStreamException   If the resource specified by {@code url} is
	 *                              not a proper SVG file.
	 * @throws NullPointerException If {@code url} is null.
	 */
	public static SVG load( InputStream stream ) throws IOException, XMLStreamException {

		if ( stream == null ) {
			throw new NullPointerException("stream");
		}

		SVGContentBuilder builder = new SVGContentBuilder(stream);

		return builder.build();

	}

	private final Map<String, Group> groups = new HashMap<>(3);
	private final Map<String, Node> nodes = new HashMap<>(1);

	/**
	 * Get the group object indicated by the given {@code id}.
	 *
	 * @param id The identifier of the group to be returned.
	 * @return A {@link Group} corresponding to the given identifier, or
	 *         {@code null}.
	 */
	public Group getGroup( String id ) {
		return groups.get(id);
	}

	/**
	 * Get node object indicated by the given {@code id}.
	 *
	 * @param id The identifier of node to be returned.
	 * @return A {@link Node} corresponding to the given identifier, or
	 *         {@code null}.
	 */
	public Node getNode( String id ) {
		return nodes.get(id);
	}

	void putGroup( String id, Group group ) {
		groups.put(id, group);
	}

	void putNode( String id, Node node ) {
		nodes.put(id, node);
	}

}
