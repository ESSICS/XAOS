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

package util;

import static util.Assert.assertArgNotNull;

import java.util.Collections;
import java.util.Comparator;

import javafx.scene.chart.Axis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;

/**
 * Comparator to be used when searching for data points within {@link Series} with particular X value.
 * 
 * @author Grzegorz Kruk
 */
public class DataXComparator implements Comparator<Data<?, ?>> {

    @SuppressWarnings("rawtypes")
    private final Axis xAxis;

    /**
     * Creates a new instance of DataXComparator class.
     * 
     * @param xAxis mandatory X axis associated with chart for which {@link Data} should be compared
     */
    public DataXComparator(Axis<?> xAxis) {
        assertArgNotNull(xAxis, "X axis");
        this.xAxis = xAxis;
    }

    /**
     * Creates a new instance of {@link Data} with given {@link Data#getXValue() X value}. Meant to be used to create
     * search keys when using methods like {@link Collections#binarySearch(java.util.List, Object, Comparator)
     * Collections.binarySearch}.
     * 
     * @param xValue X value of the data
     * @return Data containing given X value
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Data<?, ?> key(Object xValue) {
        return new Data(xValue, 0d);
    }

    @SuppressWarnings("unchecked")
    @Override
    public int compare(Data<?, ?> data1, Data<?, ?> data2) {
        double x1Numeric = xAxis.toNumericValue(data1.getXValue());
        double x2Numeric = xAxis.toNumericValue(data2.getXValue());
        return (int) Math.signum(x1Numeric - x2Numeric);
    }
}
