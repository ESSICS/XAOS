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
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class ColorUtils {

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
