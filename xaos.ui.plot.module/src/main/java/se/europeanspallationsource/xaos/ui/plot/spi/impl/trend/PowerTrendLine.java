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
package se.europeanspallationsource.xaos.ui.plot.spi.impl.trend;


import java.util.logging.Logger;
import se.europeanspallationsource.xaos.core.util.LogUtils;

import static java.util.logging.Level.WARNING;


/**
 *
 * @author reubenlindroos
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class PowerTrendLine extends BaseOLSTrendLine {

	private static final Logger LOGGER = Logger.getLogger(PowerTrendLine.class.getName());
	private static final char[] SUPERSCRIPT_CHARS = { '⁰', '¹', '²', '³', '⁴', '⁵', '⁶', '⁷', '⁸', '⁹' };
	private static final char SUPERSCRIPT_DOT = '･';
	private static final char SUPERSCRIPT_MINUS = '⁻';
	private static final char SUPERSCRIPT_PLUS = '⁺';

	private final double offset;

	public PowerTrendLine( double offset ) {

		if ( !Double.isFinite(offset) ) {

			setErrorOccurred();

			this.offset = 0;

		} else {
			this.offset = offset;
		}

	}

	@Override
	public int getDegree() {
		throw new UnsupportedOperationException("Not supported for PowerTrendLine.");
	}

	@Override
	public double getOffset() {
		return offset;
	}

	@Override
	public String nameFor( String seriesName ) {

		double a = Math.exp(getCoefficients()[0]);
		double b = getCoefficients()[1];
		StringBuilder builder = new StringBuilder(seriesName);

		builder.append("\n f(x) = ");
		builder.append(String.format("%+.2f⋅x", a));

		String exponent = String.format("%+.2f", b);

		for ( int e = 0; e < exponent.length(); e++ ) {

			char c = exponent.charAt(e);

			if ( Character.isDigit(c) ) {
				builder.append(SUPERSCRIPT_CHARS[c - '0']);
			} else if ( c == '.' || c == ',' ) {
				builder.append(SUPERSCRIPT_DOT);
			} else if ( c == '+' ) {
				builder.append(SUPERSCRIPT_PLUS);
			} else if ( c == '-' ) {
				builder.append(SUPERSCRIPT_MINUS);
			} else {
				setErrorOccurred();
				LogUtils.log(
					LOGGER,
					WARNING,
					"Invalid char [{0}]: 0x{1}",
					String.valueOf(c),
					Integer.toHexString(c).toUpperCase()
				);
			}
		}

		return builder.toString();

	}

	@Override
	protected boolean logY() {
		return true;
	}

	@Override
	protected double[] xVector( double x ) {

		double logx = Math.log(x);

		if ( !Double.isFinite(logx) ) {

			setErrorOccurred();

			logx = 0;

		}

		return new double[] { 1, logx };

	}

}
