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
package se.europeanspallationsource.xaos.ui.plot.spi;


import javafx.scene.control.Control;
import javafx.scene.control.Separator;
import se.europeanspallationsource.xaos.ui.plot.PluggableChartContainer;


/**
 * Implementations of this interface allow for new toolbar elements (buttons,
 * combo boxes, etc.) being added into the {@link PluggableChartContainer}
 * toolbar.
 *
 * @author claudio.rosati@esss.se
 */
public interface ToolbarContributor {

	/**
	 * @return {@code true} if the component provided by
	 *         {@link #provide(PluggableChartContainer)} needs to be preceded by
	 *         a {@link Separator} one. By default this method returns
	 *         {@code false}.
	 */
	default boolean isPrecededBySeparator() {
		return false;
	}

	/**
	 * Provides a new element for the {@link PluggableChartContainer} toolbar.
	 *
	 * @param chartContainer The {@link PluggableChartContainer} whose toolbar
	 *                       must be contributed to.
	 * @return A new {@link Control} to be added into the
	 *         {@link PluggableChartContainer} toolbar.
	 */
	public Control provide( PluggableChartContainer chartContainer );

}
