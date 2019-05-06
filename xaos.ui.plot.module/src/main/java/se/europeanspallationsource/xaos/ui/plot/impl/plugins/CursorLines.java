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


import chart.DensityChartFX;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.chart.ValueAxis;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;
import se.europeanspallationsource.xaos.ui.plot.plugins.AbstractCursorPlugin;


/**
 * Horizontal and vertical {@link Line}s drawn on the plot area, crossing at the
 * mouse cursor location.
 *
 * @author claudio.rosati@esss.se
 * @css.class {@code chart-cursor-lines}
 */
@SuppressWarnings( "ClassWithoutLogger" )
public final class CursorLines extends AbstractCursorPlugin {

	private final Line horizontalLine = new Line();
	private final Line verticalLine = new Line();

	/**
	 * Creates a new instance of this plugin.
	 */
	public CursorLines() {

		verticalLine.getStyleClass().add("chart-cursor-lines");
		verticalLine.setManaged(false);
		verticalLine.setVisible(false);
		horizontalLine.getStyleClass().add("chart-cursor-lines");
		horizontalLine.setManaged(false);
		horizontalLine.setVisible(false);

		getPlotChildren().addAll(verticalLine, horizontalLine);

	}

	@Override
	protected void boundsChanged() {
		//	Nothing to do.
	}

	@Override
	protected void dragDetected( MouseEvent event ) {
		horizontalLine.setVisible(false);
		verticalLine.setVisible(false);
	}

	@Override
	protected void mouseMove( MouseEvent event ) {

		super.mouseMove(event);

		if ( isInsidePlotArea(getSceneMouseLocation()) ) {

			Point2D mouseLocation = getLocationInPlotArea(getSceneMouseLocation());
			double mouseX = mouseLocation.getX();
			double mouseY = mouseLocation.getY();
			Bounds plotAreaBounds = getPlotAreaBounds();

			if ( getChart() instanceof DensityChartFX<?, ?> ) {

				ValueAxis<?> xAxis = getXValueAxis();
				ValueAxis<?> yAxis = getYValueAxis();

				mouseLocation = mouseLocation.add(new Point2D(xAxis.getLayoutX(), yAxis.getLayoutY()));
				mouseX = mouseLocation.getX();
				mouseY = mouseLocation.getY();

				horizontalLine.setStartX(xAxis.getLayoutX());
				horizontalLine.setEndX(xAxis.getWidth() + xAxis.getLayoutX());
				verticalLine.setStartY(yAxis.getLayoutY());
				verticalLine.setEndY(yAxis.getHeight() + yAxis.getLayoutY());

			} else {
				horizontalLine.setStartX(0);
				horizontalLine.setEndX(plotAreaBounds.getWidth());
				verticalLine.setStartY(0);
				verticalLine.setEndY(plotAreaBounds.getHeight());
			}

			horizontalLine.setStartY(mouseY);
			horizontalLine.setEndY(mouseY);
			verticalLine.setStartX(mouseX);
			verticalLine.setEndX(mouseX);

			if ( !horizontalLine.isVisible() ) {
				horizontalLine.setVisible(true);
				verticalLine.setVisible(true);
			}

		} else {
			horizontalLine.setVisible(false);
			verticalLine.setVisible(false);
		}

	}

}
