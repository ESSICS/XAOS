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


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javafx.scene.Node;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;
import se.europeanspallationsource.xaos.tools.annotation.ServiceProvider;
import se.europeanspallationsource.xaos.ui.control.CommonIcons;
import se.europeanspallationsource.xaos.ui.spi.IconProvider;


/**
 * Provides default icons (i.e. square {@link Node}s) for a given object key.
 *
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( "ClassWithoutLogger" )
@ServiceProvider( service = IconProvider.class )
public class DefaultCommonIconProvider implements IconProvider {

	private static final Map<CommonIcons, Ikon> ICONS_MAP;

	/**
	 * static initializer.
	 */
	static {

		Node icon;
		Map<CommonIcons, Ikon> map = new HashMap<>(100);

		map.put(CommonIcons.FILE, FontAwesome.FILE_O);
		map.put(CommonIcons.FILE_EXECUTABLE, FontAwesome.PLAY_CIRCLE_O);
		map.put(CommonIcons.FILE_HIDDEN, FontAwesome.FILE);
		map.put(CommonIcons.FILE_LINK, FontAwesome.LINK);
		map.put(CommonIcons.FOLDER_COLLAPSED, FontAwesome.FOLDER_O);
		map.put(CommonIcons.FOLDER_EXPANDED, FontAwesome.FOLDER_OPEN_O);
		map.put(CommonIcons.SQUARE_DOWN, FontAwesome.CARET_SQUARE_O_DOWN);
		map.put(CommonIcons.SQUARE_LEFT, FontAwesome.CARET_SQUARE_O_LEFT);
		map.put(CommonIcons.SQUARE_RIGHT, FontAwesome.CARET_SQUARE_O_RIGHT);
		map.put(CommonIcons.SQUARE_UP, FontAwesome.CARET_SQUARE_O_UP);

		ICONS_MAP = Collections.unmodifiableMap(map);

	}

	@Override
	public Node iconFor( Object key, int size ) {

		if ( Objects.nonNull(key) && key instanceof CommonIcons && size > 0 ) {

			Ikon icon = ICONS_MAP.get((CommonIcons) key);
			
			if ( icon != null ) {
				return FontIcon.of(icon, size);
			}

		}

		return null;

	}

}
