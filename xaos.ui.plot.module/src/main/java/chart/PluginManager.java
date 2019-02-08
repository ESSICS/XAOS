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

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.Chart;

/**
 * Connects plugins to the chart and adds plugins' plot children nodes to the chart. 
 * 
 * @author Grzegorz Kruk
 */
class PluginManager {

    private final ListChangeListener<XYChartPlugin> pluginsChanged = (change) -> {
        while (change.next()) {
            unbindPlugins(change.getRemoved());
            bindPlugins(change.getAddedSubList());
        }
    };
    

    
    private final ListChangeListener<Node> pluginPlotChildrenChanged = (change) -> {
        while (change.next()) {
            updatePlotContent(change.getRemoved(), change.getAddedSubList());
        }
    };
    
    private final Chart chart;
    private final Group pluginsNodes;
    private final ObservableList<XYChartPlugin> plugins = FXCollections.observableArrayList();

    PluginManager(Chart chart, Group pluginsNodesGroup) {    
        this.chart = chart;
        this.pluginsNodes = pluginsNodesGroup;
        
        pluginsNodesGroup.setAutoSizeChildren(false);
        pluginsNodesGroup.setManaged(false);
        plugins.addListener(pluginsChanged);
    }

    ObservableList<XYChartPlugin> getPlugins() {
        
        return plugins; //NOSONAR
    }

    private void bindPlugins(List<? extends XYChartPlugin> addedPlugins) {
        for (XYChartPlugin plugin : addedPlugins) {
            plugin.setChart(chart);
            plugin.getPlotChildren().addListener(pluginPlotChildrenChanged);
        }
        for (XYChartPlugin plugin : addedPlugins) {
            pluginsNodes.getChildren().addAll(plugin.getPlotChildren());
        }
        
    }

    private void unbindPlugins(List<? extends XYChartPlugin> removedPlugins) {
        for (XYChartPlugin plugin : removedPlugins) {
            plugin.setChart(null);
            plugin.getPlotChildren().removeListener(pluginPlotChildrenChanged);
        }
        for (XYChartPlugin plugin : removedPlugins) {
            pluginsNodes.getChildren().removeAll(plugin.getPlotChildren());
        }
    }
    
    private void updatePlotContent(List<? extends Node> removed, List<? extends Node> added) {
        pluginsNodes.getChildren().removeAll(removed);
        pluginsNodes.getChildren().addAll(added);
    }
}
