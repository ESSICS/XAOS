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


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.Node;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.keyvalue.DefaultMapEntry;
import se.europeanspallationsource.xaos.annotation.ServiceProvider;
import se.europeanspallationsource.xaos.ui.spi.FileExtensionIconProvider;


/**
 * Provides default icons (i.e. {@link Node}s) for a given file extension.
 *
 * @author claudio.rosati@esss.se
 */
@ServiceProvider(service = FileExtensionIconProvider.class)
@SuppressWarnings( "ClassWithoutLogger" )
public class DefaultFileExtensionIconProvider implements FileExtensionIconProvider {

	private static final Map<String, Node> iconsMap = Collections.unmodifiableMap(
		MapUtils.putAll(
			new HashMap<String, Node>(),
			new DefaultMapEntry[] {
				new DefaultMapEntry<String, Node>("ss", null)
			}
		)
	);

	@Override
	public Node iconFor( String extension ) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

}
