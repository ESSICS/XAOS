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


import se.europeanspallationsource.xaos.ui.plot.Plugin;
import se.europeanspallationsource.xaos.ui.plot.util.impl.ChartUndoManager;


/**
 * Helper class supporting chart pan operations.
 *
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( "ClassWithoutLogger" )
class PanHelper {

	private static final double PAN_FACTOR = 0.1;
	private static final double SCROLL_FACTOR = 0.005;

	private final Plugin plugin;

	PanHelper( Plugin plugin ) {
		this.plugin = plugin;
	}

	void moveHorizontally ( double offset ) {
		plugin.getXValueAxis().setAutoRanging(false);
		plugin.getYValueAxis().setAutoRanging(false);
		plugin.getXValueAxis().setLowerBound(plugin.getXValueAxis().getLowerBound() + offset);
		plugin.getXValueAxis().setUpperBound(plugin.getXValueAxis().getUpperBound() + offset);
	}

	void moveVertically ( double offset ) {
		plugin.getXValueAxis().setAutoRanging(false);
		plugin.getYValueAxis().setAutoRanging(false);
		plugin.getYValueAxis().setLowerBound(plugin.getYValueAxis().getLowerBound() + offset);
		plugin.getYValueAxis().setUpperBound(plugin.getYValueAxis().getUpperBound() + offset);
	}

	void panDown() {

		ChartUndoManager.get(plugin.getChart()).captureUndoable(plugin);

		double plotHeight = plugin.getYValueAxis().getUpperBound() - plugin.getYValueAxis().getLowerBound();

		moveVertically(- PAN_FACTOR * plotHeight);

	}

	void panLeft() {

		ChartUndoManager.get(plugin.getChart()).captureUndoable(plugin);

		double plotWidth = plugin.getXValueAxis().getUpperBound() - plugin.getXValueAxis().getLowerBound();

		moveHorizontally(- PAN_FACTOR * plotWidth);

	}

	void panRight() {

		ChartUndoManager.get(plugin.getChart()).captureUndoable(plugin);

		double plotWidth = plugin.getXValueAxis().getUpperBound() - plugin.getXValueAxis().getLowerBound();

		moveHorizontally(PAN_FACTOR * plotWidth);

	}

	void panUp() {

		ChartUndoManager.get(plugin.getChart()).captureUndoable(plugin);

		double plotHeight = plugin.getYValueAxis().getUpperBound() - plugin.getYValueAxis().getLowerBound();

		moveVertically(PAN_FACTOR * plotHeight);

	}

	void scroll( double xOffset, double yOffset, boolean captureUndoable ) {

		if ( captureUndoable ) {
			ChartUndoManager.get(plugin.getChart()).captureUndoable(plugin);
		}

		double plotWidth = plugin.getXValueAxis().getUpperBound() - plugin.getXValueAxis().getLowerBound();
		double plotHeight = plugin.getYValueAxis().getUpperBound() - plugin.getYValueAxis().getLowerBound();

		moveHorizontally(xOffset * SCROLL_FACTOR * plotWidth);
		moveVertically(yOffset * SCROLL_FACTOR * plotHeight);

	}

	void scrollHorizontally( double offset, boolean captureUndoable ) {

		if ( captureUndoable ) {
			ChartUndoManager.get(plugin.getChart()).captureUndoable(plugin);
		}

		double plotWidth = plugin.getXValueAxis().getUpperBound() - plugin.getXValueAxis().getLowerBound();

		moveHorizontally(offset * SCROLL_FACTOR * plotWidth);

	}

	void scrollVertically( double offset, boolean captureUndoable ) {

		if ( captureUndoable ) {
			ChartUndoManager.get(plugin.getChart()).captureUndoable(plugin);
		}

		double plotHeight = plugin.getYValueAxis().getUpperBound() - plugin.getYValueAxis().getLowerBound();

		moveVertically(offset * SCROLL_FACTOR * plotHeight);

	}

}
