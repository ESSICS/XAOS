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
package se.europeanspallationsource.xaos.ui.spi.impl;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.commons.lang3.StringUtils;
import se.europeanspallationsource.xaos.tools.annotation.ServiceProvider;
import se.europeanspallationsource.xaos.ui.spi.ClassIconProvider;


/**
 * Provides default icons (i.e. {@link Node}s) for a given file extension.
 *
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( { "ClassWithoutLogger", "NestedAssignment" } )
@ServiceProvider( service = ClassIconProvider.class )
public class DefaultJavaFXClassIconProvider implements ClassIconProvider {

	private static final Map<String, Node> ICONS_MAP = new ConcurrentHashMap<>(120);
	private static final Map<String, String> RESOURCES_MAP;

	/**
	 * static initializer.
	 */
	static {

		Map<String, String> map = new HashMap<>(120);

		try (
			InputStream in = getResourceAsStream("icons/fxcomponents");
			BufferedReader br = new BufferedReader(new InputStreamReader(in))
		) {

			String resource;

			while ( ( resource = br.readLine() ) != null ) {
				if ( !resource.contains("@") ) {
					map.put(
						resource.substring(0, resource.lastIndexOf('.')),
						"icons/fxcomponents/" + resource
					);
				}
			}

		} catch ( IOException ex ) {
			Logger.getLogger(DefaultJavaFXClassIconProvider.class.getName()).log(Level.SEVERE, null, ex);
		}

		RESOURCES_MAP = Collections.unmodifiableMap(map);

	}

	private static InputStream getResourceAsStream( String resource ) {

		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);

		return ( in == null )
			   ? DefaultJavaFXClassIconProvider.class.getResourceAsStream(resource)
			   : in;

	}

	@Override
	public Node iconFor( String clazz ) {

		if ( StringUtils.isBlank(clazz) ) {
			return null;
		}
		
		Node node = null;

		if ( ICONS_MAP.containsKey(clazz) ) {
			node = ICONS_MAP.get(clazz);
		} else if ( clazz.startsWith("javafx.") ) {

			String simpleName = clazz.substring(1 + clazz.lastIndexOf('.'));

			if ( StringUtils.isNotBlank(simpleName) ) {

				String resource = RESOURCES_MAP.get(simpleName);

				if ( resource != null ) {

					node = new ImageView(new Image(getResourceAsStream(resource)));

					ICONS_MAP.put(clazz, node);

				}

			}

		}

		return node;

	}

}
