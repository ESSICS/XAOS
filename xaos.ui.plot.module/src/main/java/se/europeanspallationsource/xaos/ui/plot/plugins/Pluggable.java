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
package se.europeanspallationsource.xaos.ui.plot.plugins;


import chart.Plugin;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.Chart;
import se.europeanspallationsource.xaos.ui.plot.Legend;
import se.europeanspallationsource.xaos.ui.plot.Legend.LegendItem;


/**
 * Defines the needed behavior for a plot chart to be plugged with
 * {@link Plugin}s.
 *
 * @author claudio.rosati@esss.se
 */
public interface Pluggable {

	/**
	 * @return The "pluggable" chart.
	 */
	Chart getChart();

	/**
	 * @return The an {@link ObservableList} of {@link LegendItem} displayed in
	 *         the {@link Legend} of the pluggable chart.
	 */
	ObservableList<LegendItem> getLegendItems();

	/**
	 * @return The pluggable chart's plot children list.
	 */
	ObservableList<Node> getPlotChildren();

	/**
	 * @return A non-{@code null} list of plugins added to the chart.
	 */
	ObservableList<Plugin> getPlugins();

	/**
	 * Returns whether the series with the given name is displayed in chart
	 * {@link Legend} or not.
	 *
	 * @param seriesName The name of the series to be checked.
	 * @return {@code true} if the series with the given name is not shown in
	 *         chart {@link Legend}.
	 */
	boolean isNotShowInLegend( String seriesName );

	/**
	 * Specifies a series to not be shown in chart {@link Legend}.
	 *
	 * @param seriesName The name of the series to not be shown in chart
	 *                   {@link Legend}.
	 */
	void setNotShowInLegend( String seriesName );

}
