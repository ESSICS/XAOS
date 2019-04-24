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


/**
 * Abstract class base of all cursor plugins.
 *
 * @author claudio.rosati@esss.se
 */
public abstract class AbstractCursorPlugin extends Plugin {

	//	TODO:CR Listen to change in the chart's area and update the displayed
	//			value at the cursor position.
	
	//	TODO:CR Define the following handlers. Give default implementation.
	//			mouseMove by default shoult record the cursor's position.

//	private final EventHandler<MouseEvent> dragDetectedHandler = this::dragDetected;
//	private final EventHandler<MouseEvent> mouseEnteredHandler = this::mouseEntered;
//	private final EventHandler<MouseEvent> mouseExitedHandler = this::mouseExited;
//	private final EventHandler<MouseEvent> mouseMoveHandler = this::mouseMove;

}
