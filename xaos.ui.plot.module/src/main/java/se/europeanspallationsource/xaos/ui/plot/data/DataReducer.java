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


import java.util.List;
import javafx.scene.chart.XYChart.Data;


/**
 * Defines the behavior of the algorithms used to reduce number of data points
 * to the desired amount.
 *
 * @param <X> Type of X values.
 * @param <Y> Type of Y values.
 * @author claudio.rosati@esss.se
 */
public interface DataReducer<X, Y> {

	/**
	 * Default number of target points: 1000.
	 */
	static final int DEFAULT_POINTS_COUNT = 1000;

	/**
	 * The minimum number of target points: 2.
	 */
	static final int MIN_TARGET_POINTS_COUNT = 2;

	/**
	 * Reduces the number of the given {@code data} points to the specified
	 * {@code targetPointsCount}, if it is smaller than the data size.
	 *
	 * @param data              {@link List} of data points to be reduced.
	 * @param targetPointsCount The desired number of target points, not smaller
	 *                          than {@link #MIN_TARGET_POINTS_COUNT}.
	 * @return A {@link List} containing reduced data, or the original
	 *         {@code data} list if its size is greater or equal to
	 *         {@code targetPointsCount}.
	 * @throws NullPointerException     If {@code data} is {@code null}.
	 * @throws IllegalArgumentException If {@code targetPointsCount} is less
	 *                                  than {@link #MIN_TARGET_POINTS_COUNT}.
	 */
	List<Data<X, Y>> reduce( List<Data<X, Y>> data, int targetPointsCount )
		throws NullPointerException, IllegalArgumentException;

}
