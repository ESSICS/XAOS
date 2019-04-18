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
	 * @see <a href="https://thoughtbot.com/blog/closer-look-color-lightness#weighted-methods">Weighted Methods</a>
	 */
	public static Color bestConstrast( Color color, Color... others ) {

		Validate.notNull(color, "Null 'color' parameter.");
		Validate.notNull(others, "Null 'others' parameter.");
		Validate.notEmpty(others, "Empty 'others' array.");

		double luma = Math.sqrt(
			0.299 * color.getRed()   * color.getRed()
		  + 0.587 * color.getGreen() * color.getGreen()
		  + 0.114 * color.getBlue()  * color.getBlue()
		);

		Optional<Pair<Color, Double>> best = Arrays.asList(others)
			.parallelStream()
			.map(c -> new Pair<>(
				c,
				Math.sqrt(
					0.299 * c.getRed()   * c.getRed()
						+ 0.587 * c.getGreen() * c.getGreen()
						+ 0.114 * c.getBlue()  * c.getBlue()
				)
			))
			.sorted(( p1, p2 ) -> (int) Math.signum((luma - p2.getValue() ) - ( luma - p1.getValue() )))
			.findFirst();

		return best.get().getKey();

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
	 */
	public static String toWeb ( Color color ) {

		Validate.notNull(color, "Null 'color' parameter.");

		if ( color.isOpaque() ) {
			return String.format(
				"#%02X%02X%02X",
				(int) (color.getRed()   * 255),
				(int) (color.getGreen() * 255),
				(int) (color.getBlue()  * 255)
			);
		} else {
			return String.format(
				"#%02X%02X%02X%02X",
				(int) (color.getRed()   * 255),
				(int) (color.getGreen() * 255),
				(int) (color.getBlue()  * 255),
				(int) (color.getOpacity() * 255)
			);
		}

	}

	private ColorUtils() {
		//	NOthing to do.
	}

}
