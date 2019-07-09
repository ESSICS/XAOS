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


import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import javafx.scene.chart.XYChart.Data;


/**
 * {@link DataReducer} implementation based on a modified version of <a
 * href="https://en.wikipedia.org/wiki/Ramer%E2%80%93Douglas%E2%80%93Peucker_algorithm">
 * Ramer-Douglas-Peucker algorithm</a>.
 * <p>
 * Compared to the original algorithm that removes points that don't introduce
 * error bigger than given epsilon, this implementation takes as argument the
 * desired number of points, removing from the curve the points in the order of
 * error size introduced i.e. points whose removal introduces smallest error are
 * removed first. The algorithm stops once the curve has the desired number of
 * points.</p>
 *
 * @param <X> Type of X values.
 * @param <Y> Type of Y values.
 * @author Grzegorz Kruk (original author).
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( "ClassWithoutLogger" )
public final class RamerDouglasPeuckerDataReducer<X extends Number, Y extends Number> implements DataReducer<X, Y> {

	private static final Comparator<Range> RANGE_KEY_POINT_DISTANCE_INVERTED_COMPARATOR = ( r1, r2 ) -> (int) Math.signum(r2.keyPointDistance - r1.keyPointDistance);

	@Override
	public List<Data<X, Y>> reduce( final List<Data<X, Y>> points, int targetPointsCount ) {

		if ( points == null ) {
			throw new IllegalArgumentException("Null list of data points.");
		} else if ( targetPointsCount < MIN_TARGET_POINTS_COUNT ) {
			throw new IllegalArgumentException(MessageFormat.format(
				"Target number of points expected to be greater or equal to {0}.",
				MIN_TARGET_POINTS_COUNT
			));
		}

		if ( points.size() <= targetPointsCount ) {
			return points;
		}

		BitSet keyPoints = new BitSet(points.size());

		keyPoints.set(0);
		keyPoints.set(points.size() - 1);

		if ( targetPointsCount > MIN_TARGET_POINTS_COUNT ) {
			appendKeyPoints(points, targetPointsCount, keyPoints);
		}

		return extractKeyPoints(points, keyPoints);

	}

	private void appendKeyPoints( List<Data<X, Y>> points, int targetPointsCount, BitSet keyPoints ) {

		Queue<Range> queue = new PriorityQueue<>(RANGE_KEY_POINT_DISTANCE_INVERTED_COMPARATOR);

		queue.add(computeRange(0, points.size() - 1, points));

		do {

			Range range = queue.poll();

			keyPoints.set(range.keyPointIndex);

			if ( !range.leftSubRangeEmpty() ) {
				queue.add(computeRange(range.firstIndex, range.keyPointIndex, points));
			}

			if ( !range.rightSubRangeEmpty() ) {
				queue.add(computeRange(range.keyPointIndex, range.lastIndex, points));
			}

		} while ( keyPoints.cardinality() < targetPointsCount );

	}

	private Range computeRange( int firstIndex, int lastIndex, List<Data<X, Y>> points ) {

		int keyPointIndex = -1;
		double keyPointDistance = -1;

		for ( int i = firstIndex + 1; i < lastIndex; ++i ) {

			double distance = distance(points.get(firstIndex), points.get(lastIndex), points.get(i));

			if ( distance > keyPointDistance ) {
				keyPointIndex = i;
				keyPointDistance = distance;
			}

		}

		return new Range(firstIndex, lastIndex, keyPointIndex, keyPointDistance);

	}

	private double distance( Data<X, Y> lineStart, Data<X, Y> lineEnd, Data<X, Y> point ) {

		double deltaX = lineStart.getXValue().doubleValue() - lineEnd.getXValue().doubleValue();
		double deltaY = lineStart.getYValue().doubleValue() - lineEnd.getYValue().doubleValue();
		double sxey = lineStart.getXValue().doubleValue() * lineEnd.getYValue().doubleValue();
		double exsy = lineEnd.getXValue().doubleValue() * lineStart.getYValue().doubleValue();
		double length = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

		return Math.abs(deltaY * point.getXValue().doubleValue() - deltaX * point.getYValue().doubleValue() + sxey - exsy) / length;

	}

	private List<Data<X, Y>> extractKeyPoints( List<Data<X, Y>> points, BitSet keyPoints ) {

		List<Data<X, Y>> dataPoints = new ArrayList<>(keyPoints.cardinality());

		for ( int i = keyPoints.nextSetBit(0); i >= 0; i = keyPoints.nextSetBit(i + 1) ) {
			dataPoints.add(points.get(i));
		}

		return dataPoints;

	}

	private static class Range {

		final int firstIndex;
		final double keyPointDistance;
		final int keyPointIndex;
		final int lastIndex;

		Range( int first, int last, int keyPointIndex, double keyPointDistance ) {
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
