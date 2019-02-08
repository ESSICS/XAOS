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

package chart.data;

import java.util.List;

import javafx.scene.chart.XYChart.Data;

/**
 * Algorithm to reduce number of data points to the desired amount. 
 * Used by the {@link DataReducingSeries}. 
 * 
 * @param <X> type of X values
 * @param <Y> type of Y values
 * 
 * @author Grzegorz Kruk
 */
public interface DataReducer<X, Y> {

    /**
     * Reduces the number of data points to the specified number if the number is smaller than the data size.
     * @param data list containing data points to be reduced
     * @param targetPointsCount the desired number of target points, not smaller than 2
     * @return list containing reduced data 
     */
    List<Data<X, Y>> reduce(List<Data<X, Y>> data, int targetPointsCount);
}
