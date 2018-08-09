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
package se.europeanspallationsource.xaos.ui;


import javafx.scene.paint.Color;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import static org.kordamp.ikonli.javafx.FontIcon.of;


/**
 * Provider of common icons.
 *
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( "ClassWithoutLogger" )
public enum CommonIcons {

	FILE(FontAwesomeSolid.FILE),
	FOLDER(FontAwesomeSolid.FOLDER),
	FOLDER_OPEN(FontAwesomeSolid.FOLDER_OPEN),
	LINK(FontAwesomeSolid.LINK),
	SQUARE_DOWN(FontAwesomeSolid.CARET_SQUARE_DOWN),
	SQUARE_LEFT(FontAwesomeSolid.CARET_SQUARE_LEFT),
	SQUARE_RIGHT(FontAwesomeSolid.CARET_SQUARE_RIGHT),
	SQUARE_UP(FontAwesomeSolid.CARET_SQUARE_UP);

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
