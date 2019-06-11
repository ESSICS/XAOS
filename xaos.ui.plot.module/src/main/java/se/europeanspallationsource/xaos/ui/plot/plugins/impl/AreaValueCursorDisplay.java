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


import chart.AreaChartFX;
import chart.Plugin;
import java.text.Format;
import java.text.MessageFormat;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.Chart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.util.Pair;
import se.europeanspallationsource.xaos.ui.util.ColorUtils;


/**
 * A {@link Plugin} displaying a {@link Label} next to the mouse cursor, showing
 * the chart area value under the mouse cursor.
 * <p>
 * The display's relative position to the mouse cursor can be adjusted using the
 * {@link #positionProperty() position} property. By default its set to
 * {@link Position#TOP}.</p>
 * <p>
 * The formatter used can be adjusted by the
 * {@link #formatterProperty() formatter} property.</p>
 *
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class AreaValueCursorDisplay extends FormattedCursorDisplay {

	private static final MessageFormat FORMATTER = new MessageFormat("Area: {0,number,0.000}");
	private static final String NAME = "Area Value Cursor Display";

	public AreaValueCursorDisplay() {
		super(NAME, Position.TOP, FORMATTER);
	}

	public AreaValueCursorDisplay( Position position ) {
		super(NAME, position, FORMATTER);
	}

	public AreaValueCursorDisplay( Position position, Format formatter ) {
		super(NAME, position, formatter);
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

			@SuppressWarnings( "unchecked" )
			Data<String, Number> data = (Data<String, Number>) value;
			String seriesName = data.getXValue();
			double seriesArea = data.getYValue().doubleValue();
			Node node = (Node) data.getExtraValue();

			if ( node instanceof Group ) {

				Group group = (Group) node;
				ObservableList<Node> children = group.getChildren();
				Node child = children.isEmpty() ? null : children.get(0);

				if ( child != null && child instanceof Shape ) {
					try {

						Color areaColor = (Color) ((Shape) child).getFill();

						getDisplay().setStyle(MessageFormat.format(
							"-xaos-chart-cursor-display-background-color: {0}; "
						  + "-xaos-chart-cursor-display-text-color: #000000;",
							ColorUtils.toWeb(areaColor)
						));

					} catch ( NullPointerException npex ) {
						//	Can happen that in certain situations dataColor evaluation
						//	will throw NPE because some element in the call path is null.
					}
				}

			}

			//	Inside MessageFormat {0} will be the series area value, {1} the series name.
			return getFormatter().format(new Object[] { seriesArea, seriesName });

		}

	}

	@Override
	protected Object valueAtPosition( Point2D mouseLocation ) {

		if ( !( getChart() instanceof AreaChartFX ) ) {
			return null;
		}

		return ((AreaChartFX<?, ?>) getChart())
			.getData()
			.parallelStream()
			.filter(series -> isSeriesVisible(series))
			.map(series -> new Pair<>(series, series.getNode()))
			.filter(pair -> pair.getValue().contains(mouseLocation))
			.map(pair -> new XYChart.Data<>(
				pair.getKey().getName(),
				computeArea(pair.getKey()),
				pair.getValue()
			))
			.findFirst()
			.orElse(null);

	}

	private Number computeArea( Series<?, ?> series ) {

		//	Using trapezoidal rule.
		Double area = 0.0;
		@SuppressWarnings( "unchecked" )
		ObservableList<Data<?, ?>> data = (ObservableList<Data<?, ?>>) series.getData();

		for ( Integer index = 1; index < data.size(); index++ ) {

			Data<?, ?> data0 = data.get(index - 1);
			Data<?, ?> data1 = data.get(index);
			Double b = Double.parseDouble(data1.getXValue().toString());
			Double a = Double.parseDouble(data0.getXValue().toString());
			Double fb = Double.parseDouble(data1.getYValue().toString());
			Double fa = Double.parseDouble(data0.getYValue().toString());

			area += Math.abs(( b - a ) / 2 * ( fa + fb ));

		}

		return area;

	}

}
