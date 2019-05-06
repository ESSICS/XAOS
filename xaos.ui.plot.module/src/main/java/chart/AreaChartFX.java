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

package chart;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.Axis;
import se.europeanspallationsource.xaos.ui.plot.impl.Legend;
import se.europeanspallationsource.xaos.ui.plot.plugins.Pluggable;
import se.europeanspallationsource.xaos.ui.plot.util.SeriesColorUtils;

/**
 * A thin extension of the FX {@link AreaChart} supporting custom {@link Plugin} plugin implementations.
 *
 * @param <X> type of X values
 * @param <Y> type of Y values
 * @author Grzegorz Kruk
 */
public class AreaChartFX<X, Y> extends AreaChart<X, Y> implements Pluggable {

    //Variables used to include plugIns in teh Chart
    private final Group pluginsNodesGroup = new Group();
    private final PluginManager pluginManager = new PluginManager(this, pluginsNodesGroup);

    //Variables that controls the line that doens't show in the Legend or series that are not displayed at the chart at a given time
    private final List<String> noShowInLegend = new ArrayList<>();
    private final List<String> seriesDrawnInPlot = new ArrayList<>();

    // Variable that stored the cumulative color line setup
    private String colorStyle = new String();

    /**
     * Construct a new Area Chart with the given axis
     *
     * @param xAxis The x axis to use
     * @param yAxis The y axis to use
     * @see javafx.scene.chart.AreaChart#AreaChart(Axis, Axis)
     */
    public AreaChartFX(Axis<X> xAxis, Axis<Y> yAxis) {
        this(xAxis, yAxis, FXCollections.<Series<X, Y>> observableArrayList());
    }

    /**
     * Construct a new Area Chart with the given axis and data
     *
     * @param xAxis The x axis to use
     * @param yAxis The y axis to use
     * @param data The data to use, this is the actual list used so any changes to it will be reflected in the chart
     * @see javafx.scene.chart.AreaChart#AreaChart(Axis, Axis, ObservableList)
     */
    public AreaChartFX(Axis<X> xAxis, Axis<Y> yAxis, ObservableList<Series<X, Y>> data) {
        super(xAxis, yAxis, data);
        getStylesheets().add(getClass().getResource("/styles/chart.css").toExternalForm());
        getPlotChildren().add(pluginsNodesGroup);
    }

	@Override
    public final ObservableList<Plugin> getPlugins() {
        return pluginManager.getPlugins();
    }

    /**
     * More robust method for adding plugins to chart.
     * Note: Only necessary if more than one plugin is being added at once.
     *
     * @param plugins list of XYChartPlugins to be added.
     *
     *
     */
    public void addChartPlugins(ObservableList<Plugin> plugins){
         plugins.forEach(item->{
         try{
            pluginManager.getPlugins().add(item);
         }
         catch (Exception e) {
             System.out.println("Error occured whilst adding" + item.getClass().toString());
         }
         });

    }

    @Override
    protected void layoutPlotChildren() {
        movePluginsNodesToFront();
        super.layoutPlotChildren();
    }

    private void movePluginsNodesToFront() {
        getPlotChildren().remove(pluginsNodesGroup);
        getPlotChildren().add(pluginsNodesGroup);
    }

	public final void setHVLSeries( int horizontal, int vertical, int longitudinal ) {
		lookup(".chart").setStyle(SeriesColorUtils.styles(horizontal, vertical, longitudinal));
	}


//	public final void setSeriesAsHorizontal( int index ) {
//
//		Validate.inclusiveBetween(0, getData().size(), index, "Out of range 'index' parameter.");
//
//		colorStyle += SeriesColorUtils.styleFor(HORIZONTAL, index);
//
//		this.lookup(".chart").setStyle(colorStyle);
//
//	}
//
//	public final void setSeriesAsVertical( int index ) {
//
//		Validate.inclusiveBetween(0, getData().size(), index, "Out of range 'index' parameter.");
//
//		colorStyle += SeriesColorUtils.styleFor(VERTICAL, index);
//
//		this.lookup(".chart").setStyle(colorStyle);
//
//	}
//
//	public final void setSeriesAsLongitudinal( int index ) {
//
//		Validate.inclusiveBetween(0, getData().size(), index, "Out of range 'index' parameter.");
//
//		colorStyle += SeriesColorUtils.styleFor(LONGITUDINAL, index);
//
//		this.lookup(".chart").setStyle(colorStyle);
//
//	}

    public final void setNoShowInLegend(String name){
        noShowInLegend.add(name);
        updateLegend();
    }

    public boolean isNoShowInLegend(String name){
        return noShowInLegend.contains(name);
    }

    public boolean isSeriesDrawn(String name){
        return seriesDrawnInPlot.contains(name);
    }

    @Override
    protected void updateLegend()
    {
        final Legend legend = new Legend();
        seriesDrawnInPlot.clear();
        legend.getItems().clear();
        for (int i = 0; i < getData().size(); i++)
        {
			final Series<X, Y> series = getData().get(i);
			final String seriesName = series.getName();
            if(!noShowInLegend.contains(seriesName)){
                Legend.LegendItem legenditem = new Legend.LegendItem(seriesName, selected -> {

                    series.getNode().setVisible(selected);
                    series.getData().forEach(d -> d.getNode().setVisible(selected));

					if ( selected ) {
						if ( !seriesDrawnInPlot.contains(seriesName) ) {
							seriesDrawnInPlot.add(seriesName);
						}
					} else {
						seriesDrawnInPlot.remove(seriesName);
					}

					getPlugins().forEach(p -> p.seriesVisibilityUpdated(this, series, selected));
				
				});
				legenditem.getSymbol().getStyleClass().addAll(
					"chart-area-symbol",
					"area-legend-symbol",
					"default-color" + ( i % 8 ),
					"series" + i
				);

                seriesDrawnInPlot.add(seriesName);
                legend.getItems().add(legenditem);

            }
        }
        setLegend(legend);
    }

}
