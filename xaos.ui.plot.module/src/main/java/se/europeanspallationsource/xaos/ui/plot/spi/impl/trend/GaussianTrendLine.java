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
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.fitting.GaussianCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import se.europeanspallationsource.xaos.core.util.LogUtils;

import static java.util.logging.Level.WARNING;


/**
 * @author natalia@milas@esss.se
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class GaussianTrendLine implements TrendLine {

	private static final Logger LOGGER = Logger.getLogger(GaussianTrendLine.class.getName());

	private boolean errorOccurred = false;
	private final WeightedObservedPoints obs = new WeightedObservedPoints();
	private double[] parameters;

	@Override
	@SuppressWarnings( "ReturnOfCollectionOrArrayField" )
	public double[] getCoefficients() {
		return parameters;
	}

	@Override
	public int getDegree() {
		throw new UnsupportedOperationException("Not supported for GaussianTrendLine.");
	}

	@Override
	public double getOffset() {
		throw new UnsupportedOperationException("Not supported for GaussianTrendLine.");
	}

	@Override
	public boolean isErrorOccurred() {
		return errorOccurred;
	}

	@Override
	public String nameFor( String seriesName ) {
		return seriesName + String.format(
			"\n f(x) = %+.2f * Exp[-(x %+.2f)²/(2 * %.2f)²]\n",
			parameters[0],
			parameters[1],
			parameters[2]
		);
	}

	@Override
	public double predict( double x ) {
		return parameters[0] * Math.exp(-( x - parameters[1] ) * ( x - parameters[1] ) / ( 2 * parameters[2] * parameters[2] ));
	}

	@Override
	public void setValues( double[] y, double[] x ) {

		if ( x.length != y.length ) {
			throw new IllegalArgumentException(String.format(
				"The numbers of y and x values must be equal (%d != %d).",
				y.length,
				x.length)
			);
		}

		for ( int j = 0; j < x.length; j++ ) {
			obs.add(x[j], y[j]);
		}

		try {
			parameters = GaussianCurveFitter.create().fit(obs.toList());
		} catch ( MathIllegalStateException misex ) {

			LogUtils.log(LOGGER, WARNING, misex, "Unable to performa gaussian fitting.");

			errorOccurred = true;
			parameters = new double[] { 0.0, 0.0, 0.0 };
			
		}

	}

}
