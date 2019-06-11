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


import java.text.MessageFormat;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import org.apache.commons.lang3.Validate;
import se.europeanspallationsource.xaos.ui.plot.data.ErrorSeries;
import se.europeanspallationsource.xaos.ui.plot.data.ErrorSeries.ErrorData;
import se.europeanspallationsource.xaos.ui.plot.plugins.AbstractCursorPlugin;
import se.europeanspallationsource.xaos.ui.util.ColorUtils;


/**
 * Shows horizontal and vertical error bars around chart data points.
 *
 * @param <X> The X type of the series.
 * @param <Y> The Y type of the series.
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class ErrorBars<X, Y> extends AbstractCursorPlugin {

	private static final String DEFAULT_STYLE = "-fx-stroke-width: 0.85; -fx-stroke: black;";
	private static final String FALLBACK_STYLE = "-fx-stroke-width: 2.35;";
	private static final String HIGHLIGHT_STYLE = "-fx-stroke-width: 2.35; -fx-stroke: {0};";
	private static final String NAME = "Error Bars";

	private final ErrorSeries<X, Y> errorSeries;
	private String highlightStyle = null;
	private ErrorData<X, Y> marker = null;
	private Series<X, Y> series = null;

	/* *********************************************************************** *
	 * START OF JAVAFX PROPERTIES                                              *
	 * *********************************************************************** */

	/*
	 * ---- pickingDistance ----------------------------------------------------
	 */
    private final DoubleProperty pickingDistance = new SimpleDoubleProperty(ErrorBars.this, "pickingDistance", 10) {
        @Override
		protected void invalidated() {
			if ( get() <= 0 ) {
				throw new IllegalArgumentException("The picking distance must be a positive value.");
			}
        }
    };

	@Override
	public String getName() {
		if ( series == null ) {
			return MessageFormat.format("{0} [{1}]", super.getName(), errorSeries.getSeriesRef());
		} else {
			return MessageFormat.format("{0} [{1}]", super.getName(), series.getName());
		}
	}

    /**
	 * @return A {@link DoubleProperty} representing the distance of the mouse
	 *         cursor from the data point (expressed in display units) that
	 *         should trigger showing the tool tip.
	 */
	public DoubleProperty pickingDistanceProperty() {
		return pickingDistance;
	}

	public double getPickingDistance() {
		return pickingDistanceProperty().get();
	}

	public void setPickingDistance( double value ) {
		pickingDistanceProperty().set(value);
	}

	/* *********************************************************************** *
	 * END OF JAVAFX PROPERTIES                                                *
	 * *********************************************************************** */

	/**
	 * @param errorSeries    List of error data for display in chart
	 * @param indexRefSeries Index of series to must own the error data. Note
	 *                       that this value will be set to the given error
	 *                       series, overwriting any previously set value.
	 * @throws NullPointerException If {@code errorSeries} is {@code null}.
	 */
	public ErrorBars( ErrorSeries<X, Y> errorSeries, int indexRefSeries ) {

		super(NAME);
		
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
	@SuppressWarnings( "unchecked" )
	protected void chartConnected( Chart chart ) {

		super.chartConnected(chart);

		ObservableList<Node> plotChildren = getPlotChildren();

		errorSeries.getData().forEach(errorData -> {

			if ( errorData.isXErrorValid() ) {
				plotChildren.add(errorData.getXErrorPath());
			}

			if ( errorData.isYErrorValid() ) {
				plotChildren.add(errorData.getYErrorPath());
			}

		});

		int index = errorSeries.getSeriesRef();

		series = ((XYChart<X, Y>) chart).getData().get(index);
		
		seriesVisibilityUpdated(chart, series, index, isSeriesVisible(series));

	}

	@Override
	protected void chartDisconnected( Chart chart ) {

		ObservableList<Node> plotChildren = getPlotChildren();

		errorSeries.getData().forEach(ErrorData -> {

			if ( ErrorData.isXErrorValid() ) {
				plotChildren.remove(ErrorData.getXErrorPath());
			}

			if ( ErrorData.isYErrorValid() ) {
				plotChildren.remove(ErrorData.getYErrorPath());
			}

		});

		series = null;

		super.chartDisconnected(chart);

	}

	@Override
	protected void dragDetected( MouseEvent e ) {
		//	Nothing to do.
	}

	@Override
	protected void mouseMove( MouseEvent event ) {
		super.mouseMove(event);
		performMove(getSceneMouseLocation());
	}

	/**
	 * {@code drawErrorBars} redraws the error bars already connected to the chart.
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

		errorSeries.getData().forEach(errorData -> {
			if ( errorData.isXErrorValid() ) {
				errorData.getXErrorPath().setVisible(false);
			}
			if ( errorData.isYErrorValid() ) {
				errorData.getYErrorPath().setVisible(false);
			}
		});

	}

	@SuppressWarnings( "unchecked" )
	private void performMove( Point2D sceneMouseLocation ) {

		if ( isInsidePlotArea(sceneMouseLocation) ) {

			Point2D mouseLocation = getLocationInPlotArea(sceneMouseLocation);
			Axis<X> xAxis = ( (XYChart<X, Y>) getChart() ).getXAxis();
			Axis<Y> yAxis = ( (XYChart<X, Y>) getChart() ).getYAxis();

			errorSeries.getData().parallelStream()
				.map(d -> new Pair<>(d, mouseLocation.distance(new Point2D(
					xAxis.getDisplayPosition(d.getXValue()),
					yAxis.getDisplayPosition(d.getYValue())
				))))
				.filter(p -> p.getValue() <= getPickingDistance())
				.sorted(( p1, p2 ) -> p1.getValue() < p2.getValue() ? -1 : p1.getValue() > p2.getValue() ? 1 : 0)
				.map(p -> p.getKey())
				.findFirst()
				.ifPresentOrElse(
					pickedErrorData -> {
						if ( marker == null || marker != pickedErrorData ) {

							if ( marker != null ) {
								if ( marker.isXErrorValid() ) {
									marker.getXErrorPath().setStyle(DEFAULT_STYLE);
								}
								if ( marker.isYErrorValid() ) {
									marker.getYErrorPath().setStyle(DEFAULT_STYLE);
								}
							}

							Node node = pickedErrorData.getNode();

							if ( highlightStyle == null && node instanceof Region ) {
								try {

									Color dataColor = (Color) ((Region) node).getBackground().getFills().get(0).getFill();

									highlightStyle = MessageFormat.format(HIGHLIGHT_STYLE, ColorUtils.toWeb(dataColor));

								} catch ( NullPointerException npex ) {
									//	Can happen that in certain situations dataColor evaluation
									//	will throw NPE because some element in the call path is null.
									highlightStyle = FALLBACK_STYLE;
								}
							}

							marker = pickedErrorData;

							if ( marker.isXErrorValid() ) {
								marker.getXErrorPath().setStyle(highlightStyle);
							}
							if ( marker.isYErrorValid() ) {
								marker.getYErrorPath().setStyle(highlightStyle);
							}

						}
					},
					() -> {
						if ( marker != null ) {

							if ( marker.isXErrorValid() ) {
								marker.getXErrorPath().setStyle("-fx-stroke-width: 0.85; -fx-stroke: black;");
							}
							if ( marker.isYErrorValid() ) {
								marker.getYErrorPath().setStyle("-fx-stroke-width: 0.85; -fx-stroke: black;");
							}

							marker = null;

						}
					}
				);

		}

	}

}
