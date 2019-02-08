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


import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;
import javafx.scene.chart.Axis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;


/**
 * Comparator to be used when searching for {@link Data} points within a
 * {@link Series}.The comparison is performed against the X value (abscissa).
 *
 * @param <X> The type of
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class AbscissaDataComparator<X> implements Comparator<Data<X, ?>> {

	/**
	 * Creates a new instance of {@link Data} whose {@link Data#getXValue()}
	 * will return the given X value. Meant to be used to create search keys
	 * when using methods like
	 * {@link Collections#binarySearch(java.util.List, java.lang.Object, java.util.Comparator)}.
	 *
	 * @param <X> The type of the X part of the returned {@link Data}.
	 * @param xValue X value of the returned {@link Data}.
	 * @return Data containing the given X value.
	 */
	@SuppressWarnings( { "unchecked", "rawtypes" } )
	public static <X> Data<X, ?> key( X xValue ) {
		return new Data(xValue, 0d);
	}

	private final Axis<X> xAxis;

	/**
	 * Creates a new instance of {@link AbscissaDataComparator}.
	 *
	 * @param xAxis Mandatory X axis associated with chart for which
	 *              {@link Data} should be compared.
	 */
	public AbscissaDataComparator( Axis<X> xAxis ) {

		Objects.requireNonNull​(xAxis, "X axis");

		this.xAxis = xAxis;

	}

	@SuppressWarnings( "unchecked" )
	@Override
	public int compare( Data<X, ?> data1, Data<X, ?> data2 ) {

		double x1Numeric = xAxis.toNumericValue(data1.getXValue());
		double x2Numeric = xAxis.toNumericValue(data2.getXValue());

		return (int) Math.signum(x1Numeric - x2Numeric);
		
	}

}
