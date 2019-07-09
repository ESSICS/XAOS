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

/**
 * @author claudio.rosati@esss.se
 */
module xaos.ui.plot {

	//	The following are "filename-based automodules", whose name is derived
	//  from the JAR name.
	requires commons.math3;
	
	requires java.desktop;
	requires java.logging;
	requires java.sql;
	requires transitive javafx.base;
	requires transitive javafx.controls;
	requires javafx.fxml;
	requires transitive javafx.graphics;
	requires javafx.swing;
	requires javafx.web;
	requires org.apache.commons.lang3;
	requires transitive org.controlsfx.controls;
	requires transitive xaos.core;
	requires transitive xaos.tools;
	requires transitive xaos.ui;
	requires java.base;

	uses se.europeanspallationsource.xaos.ui.plot.spi.ToolbarContributor;
	
	provides se.europeanspallationsource.xaos.ui.plot.spi.ToolbarContributor
		with se.europeanspallationsource.xaos.ui.plot.spi.impl.AxisPropertiesContributor,
			 se.europeanspallationsource.xaos.ui.plot.spi.impl.FitContributor,
			 se.europeanspallationsource.xaos.ui.plot.spi.impl.SaveChartAsImageContributor,
			 se.europeanspallationsource.xaos.ui.plot.spi.impl.SaveChartDataContributor,
			 se.europeanspallationsource.xaos.ui.plot.spi.impl.StatisticsContributor;

	exports se.europeanspallationsource.xaos.ui.plot;
	exports se.europeanspallationsource.xaos.ui.plot.data;
	exports se.europeanspallationsource.xaos.ui.plot.plugins;
	exports se.europeanspallationsource.xaos.ui.plot.spi;
	exports se.europeanspallationsource.xaos.ui.plot.util;

	opens se.europeanspallationsource.xaos.ui.plot to xaos.tools;
	opens se.europeanspallationsource.xaos.ui.plot.plugins to xaos.tools;
	opens se.europeanspallationsource.xaos.ui.plot.spi.impl to xaos.tools, javafx.fxml;

}
