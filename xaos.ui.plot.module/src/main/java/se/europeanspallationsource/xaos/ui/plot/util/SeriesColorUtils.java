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
package se.europeanspallationsource.xaos.ui.plot.util;


import javafx.scene.chart.XYChart.Series;
import javafx.scene.paint.Color;
import org.apache.commons.lang3.Validate;
import se.europeanspallationsource.xaos.ui.util.ColorUtils;


/**
 * Utility methods to set {@link Series}'s color.
 *
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class SeriesColorUtils {

	public static final Color HORIZONTAL   = ColorUtils.ESS_STOP;
	public static final Color LONGITUDINAL = ColorUtils.ESS_ON;
	public static final Color VERTICAL     = ColorUtils.ESS_PRIMARY;

	private static final Color[] COLORS = {
		Color.valueOf("lightblue"),
		Color.valueOf("gold"),
		Color.valueOf("orchid"),
		Color.valueOf("olivedrab"),
		Color.valueOf("darkgoldenrod"),
		Color.valueOf("darkorange"),
		Color.valueOf("navy"),
		Color.valueOf("brown"),
	};

	/**
	 * Returns the style to be applied for a given {@link Color} to a
	 * {@link Series}.
	 * <p>
	 * The returned style is obtained changing the {@code CHART_COLOR_i},
	 * {@code CHART_COLOR_i_TRANS_20} and {@code CHART_COLOR_i_TRANS_70}
	 * standard JavaFX chart colors (where <i>i</i> is a number in the
	 * {@code 1..8} range) to the given {@code color}.</p>
	 *
	 * @param color The {@link Color} for which CSS style string appropriate for
	 *              a chart {@link Series} should be created.
	 * @param index The index of the {@link Series} in the chart. The
	 *              {@code ( index % 8 ) + 1} value will be used to decide which
	 *              of the 8 standard chart colors will be overridden in the
	 *              returned style string.
	 * @return A style string overriding one of the default JavaFx 8 chart
	 *         colors with the given one.
	 * @throws NullPointerException     If {@code color} is {@code null}.
	 * @throws IllegalArgumentException If (@code index} is negative.
	 */
	public static String styleFor( Color color, int index ) {

		Validate.notNull(color, "Null 'color' parameter.");
		Validate.isTrue(index >= 0, "Negative 'index' parameter.");

		int i = ( index % 8 ) + 1;
		String webColor = ColorUtils.toWeb(color);
		String chartColor = "CHART_COLOR_" + i;

		return chartColor +          ": " + webColor + "; "
		     + chartColor + "_TRANS_20: " + webColor + "33; "
		     + chartColor + "_TRANS_70: " + webColor + "B3;";

	}

	/**
	 * @return The default styles string for a chart.
	 */
	public static String styles() {
		return styles(-1, -1, -1);
	}

	/**
	 * Return the styles string to be applied to a chart.
	 * <p>As in JavaFX 8 colors will be used. If the given indexes are
	 * non-negative, then special colors will be used to represent horizontal
	 * (red), vertical (blue) and longitudinal (green) series.</p>
	 *
	 * @param horizontal   The index of the horizontal series. If non-negative
	 *                     red will be used for the series.
	 * @param vertical     The index of the vertical series. If non-negative
	 *                     blue will be used for the series.
	 * @param longitudinal The index of the longitudinal series. If non-negative
	 *                     red will be used for the series.
	 * @return The styles string to be applied to a chart.
	 */
	@SuppressWarnings( { "AssignmentToMethodParameter", "ValueOfIncrementOrDecrementUsed" } )
	public static String styles ( int horizontal, int vertical, int longitudinal ) {

		if ( horizontal >= 0 ) {
			horizontal %= 8;
		}
		if ( vertical >= 0 ) {
			vertical %= 8;
		}
		if ( longitudinal >= 0 ) {
			longitudinal %= 8;
		}

		int colorIndex = 0;
		StringBuilder builder = new StringBuilder(360);

		for ( int i = 0; i < 8; i++ ) {

			if ( i == horizontal ) {
				builder.append(styleFor(HORIZONTAL, i));
			} else if ( i == vertical ) {
				builder.append(styleFor(VERTICAL, i));
			} else if ( i == longitudinal ) {
				builder.append(styleFor(LONGITUDINAL, i));
			} else {
				builder.append(styleFor(COLORS[colorIndex++], i));
			}

			if ( i < 7 ) {
				builder.append(' ');
			}

		}

		return builder.toString();

	}

	private SeriesColorUtils() {
		//	Nothing to do.
	}

}
