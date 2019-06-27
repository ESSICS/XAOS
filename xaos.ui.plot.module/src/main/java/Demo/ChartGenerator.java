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
package Demo;


import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;


/**
 * @param <X> type of X values
 * @param <Y> type of Y values
 * @author claudio.rosati@esss.se
 */
public interface ChartGenerator<X, Y> {

	static final Random RANDOM = new Random(System.currentTimeMillis());

	XYChart<X, Y> getNewChart( int numberOfPoints, boolean logXAxis, boolean logYAxis );

	static int[] generateIntArray( int firstValue, int variance, int size ) {

		int[] data = new int[size];

		data[0] = firstValue;

		for ( int i = 1; i < data.length; i++ ) {

			int sign = RANDOM.nextBoolean() ? 1 : -1;

			data[i] = data[i - 1] + (int) ( variance * RANDOM.nextDouble() ) * sign;

		}

		return data;

	}

	static ObservableList<XYChart.Data<Number, Number>> generateData( int numberOfPoints ) {

		int[] yValues = generateIntArray(0, 5, numberOfPoints);
		List<XYChart.Data<Number, Number>> data = new ArrayList<>(numberOfPoints);

		for ( int i = 0; i < yValues.length; i++ ) {
			data.add(new XYChart.Data<>(i, yValues[i]));
		}

		return FXCollections.observableArrayList(data);

	}

	static ObservableList<XYChart.Data<Date, Number>> generateDateData( int numberOfPoints ) {

		int[] yValues = generateIntArray(0, 5, numberOfPoints);
		List<XYChart.Data<Date, Number>> data = new ArrayList<>(numberOfPoints);

		for ( int i = 0; i < yValues.length; i++ ) {
			data.add(new XYChart.Data<>(new GregorianCalendar(
				2000 + RANDOM.nextInt(10),
				RANDOM.nextInt(12),
				RANDOM.nextInt(31)
			).getTime(), yValues[i]));
		}

		return FXCollections.observableArrayList(data);

	}

}
