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

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import javafx.scene.chart.XYChart.Data;

/**
 * {@link DataReducer} implementation based on a modified version of <a
 * href="https://en.wikipedia.org/wiki/Ramer%E2%80%93Douglas%E2%80%93Peucker_algorithm">Ramer-Douglas-Peucker
 * algorithm</a>. 
 * <p>
 * Compared to the original algorithm that removes points that don't introduce error bigger than given 
 * epsilon, this implementation takes as argument the desired number of points, removing from the curve the points
 * in the order of error size introduced i.e. points whose removal introduces smallest error are removed first. 
 * The algorithm stops once the curve has the desired number of points.
 * </p>
 * @param <X> type of X values
 * @param <Y> type of Y values
 * 
 * @author Grzegorz Kruk
 */
public final class RamerDouglasPeuckerDataReducer<X extends Number, Y extends Number> implements DataReducer<X, Y> {

    private static final int MIN_TARGET_POINTS_COUNT = 2;
    private static final Comparator<Range> RANGE_KEY_POINT_DISTANCE_INVERTED_COMPARATOR = new Comparator<Range>() {
        @Override public int compare(Range range1, Range range2) {
            return (int) Math.signum(range2.keyPointDistance - range1.keyPointDistance);
        }
    };
    
    @Override
    public List<Data<X, Y>> reduce(List<Data<X, Y>> points, int targetPointsCount) {
        checkArgs(points, targetPointsCount);
        
        if (points.size() <= targetPointsCount) { 
            return new ArrayList<>(points);
        }
        
        BitSet keyPointIndices = new BitSet(points.size());
        keyPointIndices.set(0);
        keyPointIndices.set(points.size() - 1);
        
        if (targetPointsCount > MIN_TARGET_POINTS_COUNT) {
            appendKeyPointIndices(points, targetPointsCount, keyPointIndices);
        }
        return extractKeyPoints(points, keyPointIndices);
    }

    private void checkArgs(List<Data<X, Y>> points, int targetPointsCount) {
        if (points == null) {
            throw new IllegalArgumentException("List of points must not be null");
        }
        if (targetPointsCount < MIN_TARGET_POINTS_COUNT) {
            throw new IllegalArgumentException("Target number of points must be at least 2");
        }
    }
    
    private void appendKeyPointIndices(List<Data<X, Y>> points, int targetPointsCount, BitSet keyPointIndices) {
        Queue<Range> queue = new PriorityQueue<>(RANGE_KEY_POINT_DISTANCE_INVERTED_COMPARATOR);
        queue.add(computeRange(0, points.size() - 1, points));
        
        do {
            Range range = queue.poll();
            keyPointIndices.set(range.keyPointIndex);
            if (!range.leftSubRangeEmpty()) {
                queue.add(computeRange(range.firstIndex, range.keyPointIndex, points));
            }
            if (!range.rightSubRangeEmpty()) {
                queue.add(computeRange(range.keyPointIndex, range.lastIndex, points));
            }
        } while (keyPointIndices.cardinality() < targetPointsCount);
    }

    private Range computeRange(int firstIndex, int lastIndex, List<Data<X, Y>> points) {
        int keyPointIndex = -1;
        double keyPointDistance = -1;
        for (int i = firstIndex + 1; i < lastIndex; ++i) {
            double distance = distance(points.get(firstIndex), points.get(lastIndex), points.get(i));
            if (distance > keyPointDistance) {
                keyPointIndex = i;
                keyPointDistance = distance;
            }
        }
        return new Range(firstIndex, lastIndex, keyPointIndex, keyPointDistance);
    }

    private double distance(Data<X, Y> lineStart, Data<X, Y> lineEnd, Data<X, Y> point) {
        double deltaX = lineStart.getXValue().doubleValue() - lineEnd.getXValue().doubleValue();
        double deltaY = lineStart.getYValue().doubleValue() - lineEnd.getYValue().doubleValue();
        double sxey = lineStart.getXValue().doubleValue() * lineEnd.getYValue().doubleValue();
        double exsy = lineEnd.getXValue().doubleValue() * lineStart.getYValue().doubleValue();
        double length = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        return Math.abs(deltaY * point.getXValue().doubleValue() - deltaX * point.getYValue().doubleValue() + sxey - exsy) / length;
    }
    
    private List<Data<X, Y>> extractKeyPoints(List<Data<X, Y>> points, BitSet keyPointIndices) {
        List<Data<X, Y>> keyPoints = new ArrayList<>(keyPointIndices.cardinality());
        for (int i = keyPointIndices.nextSetBit(0); i >= 0; i = keyPointIndices.nextSetBit(i + 1)) {
            keyPoints.add(points.get(i));
        }
        return keyPoints;
    }
    
    private static class Range {
        final int firstIndex;
        final int lastIndex;
        final int keyPointIndex;
        final double keyPointDistance;
        
        Range(int first, int last, int keyPointIndex, double keyPointDistance) {
            this.firstIndex = first;
            this.lastIndex = last;
            this.keyPointIndex = keyPointIndex;
            this.keyPointDistance = keyPointDistance;
        }
        
        boolean leftSubRangeEmpty() {
            return keyPointIndex == firstIndex + 1;
        }
        
        boolean rightSubRangeEmpty() {
            return keyPointIndex == lastIndex - 1;
        }
    }
}
