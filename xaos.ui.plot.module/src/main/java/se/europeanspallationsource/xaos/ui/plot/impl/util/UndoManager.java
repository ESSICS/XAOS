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
package se.europeanspallationsource.xaos.ui.plot.impl.util;


import chart.XYChartPlugin;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.WeakHashMap;
import javafx.scene.chart.Chart;
import org.apache.commons.lang3.Validate;


/**
 * Handle {@link Chart}'s undo/redo operations.
 *
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class UndoManager {

	private static final Map<Chart, Deque<UndoableEntry>> REDO_MAP = new WeakHashMap<>();
	private static final Map<Chart, Deque<UndoableEntry>> UNDO_MAP = new WeakHashMap<>();

	/**
	 * Capture the upper and lower X/Y bounds from the given {@code plugin}'s
	 * {@link Chart} and push it in the undo stack.
	 *
	 * @param plugin The {@link XYChartPlugin} needing to save the current
	 *               {@link Chart}'s bounds into the undo stack.
	 * @throws NullPointerException If {@code plugin} or {@code plugin.getChart()}
	 *                              are {@code null}.
	 */
	public static void captureUndoable( XYChartPlugin plugin ) {

		Validate.notNull(plugin, "Null 'plugin'.");

		Chart chart = plugin.getChart();

		Validate.notNull(chart, "Null 'plugin.getChart()'.");

		UndoableEntry entry = new UndoableEntry(plugin);
		Deque<UndoableEntry> undoStack = getStack(chart, UNDO_MAP);
		Deque<UndoableEntry> redoStack = getStack(chart, REDO_MAP);

		undoStack.push(entry);
		redoStack.clear();

	}

	/**
	 * @param plugin The {@link XYChartPlugin} needing to check if REDO data
	 *               is available.
	 * @return {@code true} if at least one REDO data is available
	 * @throws NullPointerException If {@code plugin} or {@code plugin.getChart()}
	 *                              are {@code null}.
	 */
	public static boolean isRedoable( XYChartPlugin plugin ) {

		Validate.notNull(plugin, "Null 'plugin'.");

		Chart chart = plugin.getChart();

		Validate.notNull(chart, "Null 'plugin.getChart()'.");

		Deque<UndoableEntry> redoStack = getStack(chart, REDO_MAP);

		return !redoStack.isEmpty();

	}

	/**
	 * @param plugin The {@link XYChartPlugin} needing to check if UNDO data
	 *               is available.
	 * @return {@code true} if at least one UNDO data is available
	 * @throws NullPointerException If {@code plugin} or {@code plugin.getChart()}
	 *                              are {@code null}.
	 */
	public static boolean isUndoable( XYChartPlugin plugin ) {

		Validate.notNull(plugin, "Null 'plugin'.");

		Chart chart = plugin.getChart();

		Validate.notNull(chart, "Null 'plugin.getChart()'.");

		Deque<UndoableEntry> undoStack = getStack(chart, UNDO_MAP);

		return !undoStack.isEmpty();

	}

	/**
	 * Set the given {@code plugin}'s {@link Chart} bounds to values previously
	 * undone.
	 *
	 * @param plugin The {@link XYChartPlugin} wishing to redo.
	 * @throws NullPointerException  If {@code plugin} or {@code plugin.getChart()}
	 *                               are {@code null}.
	 * @throws IllegalStateException If redo cannot be performed.
	 */
	public static void redo( XYChartPlugin plugin ) {

		Validate.notNull(plugin, "Null 'plugin'.");

		Chart chart = plugin.getChart();

		Validate.notNull(chart, "Null 'plugin.getChart()'.");
		Validate.validState(isRedoable(plugin), "Not redoable.");

		UndoableEntry entry = new UndoableEntry(plugin);
		Deque<UndoableEntry> undoStack = getStack(chart, UNDO_MAP);
		Deque<UndoableEntry> redoStack = getStack(chart, REDO_MAP);

		undoStack.push(entry);
		redoStack.pop().restore(plugin);

	}

	/**
	 * Set the given {@code plugin}'s {@link Chart} bounds to values captured.
	 *
	 * @param plugin The {@link XYChartPlugin} wishing to redo.
	 * @throws NullPointerException  If {@code plugin} or {@code plugin.getChart()}
	 *                               are {@code null}.
	 * @throws IllegalStateException If undo cannot be performed.
	 */
	public static void undo( XYChartPlugin plugin ) {

		Validate.notNull(plugin, "Null 'plugin'.");

		Chart chart = plugin.getChart();

		Validate.notNull(chart, "Null 'plugin.getChart()'.");
		Validate.validState(isUndoable(plugin), "Not undoable.");

		UndoableEntry entry = new UndoableEntry(plugin);
		Deque<UndoableEntry> undoStack = getStack(chart, UNDO_MAP);
		Deque<UndoableEntry> redoStack = getStack(chart, REDO_MAP);

		redoStack.push(entry);
		undoStack.pop().restore(plugin);

	}

	private static Deque<UndoableEntry> getStack( Chart chart, Map<Chart, Deque<UndoableEntry>> stackMap ) {

		Deque<UndoableEntry> stack = stackMap.get(chart);

		if ( stack == null ) {
			stack = new ArrayDeque<>(8);
		}

		return stack;

	}

	private UndoManager() {
		//	Nothing to do.
	}

	private static class UndoableEntry {

		private final double xLowerBoud;
		private final double xUpperBoud;
		private final double yLowerBoud;
		private final double yUpperBoud;

		UndoableEntry( XYChartPlugin plugin ) {
			this.xLowerBoud = plugin.getXValueAxis().getLowerBound();
			this.xUpperBoud = plugin.getXValueAxis().getUpperBound();
			this.yLowerBoud = plugin.getYValueAxis().getLowerBound();
			this.yUpperBoud = plugin.getYValueAxis().getUpperBound();
		}

		private void restore( XYChartPlugin plugin ) {
			plugin.getXValueAxis().setLowerBound(xLowerBoud);
			plugin.getXValueAxis().setUpperBound(xUpperBoud);
			plugin.getYValueAxis().setLowerBound(yLowerBoud);
			plugin.getYValueAxis().setUpperBound(yUpperBoud);
		}

	}

}
