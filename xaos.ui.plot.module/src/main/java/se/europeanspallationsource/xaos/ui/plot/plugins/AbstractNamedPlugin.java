/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (C) 2018-2019 by European Spallation Source ERIC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package se.europeanspallationsource.xaos.ui.plot.plugins;


import chart.Plugin;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.logging.Logger;
import se.europeanspallationsource.xaos.tools.annotation.BundleItem;
import se.europeanspallationsource.xaos.tools.annotation.Bundles;


/**
 * A {@link Plugin} with a name. It will also look at the {@code htmls} package
 * for an HTML file named <i>class-name.html</i>, where {@code class-name} is
 * the actual plugin class simple name, and will return its content when
 * {@link #getHTMLDescription()} is invoked.
 *
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class AbstractNamedPlugin extends Plugin {

	private static final Logger LOGGER = Logger.getLogger(AbstractNamedPlugin.class.getName());

	private final String name;

	/**
	 * @param name The display name of this plugin.
	 */
	protected AbstractNamedPlugin( String name ) {
		this.name = name;
	}

	@BundleItem(
		key = "html.language.variation",
		comment = "The extension will be added to class name, before the '.html' extension.\n"
				+ "It could be something like '_it', or '_fr'.",
		message = ""
	)
	@Override
	@SuppressWarnings( "NestedAssignment" )
	public String getHTMLDescription() {

		String htmlResourceName = "/htmls/"
								+ getClass().getSimpleName()
								+ Bundles.get(AbstractNamedPlugin.class, "html.language.variation")
								+ ".html";
		InputStream htmlResource = getClass().getResourceAsStream(htmlResourceName);

		if ( htmlResource != null ) {

			StringWriter writer = new StringWriter();

			try ( BufferedReader reader = new BufferedReader(new InputStreamReader(htmlResource)) ) {

				String line;

				while ( ( line = reader.readLine() ) != null ) {
					writer.write(line);
					writer.write('\n');
				}

			} catch ( IOException ex ) {
				LOGGER.warning(MessageFormat.format("HTML resource ''{0}'' not found or not reatable [{1}].", htmlResourceName, ex.getMessage()));
			}

			return writer.toString();

		} else {
			LOGGER.warning(MessageFormat.format("HTML resource ''{0}'' not found.", htmlResourceName));
		}

		return super.getHTMLDescription();

	}

	@Override
	public String getName() {
		return name;
	}

}
