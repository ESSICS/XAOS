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


import java.util.Arrays;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;


/**
 * A base class for all {@link TrendLine}s implementing ordinary least squares
 * (OLS) to estimate the parameters of a multiple linear regression model.
 *
 * @author claudio.rosati@esss.se
 */
public abstract class BaseOLSTrendLine implements TrendLine {

	//	Will hold prediction coefs once we get values.
	private RealMatrix coefficients = null;

	@Override
	public double[] getCoefficients() {

		double[] coefs = new double[coefficients.getData().length];

		for ( int i = 0; i < coefficients.getData().length; i++ ) {
			coefs[i] = coefficients.getEntry(i, 0);
		}

		return coefs;

	}

	@Override
	public double predict( double x ) {

		//	Apply coefs to xVector.
		double y = coefficients.preMultiply(xVector(x))[0];

		if ( logY() ) {
			//	If we predicted ln(y), we need to get y.
			y = Math.exp(y);
		}

		return y;

	}

	@Override
	@SuppressWarnings( "AssignmentToMethodParameter" )
	public void setValues( double[] y, double[] x ) {

		if ( x.length != y.length ) {
			throw new IllegalArgumentException(String.format(
				"The numbers of y and x values must be equal (%d != %d)",
				y.length,
				x.length
			));
		}

		double[][] xData = new double[x.length][];

		for ( int i = 0; i < x.length; i++ ) {
			//	The implementation determines how to produce a vector of
			//	predictors from a single x.
			xData[i] = xVector(x[i]);
		}

		if ( logY() ) { 
		
			//	In some models we are predicting ln(y), so we replace each y with ln y
			y = Arrays.copyOf(y, y.length);

			for ( int i = 0; i < x.length; i++ ) {
				y[i] = Math.log(y[i]);
			}

		}

		OLSMultipleLinearRegression ols = new OLSMultipleLinearRegression();

		//	Let the implementation include a constant in xVector if desired.
		ols.setNoIntercept(true);
		//	Provide the data to the model.
		ols.newSampleData(y, xData);

		coefficients = MatrixUtils.createColumnRealMatrix(ols.estimateRegressionParameters());

	}

	/**
	 * @return {@code true} to predict <em>ln(y)</em> (note: y must be positive).
	 */
	protected abstract boolean logY();

	/**
	 * @param x
	 * @return A vector of values for the given {@code x}.
	 */
	protected abstract double[] xVector( double x );

}
