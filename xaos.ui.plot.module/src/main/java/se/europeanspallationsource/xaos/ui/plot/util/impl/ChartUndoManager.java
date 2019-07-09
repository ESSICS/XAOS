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
package se.europeanspallationsource.xaos.ui.plot.util.impl;


import java.util.Map;
import java.util.WeakHashMap;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.Chart;
import org.apache.commons.lang3.Validate;
import se.europeanspallationsource.xaos.ui.plot.Plugin;


/**
 * Handle {@link Chart}'s undo/redo operations.
 *
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class ChartUndoManager {

	private static final Map<Chart, ChartUndoManager> MANAGERS_MAP = new WeakHashMap<>(4);

	/**
	 * Return an instance of this undo manager for the given {@link Chart}.The
	 * instance will be created if not already existing.
	 *
	 * @param chart The {@link Chart} whose undo manager instance must be
	 *              returned.
	 * @return An instance of this undo manager for the given {@link Chart}.
	 * @throws NullPointerException If {@code chart} is {@code null}.
	 */
	public static ChartUndoManager get( Chart chart ) {

		Validate.notNull(chart, "Null 'chart'.");

		ChartUndoManager manager = MANAGERS_MAP.get(chart);

		if ( manager == null ) {

			manager = new ChartUndoManager();

			MANAGERS_MAP.put(chart, manager);

		}

		return manager;

	}

	private final ObservableList<UndoableEntry> redoList = FXCollections.observableArrayList();
	private final ObservableList<UndoableEntry> undoList = FXCollections.observableArrayList();

	/* *********************************************************************** *
	 * START OF JAVAFX PROPERTIES                                              *
	 * *********************************************************************** */

	/*
	 * ---- redoable -----------------------------------------------------------
	 */
	private final BooleanProperty redoable = new SimpleBooleanProperty(ChartUndoManager.this, "redoable");

	public ReadOnlyBooleanProperty redoableProperty() {
		return redoable;
	}

	/**
	 * @return {@code true} if at least one REDO entry is available.
	 */
	public final boolean isRedoable() {
		return redoableProperty().get();
	}

	/*
	 * ---- undoable -----------------------------------------------------------
	 */
	private final BooleanProperty undoable = new SimpleBooleanProperty(ChartUndoManager.this, "undoable");

	public ReadOnlyBooleanProperty undoableProperty() {
		return undoable;
	}

	/**
	 * @return {@code true} if at least one UNDO entry is available.
	 */
	public final boolean isUndoable() {
		return undoableProperty().get();
	}

	/* *********************************************************************** *
	 * END OF JAVAFX PROPERTIES                                                *
	 * *********************************************************************** */

	private ChartUndoManager() {
		redoable.bind(Bindings.isNotEmpty(redoList));
		undoable.bind(Bindings.isNotEmpty(undoList));
	}

	/**
	 * Capture the upper and lower X/Y bounds from the given {@code plugin}'s
	 * {@link Chart} and push it in the undo stack.
	 *
	 * @param plugin The {@link Plugin} needing to save the current
	 *               {@link Chart}'s bounds into the undo stack.
	 * @throws NullPointerException If {@code plugin} or {@code plugin.getChart()}
	 *                              are {@code null}.
	 */
	public void captureUndoable( Plugin plugin ) {

		Validate.notNull(plugin, "Null 'plugin'.");

		Chart chart = plugin.getChart();

		Validate.validState(this.equals(MANAGERS_MAP.get(chart)), "Given 'plugin' is referring a different chart than this manager.");

		clear(redoList);
		push(undoList, new UndoableEntry(plugin));

	}

	/**
	 * @return The number of available redoables.
	 */
	public int getAvailableRedoables() {
		return redoList.size();
	}

	/**
	 * @return The number of available undoables.
	 */
	public int getAvailableUndoables() {
		return undoList.size();
	}

	/**
	 * Set the given {@code plugin}'s {@link Chart} bounds to values previously
	 * undone.
	 *
	 * @param plugin The {@link Plugin} wishing to redo.
	 * @throws NullPointerException  If {@code plugin} or {@code plugin.getChart()}
	 *                               are {@code null}.
	 * @throws IllegalStateException If redo cannot be performed.
	 */
	public void redo( Plugin plugin ) {

		Validate.notNull(plugin, "Null 'plugin'.");

		Chart chart = plugin.getChart();

		Validate.validState(this.equals(MANAGERS_MAP.get(chart)), "Given 'plugin' is referring a different chart than this manager.");
		Validate.validState(isRedoable(), "Not redoable.");

		push(undoList, new UndoableEntry(plugin));
		pop(redoList).restore(plugin);

	}

	/**
	 * Set the given {@code plugin}'s {@link Chart} bounds to values captured.
	 *
	 * @param plugin The {@link Plugin} wishing to redo.
	 * @throws NullPointerException  If {@code plugin} or {@code plugin.getChart()}
	 *                               are {@code null}.
	 * @throws IllegalStateException If undo cannot be performed.
	 */
	public void undo( Plugin plugin ) {

		Validate.notNull(plugin, "Null 'plugin'.");

		Chart chart = plugin.getChart();

		Validate.validState(this.equals(MANAGERS_MAP.get(chart)), "Given 'plugin' is referring a different chart than this manager.");
		Validate.validState(isUndoable(), "Not undoable.");

		push(redoList, new UndoableEntry(plugin));
		pop(undoList).restore(plugin);

	}

	private void clear ( ObservableList<UndoableEntry> list ) {
		list.clear();
	}

	private UndoableEntry pop ( ObservableList<UndoableEntry> list ) {
		return list.remove(list.size() - 1);
	}

	private void push ( ObservableList<UndoableEntry> list, UndoableEntry entry ) {
		list.add(entry);
	}

	private static class UndoableEntry {

		private final boolean xAutoRange;
		private final double xLowerBound;
		private final double xUpperBound;
		private final boolean yAutoRange;
		private final double yLowerBound;
		private final double yUpperBound;

		UndoableEntry( Plugin plugin ) {
			this.xAutoRange  = plugin.getXValueAxis().isAutoRanging();
			this.xLowerBound = plugin.getXValueAxis().getLowerBound();
			this.xUpperBound = plugin.getXValueAxis().getUpperBound();
			this.yAutoRange  = plugin.getYValueAxis().isAutoRanging();
			this.yLowerBound = plugin.getYValueAxis().getLowerBound();
			this.yUpperBound = plugin.getYValueAxis().getUpperBound();
		}

		void restore( Plugin plugin ) {
			plugin.getXValueAxis().setAutoRanging(xAutoRange);
			plugin.getYValueAxis().setAutoRanging(yAutoRange);
			plugin.getXValueAxis().setLowerBound(xLowerBound);
			plugin.getXValueAxis().setUpperBound(xUpperBound);
			plugin.getYValueAxis().setLowerBound(yLowerBound);
			plugin.getYValueAxis().setUpperBound(yUpperBound);
		}

	}

}
