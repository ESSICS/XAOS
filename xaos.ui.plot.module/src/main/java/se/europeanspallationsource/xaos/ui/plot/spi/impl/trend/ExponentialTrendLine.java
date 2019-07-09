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

/**
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class ExponentialTrendLine extends BaseOLSTrendLine {

	private final double offset;

	public ExponentialTrendLine( double offset ) {
		
		if ( !Double.isFinite(offset) ) {

			setErrorOccurred();

			this.offset = 0;

		} else {
			this.offset = offset;
		}
		
	}

	@Override
	public int getDegree() {
		throw new UnsupportedOperationException("Not supported for ExponentialTrendLine.");
	}

	@Override
	public double getOffset() {
		return offset;
	}

	@Override
	public String nameFor( String seriesName ) {

		double a = Math.exp(getCoefficients()[0]);
		double b = getCoefficients()[1];

		return seriesName + String.format("\n f(x) = %+.2f⋅Exp(%+.2f * x)", a, b);

	}

	@Override
	protected boolean logY() {
		return true;
	}

	@Override
	protected double[] xVector( double x ) {
		return new double[] { 1, x };
	}

}
