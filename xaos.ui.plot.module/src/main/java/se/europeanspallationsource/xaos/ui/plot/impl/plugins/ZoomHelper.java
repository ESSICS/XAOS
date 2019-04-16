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
package se.europeanspallationsource.xaos.ui.plot.impl.plugins;


import chart.Plugin;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.chart.ValueAxis;
import javafx.util.Duration;
import se.europeanspallationsource.xaos.ui.plot.impl.util.ChartUndoManager;


/**
 * Helper class supporting chart zoom operations.
 *
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( "ClassWithoutLogger" )
class ZoomHelper {

	static final Duration DEFAULT_ANIMATION_DURATION = Duration.millis(500);

	private final Plugin plugin;
	private Timeline zoomTimeline = new Timeline();;

	ZoomHelper( Plugin plugin ) {
		this.plugin = plugin;
	}

	void autoScale() {
		ChartUndoManager.get(plugin.getChart()).captureUndoable(plugin);
		plugin.getXValueAxis().setAutoRanging(true);
		plugin.getYValueAxis().setAutoRanging(true);
	}

	void zoomIn( boolean captureUndoable ) {

		if ( captureUndoable ) {
			ChartUndoManager.get(plugin.getChart()).captureUndoable(plugin);
		}

		ValueAxis<?> xAxis = plugin.getXValueAxis();
		ValueAxis<?> yAxis = plugin.getYValueAxis();
		double plotHeight = yAxis.getUpperBound() - yAxis.getLowerBound();
		double plotWidth  = xAxis.getUpperBound() - xAxis.getLowerBound();

		xAxis.setAutoRanging(false);
		yAxis.setAutoRanging(false);
		xAxis.setLowerBound(xAxis.getLowerBound() + 0.1 * plotWidth);
		xAxis.setUpperBound(xAxis.getUpperBound() - 0.1 * plotWidth);
		yAxis.setLowerBound(yAxis.getLowerBound() + 0.1 * plotHeight);
		yAxis.setUpperBound(yAxis.getUpperBound() - 0.1 * plotHeight);

	}

	void zoomOut( boolean captureUndoable ) {

		if ( captureUndoable ) {
			ChartUndoManager.get(plugin.getChart()).captureUndoable(plugin);
		}

		ValueAxis<?> xAxis = plugin.getXValueAxis();
		ValueAxis<?> yAxis = plugin.getYValueAxis();
		double plotHeight = yAxis.getUpperBound() - yAxis.getLowerBound();
		double plotWidth  = xAxis.getUpperBound() - xAxis.getLowerBound();

		xAxis.setAutoRanging(false);
		yAxis.setAutoRanging(false);
		xAxis.setLowerBound(xAxis.getLowerBound() - 0.1 * plotWidth);
		xAxis.setUpperBound(xAxis.getUpperBound() + 0.1 * plotWidth);
		yAxis.setLowerBound(yAxis.getLowerBound() - 0.1 * plotHeight);
		yAxis.setUpperBound(yAxis.getUpperBound() + 0.1 * plotHeight);

	}

	void zoom ( double xLowerBound, double xUpperBound, double yLowerBound, double yUpperBound, boolean animated, Duration duration ) {

		zoomTimeline.stop();

		ChartUndoManager.get(plugin.getChart()).captureUndoable(plugin);

		ValueAxis<?> xAxis = plugin.getXValueAxis();
		ValueAxis<?> yAxis = plugin.getYValueAxis();

		xAxis.setAutoRanging(false);
		yAxis.setAutoRanging(false);

		if ( animated ) {
			zoomTimeline.getKeyFrames().setAll(
				new KeyFrame(
					Duration.ZERO,
					new KeyValue(xAxis.lowerBoundProperty(), xAxis.getLowerBound()),
					new KeyValue(xAxis.upperBoundProperty(), xAxis.getUpperBound()),
					new KeyValue(yAxis.lowerBoundProperty(), yAxis.getLowerBound()),
					new KeyValue(yAxis.upperBoundProperty(), yAxis.getUpperBound())
				),
				new KeyFrame(
					duration == null ? DEFAULT_ANIMATION_DURATION : duration,
					new KeyValue(xAxis.lowerBoundProperty(), xLowerBound),
					new KeyValue(xAxis.upperBoundProperty(), xUpperBound),
					new KeyValue(yAxis.lowerBoundProperty(), yLowerBound),
					new KeyValue(yAxis.upperBoundProperty(), yUpperBound)
				)
			);
			zoomTimeline.play();
		} else {
			xAxis.setLowerBound(xLowerBound);
			xAxis.setUpperBound(xUpperBound);
			yAxis.setLowerBound(yLowerBound);
			yAxis.setUpperBound(yUpperBound);
		}

	}

}
