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
package se.europeanspallationsource.xaos.ui.util;


import java.util.Arrays;
import java.util.Optional;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import org.apache.commons.lang3.Validate;
import se.europeanspallationsource.xaos.core.util.MathUtils;


/**
 * Various utility methods operating on {@link Color}.
 *
 * @author claudio.rosati@esss.se
 * @see <a href="https://confluence.esss.lu.se/x/wIh7DQ">ESS Control System OPI Development Style Guide</a>
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class ColorUtils {

	public static final Color ESS_BLUE            = Color.rgb( 79, 228, 250);
	public static final Color ESS_BLUE_DARK       = Color.rgb( 47, 135, 148);
	public static final Color ESS_BLUE_LED        = Color.rgb( 81, 232, 255);
	public static final Color ESS_BLUE_LED_OFF    = Color.rgb( 90, 110, 110);
	public static final Color ESS_GRAY            = Color.rgb(169, 169, 169);
	public static final Color ESS_GRAY_DARK       = Color.rgb(121, 121, 121);
	public static final Color ESS_GREEN           = Color.rgb( 61, 216,  61);
	public static final Color ESS_GREEN_DARK      = Color.rgb( 40, 140,  40);
	public static final Color ESS_GREEN_LED       = Color.rgb( 70, 255,  70);
	public static final Color ESS_GREEN_LED_OFF   = Color.rgb( 90, 110,  90);
	public static final Color ESS_MAGENTA         = Color.rgb(211,  45, 156);
	public static final Color ESS_MAGENTA_DARK    = Color.rgb(135,  29, 100);
	public static final Color ESS_MAGENTA_LED     = Color.rgb(255,  82, 218);
	public static final Color ESS_MAGENTA_LED_OFF = Color.rgb(110,  90, 109);
	public static final Color ESS_ORANGE          = Color.rgb(254, 194,  81);
	public static final Color ESS_ORANGE_DARK     = Color.rgb(153, 117,  49);
	public static final Color ESS_ORANGE_LED      = Color.rgb(255, 175,  81);
	public static final Color ESS_ORANGE_LED_OFF  = Color.rgb(110, 105,  90);
	public static final Color ESS_PRIMARY         = Color.rgb(  0, 148, 201);
	public static final Color ESS_PRIMARY_DARK    = Color.rgb( 31,  83, 102);
	public static final Color ESS_PRIMARY_LIGHT   = Color.rgb(151, 188, 202);
	public static final Color ESS_RED             = Color.rgb(252,  13,  27);
	public static final Color ESS_RED_DARK        = Color.rgb(150,   8,  16);
	public static final Color ESS_RED_LED         = Color.rgb(255,  60,  46);
	public static final Color ESS_RED_LED_OFF     = Color.rgb(110, 101,  90);
	public static final Color ESS_SECONDARY       = Color.rgb(202,   0,   0);
	public static final Color ESS_SECONDARY_DARK  = Color.rgb(102,  31,  32);
	public static final Color ESS_SECONDARY_LIGHT = Color.rgb(202, 151, 152);
	public static final Color ESS_YELLOW          = Color.rgb(252, 242,  17);
	public static final Color ESS_YELLOW_DARK     = Color.rgb(150, 144,  10);
	public static final Color ESS_YELLOW_LED      = Color.rgb(255, 235,  17);
	public static final Color ESS_YELLOW_LED_OFF  = Color.rgb(110, 108,  90);

	public static final Color ESS_ATTENTION    = ESS_YELLOW;
	public static final Color ESS_DISCONNECTED = Color.rgb(105,  77, 164);
	public static final Color ESS_ERROR        = ESS_RED;
	public static final Color ESS_INVALID      = Color.rgb(149, 110, 221);
	public static final Color ESS_MAJOR        = ESS_RED;
	public static final Color ESS_MINOR        = ESS_YELLOW;
	public static final Color ESS_OFF          = ESS_GRAY;
	public static final Color ESS_OK           = ESS_GREEN;
	public static final Color ESS_ON           = Color.rgb( 22, 222,  33);
	public static final Color ESS_STOP         = Color.rgb(222,  22,  11);
	public static final Color ESS_WARNING      = ESS_YELLOW;

	/**
	 * Selects from the {@code others} {@link Color}s the one with the best
	 * contrast against the given {@code color}.
	 *
	 * @param color  The {@link Color} who needs a best contrasting one from
	 *               the {@code others} ones.
	 * @param others The array of {@link Color}s from which the best contrasting
	 *               one must be chosen.
	 * @return The best contrasting color.
	 * @throws NullPointerException     If {@code color} or {@code others} is
	 *                                  {@code null}.
	 * @throws IllegalArgumentException If {@code others} is an empty array.
	 * @see #getLuma(javafx.scene.paint.Color)
	 */
	public static Color bestConstrasting( Color color, Color... others ) {

		Validate.notNull(color, "Null 'color' parameter.");
		Validate.notEmpty(others, "Empty or null 'others' array.");
		
		if ( others.length == 1 ) {
			return others[0];
		}

		double luma = getLuma(color);

		Optional<Pair<Color, Double>> best = Arrays.asList(others)
			.parallelStream()
			.map(c -> new Pair<>(c, Math.abs(luma - getLuma(c))))
			.sorted(( p1, p2 ) -> (int) Math.signum(p2.getValue() - p1.getValue()))
			.findFirst();

		return best.get().getKey();

	}

	/**
	 * Adds the given {@code offset} to the {@code color}'s opacity. Negative
	 * values will increase transparency (lower opacity), positive values will
	 * increase opacity.
	 *
	 * @param color  The {@link Color} whose opacity must be changed.
	 * @param offset The opacity offset to be added to the current
	 *               {@code color}'s opacity.
	 * @return The changed {@link Color}.
	 * @throws NullPointerException     If {@code color} is null.
	 * @throws IllegalArgumentException If {@code offset} is not in the
	 *                                  {@code [0, 1]} range.
	 */
	public static Color changeOpacity ( Color color, double offset ) {

		Validate.notNull(color, "Null 'color' parameter.");
		Validate.inclusiveBetween(-1.0, 1.0, offset, "'offset' out of [-1.0, 1.0] range.");

		return new Color(
			color.getRed(),
			color.getGreen(),
			color.getBlue(),
			MathUtils.clamp(color.getOpacity() + offset, 0.0, 1.0)
		);

	}

	/**
	 * Returns the weighted brightness (luma) according to the HSP color model.
	 *
	 * @param color The color whose perceived brightness must be returned.
	 * @return The weighted brightness (luma).
	 * @throws NullPointerException If {@code color} is null.
	 * @see <a href="http://alienryderflex.com/hsp.html">HSP Color Model</a>
	 * @see <a href="https://thoughtbot.com/blog/closer-look-color-lightness#weighted-methods">Weighted Methods</a>.
	 */
	public static double getLuma( Color color ) {

		Validate.notNull(color, "Null 'color' parameter.");

		double r = color.getRed();
		double g = color.getGreen();
		double b = color.getBlue();

		return Math.sqrt(0.299 * r * r + 0.587 * g * g + 0.114 * b * b);

	}

	/**
	 * Converts the given {@link Color} to its web string representation.
	 * <p>
	 * If the given {@code color} is {@link Color#isOpaque() opaque} then the
	 * returned string will be {@code #rrggbb} otherwise {@code #rrggbboo},
	 * where {@code rr}, {@code gg}, and {@code bb} are the hexadecimal value
	 * of the three color components (red, green, and blue), while {@code oo}
	 * is the hexadecimal representation of the color's opacity.</p>
	 *
	 * @param color The {@link Color} to be converted.
	 * @return A {@link String} representation of the given {@code color}.
	 * @throws NullPointerException If {@code color} is null.
	 */
	public static String toWeb( Color color ) {

		Validate.notNull(color, "Null 'color' parameter.");

		if ( color.isOpaque() ) {
			return String.format(
				"#%02X%02X%02X",
				(int) ( color.getRed() * 255 ),
				(int) ( color.getGreen() * 255 ),
				(int) ( color.getBlue() * 255 )
			);
		} else {
			return String.format(
				"#%02X%02X%02X%02X",
				(int) ( color.getRed() * 255 ),
				(int) ( color.getGreen() * 255 ),
				(int) ( color.getBlue() * 255 ),
				(int) ( color.getOpacity() * 255 )
			);
		}

	}

	private ColorUtils() {
		//	NOthing to do.
	}

}
