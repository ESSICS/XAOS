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
package se.europeanspallationsource.xaos.ui.plot.plugins.impl;


import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import org.apache.commons.lang3.Validate;
import se.europeanspallationsource.xaos.ui.plot.data.ErrorSeries;
import se.europeanspallationsource.xaos.ui.plot.plugins.AbstractBoundedPlugin;


/**
 * Shows horizontal and vertical error bars around chart data points.
 *
 * @param <X> The X type of the series.
 * @param <Y> The Y type of the series.
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class ErrorBars<X, Y> extends AbstractBoundedPlugin {

	private final ErrorSeries<X, Y> errorSeries;
	private Series<?, ?> series = null;

	/**
	 * @param errorSeries    List of error data for display in chart
	 * @param indexRefSeries Index of series to must own the error data. Note
	 *                       that this value will be set to the given error
	 *                       series, overwriting any previously set value.
	 * @throws NullPointerException If {@code errorSeries} is {@code null}.
	 */
	public ErrorBars( ErrorSeries<X, Y> errorSeries, int indexRefSeries ) {
		
		Validate.notNull(errorSeries, "Null 'errorSeries' parameter.");

		this.errorSeries = errorSeries;

		errorSeries.setSeriesRef(indexRefSeries);

	}

	@Override
	public <X, Y> void seriesVisibilityUpdated( Chart chart, Series<X, Y> series, int index, boolean visible ) {
		if ( this.series == series ) {
			if ( visible ) {
				drawErrorBars(chart, series);
			} else {
				hideErrorBars(series);
			}
		}
	}

	@Override
	protected void boundsChanged() {
		if ( series != null ) {
			seriesVisibilityUpdated(getChart(), series, errorSeries.getSeriesRef(), isSeriesVisible(series));
		}
	}

	@Override
	protected void chartConnected( Chart chart ) {

		super.chartConnected(chart);

		errorSeries.getData().forEach(marker -> {

			ObservableList<Node> plotChildren = getPlotChildren();

			if ( marker.isXErrorValid() ) {
				plotChildren.add(marker.getXErrorPath());
			}

			if ( marker.isYErrorValid() ) {
				plotChildren.add(marker.getYErrorPath());
			}

		});

		int index = errorSeries.getSeriesRef();

		series = ((XYChart<?, ?>) chart).getData().get(index);
		
		seriesVisibilityUpdated(chart, series, index, isSeriesVisible(series));

	}

	@Override
	protected void chartDisconnected( Chart chart ) {

		errorSeries.getData().forEach(marker -> {

			ObservableList<Node> plotChildren = getPlotChildren();

			if ( marker.isXErrorValid() ) {
				plotChildren.remove(marker.getXErrorPath());
			}

			if ( marker.isYErrorValid() ) {
				plotChildren.remove(marker.getYErrorPath());
			}

		});

		series = null;

		super.chartDisconnected(chart);

	}

	/**
	 * drawErrorBars redraws the error bars already connected to the chart.
	 * Note: Horrible hack in order to collect the display info with function toDisplayPoint(). The other data point is only included
	 * in order for the format to work.
	 *
	 * @param chart  The chart where the {@code series} visibility change occurred.
	 * @param series The {@link Series} whose visibility changed.
	 */
	@SuppressWarnings( "unchecked" )
	private void drawErrorBars( Chart chart, Series<?, ?> series ) {

		if ( series == null ) {
			return;
		}

		Axis<X> xAxis = ( (XYChart<X, Y>) chart ).getXAxis();
		Axis<Y> yAxis = ( (XYChart<X, Y>) chart ).getYAxis();

		errorSeries.getData().forEach(errorData -> {

			Node dataNode = errorData.getNode();
//			Color dataColor = ( dataNode instanceof Region )
//							? (Color) ((Region) dataNode).getBackground().getFills().get(0).getFill()
//							: Color.BLACK;
			@SuppressWarnings( "null" )
			double dataX = ( xAxis instanceof CategoryAxis )
						 ? dataNode.getBoundsInParent().getMaxX() - dataNode.getBoundsInParent().getWidth() / 2
						 : xAxis.getDisplayPosition(errorData.getXValue());
			@SuppressWarnings( "null" )
			double dataY = ( yAxis instanceof CategoryAxis )
						 ? dataNode.getBoundsInParent().getMaxY() - dataNode.getBoundsInParent().getHeight() / 2
						 : yAxis.getDisplayPosition(errorData.getYValue());

			errorData.show(xAxis, yAxis, dataX, dataY);

		});

	}

	private <X, Y> void hideErrorBars( Series<X, Y> series ) {

		if ( series == null ) {
			return;
		}

		errorSeries.getData().forEach(marker -> {
			if ( marker.isXErrorValid() ) {
				marker.getXErrorPath().setVisible(false);
			}
			if ( marker.isYErrorValid() ) {
				marker.getYErrorPath().setVisible(false);
			}
		});

	}

}
