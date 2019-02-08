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
package se.europeanspallationsource.xaos.ui.plot.util;


import javafx.scene.chart.Axis;
import javafx.scene.chart.ValueAxis;
import org.apache.commons.lang3.Validate;


/**
 * Set of assertion methods useful for checking method arguments.
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class Assertions {

	/**
	 * Asserts that given axis is an instance of {@link ValueAxis}.
	 *
	 * @param axis     The axis to be checked.
	 * @param axisName Name of the axis to be checked (used for the thrown
	 *                 exception's message).
	 * @throws IllegalArgumentException If the give {@code axis} is not an
	 *                                  instance of {@link ValueAxis}.
	 */
	public static void assertValueAxis( Axis<?> axis, String axisName ) throws IllegalArgumentException {
		Validate.isInstanceOf(ValueAxis.class, axis, "%s axis must be an instance of ValueAxis.", axisName);
	}

	private Assertions() {
	}

}
