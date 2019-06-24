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
package se.europeanspallationsource.xaos.ui.plot.data;

/**
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class PolynomialTrendLine extends BaseOLSTrendLine {

	private static final char[] SUPERSCRIPT_CHARS = { '⁰', '¹', '²', '³', '⁴', '⁵', '⁶', '⁷', '⁸', '⁹' };

	private final int degree;

	public PolynomialTrendLine( int degree ) {

		if ( degree < 0 ) {
			throw new IllegalArgumentException("The degree of the polynomial cannot be negative.");
		}

		this.degree = degree;

	}

	@Override
	public int getDegree() {
		return degree;
	}

	@Override
	public double getOffset() {
		return 0.0;
	}

	@Override
	public String nameFor( String seriesName ) {

		StringBuilder builder = new StringBuilder(seriesName);

		builder.append("\n f(x) = ");

		for ( int i = 0; i <= getDegree(); i++ ) {

			builder.append(String.format("%+.2f", getCoefficients()[i]));

			if ( i > 0 ) {

				builder.append("⋅x");

				if ( i > 1 ) {

					String exponent = String.format("%d", i);

					for ( int e = 0; e < exponent.length(); e++ ) {
						builder.append(SUPERSCRIPT_CHARS[exponent.charAt(e) - '0']);
					}

				}

			}


		}

		return builder.toString();

	}

	@Override
	protected boolean logY() {
		return false;
	}

	@Override
	protected double[] xVector( double x ) {
		//	{1, x, x*x, x*x*x, ...}
		double[] poly = new double[degree + 1];
		double xi = 1;

		for ( int i = 0; i <= degree; i++ ) {
			poly[i] = xi;
			xi *= x;
		}

		return poly;

	}

}
