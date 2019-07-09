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
public interface TrendLine {

	/**
	 * @return The coefficients of the fitting function.
	 */
	double[] getCoefficients();

	/**
	 * @return The degree of the fitting function.
	 */
	int getDegree();

	/**
	 * @return The offset of the fitting function.
	 */
	double getOffset();

	/**
	 * @return {@code true} if one or more errors occurred on any method call
	 *         since the construction of this trend line.
	 */
	boolean isErrorOccurred();

	/**
	 * @param seriesName The name of the series.
	 * @return Returns a proper name of this trend line for the given series name.
	 */
	String nameFor( String seriesName );

	/**
	 * Gets a predicted <em>y = f(x)</em> for a given {@code x}.
	 *
	 * @param x The abscissa value for which an ordinate mus be predicted.
	 * @return The predicted <em>y = f(x)</em> for a given {@code x}.
	 */
	double predict( double x );

	/**
	 * Sets the known points.
	 *
	 * @param y The ordinate of the known points.
	 * @param x The abscissa of the known points.
	 */
	void setValues( double[] y, double[] x );

}
