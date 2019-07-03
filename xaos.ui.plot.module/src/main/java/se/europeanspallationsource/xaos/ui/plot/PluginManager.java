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
package se.europeanspallationsource.xaos.ui.plot;


import java.util.List;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.Chart;
import se.europeanspallationsource.xaos.core.util.LogUtils;

import static java.util.logging.Level.WARNING;


/**
 * Connects plugins to the chart and adds plugins' plot children nodes to the
 * chart.
 *
 * @author Grzegorz Kruk (original author).
 * @author claudio.rosati@esss.se
 */
class PluginManager {

	private static final Logger LOGGER = Logger.getLogger(PluginManager.class.getName());

	private final Chart chart;
	private final ListChangeListener<Node> pluginPlotChildrenChanged = change -> {
		while ( change.next() ) {
			updatePlotContent(change.getRemoved(), change.getAddedSubList());
		}
	};
	private final ObservableList<Plugin> plugins = FXCollections.observableArrayList();
	private final ListChangeListener<Plugin> pluginsChanged = change -> {
		while ( change.next() ) {
			unbindPlugins(change.getRemoved());
			bindPlugins(change.getAddedSubList());
		}
	};
	private final Group pluginsNodes;

	PluginManager( Chart chart, Group pluginsNodesGroup ) {

		this.chart = chart;
		this.pluginsNodes = pluginsNodesGroup;

		pluginsNodesGroup.setAutoSizeChildren(false);
		pluginsNodesGroup.setManaged(false);
	
		plugins.addListener(pluginsChanged);

	}

	@SuppressWarnings( "ReturnOfCollectionOrArrayField" )
	ObservableList<Plugin> getPlugins() {
		return plugins;
	}

	private void bindPlugins( List<? extends Plugin> addedPlugins ) {
		addedPlugins.forEach(plugin -> {
			try {
				plugin.setChart(chart);
				plugin.getPlotChildren().addListener(pluginPlotChildrenChanged);
				pluginsNodes.getChildren().addAll(plugin.getPlotChildren());
			} catch ( Exception ex ) {
				LogUtils.log(
					LOGGER,
					WARNING,
//	TODO: Uncomment the following line to debug invalidating exceptions.
//					ex,
					"Plugin ''{0}'' cannot be added to chart [reason: {1}].",
					plugin.getName(),
					ex.getMessage()
				);
			}
		});
	}

	private void unbindPlugins( List<? extends Plugin> removedPlugins ) {
		removedPlugins.forEach(plugin -> {
			pluginsNodes.getChildren().removeAll(plugin.getPlotChildren());
			plugin.getPlotChildren().removeListener(pluginPlotChildrenChanged);
			plugin.setChart(null);
		});
	}

	private void updatePlotContent( List<? extends Node> removed, List<? extends Node> added ) {
		pluginsNodes.getChildren().removeAll(removed);
		pluginsNodes.getChildren().addAll(added);
	}

}
