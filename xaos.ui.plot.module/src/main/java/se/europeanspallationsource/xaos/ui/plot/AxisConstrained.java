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


import javafx.beans.property.ObjectProperty;


/**
 * Define the common behavior of axis constrained plugins.
 *
 * @author claudio.rosati@esss.se
 */
public interface AxisConstrained {

	/**
	 * Defines the  plugin constraints along {@link AxisConstraints#X_ONLY},
	 * {@link AxisConstraints#Y_ONLY} or {@link AxisConstraints#X_AND_Y} axis.
	 *
	 * @return The plugin constraints.
	 */
	ObjectProperty<AxisConstraints> constraintsProperty();

	default AxisConstraints getConstraints() {
		return constraintsProperty().get();
	}

	default void setConstraints( AxisConstraints constraints ) {
		constraintsProperty().set(constraints);
	}

	/**
	 * Defines constraints of axis-related operations such as pan or zoom.
	 */
	enum AxisConstraints {

		/**
		 * The operation should be allowed only along the X_ONLY axis.
		 */
		X_ONLY,

		/**
		 * The operation should be allowed only along Y_ONLY axis.
		 */
		Y_ONLY,

		/**
		 * The operation can be performed on both X_ONLY and Y_ONLY axis.
		 */
		X_AND_Y

	}

}
