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
import javafx.beans.value.ChangeListener;
import javafx.scene.chart.Chart;


/**
 * Abstract class base of all plugins needing to be notified when chart plotting
 * bounds change.
 *
 * @author claudio.rosati@esss.se
 */
public abstract class AbstractBoundedPlugin extends Plugin {

	private final ChangeListener<Number> boundsListener = ( ob, ov, nv ) -> boundsChanged();

	/**
	 * Called when lower and/or upper bounds of X and/or Y axis change.
	 */
	protected abstract void boundsChanged();

	@Override
	protected void chartConnected( Chart chart ) {

		super.chartConnected(chart);

		getXValueAxis().lowerBoundProperty().addListener(boundsListener);
		getXValueAxis().upperBoundProperty().addListener(boundsListener);
		getYValueAxis().lowerBoundProperty().addListener(boundsListener);
		getYValueAxis().upperBoundProperty().addListener(boundsListener);

	}

	@Override
	protected void chartDisconnected( Chart chart ) {

		getYValueAxis().upperBoundProperty().removeListener(boundsListener);
		getYValueAxis().lowerBoundProperty().removeListener(boundsListener);
		getXValueAxis().upperBoundProperty().removeListener(boundsListener);
		getXValueAxis().lowerBoundProperty().removeListener(boundsListener);

		super.chartDisconnected(chart);

	}

}
