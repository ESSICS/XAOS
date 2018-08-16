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
package se.europeanspallationsource.xaos.ui.spi.impl;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.Node;
import org.apache.commons.lang3.StringUtils;
import se.europeanspallationsource.xaos.tools.annotation.ServiceProvider;
import se.europeanspallationsource.xaos.ui.spi.ClassIconProvider;


/**
 * Provides default icons (i.e. {@link Node}s) for a given file extension.
 *
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( "ClassWithoutLogger" )
@ServiceProvider( service = ClassIconProvider.class )
public class DefaultJavaFXClassIconProvider implements ClassIconProvider {

	private static final Map<String, Node> ICONS_MAP;

	/**
	 * static initializer.
	 */
	static {

		Map<String, Node> map = new HashMap<>(120);

		try (
			InputStream in = getResourceAsStream("se/europeanspallationsource/xaos/ui/spi/icons/fxcomponents");
			BufferedReader br = new BufferedReader(new InputStreamReader(in))
		) {

			String resource;

			while ( ( resource = br.readLine() ) != null ) {
				System.out.println("--- " + resource);
//				filenames.add(resource);
			}

		} catch ( IOException ex ) {
			Logger.getLogger(DefaultJavaFXClassIconProvider.class.getName()).log(Level.SEVERE, null, ex);
		}

		ICONS_MAP = Collections.unmodifiableMap(map);

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

		return ICONS_MAP.get(clazz);

	}

}
