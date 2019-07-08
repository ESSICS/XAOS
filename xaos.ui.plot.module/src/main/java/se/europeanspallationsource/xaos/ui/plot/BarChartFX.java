/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (C) 2018-2019 by European Spallation Source ERIC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package se.europeanspallationsource.xaos.ui.plot;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.ValueAxis;
import org.apache.commons.lang3.Validate;
import se.europeanspallationsource.xaos.core.util.LogUtils;
import se.europeanspallationsource.xaos.ui.plot.Legend.LegendItem;
import se.europeanspallationsource.xaos.ui.plot.plugins.Pluggable;
import se.europeanspallationsource.xaos.ui.plot.util.SeriesColorUtils;

import static java.util.logging.Level.WARNING;


/**
 * A thin extension of the FX {@link BarChart} supporting custom
 * {@link Plugin plugin} implementations.
 *
 * @author Grzegorz Kruk
 * @param <X> type of X values
 * @param <Y> type of Y values
 */
public class BarChartFX<X, Y> extends BarChart<X, Y> implements Pluggable {

	private static final Logger LOGGER = Logger.getLogger(BarChartFX.class.getName());

	private final List<String> notShownInLegend = new ArrayList<>(4);
	private final Group pluginsNodesGroup = new Group();
	private final PluginManager pluginManager = new PluginManager(this, pluginsNodesGroup);
	private final List<String> seriesDrawnInPlot = new ArrayList<>(4);

	/**
	 * Construct a new bar chart with the given axis. The two axis should be a
	 * {@link ValueAxis}/{@link NumberAxis} and a {@link CategoryAxis}, they can
	 * be in either order depending on if you want a horizontal or vertical bar
	 * chart.
	 *
	 * @param xAxis The x axis to use.
	 * @param yAxis The y axis to use.
	 * @see javafx.scene.chart.BarChart#BarChart(Axis, Axis)
	 */
	public BarChartFX( Axis<X> xAxis, Axis<Y> yAxis ) {
		this(xAxis, yAxis, FXCollections.<Series<X, Y>>observableArrayList());
	}

	/**
	 * Construct a new bar chart with the given axis and data. The two axis
	 * should be a {@link ValueAxis}/{@link NumberAxis} and a
	 * {@link CategoryAxis}, they can be in either order depending on if you
	 * want a horizontal or vertical bar chart.
	 *
	 * @param xAxis The x axis to use.
	 * @param yAxis The y axis to use.
	 * @param data  The data to use, this is the actual list used so any changes
	 *              to it will be reflected in the chart.
	 * @see javafx.scene.chart.BarChart#BarChart(Axis, Axis, ObservableList)
	 */
	public BarChartFX( Axis<X> xAxis, Axis<Y> yAxis, ObservableList<Series<X, Y>> data ) {

		super(xAxis, yAxis, data);

		getStylesheets().add(BarChartFX.class.getResource("/styles/chart.css").toExternalForm());
		getPlotChildren().add(pluginsNodesGroup);

	}

	/**
	 * More robust method for adding plugins to chart.
	 * <p>
	 * <b>Note:</b> Only necessary if more than one plugin is being added at
	 * once.</p>
	 *
	 * @param plugins List of {@link Plugin}s to be added.
	 */
	public void addChartPlugins( ObservableList<Plugin> plugins ) {
		plugins.forEach(plugin -> {
			try {
				pluginManager.getPlugins().add(plugin);
			} catch ( Exception ex ) {
				LogUtils.log(
					LOGGER,
					WARNING,
					"Error occured whilst adding {0} [{1}].",
					plugin.getClass().getName(),
					ex.getMessage()
				);
			}
		});

	}

	@Override
	public Chart getChart() {
		return this;
	}

	@Override
	public ObservableList<LegendItem> getLegendItems() {

		Node legend = getLegend();

		if ( legend instanceof Legend ) {
			return ( (Legend) legend ).getItems();
		} else {
			return FXCollections.emptyObservableList();
		}

	}

	@Override
	public final ObservableList<Node> getPlotChildren() {
		return super.getPlotChildren();
	}

	@Override
	public final ObservableList<Plugin> getPlugins() {
		return pluginManager.getPlugins();
	}

	@Override
	public boolean isNotShownInLegend( String name ) {
		return notShownInLegend.contains(name);
	}

	public boolean isSeriesDrawn( String name ) {
		return seriesDrawnInPlot.contains(name);
	}

	/**
	 * Sets which series has to be considered "horizontal", "vertical" and
	 * "longitudinal". Special colors will be used to represent horizontal
	 * (red), vertical (blue) and longitudinal (green) series.
	 *
	 * @param horizontal   Index of the horizontal series. Use -1 if no
	 *                     horizontal series exists.
	 * @param vertical     Index of the vertical series. Use -1 if no vertical
	 *                     series exists.
	 * @param longitudinal Index of the longitudinal series. Use -1 if no
	 *                     longitudinal series exists.
	 */
	public final void setHVLSeries( int horizontal, int vertical, int longitudinal ) {

		int size = getData().size();

		Validate.isTrue(horizontal < size, "Out of range 'horizontal' parameter.");
		Validate.isTrue(vertical < size, "Out of range 'vertical' parameter.");
		Validate.isTrue(longitudinal < size, "Out of range 'longitudinal' parameter.");

		lookup(".chart").setStyle(SeriesColorUtils.styles(horizontal, vertical, longitudinal));

	}

	@Override
	public final void setNotShownInLegend( String name ) {
		notShownInLegend.add(name);
		updateLegend();
	}

	@Override
	protected void layoutPlotChildren() {

		//	Layout plot children. This call will create fresh new symbols
		//	that are by default visible.
		super.layoutPlotChildren();

		//	If the track is hidden, then hide the symbols.
		getData().stream()
			.filter(series -> !seriesDrawnInPlot.contains(series.getName()))
			.flatMap(series -> series.getData().stream())
			.forEach(d -> d.getNode().setVisible(false));

		//	Move plugins nodes to front.
		ObservableList<Node> plotChildren = getPlotChildren();

		plotChildren.remove(pluginsNodesGroup);
		plotChildren.add(pluginsNodesGroup);

	}

	@Override
	protected void updateLegend() {

		final Legend legend = new Legend();

		seriesDrawnInPlot.clear();

		for ( int i = 0; i < getData().size(); i++ ) {

			final int seriesIndex = i;
			Series<X, Y> series = getData().get(seriesIndex);
			String seriesName = series.getName();

			if ( !notShownInLegend.contains(seriesName) ) {

				Legend.LegendItem legenditem = new Legend.LegendItem(seriesName, selected -> {

					series.getData().forEach(d -> d.getNode().setVisible(selected));

					if ( selected ) {
						if ( !seriesDrawnInPlot.contains(seriesName) ) {
							seriesDrawnInPlot.add(seriesName);
						}
					} else {
						seriesDrawnInPlot.remove(seriesName);
					}

					getPlugins().forEach(p -> p.seriesVisibilityUpdated(this, series, seriesIndex, selected));

				});

				legenditem.getSymbol().getStyleClass().addAll(
					"chart-area-symbol",
					"area-legend-symbol",
					"default-color" + ( seriesIndex % 8 ),
					"series" + seriesIndex
				);

				seriesDrawnInPlot.add(seriesName);
				legend.getItems().add(legenditem);

			}

		}

		setLegend(legend);

	}

}
