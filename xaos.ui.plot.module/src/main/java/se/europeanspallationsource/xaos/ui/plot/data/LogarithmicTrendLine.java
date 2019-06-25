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
public class LogarithmicTrendLine extends BaseOLSTrendLine {

	@Override
	public int getDegree() {
		throw new UnsupportedOperationException("Not supported for LogarithmicTrendLine.");
	}

	@Override
	public double getOffset() {
		throw new UnsupportedOperationException("Not supported for LogarithmicTrendLine.");
	}

	@Override
	public String nameFor( String seriesName ) {
		return seriesName + String.format(
			"\n f(x) = %+.2f %+.2f⋅Ln(x)",
			getCoefficients()[0],
			getCoefficients()[1]
		);

	}

	@Override
	protected boolean logY() {
		return false;
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
