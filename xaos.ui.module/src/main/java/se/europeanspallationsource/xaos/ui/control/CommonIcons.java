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
package se.europeanspallationsource.xaos.ui.control;


import javafx.scene.paint.Color;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

import static org.kordamp.ikonli.javafx.FontIcon.of;


/**
 * Provider of common icons.
 *
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( "ClassWithoutLogger" )
public enum CommonIcons {

	FILE(FontAwesome.FILE_O),
	FILE_EXECUTABLE(FontAwesome.PLAY_CIRCLE_O),
	FILE_HIDDEN(FontAwesome.FILE),
	FILE_LINK(FontAwesome.LINK),
	FOLDER_COLLAPSED(FontAwesome.FOLDER_O),
	FOLDER_EXPANDED(FontAwesome.FOLDER_OPEN_O),
	SQUARE_DOWN(FontAwesome.CARET_SQUARE_O_DOWN),
	SQUARE_LEFT(FontAwesome.CARET_SQUARE_O_LEFT),
	SQUARE_RIGHT(FontAwesome.CARET_SQUARE_O_RIGHT),
	SQUARE_UP(FontAwesome.CARET_SQUARE_O_UP);

	private final Ikon ikon;

	private CommonIcons( Ikon ikon ) {
		this.ikon = ikon;
	}

	public FontIcon getIcon() {
		return of(ikon);
	}

	public FontIcon getIcon( int size ) {
		return of(ikon, size);
	}

	public FontIcon getIcon( Color color ) {
		return of(ikon, color);
	}

	public FontIcon getIcon( int size, Color color ) {
		return of(ikon, size, color);
	}

}
