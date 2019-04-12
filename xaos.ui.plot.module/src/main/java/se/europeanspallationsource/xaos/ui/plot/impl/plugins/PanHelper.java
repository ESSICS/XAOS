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
package se.europeanspallationsource.xaos.ui.plot.impl.plugins;


import chart.Plugin;
import se.europeanspallationsource.xaos.ui.plot.impl.util.ChartUndoManager;


/**
 * Helper class supporting chart pan operations.
 *
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class PanHelper {

	private static final double PAN_FACTOR = 0.1;
	private static final double SCROLL_FACTOR = 0.005;

	private final Plugin plugin;

	public PanHelper( Plugin plugin ) {
		this.plugin = plugin;
	}

	public void moveHorizontally ( double offset ) {
		plugin.getXValueAxis().setAutoRanging(false);
		plugin.getYValueAxis().setAutoRanging(false);
		plugin.getXValueAxis().setLowerBound(plugin.getXValueAxis().getLowerBound() + offset);
		plugin.getXValueAxis().setUpperBound(plugin.getXValueAxis().getUpperBound() + offset);
	}

	public void moveVertically ( double offset ) {
		plugin.getXValueAxis().setAutoRanging(false);
		plugin.getYValueAxis().setAutoRanging(false);
		plugin.getYValueAxis().setLowerBound(plugin.getYValueAxis().getLowerBound() + offset);
		plugin.getYValueAxis().setUpperBound(plugin.getYValueAxis().getUpperBound() + offset);
	}

	public void panDown() {

		ChartUndoManager.get(plugin.getChart()).captureUndoable(plugin);

		double plotHeight = plugin.getYValueAxis().getUpperBound() - plugin.getYValueAxis().getLowerBound();

		moveVertically(- PAN_FACTOR * plotHeight);

	}

	public void panLeft() {

		ChartUndoManager.get(plugin.getChart()).captureUndoable(plugin);

		double plotWidth = plugin.getXValueAxis().getUpperBound() - plugin.getXValueAxis().getLowerBound();

		moveHorizontally(- PAN_FACTOR * plotWidth);

	}

	public void panRight() {

		ChartUndoManager.get(plugin.getChart()).captureUndoable(plugin);

		double plotWidth = plugin.getXValueAxis().getUpperBound() - plugin.getXValueAxis().getLowerBound();

		moveHorizontally(PAN_FACTOR * plotWidth);

	}

	public void panUp() {

		ChartUndoManager.get(plugin.getChart()).captureUndoable(plugin);

		double plotHeight = plugin.getYValueAxis().getUpperBound() - plugin.getYValueAxis().getLowerBound();

		moveVertically(PAN_FACTOR * plotHeight);

	}

	public void scrollHorizontally( double offset ) {

		ChartUndoManager.get(plugin.getChart()).captureUndoable(plugin);

		double plotHeight = plugin.getXValueAxis().getUpperBound() - plugin.getXValueAxis().getLowerBound();

		moveHorizontally(offset * SCROLL_FACTOR * plotHeight);

	}

	public void scrollVertically( double offset ) {

		ChartUndoManager.get(plugin.getChart()).captureUndoable(plugin);

		double plotHeight = plugin.getYValueAxis().getUpperBound() - plugin.getYValueAxis().getLowerBound();

		moveVertically(offset * SCROLL_FACTOR * plotHeight);

	}

}
