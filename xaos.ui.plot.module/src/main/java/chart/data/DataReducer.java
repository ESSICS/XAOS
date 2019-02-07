/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
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
