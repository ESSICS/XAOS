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
package se.europeanspallationsource.xaos.application.utilities;


import de.jensd.fx.glyphs.GlyphIcons;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.utils.FontAwesomeIconFactory;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.utils.MaterialDesignIconFactory;
import java.text.MessageFormat;
import javafx.scene.text.Text;
import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;


/**
 * Provider of common icons used in various part.
 * <p>
 * Icons are lazily created and cached.
 * </p>
 *
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class CommonIcons {

	private static final Cache<Glyph, Text> CACHE = new Cache2kBuilder<Glyph, Text>() {}
		.entryCapacity(Glyph.values().length)
		.loader(CommonIcons::load)
		.name("common-icons-cache")
		.build();

	/**
	 * Return the icon (as an instance of {@link Text}) for the given
	 * {@code glyph}. The icon is loaded lazily and cached to improve memory
	 * footprint and load time.
	 *
	 * @param glyph One of the predefined icon {@link Glyph}.
	 * @return The icon (as an instance of {@link Text}) for the given
	 *         {@code glyph}.
	 * @throws NullPointerException If the given {@code glyph} is {@code null}.
	 */
	public static Text get( Glyph glyph ) throws NullPointerException {
		if ( glyph != null ) {
			return CACHE.get(glyph);
		} else {
			throw new NullPointerException("glyph");
		}
	}

    private static Text load( Glyph glyph ) {
        if ( glyph.glypIcon() instanceof FontAwesomeIcon ) {
            return FontAwesomeIconFactory.get().createIcon(glyph);
		} else if ( glyph.glypIcon() instanceof MaterialDesignIcon ) {
            return MaterialDesignIconFactory.get().createIcon(glyph);
        } else {
            throw new IllegalArgumentException(MessageFormat.format(
                "Not supported icon font [family = {0}, name = {1}",
                glyph.fontFamily(),
                glyph.name()
            ));
        }
    }

	private CommonIcons() {
	}

	@SuppressWarnings( "PublicInnerClass" )
	public static enum Glyph implements GlyphIcons {

		FILE(MaterialDesignIcon.FILE),
		FOLDER(MaterialDesignIcon.FOLDER),
		SQUARE_DOWN(FontAwesomeIcon.CARET_SQUARE_ALT_DOWN),
		SQUARE_LEFT(FontAwesomeIcon.CARET_SQUARE_ALT_LEFT),
		SQUARE_RIGHT(FontAwesomeIcon.CARET_SQUARE_ALT_RIGHT),
		SQUARE_UP(FontAwesomeIcon.CARET_SQUARE_ALT_UP);

		private final GlyphIcons glyphIcon;

		private Glyph( GlyphIcons glyphIcon ) {
			this.glyphIcon = glyphIcon;
		}

		@Override
		public String fontFamily() {
			return glyphIcon.fontFamily();
		}

		public GlyphIcons glypIcon() {
			return glyphIcon;
		}

		public String glyphName() {
			return glyphIcon.name();
		}

		@Override
		public String unicode() {
			return glyphIcon.unicode();
		}

	}

}
