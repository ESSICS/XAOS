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

import chart.Plugin;
import java.text.Format;
import java.text.MessageFormat;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import se.europeanspallationsource.xaos.ui.util.ColorUtils;

/**
 * A {@link Plugin} displaying a {@link Label} next to the mouse cursor, showing
 * the coordinates of the closest chart data point, the one that is within the
 * {@link #pickingDistanceProperty() pickingDistance} from the mouse cursor.
 * <p>
 * The display's relative position to the mouse cursor can be adjusted using the
 * {@link #positionProperty() position} property. By default its set to
 * {@link Position#CENTER}.</p>
 * <p>
 * The formatter used can be adjusted by the
 * {@link #formatterProperty() formatter} property.</p>
 *
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( "ClassWithoutLogger" )
public final class DataPointCursorDisplay extends FormattedCursorDisplay {

	private static final MessageFormat FORMATTER = new MessageFormat("{0,number,0.000}:{1,number,0.000}");

	/* *********************************************************************** *
	 * START OF JAVAFX PROPERTIES                                              *
	 * *********************************************************************** */

	/*
	 * ---- pickingDistance ----------------------------------------------------
	 */
    private final DoubleProperty pickingDistance = new SimpleDoubleProperty(DataPointCursorDisplay.this, "pickingDistance", 10) {
        @Override
		protected void invalidated() {
			if ( get() <= 0 ) {
				throw new IllegalArgumentException("The picking distance must be a positive value.");
			}
        }
    };

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

	public DataPointCursorDisplay() {
		super(Position.CENTER, FORMATTER);
	}

	public DataPointCursorDisplay( Position position ) {
		super(position, FORMATTER);
	}

	public DataPointCursorDisplay( Position position, Format formatter ) {
		super(position, formatter);
	}

	@Override
	@SuppressWarnings( { "unchecked", "null" } )
	protected void chartConnected( Chart chart ) {
		if ( ! ( chart instanceof XYChart<?, ?> ) ) {
			throw new UnsupportedOperationException(MessageFormat.format(
				"{0} non supported.",
				chart.getClass().getSimpleName()
			));
		} else {
			super.chartConnected(chart);
		}
	}

	@Override
	protected String formatValue( Object value ) {

		if ( value == null ) {
			return null;
		} else {

			Data<?, ?> dataPoint = (Data<?, ?>) value;
			Node node = dataPoint.getNode();

			if ( node instanceof Region ) {
				try {

					Color dataColor = (Color) ((Region) node).getBackground().getFills().get(0).getFill();

					getDisplay().setStyle(MessageFormat.format(
						"-xaos-chart-cursor-display-background-color: {0}; "
					  + "-xaos-chart-cursor-display-text-color: {1};",
						ColorUtils.toWeb(ColorUtils.changeOpacity(dataColor.desaturate(), -0.2)),
						ColorUtils.toWeb(ColorUtils.bestConstrasting(dataColor, Color.BLACK, Color.WHITE))
					));

				} catch ( NullPointerException npex ) {
					//	Can happen that in certain situations dataColor evaluation
					//	will throw NPE because some element in the call path is null.
				}
			}

			return getFormatter().format(new Object[] { dataPoint.getXValue(), dataPoint.getYValue() });

		}

	}

	@Override
	@SuppressWarnings( "unchecked" )
	protected Object valueAtPosition( Point2D mouseLocation ) {

		Axis xAxis = ( (XYChart<?, ?>) getChart() ).getXAxis();
		Axis yAxis = ( (XYChart<?, ?>) getChart() ).getYAxis();

		return ((XYChart<?, ?>) getChart())
			.getData()
			.parallelStream()
			.filter(series -> isSeriesVisible(series))
			.flatMap(series -> series.getData().stream())
			.map(d -> new Pair<>(d, mouseLocation.distance(new Point2D(
				xAxis.getDisplayPosition(d.getXValue()),
				yAxis.getDisplayPosition(d.getYValue())
			))))
			.filter(p -> p.getValue() <= getPickingDistance())
			.unordered()
			.sorted(( p1, p2 ) -> p1.getValue() < p2.getValue() ? -1 : p1.getValue() > p2.getValue() ? 1 : 0)
			.map(p -> p.getKey())
			.findFirst()
			.orElse(null);

	}

}
